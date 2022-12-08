package io.github.melin.flink.jobserver.core.util;


import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.ivy.Ivy;
import org.apache.ivy.core.LogOptions;
import org.apache.ivy.core.module.descriptor.*;
import org.apache.ivy.core.module.id.ArtifactId;
import org.apache.ivy.core.module.id.ModuleId;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.core.report.ResolveReport;
import org.apache.ivy.core.resolve.ResolveOptions;
import org.apache.ivy.core.retrieve.RetrieveOptions;
import org.apache.ivy.core.settings.IvySettings;
import org.apache.ivy.plugins.matcher.GlobPatternMatcher;
import org.apache.ivy.plugins.repository.file.FileRepository;
import org.apache.ivy.plugins.resolver.ChainResolver;
import org.apache.ivy.plugins.resolver.FileSystemResolver;
import org.apache.ivy.plugins.resolver.IBiblioResolver;

import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DependencyUtils {

    public static void main(String[] args) throws Exception {
        List<String> list = resolveMavenCoordinates(
                "org.apache.flink:flink-core:1.16.0",
                buildIvySettings(""),
                true,
                Lists.newArrayList(), false);
        System.out.print(list.size());
    }

    private static final PrintStream printStream = System.err;

    private static DefaultModuleDescriptor getModuleDescriptor() {
        return DefaultModuleDescriptor.newDefaultInstance(
                ModuleRevisionId.newInstance("org.apache.spark", "spark-submit-parent-" + UUID.randomUUID().toString(), "1.0"));
    }

    /**
     * Build Ivy Settings using options with default resolvers
     * @param remoteRepos Comma-delimited string of remote repositories other than maven central
     * @return An IvySettings object
     */
    public static IvySettings buildIvySettings(String remoteRepos) {
        IvySettings ivySettings = new IvySettings();

        // create a pattern matcher
        ivySettings.addMatcher(new GlobPatternMatcher());
        // create the dependency resolvers
        ChainResolver repoResolver = createRepoResolvers(ivySettings.getDefaultIvyUserDir());
        ivySettings.addResolver(repoResolver);
        ivySettings.setDefaultResolver(repoResolver.getName());
        processRemoteRepoArg(ivySettings, remoteRepos);
        // (since 2.5) Setting the property ivy.maven.lookup.sources to false
        // disables the lookup of the sources artifact.
        // And setting the property ivy.maven.lookup.javadoc to false
        // disables the lookup of the javadoc artifact.
        ivySettings.setVariable("ivy.maven.lookup.sources", "false");
        ivySettings.setVariable("ivy.maven.lookup.javadoc", "false");
        return ivySettings;
    }

    /**
     * Extracts maven coordinates from a comma-delimited string
     * @param defaultIvyUserDir The default user path for Ivy
     * @return A ChainResolver used by Ivy to search for and resolve dependencies.
     */
    private static ChainResolver createRepoResolvers(File defaultIvyUserDir) {
        // We need a chain resolver if we want to check multiple repositories
        ChainResolver cr = new ChainResolver();
        cr.setName("spark-list");

        File m2Path = new File(System.getProperty("user.home"), ".m2" + File.separator + "repository");
        IBiblioResolver localM2 = new IBiblioResolver();
        localM2.setM2compatible(true);
        localM2.setRoot(m2Path.toURI().toString());
        localM2.setUsepoms(true);
        localM2.setName("local-m2-cache");
        cr.add(localM2);

        FileSystemResolver localIvy = new FileSystemResolver();
        File localIvyRoot = new File(defaultIvyUserDir, "local");
        localIvy.setLocal(true);
        localIvy.setRepository(new FileRepository(localIvyRoot));
        String ivyPattern = StringUtils.join(Lists.newArrayList(localIvyRoot.getAbsolutePath(),
                "[organisation]", "[module]", "[revision]",
                "ivys", "ivy.xml"), File.separator);

        localIvy.addIvyPattern(ivyPattern);
        String artifactPattern = StringUtils.join(Lists.newArrayList(localIvyRoot.getAbsolutePath(), "[organisation]", "[module]",
                "[revision]", "[type]s", "[artifact](-[classifier]).[ext]"), File.separator);

        localIvy.addArtifactPattern(artifactPattern);
        localIvy.setName("local-ivy-cache");
        cr.add(localIvy);

        // the biblio resolver resolves POM declared dependencies
        IBiblioResolver br = new IBiblioResolver();
        br.setM2compatible(true);
        br.setUsepoms(true);
        String defaultInternalRepo = System.getenv().getOrDefault("DEFAULT_ARTIFACT_REPOSITORY", "https://repo1.maven.org/maven2/");
        br.setRoot(defaultInternalRepo);
        br.setName("central");
        cr.add(br);

        IBiblioResolver sp = new IBiblioResolver();
        sp.setM2compatible(true);
        sp.setUsepoms(true);
        sp.setRoot(System.getenv().getOrDefault(
                "DEFAULT_ARTIFACT_REPOSITORY", "https://repos.spark-packages.org/"));
        sp.setName("spark-packages");
        cr.add(sp);
        return cr;
    }

    /* Add any optional additional remote repositories */
    private static void processRemoteRepoArg(IvySettings ivySettings, String remoteRepos) {
        if (StringUtils.isNotBlank(remoteRepos)) {
            String[] items = StringUtils.split(remoteRepos, ",");

            ChainResolver cr = new ChainResolver();
            cr.setName("user-list");
            cr.add(ivySettings.getDefaultResolver());
            int index = 0;
            for (String repo : items) {
                IBiblioResolver brr = new IBiblioResolver();
                brr.setM2compatible(true);
                brr.setUsepoms(true);
                brr.setRoot(repo);
                brr.setName(repo + "-" + (++index));
                cr.add(brr);
            }
            ivySettings.addResolver(cr);
            ivySettings.setDefaultResolver(cr.getName());
        }
    }

    /**
     * Resolves any dependencies that were supplied through maven coordinates
     * @param coordinates Comma-delimited string of maven coordinates
     * @param ivySettings An IvySettings containing resolvers to use
     * @param transitive Whether resolving transitive dependencies, default is true
     * @param exclusions Exclusions to apply when resolving transitive dependencies
     * @return Seq of path to the jars of the given maven artifacts including their
     *         transitive dependencies
     */
    public static List<String> resolveMavenCoordinates(
            String coordinates,
            IvySettings ivySettings,
            boolean transitive,
            List<String> exclusions,
            boolean isTest) throws Exception {
        if (StringUtils.isBlank(coordinates)) {
            return null;
        } else {
            PrintStream sysOut = System.out;
            // Default configuration name for ivy
            String ivyConfName = "default";

            // A Module descriptor must be specified. Entries are dummy strings
            DefaultModuleDescriptor md = getModuleDescriptor();

            md.setDefaultConf(ivyConfName);
            try {
                // To prevent ivy from logging to system out
                System.setOut(printStream);
                List<MavenCoordinate> artifacts = extractMavenCoordinates(coordinates);
                // Directories for caching downloads through ivy and storing the jars when maven coordinates
                // are supplied to spark-submit
                File packagesDirectory = new File(ivySettings.getDefaultIvyUserDir(), "jars");
                // scalastyle:off println
                printStream.println(
                        "Ivy Default Cache set to: " + ivySettings.getDefaultCache().getAbsolutePath());
                printStream.println("The jars for the packages stored in: " + packagesDirectory);
                // scalastyle:on println

                Ivy ivy = Ivy.newInstance(ivySettings);
                // Set resolve options to download transitive dependencies as well
                ResolveOptions resolveOptions = new ResolveOptions();
                resolveOptions.setTransitive(transitive);
                RetrieveOptions retrieveOptions = new RetrieveOptions();
                // Turn downloading and logging off for testing
                if (isTest) {
                    resolveOptions.setDownload(false);
                    resolveOptions.setLog(LogOptions.LOG_QUIET);
                    retrieveOptions.setLog(LogOptions.LOG_QUIET);
                } else {
                    resolveOptions.setDownload(true);
                }

                // Add exclusion rules for Spark and Scala Library
                addExclusionRules(ivySettings, ivyConfName, md);
                // add all supplied maven artifacts as dependencies
                addDependenciesToIvy(md, artifacts, ivyConfName);
                exclusions.forEach(e ->
                    md.addExcludeRule(createExclusion(e + ":*", ivySettings, ivyConfName))
                );

                // resolve dependencies
                ResolveReport rr = ivy.resolve(md, resolveOptions);
                if (rr.hasError()) {
                    throw new RuntimeException(rr.getAllProblemMessages().toString());
                }
                // retrieve all resolved dependencies
                retrieveOptions.setDestArtifactPattern(packagesDirectory.getAbsolutePath() + File.separator +
                        "[organization]_[artifact]-[revision](-[classifier]).[ext]");
                ivy.retrieve(rr.getModuleDescriptor().getModuleRevisionId(),
                        retrieveOptions.setConfs(new String[]{ivyConfName}));

                return resolveDependencyPaths(rr.getArtifacts(), packagesDirectory);
            } finally {
                System.setOut(sysOut);
                clearIvyResolutionFiles(md.getModuleRevisionId(), ivySettings, ivyConfName);
            }
        }
    }

    /**
     * Output a comma-delimited list of paths for the downloaded jars to be added to the classpath
     *
     * @param artifacts      Sequence of dependencies that were resolved and retrieved
     * @param cacheDirectory directory where jars are cached
     * @return a comma-delimited list of paths for the dependencies
     */
    private static List<String> resolveDependencyPaths(List<Artifact> artifacts, File cacheDirectory) {
        return artifacts.stream().map(artifactInfo -> {
            ModuleRevisionId artifact = artifactInfo.getModuleRevisionId();
            return cacheDirectory.getAbsolutePath() + File.separator + artifact.getOrganisation() + "_"
                    + artifact.getName() + "-" + artifact.getRevision() + ".jar";
        }).collect(Collectors.toList());
    }

    private static void clearIvyResolutionFiles(
            ModuleRevisionId mdId,
            IvySettings ivySettings,
            String ivyConfName) {
        List<String> currentResolutionFiles = Lists.newArrayList(
                mdId.getOrganisation() + "-" + mdId.getName() + "-" + ivyConfName + ".xml",
                "resolved-" + mdId.getOrganisation() + "-" + mdId.getName() + "-" + mdId.getRevision() + ".xml",
                "resolved-" + mdId.getOrganisation() + "-" + mdId.getName() + "-" + mdId.getRevision() + ".properties"
        );
        currentResolutionFiles.forEach(filename ->
            new File(ivySettings.getDefaultCache(), filename).delete()
        );
    }

    /**
     * Extracts maven coordinates from a comma-delimited string. Coordinates should be provided
     * in the format `groupId:artifactId:version` or `groupId/artifactId:version`.
     *
     * @param coordinates Comma-delimited string of maven coordinates
     * @return Sequence of Maven coordinates
     */
    private static List<MavenCoordinate> extractMavenCoordinates(String coordinates) {
        return Arrays.stream(StringUtils.split(coordinates, ",")).map(p -> {
            String[] splits = p.replace("/", ":").split(":");
            if (splits.length != 3) {
                throw new IllegalArgumentException("DependencyUtils.extractMavenCoordinates: " +
                        "Provided Maven Coordinates must be in the form 'groupId:artifactId:version'. " +
                        "The coordinate provided is: " + p);
            }
            return new MavenCoordinate(splits[0], splits[1], splits[2]);
        }).collect(Collectors.toList());
    }

    /** Adds the given maven coordinates to Ivy's module descriptor. */
    private static void addDependenciesToIvy(
            DefaultModuleDescriptor md,
            List<MavenCoordinate> artifacts,
            String ivyConfName) {
        artifacts.forEach(mvn -> {
            ModuleRevisionId ri = ModuleRevisionId.newInstance(mvn.groupId, mvn.artifactId, mvn.version);
            DefaultDependencyDescriptor dd = new DefaultDependencyDescriptor(ri, false, false);
            dd.addDependencyConfiguration(ivyConfName, ivyConfName + "(runtime)");
            // scalastyle:off println
            printStream.println(dd.getDependencyId() + "added as a dependency");
            // scalastyle:on println
            md.addDependency(dd);
        });
    }

    private static void addExclusionRules(
            IvySettings ivySettings,
            String ivyConfName,
            DefaultModuleDescriptor md) {
        // Add scala exclusion rule
        md.addExcludeRule(createExclusion("*:scala-library:*", ivySettings, ivyConfName));
    }

    private static ExcludeRule createExclusion(
            String coords,
            IvySettings ivySettings,
            String ivyConfName) {
        MavenCoordinate c = extractMavenCoordinates(coords).get(0);
        ArtifactId id = new ArtifactId(new ModuleId(c.groupId, c.artifactId), "*", "*", "*");
        DefaultExcludeRule rule = new DefaultExcludeRule(id, ivySettings.getMatcher("glob"), null);
        rule.addConfiguration(ivyConfName);
        return rule;
    }

    private static class MavenCoordinate {
        String groupId;
        String artifactId;
        String version;

        public MavenCoordinate(String groupId, String artifactId, String version) {
            this.groupId = groupId;
            this.artifactId = artifactId;
            this.version = version;
        }
    }
}

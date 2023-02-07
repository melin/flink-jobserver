package io.github.melin.flink.jobserver.driver.support;

import io.github.melin.flink.jobserver.driver.util.DriverUtils;
import org.apache.hadoop.fs.FsUrlStreamHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * 优先加载从 FlinkClassLoader 加载className
 */
public class FlinkClassLoader extends URLClassLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(FlinkClassLoader.class);

    static {
        try {
            DriverUtils.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory());
        } catch (Exception e){
            LOGGER.error(e.getMessage(), e);
        }
    }

    public FlinkClassLoader(URL[] urls, ClassLoader parent){
        super(urls, parent);
    }

    public void addJar(String jar) throws Exception{
        this.addURL(new URL(jar));
    }
}

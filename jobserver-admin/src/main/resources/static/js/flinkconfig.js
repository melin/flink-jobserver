let FLINK_CONFIG_OPTIONS = [ {
    "caption" : "jobmanager.rpc.address",
    "docHTML" : "The config parameter defining the network address to connect to for communication with the job manager. This value is only interpreted in setups where a single JobManager with static name or address exists (simple standalone setups, or container setups with dynamic service name resolution). It is not used in many high-availability setups, when a leader-election service (like ZooKeeper) is used to elect and discover the JobManager leader from potentially multiple standby JobManagers."
}, {
    "caption" : "jobmanager.bind-host",
    "docHTML" : "The local address of the network interface that the job manager binds to. If not configured, '0.0.0.0' will be used."
}, {
    "caption" : "jobmanager.rpc.port",
    "value" : "jobmanager.rpc.port : 6123",
    "meta" : "default: 6123",
    "docHTML" : "The config parameter defining the network port to connect to for communication with the job manager. Like jobmanager.rpc.address, this value is only interpreted in setups where a single JobManager with static name/address and port exists (simple standalone setups, or container setups with dynamic service name resolution). This config option is not used in many high-availability setups, when a leader-election service (like ZooKeeper) is used to elect and discover the JobManager leader from potentially multiple standby JobManagers."
}, {
    "caption" : "jobmanager.rpc.bind-port",
    "docHTML" : "The local RPC port that the JobManager binds to. If not configured, the external port (configured by 'jobmanager.rpc.port') will be used."
}, {
    "caption" : "jobmanager.heap.size",
    "docHTML" : "JVM heap size for the JobManager."
}, {
    "caption" : "jobmanager.heap.mb",
    "docHTML" : "JVM heap size (in megabytes) for the JobManager."
}, {
    "caption" : "jobmanager.memory.process.size",
    "docHTML" : "Total Process Memory size for the JobManager. This includes all the memory that a JobManager JVM process consumes, consisting of Total Flink Memory, JVM Metaspace, and JVM Overhead. In containerized setups, this should be set to the container memory. See also 'jobmanager.memory.flink.size' for Total Flink Memory size configuration."
}, {
    "caption" : "jobmanager.memory.flink.size",
    "docHTML" : "Total Flink Memory size for the JobManager. This includes all the memory that a JobManager consumes, except for JVM Metaspace and JVM Overhead. It consists of JVM Heap Memory and Off-heap Memory. See also 'jobmanager.memory.process.size' for total process memory size configuration."
}, {
    "caption" : "jobmanager.memory.heap.size",
    "docHTML" : "JVM Heap Memory size for JobManager. The minimum recommended JVM Heap size is 128.000mb (134217728 bytes)."
}, {
    "caption" : "jobmanager.memory.off-heap.size",
    "value" : "jobmanager.memory.off-heap.size : 128 mb",
    "meta" : "default: 128 mb",
    "docHTML" : "Off-heap Memory size for JobManager. This option covers all off-heap memory usage including direct and native memory allocation. The JVM direct memory limit of the JobManager process (-XX:MaxDirectMemorySize) will be set to this value if the limit is enabled by 'jobmanager.memory.enable-jvm-direct-memory-limit'. "
}, {
    "caption" : "jobmanager.memory.enable-jvm-direct-memory-limit",
    "value" : "jobmanager.memory.enable-jvm-direct-memory-limit : false",
    "meta" : "default: false",
    "docHTML" : "Whether to enable the JVM direct memory limit of the JobManager process (-XX:MaxDirectMemorySize). The limit will be set to the value of '%s' option. "
}, {
    "caption" : "jobmanager.memory.jvm-metaspace.size",
    "value" : "jobmanager.memory.jvm-metaspace.size : 256 mb",
    "meta" : "default: 256 mb",
    "docHTML" : "JVM Metaspace Size for the JobManager."
}, {
    "caption" : "jobmanager.memory.jvm-overhead.min",
    "value" : "jobmanager.memory.jvm-overhead.min : 192 mb",
    "meta" : "default: 192 mb",
    "docHTML" : "Min JVM Overhead size for the JobManager. This is off-heap memory reserved for JVM overhead, such as thread stack space, compile cache, etc. This includes native memory but not direct memory, and will not be counted when Flink calculates JVM max direct memory size parameter. The size of JVM Overhead is derived to make up the configured fraction of the Total Process Memory. If the derived size is less or greater than the configured min or max size, the min or max size will be used. The exact size of JVM Overhead can be explicitly specified by setting the min and max size to the same value."
}, {
    "caption" : "jobmanager.memory.jvm-overhead.max",
    "value" : "jobmanager.memory.jvm-overhead.max : 1 gb",
    "meta" : "default: 1 gb",
    "docHTML" : "Max JVM Overhead size for the JobManager. This is off-heap memory reserved for JVM overhead, such as thread stack space, compile cache, etc. This includes native memory but not direct memory, and will not be counted when Flink calculates JVM max direct memory size parameter. The size of JVM Overhead is derived to make up the configured fraction of the Total Process Memory. If the derived size is less or greater than the configured min or max size, the min or max size will be used. The exact size of JVM Overhead can be explicitly specified by setting the min and max size to the same value."
}, {
    "caption" : "jobmanager.memory.jvm-overhead.fraction",
    "value" : "jobmanager.memory.jvm-overhead.fraction : 0.1",
    "meta" : "default: 0.1",
    "docHTML" : "Fraction of Total Process Memory to be reserved for JVM Overhead. This is off-heap memory reserved for JVM overhead, such as thread stack space, compile cache, etc. This includes native memory but not direct memory, and will not be counted when Flink calculates JVM max direct memory size parameter. The size of JVM Overhead is derived to make up the configured fraction of the Total Process Memory. If the derived size is less or greater than the configured min or max size, the min or max size will be used. The exact size of JVM Overhead can be explicitly specified by setting the min and max size to the same value."
}, {
    "caption" : "jobmanager.execution.attempts-history-size",
    "value" : "jobmanager.execution.attempts-history-size : 16",
    "meta" : "default: 16",
    "docHTML" : "The maximum number of historical execution attempts kept in history."
}, {
    "caption" : "jobmanager.execution.failover-strategy",
    "value" : "jobmanager.execution.failover-strategy : region",
    "meta" : "default: region"
}, {
    "caption" : "jobmanager.archive.fs.dir",
    "docHTML" : "Dictionary for JobManager to store the archives of completed jobs."
}, {
    "caption" : "jobstore.cache-size",
    "value" : "jobstore.cache-size : 52428800",
    "meta" : "default: 52428800",
    "docHTML" : "The job store cache size in bytes which is used to keep completed jobs in memory."
}, {
    "caption" : "jobstore.expiration-time",
    "value" : "jobstore.expiration-time : 3600",
    "meta" : "default: 3600",
    "docHTML" : "The time in seconds after which a completed job expires and is purged from the job store."
}, {
    "caption" : "jobstore.max-capacity",
    "value" : "jobstore.max-capacity : 2147483647",
    "meta" : "default: 2147483647",
    "docHTML" : "The max number of completed jobs that can be kept in the job store. NOTICE: if memory store keeps too many jobs in session cluster, it may cause FullGC or OOM in jm."
}, {
    "caption" : "jobstore.type",
    "value" : "jobstore.type : File",
    "meta" : "default: File"
}, {
    "caption" : "jobmanager.retrieve-taskmanager-hostname",
    "value" : "jobmanager.retrieve-taskmanager-hostname : true",
    "meta" : "default: true",
    "docHTML" : "Flag indicating whether JobManager would retrieve canonical host name of TaskManager during registration. If the option is set to \"false\", TaskManager registration with JobManager could be faster, since no reverse DNS lookup is performed. However, local input split assignment (such as for HDFS files) may be impacted."
}, {
    "caption" : "jobmanager.future-pool.size",
    "docHTML" : "The size of the future thread pool to execute future callbacks for all spawned JobMasters. If no value is specified, then Flink defaults to the number of available CPU cores."
}, {
    "caption" : "jobmanager.io-pool.size",
    "docHTML" : "The size of the IO thread pool to run blocking operations for all spawned JobMasters. This includes recovery and completion of checkpoints. Increase this value if you experience slow checkpoint operations when running many jobs. If no value is specified, then Flink defaults to the number of available CPU cores."
}, {
    "caption" : "slot.request.timeout",
    "value" : "slot.request.timeout : 300000",
    "meta" : "default: 300000",
    "docHTML" : "The timeout in milliseconds for requesting a slot from Slot Pool."
}, {
    "caption" : "slot.idle.timeout",
    "value" : "slot.idle.timeout : 50000",
    "meta" : "default: 50000",
    "docHTML" : "The timeout in milliseconds for a idle slot in Slot Pool."
}, {
    "caption" : "jobmanager.scheduler",
    "value" : "jobmanager.scheduler : Default",
    "meta" : "default: Default"
}, {
    "caption" : "scheduler-mode",
    "docHTML" : "Determines the mode of the scheduler. Note that %s=%s is only supported by standalone application deployments, not by active resource managers (YARN, Kubernetes) or session clusters."
}, {
    "caption" : "jobmanager.adaptive-scheduler.min-parallelism-increase",
    "value" : "jobmanager.adaptive-scheduler.min-parallelism-increase : 1",
    "meta" : "default: 1",
    "docHTML" : "Configure the minimum increase in parallelism for a job to scale up."
}, {
    "caption" : "jobmanager.adaptive-scheduler.resource-wait-timeout",
    "value" : "jobmanager.adaptive-scheduler.resource-wait-timeout : PT5M",
    "meta" : "default: PT5M"
}, {
    "caption" : "jobmanager.adaptive-scheduler.resource-stabilization-timeout",
    "value" : "jobmanager.adaptive-scheduler.resource-stabilization-timeout : PT10S",
    "meta" : "default: PT10S"
}, {
    "caption" : "jobmanager.partition.release-during-job-execution",
    "value" : "jobmanager.partition.release-during-job-execution : true",
    "meta" : "default: true",
    "docHTML" : "Controls whether partitions should already be released during the job execution."
}, {
    "caption" : "jobmanager.adaptive-batch-scheduler.min-parallelism",
    "value" : "jobmanager.adaptive-batch-scheduler.min-parallelism : 1",
    "meta" : "default: 1",
    "docHTML" : "The lower bound of allowed parallelism to set adaptively if %s has been set to %s. Currently, this option should be configured as a power of 2, otherwise it will also be rounded up to a power of 2 automatically."
}, {
    "caption" : "jobmanager.adaptive-batch-scheduler.max-parallelism",
    "value" : "jobmanager.adaptive-batch-scheduler.max-parallelism : 128",
    "meta" : "default: 128",
    "docHTML" : "The upper bound of allowed parallelism to set adaptively if %s has been set to %s. Currently, this option should be configured as a power of 2, otherwise it will also be rounded down to a power of 2 automatically."
}, {
    "caption" : "jobmanager.adaptive-batch-scheduler.avg-data-volume-per-task",
    "value" : "jobmanager.adaptive-batch-scheduler.avg-data-volume-per-task : 1 gb",
    "meta" : "default: 1 gb",
    "docHTML" : "The average size of data volume to expect each task instance to process if %s has been set to %s. Note that since the parallelism of the vertices is adjusted to a power of 2, the actual average size will be 0.75~1.5 times this value. It is also important to note that when data skew occurs or the decided parallelism reaches the %s (due to too much data), the data actually processed by some tasks may far exceed this value."
}, {
    "caption" : "jobmanager.adaptive-batch-scheduler.default-source-parallelism",
    "value" : "jobmanager.adaptive-batch-scheduler.default-source-parallelism : 1",
    "meta" : "default: 1",
    "docHTML" : "The default parallelism of source vertices if %s has been set to %s"
}, {
    "caption" : "jobmanager.adaptive-batch-scheduler.speculative.enabled",
    "value" : "jobmanager.adaptive-batch-scheduler.speculative.enabled : false",
    "meta" : "default: false",
    "docHTML" : "Controls whether to enable speculative execution."
}, {
    "caption" : "jobmanager.adaptive-batch-scheduler.speculative.max-concurrent-executions",
    "value" : "jobmanager.adaptive-batch-scheduler.speculative.max-concurrent-executions : 2",
    "meta" : "default: 2",
    "docHTML" : "Controls the maximum number of execution attempts of each operator that can execute concurrently, including the original one and speculative ones."
}, {
    "caption" : "jobmanager.adaptive-batch-scheduler.speculative.block-slow-node-duration",
    "value" : "jobmanager.adaptive-batch-scheduler.speculative.block-slow-node-duration : PT1M",
    "meta" : "default: PT1M",
    "docHTML" : "Controls how long an detected slow node should be blocked for."
}, {
    "caption" : "jobmanager.resource-id",
    "docHTML" : "The JobManager's ResourceID. If not configured, the ResourceID will be generated randomly."
}, {
    "caption" : "taskmanager.heap.size",
    "docHTML" : "JVM heap size for the TaskManagers, which are the parallel workers of the system. On YARN setups, this value is automatically configured to the size of the TaskManager's YARN container, minus a certain tolerance value."
}, {
    "caption" : "taskmanager.heap.mb",
    "docHTML" : "JVM heap size (in megabytes) for the TaskManagers, which are the parallel workers of the system. On YARN setups, this value is automatically configured to the size of the TaskManager's YARN container, minus a certain tolerance value."
}, {
    "caption" : "taskmanager.jvm-exit-on-oom",
    "value" : "taskmanager.jvm-exit-on-oom : false",
    "meta" : "default: false",
    "docHTML" : "Whether to kill the TaskManager when the task thread throws an OutOfMemoryError."
}, {
    "caption" : "taskmanager.exit-on-fatal-akka-error",
    "value" : "taskmanager.exit-on-fatal-akka-error : false",
    "meta" : "default: false",
    "docHTML" : "Whether the quarantine monitor for task managers shall be started. The quarantine monitor shuts down the actor system if it detects that it has quarantined another actor system or if it has been quarantined by another actor system."
}, {
    "caption" : "taskmanager.host",
    "docHTML" : "The external address of the network interface where the TaskManager is exposed. Because different TaskManagers need different values for this option, usually it is specified in an additional non-shared TaskManager-specific config file."
}, {
    "caption" : "taskmanager.bind-host",
    "docHTML" : "The local address of the network interface that the task manager binds to. If not configured, '0.0.0.0' will be used."
}, {
    "caption" : "taskmanager.rpc.port",
    "value" : "taskmanager.rpc.port : 0",
    "meta" : "default: 0",
    "docHTML" : "The external RPC port where the TaskManager is exposed. Accepts a list of ports (“50100,50101”), ranges (“50100-50200”) or a combination of both. It is recommended to set a range of ports to avoid collisions when multiple TaskManagers are running on the same machine."
}, {
    "caption" : "taskmanager.rpc.bind-port",
    "docHTML" : "The local RPC port that the TaskManager binds to. If not configured, the external port (configured by 'taskmanager.rpc.port') will be used."
}, {
    "caption" : "taskmanager.registration.initial-backoff",
    "value" : "taskmanager.registration.initial-backoff : PT0.5S",
    "meta" : "default: PT0.5S",
    "docHTML" : "The initial registration backoff between two consecutive registration attempts. The backoff is doubled for each new registration attempt until it reaches the maximum registration backoff."
}, {
    "caption" : "taskmanager.registration.max-backoff",
    "value" : "taskmanager.registration.max-backoff : PT30S",
    "meta" : "default: PT30S",
    "docHTML" : "The maximum registration backoff between two consecutive registration attempts. The max registration backoff requires a time unit specifier (ms/s/min/h/d)."
}, {
    "caption" : "taskmanager.registration.refused-backoff",
    "value" : "taskmanager.registration.refused-backoff : PT10S",
    "meta" : "default: PT10S",
    "docHTML" : "The backoff after a registration has been refused by the job manager before retrying to connect."
}, {
    "caption" : "taskmanager.registration.timeout",
    "value" : "taskmanager.registration.timeout : PT5M",
    "meta" : "default: PT5M",
    "docHTML" : "Defines the timeout for the TaskManager registration. If the duration is exceeded without a successful registration, then the TaskManager terminates."
}, {
    "caption" : "taskmanager.numberOfTaskSlots",
    "value" : "taskmanager.numberOfTaskSlots : 1",
    "meta" : "default: 1",
    "docHTML" : "The number of parallel operator or user function instances that a single TaskManager can run. If this value is larger than 1, a single TaskManager takes multiple instances of a function or operator. That way, the TaskManager can utilize multiple CPU cores, but at the same time, the available memory is divided between the different operator or function instances. This value is typically proportional to the number of physical CPU cores that the TaskManager's machine has (e.g., equal to the number of cores, or half the number of cores)."
}, {
    "caption" : "taskmanager.slot.timeout",
    "value" : "taskmanager.slot.timeout : PT10S",
    "meta" : "default: PT10S",
    "docHTML" : "Timeout used for identifying inactive slots. The TaskManager will free the slot if it does not become active within the given amount of time. Inactive slots can be caused by an out-dated slot request. If no value is configured, then it will fall back to %s."
}, {
    "caption" : "taskmanager.debug.memory.log",
    "value" : "taskmanager.debug.memory.log : false",
    "meta" : "default: false",
    "docHTML" : "Flag indicating whether to start a thread, which repeatedly logs the memory usage of the JVM."
}, {
    "caption" : "taskmanager.debug.memory.log-interval",
    "value" : "taskmanager.debug.memory.log-interval : 5000",
    "meta" : "default: 5000",
    "docHTML" : "The interval (in ms) for the log thread to log the current memory usage."
}, {
    "caption" : "taskmanager.memory.segment-size",
    "value" : "taskmanager.memory.segment-size : 32 kb",
    "meta" : "default: 32 kb",
    "docHTML" : "Size of memory buffers used by the network stack and the memory manager."
}, {
    "caption" : "taskmanager.memory.min-segment-size",
    "value" : "taskmanager.memory.min-segment-size : 256 bytes",
    "meta" : "default: 256 bytes",
    "docHTML" : "Minimum possible size of memory buffers used by the network stack and the memory manager. ex. can be used for automatic buffer size adjustment."
}, {
    "caption" : "taskmanager.network.bind-policy",
    "value" : "taskmanager.network.bind-policy : ip",
    "meta" : "default: ip"
}, {
    "caption" : "taskmanager.resource-id",
    "docHTML" : "The TaskManager's ResourceID. If not configured, the ResourceID will be generated with the \"RpcAddress:RpcPort\" and a 6-character random string. Notice that this option is not valid in Yarn and Native Kubernetes mode."
}, {
    "caption" : "taskmanager.cpu.cores",
    "docHTML" : "CPU cores for the TaskExecutors. In case of Yarn setups, this value will be rounded to the closest positive integer. If not explicitly configured, legacy config options 'yarn.containers.vcores' and 'kubernetes.taskmanager.cpu' will be used for Yarn / Kubernetes setups, and 'taskmanager.numberOfTaskSlots' will be used for standalone setups (approximate number of slots)."
}, {
    "caption" : "taskmanager.memory.process.size",
    "docHTML" : "Total Process Memory size for the TaskExecutors. This includes all the memory that a TaskExecutor consumes, consisting of Total Flink Memory, JVM Metaspace, and JVM Overhead. On containerized setups, this should be set to the container memory. See also 'taskmanager.memory.flink.size' for total Flink memory size configuration."
}, {
    "caption" : "taskmanager.memory.flink.size",
    "docHTML" : "Total Flink Memory size for the TaskExecutors. This includes all the memory that a TaskExecutor consumes, except for JVM Metaspace and JVM Overhead. It consists of Framework Heap Memory, Task Heap Memory, Task Off-Heap Memory, Managed Memory, and Network Memory. See also 'taskmanager.memory.process.size' for total process memory size configuration."
}, {
    "caption" : "taskmanager.memory.framework.heap.size",
    "value" : "taskmanager.memory.framework.heap.size : 128 mb",
    "meta" : "default: 128 mb",
    "docHTML" : "Framework Heap Memory size for TaskExecutors. This is the size of JVM heap memory reserved for TaskExecutor framework, which will not be allocated to task slots."
}, {
    "caption" : "taskmanager.memory.framework.off-heap.size",
    "value" : "taskmanager.memory.framework.off-heap.size : 128 mb",
    "meta" : "default: 128 mb",
    "docHTML" : "Framework Off-Heap Memory size for TaskExecutors. This is the size of off-heap memory (JVM direct memory and native memory) reserved for TaskExecutor framework, which will not be allocated to task slots. The configured value will be fully counted when Flink calculates the JVM max direct memory size parameter."
}, {
    "caption" : "taskmanager.memory.task.heap.size",
    "docHTML" : "Task Heap Memory size for TaskExecutors. This is the size of JVM heap memory reserved for tasks. If not specified, it will be derived as Total Flink Memory minus Framework Heap Memory, Framework Off-Heap Memory, Task Off-Heap Memory, Managed Memory and Network Memory."
}, {
    "caption" : "taskmanager.memory.task.off-heap.size",
    "value" : "taskmanager.memory.task.off-heap.size : 0 bytes",
    "meta" : "default: 0 bytes",
    "docHTML" : "Task Off-Heap Memory size for TaskExecutors. This is the size of off heap memory (JVM direct memory and native memory) reserved for tasks. The configured value will be fully counted when Flink calculates the JVM max direct memory size parameter."
}, {
    "caption" : "taskmanager.memory.managed.size",
    "docHTML" : "Managed Memory size for TaskExecutors. This is the size of off-heap memory managed by the memory manager, reserved for sorting, hash tables, caching of intermediate results and RocksDB state backend. Memory consumers can either allocate memory from the memory manager in the form of MemorySegments, or reserve bytes from the memory manager and keep their memory usage within that boundary. If unspecified, it will be derived to make up the configured fraction of the Total Flink Memory."
}, {
    "caption" : "taskmanager.memory.managed.fraction",
    "value" : "taskmanager.memory.managed.fraction : 0.4",
    "meta" : "default: 0.4",
    "docHTML" : "Fraction of Total Flink Memory to be used as Managed Memory, if Managed Memory size is not explicitly specified."
}, {
    "caption" : "taskmanager.memory.managed.consumer-weights",
    "value" : "taskmanager.memory.managed.consumer-weights : {OPERATOR=70, STATE_BACKEND=70, PYTHON=30}",
    "meta" : "default: {OPERATOR=70, STATE_BACKEND=70, PYTHON=30}",
    "docHTML" : "Managed memory weights for different kinds of consumers. A slot’s managed memory is shared by all kinds of consumers it contains, proportionally to the kinds’ weights and regardless of the number of consumers from each kind. Currently supported kinds of consumers are OPERATOR (for built-in algorithms), STATE_BACKEND (for RocksDB state backend) and PYTHON (for Python processes)."
}, {
    "caption" : "taskmanager.memory.network.min",
    "value" : "taskmanager.memory.network.min : 64 mb",
    "meta" : "default: 64 mb",
    "docHTML" : "Min Network Memory size for TaskExecutors. Network Memory is off-heap memory reserved for ShuffleEnvironment (e.g., network buffers). Network Memory size is derived to make up the configured fraction of the Total Flink Memory. If the derived size is less/greater than the configured min/max size, the min/max size will be used. The exact size of Network Memory can be explicitly specified by setting the min/max to the same value."
}, {
    "caption" : "taskmanager.memory.network.max",
    "value" : "taskmanager.memory.network.max : 1 gb",
    "meta" : "default: 1 gb",
    "docHTML" : "Max Network Memory size for TaskExecutors. Network Memory is off-heap memory reserved for ShuffleEnvironment (e.g., network buffers). Network Memory size is derived to make up the configured fraction of the Total Flink Memory. If the derived size is less/greater than the configured min/max size, the min/max size will be used. The exact size of Network Memory can be explicitly specified by setting the min/max to the same value."
}, {
    "caption" : "taskmanager.memory.network.fraction",
    "value" : "taskmanager.memory.network.fraction : 0.1",
    "meta" : "default: 0.1",
    "docHTML" : "Fraction of Total Flink Memory to be used as Network Memory. Network Memory is off-heap memory reserved for ShuffleEnvironment (e.g., network buffers). Network Memory size is derived to make up the configured fraction of the Total Flink Memory. If the derived size is less/greater than the configured min/max size, the min/max size will be used. The exact size of Network Memory can be explicitly specified by setting the min/max size to the same value."
}, {
    "caption" : "taskmanager.network.memory.buffer-debloat.period",
    "value" : "taskmanager.network.memory.buffer-debloat.period : PT0.2S",
    "meta" : "default: PT0.2S",
    "docHTML" : "The minimum period of time after which the buffer size will be debloated if required. The low value provides a fast reaction to the load fluctuation but can influence the performance."
}, {
    "caption" : "taskmanager.network.memory.buffer-debloat.samples",
    "value" : "taskmanager.network.memory.buffer-debloat.samples : 20",
    "meta" : "default: 20",
    "docHTML" : "The number of the last buffer size values that will be taken for the correct calculation of the new one."
}, {
    "caption" : "taskmanager.network.memory.buffer-debloat.target",
    "value" : "taskmanager.network.memory.buffer-debloat.target : PT1S",
    "meta" : "default: PT1S",
    "docHTML" : "The target total time after which buffered in-flight data should be fully consumed. This configuration option will be used, in combination with the measured throughput, to adjust the amount of in-flight data."
}, {
    "caption" : "taskmanager.network.memory.buffer-debloat.enabled",
    "value" : "taskmanager.network.memory.buffer-debloat.enabled : false",
    "meta" : "default: false",
    "docHTML" : "The switch of the automatic buffered debloating feature. If enabled the amount of in-flight data will be adjusted automatically accordingly to the measured throughput."
}, {
    "caption" : "taskmanager.network.memory.buffer-debloat.threshold-percentages",
    "value" : "taskmanager.network.memory.buffer-debloat.threshold-percentages : 25",
    "meta" : "default: 25",
    "docHTML" : "The minimum difference in percentage between the newly calculated buffer size and the old one to announce the new value. Can be used to avoid constant back and forth small adjustments."
}, {
    "caption" : "taskmanager.memory.framework.off-heap.batch-shuffle.size",
    "value" : "taskmanager.memory.framework.off-heap.batch-shuffle.size : 64 mb",
    "meta" : "default: 64 mb",
    "docHTML" : "Size of memory used by blocking shuffle for shuffle data read (currently only used by sort-shuffle and hybrid shuffle). Notes: 1) The memory is cut from 'taskmanager.memory.framework.off-heap.size' so must be smaller than that, which means you may also need to increase 'taskmanager.memory.framework.off-heap.size' after you increase this config value; 2) This memory size can influence the shuffle performance and you can increase this config value for large-scale batch jobs (for example, to 128M or 256M)."
}, {
    "caption" : "taskmanager.memory.jvm-metaspace.size",
    "value" : "taskmanager.memory.jvm-metaspace.size : 256 mb",
    "meta" : "default: 256 mb",
    "docHTML" : "JVM Metaspace Size for the TaskExecutors."
}, {
    "caption" : "taskmanager.memory.jvm-overhead.min",
    "value" : "taskmanager.memory.jvm-overhead.min : 192 mb",
    "meta" : "default: 192 mb",
    "docHTML" : "Min JVM Overhead size for the TaskExecutors. This is off-heap memory reserved for JVM overhead, such as thread stack space, compile cache, etc. This includes native memory but not direct memory, and will not be counted when Flink calculates JVM max direct memory size parameter. The size of JVM Overhead is derived to make up the configured fraction of the Total Process Memory. If the derived size is less/greater than the configured min/max size, the min/max size will be used. The exact size of JVM Overhead can be explicitly specified by setting the min/max size to the same value."
}, {
    "caption" : "taskmanager.memory.jvm-overhead.max",
    "value" : "taskmanager.memory.jvm-overhead.max : 1 gb",
    "meta" : "default: 1 gb",
    "docHTML" : "Max JVM Overhead size for the TaskExecutors. This is off-heap memory reserved for JVM overhead, such as thread stack space, compile cache, etc. This includes native memory but not direct memory, and will not be counted when Flink calculates JVM max direct memory size parameter. The size of JVM Overhead is derived to make up the configured fraction of the Total Process Memory. If the derived size is less/greater than the configured min/max size, the min/max size will be used. The exact size of JVM Overhead can be explicitly specified by setting the min/max size to the same value."
}, {
    "caption" : "taskmanager.memory.jvm-overhead.fraction",
    "value" : "taskmanager.memory.jvm-overhead.fraction : 0.1",
    "meta" : "default: 0.1",
    "docHTML" : "Fraction of Total Process Memory to be reserved for JVM Overhead. This is off-heap memory reserved for JVM overhead, such as thread stack space, compile cache, etc. This includes native memory but not direct memory, and will not be counted when Flink calculates JVM max direct memory size parameter. The size of JVM Overhead is derived to make up the configured fraction of the Total Process Memory. If the derived size is less/greater than the configured min/max size, the min/max size will be used. The exact size of JVM Overhead can be explicitly specified by setting the min/max size to the same value."
}, {
    "caption" : "task.cancellation.interval",
    "value" : "task.cancellation.interval : 30000",
    "meta" : "default: 30000",
    "docHTML" : "Time interval between two successive task cancellation attempts in milliseconds."
}, {
    "caption" : "task.cancellation.timeout",
    "value" : "task.cancellation.timeout : 180000",
    "meta" : "default: 180000",
    "docHTML" : "Timeout in milliseconds after which a task cancellation times out and leads to a fatal TaskManager error. A value of 0 deactivates the watch dog. Notice that a task cancellation is different from both a task failure and a clean shutdown.  Task cancellation timeout only applies to task cancellation and does not apply to task closing/clean-up caused by a task failure or a clean shutdown."
}, {
    "caption" : "task.cancellation.timers.timeout",
    "value" : "task.cancellation.timers.timeout : 7500",
    "meta" : "default: 7500",
    "docHTML" : "Time we wait for the timers in milliseconds to finish all pending timer threads when the stream task is cancelled."
}, {
    "caption" : "classloader.resolve-order",
    "value" : "classloader.resolve-order : child-first",
    "meta" : "default: child-first",
    "docHTML" : "Defines the class resolution strategy when loading classes from user code, meaning whether to first check the user code jar (\"child-first\") or the application classpath (\"parent-first\"). The default settings indicate to load classes first from the user code jar, which means that user code jars can include and load different dependencies than Flink uses (transitively)."
}, {
    "caption" : "classloader.parent-first-patterns.default",
    "value" : "classloader.parent-first-patterns.default : [java., scala., org.apache.flink., com.esotericsoftware.kryo, org.apache.hadoop., javax.annotation., org.xml, javax.xml, org.apache.xerces, org.w3c, org.rocksdb., org.slf4j, org.apache.log4j, org.apache.logging, org.apache.commons.logging, ch.qos.logback]",
    "meta" : "default: [java., scala., org.apache.flink., com.esotericsoftware.kryo, org.apache.hadoop., javax.annotation., org.xml, javax.xml, org.apache.xerces, org.w3c, org.rocksdb., org.slf4j, org.apache.log4j, org.apache.logging, org.apache.commons.logging, ch.qos.logback]",
    "docHTML" : "A (semicolon-separated) list of patterns that specifies which classes should always be resolved through the parent ClassLoader first. A pattern is a simple prefix that is checked against the fully qualified class name. This setting should generally not be modified. To add another pattern we recommend to use \"classloader.parent-first-patterns.additional\" instead."
}, {
    "caption" : "classloader.parent-first-patterns.additional",
    "value" : "classloader.parent-first-patterns.additional : []",
    "meta" : "default: []",
    "docHTML" : "A (semicolon-separated) list of patterns that specifies which classes should always be resolved through the parent ClassLoader first. A pattern is a simple prefix that is checked against the fully qualified class name. These patterns are appended to \"classloader.parent-first-patterns.default\"."
}, {
    "caption" : "classloader.fail-on-metaspace-oom-error",
    "value" : "classloader.fail-on-metaspace-oom-error : true",
    "meta" : "default: true",
    "docHTML" : "Fail Flink JVM processes if 'OutOfMemoryError: Metaspace' is thrown while trying to load a user code class."
}, {
    "caption" : "classloader.check-leaked-classloader",
    "value" : "classloader.check-leaked-classloader : true",
    "meta" : "default: true",
    "docHTML" : "Fails attempts at loading classes if the user classloader of a job is used after it has terminated.\nThis is usually caused by the classloader being leaked by lingering threads or misbehaving libraries, which may also result in the classloader being used by other jobs.\nThis check should only be disabled if such a leak prevents further jobs from running."
}, {
    "caption" : "plugin.classloader.parent-first-patterns.default",
    "value" : "plugin.classloader.parent-first-patterns.default : [java., org.apache.flink., javax.annotation., org.slf4j, org.apache.log4j, org.apache.logging, org.apache.commons.logging, ch.qos.logback]",
    "meta" : "default: [java., org.apache.flink., javax.annotation., org.slf4j, org.apache.log4j, org.apache.logging, org.apache.commons.logging, ch.qos.logback]",
    "docHTML" : "A (semicolon-separated) list of patterns that specifies which classes should always be resolved through the plugin parent ClassLoader first. A pattern is a simple prefix that is checked  against the fully qualified class name. This setting should generally not be modified. To add another  pattern we recommend to use \"plugin.classloader.parent-first-patterns.additional\" instead."
}, {
    "caption" : "plugin.classloader.parent-first-patterns.additional",
    "value" : "plugin.classloader.parent-first-patterns.additional : []",
    "meta" : "default: []",
    "docHTML" : "A (semicolon-separated) list of patterns that specifies which classes should always be resolved through the plugin parent ClassLoader first. A pattern is a simple prefix that is checked  against the fully qualified class name. These patterns are appended to \"plugin.classloader.parent-first-patterns.default\"."
}, {
    "caption" : "env.java.opts",
    "value" : "env.java.opts : ",
    "meta" : "default: ",
    "docHTML" : "Java options to start the JVM of all Flink processes with."
}, {
    "caption" : "env.java.opts.jobmanager",
    "value" : "env.java.opts.jobmanager : ",
    "meta" : "default: ",
    "docHTML" : "Java options to start the JVM of the JobManager with."
}, {
    "caption" : "env.java.opts.taskmanager",
    "value" : "env.java.opts.taskmanager : ",
    "meta" : "default: ",
    "docHTML" : "Java options to start the JVM of the TaskManager with."
}, {
    "caption" : "env.java.opts.historyserver",
    "value" : "env.java.opts.historyserver : ",
    "meta" : "default: ",
    "docHTML" : "Java options to start the JVM of the HistoryServer with."
}, {
    "caption" : "env.java.opts.client",
    "value" : "env.java.opts.client = ",
    "meta" : "default: ",
    "docHTML" : "Java options to start the JVM of the Flink Client with."
}, {
    "caption" : "env.log.dir",
    "docHTML" : "Defines the directory where the Flink logs are saved. It has to be an absolute path. (Defaults to the log directory under Flink’s home)"
}, {
    "caption" : "env.pid.dir",
    "value" : "env.pid.dir : /tmp",
    "meta" : "default: /tmp",
    "docHTML" : "Defines the directory where the flink-<host>-<process>.pid files are saved."
}, {
    "caption" : "env.log.max",
    "value" : "env.log.max : 5",
    "meta" : "default: 5",
    "docHTML" : "The maximum number of old log files to keep."
}, {
    "caption" : "env.ssh.opts",
    "docHTML" : "Additional command line options passed to SSH clients when starting or stopping JobManager, TaskManager, and Zookeeper services (start-cluster.sh, stop-cluster.sh, start-zookeeper-quorum.sh, stop-zookeeper-quorum.sh)."
}, {
    "caption" : "env.hadoop.conf.dir",
    "docHTML" : "Path to hadoop configuration directory. It is required to read HDFS and/or YARN configuration. You can also set it via environment variable."
}, {
    "caption" : "env.yarn.conf.dir",
    "docHTML" : "Path to yarn configuration directory. It is required to run flink on YARN. You can also set it via environment variable."
}, {
    "caption" : "env.hbase.conf.dir",
    "docHTML" : "Path to hbase configuration directory. It is required to read HBASE configuration. You can also set it via environment variable."
}, {
    "caption" : "io.tmp.dirs",
    "value" : "io.tmp.dirs : /var/folders/01/p115bpln55b2nxcg1q_lb_240000gn/T/",
    "meta" : "default: /var/folders/01/p115bpln55b2nxcg1q_lb_240000gn/T/",
    "docHTML" : "Directories for temporary files, separated by\",\", \"|\", or the system's java.io.File.pathSeparator."
}, {
    "caption" : "parallelism.default",
    "value" : "parallelism.default : 1",
    "meta" : "default: 1",
    "docHTML" : "Default parallelism for jobs."
}, {
    "caption" : "fs.default-scheme",
    "docHTML" : "The default filesystem scheme, used for paths that do not declare a scheme explicitly. May contain an authority, e.g. host:port in case of an HDFS NameNode."
}, {
    "caption" : "fs.allowed-fallback-filesystems",
    "value" : "fs.allowed-fallback-filesystems : ",
    "meta" : "default: ",
    "docHTML" : "A (semicolon-separated) list of file schemes, for which Hadoop can be used instead of an appropriate Flink plugin. (example: s3;wasb)"
}, {
    "caption" : "fs.overwrite-files",
    "value" : "fs.overwrite-files : false",
    "meta" : "default: false",
    "docHTML" : "Specifies whether file output writers should overwrite existing files by default. Set to \"true\" to overwrite by default,\"false\" otherwise."
}, {
    "caption" : "fs.output.always-create-directory",
    "value" : "fs.output.always-create-directory : false",
    "meta" : "default: false",
    "docHTML" : "File writers running with a parallelism larger than one create a directory for the output file path and put the different result files (one per parallel writer task) into that directory. If this option is set to \"true\", writers with a parallelism of 1 will also create a directory and place a single result file into it. If the option is set to \"false\", the writer will directly create the file directly at the output path, without creating a containing directory."
}, {
    "caption" : "table.exec.state.ttl",
    "value" : "table.exec.state.ttl : PT0S",
    "meta" : "default: PT0S",
    "docHTML" : "Specifies a minimum time interval for how long idle state (i.e. state which was not updated), will be retained. State will never be cleared until it was idle for less than the minimum time, and will be cleared at some time after it was idle. Default is never clean-up the state. NOTE: Cleaning up state requires additional overhead for bookkeeping. Default value is 0, which means that it will never clean up state."
}, {
    "caption" : "table.exec.source.idle-timeout",
    "value" : "table.exec.source.idle-timeout : PT0S",
    "meta" : "default: PT0S",
    "docHTML" : "When a source do not receive any elements for the timeout time, it will be marked as temporarily idle. This allows downstream tasks to advance their watermarks without the need to wait for watermarks from this source while it is idle. Default value is 0, which means detecting source idleness is not enabled."
}, {
    "caption" : "table.exec.source.cdc-events-duplicate",
    "value" : "table.exec.source.cdc-events-duplicate : false",
    "meta" : "default: false"
}, {
    "caption" : "table.exec.sink.not-null-enforcer",
    "value" : "table.exec.sink.not-null-enforcer : ERROR",
    "meta" : "default: ERROR",
    "docHTML" : "Determines how Flink enforces NOT NULL column constraints when inserting null values."
}, {
    "caption" : "table.exec.sink.type-length-enforcer",
    "value" : "table.exec.sink.type-length-enforcer : IGNORE",
    "meta" : "default: IGNORE",
    "docHTML" : "Determines whether values for columns with CHAR(<length>)/VARCHAR(<length>)/BINARY(<length>)/VARBINARY(<length>) types will be trimmed or padded (only for CHAR(<length>)/BINARY(<length>)), so that their length will match the one defined by the length of their respective CHAR/VARCHAR/BINARY/VARBINARY column type."
}, {
    "caption" : "table.exec.sink.upsert-materialize",
    "value" : "table.exec.sink.upsert-materialize : AUTO",
    "meta" : "default: AUTO"
}, {
    "caption" : "table.exec.sink.keyed-shuffle",
    "value" : "table.exec.sink.keyed-shuffle : AUTO",
    "meta" : "default: AUTO"
}, {
    "caption" : "table.exec.sort.default-limit",
    "value" : "table.exec.sort.default-limit : -1",
    "meta" : "default: -1",
    "docHTML" : "Default limit when user don't set a limit after order by. -1 indicates that this configuration is ignored."
}, {
    "caption" : "table.exec.sort.max-num-file-handles",
    "value" : "table.exec.sort.max-num-file-handles : 128",
    "meta" : "default: 128",
    "docHTML" : "The maximal fan-in for external merge sort. It limits the number of file handles per operator. If it is too small, may cause intermediate merging. But if it is too large, it will cause too many files opened at the same time, consume memory and lead to random reading."
}, {
    "caption" : "table.exec.sort.async-merge-enabled",
    "value" : "table.exec.sort.async-merge-enabled : true",
    "meta" : "default: true",
    "docHTML" : "Whether to asynchronously merge sorted spill files."
}, {
    "caption" : "table.exec.spill-compression.enabled",
    "value" : "table.exec.spill-compression.enabled : true",
    "meta" : "default: true",
    "docHTML" : "Whether to compress spilled data. Currently we only support compress spilled data for sort and hash-agg and hash-join operators."
}, {
    "caption" : "table.exec.spill-compression.block-size",
    "value" : "table.exec.spill-compression.block-size : 64 kb",
    "meta" : "default: 64 kb",
    "docHTML" : "The memory size used to do compress when spilling data. The larger the memory, the higher the compression ratio, but more memory resource will be consumed by the job."
}, {
    "caption" : "table.exec.resource.default-parallelism",
    "value" : "table.exec.resource.default-parallelism : -1",
    "meta" : "default: -1",
    "docHTML" : "Sets default parallelism for all operators (such as aggregate, join, filter) to run with parallel instances. This config has a higher priority than parallelism of StreamExecutionEnvironment (actually, this config overrides the parallelism of StreamExecutionEnvironment). A value of -1 indicates that no default parallelism is set, then it will fallback to use the parallelism of StreamExecutionEnvironment."
}, {
    "caption" : "table.exec.resource.external-buffer-memory",
    "value" : "table.exec.resource.external-buffer-memory : 10 mb",
    "meta" : "default: 10 mb",
    "docHTML" : "Sets the external buffer memory size that is used in sort merge join and nested join and over window. Note: memory size is only a weight hint, it will affect the weight of memory that can be applied by a single operator in the task, the actual memory used depends on the running environment."
}, {
    "caption" : "table.exec.resource.hash-agg.memory",
    "value" : "table.exec.resource.hash-agg.memory : 128 mb",
    "meta" : "default: 128 mb",
    "docHTML" : "Sets the managed memory size of hash aggregate operator. Note: memory size is only a weight hint, it will affect the weight of memory that can be applied by a single operator in the task, the actual memory used depends on the running environment."
}, {
    "caption" : "table.exec.resource.hash-join.memory",
    "value" : "table.exec.resource.hash-join.memory : 128 mb",
    "meta" : "default: 128 mb",
    "docHTML" : "Sets the managed memory for hash join operator. It defines the lower limit. Note: memory size is only a weight hint, it will affect the weight of memory that can be applied by a single operator in the task, the actual memory used depends on the running environment."
}, {
    "caption" : "table.exec.resource.sort.memory",
    "value" : "table.exec.resource.sort.memory : 128 mb",
    "meta" : "default: 128 mb",
    "docHTML" : "Sets the managed buffer memory size for sort operator. Note: memory size is only a weight hint, it will affect the weight of memory that can be applied by a single operator in the task, the actual memory used depends on the running environment."
}, {
    "caption" : "table.exec.window-agg.buffer-size-limit",
    "value" : "table.exec.window-agg.buffer-size-limit : 100000",
    "meta" : "default: 100000",
    "docHTML" : "Sets the window elements buffer size limit used in group window agg operator."
}, {
    "caption" : "table.exec.async-lookup.buffer-capacity",
    "value" : "table.exec.async-lookup.buffer-capacity : 100",
    "meta" : "default: 100",
    "docHTML" : "The max number of async i/o operation that the async lookup join can trigger."
}, {
    "caption" : "table.exec.async-lookup.timeout",
    "value" : "table.exec.async-lookup.timeout : PT3M",
    "meta" : "default: PT3M",
    "docHTML" : "The async timeout for the asynchronous operation to complete."
}, {
    "caption" : "table.exec.async-lookup.output-mode",
    "value" : "table.exec.async-lookup.output-mode : ORDERED",
    "meta" : "default: ORDERED",
    "docHTML" : "Output mode for asynchronous operations which will convert to {@see AsyncDataStream.OutputMode}, ORDERED by default. If set to ALLOW_UNORDERED, will attempt to use {@see AsyncDataStream.OutputMode.UNORDERED} when it does not affect the correctness of the result, otherwise ORDERED will be still used."
}, {
    "caption" : "table.exec.mini-batch.enabled",
    "value" : "table.exec.mini-batch.enabled : false",
    "meta" : "default: false",
    "docHTML" : "Specifies whether to enable MiniBatch optimization. MiniBatch is an optimization to buffer input records to reduce state access. This is disabled by default. To enable this, users should set this config to true. NOTE: If mini-batch is enabled, 'table.exec.mini-batch.allow-latency' and 'table.exec.mini-batch.size' must be set."
}, {
    "caption" : "table.exec.mini-batch.allow-latency",
    "value" : "table.exec.mini-batch.allow-latency : PT0S",
    "meta" : "default: PT0S",
    "docHTML" : "The maximum latency can be used for MiniBatch to buffer input records. MiniBatch is an optimization to buffer input records to reduce state access. MiniBatch is triggered with the allowed latency interval and when the maximum number of buffered records reached. NOTE: If table.exec.mini-batch.enabled is set true, its value must be greater than zero."
}, {
    "caption" : "table.exec.mini-batch.size",
    "value" : "table.exec.mini-batch.size : -1",
    "meta" : "default: -1",
    "docHTML" : "The maximum number of input records can be buffered for MiniBatch. MiniBatch is an optimization to buffer input records to reduce state access. MiniBatch is triggered with the allowed latency interval and when the maximum number of buffered records reached. NOTE: MiniBatch only works for non-windowed aggregations currently. If table.exec.mini-batch.enabled is set true, its value must be positive."
}, {
    "caption" : "table.exec.disabled-operators",
    "docHTML" : "Mainly for testing. A comma-separated list of operator names, each name represents a kind of disabled operator.\nOperators that can be disabled include \"NestedLoopJoin\", \"ShuffleHashJoin\", \"BroadcastHashJoin\", \"SortMergeJoin\", \"HashAgg\", \"SortAgg\".\nBy default no operator is disabled."
}, {
    "caption" : "table.exec.shuffle-mode"
}, {
    "caption" : "table.exec.legacy-cast-behaviour",
    "value" : "table.exec.legacy-cast-behaviour : DISABLED",
    "meta" : "default: DISABLED",
    "docHTML" : "Determines whether CAST will operate following the legacy behaviour or the new one that introduces various fixes and improvements."
}, {
    "caption" : "table.exec.rank.topn-cache-size",
    "value" : "table.exec.rank.topn-cache-size : 10000",
    "meta" : "default: 10000",
    "docHTML" : "Rank operators have a cache which caches partial state contents to reduce state access. Cache size is the number of records in each ranking task."
}, {
    "caption" : "table.exec.simplify-operator-name-enabled",
    "value" : "table.exec.simplify-operator-name-enabled : true",
    "meta" : "default: true",
    "docHTML" : "When it is true, the optimizer will simplify the operator name with id and type of ExecNode and keep detail in description. Default value is true."
}, {
    "caption" : "table.exec.deduplicate.insert-update-after-sensitive-enabled",
    "value" : "table.exec.deduplicate.insert-update-after-sensitive-enabled : true",
    "meta" : "default: true",
    "docHTML" : "Set whether the job (especially the sinks) is sensitive to INSERT messages and UPDATE_AFTER messages. If false, Flink may, sometimes (e.g. deduplication for last row), send UPDATE_AFTER instead of INSERT for the first row. If true, Flink will guarantee to send INSERT for the first row, in that case there will be additional overhead. Default is true."
}, {
    "caption" : "table.exec.deduplicate.mini-batch.compact-changes-enabled",
    "value" : "table.exec.deduplicate.mini-batch.compact-changes-enabled : false",
    "meta" : "default: false",
    "docHTML" : "Set whether to compact the changes sent downstream in row-time mini-batch. If true, Flink will compact changes and send only the latest change downstream. Note that if the downstream needs the details of versioned data, this optimization cannot be applied. If false, Flink will send all changes to downstream just like when the mini-batch is not enabled."
}, {
    "caption" : "table.exec.legacy-transformation-uids",
    "value" : "table.exec.legacy-transformation-uids : false",
    "meta" : "default: false",
    "docHTML" : "This flag has been replaced by table.exec.uid.generation. Use the enum value DISABLED to restore legacy behavior. However, the new default value should be sufficient for most use cases as only pipelines from compiled plans get UIDs assigned."
}, {
    "caption" : "table.exec.uid.generation",
    "value" : "table.exec.uid.generation : PLAN_ONLY",
    "meta" : "default: PLAN_ONLY"
}, {
    "caption" : "table.exec.uid.format",
    "value" : "table.exec.uid.format : <id>_<transformation>",
    "meta" : "default: <id>_<transformation>",
    "docHTML" : "Defines the format pattern for generating the UID of an ExecNode streaming transformation. The pattern can be defined globally or per-ExecNode in the compiled plan. Supported arguments are: <id> (from static counter), <type> (e.g. 'stream-exec-sink'), <version>, and <transformation> (e.g. 'constraint-validator' for a sink). In Flink 1.15.x the pattern was wrongly defined as '<id>_<type>_<version>_<transformation>' which would prevent migrations in the future."
}, {
    "caption" : "table.builtin-catalog-name",
    "value" : "table.builtin-catalog-name : default_catalog",
    "meta" : "default: default_catalog",
    "docHTML" : "The name of the initial catalog to be created when instantiating a TableEnvironment."
}, {
    "caption" : "table.builtin-database-name",
    "value" : "table.builtin-database-name : default_database",
    "meta" : "default: default_database",
    "docHTML" : "The name of the default database in the initial catalog to be created when instantiating TableEnvironment."
}, {
    "caption" : "table.dml-sync",
    "value" : "table.dml-sync : false",
    "meta" : "default: false",
    "docHTML" : "Specifies if the DML job (i.e. the insert operation) is executed asynchronously or synchronously. By default, the execution is async, so you can submit multiple DML jobs at the same time. If set this option to true, the insert operation will wait for the job to finish."
}, {
    "caption" : "table.dynamic-table-options.enabled",
    "value" : "table.dynamic-table-options.enabled : true",
    "meta" : "default: true",
    "docHTML" : "Enable or disable the OPTIONS hint used to specify table options dynamically, if disabled, an exception would be thrown if any OPTIONS hint is specified"
}, {
    "caption" : "table.sql-dialect",
    "value" : "table.sql-dialect : default",
    "meta" : "default: default",
    "docHTML" : "The SQL dialect defines how to parse a SQL query. A different SQL dialect may support different SQL grammar. Currently supported dialects are: default and hive"
}, {
    "caption" : "table.local-time-zone",
    "value" : "table.local-time-zone : default",
    "meta" : "default: default",
    "docHTML" : "The local time zone defines current session time zone id. It is used when converting to/from <code>TIMESTAMP WITH LOCAL TIME ZONE</code>. Internally, timestamps with local time zone are always represented in the UTC time zone. However, when converting to data types that don't include a time zone (e.g. TIMESTAMP, TIME, or simply STRING), the session time zone is used during conversion. The input of option is either a full name such as \"America/Los_Angeles\", or a custom timezone id such as \"GMT-08:00\"."
}, {
    "caption" : "table.plan.compile.catalog-objects",
    "value" : "table.plan.compile.catalog-objects : ALL",
    "meta" : "default: ALL"
}, {
    "caption" : "table.plan.restore.catalog-objects",
    "value" : "table.plan.restore.catalog-objects : ALL",
    "meta" : "default: ALL",
    "docHTML" : "Strategy how to restore catalog objects such as tables, functions, or data types using a given plan and performing catalog lookups if necessary. It influences the need for catalog metadata to bepresent and enables partial enrichment of plan information."
}, {
    "caption" : "table.plan.force-recompile",
    "value" : "table.plan.force-recompile : false",
    "meta" : "default: false",
    "docHTML" : "When false COMPILE PLAN statement will fail if the output plan file is already existing, unless the clause IF NOT EXISTS is used. When true COMPILE PLAN will overwrite the existing output plan file. We strongly suggest to enable this flag only for debugging purpose."
}, {
    "caption" : "table.generated-code.max-length",
    "value" : "table.generated-code.max-length : 4000",
    "meta" : "default: 4000",
    "docHTML" : "Specifies a threshold where generated code will be split into sub-function calls. Java has a maximum method length of 64 KB. This setting allows for finer granularity if necessary. Default value is 4000 instead of 64KB as by default JIT refuses to work on methods with more than 8K byte code."
}, {
    "caption" : "table.generated-code.max-members",
    "value" : "table.generated-code.max-members : 10000",
    "meta" : "default: 10000",
    "docHTML" : "Specifies a threshold where class members of generated code will be grouped into arrays by types."
}, {
    "caption" : "table.resources.download-dir",
    "value" : "table.resources.download-dir : /var/folders/01/p115bpln55b2nxcg1q_lb_240000gn/T/",
    "meta" : "default: /var/folders/01/p115bpln55b2nxcg1q_lb_240000gn/T/",
    "docHTML" : "Local directory that is used by planner for storing downloaded resources."
}, {
    "caption" : "classloader.resolve-order",
    "value" : "classloader.resolve-order : child-first",
    "meta" : "default: child-first",
    "docHTML" : "Defines the class resolution strategy when loading classes from user code, meaning whether to first check the user code jar (\"child-first\") or the application classpath (\"parent-first\"). The default settings indicate to load classes first from the user code jar, which means that user code jars can include and load different dependencies than Flink uses (transitively)."
}, {
    "caption" : "classloader.parent-first-patterns.default",
    "value" : "classloader.parent-first-patterns.default : [java., scala., org.apache.flink., com.esotericsoftware.kryo, org.apache.hadoop., javax.annotation., org.xml, javax.xml, org.apache.xerces, org.w3c, org.rocksdb., org.slf4j, org.apache.log4j, org.apache.logging, org.apache.commons.logging, ch.qos.logback]",
    "meta" : "default: [java., scala., org.apache.flink., com.esotericsoftware.kryo, org.apache.hadoop., javax.annotation., org.xml, javax.xml, org.apache.xerces, org.w3c, org.rocksdb., org.slf4j, org.apache.log4j, org.apache.logging, org.apache.commons.logging, ch.qos.logback]",
    "docHTML" : "A (semicolon-separated) list of patterns that specifies which classes should always be resolved through the parent ClassLoader first. A pattern is a simple prefix that is checked against the fully qualified class name. This setting should generally not be modified. To add another pattern we recommend to use \"classloader.parent-first-patterns.additional\" instead."
}, {
    "caption" : "classloader.parent-first-patterns.additional",
    "value" : "classloader.parent-first-patterns.additional : []",
    "meta" : "default: []",
    "docHTML" : "A (semicolon-separated) list of patterns that specifies which classes should always be resolved through the parent ClassLoader first. A pattern is a simple prefix that is checked against the fully qualified class name. These patterns are appended to \"classloader.parent-first-patterns.default\"."
}, {
    "caption" : "classloader.fail-on-metaspace-oom-error",
    "value" : "classloader.fail-on-metaspace-oom-error : true",
    "meta" : "default: true",
    "docHTML" : "Fail Flink JVM processes if 'OutOfMemoryError: Metaspace' is thrown while trying to load a user code class."
}, {
    "caption" : "classloader.check-leaked-classloader",
    "value" : "classloader.check-leaked-classloader : true",
    "meta" : "default: true",
    "docHTML" : "Fails attempts at loading classes if the user classloader of a job is used after it has terminated.\nThis is usually caused by the classloader being leaked by lingering threads or misbehaving libraries, which may also result in the classloader being used by other jobs.\nThis check should only be disabled if such a leak prevents further jobs from running."
}, {
    "caption" : "plugin.classloader.parent-first-patterns.default",
    "value" : "plugin.classloader.parent-first-patterns.default : [java., org.apache.flink., javax.annotation., org.slf4j, org.apache.log4j, org.apache.logging, org.apache.commons.logging, ch.qos.logback]",
    "meta" : "default: [java., org.apache.flink., javax.annotation., org.slf4j, org.apache.log4j, org.apache.logging, org.apache.commons.logging, ch.qos.logback]",
    "docHTML" : "A (semicolon-separated) list of patterns that specifies which classes should always be resolved through the plugin parent ClassLoader first. A pattern is a simple prefix that is checked  against the fully qualified class name. This setting should generally not be modified. To add another  pattern we recommend to use \"plugin.classloader.parent-first-patterns.additional\" instead."
}, {
    "caption" : "plugin.classloader.parent-first-patterns.additional",
    "value" : "plugin.classloader.parent-first-patterns.additional : []",
    "meta" : "default: []",
    "docHTML" : "A (semicolon-separated) list of patterns that specifies which classes should always be resolved through the plugin parent ClassLoader first. A pattern is a simple prefix that is checked  against the fully qualified class name. These patterns are appended to \"plugin.classloader.parent-first-patterns.default\"."
}, {
    "caption" : "env.java.opts",
    "value" : "env.java.opts : ",
    "meta" : "default: ",
    "docHTML" : "Java options to start the JVM of all Flink processes with."
}, {
    "caption" : "env.java.opts.jobmanager",
    "value" : "env.java.opts.jobmanager : ",
    "meta" : "default: ",
    "docHTML" : "Java options to start the JVM of the JobManager with."
}, {
    "caption" : "env.java.opts.taskmanager",
    "value" : "env.java.opts.taskmanager : ",
    "meta" : "default: ",
    "docHTML" : "Java options to start the JVM of the TaskManager with."
}, {
    "caption" : "env.java.opts.historyserver",
    "value" : "env.java.opts.historyserver : ",
    "meta" : "default: ",
    "docHTML" : "Java options to start the JVM of the HistoryServer with."
}, {
    "caption" : "env.java.opts.client",
    "value" : "env.java.opts.client : ",
    "meta" : "default: ",
    "docHTML" : "Java options to start the JVM of the Flink Client with."
}, {
    "caption" : "env.log.dir",
    "docHTML" : "Defines the directory where the Flink logs are saved. It has to be an absolute path. (Defaults to the log directory under Flink’s home)"
}, {
    "caption" : "env.pid.dir",
    "value" : "env.pid.dir : /tmp",
    "meta" : "default: /tmp",
    "docHTML" : "Defines the directory where the flink-<host>-<process>.pid files are saved."
}, {
    "caption" : "env.log.max",
    "value" : "env.log.max : 5",
    "meta" : "default: 5",
    "docHTML" : "The maximum number of old log files to keep."
}, {
    "caption" : "env.ssh.opts",
    "docHTML" : "Additional command line options passed to SSH clients when starting or stopping JobManager, TaskManager, and Zookeeper services (start-cluster.sh, stop-cluster.sh, start-zookeeper-quorum.sh, stop-zookeeper-quorum.sh)."
}, {
    "caption" : "env.hadoop.conf.dir",
    "docHTML" : "Path to hadoop configuration directory. It is required to read HDFS and/or YARN configuration. You can also set it via environment variable."
}, {
    "caption" : "env.yarn.conf.dir",
    "docHTML" : "Path to yarn configuration directory. It is required to run flink on YARN. You can also set it via environment variable."
}, {
    "caption" : "env.hbase.conf.dir",
    "docHTML" : "Path to hbase configuration directory. It is required to read HBASE configuration. You can also set it via environment variable."
}, {
    "caption" : "io.tmp.dirs",
    "value" : "io.tmp.dirs : /var/folders/01/p115bpln55b2nxcg1q_lb_240000gn/T/",
    "meta" : "default: /var/folders/01/p115bpln55b2nxcg1q_lb_240000gn/T/",
    "docHTML" : "Directories for temporary files, separated by\",\", \"|\", or the system's java.io.File.pathSeparator."
}, {
    "caption" : "parallelism.default",
    "value" : "parallelism.default : 1",
    "meta" : "default: 1",
    "docHTML" : "Default parallelism for jobs."
}, {
    "caption" : "fs.default-scheme",
    "docHTML" : "The default filesystem scheme, used for paths that do not declare a scheme explicitly. May contain an authority, e.g. host:port in case of an HDFS NameNode."
}, {
    "caption" : "fs.allowed-fallback-filesystems",
    "value" : "fs.allowed-fallback-filesystems : ",
    "meta" : "default: ",
    "docHTML" : "A (semicolon-separated) list of file schemes, for which Hadoop can be used instead of an appropriate Flink plugin. (example: s3;wasb)"
}, {
    "caption" : "fs.overwrite-files",
    "value" : "fs.overwrite-files : false",
    "meta" : "default: false",
    "docHTML" : "Specifies whether file output writers should overwrite existing files by default. Set to \"true\" to overwrite by default,\"false\" otherwise."
}, {
    "caption" : "fs.output.always-create-directory",
    "value" : "fs.output.always-create-directory : false",
    "meta" : "default: false",
    "docHTML" : "File writers running with a parallelism larger than one create a directory for the output file path and put the different result files (one per parallel writer task) into that directory. If this option is set to \"true\", writers with a parallelism of 1 will also create a directory and place a single result file into it. If the option is set to \"false\", the writer will directly create the file directly at the output path, without creating a containing directory."
}, {
    "caption" : "rest.bind-address",
    "docHTML" : "The address that the server binds itself."
}, {
    "caption" : "rest.bind-port",
    "value" : "rest.bind-port : 8081",
    "meta" : "default: 8081",
    "docHTML" : "The port that the server binds itself. Accepts a list of ports (“50100,50101”), ranges (“50100-50200”) or a combination of both. It is recommended to set a range of ports to avoid collisions when multiple Rest servers are running on the same machine."
}, {
    "caption" : "rest.address",
    "docHTML" : "The address that should be used by clients to connect to the server. Attention: This option is respected only if the high-availability configuration is NONE."
}, {
    "caption" : "rest.port",
    "value" : "rest.port : 8081",
    "meta" : "default: 8081",
    "docHTML" : "The port that the client connects to. If %s has not been specified, then the REST server will bind to this port. Attention: This option is respected only if the high-availability configuration is NONE."
}, {
    "caption" : "rest.await-leader-timeout",
    "value" : "rest.await-leader-timeout : 30000",
    "meta" : "default: 30000",
    "docHTML" : "The time in ms that the client waits for the leader address, e.g., Dispatcher or WebMonitorEndpoint"
}, {
    "caption" : "rest.retry.max-attempts",
    "value" : "rest.retry.max-attempts : 20",
    "meta" : "default: 20",
    "docHTML" : "The number of retries the client will attempt if a retryable operations fails."
}, {
    "caption" : "rest.retry.delay",
    "value" : "rest.retry.delay : 3000",
    "meta" : "default: 3000",
    "docHTML" : "The time in ms that the client waits between retries (See also `rest.retry.max-attempts`)."
}, {
    "caption" : "rest.connection-timeout",
    "value" : "rest.connection-timeout : 15000",
    "meta" : "default: 15000",
    "docHTML" : "The maximum time in ms for the client to establish a TCP connection."
}, {
    "caption" : "rest.idleness-timeout",
    "value" : "rest.idleness-timeout : 300000",
    "meta" : "default: 300000",
    "docHTML" : "The maximum time in ms for a connection to stay idle before failing."
}, {
    "caption" : "rest.server.max-content-length",
    "value" : "rest.server.max-content-length : 104857600",
    "meta" : "default: 104857600",
    "docHTML" : "The maximum content length in bytes that the server will handle."
}, {
    "caption" : "rest.client.max-content-length",
    "value" : "rest.client.max-content-length : 104857600",
    "meta" : "default: 104857600",
    "docHTML" : "The maximum content length in bytes that the client will handle."
}, {
    "caption" : "rest.server.numThreads",
    "value" : "rest.server.numThreads : 4",
    "meta" : "default: 4",
    "docHTML" : "The number of threads for the asynchronous processing of requests."
}, {
    "caption" : "rest.server.thread-priority",
    "value" : "rest.server.thread-priority : 5",
    "meta" : "default: 5",
    "docHTML" : "Thread priority of the REST server's executor for processing asynchronous requests. Lowering the thread priority will give Flink's main components more CPU time whereas increasing will allocate more time for the REST server's processing."
}, {
    "caption" : "rest.flamegraph.enabled",
    "value" : "rest.flamegraph.enabled : false",
    "meta" : "default: false",
    "docHTML" : "Enables the experimental flame graph feature."
}, {
    "caption" : "rest.flamegraph.cleanup-interval",
    "value" : "rest.flamegraph.cleanup-interval : PT10M",
    "meta" : "default: PT10M",
    "docHTML" : "Time after which cached stats are cleaned up if not accessed. It can be specified using notation: \"100 s\", \"10 m\"."
}, {
    "caption" : "rest.flamegraph.refresh-interval",
    "value" : "rest.flamegraph.refresh-interval : PT1M",
    "meta" : "default: PT1M",
    "docHTML" : "Time after which available stats are deprecated and need to be refreshed (by resampling).  It can be specified using notation: \"30 s\", \"1 m\"."
}, {
    "caption" : "rest.flamegraph.num-samples",
    "value" : "rest.flamegraph.num-samples : 100",
    "meta" : "default: 100",
    "docHTML" : "Number of samples to take to build a FlameGraph."
}, {
    "caption" : "rest.flamegraph.delay-between-samples",
    "value" : "rest.flamegraph.delay-between-samples : PT0.05S",
    "meta" : "default: PT0.05S",
    "docHTML" : "Delay between individual stack trace samples taken for building a FlameGraph. It can be specified using notation: \"100 ms\", \"1 s\"."
}, {
    "caption" : "rest.flamegraph.stack-depth",
    "value" : "rest.flamegraph.stack-depth : 100",
    "meta" : "default: 100",
    "docHTML" : "Maximum depth of stack traces used to create FlameGraphs."
}, {
    "caption" : "rest.async.store-duration",
    "value" : "rest.async.store-duration : PT5M",
    "meta" : "default: PT5M",
    "docHTML" : "Maximum duration that the result of an async operation is stored. Once elapsed the result of the operation can no longer be retrieved."
}, {
    "caption" : "yarn.appmaster.vcores",
    "value" : "yarn.appmaster.vcores : 1",
    "meta" : "default: 1",
    "docHTML" : "The number of virtual cores (vcores) used by YARN application master."
}, {
    "caption" : "yarn.classpath.include-user-jar",
    "value" : "yarn.classpath.include-user-jar : ORDER",
    "meta" : "default: ORDER",
    "docHTML" : "Defines whether user-jars are included in the system class path as well as their positioning in the path."
}, {
    "caption" : "yarn.containers.vcores",
    "value" : "yarn.containers.vcores : -1",
    "meta" : "default: -1",
    "docHTML" : "The number of virtual cores (vcores) per YARN container. By default, the number of vcores is set to the number of slots per TaskManager, if set, or to 1, otherwise. In order for this parameter to be used your cluster must have CPU scheduling enabled. You can do this by setting the %s."
}, {
    "caption" : "yarn.application-attempts",
    "docHTML" : "Number of ApplicationMaster restarts. By default, the value will be set to 1. If high availability is enabled, then the default value will be 2. The restart number is also limited by YARN (configured via %s). Note that that the entire Flink cluster will restart and the YARN Client will lose the connection."
}, {
    "caption" : "yarn.application-attempt-failures-validity-interval",
    "value" : "yarn.application-attempt-failures-validity-interval : 10000",
    "meta" : "default: 10000",
    "docHTML" : "Time window in milliseconds which defines the number of application attempt failures when restarting the AM. Failures which fall outside of this window are not being considered. Set this value to -1 in order to count globally. See %s for more information."
}, {
    "caption" : "yarn.heartbeat.interval",
    "value" : "yarn.heartbeat.interval : 5",
    "meta" : "default: 5",
    "docHTML" : "Time between heartbeats with the ResourceManager in seconds."
}, {
    "caption" : "yarn.heartbeat.container-request-interval",
    "value" : "yarn.heartbeat.container-request-interval : 500",
    "meta" : "default: 500"
}, {
    "caption" : "yarn.properties-file.location",
    "docHTML" : "When a Flink job is submitted to YARN, the JobManager’s host and the number of available processing slots is written into a properties file, so that the Flink client is able to pick those details up. This configuration parameter allows changing the default location of that file (for example for environments sharing a Flink installation between users)."
}, {
    "caption" : "yarn.application-master.port",
    "value" : "yarn.application-master.port : 0",
    "meta" : "default: 0",
    "docHTML" : "With this configuration option, users can specify a port, a range of ports or a list of ports for the Application Master (and JobManager) RPC port. By default we recommend using the default value (0) to let the operating system choose an appropriate port. In particular when multiple AMs are running on the same physical host, fixed port assignments prevent the AM from starting. For example when running Flink on YARN on an environment with a restrictive firewall, this option allows specifying a range of allowed ports."
}, {
    "caption" : "yarn.application.priority",
    "value" : "yarn.application.priority : -1",
    "meta" : "default: -1",
    "docHTML" : "A non-negative integer indicating the priority for submitting a Flink YARN application. It will only take effect if YARN priority scheduling setting is enabled. Larger integer corresponds with higher priority. If priority is negative or set to '-1'(default), Flink will unset yarn priority setting and use cluster default priority. Please refer to YARN's official documentation for specific settings required to enable priority scheduling for the targeted YARN version."
}, {
    "caption" : "yarn.file-replication",
    "value" : "yarn.file-replication : -1",
    "meta" : "default: -1",
    "docHTML" : "Number of file replication of each local resource file. If it is not configured, Flink will use the default replication value in hadoop configuration."
}, {
    "caption" : "yarn.tags",
    "value" : "yarn.tags : ",
    "meta" : "default: ",
    "docHTML" : "A comma-separated list of tags to apply to the Flink YARN application."
}, {
    "caption" : "yarn.staging-directory",
    "docHTML" : "Staging directory used to store YARN files while submitting applications. Per default, it uses the home directory of the configured file system."
}, {
    "caption" : "yarn.ship-files",
    "docHTML" : "A semicolon-separated list of files and/or directories to be shipped to the YARN cluster."
}, {
    "caption" : "yarn.ship-archives",
    "docHTML" : "A semicolon-separated list of archives to be shipped to the YARN cluster. These archives will be un-packed when localizing and they can be any of the following types: \".tar.gz\", \".tar\", \".tgz\", \".dst\", \".jar\", \".zip\"."
}, {
    "caption" : "yarn.flink-dist-jar",
    "docHTML" : "The location of the Flink dist jar."
}, {
    "caption" : "yarn.application.id",
    "docHTML" : "The YARN application id of the running yarn cluster. This is the YARN cluster where the pipeline is going to be executed."
}, {
    "caption" : "yarn.application.queue",
    "docHTML" : "The YARN queue on which to put the current pipeline."
}, {
    "caption" : "yarn.application.name",
    "docHTML" : "A custom name for your YARN application."
}, {
    "caption" : "yarn.application.type",
    "docHTML" : "A custom type for your YARN application.."
}, {
    "caption" : "yarn.application.node-label",
    "docHTML" : "Specify YARN node label for the YARN application."
}, {
    "caption" : "yarn.taskmanager.node-label",
    "docHTML" : "Specify YARN node label for the Flink TaskManagers, it will override the yarn.application.node-label for TaskManagers if both are set."
}, {
    "caption" : "yarn.security.kerberos.ship-local-keytab",
    "value" : "yarn.security.kerberos.ship-local-keytab : true",
    "meta" : "default: true",
    "docHTML" : "When this is true Flink will ship the keytab file configured via security.kerberos.login.keytab as a localized YARN resource."
}, {
    "caption" : "yarn.security.kerberos.localized-keytab-path",
    "value" : "yarn.security.kerberos.localized-keytab-path : krb5.keytab",
    "meta" : "default: krb5.keytab",
    "docHTML" : "Local (on NodeManager) path where kerberos keytab file will be localized to. If yarn.security.kerberos.ship-local-keytab set to true, Flink willl ship the keytab file as a YARN local resource. In this case, the path is relative to the local resource directory. If set to false, Flink will try to directly locate the keytab from the path itself."
}, {
    "caption" : "yarn.provided.lib.dirs",
    "docHTML" : "A semicolon-separated list of provided lib directories. They should be pre-uploaded and world-readable. Flink will use them to exclude the local Flink jars(e.g. flink-dist, lib/, plugins/)uploading to accelerate the job submission process. Also YARN will cache them on the nodes so that they doesn't need to be downloaded every time for each application. An example could be hdfs://$namenode_address/path/of/flink/lib"
}, {
    "caption" : "yarn.provided.usrlib.dir",
    "docHTML" : "The provided usrlib directory in remote. It should be pre-uploaded and world-readable. Flink will use it to exclude the local usrlib directory(i.e. usrlib/ under the parent directory of FLINK_LIB_DIR). Unlike yarn.provided.lib.dirs, YARN will not cache it on the nodes as it is for each application. An example could be hdfs://$namenode_address/path/of/flink/usrlib"
}, {
    "caption" : "flink.hadoop.<key>",
    "docHTML" : "A general option to probe Hadoop configuration through prefix 'flink.hadoop.'. Flink will remove the prefix to get <key> (from %s and %s) then set the <key> and value to Hadoop configuration. For example, flink.hadoop.dfs.replication=5 in Flink configuration and convert to dfs.replication=5 in Hadoop configuration."
}, {
    "caption" : "flink.yarn.<key>",
    "docHTML" : "A general option to probe Yarn configuration through prefix 'flink.yarn.'. Flink will remove the prefix 'flink.' to get yarn.<key> (from %s) then set the yarn.<key> and value to Yarn configuration. For example, flink.yarn.resourcemanager.container.liveness-monitor.interval-ms=300000 in Flink configuration and convert to yarn.resourcemanager.container.liveness-monitor.interval-ms=300000 in Yarn configuration."
}, {
    "caption" : "external-resource.<resource_name>.yarn.config-key",
    "docHTML" : "If configured, Flink will add this key to the resource profile of container request to Yarn. The value will be set to the value of external-resource.<resource_name>.amount."
}, {
    "caption" : "kubernetes.context",
    "docHTML" : "The desired context from your Kubernetes config file used to configure the Kubernetes client for interacting with the cluster. This could be helpful if one has multiple contexts configured and wants to administrate different Flink clusters on different Kubernetes clusters/contexts."
}, {
    "caption" : "kubernetes.rest-service.exposed.type",
    "value" : "kubernetes.rest-service.exposed.type : ClusterIP",
    "meta" : "default: ClusterIP",
    "docHTML" : "The exposed type of the rest service. The exposed rest service could be used to access the Flink’s Web UI and REST endpoint."
}, {
    "caption" : "kubernetes.rest-service.exposed.node-port-address-type",
    "value" : "kubernetes.rest-service.exposed.node-port-address-type : InternalIP",
    "meta" : "default: InternalIP",
    "docHTML" : "The user-specified %s that is used for filtering node IPs when constructing a %s connection string. This option is only considered when '%s' is set to '%s'."
}, {
    "caption" : "kubernetes.jobmanager.service-account",
    "value" : "kubernetes.jobmanager.service-account : default",
    "meta" : "default: default",
    "docHTML" : "Service account that is used by jobmanager within kubernetes cluster. The job manager uses this service account when requesting taskmanager pods from the API server. If not explicitly configured, config option 'kubernetes.service-account' will be used."
}, {
    "caption" : "kubernetes.taskmanager.service-account",
    "value" : "kubernetes.taskmanager.service-account : default",
    "meta" : "default: default",
    "docHTML" : "Service account that is used by taskmanager within kubernetes cluster. The task manager uses this service account when watching config maps on the API server to retrieve leader address of jobmanager and resourcemanager. If not explicitly configured, config option 'kubernetes.service-account' will be used."
}, {
    "caption" : "kubernetes.service-account",
    "value" : "kubernetes.service-account : default",
    "meta" : "default: default",
    "docHTML" : "Service account that is used by jobmanager and taskmanager within kubernetes cluster. Notice that this can be overwritten by config options 'kubernetes.jobmanager.service-account' and 'kubernetes.taskmanager.service-account' for jobmanager and taskmanager respectively."
}, {
    "caption" : "kubernetes.jobmanager.owner.reference",
    "docHTML" : "The user-specified %s to be set to the JobManager Deployment. When all the owner resources are deleted, the JobManager Deployment will be deleted automatically, which also deletes all the resources created by this Flink cluster. The value should be formatted as a semicolon-separated list of owner references, where each owner reference is a comma-separated list of `key:value` pairs. E.g., apiVersion:v1,blockOwnerDeletion:true,controller:true,kind:FlinkApplication,name:flink-app-name,uid:flink-app-uid;apiVersion:v1,kind:Deployment,name:deploy-name,uid:deploy-uid"
}, {
    "caption" : "kubernetes.jobmanager.cpu",
    "value" : "kubernetes.jobmanager.cpu : 1.0",
    "meta" : "default: 1.0",
    "docHTML" : "The number of cpu used by job manager"
}, {
    "caption" : "kubernetes.jobmanager.cpu.limit-factor",
    "value" : "kubernetes.jobmanager.cpu.limit-factor : 1.0",
    "meta" : "default: 1.0",
    "docHTML" : "The limit factor of cpu used by job manager. The resources limit cpu will be set to cpu * limit-factor."
}, {
    "caption" : "kubernetes.jobmanager.memory.limit-factor",
    "value" : "kubernetes.jobmanager.memory.limit-factor : 1.0",
    "meta" : "default: 1.0",
    "docHTML" : "The limit factor of memory used by job manager. The resources limit memory will be set to memory * limit-factor."
}, {
    "caption" : "kubernetes.taskmanager.cpu",
    "value" : "kubernetes.taskmanager.cpu : -1.0",
    "meta" : "default: -1.0",
    "docHTML" : "The number of cpu used by task manager. By default, the cpu is set to the number of slots per TaskManager"
}, {
    "caption" : "kubernetes.taskmanager.cpu.limit-factor",
    "value" : "kubernetes.taskmanager.cpu.limit-factor : 1.0",
    "meta" : "default: 1.0",
    "docHTML" : "The limit factor of cpu used by task manager. The resources limit cpu will be set to cpu * limit-factor."
}, {
    "caption" : "kubernetes.taskmanager.memory.limit-factor",
    "value" : "kubernetes.taskmanager.memory.limit-factor : 1.0",
    "meta" : "default: 1.0",
    "docHTML" : "The limit factor of memory used by task manager. The resources limit memory will be set to memory * limit-factor."
}, {
    "caption" : "kubernetes.container.image.pull-policy",
    "value" : "kubernetes.container.image.pull-policy : IfNotPresent",
    "meta" : "default: IfNotPresent",
    "docHTML" : "The Kubernetes container image pull policy. The default policy is IfNotPresent to avoid putting pressure to image repository."
}, {
    "caption" : "kubernetes.container.image.pull-secrets",
    "docHTML" : "A semicolon-separated list of the Kubernetes secrets used to access private image registries."
}, {
    "caption" : "kubernetes.config.file",
    "docHTML" : "The kubernetes config file will be used to create the client. The default is located at ~/.kube/config"
}, {
    "caption" : "kubernetes.namespace",
    "value" : "kubernetes.namespace : default",
    "meta" : "default: default",
    "docHTML" : "The namespace that will be used for running the jobmanager and taskmanager pods."
}, {
    "caption" : "kubernetes.jobmanager.labels",
    "docHTML" : "The labels to be set for JobManager pod. Specified as key:value pairs separated by commas. For example, version:alphav1,deploy:test."
}, {
    "caption" : "kubernetes.taskmanager.labels",
    "docHTML" : "The labels to be set for TaskManager pods. Specified as key:value pairs separated by commas. For example, version:alphav1,deploy:test."
}, {
    "caption" : "kubernetes.jobmanager.node-selector",
    "docHTML" : "The node selector to be set for JobManager pod. Specified as key:value pairs separated by commas. For example, environment:production,disk:ssd."
}, {
    "caption" : "kubernetes.taskmanager.node-selector",
    "docHTML" : "The node selector to be set for TaskManager pods. Specified as key:value pairs separated by commas. For example, environment:production,disk:ssd."
}, {
    "caption" : "kubernetes.cluster-id",
    "docHTML" : "The cluster-id, which should be no more than 45 characters, is used for identifying a unique Flink cluster. The id must only contain lowercase alphanumeric characters and \"-\". The required format is %s. If not set, the client will automatically generate it with a random ID."
}, {
    "caption" : "kubernetes.container.image",
    "value" : "kubernetes.container.image : apache/flink:1.16.0-scala_2.12",
    "meta" : "default: apache/flink:1.16.0-scala_2.12",
    "docHTML" : "Image to use for Flink containers. The specified image must be based upon the same Apache Flink and Scala versions as used by the application. Visit %s for the official docker images provided by the Flink project. The Flink project also publishes docker images to %s."
}, {
    "caption" : "kubernetes.entry.path",
    "value" : "kubernetes.entry.path : /docker-entrypoint.sh",
    "meta" : "default: /docker-entrypoint.sh",
    "docHTML" : "The entrypoint script of kubernetes in the image. It will be used as command for jobmanager and taskmanager container."
}, {
    "caption" : "kubernetes.flink.conf.dir",
    "value" : "kubernetes.flink.conf.dir : /opt/flink/conf",
    "meta" : "default: /opt/flink/conf",
    "docHTML" : "The flink conf directory that will be mounted in pod. The flink-conf.yaml, log4j.properties, logback.xml in this path will be overwritten from config map."
}, {
    "caption" : "kubernetes.flink.log.dir",
    "docHTML" : "The directory that logs of jobmanager and taskmanager be saved in the pod. The default value is $FLINK_HOME/log."
}, {
    "caption" : "kubernetes.hadoop.conf.config-map.name",
    "docHTML" : "Specify the name of an existing ConfigMap that contains custom Hadoop configuration to be mounted on the JobManager(s) and TaskManagers."
}, {
    "caption" : "kubernetes.jobmanager.annotations",
    "docHTML" : "The user-specified annotations that are set to the JobManager pod. The value could be in the form of a1:v1,a2:v2"
}, {
    "caption" : "kubernetes.taskmanager.annotations",
    "docHTML" : "The user-specified annotations that are set to the TaskManager pod. The value could be in the form of a1:v1,a2:v2"
}, {
    "caption" : "kubernetes.jobmanager.entrypoint.args",
    "value" : "kubernetes.jobmanager.entrypoint.args : ",
    "meta" : "default: ",
    "docHTML" : "Extra arguments used when starting the job manager."
}, {
    "caption" : "kubernetes.taskmanager.entrypoint.args",
    "value" : "kubernetes.taskmanager.entrypoint.args : ",
    "meta" : "default: ",
    "docHTML" : "Extra arguments used when starting the task manager."
}, {
    "caption" : "kubernetes.jobmanager.tolerations",
    "docHTML" : "The user-specified tolerations to be set to the JobManager pod. The value should be in the form of key:key1,operator:Equal,value:value1,effect:NoSchedule;key:key2,operator:Exists,effect:NoExecute,tolerationSeconds:6000"
}, {
    "caption" : "kubernetes.taskmanager.tolerations",
    "docHTML" : "The user-specified tolerations to be set to the TaskManager pod. The value should be in the form of key:key1,operator:Equal,value:value1,effect:NoSchedule;key:key2,operator:Exists,effect:NoExecute,tolerationSeconds:6000"
}, {
    "caption" : "kubernetes.rest-service.annotations",
    "docHTML" : "The user-specified annotations that are set to the rest Service. The value should be in the form of a1:v1,a2:v2"
}, {
    "caption" : "kubernetes.secrets",
    "docHTML" : "The user-specified secrets that will be mounted into Flink container. The value should be in the form of %s."
}, {
    "caption" : "kubernetes.env.secretKeyRef",
    "docHTML" : "The user-specified secrets to set env variables in Flink container. The value should be in the form of %s."
}, {
    "caption" : "external-resource.<resource_name>.kubernetes.config-key",
    "docHTML" : "If configured, Flink will add \"resources.limits.<config-key>\" and \"resources.requests.<config-key>\" to the main container of TaskExecutor and set the value to the value of external-resource.<resource_name>.amount."
}, {
    "caption" : "kubernetes.transactional-operation.max-retries",
    "value" : "kubernetes.transactional-operation.max-retries : 5",
    "meta" : "default: 5",
    "docHTML" : "Defines the number of Kubernetes transactional operation retries before the client gives up. For example, %s."
}, {
    "caption" : "kubernetes.pod-template-file.jobmanager",
    "docHTML" : "Specify a local file that contains the jobmanager pod template definition. It will be used to initialize the jobmanager pod. The main container should be defined with name 'flink-main-container'. If not explicitly configured, config option 'kubernetes.pod-template-file' will be used."
}, {
    "caption" : "kubernetes.pod-template-file.taskmanager",
    "docHTML" : "Specify a local file that contains the taskmanager pod template definition. It will be used to initialize the taskmanager pod. The main container should be defined with name 'flink-main-container'. If not explicitly configured, config option 'kubernetes.pod-template-file' will be used."
}, {
    "caption" : "kubernetes.pod-template-file",
    "docHTML" : "Specify a local file that contains the pod template definition. It will be used to initialize the jobmanager and taskmanager pod. The main container should be defined with name 'flink-main-container'. Notice that this can be overwritten by config options 'kubernetes.pod-template-file.jobmanager' and 'kubernetes.pod-template-file.taskmanager' for jobmanager and taskmanager respectively."
}, {
    "caption" : "kubernetes.client.io-pool.size",
    "value" : "kubernetes.client.io-pool.size : 4",
    "meta" : "default: 4",
    "docHTML" : "The size of the IO executor pool used by the Kubernetes client to execute blocking IO operations (e.g. start/stop TaskManager pods, update leader related ConfigMaps, etc.). Increasing the pool size allows to run more IO operations concurrently."
}, {
    "caption" : "kubernetes.jobmanager.replicas",
    "value" : "kubernetes.jobmanager.replicas : 1",
    "meta" : "default: 1",
    "docHTML" : "Specify how many JobManager pods will be started simultaneously. Configure the value to greater than 1 to start standby JobManagers. It will help to achieve faster recovery. Notice that high availability should be enabled when starting standby JobManagers."
}, {
    "caption" : "kubernetes.hostnetwork.enabled",
    "value" : "kubernetes.hostnetwork.enabled : false",
    "meta" : "default: false",
    "docHTML" : "Whether to enable HostNetwork mode. The HostNetwork allows the pod could use the node network namespace instead of the individual pod network namespace. Please note that the JobManager service account should have the permission to update Kubernetes service."
}, {
    "caption" : "kubernetes.client.user-agent",
    "value" : "kubernetes.client.user-agent : flink",
    "meta" : "default: flink",
    "docHTML" : "The user agent to be used for contacting with Kubernetes APIServer."
}, {
    "caption" : "kubernetes.node-name-label",
    "value" : "kubernetes.node-name-label : kubernetes.io/hostname",
    "meta" : "default: kubernetes.io/hostname",
    "docHTML" : "The node label whose value is the same as the node name. Currently, this will only be used to set the node affinity of TM pods to avoid being scheduled on blocked nodes."
}, {
    "caption" : "restart-strategy"
}, {
    "caption" : "restart-strategy.fixed-delay.attempts",
    "value" : "restart-strategy.fixed-delay.attempts : 1",
    "meta" : "default: 1",
    "docHTML" : "The number of times that Flink retries the execution before the job is declared as failed if %s has been set to %s."
}, {
    "caption" : "restart-strategy.fixed-delay.delay",
    "value" : "restart-strategy.fixed-delay.delay : PT1S",
    "meta" : "default: PT1S",
    "docHTML" : "Delay between two consecutive restart attempts if %s has been set to %s. Delaying the retries can be helpful when the program interacts with external systems where for example connections or pending transactions should reach a timeout before re-execution is attempted. It can be specified using notation: \"1 min\", \"20 s\""
}, {
    "caption" : "restart-strategy.failure-rate.max-failures-per-interval",
    "value" : "restart-strategy.failure-rate.max-failures-per-interval : 1",
    "meta" : "default: 1",
    "docHTML" : "Maximum number of restarts in given time interval before failing a job if %s has been set to %s."
}, {
    "caption" : "restart-strategy.failure-rate.failure-rate-interval",
    "value" : "restart-strategy.failure-rate.failure-rate-interval : PT1M",
    "meta" : "default: PT1M",
    "docHTML" : "Time interval for measuring failure rate if %s has been set to %s. It can be specified using notation: \"1 min\", \"20 s\""
}, {
    "caption" : "restart-strategy.failure-rate.delay",
    "value" : "restart-strategy.failure-rate.delay : PT1S",
    "meta" : "default: PT1S",
    "docHTML" : "Delay between two consecutive restart attempts if %s has been set to %s. It can be specified using notation: \"1 min\", \"20 s\""
}, {
    "caption" : "restart-strategy.exponential-delay.initial-backoff",
    "value" : "restart-strategy.exponential-delay.initial-backoff : PT1S",
    "meta" : "default: PT1S",
    "docHTML" : "Starting duration between restarts if %s has been set to %s. It can be specified using notation: \"1 min\", \"20 s\""
}, {
    "caption" : "restart-strategy.exponential-delay.max-backoff",
    "value" : "restart-strategy.exponential-delay.max-backoff : PT5M",
    "meta" : "default: PT5M",
    "docHTML" : "The highest possible duration between restarts if %s has been set to %s. It can be specified using notation: \"1 min\", \"20 s\""
}, {
    "caption" : "restart-strategy.exponential-delay.backoff-multiplier",
    "value" : "restart-strategy.exponential-delay.backoff-multiplier : 2.0",
    "meta" : "default: 2.0",
    "docHTML" : "Backoff value is multiplied by this value after every failure,until max backoff is reached if %s has been set to %s."
}, {
    "caption" : "restart-strategy.exponential-delay.reset-backoff-threshold",
    "value" : "restart-strategy.exponential-delay.reset-backoff-threshold : PT1H",
    "meta" : "default: PT1H",
    "docHTML" : "Threshold when the backoff is reset to its initial value if %s has been set to %s. It specifies how long the job must be running without failure to reset the exponentially increasing backoff to its initial value. It can be specified using notation: \"1 min\", \"20 s\""
}, {
    "caption" : "restart-strategy.exponential-delay.jitter-factor",
    "value" : "restart-strategy.exponential-delay.jitter-factor : 0.1",
    "meta" : "default: 0.1",
    "docHTML" : "Jitter specified as a portion of the backoff if %s has been set to %s. It represents how large random value will be added or subtracted to the backoff. Useful when you want to avoid restarting multiple jobs at the same time."
}, {
    "caption" : "cleanup-strategy",
    "value" : "cleanup-strategy : exponential-delay",
    "meta" : "default: exponential-delay"
}, {
    "caption" : "cleanup-strategy.fixed-delay.attempts",
    "value" : "cleanup-strategy.fixed-delay.attempts : 2147483647",
    "meta" : "default: 2147483647",
    "docHTML" : "The number of times that Flink retries the cleanup before giving up if %s has been set to %s. Reaching the configured limit means that the job artifacts (and the job's JobResultStore entry) might need to be cleaned up manually."
}, {
    "caption" : "cleanup-strategy.fixed-delay.delay",
    "value" : "cleanup-strategy.fixed-delay.delay : PT1M",
    "meta" : "default: PT1M",
    "docHTML" : "Amount of time that Flink waits before re-triggering the cleanup after a failed attempt if the %s is set to %s. It can be specified using the following notation: \"1 min\", \"20 s\""
}, {
    "caption" : "cleanup-strategy.exponential-delay.initial-backoff",
    "value" : "cleanup-strategy.exponential-delay.initial-backoff : PT1S",
    "meta" : "default: PT1S",
    "docHTML" : "Starting duration between cleanup retries if %s has been set to %s. It can be specified using the following notation: \"1 min\", \"20 s\""
}, {
    "caption" : "cleanup-strategy.exponential-delay.max-backoff",
    "value" : "cleanup-strategy.exponential-delay.max-backoff : PT1H",
    "meta" : "default: PT1H",
    "docHTML" : "The highest possible duration between cleanup retries if %s has been set to %s. It can be specified using the following notation: \"1 min\", \"20 s\""
}, {
    "caption" : "cleanup-strategy.exponential-delay.attempts",
    "value" : "cleanup-strategy.exponential-delay.attempts : 2147483647",
    "meta" : "default: 2147483647",
    "docHTML" : "The number of times a failed cleanup is retried if %s has been set to %s. Reaching the configured limit means that the job artifacts (and the job's JobResultStore entry) might need to be cleaned up manually."
}, {
    "caption" : "state.backend"
}, {
    "caption" : "state.checkpoint-storage"
}, {
    "caption" : "state.checkpoints.num-retained",
    "value" : "state.checkpoints.num-retained : 1",
    "meta" : "default: 1",
    "docHTML" : "The maximum number of completed checkpoints to retain."
}, {
    "caption" : "state.backend.async",
    "value" : "state.backend.async : true",
    "meta" : "default: true",
    "docHTML" : "Deprecated option. All state snapshots are asynchronous."
}, {
    "caption" : "state.backend.incremental",
    "value" : "state.backend.incremental : false",
    "meta" : "default: false",
    "docHTML" : "Option whether the state backend should create incremental checkpoints, if possible. For an incremental checkpoint, only a diff from the previous checkpoint is stored, rather than the complete checkpoint state. Once enabled, the state size shown in web UI or fetched from rest API only represents the delta checkpoint size instead of full checkpoint size. Some state backends may not support incremental checkpoints and ignore this option."
}, {
    "caption" : "state.backend.local-recovery",
    "value" : "state.backend.local-recovery : false",
    "meta" : "default: false",
    "docHTML" : "This option configures local recovery for this state backend. By default, local recovery is deactivated. Local recovery currently only covers keyed state backends (including both the EmbeddedRocksDBStateBackend and the HashMapStateBackend)."
}, {
    "caption" : "taskmanager.state.local.root-dirs",
    "docHTML" : "The config parameter defining the root directories for storing file-based state for local recovery. Local recovery currently only covers keyed state backends. If not configured it will default to <WORKING_DIR>/localState. The <WORKING_DIR> can be configured via %s"
}, {
    "caption" : "state.savepoints.dir",
    "docHTML" : "The default directory for savepoints. Used by the state backends that write savepoints to file systems (HashMapStateBackend, EmbeddedRocksDBStateBackend)."
}, {
    "caption" : "state.checkpoints.dir",
    "docHTML" : "The default directory used for storing the data files and meta data of checkpoints in a Flink supported filesystem. The storage path must be accessible from all participating processes/nodes(i.e. all TaskManagers and JobManagers)."
}, {
    "caption" : "state.storage.fs.memory-threshold",
    "value" : "state.storage.fs.memory-threshold : 20 kb",
    "meta" : "default: 20 kb",
    "docHTML" : "The minimum size of state data files. All state chunks smaller than that are stored inline in the root checkpoint metadata file. The max memory threshold for this configuration is 1MB."
}, {
    "caption" : "state.storage.fs.write-buffer-size",
    "value" : "state.storage.fs.write-buffer-size : 4096",
    "meta" : "default: 4096",
    "docHTML" : "The default size of the write buffer for the checkpoint streams that write to file systems. The actual write buffer size is determined to be the maximum of the value of this option and option 'state.storage.fs.memory-threshold'."
}, {
    "caption" : "high-availability",
    "value" : "high-availability : NONE",
    "meta" : "default: NONE",
    "docHTML" : "Defines high-availability mode used for cluster execution. To enable high-availability, set this mode to \"ZOOKEEPER\", \"KUBERNETES\", or specify the fully qualified name of the factory class."
}, {
    "caption" : "high-availability.cluster-id",
    "value" : "high-availability.cluster-id : /default",
    "meta" : "default: /default",
    "docHTML" : "The ID of the Flink cluster, used to separate multiple Flink clusters from each other. Needs to be set for standalone clusters but is automatically inferred in YARN."
}, {
    "caption" : "high-availability.storageDir",
    "docHTML" : "File system path (URI) where Flink persists metadata in high-availability setups."
}, {
    "caption" : "high-availability.jobmanager.port",
    "value" : "high-availability.jobmanager.port : 0",
    "meta" : "default: 0",
    "docHTML" : "The port (range) used by the Flink Master for its RPC connections in highly-available setups. In highly-available setups, this value is used instead of 'jobmanager.rpc.port'.A value of '0' means that a random free port is chosen. TaskManagers discover this port through the high-availability services (leader election), so a random port or a port range works without requiring any additional means of service discovery."
}, {
    "caption" : "high-availability.zookeeper.quorum",
    "docHTML" : "The ZooKeeper quorum to use, when running Flink in a high-availability mode with ZooKeeper."
}, {
    "caption" : "high-availability.zookeeper.path.root",
    "value" : "high-availability.zookeeper.path.root : /flink",
    "meta" : "default: /flink",
    "docHTML" : "The root path under which Flink stores its entries in ZooKeeper."
}, {
    "caption" : "high-availability.zookeeper.path.jobgraphs",
    "value" : "high-availability.zookeeper.path.jobgraphs : /jobgraphs",
    "meta" : "default: /jobgraphs",
    "docHTML" : "ZooKeeper root path (ZNode) for job graphs"
}, {
    "caption" : "high-availability.zookeeper.client.session-timeout",
    "value" : "high-availability.zookeeper.client.session-timeout : 60000",
    "meta" : "default: 60000",
    "docHTML" : "Defines the session timeout for the ZooKeeper session in ms."
}, {
    "caption" : "high-availability.zookeeper.client.connection-timeout",
    "value" : "high-availability.zookeeper.client.connection-timeout : 15000",
    "meta" : "default: 15000",
    "docHTML" : "Defines the connection timeout for ZooKeeper in ms."
}, {
    "caption" : "high-availability.zookeeper.client.retry-wait",
    "value" : "high-availability.zookeeper.client.retry-wait : 5000",
    "meta" : "default: 5000",
    "docHTML" : "Defines the pause between consecutive retries in ms."
}, {
    "caption" : "high-availability.zookeeper.client.max-retry-attempts",
    "value" : "high-availability.zookeeper.client.max-retry-attempts : 3",
    "meta" : "default: 3",
    "docHTML" : "Defines the number of connection retries before the client gives up."
}, {
    "caption" : "high-availability.zookeeper.path.running-registry",
    "value" : "high-availability.zookeeper.path.running-registry : /running_job_registry/",
    "meta" : "default: /running_job_registry/",
    "docHTML" : ""
}, {
    "caption" : "high-availability.zookeeper.client.acl",
    "value" : "high-availability.zookeeper.client.acl : open",
    "meta" : "default: open",
    "docHTML" : "Defines the ACL (open|creator) to be configured on ZK node. The configuration value can be set to “creator” if the ZooKeeper server configuration has the “authProvider” property mapped to use SASLAuthenticationProvider and the cluster is configured to run in secure mode (Kerberos)."
}, {
    "caption" : "high-availability.zookeeper.client.tolerate-suspended-connections",
    "value" : "high-availability.zookeeper.client.tolerate-suspended-connections : false",
    "meta" : "default: false",
    "docHTML" : "Defines whether a suspended ZooKeeper connection will be treated as an error that causes the leader information to be invalidated or not. In case you set this option to %s, Flink will wait until a ZooKeeper connection is marked as lost before it revokes the leadership of components. This has the effect that Flink is more resilient against temporary connection instabilities at the cost of running more likely into timing issues with ZooKeeper."
}, {
    "caption" : "high-availability.job.delay",
    "docHTML" : "The time before a JobManager after a fail over recovers the current jobs."
}, {
    "caption" : "state.backend.rocksdb.metrics.num-immutable-mem-table",
    "value" : "state.backend.rocksdb.metrics.num-immutable-mem-table : false",
    "meta" : "default: false",
    "docHTML" : "Monitor the number of immutable memtables in RocksDB."
}, {
    "caption" : "state.backend.rocksdb.metrics.mem-table-flush-pending",
    "value" : "state.backend.rocksdb.metrics.mem-table-flush-pending : false",
    "meta" : "default: false",
    "docHTML" : "Monitor the number of pending memtable flushes in RocksDB."
}, {
    "caption" : "state.backend.rocksdb.metrics.compaction-pending",
    "value" : "state.backend.rocksdb.metrics.compaction-pending : false",
    "meta" : "default: false",
    "docHTML" : "Track pending compactions in RocksDB. Returns 1 if a compaction is pending, 0 otherwise."
}, {
    "caption" : "state.backend.rocksdb.metrics.background-errors",
    "value" : "state.backend.rocksdb.metrics.background-errors : false",
    "meta" : "default: false",
    "docHTML" : "Monitor the number of background errors in RocksDB."
}, {
    "caption" : "state.backend.rocksdb.metrics.cur-size-active-mem-table",
    "value" : "state.backend.rocksdb.metrics.cur-size-active-mem-table : false",
    "meta" : "default: false",
    "docHTML" : "Monitor the approximate size of the active memtable in bytes."
}, {
    "caption" : "state.backend.rocksdb.metrics.cur-size-all-mem-tables",
    "value" : "state.backend.rocksdb.metrics.cur-size-all-mem-tables : false",
    "meta" : "default: false",
    "docHTML" : "Monitor the approximate size of the active and unflushed immutable memtables in bytes."
}, {
    "caption" : "state.backend.rocksdb.metrics.size-all-mem-tables",
    "value" : "state.backend.rocksdb.metrics.size-all-mem-tables : false",
    "meta" : "default: false",
    "docHTML" : "Monitor the approximate size of the active, unflushed immutable, and pinned immutable memtables in bytes."
}, {
    "caption" : "state.backend.rocksdb.metrics.num-entries-active-mem-table",
    "value" : "state.backend.rocksdb.metrics.num-entries-active-mem-table : false",
    "meta" : "default: false",
    "docHTML" : "Monitor the total number of entries in the active memtable."
}, {
    "caption" : "state.backend.rocksdb.metrics.num-entries-imm-mem-tables",
    "value" : "state.backend.rocksdb.metrics.num-entries-imm-mem-tables : false",
    "meta" : "default: false",
    "docHTML" : "Monitor the total number of entries in the unflushed immutable memtables."
}, {
    "caption" : "state.backend.rocksdb.metrics.num-deletes-active-mem-table",
    "value" : "state.backend.rocksdb.metrics.num-deletes-active-mem-table : false",
    "meta" : "default: false",
    "docHTML" : "Monitor the total number of delete entries in the active memtable."
}, {
    "caption" : "state.backend.rocksdb.metrics.num-deletes-imm-mem-tables",
    "value" : "state.backend.rocksdb.metrics.num-deletes-imm-mem-tables : false",
    "meta" : "default: false",
    "docHTML" : "Monitor the total number of delete entries in the unflushed immutable memtables."
}, {
    "caption" : "state.backend.rocksdb.metrics.estimate-num-keys",
    "value" : "state.backend.rocksdb.metrics.estimate-num-keys : false",
    "meta" : "default: false",
    "docHTML" : "Estimate the number of keys in RocksDB."
}, {
    "caption" : "state.backend.rocksdb.metrics.estimate-table-readers-mem",
    "value" : "state.backend.rocksdb.metrics.estimate-table-readers-mem : false",
    "meta" : "default: false",
    "docHTML" : "Estimate the memory used for reading SST tables, excluding memory used in block cache (e.g.,filter and index blocks) in bytes."
}, {
    "caption" : "state.backend.rocksdb.metrics.num-snapshots",
    "value" : "state.backend.rocksdb.metrics.num-snapshots : false",
    "meta" : "default: false",
    "docHTML" : "Monitor the number of unreleased snapshots of the database."
}, {
    "caption" : "state.backend.rocksdb.metrics.num-live-versions",
    "value" : "state.backend.rocksdb.metrics.num-live-versions : false",
    "meta" : "default: false",
    "docHTML" : "Monitor number of live versions. Version is an internal data structure. See RocksDB file version_set.h for details. More live versions often mean more SST files are held from being deleted, by iterators or unfinished compactions."
}, {
    "caption" : "state.backend.rocksdb.metrics.estimate-live-data-size",
    "value" : "state.backend.rocksdb.metrics.estimate-live-data-size : false",
    "meta" : "default: false",
    "docHTML" : "Estimate of the amount of live data in bytes (usually smaller than sst files size due to space amplification)."
}, {
    "caption" : "state.backend.rocksdb.metrics.total-sst-files-size",
    "value" : "state.backend.rocksdb.metrics.total-sst-files-size : false",
    "meta" : "default: false",
    "docHTML" : "Monitor the total size (bytes) of all SST files of all versions.WARNING: may slow down online queries if there are too many files."
}, {
    "caption" : "state.backend.rocksdb.metrics.live-sst-files-size",
    "value" : "state.backend.rocksdb.metrics.live-sst-files-size : false",
    "meta" : "default: false",
    "docHTML" : "Monitor the total size (bytes) of all SST files belonging to the latest version.WARNING: may slow down online queries if there are too many files."
}, {
    "caption" : "state.backend.rocksdb.metrics.estimate-pending-compaction-bytes",
    "value" : "state.backend.rocksdb.metrics.estimate-pending-compaction-bytes : false",
    "meta" : "default: false",
    "docHTML" : "Estimated total number of bytes compaction needs to rewrite to get all levels down to under target size. Not valid for other compactions than level-based."
}, {
    "caption" : "state.backend.rocksdb.metrics.num-running-compactions",
    "value" : "state.backend.rocksdb.metrics.num-running-compactions : false",
    "meta" : "default: false",
    "docHTML" : "Monitor the number of currently running compactions."
}, {
    "caption" : "state.backend.rocksdb.metrics.num-running-flushes",
    "value" : "state.backend.rocksdb.metrics.num-running-flushes : false",
    "meta" : "default: false",
    "docHTML" : "Monitor the number of currently running flushes."
}, {
    "caption" : "state.backend.rocksdb.metrics.actual-delayed-write-rate",
    "value" : "state.backend.rocksdb.metrics.actual-delayed-write-rate : false",
    "meta" : "default: false",
    "docHTML" : "Monitor the current actual delayed write rate. 0 means no delay."
}, {
    "caption" : "state.backend.rocksdb.metrics.is-write-stopped",
    "value" : "state.backend.rocksdb.metrics.is-write-stopped : false",
    "meta" : "default: false",
    "docHTML" : "Track whether write has been stopped in RocksDB. Returns 1 if write has been stopped, 0 otherwise."
}, {
    "caption" : "state.backend.rocksdb.metrics.block-cache-capacity",
    "value" : "state.backend.rocksdb.metrics.block-cache-capacity : false",
    "meta" : "default: false",
    "docHTML" : "Monitor block cache capacity."
}, {
    "caption" : "state.backend.rocksdb.metrics.block-cache-usage",
    "value" : "state.backend.rocksdb.metrics.block-cache-usage : false",
    "meta" : "default: false",
    "docHTML" : "Monitor the memory size for the entries residing in block cache."
}, {
    "caption" : "state.backend.rocksdb.metrics.block-cache-pinned-usage",
    "value" : "state.backend.rocksdb.metrics.block-cache-pinned-usage : false",
    "meta" : "default: false",
    "docHTML" : "Monitor the memory size for the entries being pinned in block cache."
}, {
    "caption" : "state.backend.rocksdb.metrics.column-family-as-variable",
    "value" : "state.backend.rocksdb.metrics.column-family-as-variable : false",
    "meta" : "default: false",
    "docHTML" : "Whether to expose the column family as a variable for RocksDB property based metrics."
}, {
    "caption" : "state.backend.rocksdb.metrics.block-cache-hit",
    "value" : "state.backend.rocksdb.metrics.block-cache-hit : false",
    "meta" : "default: false",
    "docHTML" : "Monitor the total count of block cache hit in RocksDB (BLOCK_CACHE_HIT == BLOCK_CACHE_INDEX_HIT + BLOCK_CACHE_FILTER_HIT + BLOCK_CACHE_DATA_HIT)."
}, {
    "caption" : "state.backend.rocksdb.metrics.block-cache-miss",
    "value" : "state.backend.rocksdb.metrics.block-cache-miss : false",
    "meta" : "default: false",
    "docHTML" : "Monitor the total count of block cache misses in RocksDB (BLOCK_CACHE_MISS == BLOCK_CACHE_INDEX_MISS + BLOCK_CACHE_FILTER_MISS + BLOCK_CACHE_DATA_MISS)."
}, {
    "caption" : "state.backend.rocksdb.metrics.bytes-read",
    "value" : "state.backend.rocksdb.metrics.bytes-read : false",
    "meta" : "default: false",
    "docHTML" : "Monitor the number of uncompressed bytes read (from memtables/cache/sst) from Get() operation in RocksDB."
}, {
    "caption" : "state.backend.rocksdb.metrics.iter-bytes-read",
    "value" : "state.backend.rocksdb.metrics.iter-bytes-read : false",
    "meta" : "default: false",
    "docHTML" : "Monitor the number of uncompressed bytes read (from memtables/cache/sst) from an iterator operation in RocksDB."
}, {
    "caption" : "state.backend.rocksdb.metrics.bytes-written",
    "value" : "state.backend.rocksdb.metrics.bytes-written : false",
    "meta" : "default: false",
    "docHTML" : "Monitor the number of uncompressed bytes written by DB::{Put(), Delete(), Merge(), Write()} operations, which does not include the compaction written bytes, in RocksDB."
}, {
    "caption" : "state.backend.rocksdb.metrics.compaction-read-bytes",
    "value" : "state.backend.rocksdb.metrics.compaction-read-bytes : false",
    "meta" : "default: false",
    "docHTML" : "Monitor the bytes read during compaction in RocksDB."
}, {
    "caption" : "state.backend.rocksdb.metrics.compaction-write-bytes",
    "value" : "state.backend.rocksdb.metrics.compaction-write-bytes : false",
    "meta" : "default: false",
    "docHTML" : "Monitor the bytes written during compaction in RocksDB."
}, {
    "caption" : "state.backend.rocksdb.metrics.stall-micros",
    "value" : "state.backend.rocksdb.metrics.stall-micros : false",
    "meta" : "default: false",
    "docHTML" : "Monitor the duration of writer requiring to wait for compaction or flush to finish in RocksDB."
}, {
    "caption" : "state.backend.rocksdb.localdir",
    "docHTML" : "The local directory (on the TaskManager) where RocksDB puts its files. Per default, it will be <WORKING_DIR>/tmp. See %s for more details."
}, {
    "caption" : "state.backend.rocksdb.timer-service.factory",
    "value" : "state.backend.rocksdb.timer-service.factory : ROCKSDB",
    "meta" : "default: ROCKSDB",
    "docHTML" : "This determines the factory for timer service state implementation."
}, {
    "caption" : "state.backend.rocksdb.checkpoint.transfer.thread.num",
    "value" : "state.backend.rocksdb.checkpoint.transfer.thread.num : 4",
    "meta" : "default: 4",
    "docHTML" : "The number of threads (per stateful operator) used to transfer (download and upload) files in RocksDBStateBackend."
}, {
    "caption" : "state.backend.rocksdb.predefined-options",
    "value" : "state.backend.rocksdb.predefined-options : DEFAULT",
    "meta" : "default: DEFAULT",
    "docHTML" : "The predefined settings for RocksDB DBOptions and ColumnFamilyOptions by Flink community. Current supported candidate predefined-options are DEFAULT, SPINNING_DISK_OPTIMIZED, SPINNING_DISK_OPTIMIZED_HIGH_MEM or FLASH_SSD_OPTIMIZED. Note that user customized options and options from the RocksDBOptionsFactory are applied on top of these predefined ones."
}, {
    "caption" : "state.backend.rocksdb.options-factory",
    "docHTML" : "The options factory class for users to add customized options in DBOptions and ColumnFamilyOptions for RocksDB. If set, the RocksDB state backend will load the class and apply configs to DBOptions and ColumnFamilyOptions after loading ones from 'RocksDBConfigurableOptions' and pre-defined options."
}, {
    "caption" : "state.backend.rocksdb.memory.managed",
    "value" : "state.backend.rocksdb.memory.managed : true",
    "meta" : "default: true",
    "docHTML" : "If set, the RocksDB state backend will automatically configure itself to use the managed memory budget of the task slot, and divide the memory over write buffers, indexes, block caches, etc. That way, the three major uses of memory of RocksDB will be capped."
}, {
    "caption" : "state.backend.rocksdb.memory.fixed-per-slot",
    "docHTML" : "The fixed total amount of memory, shared among all RocksDB instances per slot. This option overrides the 'state.backend.rocksdb.memory.managed' option when configured. If neither this option, nor the 'state.backend.rocksdb.memory.managed' optionare set, then each RocksDB column family state has its own memory caches (as controlled by the column family options)."
}, {
    "caption" : "state.backend.rocksdb.memory.write-buffer-ratio",
    "value" : "state.backend.rocksdb.memory.write-buffer-ratio : 0.5",
    "meta" : "default: 0.5",
    "docHTML" : "The maximum amount of memory that write buffers may take, as a fraction of the total shared memory. This option only has an effect when 'state.backend.rocksdb.memory.managed' or 'state.backend.rocksdb.memory.fixed-per-slot' are configured."
}, {
    "caption" : "state.backend.rocksdb.memory.high-prio-pool-ratio",
    "value" : "state.backend.rocksdb.memory.high-prio-pool-ratio : 0.1",
    "meta" : "default: 0.1",
    "docHTML" : "The fraction of cache memory that is reserved for high-priority data like index, filter, and compression dictionary blocks. This option only has an effect when 'state.backend.rocksdb.memory.managed' or 'state.backend.rocksdb.memory.fixed-per-slot' are configured."
}, {
    "caption" : "state.backend.rocksdb.memory.partitioned-index-filters",
    "value" : "state.backend.rocksdb.memory.partitioned-index-filters : false",
    "meta" : "default: false",
    "docHTML" : "With partitioning, the index/filter block of an SST file is partitioned into smaller blocks with an additional top-level index on them. When reading an index/filter, only top-level index is loaded into memory. The partitioned index/filter then uses the top-level index to load on demand into the block cache the partitions that are required to perform the index/filter query. This option only has an effect when 'state.backend.rocksdb.memory.managed' or 'state.backend.rocksdb.memory.fixed-per-slot' are configured."
}, {
    "caption" : "queryable-state.proxy.ports",
    "value" : "queryable-state.proxy.ports : 9069",
    "meta" : "default: 9069",
    "docHTML" : "The port range of the queryable state proxy. The specified range can be a single port: \"9123\", a range of ports: \"50100-50200\", or a list of ranges and ports: \"50100-50200,50300-50400,51234\"."
}, {
    "caption" : "queryable-state.proxy.network-threads",
    "value" : "queryable-state.proxy.network-threads : 0",
    "meta" : "default: 0",
    "docHTML" : "Number of network (Netty's event loop) Threads for queryable state proxy."
}, {
    "caption" : "queryable-state.proxy.query-threads",
    "value" : "queryable-state.proxy.query-threads : 0",
    "meta" : "default: 0",
    "docHTML" : "Number of query Threads for queryable state proxy. Uses the number of slots if set to 0."
}, {
    "caption" : "queryable-state.server.ports",
    "value" : "queryable-state.server.ports : 9067",
    "meta" : "default: 9067",
    "docHTML" : "The port range of the queryable state server. The specified range can be a single port: \"9123\", a range of ports: \"50100-50200\", or a list of ranges and ports: \"50100-50200,50300-50400,51234\"."
}, {
    "caption" : "queryable-state.server.network-threads",
    "value" : "queryable-state.server.network-threads : 0",
    "meta" : "default: 0",
    "docHTML" : "Number of network (Netty's event loop) Threads for queryable state server."
}, {
    "caption" : "queryable-state.server.query-threads",
    "value" : "queryable-state.server.query-threads : 0",
    "meta" : "default: 0",
    "docHTML" : "Number of query Threads for queryable state server. Uses the number of slots if set to 0."
}, {
    "caption" : "queryable-state.enable",
    "value" : "queryable-state.enable : false",
    "meta" : "default: false",
    "docHTML" : "Option whether the queryable state proxy and server should be enabled where possible and configurable."
}, {
    "caption" : "queryable-state.client.network-threads",
    "value" : "queryable-state.client.network-threads : 0",
    "meta" : "default: 0",
    "docHTML" : "Number of network (Netty's event loop) Threads for queryable state client."
}, {
    "caption" : "execution.checkpointing.mode",
    "value" : "execution.checkpointing.mode : EXACTLY_ONCE",
    "meta" : "default: EXACTLY_ONCE",
    "docHTML" : "The checkpointing mode (exactly-once vs. at-least-once)."
}, {
    "caption" : "execution.checkpointing.timeout",
    "value" : "execution.checkpointing.timeout : PT10M",
    "meta" : "default: PT10M",
    "docHTML" : "The maximum time that a checkpoint may take before being discarded."
}, {
    "caption" : "execution.checkpointing.max-concurrent-checkpoints",
    "value" : "execution.checkpointing.max-concurrent-checkpoints : 1",
    "meta" : "default: 1",
    "docHTML" : "The maximum number of checkpoint attempts that may be in progress at the same time. If this value is n, then no checkpoints will be triggered while n checkpoint attempts are currently in flight. For the next checkpoint to be triggered, one checkpoint attempt would need to finish or expire."
}, {
    "caption" : "execution.checkpointing.min-pause",
    "value" : "execution.checkpointing.min-pause : PT0S",
    "meta" : "default: PT0S"
}, {
    "caption" : "execution.checkpointing.tolerable-failed-checkpoints",
    "docHTML" : "The tolerable checkpoint consecutive failure number. If set to 0, that means we do not tolerance any checkpoint failure. This only applies to the following failure reasons: IOException on the Job Manager, failures in the async phase on the Task Managers and checkpoint expiration due to a timeout. Failures originating from the sync phase on the Task Managers are always forcing failover of an affected task. Other types of checkpoint failures (such as checkpoint being subsumed) are being ignored."
}, {
    "caption" : "execution.checkpointing.externalized-checkpoint-retention",
    "value" : "execution.checkpointing.externalized-checkpoint-retention : NO_EXTERNALIZED_CHECKPOINTS",
    "meta" : "default: NO_EXTERNALIZED_CHECKPOINTS"
}, {
    "caption" : "execution.checkpointing.interval"
}, {
    "caption" : "execution.checkpointing.unaligned",
    "value" : "execution.checkpointing.unaligned : false",
    "meta" : "default: false"
}, {
    "caption" : "execution.checkpointing.aligned-checkpoint-timeout",
    "value" : "execution.checkpointing.aligned-checkpoint-timeout : PT0S",
    "meta" : "default: PT0S"
}, {
    "caption" : "execution.checkpointing.alignment-timeout",
    "value" : "execution.checkpointing.alignment-timeout : PT0S",
    "meta" : "default: PT0S"
}, {
    "caption" : "execution.checkpointing.unaligned.forced",
    "value" : "execution.checkpointing.unaligned.forced : false",
    "meta" : "default: false",
    "docHTML" : "Forces unaligned checkpoints, particularly allowing them for iterative jobs."
}, {
    "caption" : "execution.checkpointing.recover-without-channel-state.checkpoint-id",
    "value" : "execution.checkpointing.recover-without-channel-state.checkpoint-id : -1",
    "meta" : "default: -1"
}, {
    "caption" : "execution.checkpointing.checkpoints-after-tasks-finish.enabled",
    "value" : "execution.checkpointing.checkpoints-after-tasks-finish.enabled : true",
    "meta" : "default: true",
    "docHTML" : "Feature toggle for enabling checkpointing even if some of tasks have finished. Before you enable it, please take a look at %s "
}, {
    "caption" : "pipeline.name",
    "docHTML" : "The job name used for printing and logging."
}, {
    "caption" : "pipeline.jars",
    "docHTML" : "A semicolon-separated list of the jars to package with the job jars to be sent to the cluster. These have to be valid paths."
}, {
    "caption" : "pipeline.classpaths",
    "docHTML" : "A semicolon-separated list of the classpaths to package with the job jars to be sent to the cluster. These have to be valid URLs."
}, {
    "caption" : "pipeline.auto-generate-uids",
    "value" : "pipeline.auto-generate-uids : true",
    "meta" : "default: true"
}, {
    "caption" : "pipeline.auto-type-registration",
    "value" : "pipeline.auto-type-registration : true",
    "meta" : "default: true",
    "docHTML" : "Controls whether Flink is automatically registering all types in the user programs with Kryo."
}, {
    "caption" : "pipeline.auto-watermark-interval",
    "value" : "pipeline.auto-watermark-interval : PT0S",
    "meta" : "default: PT0S",
    "docHTML" : "The interval of the automatic watermark emission. Watermarks are used throughout the streaming system to keep track of the progress of time. They are used, for example, for time based windowing."
}, {
    "caption" : "pipeline.closure-cleaner-level",
    "value" : "pipeline.closure-cleaner-level : RECURSIVE",
    "meta" : "default: RECURSIVE",
    "docHTML" : "Configures the mode in which the closure cleaner works."
}, {
    "caption" : "pipeline.force-avro",
    "value" : "pipeline.force-avro : false",
    "meta" : "default: false"
}, {
    "caption" : "pipeline.force-kryo",
    "value" : "pipeline.force-kryo : false",
    "meta" : "default: false",
    "docHTML" : "If enabled, forces TypeExtractor to use Kryo serializer for POJOS even though we could analyze as POJO. In some cases this might be preferable. For example, when using interfaces with subclasses that cannot be analyzed as POJO."
}, {
    "caption" : "pipeline.generic-types",
    "value" : "pipeline.generic-types : true",
    "meta" : "default: true"
}, {
    "caption" : "pipeline.global-job-parameters",
    "docHTML" : "Register a custom, serializable user configuration object. The configuration can be  accessed in operators"
}, {
    "caption" : "pipeline.max-parallelism",
    "value" : "pipeline.max-parallelism : -1",
    "meta" : "default: -1",
    "docHTML" : "The program-wide maximum parallelism used for operators which haven't specified a maximum parallelism. The maximum parallelism specifies the upper limit for dynamic scaling and the number of key groups used for partitioned state."
}, {
    "caption" : "pipeline.object-reuse",
    "value" : "pipeline.object-reuse : false",
    "meta" : "default: false",
    "docHTML" : "When enabled objects that Flink internally uses for deserialization and passing data to user-code functions will be reused. Keep in mind that this can lead to bugs when the user-code function of an operation is not aware of this behaviour."
}, {
    "caption" : "pipeline.default-kryo-serializers"
}, {
    "caption" : "pipeline.registered-kryo-types",
    "docHTML" : "Semicolon separated list of types to be registered with the serialization stack. If the type is eventually serialized as a POJO, then the type is registered with the POJO serializer. If the type ends up being serialized with Kryo, then it will be registered at Kryo to make sure that only tags are written."
}, {
    "caption" : "pipeline.registered-pojo-types",
    "docHTML" : "Semicolon separated list of types to be registered with the serialization stack. If the type is eventually serialized as a POJO, then the type is registered with the POJO serializer. If the type ends up being serialized with Kryo, then it will be registered at Kryo to make sure that only tags are written."
}, {
    "caption" : "pipeline.operator-chaining",
    "value" : "pipeline.operator-chaining : true",
    "meta" : "default: true",
    "docHTML" : "Operator chaining allows non-shuffle operations to be co-located in the same thread fully avoiding serialization and de-serialization."
}, {
    "caption" : "pipeline.cached-files"
}, {
    "caption" : "pipeline.vertex-description-mode",
    "value" : "pipeline.vertex-description-mode : TREE",
    "meta" : "default: TREE",
    "docHTML" : "The mode how we organize description of a job vertex."
}, {
    "caption" : "pipeline.vertex-name-include-index-prefix",
    "value" : "pipeline.vertex-name-include-index-prefix : false",
    "meta" : "default: false",
    "docHTML" : "Whether name of vertex includes topological index or not. When it is true, the name will have a prefix of index of the vertex, like '[vertex-0]Source: source'. It is false by default"
}, {
    "caption" : "metrics.reporters",
    "docHTML" : "An optional list of reporter names. If configured, only reporters whose name matches any of the names in the list will be started. Otherwise, all reporters that could be found in the configuration will be started."
}, {
    "caption" : "class",
    "docHTML" : "The reporter class to use for the reporter named <name>."
}, {
    "caption" : "factory.class",
    "docHTML" : "The reporter factory class to use for the reporter named <name>."
}, {
    "caption" : "interval",
    "value" : "interval : PT10S",
    "meta" : "default: PT10S",
    "docHTML" : "The reporter interval to use for the reporter named <name>. Only applicable to push-based reporters."
}, {
    "caption" : "scope.delimiter",
    "value" : "scope.delimiter : .",
    "meta" : "default: .",
    "docHTML" : "The delimiter used to assemble the metric identifier for the reporter named <name>."
}, {
    "caption" : "scope.variables.additional",
    "value" : "scope.variables.additional : {}",
    "meta" : "default: {}",
    "docHTML" : "The map of additional variables that should be included for the reporter named <name>. Only applicable to tag-based reporters."
}, {
    "caption" : "scope.variables.excludes",
    "value" : "scope.variables.excludes : .",
    "meta" : "default: .",
    "docHTML" : "The set of variables that should be excluded for the reporter named <name>. Only applicable to tag-based reporters."
}, {
    "caption" : "filter.includes",
    "value" : "filter.includes : [*:*:*]",
    "meta" : "default: [*:*:*]"
}, {
    "caption" : "filter.excludes",
    "value" : "filter.excludes : []",
    "meta" : "default: []"
}, {
    "caption" : "<parameter>",
    "docHTML" : "Configures the parameter <parameter> for the reporter named <name>."
}, {
    "caption" : "metrics.scope.delimiter",
    "value" : "metrics.scope.delimiter : .",
    "meta" : "default: .",
    "docHTML" : "Delimiter used to assemble the metric identifier."
}, {
    "caption" : "metrics.scope.jm",
    "value" : "metrics.scope.jm : <host>.jobmanager",
    "meta" : "default: <host>.jobmanager",
    "docHTML" : "Defines the scope format string that is applied to all metrics scoped to a JobManager. Only effective when a identifier-based reporter is configured."
}, {
    "caption" : "metrics.scope.tm",
    "value" : "metrics.scope.tm : <host>.taskmanager.<tm_id>",
    "meta" : "default: <host>.taskmanager.<tm_id>",
    "docHTML" : "Defines the scope format string that is applied to all metrics scoped to a TaskManager. Only effective when a identifier-based reporter is configured"
}, {
    "caption" : "metrics.scope.jm.job",
    "value" : "metrics.scope.jm.job : <host>.jobmanager.<job_name>",
    "meta" : "default: <host>.jobmanager.<job_name>",
    "docHTML" : "Defines the scope format string that is applied to all metrics scoped to a job on a JobManager. Only effective when a identifier-based reporter is configured"
}, {
    "caption" : "metrics.scope.tm.job",
    "value" : "metrics.scope.tm.job : <host>.taskmanager.<tm_id>.<job_name>",
    "meta" : "default: <host>.taskmanager.<tm_id>.<job_name>",
    "docHTML" : "Defines the scope format string that is applied to all metrics scoped to a job on a TaskManager. Only effective when a identifier-based reporter is configured"
}, {
    "caption" : "metrics.scope.task",
    "value" : "metrics.scope.task : <host>.taskmanager.<tm_id>.<job_name>.<task_name>.<subtask_index>",
    "meta" : "default: <host>.taskmanager.<tm_id>.<job_name>.<task_name>.<subtask_index>",
    "docHTML" : "Defines the scope format string that is applied to all metrics scoped to a task. Only effective when a identifier-based reporter is configured"
}, {
    "caption" : "metrics.scope.operator",
    "value" : "metrics.scope.operator : <host>.taskmanager.<tm_id>.<job_name>.<operator_name>.<subtask_index>",
    "meta" : "default: <host>.taskmanager.<tm_id>.<job_name>.<operator_name>.<subtask_index>",
    "docHTML" : "Defines the scope format string that is applied to all metrics scoped to an operator. Only effective when a identifier-based reporter is configured"
}, {
    "caption" : "metrics.latency.interval",
    "value" : "metrics.latency.interval : 0",
    "meta" : "default: 0",
    "docHTML" : "Defines the interval at which latency tracking marks are emitted from the sources. Disables latency tracking if set to 0 or a negative value. Enabling this feature can significantly impact the performance of the cluster."
}, {
    "caption" : "metrics.latency.granularity",
    "value" : "metrics.latency.granularity : operator",
    "meta" : "default: operator"
}, {
    "caption" : "metrics.latency.history-size",
    "value" : "metrics.latency.history-size : 128",
    "meta" : "default: 128",
    "docHTML" : "Defines the number of measured latencies to maintain at each operator."
}, {
    "caption" : "metrics.system-resource",
    "value" : "metrics.system-resource : false",
    "meta" : "default: false",
    "docHTML" : "Flag indicating whether Flink should report system resource metrics such as machine's CPU, memory or network usage."
}, {
    "caption" : "metrics.system-resource-probing-interval",
    "value" : "metrics.system-resource-probing-interval : 5000",
    "meta" : "default: 5000",
    "docHTML" : "Interval between probing of system resource metrics specified in milliseconds. Has an effect only when 'metrics.system-resource' is enabled."
}, {
    "caption" : "metrics.internal.query-service.port",
    "value" : "metrics.internal.query-service.port : 0",
    "meta" : "default: 0",
    "docHTML" : "The port range used for Flink's internal metric query service. Accepts a list of ports (“50100,50101”), ranges(“50100-50200”) or a combination of both. It is recommended to set a range of ports to avoid collisions when multiple Flink components are running on the same machine. Per default Flink will pick a random port."
}, {
    "caption" : "metrics.internal.query-service.thread-priority",
    "value" : "metrics.internal.query-service.thread-priority : 1",
    "meta" : "default: 1",
    "docHTML" : "The thread priority used for Flink's internal metric query service. The thread is created by Akka's thread pool executor. The range of the priority is from 1 (MIN_PRIORITY) to 10 (MAX_PRIORITY). Warning, increasing this value may bring the main Flink components down."
}, {
    "caption" : "metrics.fetcher.update-interval",
    "value" : "metrics.fetcher.update-interval : 10000",
    "meta" : "default: 10000",
    "docHTML" : "Update interval for the metric fetcher used by the web UI in milliseconds. Decrease this value for faster updating metrics. Increase this value if the metric fetcher causes too much load. Setting this value to 0 disables the metric fetching completely."
}, {
    "caption" : "metrics.job.status.enable",
    "value" : "metrics.job.status.enable : [CURRENT_TIME]",
    "meta" : "default: [CURRENT_TIME]",
    "docHTML" : "The selection of job status metrics that should be reported."
}, {
    "caption" : "akka.rpc.force-invocation-serialization",
    "value" : "akka.rpc.force-invocation-serialization : false",
    "meta" : "default: false",
    "docHTML" : "Forces the serialization of all RPC invocations (that are not explicitly annotated with %s).This option can be used to find serialization issues in the argument/response types without relying requiring HA setups.This option should not be enabled in production."
}, {
    "caption" : "akka.ask.callstack",
    "value" : "akka.ask.callstack : true",
    "meta" : "default: true",
    "docHTML" : "If true, call stack for asynchronous asks are captured. That way, when an ask fails (for example times out), you get a proper exception, describing to the original method call and call site. Note that in case of having millions of concurrent RPC calls, this may add to the memory footprint."
}, {
    "caption" : "akka.ask.timeout",
    "value" : "akka.ask.timeout : PT10S",
    "meta" : "default: PT10S",
    "docHTML" : "Timeout used for all futures and blocking Akka calls. If Flink fails due to timeouts then you should try to increase this value. Timeouts can be caused by slow machines or a congested network. The timeout value requires a time-unit specifier (ms/s/min/h/d)."
}, {
    "caption" : "akka.ask.timeout",
    "value" : "akka.ask.timeout : 10 s",
    "meta" : "default: 10 s",
    "docHTML" : "Timeout used for all futures and blocking Akka calls. If Flink fails due to timeouts then you should try to increase this value. Timeouts can be caused by slow machines or a congested network. The timeout value requires a time-unit specifier (ms/s/min/h/d)."
}, {
    "caption" : "akka.tcp.timeout",
    "value" : "akka.tcp.timeout : 20 s",
    "meta" : "default: 20 s",
    "docHTML" : "Timeout for all outbound connections. If you should experience problems with connecting to a TaskManager due to a slow network, you should increase this value."
}, {
    "caption" : "akka.startup-timeout",
    "docHTML" : "Timeout after which the startup of a remote component is considered being failed."
}, {
    "caption" : "akka.ssl.enabled",
    "value" : "akka.ssl.enabled : true",
    "meta" : "default: true",
    "docHTML" : "Turns on SSL for Akka’s remote communication. This is applicable only when the global ssl flag security.ssl.enabled is set to true."
}, {
    "caption" : "akka.framesize",
    "value" : "akka.framesize : 10485760b",
    "meta" : "default: 10485760b",
    "docHTML" : "Maximum size of messages which are sent between the JobManager and the TaskManagers. If Flink fails because messages exceed this limit, then you should increase it. The message size requires a size-unit specifier."
}, {
    "caption" : "akka.throughput",
    "value" : "akka.throughput : 15",
    "meta" : "default: 15",
    "docHTML" : "Number of messages that are processed in a batch before returning the thread to the pool. Low values denote a fair scheduling whereas high values can increase the performance at the cost of unfairness."
}, {
    "caption" : "akka.log.lifecycle.events",
    "value" : "akka.log.lifecycle.events : false",
    "meta" : "default: false",
    "docHTML" : "Turns on the Akka’s remote logging of events. Set this value to 'true' in case of debugging."
}, {
    "caption" : "akka.lookup.timeout",
    "value" : "akka.lookup.timeout : PT10S",
    "meta" : "default: PT10S",
    "docHTML" : "Timeout used for the lookup of the JobManager. The timeout value has to contain a time-unit specifier (ms/s/min/h/d)."
}, {
    "caption" : "akka.lookup.timeout",
    "value" : "akka.lookup.timeout : 10 s",
    "meta" : "default: 10 s",
    "docHTML" : "Timeout used for the lookup of the JobManager. The timeout value has to contain a time-unit specifier (ms/s/min/h/d)."
}, {
    "caption" : "akka.client.timeout",
    "value" : "akka.client.timeout : 60 s",
    "meta" : "default: 60 s",
    "docHTML" : "DEPRECATED: Use the \"client.timeout\" instead. Timeout for all blocking calls on the client side."
}, {
    "caption" : "akka.jvm-exit-on-fatal-error",
    "value" : "akka.jvm-exit-on-fatal-error : true",
    "meta" : "default: true",
    "docHTML" : "Exit JVM on fatal Akka errors."
}, {
    "caption" : "akka.retry-gate-closed-for",
    "value" : "akka.retry-gate-closed-for : 50",
    "meta" : "default: 50",
    "docHTML" : "Milliseconds a gate should be closed for after a remote connection was disconnected."
}, {
    "caption" : "akka.fork-join-executor.parallelism-factor",
    "value" : "akka.fork-join-executor.parallelism-factor : 2.0",
    "meta" : "default: 2.0",
    "docHTML" : "The parallelism factor is used to determine thread pool size using the following formula: ceil(available processors * factor). Resulting size is then bounded by the parallelism-min and parallelism-max values."
}, {
    "caption" : "akka.fork-join-executor.parallelism-min",
    "value" : "akka.fork-join-executor.parallelism-min : 8",
    "meta" : "default: 8",
    "docHTML" : "Min number of threads to cap factor-based parallelism number to."
}, {
    "caption" : "akka.fork-join-executor.parallelism-max",
    "value" : "akka.fork-join-executor.parallelism-max : 64",
    "meta" : "default: 64",
    "docHTML" : "Max number of threads to cap factor-based parallelism number to."
}, {
    "caption" : "akka.client-socket-worker-pool.pool-size-min",
    "value" : "akka.client-socket-worker-pool.pool-size-min : 1",
    "meta" : "default: 1",
    "docHTML" : "Min number of threads to cap factor-based number to."
}, {
    "caption" : "akka.client-socket-worker-pool.pool-size-max",
    "value" : "akka.client-socket-worker-pool.pool-size-max : 2",
    "meta" : "default: 2",
    "docHTML" : "Max number of threads to cap factor-based number to."
}, {
    "caption" : "akka.client-socket-worker-pool.pool-size-factor",
    "value" : "akka.client-socket-worker-pool.pool-size-factor : 1.0",
    "meta" : "default: 1.0",
    "docHTML" : "The pool size factor is used to determine thread pool size using the following formula: ceil(available processors * factor). Resulting size is then bounded by the pool-size-min and pool-size-max values."
}, {
    "caption" : "akka.server-socket-worker-pool.pool-size-min",
    "value" : "akka.server-socket-worker-pool.pool-size-min : 1",
    "meta" : "default: 1",
    "docHTML" : "Min number of threads to cap factor-based number to."
}, {
    "caption" : "akka.server-socket-worker-pool.pool-size-max",
    "value" : "akka.server-socket-worker-pool.pool-size-max : 2",
    "meta" : "default: 2",
    "docHTML" : "Max number of threads to cap factor-based number to."
}, {
    "caption" : "akka.server-socket-worker-pool.pool-size-factor",
    "value" : "akka.server-socket-worker-pool.pool-size-factor : 1.0",
    "meta" : "default: 1.0",
    "docHTML" : "The pool size factor is used to determine thread pool size using the following formula: ceil(available processors * factor). Resulting size is then bounded by the pool-size-min and pool-size-max values."
}, {
    "caption" : "akka.watch.heartbeat.interval",
    "value" : "akka.watch.heartbeat.interval : 10 s",
    "meta" : "default: 10 s",
    "docHTML" : "Heartbeat interval for Akka’s DeathWatch mechanism to detect dead TaskManagers. If TaskManagers are wrongly marked dead because of lost or delayed heartbeat messages, then you should decrease this value or increase akka.watch.heartbeat.pause. A thorough description of Akka’s DeathWatch can be found %s"
}, {
    "caption" : "akka.watch.heartbeat.pause",
    "value" : "akka.watch.heartbeat.pause : 60 s",
    "meta" : "default: 60 s",
    "docHTML" : "Acceptable heartbeat pause for Akka’s DeathWatch mechanism. A low value does not allow an irregular heartbeat. If TaskManagers are wrongly marked dead because of lost or delayed heartbeat messages, then you should increase this value or decrease akka.watch.heartbeat.interval. Higher value increases the time to detect a dead TaskManager. A thorough description of Akka’s DeathWatch can be found %s"
}, {
    "caption" : "akka.watch.threshold",
    "value" : "akka.watch.threshold : 12",
    "meta" : "default: 12",
    "docHTML" : "Threshold for the DeathWatch failure detector. A low value is prone to false positives whereas a high value increases the time to detect a dead TaskManager. A thorough description of Akka’s DeathWatch can be found %s"
}, {
    "caption" : "security.context.factory.classes",
    "value" : "security.context.factory.classes : [org.apache.flink.runtime.security.contexts.HadoopSecurityContextFactory, org.apache.flink.runtime.security.contexts.NoOpSecurityContextFactory]",
    "meta" : "default: [org.apache.flink.runtime.security.contexts.HadoopSecurityContextFactory, org.apache.flink.runtime.security.contexts.NoOpSecurityContextFactory]",
    "docHTML" : "List of factories that should be used to instantiate a security context. If multiple are configured, Flink will use the first compatible factory. You should have a NoOpSecurityContextFactory in this list as a fallback."
}, {
    "caption" : "security.module.factory.classes",
    "value" : "security.module.factory.classes : [org.apache.flink.runtime.security.modules.HadoopModuleFactory, org.apache.flink.runtime.security.modules.JaasModuleFactory, org.apache.flink.runtime.security.modules.ZookeeperModuleFactory]",
    "meta" : "default: [org.apache.flink.runtime.security.modules.HadoopModuleFactory, org.apache.flink.runtime.security.modules.JaasModuleFactory, org.apache.flink.runtime.security.modules.ZookeeperModuleFactory]",
    "docHTML" : "List of factories that should be used to instantiate security modules. All listed modules will be installed. Keep in mind that the configured security context might rely on some modules being present."
}, {
    "caption" : "security.kerberos.login.principal",
    "docHTML" : "Kerberos principal name associated with the keytab."
}, {
    "caption" : "security.kerberos.login.keytab",
    "docHTML" : "Absolute path to a Kerberos keytab file that contains the user credentials."
}, {
    "caption" : "security.kerberos.krb5-conf.path",
    "docHTML" : "Specify the local location of the krb5.conf file. If defined, this conf would be mounted on the JobManager and TaskManager containers/pods for Kubernetes and Yarn. Note: The KDC defined needs to be visible from inside the containers."
}, {
    "caption" : "security.kerberos.login.use-ticket-cache",
    "value" : "security.kerberos.login.use-ticket-cache : true",
    "meta" : "default: true",
    "docHTML" : "Indicates whether to read from your Kerberos ticket cache."
}, {
    "caption" : "security.kerberos.login.contexts",
    "docHTML" : "A comma-separated list of login contexts to provide the Kerberos credentials to (for example, `Client,KafkaClient` to use the credentials for ZooKeeper authentication and for Kafka authentication)"
}, {
    "caption" : "security.kerberos.fetch.delegation-token",
    "value" : "security.kerberos.fetch.delegation-token : true",
    "meta" : "default: true",
    "docHTML" : "Indicates whether to fetch the delegation tokens for external services the Flink job needs to contact. Only HDFS and HBase are supported. It is used in Yarn deployments. If true, Flink will fetch HDFS and HBase delegation tokens and inject them into Yarn AM containers. If false, Flink will assume that the delegation tokens are managed outside of Flink. As a consequence, it will not fetch delegation tokens for HDFS and HBase. You may need to disable this option, if you rely on submission mechanisms, e.g. Apache Oozie, to handle delegation tokens."
}, {
    "caption" : "security.kerberos.relogin.period",
    "value" : "security.kerberos.relogin.period : PT1M",
    "meta" : "default: PT1M",
    "docHTML" : "The time period when keytab login happens automatically in order to always have a valid TGT."
}, {
    "caption" : "security.kerberos.tokens.renewal.retry.backoff",
    "value" : "security.kerberos.tokens.renewal.retry.backoff : PT1H",
    "meta" : "default: PT1H",
    "docHTML" : "The time period how long to wait before retrying to obtain new delegation tokens after a failure."
}, {
    "caption" : "security.kerberos.tokens.renewal.time-ratio",
    "value" : "security.kerberos.tokens.renewal.time-ratio : 0.75",
    "meta" : "default: 0.75",
    "docHTML" : "Ratio of the tokens's expiration time when new credentials should be re-obtained."
}, {
    "caption" : "security.kerberos.access.hadoopFileSystems",
    "docHTML" : "A comma-separated list of Kerberos-secured Hadoop filesystems Flink is going to access. For example, security.kerberos.access.hadoopFileSystems=hdfs://namenode2:9002,hdfs://namenode3:9003. The JobManager needs to have access to these filesystems to retrieve the security tokens."
}, {
    "caption" : "zookeeper.sasl.disable",
    "value" : "zookeeper.sasl.disable : false",
    "meta" : "default: false",
    "docHTML" : ""
}, {
    "caption" : "zookeeper.sasl.service-name",
    "value" : "zookeeper.sasl.service-name : zookeeper",
    "meta" : "default: zookeeper",
    "docHTML" : ""
}, {
    "caption" : "zookeeper.sasl.login-context-name",
    "value" : "zookeeper.sasl.login-context-name : Client",
    "meta" : "default: Client",
    "docHTML" : ""
}, {
    "caption" : "security.ssl.enabled",
    "value" : "security.ssl.enabled : false",
    "meta" : "default: false",
    "docHTML" : "Turns on SSL for internal and external network communication.This can be overridden by 'security.ssl.internal.enabled', 'security.ssl.external.enabled'. Specific internal components (rpc, data transport, blob server) may optionally override this through their own settings."
}, {
    "caption" : "security.ssl.internal.enabled",
    "value" : "security.ssl.internal.enabled : false",
    "meta" : "default: false",
    "docHTML" : "Turns on SSL for internal network communication. Optionally, specific components may override this through their own settings (rpc, data transport, REST, etc)."
}, {
    "caption" : "security.ssl.rest.enabled",
    "value" : "security.ssl.rest.enabled : false",
    "meta" : "default: false",
    "docHTML" : "Turns on SSL for external communication via the REST endpoints."
}, {
    "caption" : "security.ssl.rest.authentication-enabled",
    "value" : "security.ssl.rest.authentication-enabled : false",
    "meta" : "default: false",
    "docHTML" : "Turns on mutual SSL authentication for external communication via the REST endpoints."
}, {
    "caption" : "security.ssl.keystore",
    "docHTML" : "The Java keystore file to be used by the flink endpoint for its SSL Key and Certificate."
}, {
    "caption" : "security.ssl.keystore-password",
    "docHTML" : "The secret to decrypt the keystore file."
}, {
    "caption" : "security.ssl.key-password",
    "docHTML" : "The secret to decrypt the server key in the keystore."
}, {
    "caption" : "security.ssl.truststore",
    "docHTML" : "The truststore file containing the public CA certificates to be used by flink endpoints to verify the peer’s certificate."
}, {
    "caption" : "security.ssl.truststore-password",
    "docHTML" : "The secret to decrypt the truststore."
}, {
    "caption" : "security.ssl.internal.keystore",
    "docHTML" : "The Java keystore file with SSL Key and Certificate, to be used Flink's internal endpoints (rpc, data transport, blob server)."
}, {
    "caption" : "security.ssl.internal.keystore-password",
    "docHTML" : "The secret to decrypt the keystore file for Flink's for Flink's internal endpoints (rpc, data transport, blob server)."
}, {
    "caption" : "security.ssl.internal.key-password",
    "docHTML" : "The secret to decrypt the key in the keystore for Flink's internal endpoints (rpc, data transport, blob server)."
}, {
    "caption" : "security.ssl.internal.truststore",
    "docHTML" : "The truststore file containing the public CA certificates to verify the peer for Flink's internal endpoints (rpc, data transport, blob server)."
}, {
    "caption" : "security.ssl.internal.truststore-password",
    "docHTML" : "The password to decrypt the truststore for Flink's internal endpoints (rpc, data transport, blob server)."
}, {
    "caption" : "security.ssl.internal.cert.fingerprint",
    "docHTML" : "The sha1 fingerprint of the internal certificate. This further protects the internal communication to present the exact certificate used by Flink.This is necessary where one cannot use private CA(self signed) or there is internal firm wide CA is required"
}, {
    "caption" : "security.ssl.rest.keystore",
    "docHTML" : "The Java keystore file with SSL Key and Certificate, to be used Flink's external REST endpoints."
}, {
    "caption" : "security.ssl.rest.keystore-password",
    "docHTML" : "The secret to decrypt the keystore file for Flink's for Flink's external REST endpoints."
}, {
    "caption" : "security.ssl.rest.key-password",
    "docHTML" : "The secret to decrypt the key in the keystore for Flink's external REST endpoints."
}, {
    "caption" : "security.ssl.rest.truststore",
    "docHTML" : "The truststore file containing the public CA certificates to verify the peer for Flink's external REST endpoints."
}, {
    "caption" : "security.ssl.rest.truststore-password",
    "docHTML" : "The password to decrypt the truststore for Flink's external REST endpoints."
}, {
    "caption" : "security.ssl.rest.cert.fingerprint",
    "docHTML" : "The sha1 fingerprint of the rest certificate. This further protects the rest REST endpoints to present certificate which is only used by proxy serverThis is necessary where once uses public CA or internal firm wide CA"
}, {
    "caption" : "security.ssl.protocol",
    "value" : "security.ssl.protocol : TLSv1.2",
    "meta" : "default: TLSv1.2",
    "docHTML" : "The SSL protocol version to be supported for the ssl transport. Note that it doesn’t support comma separated list."
}, {
    "caption" : "security.ssl.algorithms",
    "value" : "security.ssl.algorithms : TLS_RSA_WITH_AES_128_CBC_SHA",
    "meta" : "default: TLS_RSA_WITH_AES_128_CBC_SHA",
    "docHTML" : "The comma separated list of standard SSL algorithms to be supported. Read more %s"
}, {
    "caption" : "security.ssl.verify-hostname",
    "value" : "security.ssl.verify-hostname : true",
    "meta" : "default: true",
    "docHTML" : "Flag to enable peer’s hostname verification during ssl handshake."
}, {
    "caption" : "security.ssl.provider",
    "value" : "security.ssl.provider : JDK",
    "meta" : "default: JDK"
}, {
    "caption" : "security.ssl.internal.session-cache-size",
    "value" : "security.ssl.internal.session-cache-size : -1",
    "meta" : "default: -1",
    "docHTML" : "The size of the cache used for storing SSL session objects. According to %s, you should always set this to an appropriate number to not run into a bug with stalling IO threads during garbage collection. (-1 = use system default)."
}, {
    "caption" : "security.ssl.internal.session-timeout",
    "value" : "security.ssl.internal.session-timeout : -1",
    "meta" : "default: -1",
    "docHTML" : "The timeout (in ms) for the cached SSL session objects. (-1 = use system default)"
}, {
    "caption" : "security.ssl.internal.handshake-timeout",
    "value" : "security.ssl.internal.handshake-timeout : -1",
    "meta" : "default: -1",
    "docHTML" : "The timeout (in ms) during SSL handshake. (-1 = use system default)"
}, {
    "caption" : "security.ssl.internal.close-notify-flush-timeout",
    "value" : "security.ssl.internal.close-notify-flush-timeout : -1",
    "meta" : "default: -1",
    "docHTML" : "The timeout (in ms) for flushing the `close_notify` that was triggered by closing a channel. If the `close_notify` was not flushed in the given timeout the channel will be closed forcibly. (-1 = use system default)"
} ]
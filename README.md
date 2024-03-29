## REST job server for Apache Flink (Flink as a Service)

主要特点
1. 通过Rest API 提交flink 作业运行，支持sql，java/scala 类型作业，解耦业务系统与Flink 集群。
2. Flink Job 运行资源相互隔离，每一个job 独立运行在一个FLink driver中。
3. 预启动 Flink Driver，特别是在WebIDE 交互场景，有效提高Job 启动速度，Driver 共享运行多个Job(同时只有一个job运行，有点类似pre job模式)
4. 支持多集群部署，Client 提交Job 到指定集群运行，只支持 application 和 session 模式
5. Driver 定制化，可以实现比较多能力，例如：表权限，碎片文件压缩，DQC等功能。
6. 更加灵活的对调度任务并发和优先级控制，例如单个用户最大并发数量，不完全依赖底层yarn、k8s等资源管理能力。例如一些调度框架是把作业直接传给yarn 资源管理器，如果yarn资源不够，提交上去的任务全部在yarn 等待队列中。CDH 默认是公平调度，会导致任务无法按照优先级运行。

![Flink 任务](imgs/jobserver.png)
![集群管理](imgs/cluster.png)

## 一、Build

```
mvn clean package -Prelease,hadoop-2 -DlibScope=provided
mvn clean package -Prelease,hadoop-3 -DlibScope=provided
mvn clean package -Prelease,cdh6 -DlibScope=provided
```

## 二、作业实例接口 
包含作业实例提交、作业实例状态查询、作业实例运行日志查询、终止作业实例接口，具体接口：[Rest API](https://github.com/melin/flink-jobserver/blob/master/flink-admin/src/main/java/io/github/melin/flink/jobserver/web/rest/JobServerRestApi.java)

### 1、Flink jar 作业
请参考: [Flink jar](https://github.com/melin/flink-jobserver/tree/master/jobserver-api)

## 三、Yarn Cluster 模式部署
### 1、准备环境
Flink 任务运行环境：Hadoop 2.7.7，Spark 3.3.0。为避免每次任务运行上传jar，提前把相关jar 上传到 hdfs 路径。根路径：/user/superior/flink-jobserver (可以在集群管理中通过，修改jobserver.driver.home 参数设置)，根路径下有不同jar 和 目录
1. flink-jobserver-driver-0.1.0.jar  -- flink jobserver driver jar，jar 参考编译 flink jobserver 部分。
2. aspectjweaver-1.9.9.1.jar  -- aspectj 拦截spark 相关代码，实现功能增强，直接maven 仓库下载 
3. flink-1.16.0  --flink 依赖所有jar，从flink 官网下载: flink-1.16.0-bin-scala_2.12.tgz, 解压后把lib 目录下所有jar 上传到 flink-1.16.0 目录。
4. 如果使用hudi，上传 hudi-flink[version]-bundle-[version].jar 到 flink-1.16.0 目录。

### 2、Yarn 集群配置要求
1. yarn 开启日志聚合，方便出现问题查看日志 
2. yarn 开启cgroup cpu 资源隔离，避免资源争抢 
3. 开启 history server 
4. hdfs block 设置为256M

### 3、部署 jobserver admin 服务

上传 flink-jobserver-0.1.0.tar.gz 文件到服务器，直接解压生成目录：flink-jobserver-0.1.0
> tar -zxf flink-jobserver-0.1.0.tar.gz

创建数据jobserver，执行 script/jobserver.sql 脚本，创建表。

### 4、集群相关参数

[参考代码](https://github.com/melin/flink-jobserver/blob/master/jobserver-admin/src/main/java/io/github/melin/flink/jobserver/FlinkJobServerConf.java)

### 5、启动服务
```
./bin/server.sh start dev
-- 启动脚本有两个参数
第一个参数可选值：start、stop、restart、status、log，启动status 和 log 不需要指定第二参数。
第二个参数可选值：dev、test、production。对应spring boot profile，对应应用启动加载conf目录下application-[profile].properties 文件
```

## 四、相关项目
1. https://gitee.com/melin/bee
2. https://github.com/melin/superior-sql-parser
3. https://github.com/melin/superior-sql-formatter
4. https://github.com/melin/superior-sql-autocomplete
5. https://github.com/melin/datatunnel
6. https://github.com/melin/spark-jobserver

## 五、参考资料
1. [Flink SQL通过Hudi HMS Catalog读写Hudi并同步Hive表](https://mp.weixin.qq.com/s/WpvOvFv-iAzdCwmOO5oQ4Q)

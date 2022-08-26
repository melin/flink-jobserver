## REST job server for Apache Flink

主要特点

1. 通过Rest API 提交spark 作业运行，支持sql，java/scala 类型作业，解耦业务系统与Flink 集群。
2. Flink Job 运行资源相互隔离，每一个job 独立运行在一个Spark driver中。
3. 预启动 Flink Driver，提高Job 启动速度，Driver 共享运行多个Job(同时只有一个job运行)
4. 支持多集群部署，Client 提交Job 到指定集群运行，不是模式只支持 flink yarn application
5. Driver 定制化，可以实现比较多能力，例如：表权限，碎片文件压缩，DQC等功能。

## 一、Build

```
mvn clean package -Prelease,hadoop-2 -DlibScope=provided
mvn clean package -Prelease,hadoop-3 -DlibScope=provided
mvn clean package -Prelease,cdh6 -DlibScope=provided
```

## 二、作业实例接口
包含作业实例提交、作业实例状态查询、作业实例运行日志查询、终止作业实例接口，具体接口：[Rest API](https://github.com/melin/flink-jobserver/blob/master/flink-admin/src/main/java/io/github/melin/flink/jobserver/web/rest/JobServerRestApi.java)

### 1、Spark jar 作业
请参考: [Spark jar](https://github.com/melin/flink-jobserver/tree/master/jobserver-api)

## 三、Yarn Cluster 模式部署
### 1、准备环境
Flink 任务运行环境：Hadoop 2.7.7，Spark 3.3.0。为避免每次任务运行上传jar，提前把相关jar 上传到 hdfs 路径。根路径：/user/superior/flink-jobserver (可以在集群管理中通过，修改jobserver.driver.home 参数设置)，根路径下有不同jar 和 目录
1. flink-jobserver-driver-0.1.0.jar  -- spark jobserver driver jar，jar 参考编译spark jobserver 部分。
2. flink-1.15.2  --flink 依赖所有jar，从flink 官网下载: flink-1.15.2-bin-scala_2.12.tgz, 解压后把lib 目录下所有jar 上传到 flink-1.15.2 目录。

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
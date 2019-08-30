# springcloud-sample


* [config-server](#config-server)
    *  基于git、rmq,实现了提交配置文件的修改， client能及时获取到最新配置,service-provider中利用spring-profile 来构建多环境配置 java -jar service-provider-0.0.1-SNAPSHOT-exec.jar –spring.profiles.active=test 多实例注册到eureka，高可用，调用方写服务名即可
    
* [Apollo](#config-server)
    * 企业级特性较多，部署和接入都有很详细的文档照着做就行。https://github.com/ctripcorp/apollo/wiki
    
* [service-provider](#service-provider)
    * swagger-ui 、mybatis、druid datasource 热部署、druid monitor监控sql语句,并重写DruidDataSourceStatLoggerAdapter实现监控到的sql另存
    
    
* [service-consumer](#service-consumer)
    * consumer有两个实现,ribbon、feign、现阶段感觉feign比较容易上手,特别是在处理嵌套的json时，很方便
    
    
* [spring-boot-admin](#spring-boot-admin)
    * 向eureka-server中注册,并且监控service-provider、service-consumer和config-server。tip: spring-boot-admin会从eureka中获取各个应用的信息
    
* [zipk-server](#zipk-server)
    * 记录各服务之间的链路数据，添加自定义数据 tracer.addTag(key,val) ，数据保存到es 6.2.2，可用kibana进行聚合展示
    
    
* [mybatis](#mybatis)
    * 使用mybatis-generator自动生成mapper和model,重复执行一个表的时候xml会在已有文件里面追加，每次修改gennerator的配置文件的时候要格外注意 mvn mybatis-generator:generate  ，因为要写sql语句，所以和数据库紧耦合
    
* [spring-retry](#spring-retry)
    * 在service-consumer-ribbon里面有加入,测试调用service-provider失败后的场景
    
* [redis](#redis)
    * 使用jedis操作redis集群
        
* [kafka](#kafka)
    * 调用kafkaClient 实现 消息发送和消费，consumer单线程poll数据，线程池处理records不保证顺序消费，满足至少一次的语义,更多原理性东西参考:http://orchome.com/535

	
* [rabbitmp](#rabbitmp)
    * service-provider发rmq消息，rmqconsumer中处理，sender-->exchange-->exchange->queue-->consumer，exchange queue msg 都持久化，consumer处理消息用线程池
    
    	
* [日志处理](#日志处理)
    * zipkin会生成一个全局的traceid 写debug、errorlog的时候建议都带上traceid 到时好关联查找问题，用filebeat收集日志到es
    
* [filebeat-logstash](#filebeat-logstash)
    * filebeat & logstash 的实例配置我都有放一个在 service-provider resource文件夹下,可直接参考
    
    
    
    
    
    
* [守护进程 supervisor 安装](#守护进程 supervisor 安装)
  * systemd 来守护supervisord
  我用的是pip 安装：
  
	> pip install supervisor
	
	如果没有pip,需要先安装PIP
	
	> yum -y install epel-release
	
	> yum -y install python-pip
	
	产生 supervisor 的配置文件
	
	> echo_supervisord_conf > /etc/supervisor/supervisord.conf
	
	默认的日志在 /tmp/supervisord.log

	supervisor 默认不是系统服务，不会随着系统重启而启动，下载是把它做成系统服务:
	
	> vi /usr/lib/systemd/system/supervisord.service
	
	添加以下内容：
	
		# supervisord service for systemd (CentOS 7.0+)
		# by ET-CS (https://github.com/ET-CS)
		[Unit]
		Description=Supervisor daemon

		[Service]
		Type=forking
		ExecStart=/usr/bin/supervisord
		ExecStop=/usr/bin/supervisorctl $OPTIONS shutdown
		ExecReload=/usr/bin/supervisorctl $OPTIONS reload
		KillMode=process
		Restart=on-failure
		RestartSec=42s

		[Install]
		WantedBy=multi-user.target

	保存退出:
	
	> systemctl daemon-reload
	
	> systemctl enable supervisord.service
	
	> systemctl Start supervisord.service

    
* [supervisor 守护java进程](#supervisor 守护java进程)

  * supervisor 守护java进程 配置

  在/etc/supervisor/supervisord.conf文件中添加 
  
  [include]

  files = /etc/conf.d/*.conf 
  
  然后直接在/etc/supervisor/conf.d目录下 添加一个 pacakgeoptionbusinessapa.conf文件 里面添加如下配置:
    
  [program:package-option-business-api] 

  command=/usr/java/jdk-11.0.3+7/bin/java -jar package-option-business-api-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod

  directory=/usr/local/soft/pbs/package-option-business-api

  autorestart=true

  stderr_logfile=/var/log/supervisor/package-option-business-api.err.log

  stdout_logfile=/var/log/supervisor/package-option-business-api.out.log

  user=root

  stopsignal=INT



  
  或者

  直接在/etc/supervisor/supervisord.conf中添加如下配置:
  
  [program:package-option-business-api] 

  command=/usr/java/jdk-11.0.3+7/bin/java -jar package-option-business-api-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod

  directory=/usr/local/soft/pbs/package-option-business-api

  autorestart=true

  stderr_logfile=/var/log/supervisor/package-option-business-api.err.log

  stdout_logfile=/var/log/supervisor/package-option-business-api.out.log

  user=root

  stopsignal=INT

  配置添加成功后 

  supervisorctl update 更新配置   supervisorctl reload 重启配置中所有进程  supervisorctl 查看正在守护的进程  supervisorctl restart program_name 重启某个进程

  

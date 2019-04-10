# spring-boot-starter-redis

本项目兼容redis-cluster和redis-single，实现功能如下：

通过注解的方式存储，查看，和删除实体，实际意义上实现redis集群效果。

使用方法:

1，引入spring-boot-starter-redis,当前项目可能存在重名情况，不适合在公共maven仓库生成，可以下载项目自行编译进入本地仓库后使用:

    <dependency>
		<groupId>com.viching.redis</groupId>
		<artifactId>spring-boot-starter-redis</artifactId>
		<version>0.0.2</version>
	</dependency>

2，redis-cluster，环境配置和使用请参照官方文档,相关参数可配置如下，例如：

	spring:
	  redis:
	    #host: 
	    #port: 6379
	    timeout: 1000
	    password: admin
	    cluster:   // 若不配置表示为单机redis,否则忽略单机配置
	      maxRedirects: 10
	      nodes: 
	       - 192.168.1.103:7000
	       - 192.168.1.103:7001
	       - 192.168.1.103:7002
	       - 192.168.1.103:7003
	       - 192.168.1.103:7004
	       - 192.168.1.103:7005
	    maxIdle: 300
	    maxTotal: 600
	    maxWaitMillis: 1000
	    testOnBorrow: true
	    jedis: 
	      pool:
	        maxActive: 5000 #最大连接数
	        maxIdle: 30 #最大空闲连接数
	        minIdle: 5 #最小空闲连接数
	        maxWait: 3000  #获取连接最大等待时间 ms  #default -1

3，@PullCache 查询，如缓存中存在，则从缓存取，例如：

    @PullCache(value = "Student", key="id", expires=10)
    @Override
    public Student query(String id) {
        return studentMapper.selectByPrimaryKey(id);
    }

4，@PushCache 保存，保存完毕，放入缓存例如：

    @PushCache(value = "Student", key = "student.id", expires = 10)
    @Override
    public void save(Student student) {
        return studentMapper.insert(student);
    }

5，@RemoveCache 删除，删除数据库同时删除缓存，例如：

    @RemoveCache(value = "Student", key="id")
    @Override
    public void delete(String id) {
        studentMapper.delete(id);
    }

    

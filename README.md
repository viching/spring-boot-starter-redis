# spring-boot-starter-redis

这个项目，建立在redis-cluster基础之上，实现功能如下：

通过注解的方式存储，查看，和删除实体，实际意义上实现redis集群效果。

使用方法:
1，引入spring-boot-starter-redis,当前项目可能存在重名情况，不适合在公共maven仓库生成，可以下载项目自行编译进入本地仓库后使用。
    <dependency>
			<groupId>com.viching.redis</groupId>
			<artifactId>spring-boot-starter-redis</artifactId>
			<version>1.0-SNAPSHOT</version>
		</dependency>
2，redis-cluster，环境配置和使用请参照官方文档
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

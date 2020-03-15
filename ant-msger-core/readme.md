需要在application.properties文件中配置以下信息：
```access transformers
# 协议映射
system.msger.protocol={'Jt808': 0x7e ,'AntIM':0x7a}


############## REDIS ##################################################
# Redis数据库索引（默认为0）
spring.redis.database=3
# Redis服务器地址
spring.redis.host=49.232.172.2
# Redis服务器连接端口
spring.redis.port=6379
# Redis服务器连接密码（默认为空）
spring.redis.password=
# 连接池最大连接数（使用负值表示没有限制）
spring.redis.pool.max-active=200
# 连接池最大阻塞等待时间（使用负值表示没有限制）
spring.redis.pool.max-wait=-1
# 连接池中的最大空闲连接
spring.redis.pool.max-idle=200
# 连接池中的最小空闲连接
spring.redis.pool.min-idle=0
# 连接超时时间（毫秒）
spring.redis.timeout=5000


##################自身用的redis
# 分段消息缓存
redis.key.msg.frag=com:antnest:msger:msg:frag1
```
# GMall
商城

## gmall-user 用户服务
端口号：8080
主流框架必有的包：controller，service，mapper，bean；mapper需要配置对应的xml映射文件(*Mapper.xml)。
发现的问题：
1. 在使用tk.mybatis这个库的时候，如果库版本(例如1.2.3)和Spring boot版本(例如2.2.2)差太多，会出现“java.util.NoSuchElementException: No value bound”的错误。将该库版本改为2.1.5就能解决。

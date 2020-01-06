# GMall
商城

# 开发过程中遇到的问题
1. Spring Boot 2.2.2-release与tk.mapper 1.2.3存在兼容问题，将tk.mapper改为2.1.5后解决。
2. Spring Boot 2.2.2-release与dubbo 2.6.0存在兼容问题.为了后续项目的开发，将Spring Boot的版本改为1.5.21-release。tk.mapper也改回1.2.3版本。
3. 使用dubbo后，想要收到心跳的话，就需要将service的实现类(XXXServiceImpl)中的@Service注解，改为dubbo包中的@Service注解。
4. bean中的类都要实现Serializable接口。

## gmall-user 用户服务
端口号：8080
主流框架必有的包：controller，service，mapper，bean；mapper需要配置对应的xml映射文件(*Mapper.xml)。
发现的问题：
1. 在使用tk.mybatis这个库的时候，如果库版本(例如1.2.3)和Spring boot版本(例如2.2.2)差太多，会出现“java.util.NoSuchElementException: No value bound”的错误。将该库版本改为2.1.5就能解决。

## gmall-parent
父工程。后续项目的都要依赖这个父项目。设置要使用的库的版本。

## gmall-api
大项目的通用接口，把各个子模块都需要用到的库抽取到这里。

## gmall-web-util
存放web前端需要用到的公共接口。

## gmall-service-util
存放service后端需要用到的公共接口。

## gmall-common-util
存放web前端和service后端的公共接口。

## 配置dubbo服务
在使用tomca部署dubbo的时候，如果使用的是context的标签，那么标签的docBase属性必须是**绝对路径**。

## gmall-user-service 用户服务的service层
端口8070

## gmall-user-web 用户服务的web层
端口8080
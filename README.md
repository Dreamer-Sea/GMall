# GMall
商城

# 各个模块的说明
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

## gmall-manage-service
端口8071

## gmall-manage-web
端口8081

## gmall-admin前端界面(由他人开发)
其中conf文件夹配置前端服务的ip，以及前端访问数据的后端的服务的ip地址。
dev.env.js 前端访问后端的数据服务的地址。
index.js 前端的服务器端口。
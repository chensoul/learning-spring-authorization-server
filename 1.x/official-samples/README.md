# spring-authorization-server 1.x 官方示例

spring-authorization-server 1.x 版本官方示例。代码地址：https://github.com/spring-projects/spring-authorization-server/tree/main/samples

## 说明

- default-authorizationserver: 默认配置的授权服务器，端口：9000
- demo-authorizationserver: 自定义配置的授权服务器，端口：9443
- demo-client: 客户端示例，端口：8080
- messages-resource: message 资源服务，端口：8090
- users-resource:  users 资源服务，端口：8091

## 测试

启动 Authorization Server：

```bash
cd demo-authorizationserver 
./gradlew bootRun
```

启动 Client：

```bash
cd demo-client 
./gradlew bootRun
```

启动资源服务：

```bash
cd messages-resource
./gradlew bootRun

cd users-resource
./gradlew bootRun
```

打开浏览器，访问：http://localhost:8080，输入 user1/password


## 参考文档

- https://github.com/spring-projects/spring-authorization-server/blob/main/samples/README.adoc

# spring-authorization-server 0.x 官方示例

spring-authorization-server 0.x
版本官方示例。代码地址：https://github.com/spring-projects/spring-authorization-server/tree/0.4.x/samples

## 说明

- default-authorizationserver-0: 默认配置的授权服务器，端口：9000
- custom-consent-authorizationserve: 自定义同意页面的授权服务器，端口：9000
- federated-identity-authorizationserver：联合身份授权服务器，端口：9000
- messages-client-0: messages 客户端，端口：8080
- messages-resource-0: message 资源服务，端口：8090

## 测试

启动 Authorization Server：

```bash
cd default-authorizationserver-0
mvn sprint-boot:run
```

启动 Client：

```bash
cd messages-client -0
mvn sprint-boot:run
```

启动资源服务：

```bash
cd messages-resource-0
mvn sprint-boot:run
```

打开浏览器，访问：http://localhost:8080，输入 user1/password

## 参考文档

- https://github.com/spring-projects/spring-authorization-server/blob/0.4.x/samples/README.adoc
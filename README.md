# learn-spring-authorization-server

spring-authorization-server 学习笔记

- [0.x](./0.x/README.md)：spring-authorization-server 0.x 分支
- [1.x](./1.x/README.md)：spring-authorization-server 1.x 分支


## 创建证书

生成 keystore：

```bash
keytool -genkeypair -alias authorizationserver -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore.p12 -storepass password -dname "CN=Web Server,OU=Unit,O=Organization,L=City,S=State,C=CN" -validity 3650
```

导出公钥文件：

```bash
keytool -list -rfc --keystore keystore.p12 -storepass password | openssl x509 -inform pem -pubkey > public.key
```

导出私钥文件：

```bash
keytool -importkeystore -srckeystore keystore.p12 -srcstorepass password -destkeystore private.p12 -deststoretype PKCS12 -deststorepass password -destkeypass password

#输入密码 storepass
openssl pkcs12 -in private.p12 -nodes -nocerts -out private.key
```

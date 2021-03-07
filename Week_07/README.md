学习笔记

## 插入100万记录
[order-creator/src/main/java/study/java1/week07/ordercreator/Main.java](order-creator/src/main/java/study/java1/week07/ordercreator/Main.java)
```java
//以下均采用最后commit的方式，均采用prepareStatement

//单线程，每次插入一条，耗时172秒
test.createOrderSync(orderSnPrefix, 1000000,1);

//单线程，每次插入1w条，耗时21秒
test.createOrderSync(orderSnPrefix, 1000000, 10000);

//10个线程，每个线程负责10w条，每次插入一条，耗时64秒
test.createOrderParallel(orderSnPrefix, 1000000, 1);

//10个线程，每个线程负责10w条，每次插入1w条，耗时7秒
test.createOrderParallel(orderSnPrefix, 1000000, 10000);

```

## 读写分离-动态切换数据源版本1.0
源码见：[readwrite-v1](readwrite-v1)

> AbstractRoutingDataSource + ThreadLocal + AOP


## 读写分离-数据库框架版本2.0

源码见：[readwrite-v2](readwrite-v2)

> 利用sharding-jdbc
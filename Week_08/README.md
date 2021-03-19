# week08作业

## 周三作业
2.（必做）设计对前面的订单表数据进行水平分库分表，拆分 2 个库，每个库 16 张表。并在新结构在演示常见的增删改查操作。

两个库，每个库16张表订单表
* 配置按商家id模2，确定库
* 按订单编号的hash值模16，确定表

sql中要带上分库键（seller_id）和分表键（order_sn）

利用sharding-jdbc实现

## 周日作业

2.（必做）基于 hmily TCC 或 ShardingSphere 的 Atomikos XA 实现一个简单的分布式事务应用 demo（二选一）

选用ShardingSphere的Atomikos XA。

```java
@Transactional
@ShardingTransactionType(TransactionType.XA)
public void createTwoOrder(){
    String orderSn = "20210305-23321231";
    Map<String, Object> orderInfo = new HashMap<>();
    orderInfo.put("orderSn", orderSn);
    orderInfo.put("sellerId", 1);
    
    createOrder(orderInfo);
//  if(true){
//  throw new RuntimeException("err");
//  }
            
    orderInfo.put("sellerId", 2);
    createOrder(orderInfo);
    
    if(true){
        throw new RuntimeException("err");
    }

    orderInfo.put("sellerId", 2);
    createOrder(orderInfo);
}
```

sellerId分别用1和2创建两条订单，根据分库规则，会被路由到两个库中。

经测试验证，分别在创建第一个订单后和创建第二个订单后抛出异常，数据库均未发生改变。如果把异常注释掉，两个库中的订单均插入成功。
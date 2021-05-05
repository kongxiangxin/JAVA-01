# week11
## 4.（必做）基于 Redis 封装分布式数据操作
###  在 Java 中实现一个简单的分布式锁
见[DistributedLocker](./src/main/java/study/java1/week11/lock/DistributedLocker.java)
* 基于RedisTemplate实现
* 加锁时利用ValueOperations的setIfAbsent方法
* 解锁时利用lua脚本判断调用方
* 加锁时间30s
* 提供lock和tryLock两个方法
* 未实现可重入锁
* 未实现锁自动续期

### 在 Java 中实现一个分布式计数器，模拟减库存。
见[StockCounter](./src/main/java/study/java1/week11/counter/StockCounter.java)
* 基于redis的decrby命令减库存
* 为避免value出现负数，减库存是用lua脚本，先判断库存中是否大于要减的库存

# week09

## 1. 改造自定义 RPC 的程序，提交到 GitHub
### 1.1 尝试将服务端写死查找接口实现类变成泛型和反射
```java
@Override
public <T> T resolve(Class<T> serviceClass) {
    return this.applicationContext.getBean(serviceClass);
}
```

> 可能没有理解题目要求，因为这样改貌似没有意义


### 1.2 尝试将客户端动态代理改成 AOP，添加异常处理；
> 同样也没有理解这道题目的要求，因为AOP的底层实现就可以是动态代理。
> 所以参考dubbo+spring的客户端使用方式，用注解的方式定义好服务，由BeanPostProcessor负责实例化代理，并set到注解描述的字段中。

core中定义注解ServiceRef
```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ServiceRef {
    String url();
}
```
core中实现自动装配ServiceRefAutoConfiguration
```java
@Bean
public BeanPostProcessor beanPostProcessor(){
    return new BeanPostProcessor(){
        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            Class<?> objClz;
            if (AopUtils.isAopProxy(bean)) {
                objClz = AopUtils.getTargetClass(bean);
            } else {
                objClz = bean.getClass();
            }
            Field[] fields = objClz.getDeclaredFields();
            for (Field field : fields) {
                ServiceRef serviceRef = field.getAnnotation(ServiceRef.class);
                if(serviceRef == null){
                    continue;
                }
                //todo: singleton
                Object proxy = Rpcfx.create(field.getType(), serviceRef.url());
                field.setAccessible(true);
                try {
                    field.set(bean, proxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
            return bean;
        }
    };
}

```

使用：
```java
@RestController
public class UserController {

    @ServiceRef(url = "http://localhost:8080")
    private UserService userService;

    @GetMapping("/user/{id}")
    public User findById(@PathVariable("id") Integer id) {
        return userService.findById(id);
    }
}
```

### 1.3 尝试使用 Netty+HTTP 作为 client 端传输方式。

[rpcfx/rpcfx-core/src/main/java/io/kimmking/rpcfx/client/NettyClient.java](rpcfx/rpcfx-core/src/main/java/io/kimmking/rpcfx/client/NettyClient.java)

## 2.结合 dubbo+hmily，实现一个 TCC 外汇交易处理
题目要求：
> * 用户 A 的美元账户和人民币账户都在 A 库，使用 1 美元兑换 7 人民币 ;
> * 用户 B 的美元账户和人民币账户都在 B 库，使用 7 人民币兑换 1 美元 ;
> * 设计账户表，冻结资产表，实现上述两个本地事务的分布式事务。

方案
* 美元账号和人民币账号均保存在account表，用货币类型（currency）来区分，即user_id + currency是唯一键，可以存储某个用户某个货币的余额
* 添加account_trade表存储每次兑换的交易，并以status枚举交易的状态
* 创建两个数据库，用shardingsphere做分库
* dubbo服务提供batchExchange方法，接受多个用户的兑换请求，利用hmily的tcc保证这些请求要么都成功，要么都失败。
* 源码见[hmily-demo-currency](hmily-demo-currency)
package study.java1.week11;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import study.java1.week11.counter.StockCounter;
import study.java1.week11.lock.DistributedLocker;

import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private DistributedLocker locker;
    @Autowired
    private StockCounter counter;

    @RequestMapping("/lock")
    public void lockTest(){
        final String lockKey = "sth-to-lock";
        Runnable runnable = new Runnable() {
            public void run() {
                String requestId = UUID.randomUUID().toString();
                try{
                    boolean ret = locker.tryLock(lockKey, requestId);
                    System.out.println("线程" + Thread.currentThread().getName() + "是否拿到锁:" + ret);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }finally {
                    locker.unlock(lockKey, requestId);
                }
            }
        };
        for (int i = 0; i < 40; i ++){
            new Thread(runnable).start();
        }
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping("stock")
    public void test(){
        final String stockKey = "sth-to-stock";
        //初始化库存100
        counter.init(stockKey, 100L);

        final Random random = new Random(1);
        Runnable runnable = new Runnable() {
            public void run() {
                int num = random.nextInt(10) + 1;
                long ret = counter.decrement(stockKey, num);
                if(ret < 0){
                    System.out.println("线程" + Thread.currentThread().getName() + "没有取到库存，请求:" + num);
                }else{
                    System.out.println("线程" + Thread.currentThread().getName() + "成功扣减" + num + "个库存");
                }
            }
        };
        for (int i = 0; i < 40; i ++){
            new Thread(runnable).start();
        }
    }

}

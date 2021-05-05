package study.java1.week11.lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.DefaultScriptExecutor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Component
public class DistributedLocker {
    //10s过期
    private final long KEY_TIMEOUT = 30000;
    private RedisTemplate redisTemplate;
    private DefaultScriptExecutor<String> scriptExecutor;
    private DefaultRedisScript<Boolean> redisScript;

    /**
     * 获取锁
     * @param lockKey
     * @param requestId
     * @param timeout 超时时间，毫秒。如果是-1，会一直等，直到拿到锁
     * @return
     */
    public boolean lock(String lockKey, String requestId, int timeout){
        long startTime = System.currentTimeMillis();
        while (true){
            Boolean result = redisTemplate.opsForValue().setIfAbsent(lockKey, requestId, KEY_TIMEOUT, TimeUnit.MILLISECONDS);
            if(Boolean.TRUE.equals(result)){
                return true;
            }
            long currentTime = System.currentTimeMillis();
            if(currentTime - startTime > timeout && timeout != -1){
                //超时了， timeout是-1表示用不超时，拿不到锁就一直等
                return false;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 尝试获取锁，拿不到锁的话直接返回false，不等待
     * @param lockKey
     * @param requestId
     * @return
     */
    public boolean tryLock(String lockKey, String requestId){
        return lock(lockKey, requestId, 0);
    }

    public boolean unlock(String lockKey, String requestId){
        return scriptExecutor.execute(redisScript, Collections.singletonList(lockKey), requestId);
    }


    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        String luaScript = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
        redisScript = new DefaultRedisScript<Boolean>(luaScript);
        redisScript.setResultType(Boolean.class);
        scriptExecutor = new DefaultScriptExecutor<String>(redisTemplate);
    }
}

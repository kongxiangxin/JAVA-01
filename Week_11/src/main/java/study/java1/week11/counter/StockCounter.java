package study.java1.week11.counter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.DefaultScriptExecutor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Component
public class StockCounter {

    private RedisTemplate redisTemplate;
    private DefaultScriptExecutor<String> scriptExecutor;
    private DefaultRedisScript<Long> redisScript;


    public boolean init(String key, Long total){
        redisTemplate.opsForValue().set(key, total);
        return true;
    }

    public long decrement(String key, long num){
        long ret = scriptExecutor.execute(redisScript, Collections.singletonList(key), num);
        return ret;
    }

    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        String luaScript =
                "local stock = tonumber(redis.call('get', KEYS[1]));\n" +
                "local num = tonumber(ARGV[1]);\n" +
                "if(stock >= num) then\n" +
                "\treturn redis.call('decrby', KEYS[1], num);\n" +
                "end;\n" +
                "-- 库存不足\n" +
                "return -1;";
        redisScript = new DefaultRedisScript<Long>(luaScript);
        redisScript.setResultType(Long.class);
        scriptExecutor = new DefaultScriptExecutor<String>(redisTemplate);
    }
}

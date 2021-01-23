学习笔记

java -Xms5g -Xmx5g -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis
java -Xms8g -Xmx8g GCLogAnalysis

java -Xms4g -Xmx4g -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis

# GC（周三作业）
MacOS bigsur, 4核16G


## gc日志解读（并行gc为例）
java -Xms512m -Xmx512m -Xloggc:gc.demo.log -XX:+PrintHeapAtGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis

1.第1次 YGC
```
{Heap before GC invocations=1 (full 0):
 PSYoungGen      total 153088K, used 131329K [0x00000007b5580000, 0x00000007c0000000, 0x00000007c0000000)
  eden space 131584K, 99% used [0x00000007b5580000,0x00000007bd5c0630,0x00000007bd600000)
  from space 21504K, 0% used [0x00000007beb00000,0x00000007beb00000,0x00000007c0000000)
  to   space 21504K, 0% used [0x00000007bd600000,0x00000007bd600000,0x00000007beb00000)
 ParOldGen       total 349696K, used 0K [0x00000007a0000000, 0x00000007b5580000, 0x00000007b5580000)
  object space 349696K, 0% used [0x00000007a0000000,0x00000007a0000000,0x00000007b5580000)
 Metaspace       used 2601K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 285K, capacity 386K, committed 512K, reserved 1048576K
2021-01-22T16:18:19.524-0800: 0.146: [GC (Allocation Failure) [PSYoungGen: 131329K->21501K(153088K)] 131329K->40432K(502784K), 0.0155778 secs] [Times: user=0.04 sys=0.07, real=0.02 secs] 
Heap after GC invocations=1 (full 0):
 PSYoungGen      total 153088K, used 21501K [0x00000007b5580000, 0x00000007c0000000, 0x00000007c0000000)
  eden space 131584K, 0% used [0x00000007b5580000,0x00000007b5580000,0x00000007bd600000)
  from space 21504K, 99% used [0x00000007bd600000,0x00000007beaff4c8,0x00000007beb00000)
  to   space 21504K, 0% used [0x00000007beb00000,0x00000007beb00000,0x00000007c0000000)
 ParOldGen       total 349696K, used 18931K [0x00000007a0000000, 0x00000007b5580000, 0x00000007b5580000)
  object space 349696K, 5% used [0x00000007a0000000,0x00000007a127cd28,0x00000007b5580000)
 Metaspace       used 2601K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 285K, capacity 386K, committed 512K, reserved 1048576K
} 
```
young区大小131M，GC后变为21M
清理了 131 - 21 = 110M。整个堆从 131M 变为 40M，减少了 131 - 40 = 91M，有20M进入老年代

精确一点：
* 131329 - 21501 = 109828
* 131329 - 40432 =  90897
* 109828 - 90897 = 18931
这和gclog中是匹配的


2.第2次 YGC
```
2021-01-22T16:18:19.563-0800: 0.185: [GC (Allocation Failure) [PSYoungGen: 153085K->21491K(153088K)] 172016K->85948K(502784K), 0.0272583 secs] [Times: user=0.04 sys=0.11, real=0.03 secs] 
```
young区大小 153085 - 21491 = 131,594。整个堆 172016 - 85948 = 86,068，有45M进入老年代

3.第6次 YGC
```
2021-01-22T16:18:19.695-0800: 0.317: [GC (Allocation Failure) [PSYoungGen: 153083K->21499K(80384K)] 341428K->248078K(430080K), 0.0158856 secs] [Times: user=0.03 sys=0.07, real=0.02 secs] 
```
young区大小变成80384K、堆大小变成430080K了！（老年代大小没变）
应该是开启了自适应（UseAdaptiveSizePolicy默认开启），触发了某种规则，自动调小了年轻代

4.第7次 YGC
```
2021-01-22T16:18:19.717-0800: 0.339: [GC (Allocation Failure) [PSYoungGen: 80101K->35521K(116736K)] 306679K->266704K(466432K), 0.0033238 secs] [Times: user=0.01 sys=0.00, real=0.01 secs] 
```
自适应应该是起了效果了，上一次耗时158毫秒，本次耗时3毫秒。 不过年轻代又调大了一些，调成了116M

5.第12次 FGC
```
2021-01-22T16:18:19.787-0800: 0.409: [Full GC (Ergonomics) [PSYoungGen: 18668K->0K(116736K)] [ParOldGen: 312574K->235626K(349696K)] 331242K->235626K(466432K), [Metaspace: 2601K->2601K(1056768K)], 0.0189470 secs] [Times: user=0.12 sys=0.00, real=0.02 secs] 
```
发生第一次FGC了，发生前old区已用312574K（总共349696K），使用率89%。
FGC后，年轻代使用量变为0，老年代降为235M，使用率 235M/349M = 67%
整个堆已使用大小变为235M。耗时18毫秒

java -Xmx128m  -Xloggc:gc.demo.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis

## 不同GC、不同内存对吞吐量和响应时间的对比

### 串行GC
示例命令行
```
java -Xloggc:gc.demo.log -Xms1g -Xmx1g -XX:-UseAdaptiveSizePolicy -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+UseSerialGC GCLogAnalysis
```

|  堆大小    | 创建对象个数 | YGC次数 | FGC次数 | GC总耗时   | 单次GC最大耗时 | 平均gc耗时  |
|  ----     | ----       |  ----   | ----   | ----      | ----         |   ---       |
| 512M      | 11,225     |  19     | 2      | 0.306     | 0.036        |  0.015      |
| 1G        | 13,664     |  13     | 0      | 0.468     | 0.065        |  0.036     |
| 2G        | 12,657     |  6      | 0      | 0.498     | 0.101        |  0.083      |
| 4G        | 09,257     |  2      | 0      | 0.288     | 0.163        |  0.144      |
| 8G        | 07,339     |  0      | 0      | --        | --           |  --         |


> * 堆内存不是越高越好，会增加单次GC的耗时，在GC发生时会增大系统的响应时间
> * 1s内，gc总耗时和创建对象的个数没有关系,8g的时候没有gc但对象最少；4g耗时比2g少，但对象不如4g的多
> * 尝试关闭mac的swap没关成，找了一台linux，关掉swap，也是类似的结果
> * 如果修改GCLogAnalysis.java，拉长timeoutMillis（比如调成30s），生成的对象个数会跟随着内存增大而变多，不知道在大堆的情况下，jvm为对象开辟内存空间的时候会不会有什么额外的消耗？

### 并行GC
示例命令行
```
java -Xloggc:gc.demo.log -Xms8g -Xmx8g -XX:-UseAdaptiveSizePolicy -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+UseParallelGC GCLogAnalysis
```

|  堆大小    | 创建对象个数 | YGC次数 | FGC次数 | GC总耗时   | 单次GC最大耗时 | 平均gc耗时  |
|  ----     | ----       |  ----   | ----   | ----      | ----         |   ---       |
| 512M      | 13,663     |  13     | 17     | 0.536     | 0.027        |  0.018      |
| 1G        | 16,895     |  17     | 2      | 0.405     | 0.045        |  0.021      |
| 2G        | 15,692     |  7      | 0      | 0.346     | 0.069        |  0.049      |
| 4G        | 11,883     |  3      | 0      | 0.231     | 0.101        |  0.077      |
| 8G        | 06,896     |  0      | 0      | --        | --           |  --         |


> * 并行GC比串行GC每次GC时间耗时少，但51m和1g的gc次数变多了
> * 其他表现和串行GC一样

### CMS GC
示例命令行
```
java -Xloggc:gc.demo2.log -Xms8g -Xmx8g -XX:-UseAdaptiveSizePolicy -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+UseConcMarkSweepGC GCLogAnalysis
```

|  堆大小    | 创建对象个数 | YGC次数 | FGC次数(CMS) 
|  ----     | ----       |  ----   | ----        
| 512M      | 12,122     |  23     | 13          
| 1G        | 12,957     |  12     | 3           
| 2G        | 12,271     |  6      | 1           
| 4G        | 12,407     |  6      | 0           
| 8G        | 12,282     |  6      | 0      

> * CMS的YGC和并行、串行GC一样
> * 8G的堆内存下，发生了YGC，但生成对象数量不像串行、并行那样少了，几个堆大小下，生成对象数量差不多     

### G1 GC
示例命令行
```
java -Xloggc:gc.demo.log -Xms1g -Xmx1g -XX:-UseAdaptiveSizePolicy -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+UseG1GC GCLogAnalysis
```

|  堆大小    | 创建对象个数
|  ----     | ----       
| 512M      | 11,946     
| 1G        | 14,478   
| 2G        | 13,580     
| 4G        | 12,023     
| 8G        | 15,341     

> 除了512M稍微差一点，其他都差不多，而且随着内存增大，有递增趋势

### 结论和问题
> 随内存增大，G1优势明显
> 程序运行生成的对象个数，和gc总耗时没关系，多次测试出总耗时最少但吞吐最少的结果，不知为何老师在讲课时关注了总时长

# 用 HttpClient 或 OkHttp 访问 http://localhost:8801（周日作业）
见./httpclient/src/main/java/HttpClient.java

```java
public static void main(String[] args) throws IOException {
    HttpGet httpGet = new HttpGet("http://localhost:8801");
    CloseableHttpClient client = HttpClients.createDefault();
    CloseableHttpResponse response = client.execute(httpGet);
    HttpEntity entity = response.getEntity();
    if(entity != null){
        String result = EntityUtils.toString(entity,Charset.forName("utf-8"));
        System.out.println("response: " + result);
    }
}
```
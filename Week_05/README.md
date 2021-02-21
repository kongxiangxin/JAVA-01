#（必做）写代码实现 Spring Bean 的装配，方式越多越好（XML、Annotation 都可以）
springapp/AppContextTest.java
* xmlAppContextTest() xml中常规配置
* customSchemaTest() 自定义xml schema
* annotationTest() 利用@Component、@AutoWired等注解
* codeConfigTest() 利用@Configuration、@Bean等注解

springapp/SpringBootAutoConfigTest.java
* 利用META-INF/spring.factories

#（选做）实现一个 Spring XML 自定义配置，配置一组 Bean
见springapp/AppContextTest.java中的customSchemaTest

#（必做）给前面课程提供的 Student/Klass/School 实现自动配置和 Starter。
见school-starter/SchoolConfiguration和springapp/SpringBootAutoConfigTest.java

# （必做）研究一下 JDBC 接口和数据库连接池，掌握它们的设计和用法
见jdbc目录
package study.java1.week05.schema;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import study.java1.week05.pojo.Student;

public class StudentBeanDefinitionParser implements BeanDefinitionParser {

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(Student.class);
        beanDefinition.setLazyInit(false);
        beanDefinition.getPropertyValues().add("name", element.getAttribute("name"));
        beanDefinition.getPropertyValues().add("id", element.getAttribute("id"));
        BeanDefinitionRegistry beanDefinitionRegistry = parserContext.getRegistry();
        beanDefinitionRegistry.registerBeanDefinition(element.getAttribute("id"), beanDefinition);//注册bean到BeanDefinitionRegistry中
        return beanDefinition;
    }
}

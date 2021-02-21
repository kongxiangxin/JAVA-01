package study.java1.week05.schema;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class SchoolNamespaceHandler extends NamespaceHandlerSupport {
    @Override
    public void init() {
        registerBeanDefinitionParser("student", new StudentBeanDefinitionParser());
        registerBeanDefinitionParser("klass", new KlassBeanDefinitionParser());
        registerBeanDefinitionParser("school", new SchoolBeanDefinitionParser());
    }
}

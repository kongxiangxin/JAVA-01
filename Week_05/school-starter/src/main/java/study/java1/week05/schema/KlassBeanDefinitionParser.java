package study.java1.week05.schema;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;
import study.java1.week05.pojo.Klass;

import java.util.List;

public class KlassBeanDefinitionParser implements BeanDefinitionParser {

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(Klass.class);
        beanDefinition.setLazyInit(false);

        BeanDefinitionRegistry beanDefinitionRegistry = parserContext.getRegistry();
        beanDefinitionRegistry.registerBeanDefinition(element.getAttribute("id"), beanDefinition);//注册bean到BeanDefinitionRegistry中

        List<Element> childElements = DomUtils.getChildElementsByTagName(element, "student");
        if (childElements.size() > 0) {
            parseChildComponents(childElements, beanDefinition, parserContext);
        }

        return beanDefinition;
    }

    private static void parseChildComponents(List<Element> childElements, RootBeanDefinition klass, ParserContext parserContext) {
        ManagedList<BeanDefinition> children = new ManagedList<BeanDefinition>(childElements.size());

        for (Element element : childElements) {
            StudentBeanDefinitionParser studentBeanDefinitionParser = new StudentBeanDefinitionParser();
            BeanDefinition child = studentBeanDefinitionParser.parse(element, parserContext);
            children.add(child);
        }

        klass.getPropertyValues().add("students", children);
    }
}

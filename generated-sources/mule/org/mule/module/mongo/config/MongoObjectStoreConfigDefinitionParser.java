
package org.mule.module.mongo.config;

import org.apache.commons.lang.StringUtils;
import org.mule.api.lifecycle.Disposable;
import org.mule.api.lifecycle.Initialisable;
import org.mule.config.spring.MuleHierarchicalBeanDefinitionParserDelegate;
import org.mule.config.spring.parsers.generic.AutoIdUtils;
import org.mule.module.mongo.adapters.MongoObjectStoreLifecycleAdapter;
import org.mule.util.TemplateParser;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class MongoObjectStoreConfigDefinitionParser
    implements BeanDefinitionParser
{

    /**
     * Mule Pattern Info
     * 
     */
    private TemplateParser.PatternInfo patternInfo;

    public MongoObjectStoreConfigDefinitionParser() {
        patternInfo = TemplateParser.createMuleStyleParser().getStyle();
    }

    public BeanDefinition parse(Element element, ParserContext parserContent) {
        String name = element.getAttribute("name");
        if ((name == null)||StringUtils.isBlank(name)) {
            element.setAttribute("name", AutoIdUtils.getUniqueName(element, "mule-bean"));
        }
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(MongoObjectStoreLifecycleAdapter.class.getName());
        if (Initialisable.class.isAssignableFrom(MongoObjectStoreLifecycleAdapter.class)) {
            builder.setInitMethodName(Initialisable.PHASE_NAME);
        }
        if (Disposable.class.isAssignableFrom(MongoObjectStoreLifecycleAdapter.class)) {
            builder.setDestroyMethodName(Disposable.PHASE_NAME);
        }
        if ((element.getAttribute("host")!= null)&&(!StringUtils.isBlank(element.getAttribute("host")))) {
            builder.addPropertyValue("host", element.getAttribute("host"));
        }
        if ((element.getAttribute("port")!= null)&&(!StringUtils.isBlank(element.getAttribute("port")))) {
            builder.addPropertyValue("port", element.getAttribute("port"));
        }
        if ((element.getAttribute("database")!= null)&&(!StringUtils.isBlank(element.getAttribute("database")))) {
            builder.addPropertyValue("database", element.getAttribute("database"));
        }
        if ((element.getAttribute("username")!= null)&&(!StringUtils.isBlank(element.getAttribute("username")))) {
            builder.addPropertyValue("username", element.getAttribute("username"));
        }
        if ((element.getAttribute("password")!= null)&&(!StringUtils.isBlank(element.getAttribute("password")))) {
            builder.addPropertyValue("password", element.getAttribute("password"));
        }
        if (element.hasAttribute("writeConcern")) {
            builder.addPropertyValue("writeConcern", element.getAttribute("writeConcern"));
        }
        BeanDefinition definition = builder.getBeanDefinition();
        definition.setAttribute(MuleHierarchicalBeanDefinitionParserDelegate.MULE_NO_RECURSE, Boolean.TRUE);
        return definition;
    }

}

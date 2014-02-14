
package org.mule.module.mongo.config;

import javax.annotation.Generated;
import org.mule.module.mongo.config.AbstractDefinitionParser.ParseDelegate;
import org.mule.module.mongo.processors.RemoveUsingQueryMapMessageProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

@Generated(value = "Mule DevKit Version 3.4.0", date = "2013-07-03T03:36:05-05:00", comments = "Build UNKNOWN_BUILDNUMBER")
public class RemoveUsingQueryMapDefinitionParser
    extends AbstractDefinitionParser
{


    public BeanDefinition parse(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(RemoveUsingQueryMapMessageProcessor.class.getName());
        builder.setScope(BeanDefinition.SCOPE_PROTOTYPE);
        parseConfigRef(element, builder);
        parseProperty(builder, element, "collection", "collection");
        parseMapAndSetProperty(element, builder, "queryAttributes", "query-attributes", "query-attribute", new ParseDelegate<String>() {


            public String parse(Element element) {
                return element.getTextContent();
            }

        }
        );
        parseProperty(builder, element, "writeConcern", "writeConcern");
        parseProperty(builder, element, "username", "username");
        parseProperty(builder, element, "password", "password");
        parseProperty(builder, element, "database", "database");
        BeanDefinition definition = builder.getBeanDefinition();
        setNoRecurseOnDefinition(definition);
        attachProcessorDefinition(parserContext, definition);
        return definition;
    }

}


package org.mule.module.mongo.config.spring;

import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.mule.config.spring.MuleHierarchicalBeanDefinitionParserDelegate;
import org.mule.config.spring.util.SpringXMLUtils;
import org.mule.module.mongo.config.SaveObjectFromMapMessageProcessor;
import org.mule.util.TemplateParser;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

public class SaveObjectFromMapDefinitionParser
    implements BeanDefinitionParser
{

    /**
     * Mule Pattern Info
     * 
     */
    private TemplateParser.PatternInfo patternInfo;

    public SaveObjectFromMapDefinitionParser() {
        patternInfo = TemplateParser.createMuleStyleParser().getStyle();
    }

    public BeanDefinition parse(Element element, ParserContext parserContent) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(SaveObjectFromMapMessageProcessor.class.getName());
        String configRef = element.getAttribute("config-ref");
        if ((configRef!= null)&&(!StringUtils.isBlank(configRef))) {
            builder.addPropertyValue("moduleObject", configRef);
        }
        if ((element.getAttribute("collection")!= null)&&(!StringUtils.isBlank(element.getAttribute("collection")))) {
            builder.addPropertyValue("collection", element.getAttribute("collection"));
        }
        Element elementAttributesListElement = null;
        elementAttributesListElement = DomUtils.getChildElementByTagName(element, "element-attributes");
        List<Element> elementAttributesListChilds = null;
        if (elementAttributesListElement!= null) {
            String elementAttributesRef = elementAttributesListElement.getAttribute("ref");
            if ((elementAttributesRef!= null)&&(!StringUtils.isBlank(elementAttributesRef))) {
                if ((!elementAttributesRef.startsWith(patternInfo.getPrefix()))&&(!elementAttributesRef.endsWith(patternInfo.getSuffix()))) {
                    builder.addPropertyValue("elementAttributes", new RuntimeBeanReference(elementAttributesRef));
                } else {
                    builder.addPropertyValue("elementAttributes", elementAttributesRef);
                }
            } else {
                ManagedMap elementAttributes = new ManagedMap();
                elementAttributesListChilds = DomUtils.getChildElementsByTagName(elementAttributesListElement, "element-attribute");
                if (elementAttributesListChilds!= null) {
                    if (elementAttributesListChilds.size() == 0) {
                        elementAttributesListChilds = DomUtils.getChildElements(elementAttributesListElement);
                    }
                    for (Element elementAttributesChild: elementAttributesListChilds) {
                        String elementAttributesValueRef = elementAttributesChild.getAttribute("value-ref");
                        String elementAttributesKeyRef = elementAttributesChild.getAttribute("key-ref");
                        Object valueObject = null;
                        Object keyObject = null;
                        if ((elementAttributesValueRef!= null)&&(!StringUtils.isBlank(elementAttributesValueRef))) {
                            valueObject = new RuntimeBeanReference(elementAttributesValueRef);
                        } else {
                            valueObject = elementAttributesChild.getTextContent();
                        }
                        if ((elementAttributesKeyRef!= null)&&(!StringUtils.isBlank(elementAttributesKeyRef))) {
                            keyObject = new RuntimeBeanReference(elementAttributesKeyRef);
                        } else {
                            keyObject = elementAttributesChild.getAttribute("key");
                        }
                        if ((keyObject == null)||((keyObject instanceof String)&&StringUtils.isBlank(((String) keyObject)))) {
                            keyObject = elementAttributesChild.getTagName();
                        }
                        elementAttributes.put(keyObject, valueObject);
                    }
                }
                builder.addPropertyValue("elementAttributes", elementAttributes);
            }
        }
        if (element.hasAttribute("writeConcern")) {
            builder.addPropertyValue("writeConcern", element.getAttribute("writeConcern"));
        }
        if ((element.getAttribute("retryMax")!= null)&&(!StringUtils.isBlank(element.getAttribute("retryMax")))) {
            builder.addPropertyValue("retryMax", element.getAttribute("retryMax"));
        }
        if ((element.getAttribute("username")!= null)&&(!StringUtils.isBlank(element.getAttribute("username")))) {
            builder.addPropertyValue("username", element.getAttribute("username"));
        }
        if ((element.getAttribute("password")!= null)&&(!StringUtils.isBlank(element.getAttribute("password")))) {
            builder.addPropertyValue("password", element.getAttribute("password"));
        }
        BeanDefinition definition = builder.getBeanDefinition();
        definition.setAttribute(MuleHierarchicalBeanDefinitionParserDelegate.MULE_NO_RECURSE, Boolean.TRUE);
        MutablePropertyValues propertyValues = parserContent.getContainingBeanDefinition().getPropertyValues();
        if (parserContent.getContainingBeanDefinition().getBeanClassName().equals("org.mule.config.spring.factories.PollingMessageSourceFactoryBean")) {
            propertyValues.addPropertyValue("messageProcessor", definition);
        } else {
            if (parserContent.getContainingBeanDefinition().getBeanClassName().equals("org.mule.enricher.MessageEnricher")) {
                propertyValues.addPropertyValue("enrichmentMessageProcessor", definition);
            } else {
                PropertyValue messageProcessors = propertyValues.getPropertyValue("messageProcessors");
                if ((messageProcessors == null)||(messageProcessors.getValue() == null)) {
                    propertyValues.addPropertyValue("messageProcessors", new ManagedList());
                }
                List listMessageProcessors = ((List) propertyValues.getPropertyValue("messageProcessors").getValue());
                listMessageProcessors.add(definition);
            }
        }
        return definition;
    }

    protected String getAttributeValue(Element element, String attributeName) {
        if (!StringUtils.isEmpty(element.getAttribute(attributeName))) {
            return element.getAttribute(attributeName);
        }
        return null;
    }

    private String generateChildBeanName(Element element) {
        String id = SpringXMLUtils.getNameOrId(element);
        if (StringUtils.isBlank(id)) {
            String parentId = SpringXMLUtils.getNameOrId(((Element) element.getParentNode()));
            return ((("."+ parentId)+":")+ element.getLocalName());
        } else {
            return id;
        }
    }

}

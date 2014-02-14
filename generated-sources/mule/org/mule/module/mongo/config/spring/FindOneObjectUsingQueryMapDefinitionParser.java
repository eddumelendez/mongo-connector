
package org.mule.module.mongo.config.spring;

import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.mule.config.spring.MuleHierarchicalBeanDefinitionParserDelegate;
import org.mule.config.spring.util.SpringXMLUtils;
import org.mule.module.mongo.config.FindOneObjectUsingQueryMapMessageProcessor;
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

public class FindOneObjectUsingQueryMapDefinitionParser
    implements BeanDefinitionParser
{

    /**
     * Mule Pattern Info
     * 
     */
    private TemplateParser.PatternInfo patternInfo;

    public FindOneObjectUsingQueryMapDefinitionParser() {
        patternInfo = TemplateParser.createMuleStyleParser().getStyle();
    }

    public BeanDefinition parse(Element element, ParserContext parserContent) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(FindOneObjectUsingQueryMapMessageProcessor.class.getName());
        String configRef = element.getAttribute("config-ref");
        if ((configRef!= null)&&(!StringUtils.isBlank(configRef))) {
            builder.addPropertyValue("moduleObject", configRef);
        }
        if ((element.getAttribute("collection")!= null)&&(!StringUtils.isBlank(element.getAttribute("collection")))) {
            builder.addPropertyValue("collection", element.getAttribute("collection"));
        }
        Element queryAttributesListElement = null;
        queryAttributesListElement = DomUtils.getChildElementByTagName(element, "query-attributes");
        List<Element> queryAttributesListChilds = null;
        if (queryAttributesListElement!= null) {
            String queryAttributesRef = queryAttributesListElement.getAttribute("ref");
            if ((queryAttributesRef!= null)&&(!StringUtils.isBlank(queryAttributesRef))) {
                if ((!queryAttributesRef.startsWith(patternInfo.getPrefix()))&&(!queryAttributesRef.endsWith(patternInfo.getSuffix()))) {
                    builder.addPropertyValue("queryAttributes", new RuntimeBeanReference(queryAttributesRef));
                } else {
                    builder.addPropertyValue("queryAttributes", queryAttributesRef);
                }
            } else {
                ManagedMap queryAttributes = new ManagedMap();
                queryAttributesListChilds = DomUtils.getChildElementsByTagName(queryAttributesListElement, "query-attribute");
                if (queryAttributesListChilds!= null) {
                    if (queryAttributesListChilds.size() == 0) {
                        queryAttributesListChilds = DomUtils.getChildElements(queryAttributesListElement);
                    }
                    for (Element queryAttributesChild: queryAttributesListChilds) {
                        String queryAttributesValueRef = queryAttributesChild.getAttribute("value-ref");
                        String queryAttributesKeyRef = queryAttributesChild.getAttribute("key-ref");
                        Object valueObject = null;
                        Object keyObject = null;
                        if ((queryAttributesValueRef!= null)&&(!StringUtils.isBlank(queryAttributesValueRef))) {
                            valueObject = new RuntimeBeanReference(queryAttributesValueRef);
                        } else {
                            valueObject = queryAttributesChild.getTextContent();
                        }
                        if ((queryAttributesKeyRef!= null)&&(!StringUtils.isBlank(queryAttributesKeyRef))) {
                            keyObject = new RuntimeBeanReference(queryAttributesKeyRef);
                        } else {
                            keyObject = queryAttributesChild.getAttribute("key");
                        }
                        if ((keyObject == null)||((keyObject instanceof String)&&StringUtils.isBlank(((String) keyObject)))) {
                            keyObject = queryAttributesChild.getTagName();
                        }
                        queryAttributes.put(keyObject, valueObject);
                    }
                }
                builder.addPropertyValue("queryAttributes", queryAttributes);
            }
        }
        Element fieldsListElement = null;
        fieldsListElement = DomUtils.getChildElementByTagName(element, "fields");
        List<Element> fieldsListChilds = null;
        if (fieldsListElement!= null) {
            String fieldsRef = fieldsListElement.getAttribute("ref");
            if ((fieldsRef!= null)&&(!StringUtils.isBlank(fieldsRef))) {
                if ((!fieldsRef.startsWith(patternInfo.getPrefix()))&&(!fieldsRef.endsWith(patternInfo.getSuffix()))) {
                    builder.addPropertyValue("fields", new RuntimeBeanReference(fieldsRef));
                } else {
                    builder.addPropertyValue("fields", fieldsRef);
                }
            } else {
                ManagedList fields = new ManagedList();
                fieldsListChilds = DomUtils.getChildElementsByTagName(fieldsListElement, "field");
                if (fieldsListChilds!= null) {
                    for (Element fieldsChild: fieldsListChilds) {
                        String valueRef = fieldsChild.getAttribute("value-ref");
                        if ((valueRef!= null)&&(!StringUtils.isBlank(valueRef))) {
                            fields.add(new RuntimeBeanReference(valueRef));
                        } else {
                            fields.add(fieldsChild.getTextContent());
                        }
                    }
                }
                builder.addPropertyValue("fields", fields);
            }
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

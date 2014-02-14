
package org.mule.module.mongo.config;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import org.mule.DefaultMuleEvent;
import org.mule.DefaultMuleMessage;
import org.mule.api.MessagingException;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.construct.FlowConstruct;
import org.mule.api.construct.FlowConstructAware;
import org.mule.api.context.MuleContextAware;
import org.mule.api.expression.ExpressionManager;
import org.mule.api.lifecycle.Disposable;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.lifecycle.Startable;
import org.mule.api.lifecycle.Stoppable;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.registry.RegistrationException;
import org.mule.api.transformer.DataType;
import org.mule.api.transformer.Transformer;
import org.mule.api.transformer.TransformerException;
import org.mule.config.i18n.CoreMessages;
import org.mule.config.i18n.MessageFactory;
import org.mule.module.mongo.MongoCloudConnector;
import org.mule.module.mongo.MongoSession;
import org.mule.transformer.TransformerTemplate;
import org.mule.transformer.types.DataTypeFactory;
import org.mule.transport.NullPayload;
import org.mule.util.TemplateParser;


/**
 * CountObjectsUsingMapQueryMessageProcessor invokes the {@link org.mule.module.mongo.MongoCloudConnector#countObjectsUsingMapQuery(org.mule.module.mongo.MongoSession, java.lang.String, java.util.Map)} method in {@link MongoCloudConnector }. For each argument there is a field in this processor to match it.  Before invoking the actual method the processor will evaluate and transform where possible to the expected argument type.
 * 
 */
public class CountObjectsUsingMapQueryMessageProcessor
    implements FlowConstructAware, MuleContextAware, Disposable, Initialisable, Startable, Stoppable, MessageProcessor
{

    private Object collection;
    private String _collectionType;
    private Object queryAttributes;
    private Map<String, String> _queryAttributesType;
    private Object username;
    private String _usernameType;
    private Object password;
    private String _passwordType;
    /**
     * Module object
     * 
     */
    private MongoCloudConnectorSessionManagerAdapter moduleObject;
    /**
     * Mule Context
     * 
     */
    private MuleContext muleContext;
    /**
     * Mule Expression Manager
     * 
     */
    private ExpressionManager expressionManager;
    /**
     * Mule Pattern Info
     * 
     */
    private TemplateParser.PatternInfo patternInfo;
    /**
     * Flow construct
     * 
     */
    private FlowConstruct flowConstruct;

    /**
     * Obtains the expression manager from the Mule context and initialises the connector. If a target object  has not been set already it will search the Mule registry for a default one.
     * 
     * @throws InitialisationException
     */
    public void initialise()
        throws InitialisationException
    {
        expressionManager = muleContext.getExpressionManager();
        patternInfo = TemplateParser.createMuleStyleParser().getStyle();
        if (moduleObject == null) {
            try {
                moduleObject = muleContext.getRegistry().lookupObject(MongoCloudConnectorSessionManagerAdapter.class);
                if (moduleObject == null) {
                    throw new InitialisationException(MessageFactory.createStaticMessage("Cannot find object"), this);
                }
            } catch (RegistrationException e) {
                throw new InitialisationException(CoreMessages.initialisationFailure("org.mule.module.mongo.config.MongoCloudConnectorSessionManagerAdapter"), e, this);
            }
        }
    }

    public void start()
        throws MuleException
    {
    }

    public void stop()
        throws MuleException
    {
    }

    public void dispose() {
    }

    /**
     * Set the Mule context
     * 
     * @param context Mule context to set
     */
    public void setMuleContext(MuleContext context) {
        this.muleContext = context;
    }

    /**
     * Sets flow construct
     * 
     * @param flowConstruct Flow construct to set
     */
    public void setFlowConstruct(FlowConstruct flowConstruct) {
        this.flowConstruct = flowConstruct;
    }

    /**
     * Sets the instance of the object under which the processor will execute
     * 
     * @param moduleObject Instace of the module
     */
    public void setModuleObject(MongoCloudConnectorSessionManagerAdapter moduleObject) {
        this.moduleObject = moduleObject;
    }

    /**
     * Sets collection
     * 
     * @param value Value to set
     */
    public void setCollection(Object value) {
        this.collection = value;
    }

    /**
     * Sets queryAttributes
     * 
     * @param value Value to set
     */
    public void setQueryAttributes(Object value) {
        this.queryAttributes = value;
    }

    /**
     * Sets username
     * 
     * @param value Value to set
     */
    public void setUsername(Object value) {
        this.username = value;
    }

    /**
     * Sets password
     * 
     * @param value Value to set
     */
    public void setPassword(Object value) {
        this.password = value;
    }

    /**
     * Get all superclasses and interfaces recursively.
     * 
     * @param classes List of classes to which to add all found super classes and interfaces.
     * @param clazz   The class to start the search with.
     */
    private void computeClassHierarchy(Class clazz, List classes) {
        for (Class current = clazz; (current!= null); current = current.getSuperclass()) {
            if (classes.contains(current)) {
                return ;
            }
            classes.add(current);
            for (Class currentInterface: current.getInterfaces()) {
                computeClassHierarchy(currentInterface, classes);
            }
        }
    }

    /**
     * Checks whether the specified class parameter is an instance of {@link List }
     * 
     * @param clazz <code>Class</code> to check.
     * @return
     */
    private boolean isListClass(Class clazz) {
        List<Class> classes = new ArrayList<Class>();
        computeClassHierarchy(clazz, classes);
        return classes.contains(List.class);
    }

    /**
     * Checks whether the specified class parameter is an instance of {@link Map }
     * 
     * @param clazz <code>Class</code> to check.
     * @return
     */
    private boolean isMapClass(Class clazz) {
        List<Class> classes = new ArrayList<Class>();
        computeClassHierarchy(clazz, classes);
        return classes.contains(Map.class);
    }

    private boolean isList(Type type) {
        if ((type instanceof Class)&&isListClass(((Class) type))) {
            return true;
        }
        if (type instanceof ParameterizedType) {
            return isList(((ParameterizedType) type).getRawType());
        }
        if (type instanceof WildcardType) {
            Type[] upperBounds = ((WildcardType) type).getUpperBounds();
            return ((upperBounds.length!= 0)&&isList(upperBounds[ 0 ]));
        }
        return false;
    }

    private boolean isMap(Type type) {
        if ((type instanceof Class)&&isMapClass(((Class) type))) {
            return true;
        }
        if (type instanceof ParameterizedType) {
            return isMap(((ParameterizedType) type).getRawType());
        }
        if (type instanceof WildcardType) {
            Type[] upperBounds = ((WildcardType) type).getUpperBounds();
            return ((upperBounds.length!= 0)&&isMap(upperBounds[ 0 ]));
        }
        return false;
    }

    private boolean isAssignableFrom(Type expectedType, Class clazz) {
        if (expectedType instanceof Class) {
            return ((Class) expectedType).isAssignableFrom(clazz);
        }
        if (expectedType instanceof ParameterizedType) {
            return isAssignableFrom(((ParameterizedType) expectedType).getRawType(), clazz);
        }
        if (expectedType instanceof WildcardType) {
            Type[] upperBounds = ((WildcardType) expectedType).getUpperBounds();
            if (upperBounds.length!= 0) {
                return isAssignableFrom(upperBounds[ 0 ], clazz);
            }
        }
        return false;
    }

    private Object evaluate(MuleMessage muleMessage, Object source) {
        if (source instanceof String) {
            String stringSource = ((String) source);
            if (stringSource.startsWith(patternInfo.getPrefix())&&stringSource.endsWith(patternInfo.getSuffix())) {
                return expressionManager.evaluate(stringSource, muleMessage);
            } else {
                return expressionManager.parse(stringSource, muleMessage);
            }
        }
        return source;
    }

    private Object evaluateAndTransform(MuleMessage muleMessage, Type expectedType, Object source)
        throws TransformerException
    {
        if (source == null) {
            return source;
        }
        Object target = null;
        if (isList(source.getClass())) {
            if (isList(expectedType)) {
                List newList = new ArrayList();
                Type valueType = ((ParameterizedType) expectedType).getActualTypeArguments()[ 0 ];
                ListIterator iterator = ((List) source).listIterator();
                while (iterator.hasNext()) {
                    Object subTarget = iterator.next();
                    newList.add(evaluateAndTransform(muleMessage, valueType, subTarget));
                }
                target = newList;
            } else {
                target = source;
            }
        } else {
            if (isMap(source.getClass())) {
                if (isMap(expectedType)) {
                    Type keyType = Object.class;
                    Type valueType = Object.class;
                    if (expectedType instanceof ParameterizedType) {
                        keyType = ((ParameterizedType) expectedType).getActualTypeArguments()[ 0 ];
                        valueType = ((ParameterizedType) expectedType).getActualTypeArguments()[ 1 ];
                    }
                    Map map = ((Map) source);
                    Map newMap = new HashMap();
                    for (Object entryObj: map.entrySet()) {
                        {
                            Map.Entry entry = ((Map.Entry) entryObj);
                            Object newKey = evaluateAndTransform(muleMessage, keyType, entry.getKey());
                            Object newValue = evaluateAndTransform(muleMessage, valueType, entry.getValue());
                            newMap.put(newKey, newValue);
                        }
                    }
                    target = newMap;
                } else {
                    target = source;
                }
            } else {
                target = evaluate(muleMessage, source);
            }
        }
        if ((target!= null)&&(!isAssignableFrom(expectedType, target.getClass()))) {
            DataType sourceDataType = DataTypeFactory.create(target.getClass());
            DataType targetDataType = DataTypeFactory.create(((Class) expectedType));
            Transformer t = muleContext.getRegistry().lookupTransformer(sourceDataType, targetDataType);
            return t.transform(target);
        } else {
            return target;
        }
    }

    /**
     * Invokes the MessageProcessor.
     * 
     * @param event MuleEvent to be processed
     * @throws MuleException
     */
    public MuleEvent process(MuleEvent event)
        throws MuleException
    {
        MuleMessage muleMessage = event.getMessage();
        MongoSession session = null;
        String transformedUsername = null;
        String transformedPassword = null;
        try {
            if (username!= null) {
                transformedUsername = ((String) evaluateAndTransform(muleMessage, CountObjectsUsingMapQueryMessageProcessor.class.getDeclaredField("_usernameType").getGenericType(), username));
            } else {
                transformedUsername = ((String) evaluateAndTransform(muleMessage, CountObjectsUsingMapQueryMessageProcessor.class.getDeclaredField("_usernameType").getGenericType(), moduleObject.getUsername()));
            }
            if (password!= null) {
                transformedPassword = ((String) evaluateAndTransform(muleMessage, CountObjectsUsingMapQueryMessageProcessor.class.getDeclaredField("_passwordType").getGenericType(), password));
            } else {
                transformedPassword = ((String) evaluateAndTransform(muleMessage, CountObjectsUsingMapQueryMessageProcessor.class.getDeclaredField("_passwordType").getGenericType(), moduleObject.getPassword()));
            }
            session = moduleObject.borrowSession(new MongoCloudConnectorSessionManagerAdapter.SessionKey(transformedUsername, transformedPassword));
            String transformedCollection = ((String) evaluateAndTransform(muleMessage, CountObjectsUsingMapQueryMessageProcessor.class.getDeclaredField("_collectionType").getGenericType(), collection));
            Map<String, String> transformedQueryAttributes = ((Map<String, String> ) evaluateAndTransform(muleMessage, CountObjectsUsingMapQueryMessageProcessor.class.getDeclaredField("_queryAttributesType").getGenericType(), queryAttributes));
            Object resultPayload;
            resultPayload = moduleObject.countObjectsUsingMapQuery(session, transformedCollection, transformedQueryAttributes);
            if (resultPayload == null) {
                event = new DefaultMuleEvent(new DefaultMuleMessage(NullPayload.getInstance(), muleContext), event);
            } else {
                List<Transformer> transformerList;
                transformerList = new ArrayList<Transformer>();
                transformerList.add(new TransformerTemplate(new TransformerTemplate.OverwitePayloadCallback(resultPayload)));
                event.getMessage().applyTransformers(event, transformerList);
            }
            return event;
        } catch (Exception e) {
            throw new MessagingException(CoreMessages.failedToInvoke("countObjectsUsingMapQuery"), event, e);
        } finally {
            if (session!= null) {
                try {
                    moduleObject.returnSession(new MongoCloudConnectorSessionManagerAdapter.SessionKey(transformedUsername, transformedPassword), session);
                } catch (Exception e) {
                    throw new MessagingException(CoreMessages.failedToInvoke("countObjectsUsingMapQuery"), event, e);
                }
            }
        }
    }

}

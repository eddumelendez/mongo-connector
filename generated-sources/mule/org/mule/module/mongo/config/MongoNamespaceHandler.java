
package org.mule.module.mongo.config;

import javax.annotation.Generated;
import org.mule.config.MuleManifest;
import org.mule.config.spring.parsers.specific.MessageProcessorDefinitionParser;
import org.mule.module.mongo.transformers.BsonListToJsonTransformer;
import org.mule.module.mongo.transformers.DbObjectToMapTransformer;
import org.mule.module.mongo.transformers.DbobjectToJsonTransformer;
import org.mule.module.mongo.transformers.JsonToDbobjectTransformer;
import org.mule.module.mongo.transformers.MongoCollectionToJsonTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;


/**
 * Registers bean definitions parsers for handling elements in <code>http://www.mulesoft.org/schema/mule/mongo</code>.
 * 
 */
@Generated(value = "Mule DevKit Version 3.5.0-RC1", date = "2014-05-08T10:05:29-05:00", comments = "Build master.1926.b0106b2")
public class MongoNamespaceHandler
    extends NamespaceHandlerSupport
{

    private static Logger logger = LoggerFactory.getLogger(MongoNamespaceHandler.class);

    private void handleException(String beanName, String beanScope, NoClassDefFoundError noClassDefFoundError) {
        String muleVersion = "";
        try {
            muleVersion = MuleManifest.getProductVersion();
        } catch (Exception _x) {
            logger.error("Problem while reading mule version");
        }
        logger.error(((((("Cannot launch the mule app, the  "+ beanScope)+" [")+ beanName)+"] within the connector [mongo] is not supported in mule ")+ muleVersion));
        throw new FatalBeanException(((((("Cannot launch the mule app, the  "+ beanScope)+" [")+ beanName)+"] within the connector [mongo] is not supported in mule ")+ muleVersion), noClassDefFoundError);
    }

    /**
     * Invoked by the {@link DefaultBeanDefinitionDocumentReader} after construction but before any custom elements are parsed. 
     * @see NamespaceHandlerSupport#registerBeanDefinitionParser(String, BeanDefinitionParser)
     * 
     */
    public void init() {
        try {
            this.registerBeanDefinitionParser("config", new MongoCloudConnectorConfigDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("config", "@Config", ex);
        }
        try {
            this.registerBeanDefinitionParser("add-user", new AddUserDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("add-user", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("drop-database", new DropDatabaseDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("drop-database", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("list-collections", new ListCollectionsDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("list-collections", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("exists-collection", new ExistsCollectionDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("exists-collection", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("drop-collection", new DropCollectionDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("drop-collection", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("create-collection", new CreateCollectionDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("create-collection", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("insert-object", new InsertObjectDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("insert-object", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("insert-object-from-map", new InsertObjectFromMapDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("insert-object-from-map", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("update-objects", new UpdateObjectsDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("update-objects", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("update-objects-using-query-map", new UpdateObjectsUsingQueryMapDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("update-objects-using-query-map", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("update-objects-using-map", new UpdateObjectsUsingMapDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("update-objects-using-map", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("update-objects-by-function", new UpdateObjectsByFunctionDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("update-objects-by-function", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("update-objects-by-function-using-map", new UpdateObjectsByFunctionUsingMapDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("update-objects-by-function-using-map", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("save-object", new SaveObjectDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("save-object", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("save-object-from-map", new SaveObjectFromMapDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("save-object-from-map", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("remove-objects", new RemoveObjectsDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("remove-objects", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("remove-objects-using-query-map", new RemoveObjectsUsingQueryMapDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("remove-objects-using-query-map", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("map-reduce-objects", new MapReduceObjectsDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("map-reduce-objects", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("count-objects", new CountObjectsDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("count-objects", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("count-objects-using-query-map", new CountObjectsUsingQueryMapDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("count-objects-using-query-map", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("find-objects", new FindObjectsDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("find-objects", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("find-objects-using-query-map", new FindObjectsUsingQueryMapDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("find-objects-using-query-map", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("find-one-object", new FindOneObjectDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("find-one-object", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("find-one-object-using-query-map", new FindOneObjectUsingQueryMapDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("find-one-object-using-query-map", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("create-index", new CreateIndexDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("create-index", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("drop-index", new DropIndexDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("drop-index", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("list-indices", new ListIndicesDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("list-indices", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("create-file-from-payload", new CreateFileFromPayloadDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("create-file-from-payload", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("find-files", new FindFilesDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("find-files", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("find-files-using-query-map", new FindFilesUsingQueryMapDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("find-files-using-query-map", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("find-one-file", new FindOneFileDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("find-one-file", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("find-one-file-using-query-map", new FindOneFileUsingQueryMapDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("find-one-file-using-query-map", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("get-file-content", new GetFileContentDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("get-file-content", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("get-file-content-using-query-map", new GetFileContentUsingQueryMapDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("get-file-content-using-query-map", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("list-files", new ListFilesDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("list-files", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("list-files-using-query-map", new ListFilesUsingQueryMapDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("list-files-using-query-map", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("remove-files", new RemoveFilesDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("remove-files", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("remove-files-using-query-map", new RemoveFilesUsingQueryMapDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("remove-files-using-query-map", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("execute-command", new ExecuteCommandDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("execute-command", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("dump", new DumpDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("dump", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("incremental-dump", new IncrementalDumpDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("incremental-dump", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("restore", new RestoreDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("restore", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("start-consistent-request", new StartConsistentRequestDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("start-consistent-request", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("end-consistent-request", new EndConsistentRequestDefinitionParser());
        } catch (NoClassDefFoundError ex) {
            handleException("end-consistent-request", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("json-to-dbobject", new MessageProcessorDefinitionParser(JsonToDbobjectTransformer.class));
        } catch (NoClassDefFoundError ex) {
            handleException("json-to-dbobject", "@Transformer", ex);
        }
        try {
            this.registerBeanDefinitionParser("dbobject-to-json", new MessageProcessorDefinitionParser(DbobjectToJsonTransformer.class));
        } catch (NoClassDefFoundError ex) {
            handleException("dbobject-to-json", "@Transformer", ex);
        }
        try {
            this.registerBeanDefinitionParser("bson-list-to-json", new MessageProcessorDefinitionParser(BsonListToJsonTransformer.class));
        } catch (NoClassDefFoundError ex) {
            handleException("bson-list-to-json", "@Transformer", ex);
        }
        try {
            this.registerBeanDefinitionParser("mongo-collection-to-json", new MessageProcessorDefinitionParser(MongoCollectionToJsonTransformer.class));
        } catch (NoClassDefFoundError ex) {
            handleException("mongo-collection-to-json", "@Transformer", ex);
        }
        try {
            this.registerBeanDefinitionParser("db-object-to-map", new MessageProcessorDefinitionParser(DbObjectToMapTransformer.class));
        } catch (NoClassDefFoundError ex) {
            handleException("db-object-to-map", "@Transformer", ex);
        }
    }

}


package org.mule.module.mongo.config;

import org.mule.config.spring.parsers.specific.MessageProcessorDefinitionParser;
import org.mule.module.mongo.transformers.BsonListToJsonTransformer;
import org.mule.module.mongo.transformers.DbObjectToMapTransformer;
import org.mule.module.mongo.transformers.DbobjectToJsonTransformer;
import org.mule.module.mongo.transformers.JsonToDbobjectTransformer;
import org.mule.module.mongo.transformers.MongoCollectionToJsonTransformer;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;


/**
 * Registers bean definitions parsers for handling elements in <code>http://www.mulesoft.org/schema/mule/mongo</code>.
 * 
 */
public class MongoCloudConnectorNamespaceHandler
    extends NamespaceHandlerSupport
{


    /**
     * Invoked by the {@link DefaultBeanDefinitionDocumentReader} after construction but before any custom elements are parsed. 
     * @see NamespaceHandlerSupport#registerBeanDefinitionParser(String, BeanDefinitionParser)
     * 
     */
    public void init() {
        registerBeanDefinitionParser("config", new MongoCloudConnectorConfigDefinitionParser());
        registerBeanDefinitionParser("list-collections", new ListCollectionsDefinitionParser());
        registerBeanDefinitionParser("exists-collection", new ExistsCollectionDefinitionParser());
        registerBeanDefinitionParser("drop-collection", new DropCollectionDefinitionParser());
        registerBeanDefinitionParser("create-collection", new CreateCollectionDefinitionParser());
        registerBeanDefinitionParser("insert-object", new InsertObjectDefinitionParser());
        registerBeanDefinitionParser("insert-object-from-map", new InsertObjectFromMapDefinitionParser());
        registerBeanDefinitionParser("update-objects", new UpdateObjectsDefinitionParser());
        registerBeanDefinitionParser("update-objects-using-query-map", new UpdateObjectsUsingQueryMapDefinitionParser());
        registerBeanDefinitionParser("update-objects-using-map", new UpdateObjectsUsingMapDefinitionParser());
        registerBeanDefinitionParser("update-objects-by-function", new UpdateObjectsByFunctionDefinitionParser());
        registerBeanDefinitionParser("update-objects-by-function-using-map", new UpdateObjectsByFunctionUsingMapDefinitionParser());
        registerBeanDefinitionParser("save-object", new SaveObjectDefinitionParser());
        registerBeanDefinitionParser("save-object-from-map", new SaveObjectFromMapDefinitionParser());
        registerBeanDefinitionParser("remove-objects", new RemoveObjectsDefinitionParser());
        registerBeanDefinitionParser("remove-using-query-map", new RemoveUsingQueryMapDefinitionParser());
        registerBeanDefinitionParser("map-reduce-objects", new MapReduceObjectsDefinitionParser());
        registerBeanDefinitionParser("count-objects", new CountObjectsDefinitionParser());
        registerBeanDefinitionParser("count-objects-using-query-map", new CountObjectsUsingQueryMapDefinitionParser());
        registerBeanDefinitionParser("find-objects", new FindObjectsDefinitionParser());
        registerBeanDefinitionParser("find-objects-using-query-map", new FindObjectsUsingQueryMapDefinitionParser());
        registerBeanDefinitionParser("find-one-object", new FindOneObjectDefinitionParser());
        registerBeanDefinitionParser("find-one-object-using-query-map", new FindOneObjectUsingQueryMapDefinitionParser());
        registerBeanDefinitionParser("create-index", new CreateIndexDefinitionParser());
        registerBeanDefinitionParser("drop-index", new DropIndexDefinitionParser());
        registerBeanDefinitionParser("list-indices", new ListIndicesDefinitionParser());
        registerBeanDefinitionParser("create-file-from-payload", new CreateFileFromPayloadDefinitionParser());
        registerBeanDefinitionParser("find-files", new FindFilesDefinitionParser());
        registerBeanDefinitionParser("find-files-using-query-map", new FindFilesUsingQueryMapDefinitionParser());
        registerBeanDefinitionParser("find-one-file", new FindOneFileDefinitionParser());
        registerBeanDefinitionParser("find-one-file-using-query-map", new FindOneFileUsingQueryMapDefinitionParser());
        registerBeanDefinitionParser("get-file-content", new GetFileContentDefinitionParser());
        registerBeanDefinitionParser("get-file-content-using-query-map", new GetFileContentUsingQueryMapDefinitionParser());
        registerBeanDefinitionParser("list-files", new ListFilesDefinitionParser());
        registerBeanDefinitionParser("list-files-using-query-map", new ListFilesUsingQueryMapDefinitionParser());
        registerBeanDefinitionParser("remove-files", new RemoveFilesDefinitionParser());
        registerBeanDefinitionParser("remove-files-using-query-map", new RemoveFilesUsingQueryMapDefinitionParser());
        registerBeanDefinitionParser("execute-command", new ExecuteCommandDefinitionParser());
        registerBeanDefinitionParser("json-to-dbobject", new MessageProcessorDefinitionParser(JsonToDbobjectTransformer.class));
        registerBeanDefinitionParser("dbobject-to-json", new MessageProcessorDefinitionParser(DbobjectToJsonTransformer.class));
        registerBeanDefinitionParser("bson-list-to-json", new MessageProcessorDefinitionParser(BsonListToJsonTransformer.class));
        registerBeanDefinitionParser("mongo-collection-to-json", new MessageProcessorDefinitionParser(MongoCollectionToJsonTransformer.class));
        registerBeanDefinitionParser("db-object-to-map", new MessageProcessorDefinitionParser(DbObjectToMapTransformer.class));
    }

}

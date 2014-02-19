
package org.mule.module.mongo.transformers;

import java.lang.reflect.Method;
import javax.annotation.Generated;
import org.bson.types.BasicBSONList;
import org.mule.api.transformer.DataType;
import org.mule.api.transformer.DiscoverableTransformer;
import org.mule.api.transformer.TransformerException;
import org.mule.config.i18n.CoreMessages;
import org.mule.module.mongo.MongoCloudConnector;
import org.mule.transformer.AbstractTransformer;
import org.mule.transformer.types.DataTypeFactory;

@Generated(value = "Mule DevKit Version 3.5.0-SNAPSHOT", date = "2014-02-19T03:09:09-06:00", comments = "Build UNKNOWN_BUILDNUMBER")
public class BsonListToJsonTransformer
    extends AbstractTransformer
    implements DiscoverableTransformer
{

    private int weighting = (DiscoverableTransformer.DEFAULT_PRIORITY_WEIGHTING + 5);

    public BsonListToJsonTransformer() {
        registerSourceType(DataTypeFactory.create(BasicBSONList.class));
        try {
            Method method = MongoCloudConnector.class.getMethod("bsonListToJson", BasicBSONList.class);
            DataType dataType = DataTypeFactory.createFromReturnType(method);
            setReturnDataType(dataType);
        } catch (NoSuchMethodException _x) {
            throw new RuntimeException("Unable to find method bsonListToJson");
        }
        setName("BsonListToJsonTransformer");
    }

    protected Object doTransform(Object src, String encoding)
        throws TransformerException
    {
        String result = null;
        try {
            result = MongoCloudConnector.bsonListToJson(((BasicBSONList) src));
        } catch (Exception exception) {
            throw new TransformerException(CoreMessages.transformFailed(src.getClass().getName(), "java.lang.String"), this, exception);
        }
        return result;
    }

    public int getPriorityWeighting() {
        return weighting;
    }

    public void setPriorityWeighting(int weighting) {
        this.weighting = weighting;
    }

}

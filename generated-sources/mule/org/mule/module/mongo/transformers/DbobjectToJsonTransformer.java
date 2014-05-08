
package org.mule.module.mongo.transformers;

import java.lang.reflect.Method;
import javax.annotation.Generated;
import com.mongodb.DBObject;
import org.mule.api.transformer.DataType;
import org.mule.api.transformer.DiscoverableTransformer;
import org.mule.api.transformer.TransformerException;
import org.mule.config.i18n.CoreMessages;
import org.mule.module.mongo.MongoCloudConnector;
import org.mule.transformer.AbstractTransformer;
import org.mule.transformer.types.DataTypeFactory;

@Generated(value = "Mule DevKit Version 3.5.0-RC1", date = "2014-05-08T04:40:43-05:00", comments = "Build master.1926.b0106b2")
public class DbobjectToJsonTransformer
    extends AbstractTransformer
    implements DiscoverableTransformer
{

    private int weighting = (DiscoverableTransformer.DEFAULT_PRIORITY_WEIGHTING + 5);

    public DbobjectToJsonTransformer() {
        registerSourceType(DataTypeFactory.create(DBObject.class));
        try {
            Method method = MongoCloudConnector.class.getMethod("dbobjectToJson", DBObject.class);
            DataType dataType = DataTypeFactory.createFromReturnType(method);
            setReturnDataType(dataType);
        } catch (NoSuchMethodException _x) {
            throw new RuntimeException("Unable to find method dbobjectToJson");
        }
        setName("DbobjectToJsonTransformer");
    }

    protected Object doTransform(Object src, String encoding)
        throws TransformerException
    {
        String result = null;
        try {
            result = MongoCloudConnector.dbobjectToJson(((DBObject) src));
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

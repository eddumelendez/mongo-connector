
package org.mule.module.mongo.config;

import com.mongodb.DBObject;
import org.mule.api.transformer.DiscoverableTransformer;
import org.mule.api.transformer.TransformerException;
import org.mule.config.i18n.CoreMessages;
import org.mule.module.mongo.MongoCloudConnector;
import org.mule.transformer.AbstractTransformer;
import org.mule.transformer.types.DataTypeFactory;

public class DbobjectToJsonTransformer
    extends AbstractTransformer
    implements DiscoverableTransformer
{

    private int weighting = (DiscoverableTransformer.DEFAULT_PRIORITY_WEIGHTING + 5);

    public DbobjectToJsonTransformer() {
        registerSourceType(DataTypeFactory.create(DBObject.class));
        registerSourceType(DataTypeFactory.create(DBObject.class));
        setReturnClass(String.class);
        setName("org.mule.module.mongo.DbobjectToJsonTransformer");
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

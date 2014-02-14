
package org.mule.module.mongo.config;

import java.util.Map;
import com.mongodb.DBObject;
import org.mule.api.transformer.DiscoverableTransformer;
import org.mule.api.transformer.TransformerException;
import org.mule.config.i18n.CoreMessages;
import org.mule.module.mongo.MongoCloudConnector;
import org.mule.transformer.AbstractTransformer;
import org.mule.transformer.types.DataTypeFactory;

public class DbObjectToMapTransformer
    extends AbstractTransformer
    implements DiscoverableTransformer
{

    private int weighting = (DiscoverableTransformer.DEFAULT_PRIORITY_WEIGHTING + 5);

    public DbObjectToMapTransformer() {
        registerSourceType(DataTypeFactory.create(DBObject.class));
        registerSourceType(DataTypeFactory.create(DBObject.class));
        setReturnClass(Map.class);
        setName("org.mule.module.mongo.DbObjectToMapTransformer");
    }

    protected Object doTransform(Object src, String encoding)
        throws TransformerException
    {
        Map result = null;
        try {
            result = MongoCloudConnector.dbObjectToMap(((DBObject) src));
        } catch (Exception exception) {
            throw new TransformerException(CoreMessages.transformFailed(src.getClass().getName(), "java.util.Map"), this, exception);
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

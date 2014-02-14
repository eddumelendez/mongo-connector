
package org.mule.module.mongo.config;

import com.mongodb.DBObject;
import org.mule.api.transformer.DiscoverableTransformer;
import org.mule.api.transformer.TransformerException;
import org.mule.config.i18n.CoreMessages;
import org.mule.module.mongo.MongoCloudConnector;
import org.mule.transformer.AbstractTransformer;
import org.mule.transformer.types.DataTypeFactory;

public class JsonToDbobjectTransformer
    extends AbstractTransformer
    implements DiscoverableTransformer
{

    private int weighting = (DiscoverableTransformer.DEFAULT_PRIORITY_WEIGHTING + 5);

    public JsonToDbobjectTransformer() {
        registerSourceType(DataTypeFactory.create(String.class));
        registerSourceType(DataTypeFactory.create(String.class));
        setReturnClass(DBObject.class);
        setName("org.mule.module.mongo.JsonToDbobjectTransformer");
    }

    protected Object doTransform(Object src, String encoding)
        throws TransformerException
    {
        DBObject result = null;
        try {
            result = MongoCloudConnector.jsonToDbobject(((String) src));
        } catch (Exception exception) {
            throw new TransformerException(CoreMessages.transformFailed(src.getClass().getName(), "com.mongodb.DBObject"), this, exception);
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

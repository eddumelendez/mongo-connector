
package org.mule.module.mongo.api.transformers;

import javax.annotation.Generated;
import org.mule.api.transformer.DiscoverableTransformer;
import org.mule.api.transformer.TransformerException;
import org.mule.module.mongo.api.IndexOrder;
import org.mule.transformer.AbstractTransformer;
import org.mule.transformer.types.DataTypeFactory;

@Generated(value = "Mule DevKit Version 3.5.0-RC1", date = "2014-05-08T10:05:29-05:00", comments = "Build master.1926.b0106b2")
public class IndexOrderEnumTransformer
    extends AbstractTransformer
    implements DiscoverableTransformer
{

    private int weighting = DiscoverableTransformer.DEFAULT_PRIORITY_WEIGHTING;

    public IndexOrderEnumTransformer() {
        registerSourceType(DataTypeFactory.create(String.class));
        setReturnClass(IndexOrder.class);
        setName("IndexOrderEnumTransformer");
    }

    protected Object doTransform(Object src, String encoding)
        throws TransformerException
    {
        IndexOrder result = null;
        result = Enum.valueOf(IndexOrder.class, ((String) src));
        return result;
    }

    public int getPriorityWeighting() {
        return weighting;
    }

    public void setPriorityWeighting(int weighting) {
        this.weighting = weighting;
    }

}

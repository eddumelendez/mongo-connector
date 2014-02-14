
package org.mule.module.mongo.transformers;

import org.mule.api.MuleContext;
import org.mule.api.context.MuleContextAware;
import org.mule.api.transformer.DiscoverableTransformer;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;
import org.mule.transformer.types.DataTypeFactory;

public class WriteConcernEnumTransformer
    extends AbstractTransformer
    implements MuleContextAware, DiscoverableTransformer
{

    /**
     * Mule Context
     * 
     */
    private MuleContext muleContext;
    private int weighting = DiscoverableTransformer.DEFAULT_PRIORITY_WEIGHTING;

    public WriteConcernEnumTransformer() {
        registerSourceType(DataTypeFactory.create(String.class));
        setReturnClass(org.mule.module.mongo.api.WriteConcern.class);
        setName("WriteConcernEnumTransformer");
    }

    /**
     * Set the Mule context
     * 
     * @param context Mule context to set
     */
    public void setMuleContext(MuleContext context) {
        this.muleContext = context;
    }

    protected Object doTransform(Object src, String encoding)
        throws TransformerException
    {
        org.mule.module.mongo.api.WriteConcern result = null;
        result = Enum.valueOf(org.mule.module.mongo.api.WriteConcern.class, ((String) src));
        return result;
    }

    public int getPriorityWeighting() {
        return weighting;
    }

    public void setPriorityWeighting(int weighting) {
        this.weighting = weighting;
    }

}

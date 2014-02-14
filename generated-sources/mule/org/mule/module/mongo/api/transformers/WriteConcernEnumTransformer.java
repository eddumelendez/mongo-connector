
package org.mule.module.mongo.api.transformers;

import javax.annotation.Generated;
import org.mule.api.transformer.DiscoverableTransformer;
import org.mule.api.transformer.TransformerException;
import org.mule.module.mongo.api.WriteConcern;
import org.mule.transformer.AbstractTransformer;
import org.mule.transformer.types.DataTypeFactory;

@Generated(value = "Mule DevKit Version 3.5.0-SNAPSHOT", date = "2014-02-14T12:14:34-06:00", comments = "Build UNKNOWN_BUILDNUMBER")
public class WriteConcernEnumTransformer
    extends AbstractTransformer
    implements DiscoverableTransformer
{

    private int weighting = DiscoverableTransformer.DEFAULT_PRIORITY_WEIGHTING;

    public WriteConcernEnumTransformer() {
        registerSourceType(DataTypeFactory.create(String.class));
        setReturnClass(WriteConcern.class);
        setName("WriteConcernEnumTransformer");
    }

    protected Object doTransform(Object src, String encoding)
        throws TransformerException
    {
        WriteConcern result = null;
        result = Enum.valueOf(WriteConcern.class, ((String) src));
        return result;
    }

    public int getPriorityWeighting() {
        return weighting;
    }

    public void setPriorityWeighting(int weighting) {
        this.weighting = weighting;
    }

}

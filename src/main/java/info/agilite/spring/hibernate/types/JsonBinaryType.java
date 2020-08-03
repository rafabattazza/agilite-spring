package info.agilite.spring.hibernate.types;

import java.lang.reflect.Type;
import java.util.Properties;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.usertype.DynamicParameterizedType;

public class JsonBinaryType extends AbstractSingleColumnStandardBasicType<Object> implements DynamicParameterizedType {

    public static final JsonBinaryType INSTANCE = new JsonBinaryType();

    public JsonBinaryType() {
        super(
            JsonBinarySqlTypeDescriptor.INSTANCE,
            new JsonTypeDescriptor()
        );
    }

    public JsonBinaryType(Type javaType) {
        super(
            JsonBinarySqlTypeDescriptor.INSTANCE,
            new JsonTypeDescriptor(javaType)
        );
    }

    public String getName() {
        return "jsonb";
    }

    @Override
    public void setParameterValues(Properties parameters) {
        ((JsonTypeDescriptor) getJavaTypeDescriptor()).setParameterValues(parameters);
    }

}
package info.agilite.spring.base.json;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Collection;

import org.hibernate.internal.util.SerializationHelper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import info.agilite.utils.jackson.JSonMapperCreator;
public class ObjectMapperWrapper {

    public static final ObjectMapperWrapper INSTANCE = new ObjectMapperWrapper();

    private final ObjectMapper objectMapper;

    public ObjectMapperWrapper() {
        this.objectMapper = JSonMapperCreator.create();
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public <T> T fromString(String string, Class<T> clazz) {
        try {
            return objectMapper.readValue(string, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException("The given string value: " + string + " cannot be transformed to Json object", e);
        }
    }

    public <T> T fromString(String string, Type type) {
        try {
            return objectMapper.readValue(string, objectMapper.getTypeFactory().constructType(type));
        } catch (IOException e) {
            throw new IllegalArgumentException("The given string value: " + string + " cannot be transformed to Json object", e);
        }
    }

    public String toString(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("The given Json object value: " + value + " cannot be transformed to a String", e);
        }
    }

    public JsonNode toJsonNode(String value) {
        try {
            return objectMapper.readTree(value);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public <T> T clone(T value) {
        if (value instanceof Collection && !((Collection) value).isEmpty()) {
            Object firstElement = ((Collection) value).iterator().next();
            if (!(firstElement instanceof Serializable)) {
                JavaType type = TypeFactory.defaultInstance().constructParametricType(value.getClass(), firstElement.getClass());
                return fromString(toString(value), type);
            }
        }

        return value instanceof Serializable ?
                (T) SerializationHelper.clone((Serializable) value) :
                fromString(toString(value), (Class<T>) value.getClass());
    }
}

package info.agilite.spring.base.metadata;

import java.lang.reflect.Type;

import com.fasterxml.jackson.core.type.TypeReference;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Data
public class JSonType implements Type{
	TypeReference<?> type;
}

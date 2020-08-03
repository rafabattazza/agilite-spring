package info.agilite.spring.base.handler;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(makeFinal=true, level=AccessLevel.PRIVATE)
@AllArgsConstructor(access=AccessLevel.PUBLIC)
@Getter
public class ExceptionEntity {
	Long time = System.currentTimeMillis();
	String message;
	String error;
	String path;
	List<Violation> validations;
}

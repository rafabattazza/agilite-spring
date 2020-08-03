package info.agilite.spring.base;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
/**
 * Exception que deve ser utilizada nos Services deixando o Service desacoplado do Spring.
 * 
 * Quando essa exceção chegar ao Controller ela será transformada pelo Handler em uma BadRequest 400
 * 
 * @author Rafael
 *
 */

@FieldDefaults(makeFinal=true, level=AccessLevel.PRIVATE)
@AllArgsConstructor(access=AccessLevel.PUBLIC)
@Getter
public class ValidationException extends RuntimeException{
	public ValidationException(String message) {
		super(message);
	}

	public ValidationException(String message, Throwable cause) {
		super(message, cause);
	}
	
}

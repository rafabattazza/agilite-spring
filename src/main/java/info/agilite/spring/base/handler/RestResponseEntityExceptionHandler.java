package info.agilite.spring.base.handler;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import info.agilite.spring.base.metadata.EntitiesMetadata;
import info.agilite.spring.base.metadata.PropertyMetadata;
import info.agilite.utils.ExceptionUtils;
import info.agilite.utils.ForbiddenException;
import info.agilite.utils.StringUtils;
import info.agilite.utils.Utils;
import info.agilite.utils.ValidationException;
import lombok.extern.slf4j.Slf4j;
/**
 * Handler para exceptions, retorna no body da requisição um JSON gerado a partir da classe {@link ExceptionEntity}
 * 
 * 
 * @author Rafael
 *
 */

@ControllerAdvice
@Slf4j
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler  {
	@ResponseBody
	@ExceptionHandler(ValidationException.class)
	protected ResponseEntity<Object> handleResponseStatusException(ValidationException ex, WebRequest request) {
		log.error("RestResponseEntityExceptionHandler", ex);
		return handleExceptionInternal(ex, null, null, HttpStatus.PRECONDITION_REQUIRED, request);
	}
	
	@ResponseBody
	@ExceptionHandler(ForbiddenException.class)
	protected ResponseEntity<Object> handleForbiddenException(ForbiddenException ex, WebRequest request) {
		log.error("ForbiddenException", ex);
		return handleExceptionInternal(ex, null, null, HttpStatus.FORBIDDEN, request);
	}
	
	@ResponseBody
	@ExceptionHandler(RestClientResponseException.class)
	protected ResponseEntity<Object> handleResponseStatusException(RestClientResponseException ex, WebRequest request) {
		log.error("RestClientResponseException", ex);
		return handleExceptionInternal(ex, null, null, HttpStatus.valueOf(ex.getRawStatusCode()),  request);
	}
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		List<Violation> violations = ex.getBindingResult().getFieldErrors().stream().map(v -> new Violation(v.getField(), v.getDefaultMessage())).collect(Collectors.toList());
		return ResponseEntity.badRequest().body(createMessageError(ex, request, violations)); 
	}

	@ExceptionHandler(javax.validation.ConstraintViolationException.class)
	@ResponseBody
	protected ResponseEntity<Object> onConstraintValidationException(javax.validation.ConstraintViolationException e, WebRequest request) {
		List<Violation> violations = e.getConstraintViolations().stream().map(v -> new Violation(v.getPropertyPath().toString(), v.getMessage())).collect(Collectors.toList());
		return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED).body(createMessageError(e, request, violations)); 
	}
	
	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseBody
	protected ResponseEntity<Object> onConstraintValidationException(DataIntegrityViolationException ex, WebRequest request) {
		ConstraintViolationException constraintViolation = ExceptionUtils.findCause(ex, ConstraintViolationException.class);
		
		if(constraintViolation != null) {
			List<Violation> violations = obterValidatcoesDaConstraintException(constraintViolation);
			if(!Utils.isEmpty(violations)) {
				return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED).body(createMessageError(constraintViolation, request, violations)); 
			}
		}
		
		return handleExceptionInternal(ex, null, null, HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
		if(body == null) {
			body = createMessageError(ex, request);
		}
		return super.handleExceptionInternal(ex, body, headers, status, request);
	}

	private ExceptionEntity createMessageError(Exception ex, WebRequest request){
		return createMessageError(ex, request, null);
	}
	
	private ExceptionEntity createMessageError(Exception ex, WebRequest request, List<Violation> validations){
		return new ExceptionEntity(validations != null ? "BadRequest" : ex.getMessage(), ex.getClass().getName(), parseDescription(request), validations);
	}
	
	private String parseDescription(WebRequest request) {
		try {
			String desc = request.getDescription(false);
			return desc.replace("uri=", "");
		} catch (Exception e) {
			return request.getDescription(false);
		}
	}
	
	private List<Violation> obterValidatcoesDaConstraintException(ConstraintViolationException constraintViolation) {
		List<Violation> violations = new ArrayList<>();
		
		String contraintName = constraintViolation.getConstraintName();
		if(contraintName == null)return null;
		
		boolean isUk = contraintName.contains("_uk");
		
		String msg = "Erro de validação";
		if(isUk) {
			String ukFields = EntitiesMetadata.INSTANCE.getUkFields(contraintName.toLowerCase());
			
			if(!StringUtils.isNullOrEmpty(ukFields)) {
				String campos = "";
				String[] splits = ukFields.split(",");
				for(int i = 0; i < splits.length; i++) {
					String fieldName = splits[i];
					PropertyMetadata property = EntitiesMetadata.INSTANCE.getPropertyByName(fieldName);
					if(property != null) {
						campos += property.getLabel() + (i == splits.length-1 ? "." : ", ");
					}
				}
				
				if(splits.length > 1) {
					msg = "Registro repetido, já existe um registro com os mesmos valores nos campos '" + campos + "'";
				}else {
					msg = "Registro repetido, Já existe um registro com o mesmo valor no campo '" + campos + "'";
				}
			}else {
				msg = "Esse registro já existe";
				
			}
			
		}else {
			String tableName = StringUtils.substringBeforeFirst(contraintName, "_");
			String tableDescr = EntitiesMetadata.INSTANCE.getTableLabelByName(tableName);
			msg = "Não foi possível excluir esse registro, pois ele está relacionado no cadastro de '" + tableDescr + "', você pode Arquivar o registro para que ele não seja mais exibido.";
		}
		
		
		violations.add(new Violation(msg, "(" + contraintName + ")"));
		return violations;
	}
}

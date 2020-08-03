package info.agilite.spring.base.controller;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import info.agilite.spring.base.handler.ExceptionEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@Controller
@FieldDefaults(makeFinal=true, level=AccessLevel.PRIVATE)
@AllArgsConstructor(access=AccessLevel.PACKAGE)
public class CommonErrorController implements ErrorController {

	ErrorAttributes errorAttributes;

	@RequestMapping(value = "/error")
	@ResponseBody
	public ExceptionEntity error(WebRequest webRequest, HttpServletResponse response) {
		Map<String, Object> atts = errorAttributes.getErrorAttributes(webRequest, false);
		
		Throwable e = errorAttributes.getError(webRequest);
		return new ExceptionEntity((String)atts.get("message"),  e == null ? "" : e.getClass().getName(), (String)atts.get("path"), null);
	}

	@Override
	public String getErrorPath() {
		return "error";
	}
}
package info.agilite.spring.base.http;

import javax.servlet.http.HttpServletResponse;

public class ResponseUtils {

	public static void definirHeadersParaExportacao(HttpServletResponse response, String contentType, String nome) {
		String exposedHeaders = "filename, content-type";
		response.setHeader("Access-Control-Expose-Headers", exposedHeaders);
		response.setHeader("content-type", contentType);
		response.setHeader("filename", nome);
	}
}

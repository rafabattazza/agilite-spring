package info.agilite.spring.base.jasper;

import java.io.File;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import info.agilite.spring.base.http.ResponseUtils;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

public class ReportUtil {

	public static void writePDF(File reportFile, CloseableDataSource dataSource, Map<String, Object> params, HttpServletResponse response, String nome) {
		try {
			JasperReport report = (JasperReport)JRLoader.loadObject(reportFile);
			JasperPrint print = JasperFillManager.fillReport(report, params, dataSource);

			ResponseUtils.definirHeadersParaExportacao(response, "application/pdf", nome);
			JasperExportManager.exportReportToPdfStream(print, response.getOutputStream());
		}catch (Exception e) {
			throw new RuntimeException("Erro ao gerar PDF", e);
		}finally {
			params.clear();
			dataSource.close();
		}
	}
}

package info.agilite.spring.base.jasper;

import java.io.File;
import java.io.OutputStream;
import java.util.Map;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

public class ReportUtil {

	public static void gerarPDFStream(File reportFile, CloseableDataSource dataSource, Map<String, Object> params, OutputStream output) {
		try {
			JasperReport report = (JasperReport)JRLoader.loadObject(reportFile);
			JasperPrint print = JasperFillManager.fillReport(report, params, dataSource);
			
//			JasperExportManager.exportReportToPdfStream(print, output);
			byte[] dados = JasperExportManager.exportReportToPdf(print);
			
			
		}catch (Exception e) {
			throw new RuntimeException("Erro ao gerar PDF", e);
		}finally {
			params.clear();
			dataSource.close();
		}
	}
}

package info.agilite.spring.base.jasper;

import net.sf.jasperreports.engine.JRRewindableDataSource;

public interface CloseableDataSource extends JRRewindableDataSource{
	public void close();

}

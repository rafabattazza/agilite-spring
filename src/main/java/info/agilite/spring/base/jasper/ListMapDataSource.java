package info.agilite.spring.base.jasper;

import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRewindableDataSource;

public class ListMapDataSource implements JRRewindableDataSource {
	private List<Map<String, ?>> records;
	private int currentRecord = -1;
	
	public ListMapDataSource(List<Map<String, ?>> records) {
		this.records = records;
	}

	@Override
	public boolean next() {
		boolean hasNext = false;

		if (records != null) {
			++currentRecord;
			hasNext = records.size() > currentRecord;
		}

		return hasNext;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;

		value = records.get(currentRecord).get(field.getName());
		
		if (value instanceof List) {
			return new ListMapDataSource((List<Map<String, ?>>) value);
		}else {
			System.out.println(field.getValueClass());//TODO Converter baseado no tipo do filed
			return value;
		}
	}

	@Override
	public void moveFirst() {
		if (records != null) {
			currentRecord = -1;
		}
	}

	public int getRecordCount() {
		return records == null ? 0 : records.size();
	}

	public ListMapDataSource cloneDataSource() {
		return new ListMapDataSource(records);
	}
}

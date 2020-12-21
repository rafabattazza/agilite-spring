package info.agilite.spring.base.jasper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

import info.agilite.utils.TableMap;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class TableMapDataSource implements CloseableDataSource {
	private List<TableMap> records;
	private Iterator<TableMap> iterator;
	private TableMap currentRecord;
	
	private final Map<String, SubDataSourceConfig> subDataSources = new HashMap<>();
	
	public BiFunction<JRField, Object, Object> converter = this::converterValor;
			
	public TableMapDataSource(List<TableMap> records) {
		this.records = records;

		if (records != null) {
			iterator = records.iterator();
		}
	}

	@Override
	public boolean next() {
		boolean hasNext = false;

		if (iterator != null) {
			hasNext = iterator.hasNext();

			if (hasNext) {
				currentRecord = iterator.next();
			}
		}

		return hasNext;
	}

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;

		if (currentRecord != null) {
			if (this.subDataSources.containsKey(field.getName().toLowerCase())) {
				SubDataSourceConfig dsConfig = subDataSources.get(field.getName().toLowerCase());
				if(dsConfig != null) {
					Object valorPai = currentRecord.get(dsConfig.colunaPai);
					
					List<TableMap> subDataSet = new ArrayList<>();
					while(Objects.equals(valorPai, dsConfig.getValorFilho())) {
						try {
							subDataSet.add(dsConfig.getRow());
						} finally {
							dsConfig.next();
						}
					}
					
					value = new TableMapDataSource(subDataSet);
				}
			}
			
			value = currentRecord.get(field.getName());
			
		}
		
		return converter.apply(field, value);
	}
	
	public Object converterValor(JRField field, Object value) {
		if(value == null)return null;
		String classe = field.getValueClassName();
		
		if(classe.equalsIgnoreCase("java.lang.Long")) {
			if(value instanceof Long)return value;
			return new Long(value+"");
		}
		
		return value;
	}
	
	@Override
	public void moveFirst() {
		if (records != null) {
			iterator = records.iterator();
		}
	}

	public int getRecordCount() {
		return records == null ? 0 : records.size();
	}

	public TableMapDataSource cloneDataSource() {
		return new TableMapDataSource(records);
	}
	public void addSubDataSource(String nome, List<TableMap> dados, String colunaPai, String colunaFilha) {
		this.subDataSources.put(nome.toLowerCase(), new SubDataSourceConfig(dados, colunaPai, colunaFilha));
	}
	
	private class SubDataSourceConfig{
		List<TableMap> dados; 
		String colunaPai; 
		String colunaFilha;
		
		public SubDataSourceConfig(List<TableMap> dados, String colunaPai, String colunaFilha) {
			super();
			this.dados = dados;
			this.colunaPai = colunaPai;
			
			this.colunaFilha = colunaFilha;
		}
		
		int lastIndex = 0;
		Object getValorFilho() {
			if(dados.size() == 0 || dados.size() == lastIndex)return null;
			return dados.get(lastIndex).get(colunaFilha);
		}
		TableMap getRow() {
			return dados.get(lastIndex);
		}
		void next() {
			this.lastIndex++;
		}
	}
	@Override
	public void close() {
		try {records.clear();} catch (Exception e) {}
		try {records = null;} catch (Exception e) {}
	}
}

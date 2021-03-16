package info.agilite.spring.base.crud;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.jdbc.ReturningWork;

import info.agilite.spring.base.database.HibernateWrapper;
import info.agilite.spring.base.http.ResponseUtils;
import info.agilite.spring.base.metadata.EntitiesMetadata;
import info.agilite.spring.base.metadata.PropertyMetadata;
import info.agilite.utils.StringUtils;

class CrudProviderExportarUtils {
	private HibernateWrapper hibernate;
	private Class<?> entityClass;
	
	public CrudProviderExportarUtils(Class<?> entityClass, HibernateWrapper hibernate) {
		super();
		this.entityClass = entityClass;
		this.hibernate = hibernate;
	}
	
	public void exportar(String type, List<Long> ids, HttpServletResponse response, List<CrudExportColumn> colunas, String jdbcJoin) {
		String paramIds = ids.stream().map(id -> id.toString()).collect(Collectors.joining(","));
		
		try(PreparedStatement stm = hibernate.session().doReturningWork(new ReturningWork<PreparedStatement>() {
			@Override
			public PreparedStatement execute(Connection cnn) throws SQLException {
				String join = StringUtils.isNullOrEmpty(jdbcJoin) ? " " : jdbcJoin;
				String sql = StringUtils.concat(
						" SELECT ", colunas.stream().map(CrudExportColumn::getToSQL).collect(Collectors.joining(", ")),
						" FROM ", entityClass.getSimpleName(),
						join,
						" WHERE ", entityClass.getSimpleName().toLowerCase(), "id IN (", paramIds, ") ",
						" ORDER BY ", entityClass.getSimpleName().toLowerCase(), "id"
					);
				return cnn.prepareStatement(sql);
			}
		})){
			if(type.equalsIgnoreCase("excel")) {
				ResultSet rs = stm.executeQuery();
				exportarExcel(rs, colunas, response);
			}else {
				throw new RuntimeException("Tipo '".concat(type).concat("' não possui exportador"));
			}
		}catch(SQLException | IOException e) {
			throw new RuntimeException("Erro ao exportar dados para o Excel", e);
		}
	}
	
	public List<CrudExportColumn> getColunasExportacao() {
		List<CrudExportColumn> colunas = new ArrayList<>();
		String tableAlias = entityClass.getSimpleName().toLowerCase();
		for(PropertyMetadata prop : EntitiesMetadata.INSTANCE.getPropertiesByTable(entityClass.getSimpleName())) {
			if(prop.getNome().equalsIgnoreCase(tableAlias + "id"))continue;
			if(prop.getType().getTypeName().endsWith("JSonType"))continue;
			
			if(prop.isFk()) {
				adicionarColunasFk(colunas, prop);
				continue;
			}
			colunas.add(new CrudExportColumn(prop.getSqlNome(), prop.getLabel(), tableAlias, prop.getType(), prop.getLegendas()));
		}
		
		return colunas;
	}
	
	private void adicionarColunasFk(List<CrudExportColumn> colunas, PropertyMetadata propFk) {
		String tableFk = StringUtils.substringAfterLast(propFk.getType().getTypeName(), ".");
		List<PropertyMetadata> propriedadesDaFk = EntitiesMetadata.INSTANCE.getPropertiesByTable(tableFk);
		propriedadesDaFk.stream().filter(prop -> prop.isAutocomplete())
			.forEach(prop-> {
				if(prop.isFk()) {
					adicionarColunasFk(colunas, prop);
				}else {
					colunas.add(new CrudExportColumn(prop.getSqlNome(), propFk.getLabel() + "-" + prop.getLabel(), propFk.getNome(), prop.getType(), prop.getLegendas()));
				}
		});
	}
	
	public String getJoinJdbcParaExportacao() {
		StringBuilder join = new StringBuilder();
		String tableAlias = entityClass.getSimpleName().toLowerCase();
		for(PropertyMetadata prop : EntitiesMetadata.INSTANCE.getPropertiesByTable(entityClass.getSimpleName())) {
			if(prop.isFk()) {
				adicionarJoinFk(join, tableAlias, prop);
			}
		}
		return join.toString();
	}
	
	private void adicionarJoinFk(StringBuilder join, String aliasTable, PropertyMetadata propFk) {
		String tableFk = StringUtils.substringAfterLast(propFk.getType().getTypeName(), ".");
		join.append(
			StringUtils.concat(" LEFT JOIN ", tableFk, " AS ", propFk.getNome(), " ON ", propFk.getNome(), ".", tableFk.toLowerCase(), "id = ", propFk.getNome())
		);
		
		List<PropertyMetadata> propriedadesDaFk = EntitiesMetadata.INSTANCE.getPropertiesByTable(tableFk);
		propriedadesDaFk.stream().filter(prop -> prop.isAutocomplete())
		.forEach(prop-> {
			if(prop.isFk()) {
				adicionarJoinFk(join, propFk.getNome(), prop);
			}
		});
		
	}
	
	private void exportarExcel(ResultSet rs, List<CrudExportColumn> colunas, HttpServletResponse response) throws SQLException, IOException {
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Planilha0");
		
		Row header = sheet.createRow(0);
		CellStyle headerStyle = createHeaderStyle(workbook);
		for(int i = 0; i < colunas.size(); i++) {
			CrudExportColumn coluna = colunas.get(i);
			Cell headerCell = header.createCell(i);
			headerCell.setCellValue(coluna.getLabel());
			headerCell.setCellStyle(headerStyle);
		}
		
		int indexRow = 1;
		while(rs.next()) {
			Row row = sheet.createRow(indexRow);
			for(int i = 0; i < colunas.size(); i++) {
				Cell cell = row.createCell(i);
				printCell(cell, colunas.get(i), rs.getObject(i+1));
			}
			indexRow++;
		}
		for(int i = 0; i < colunas.size(); i++) {
			sheet.autoSizeColumn(i);
		}
		
		ResponseUtils.definirHeadersParaExportacao(response, "application/vnd.ms-excel", EntitiesMetadata.INSTANCE.getTableLabelByName(entityClass.getSimpleName()) + ".xls");
		workbook.write(response.getOutputStream());
	}
	
	private CellStyle createHeaderStyle(Workbook workbook) {
		CellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		XSSFFont font = ((XSSFWorkbook) workbook).createFont();
		font.setFontName("Arial");
		font.setFontHeightInPoints((short) 10);
		headerStyle.setFont(font);
		
		return headerStyle;
	}
	
	private void printCell(Cell cell, CrudExportColumn column, Object value) {
		if(value == null || StringUtils.isNullOrEmpty(value.toString())) return;

		if(column.getLegendas() != null) {
			cell.setCellValue(column.getLegendas().get(value.toString()));
		}else {
			if(column.getType() == Boolean.class) {
				cell.setCellValue(new Integer(1).equals(value) ? "Sim" : "Não");
			}else if(column.getType() == LocalDate.class) {
				cell.setCellValue(new SimpleDateFormat("dd/MM/yyyy").format(new Date(((java.sql.Date)value).getTime())));
			}else if(column.getType() == BigDecimal.class) {
				cell.setCellValue(new BigDecimal(value.toString()).doubleValue());
			}else {
				cell.setCellValue(value.toString());
			}
		}
	}
}


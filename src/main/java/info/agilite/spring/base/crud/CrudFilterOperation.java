package info.agilite.spring.base.crud;

public enum CrudFilterOperation {
	equals("="), between("BETWEEN"), like("LIKE"), isNull("IS NULL"), isNotNull("IS NOT NULL");
	private final String sqlOperation;

	private CrudFilterOperation(String sqlOperation) {
		this.sqlOperation = sqlOperation;
	}

	public String getSqlOperation() {
		return sqlOperation;
	}
	
}

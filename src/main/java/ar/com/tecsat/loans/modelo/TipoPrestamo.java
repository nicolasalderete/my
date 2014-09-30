package ar.com.tecsat.loans.modelo;

public enum TipoPrestamo {

DIARIO("Diario",21), SEMANAL("Semanal", 4), MENSUAL("Mensual", 1), QUINCENAL("Quincenal", 2);
	
	private final int value;
	private final String description;
	
	private TipoPrestamo(String tipo, int value) {
		this.value = value;
		this.description = tipo;
	}

	public int getValue() {
		return value;
	}

	public String getDescription() {
		return description;
	}
}

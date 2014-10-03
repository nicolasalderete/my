package ar.com.tecsat.loans.modelo;

public enum TipoPrestamo {

DIARIO("Diario",1), SEMANAL("Semanal", 7), MENSUAL("Mensual", 30), QUINCENAL("Quincenal", 14);
	
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

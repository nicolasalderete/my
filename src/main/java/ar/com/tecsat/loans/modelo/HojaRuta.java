package ar.com.tecsat.loans.modelo;

import java.math.BigDecimal;

public class HojaRuta {
	
	private String nombre;
	private String domicilio;
	private String entreCalles;
	private int cuota;
	private BigDecimal monto;
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getDomicilio() {
		return domicilio;
	}
	public void setDomicilio(String domicilio) {
		this.domicilio = domicilio;
	}
	public String getEntreCalles() {
		return entreCalles;
	}
	public void setEntreCalles(String entreCalles) {
		this.entreCalles = entreCalles;
	}
	public int getCuota() {
		return cuota;
	}
	public void setCuota(int cuota) {
		this.cuota = cuota;
	}
	public BigDecimal getMonto() {
		return monto;
	}
	public void setMonto(BigDecimal monto) {
		this.monto = monto;
	}
	
}

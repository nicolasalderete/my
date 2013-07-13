package ar.com.tecsat.loans.bean.utils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author nicolas
 *
 */
@SuppressWarnings("serial")
public class CuotaFiltro implements Serializable {
	
	private Integer idCliente;
	private Integer idPrestamo;
	private Date fechaVencimiento;
	private Date fechaDesde;
	private Date fechaHasta;
	private String condicionMonto;
	private String condicionCuota;
	private BigDecimal montoCuota;
	private Integer numeroCuota;
	private String estadoCuota;
	private BigDecimal importePago;
	
	public Integer getIdCliente() {
		return idCliente;
	}
	public void setIdCliente(Integer idCliente) {
		this.idCliente = idCliente;
	}
	public Integer getIdPrestamo() {
		return idPrestamo;
	}
	public void setIdPrestamo(Integer idPrestamo) {
		this.idPrestamo = idPrestamo;
	}
	public Date getFechaVencimiento() {
		return fechaVencimiento;
	}
	public void setFechaVencimiento(Date fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}
	public Date getFechaDesde() {
		return fechaDesde;
	}
	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}
	public Date getFechaHasta() {
		return fechaHasta;
	}
	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}
	public String getCondicionMonto() {
		return condicionMonto;
	}
	public void setCondicionMonto(String condicionMonto) {
		this.condicionMonto = condicionMonto;
	}
	public BigDecimal getMontoCuota() {
		return montoCuota;
	}
	public void setMontoCuota(BigDecimal montoCuota) {
		this.montoCuota = montoCuota;
	}
	public Integer getNumeroCuota() {
		return numeroCuota;
	}
	public void setNumeroCuota(Integer numeroCuota) {
		this.numeroCuota = numeroCuota;
	}
	public String getEstadoCuota() {
		return estadoCuota;
	}
	public void setEstadoCuota(String estadoCuota) {
		this.estadoCuota = estadoCuota;
	}
	public String getCondicionCuota() {
		return condicionCuota;
	}
	public void setCondicionCuota(String condicionCuota) {
		this.condicionCuota = condicionCuota;
	}
	public BigDecimal getImportePago() {
		return importePago;
	}
	public void setImportePago(BigDecimal importePago) {
		this.importePago = importePago;
	}
	
}

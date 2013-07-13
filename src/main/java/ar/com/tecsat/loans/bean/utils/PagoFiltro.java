package ar.com.tecsat.loans.bean.utils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author nicolas
 *
 */
@SuppressWarnings("serial")
public class PagoFiltro implements Serializable {

	private Integer idCliente;
	private Integer idPrestamo;
	private String pagEstado;
	private Date pagFestadoDesde;
	private Date pagFestadoHasta;
	private String condicionMonto;
	private BigDecimal pagMonto;
	private Integer numeroCuota;
	
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
	public String getPagEstado() {
		return pagEstado;
	}
	public void setPagEstado(String pagEstado) {
		this.pagEstado = pagEstado;
	}
	public Date getPagFestadoDesde() {
		return pagFestadoDesde;
	}
	public void setPagFestadoDesde(Date pagFestadoDesde) {
		this.pagFestadoDesde = pagFestadoDesde;
	}
	public Date getPagFestadoHasta() {
		return pagFestadoHasta;
	}
	public void setPagFestadoHasta(Date pagFestadoHasta) {
		this.pagFestadoHasta = pagFestadoHasta;
	}
	public String getCondicionMonto() {
		return condicionMonto;
	}
	public void setCondicionMonto(String condicionMonto) {
		this.condicionMonto = condicionMonto;
	}
	public BigDecimal getPagMonto() {
		return pagMonto;
	}
	public void setPagMonto(BigDecimal pagMonto) {
		this.pagMonto = pagMonto;
	}
	public Integer getNumeroCuota() {
		return numeroCuota;
	}
	public void setNumeroCuota(Integer numeroCuota) {
		this.numeroCuota = numeroCuota;
	}
}

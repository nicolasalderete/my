package ar.com.tecsat.loans.bean.utils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import ar.com.tecsat.loans.modelo.Prestamo;
import ar.com.tecsat.loans.modelo.TipoPrestamo;

/**
 * @author nicolas
 *
 */
@SuppressWarnings("serial")
public class PrestamoFiltro implements Serializable {

	private Integer idCliente;
	private Integer cantCuotas;
	private String condicionCuotas;
	private String condicionMonto;
	private String condicionTasa;
	private BigDecimal capital;
	private Double tasa;
	private String estado;
	private Date fechaSoli;
	private Date fechaDesde;
	private Date fechaHasta;
	private Prestamo prestamo;
	private TipoPrestamo tipoPrestamo;
	private BigDecimal interesTotal;
	
	public Integer getIdCliente() {
		return idCliente;
	}
	public void setIdCliente(Integer idCliente) {
		this.idCliente = idCliente;
	}
	public Integer getCantCuotas() {
		return cantCuotas;
	}
	public void setCantCuotas(Integer cantCuotas) {
		this.cantCuotas = cantCuotas;
	}
	public String getCondicionMonto() {
		return condicionMonto;
	}
	public void setCondicionMonto(String condicionMonto) {
		this.condicionMonto = condicionMonto;
	}
	public String getCondicionTasa() {
		return condicionTasa;
	}
	public void setCondicionTasa(String condicionTasa) {
		this.condicionTasa = condicionTasa;
	}
	public BigDecimal getCapital() {
		return capital;
	}
	public void setCapital(BigDecimal monto) {
		this.capital = monto;
	}
	public Double getTasa() {
		return tasa;
	}
	public void setTasa(Double tasa) {
		this.tasa = tasa;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
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
	public Date getFechaSoli() {
		return fechaSoli;
	}
	public void setFechaSoli(Date fechaSoli) {
		this.fechaSoli = fechaSoli;
	}
	public String getCondicionCuotas() {
		return condicionCuotas;
	}
	public void setCondicionCuotas(String condicionCuotas) {
		this.condicionCuotas = condicionCuotas;
	}
	public Prestamo getPrestamo() {
		return prestamo;
	}
	public void setPrestamo(Prestamo prestamo) {
		this.prestamo = prestamo;
	}
	public TipoPrestamo getTipoPrestamo() {
		return tipoPrestamo;
	}
	public void setTipoPrestamo(TipoPrestamo tipoPrestamo) {
		this.tipoPrestamo = tipoPrestamo;
	}
	public BigDecimal getInteresTotal() {
		return interesTotal;
	}
	public void setInteresTotal(BigDecimal interesTotal) {
		this.interesTotal = interesTotal;
	}
}

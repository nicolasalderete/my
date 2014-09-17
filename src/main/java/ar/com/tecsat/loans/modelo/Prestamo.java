package ar.com.tecsat.loans.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * The persistent class for the prestamo database table.
 * 
 */
@Entity
@Table(name = "prestamo")
@NamedQueries({ @NamedQuery(name = "findAllPrestamos", query = "select p from Prestamo p") })
public class Prestamo implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	
	private BigDecimal preCapital;
	private Integer preCantCuotas;
	private Integer preCantMeses;
	private Double preTasaMensual;
	private BigDecimal preInteresTotal;
	private TipoPrestamo tipoPrestamo;

	private String preEstado;
	private Date preFechaEstado;
	private Date preFechaInicio;
	private List<Pago> pagos;
	private Cliente cliente;
	private List<Cuota> cuotas;
	
	@SuppressWarnings("unused")
	private BigDecimal preMontoTotal;

	public Prestamo() {
	}

	@PrePersist
	public void init() {
		setPreEstado(PrestamoEstado.VIGENTE.toString());
		setPreFechaEstado(Calendar.getInstance().getTime());
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pre_id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer preId) {
		this.id = preId;
	}

	@Column(name = "pre_cant_cuotas", nullable = false)
	public Integer getPreCantCuotas() {
		return this.preCantCuotas;
	}

	public void setPreCantCuotas(Integer preCantCuotas) {
		this.preCantCuotas = preCantCuotas;
	}

	@Column(name = "pre_capital", nullable = false, precision = 2)
	public BigDecimal getPreCapital() {
		return this.preCapital;
	}

	public void setPreCapital(BigDecimal preCapital) {
		this.preCapital = preCapital;
	}

	@Column(name = "pre_estado", nullable = false, length = 20)
	public String getPreEstado() {
		return this.preEstado;
	}

	public void setPreEstado(String preEstado) {
		this.preEstado = preEstado;
	}

	@Column(name = "pre_festado", nullable = false)
	public Date getPreFechaEstado() {
		return this.preFechaEstado;
	}

	public void setPreFechaEstado(Date preFestado) {
		this.preFechaEstado = preFestado;
	}

	@Column(name = "pre_finicio")
	public Date getPreFechaInicio() {
		return this.preFechaInicio;
	}

	public void setPreFechaInicio(Date preFinicio) {
		this.preFechaInicio = preFinicio;
	}

	@Column(name = "pre_tasa", nullable = false, precision = 2)
	public Double getPreTasaMensual() {
		return this.preTasaMensual;
	}

	public void setPreTasaMensual(Double preTasa) {
		this.preTasaMensual = preTasa;
	}

	// bi-directional many-to-one association to Pago
	@OneToMany(mappedBy = "prestamo")
	public List<Pago> getPagos() {
		return this.pagos;
	}

	public void setPagos(List<Pago> pagos) {
		this.pagos = pagos;
	}

	// bi-directional many-to-one association to Cliente
	@ManyToOne
	@JoinColumn(name = "cli_id", nullable = false)
	public Cliente getCliente() {
		return this.cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	@OneToMany(mappedBy = "prestamo", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	public List<Cuota> getCuotas() {
		return cuotas;
	}

	public void setCuotas(List<Cuota> cuotas) {
		this.cuotas = cuotas;
	}

	@Column(name = "pre_intereses", nullable = false, precision = 2)
	public BigDecimal getPreInteresTotal() {
		return preInteresTotal;
	}

	public void setPreInteresTotal(BigDecimal preIntereses) {
		this.preInteresTotal = preIntereses;
	}

	@Transient
	public BigDecimal getPreMontoTotal() {
		return this.preCapital.add(this.preInteresTotal);
	}

	public void setPreMontoTotal(BigDecimal preTotalMonto) {
		this.preMontoTotal = preTotalMonto;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, name = "pre_tipo")
	public TipoPrestamo getTipoPrestamo() {
		return tipoPrestamo;
	}

	public void setTipoPrestamo(TipoPrestamo tipoPrestamo) {
		this.tipoPrestamo = tipoPrestamo;
	}

	@Column(name = "pre_cant_meses", nullable = false)
	public Integer getPreCantMeses() {
		return preCantMeses;
	}

	public void setPreCantMeses(Integer preCantMeses) {
		this.preCantMeses = preCantMeses;
	}

}
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

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pre_id", unique = true, nullable = false)
	private Integer id;
	
	@Column(name = "pre_capital", nullable = false, precision = 2)
	private BigDecimal preCapital;
	
	@Column(name = "pre_cant_cuotas", nullable = false)
	private Integer preCantCuotas;
	
	@Column(name = "pre_tasa", nullable = false, precision = 2)
	private Double preTasa;
	
	@Column(name = "pre_intereses", nullable = false, precision = 2)
	private BigDecimal preInteresTotal;
	
	@Column(name = "pre_cuota_pura", nullable = false, precision = 2)
	private BigDecimal preCuotaPura;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, name = "pre_tipo")
	private TipoPrestamo tipoPrestamo;
	
	@Column(name = "pre_estado", nullable = false, length = 20)
	private String preEstado;
	
	@Column(name = "pre_festado", nullable = false)
	private Date preFechaEstado;
	
	@Column(name = "pre_finicio")
	private Date preFechaInicio;
	
	@Column(name = "pre_monto_total")
	private BigDecimal preMontoTotal;

	@ManyToOne
	@JoinColumn(name = "cli_id", nullable = false)
	private Cliente cliente;
	
	@OneToMany(mappedBy = "prestamo")
	private List<Pago> pagos;
	
	@OneToMany(mappedBy = "prestamo", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	private List<Cuota> cuotas;

	public Prestamo() {
	}

	@PrePersist
	public void init() {
		setPreEstado(PrestamoEstado.VIGENTE.toString());
		setPreFechaEstado(Calendar.getInstance().getTime());
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer preId) {
		this.id = preId;
	}

	public Integer getPreCantCuotas() {
		return this.preCantCuotas;
	}

	public void setPreCantCuotas(Integer preCantCuotas) {
		this.preCantCuotas = preCantCuotas;
	}

	public BigDecimal getPreCapital() {
		return this.preCapital;
	}

	public void setPreCapital(BigDecimal preCapital) {
		this.preCapital = preCapital;
	}

	public String getPreEstado() {
		return this.preEstado;
	}

	public void setPreEstado(String preEstado) {
		this.preEstado = preEstado;
	}

	public Date getPreFechaEstado() {
		return this.preFechaEstado;
	}

	public void setPreFechaEstado(Date preFestado) {
		this.preFechaEstado = preFestado;
	}

	public Date getPreFechaInicio() {
		return this.preFechaInicio;
	}

	public void setPreFechaInicio(Date preFinicio) {
		this.preFechaInicio = preFinicio;
	}

	public Double getPreTasa() {
		return preTasa;
	}
	
	public void setPreTasa(Double preTasa) {
		this.preTasa = preTasa;
	}
	
	// bi-directional many-to-one association to Pago
	public List<Pago> getPagos() {
		return this.pagos;
	}

	public void setPagos(List<Pago> pagos) {
		this.pagos = pagos;
	}

	// bi-directional many-to-one association to Cliente
	public Cliente getCliente() {
		return this.cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public List<Cuota> getCuotas() {
		return cuotas;
	}

	public void setCuotas(List<Cuota> cuotas) {
		this.cuotas = cuotas;
	}

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

	public TipoPrestamo getTipoPrestamo() {
		return tipoPrestamo;
	}

	public void setTipoPrestamo(TipoPrestamo tipoPrestamo) {
		this.tipoPrestamo = tipoPrestamo;
	}

	public BigDecimal getPreCuotaPura() {
		return preCuotaPura;
	}

	public void setPreCuotaPura(BigDecimal preCuotaPura) {
		this.preCuotaPura = preCuotaPura;
	}

}
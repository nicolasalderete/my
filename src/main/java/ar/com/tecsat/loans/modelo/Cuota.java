package ar.com.tecsat.loans.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * The persistent class for the cuota database table.
 * 
 */
@Entity
@Table(name = "cuota")
@NamedQueries({ @NamedQuery(name = "findCuotas", query = "select c from Cuota c"),
		@NamedQuery(name = "findCuotasByFecha", query = "select c from Cuota c where c.cuoFechaVencimiento BETWEEN :start AND :end"),
		@NamedQuery(name = "findCuotasByPrestamo", query = "select c from Cuota c where c.prestamo.id=:prestamo") })
public class Cuota implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cuo_id", unique = true, nullable = false)
	private Integer cuoId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "cuo_fvencimiento", nullable = false)
	private Date cuoFechaVencimiento;

	@Column(name = "cuo_importe", nullable = false, precision = 10, scale = 2)
	private BigDecimal cuoImporte;

	@Column(name = "cuo_numero", nullable = false)
	private int cuoNumero;

	@Column(name = "cuo_sal_deudor", precision = 10, scale = 2)
	private BigDecimal cuoSaldoDeudor;

	@Column(name = "cuo_sal_favor", precision = 10, scale = 2)
	private BigDecimal cuoSaldoFavor;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "pre_id", nullable = false)
	private Prestamo prestamo;

	@Column(name = "cuo_estado", nullable = false, length = 25)
	private String cuoEstado;

	@Column(name = "cuo_intereses", precision = 10, scale = 2)
	private BigDecimal cuoInteres;

	@Column(name = "cuo_interes_punitorio", precision = 10, scale = 2)
	private BigDecimal cuoInteresPunitorio;

	@Column(name = "cuo_saldo", precision = 10, scale = 2)
	private BigDecimal cuoSaldo;

	@SuppressWarnings("unused")
	@Transient
	private BigDecimal cuoTotalPagar;

	@Column(name = "cuo_tasa_mensual", nullable = false, precision = 2)
	private Double cuoTasaMensual;

	@OneToMany(mappedBy = "cuota", cascade = CascadeType.PERSIST)
	private List<Pago> pagos;

	public Cuota() {
	}

	public Integer getCuoId() {
		return this.cuoId;
	}

	public void setCuoId(Integer cuoId) {
		this.cuoId = cuoId;
	}

	public Date getCuoFechaVencimiento() {
		return this.cuoFechaVencimiento;
	}

	public void setCuoFechaVencimiento(Date cuoFvencimiento) {
		this.cuoFechaVencimiento = cuoFvencimiento;
	}

	public BigDecimal getCuoImporte() {
		return this.cuoImporte;
	}

	public void setCuoImporte(BigDecimal cuoImporte) {
		this.cuoImporte = cuoImporte;
	}

	public int getCuoNumero() {
		return this.cuoNumero;
	}

	public void setCuoNumero(int cuoNumero) {
		this.cuoNumero = cuoNumero;
	}

	public BigDecimal getCuoSaldoDeudor() {
		return this.cuoSaldoDeudor;
	}

	public void setCuoSaldoDeudor(BigDecimal cuoSalDeudor) {
		this.cuoSaldoDeudor = cuoSalDeudor;
	}

	public BigDecimal getCuoSaldoFavor() {
		return this.cuoSaldoFavor;
	}

	public void setCuoSaldoFavor(BigDecimal cuoSalFavor) {
		this.cuoSaldoFavor = cuoSalFavor;
	}

	public Prestamo getPrestamo() {
		return prestamo;
	}

	public void setPrestamo(Prestamo prestamo) {
		this.prestamo = prestamo;
	}

	public String getCuoEstado() {
		return cuoEstado;
	}

	public void setCuoEstado(String cuoEstado) {
		this.cuoEstado = cuoEstado;
	}

	public BigDecimal getCuoInteres() {
		return cuoInteres;
	}

	public void setCuoInteres(BigDecimal cuoIntereses) {
		this.cuoInteres = cuoIntereses;
	}

	public BigDecimal getCuoTotalPagar() {
		return this.cuoImporte.add(this.cuoInteresPunitorio).add(this.cuoSaldoDeudor).subtract(this.cuoSaldoFavor);
	}

	public void setCuoTotalPagar(BigDecimal cuoTotal) {
		this.cuoTotalPagar = cuoTotal;
	}

	public List<Pago> getPagos() {
		return pagos;
	}

	public void setPagos(List<Pago> pagos) {
		this.pagos = pagos;
	}

	public Double getCuoTasaMensual() {
		return cuoTasaMensual;
	}

	public void setCuoTasaMensual(Double tasaMensual) {
		this.cuoTasaMensual = tasaMensual;
	}

	public BigDecimal getCuoInteresPunitorio() {
		return cuoInteresPunitorio;
	}

	public void setCuoInteresPunitorio(BigDecimal interesPunitorio) {
		this.cuoInteresPunitorio = interesPunitorio;
	}

	public BigDecimal getCuoSaldo() {
		return cuoSaldo;
	}

	public void setCuoSaldo(BigDecimal saldo) {
		this.cuoSaldo = saldo;
	}

	@Override
	public String toString() {
		return String.format("Cuota: %s de %s - Cliente: %s", this.cuoNumero, this.prestamo.getPreCantCuotas(),
				this.prestamo.getCliente().getCliNombre());
	}
}
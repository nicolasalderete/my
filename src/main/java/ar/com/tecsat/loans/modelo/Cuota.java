package ar.com.tecsat.loans.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import ar.com.tecsat.loans.bean.CuotaEstado;

/**
 * The persistent class for the cuota database table.
 * 
 */
@Entity
@Table(name = "cuota")
@NamedQueries({
		@NamedQuery(name = "findCuotas", query = "select c from Cuota c"),
		@NamedQuery(name = "findCuotasByFecha", query = "select c from Cuota c where c.cuoFechaVencimiento BETWEEN :start AND :end"),
		@NamedQuery(name = "findCuotasByPrestamo", query = "select c from Cuota c where c.prestamo.id=:prestamo"),
		@NamedQuery(name = "findCuotaByFechaVto", query = "select c from Cuota c where c.cuoFechaVencimiento=:CURRENT_DATE") })
public class Cuota implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cuo_id", unique = true, nullable = false)
	private Integer cuoId;

	@Column(name = "cuo_numero", nullable = false)
	private int cuoNumero;

	@Column(name = "cuo_importe", nullable = false, precision = 10, scale = 2)
	private BigDecimal cuoImporte;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "cuo_fvencimiento", nullable = false)
	private Date cuoFechaVencimiento;

	@Column(name = "cuo_pago_parcial", precision = 10, scale = 2)
	private BigDecimal cuoPagoParcial;

	@Column(name = "cuo_pura", nullable = false, precision = 10, scale = 2)
	private BigDecimal cuoPura;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, name = "cuo_estado")
	private CuotaEstado cuoEstado;

	@Column(name = "cuo_interes_punitorio", precision = 10, scale = 2)
	private BigDecimal cuoInteresPunitorio;

	@Column(name = "cuo_saldo", precision = 10, scale = 2)
	private BigDecimal cuoSaldo;

	@Column(name = "cuo_interes", nullable = false, precision = 2)
	private BigDecimal cuoInteres;

	@ManyToOne
	@JoinColumn(name = "pre_id", nullable = false)
	private Prestamo prestamo;

	@OneToMany(mappedBy = "cuota", cascade = CascadeType.REMOVE)
	private List<Pago> pagos;

	public Cuota() {
	}
	
	public Integer getCuoId() {
		return cuoId;
	}

	public void setCuoId(Integer cuoId) {
		this.cuoId = cuoId;
	}

	public int getCuoNumero() {
		return cuoNumero;
	}

	public void setCuoNumero(int cuoNumero) {
		this.cuoNumero = cuoNumero;
	}

	public BigDecimal getCuoImporte() {
		return cuoImporte;
	}

	public void setCuoImporte(BigDecimal cuoImporte) {
		this.cuoImporte = cuoImporte;
	}

	public Date getCuoFechaVencimiento() {
		return cuoFechaVencimiento;
	}

	public void setCuoFechaVencimiento(Date cuoFechaVencimiento) {
		this.cuoFechaVencimiento = cuoFechaVencimiento;
	}

	public BigDecimal getCuoPagoParcial() {
		return cuoPagoParcial;
	}

	public void setCuoPagoParcial(BigDecimal cuoPagoParcial) {
		this.cuoPagoParcial = cuoPagoParcial;
	}

	public BigDecimal getCuoPura() {
		return cuoPura;
	}

	public void setCuoPura(BigDecimal cuoPura) {
		this.cuoPura = cuoPura;
	}

	public CuotaEstado getCuoEstado() {
		return cuoEstado;
	}

	public void setCuoEstado(CuotaEstado cuoEstado) {
		this.cuoEstado = cuoEstado;
	}

	public BigDecimal getCuoInteresPunitorio() {
		return cuoInteresPunitorio;
	}

	public void setCuoInteresPunitorio(BigDecimal cuoInteresPunitorio) {
		this.cuoInteresPunitorio = cuoInteresPunitorio;
	}

	public BigDecimal getCuoSaldo() {
		return cuoSaldo;
	}

	public void setCuoSaldo(BigDecimal cuoSaldo) {
		this.cuoSaldo = cuoSaldo;
	}

	public BigDecimal getCuoInteres() {
		return cuoInteres;
	}

	public void setCuoInteres(BigDecimal cuoInteres) {
		this.cuoInteres = cuoInteres;
	}

	public Prestamo getPrestamo() {
		return prestamo;
	}

	public void setPrestamo(Prestamo prestamo) {
		this.prestamo = prestamo;
	}

	public List<Pago> getPagos() {
		return pagos;
	}

	public void setPagos(List<Pago> pagos) {
		this.pagos = pagos;
	}

	@Override
	public String toString() {
		return String.format("Cuota %s de %s, correspondiente a un prestamo de $%s. Estado de la cuota %s",
				this.cuoNumero, this.prestamo.getPreCantCuotas(), this.prestamo.getPreCapital(), this.cuoEstado);
	}

	public boolean isPagable() {
		return tieneSaldoAPagar();
	}

	public boolean isVencida() {
		return this.cuoEstado.equals(CuotaEstado.VENCIDA) || this.cuoEstado.equals(CuotaEstado.PAGO_PARCIAL_VENCIDO);
	}

	private boolean tieneSaldoAPagar() {
		return this.cuoImporte.compareTo(BigDecimal.valueOf(0)) > 0;
	}

	public boolean isCancelada() {
		return this.cuoEstado.equals(CuotaEstado.CANCELADA);
	}

}
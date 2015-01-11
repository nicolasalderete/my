package ar.com.tecsat.loans.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the pago database table.
 * 
 */
@Entity
@Table(name="pago")
@NamedQueries({
	@NamedQuery(name="findAllPagos",
				query="select p from Pago p")
})
public class Pago implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer pagId;
	private String pagEstado;
	private Date pagFestado;
	private BigDecimal pagMonto;
//	private Cliente cliente;
//	private Prestamo prestamo;
	private Cuota cuota;

    public Pago() {
    }

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="pag_id", unique=true, nullable=false)
	public Integer getPagId() {
		return this.pagId;
	}

	public void setPagId(Integer pagId) {
		this.pagId = pagId;
	}

	@Column(name="pag_estado", nullable=false, length=20)
	public String getPagEstado() {
		return this.pagEstado;
	}

	public void setPagEstado(String pagEstado) {
		this.pagEstado = pagEstado;
	}

	@Column(name="pag_festado", nullable=false)
	public Date getPagFestado() {
		return this.pagFestado;
	}

	public void setPagFestado(Date pagFestado) {
		this.pagFestado = pagFestado;
	}

	@Column(name="pag_monto", nullable=false)
	public BigDecimal getPagMonto() {
		return this.pagMonto;
	}

	public void setPagMonto(BigDecimal pagMonto) {
		this.pagMonto = pagMonto;
	}

//	//bi-directional many-to-one association to Cliente
//    @ManyToOne
//	@JoinColumn(name="cli_id", nullable=false)
//	public Cliente getCliente() {
//		return this.cliente;
//	}
//
//	public void setCliente(Cliente cliente) {
//		this.cliente = cliente;
//	}

	//bi-directional many-to-one association to Prestamo
//    @ManyToOne
//	@JoinColumn(name="pre_id", nullable=false)
//	public Prestamo getPrestamo() {
//		return this.prestamo;
//	}
//
//	public void setPrestamo(Prestamo prestamo) {
//		this.prestamo = prestamo;
//	}

	//bi-directional many-to-one association to Cuota
    @ManyToOne
	@JoinColumn(name="cuo_id", nullable=false)
	public Cuota getCuota() {
		return cuota;
	}

	public void setCuota(Cuota cuota) {
		this.cuota = cuota;
	}
	
}
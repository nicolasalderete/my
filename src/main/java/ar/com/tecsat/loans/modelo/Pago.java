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
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="pag_id", unique=true, nullable=false)
	private Integer pagId;

	@Column(name="pag_estado", nullable=false, length=20)
	private String pagEstado;

	@Column(name="pag_festado", nullable=false)
	private Date pagFestado;

	@Column(name="pag_monto", nullable=false)
	private BigDecimal pagMonto;

	@ManyToOne
	@JoinColumn(name="cuo_id", nullable=false)
	private Cuota cuota;

    public Pago() {
    }

	public Integer getPagId() {
		return this.pagId;
	}

	public void setPagId(Integer pagId) {
		this.pagId = pagId;
	}

	public String getPagEstado() {
		return this.pagEstado;
	}

	public void setPagEstado(String pagEstado) {
		this.pagEstado = pagEstado;
	}

	public Date getPagFestado() {
		return this.pagFestado;
	}

	public void setPagFestado(Date pagFestado) {
		this.pagFestado = pagFestado;
	}

	public BigDecimal getPagMonto() {
		return this.pagMonto;
	}

	public void setPagMonto(BigDecimal pagMonto) {
		this.pagMonto = pagMonto;
	}

	public Cuota getCuota() {
		return cuota;
	}

	public void setCuota(Cuota cuota) {
		this.cuota = cuota;
	}
	
}
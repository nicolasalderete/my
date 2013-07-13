package ar.com.tecsat.loans.modelo;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the operador database table.
 * 
 */
@Entity
@Table(name="operador")
@NamedQueries({
	@NamedQuery(name="findByUserAndPass", 
				query="select o from Operador o where o.opeUsername=:user and o.opePassword=:pass")
})
public class Operador implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer opeId;
	private String opeEstado;
	private Calendar opeFestado;
	private String opePassword;
	private String opeUsername;

    public Operador() {
    }

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ope_id", unique=true, nullable=false)
	public Integer getOpeId() {
		return this.opeId;
	}

	public void setOpeId(Integer opeId) {
		this.opeId = opeId;
	}

	@Column(name="ope_estado", nullable=false, length=20)
	public String getOpeEstado() {
		return this.opeEstado;
	}

	public void setOpeEstado(String opeEstado) {
		this.opeEstado = opeEstado;
	}


	@Column(name="ope_festado", nullable=false)
	public Calendar getOpeFestado() {
		return this.opeFestado;
	}

	public void setOpeFestado(Calendar opeFestado) {
		this.opeFestado = opeFestado;
	}


	@Column(name="ope_password", nullable=false, length=50)
	public String getOpePassword() {
		return this.opePassword;
	}

	public void setOpePassword(String opePassword) {
		this.opePassword = opePassword;
	}


	@Column(name="ope_username", nullable=false, length=20)
	public String getOpeUsername() {
		return this.opeUsername;
	}

	public void setOpeUsername(String opeUsername) {
		this.opeUsername = opeUsername;
	}

}
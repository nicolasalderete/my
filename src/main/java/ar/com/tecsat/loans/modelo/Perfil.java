package ar.com.tecsat.loans.modelo;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.Table;


/**
 * The persistent class for the perfil database table.
 * 
 */
@Entity
@Table(name="perfil")
@NamedQuery(name = "findPerfil", query = "select p from Perfil p")
public class Perfil implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer perId;
	private String perCbu;
	private String perCelular;
	private String perCuenta;
	private String perDireccion;
	private String perDni;
	private String perEstado;
	private Calendar perFestado;
	private String perLocalidad;
	private String perMail;
	private String perNombre;
	private String perTelefono;
	private String perBanco;
	private String perTipoCuenta;

    public Perfil() {
    }

	@PrePersist
	public void init(){
		setPerEstado(PerfilEstado.HABILITADO.toString());
		setPerFestado(Calendar.getInstance());
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="per_id", unique=true, nullable=false)
	public Integer getPerId() {
		return this.perId;
	}

	public void setPerId(Integer perId) {
		this.perId = perId;
	}


	@Column(name="per_cbu", length=22)
	public String getPerCbu() {
		return this.perCbu;
	}

	public void setPerCbu(String perCbu) {
		this.perCbu = perCbu;
	}


	@Column(name="per_celular", length=50)
	public String getPerCelular() {
		return this.perCelular;
	}

	public void setPerCelular(String perCelular) {
		this.perCelular = perCelular;
	}


	@Column(name="per_cuenta", length=20)
	public String getPerCuenta() {
		return this.perCuenta;
	}

	public void setPerCuenta(String perCuenta) {
		this.perCuenta = perCuenta;
	}


	@Column(name="per_direccion", length=100)
	public String getPerDireccion() {
		return this.perDireccion;
	}

	public void setPerDireccion(String perDireccion) {
		this.perDireccion = perDireccion;
	}


	@Column(name="per_dni", nullable=false, length=10)
	public String getPerDni() {
		return this.perDni;
	}

	public void setPerDni(String perDni) {
		this.perDni = perDni;
	}

	@Column(name="per_estado", length=20)
	public String getPerEstado() {
		return this.perEstado;
	}

	public void setPerEstado(String perEstado) {
		this.perEstado = perEstado;
	}


	@Column(name="per_festado", nullable=false)
	public Calendar getPerFestado() {
		return this.perFestado;
	}

	public void setPerFestado(Calendar perFestado) {
		this.perFestado = perFestado;
	}


	@Column(name="per_localidad", length=100)
	public String getPerLocalidad() {
		return this.perLocalidad;
	}

	public void setPerLocalidad(String perLocalidad) {
		this.perLocalidad = perLocalidad;
	}


	@Column(name="per_mail", length=100)
	public String getPerMail() {
		return this.perMail;
	}

	public void setPerMail(String perMail) {
		this.perMail = perMail;
	}


	@Column(name="per_nombre", nullable=false, length=50)
	public String getPerNombre() {
		return this.perNombre;
	}

	public void setPerNombre(String perNombre) {
		this.perNombre = perNombre;
	}


	@Column(name="per_telefono", length=50)
	public String getPerTelefono() {
		return this.perTelefono;
	}

	public void setPerTelefono(String perTelefono) {
		this.perTelefono = perTelefono;
	}

	@Column(name="banco", length=50)
	public String getPerBanco() {
		return perBanco;
	}

	public void setPerBanco(String banco) {
		this.perBanco = banco;
	}
	
	@Column(name="tipo_cta", length=50)
	public String getPerTipoCuenta() {
		return perTipoCuenta;
	}

	public void setPerTipoCuenta(String tipoCuenta) {
		this.perTipoCuenta = tipoCuenta;
	}

}
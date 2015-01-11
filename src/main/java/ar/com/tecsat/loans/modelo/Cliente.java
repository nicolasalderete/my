package ar.com.tecsat.loans.modelo;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;


/**
 * The persistent class for the cliente database table.
 * 
 */
@Entity
@Table(name="cliente")
@NamedQueries({
	@NamedQuery(name="findClientes", 
			query="select c from Cliente c"),
	@NamedQuery(name="findByPk",
			query="select c from Cliente c where c.cliId=:cliId"),
	@NamedQuery(name="findClienteByDni",
			query="select c from Cliente c where c.cliDni=:dni")
})
public class Cliente implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer cliId;
	private String cliCbu;
	private String cliCelular;
	private String cliCuenta;
	private String cliDireccion;
	private String cliDni;
	private String cliEstado;
	private Date cliFestado;
	private String cliLocalidad;
	private String cliMail;
	private String cliNombre;
	private String cliTelefono;
	private String cliBanco;
	private String cliEntreCalle;
	private String cliFacebook;
	private String cliTipoCuenta;
//	private List<Pago> pagos;
	private List<Prestamo> prestamos;

    public Cliente() {
    }


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="cli_id", unique=true, nullable=false)
	public Integer getCliId() {
		return this.cliId;
	}

	public void setCliId(Integer cliId) {
		this.cliId = cliId;
	}


	@Column(name="cli_cbu", length=22)
	public String getCliCbu() {
		return this.cliCbu;
	}

	public void setCliCbu(String cliCbu) {
		this.cliCbu = cliCbu;
	}


	@Column(name="cli_celular", length=50)
	public String getCliCelular() {
		return this.cliCelular;
	}

	public void setCliCelular(String cliCelular) {
		this.cliCelular = cliCelular;
	}


	@Column(name="cli_cuenta", length=20)
	public String getCliCuenta() {
		return this.cliCuenta;
	}

	public void setCliCuenta(String cliCuenta) {
		this.cliCuenta = cliCuenta;
	}


	@Column(name="cli_direccion", length=100)
	public String getCliDireccion() {
		return this.cliDireccion;
	}

	public void setCliDireccion(String cliDireccion) {
		this.cliDireccion = cliDireccion;
	}


	@Column(name="cli_dni", nullable=false, length=10)
	public String getCliDni() {
		return this.cliDni;
	}

	public void setCliDni(String cliDni) {
		this.cliDni = cliDni;
	}

	@Column(name="cli_estado", length=20)
	public String getCliEstado() {
		return this.cliEstado;
	}

	public void setCliEstado(String cliEstado) {
		this.cliEstado = cliEstado;
	}


	@Column(name="cli_festado", nullable=false)
	public Date getCliFestado() {
		return this.cliFestado;
	}

	public void setCliFestado(Date cliFestado) {
		this.cliFestado = cliFestado;
	}


	@Column(name="cli_localidad", length=100)
	public String getCliLocalidad() {
		return this.cliLocalidad;
	}

	public void setCliLocalidad(String cliLocalidad) {
		this.cliLocalidad = cliLocalidad;
	}


	@Column(name="cli_mail", length=100)
	public String getCliMail() {
		return this.cliMail;
	}

	public void setCliMail(String cliMail) {
		this.cliMail = cliMail;
	}


	@Column(name="cli_nombre", nullable=false, length=50)
	public String getCliNombre() {
		return this.cliNombre;
	}

	public void setCliNombre(String cliNombre) {
		this.cliNombre = cliNombre;
	}


	@Column(name="cli_telefono", length=50)
	public String getCliTelefono() {
		return this.cliTelefono;
	}

	public void setCliTelefono(String cliTelefono) {
		this.cliTelefono = cliTelefono;
	}


//	//bi-directional many-to-one association to Pago
//	@OneToMany(mappedBy="cliente")
//	public List<Pago> getPagos() {
//		return this.pagos;
//	}
//
//	public void setPagos(List<Pago> pagos) {
//		this.pagos = pagos;
//	}
//	
//
	//bi-directional many-to-one association to Prestamo
	@OneToMany(mappedBy="cliente")
	public List<Prestamo> getPrestamos() {
		return this.prestamos;
	}

	public void setPrestamos(List<Prestamo> prestamos) {
		this.prestamos = prestamos;
	}
	
	@PrePersist
	public void setEstadoAndFecha(){
		setCliEstado(ClienteEstado.HABILITADO.toString());
		setCliFestado(Calendar.getInstance().getTime());
	}

	@Column(name="banco", length=50)
	public String getCliBanco() {
		return cliBanco;
	}

	public void setCliBanco(String banco) {
		this.cliBanco = banco;
	}

	@Column(name="tipo_cta", length=50)
	public String getCliTipoCuenta() {
		return cliTipoCuenta;
	}


	public void setCliTipoCuenta(String tipoCuenta) {
		this.cliTipoCuenta = tipoCuenta;
	}

	@Column(name="entre_calle", length=100)
	public String getCliEntreCalle() {
		return cliEntreCalle;
	}

	public void setCliEntreCalle(String entreCalle) {
		this.cliEntreCalle = entreCalle;
	}

	@Column(name="url_facebook", length=100)
	public String getCliFacebook() {
		return cliFacebook;
	}

	public void setCliFacebook(String facebook) {
		this.cliFacebook = facebook;
	}

	@Override
	public String toString() {
		return String.format("Cliente: %s - Dni: %s", this.cliNombre, this.cliDni) ;
	}
}
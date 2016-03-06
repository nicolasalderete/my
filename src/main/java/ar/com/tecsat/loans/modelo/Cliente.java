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
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="cli_id", unique=true, nullable=false)
	private Integer cliId;
	@Column(name="cli_cbu", length=22)
	private String cliCbu;
	@Column(name="cli_celular", length=50)
	private String cliCelular;
	@Column(name="cli_cuenta", length=20)	
	private String cliCuenta;
	@Column(name="cli_direccion", length=100)
	private String cliDireccion;
	@Column(name="cli_dni", nullable=false, length=10)
	private String cliDni;
	@Column(name="cli_estado", length=20)
	private String cliEstado;
	@Column(name="cli_festado", nullable=false)
	private Date cliFestado;
	@Column(name="cli_localidad", length=100)
	private String cliLocalidad;
	@Column(name="cli_mail", length=100)
	private String cliMail;
	@Column(name="cli_nombre", nullable=false, length=50)
	private String cliNombre;
	@Column(name="cli_telefono", length=50)
	private String cliTelefono;
	@Column(name="banco", length=50)
	private String cliBanco;
	@Column(name="entre_calle", length=100)
	private String cliEntreCalle;
	@Column(name="url_facebook", length=100)
	private String cliFacebook;
	@Column(name="tipo_cta", length=50)
	private String cliTipoCuenta;
	@Column(name="cli_barrio", length=100)
	private String cliBarrio;
	@OneToMany(mappedBy = "cliente")
	private List<Prestamo> prestamos;
	
    public Cliente() {
    }
	public Integer getCliId() {
		return this.cliId;
	}
	public void setCliId(Integer cliId) {
		this.cliId = cliId;
	}
	public String getCliCbu() {
		return this.cliCbu;
	}
	public void setCliCbu(String cliCbu) {
		this.cliCbu = cliCbu;
	}
	public String getCliCelular() {
		return this.cliCelular;
	}
	public void setCliCelular(String cliCelular) {
		this.cliCelular = cliCelular;
	}
	public String getCliCuenta() {
		return this.cliCuenta;
	}
	public void setCliCuenta(String cliCuenta) {
		this.cliCuenta = cliCuenta;
	}
	public String getCliDireccion() {
		return this.cliDireccion;
	}
	public void setCliDireccion(String cliDireccion) {
		this.cliDireccion = cliDireccion;
	}
	public String getCliDni() {
		return this.cliDni;
	}
	public void setCliDni(String cliDni) {
		this.cliDni = cliDni;
	}
	public String getCliEstado() {
		return this.cliEstado;
	}
	public void setCliEstado(String cliEstado) {
		this.cliEstado = cliEstado;
	}
	public Date getCliFestado() {
		return this.cliFestado;
	}
	public void setCliFestado(Date cliFestado) {
		this.cliFestado = cliFestado;
	}
	public String getCliLocalidad() {
		return this.cliLocalidad;
	}
	public void setCliLocalidad(String cliLocalidad) {
		this.cliLocalidad = cliLocalidad;
	}
	public String getCliMail() {
		return this.cliMail;
	}
	public void setCliMail(String cliMail) {
		this.cliMail = cliMail;
	}
	public String getCliNombre() {
		return this.cliNombre;
	}
	public void setCliNombre(String cliNombre) {
		this.cliNombre = cliNombre;
	}
	public String getCliTelefono() {
		return this.cliTelefono;
	}
	public void setCliTelefono(String cliTelefono) {
		this.cliTelefono = cliTelefono;
	}
	public String getCliBanco() {
		return cliBanco;
	}
	public void setCliBanco(String banco) {
		this.cliBanco = banco;
	}
	public String getCliTipoCuenta() {
		return cliTipoCuenta;
	}
	public void setCliTipoCuenta(String tipoCuenta) {
		this.cliTipoCuenta = tipoCuenta;
	}
	public String getCliEntreCalle() {
		return cliEntreCalle;
	}
	public void setCliEntreCalle(String entreCalle) {
		this.cliEntreCalle = entreCalle;
	}
	public String getCliFacebook() {
		return cliFacebook;
	}
	public void setCliFacebook(String facebook) {
		this.cliFacebook = facebook;
	}
	public String getCliBarrio() {
		return cliBarrio;
	}
	public void setCliBarrio(String cliBarrio) {
		this.cliBarrio = cliBarrio;
	}

	@Override
	public String toString() {
		return String.format("Cliente: %s - Dni: %s", this.cliNombre, this.cliDni) ;
	}

	@PrePersist
	public void setEstadoAndFecha(){
		setCliEstado(ClienteEstado.HABILITADO.toString());
		setCliFestado(Calendar.getInstance().getTime());
	}
	public List<Prestamo> getPrestamos() {
		return prestamos;
	}
	public void setPrestamos(List<Prestamo> prestamos) {
		this.prestamos = prestamos;
	}
}
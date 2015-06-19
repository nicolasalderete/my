package ar.com.tecsat.loans.bean.utils;

import java.io.Serializable;
import java.util.Date;

/**
 * @author nicolas
 *
 */
@SuppressWarnings("serial")
public class ClienteFiltro implements Serializable {
	
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
	private String banco;
	private String cliBarrio;
	
	public String getCliCbu() {
		return cliCbu;
	}
	public void setCliCbu(String cliCbu) {
		this.cliCbu = cliCbu;
	}
	public String getCliCelular() {
		return cliCelular;
	}
	public void setCliCelular(String cliCelular) {
		this.cliCelular = cliCelular;
	}
	public String getCliCuenta() {
		return cliCuenta;
	}
	public void setCliCuenta(String cliCuenta) {
		this.cliCuenta = cliCuenta;
	}
	public String getCliDireccion() {
		return cliDireccion;
	}
	public void setCliDireccion(String cliDireccion) {
		this.cliDireccion = cliDireccion;
	}
	public String getCliDni() {
		return cliDni;
	}
	public void setCliDni(String cliDni) {
		this.cliDni = cliDni;
	}
	public String getCliEstado() {
		return cliEstado;
	}
	public void setCliEstado(String cliEstado) {
		this.cliEstado = cliEstado;
	}
	public Date getCliFestado() {
		return cliFestado;
	}
	public void setCliFestado(Date cliFestado) {
		this.cliFestado = cliFestado;
	}
	public String getCliLocalidad() {
		return cliLocalidad;
	}
	public void setCliLocalidad(String cliLocalidad) {
		this.cliLocalidad = cliLocalidad;
	}
	public String getCliMail() {
		return cliMail;
	}
	public void setCliMail(String cliMail) {
		this.cliMail = cliMail;
	}
	public String getCliNombre() {
		return cliNombre;
	}
	public void setCliNombre(String cliNombre) {
		this.cliNombre = cliNombre;
	}
	public String getCliTelefono() {
		return cliTelefono;
	}
	public void setCliTelefono(String cliTelefono) {
		this.cliTelefono = cliTelefono;
	}
	public String getBanco() {
		return banco;
	}
	public void setBanco(String banco) {
		this.banco = banco;
	}
	public String getCliBarrio() {
		return cliBarrio;
	}
	public void setCliBarrio(String cliBarrio) {
		this.cliBarrio = cliBarrio;
	}
	
}

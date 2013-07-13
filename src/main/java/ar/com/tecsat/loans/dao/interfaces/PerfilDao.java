package ar.com.tecsat.loans.dao.interfaces;

import javax.ejb.Local;

import ar.com.tecsat.loans.exceptions.AdministrativeException;
import ar.com.tecsat.loans.modelo.Perfil;

/**
 * @author nicolas
 *
 */
@Local
public interface PerfilDao {
	
	public Perfil findPerfil() throws AdministrativeException;

	public void update(Perfil perfil) throws AdministrativeException;

	public void guardar(Perfil perfil) throws AdministrativeException;
	
}

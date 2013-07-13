package ar.com.tecsat.loans.service;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ar.com.tecsat.loans.dao.interfaces.PerfilDao;
import ar.com.tecsat.loans.exceptions.AdministrativeException;
import ar.com.tecsat.loans.modelo.Perfil;

/**
 * @author nicolas
 *
 */
@Stateless
public class PerfilService {

	@EJB
	private PerfilDao perfilDao;

	public Perfil findPerfil() throws AdministrativeException {
		Perfil perfil = null;
		try {
			perfil = perfilDao.findPerfil();
		} catch (AdministrativeException e) {
			throw new AdministrativeException(e.getMessage());
		}
		return perfil;
	}

	public void actualizarPerfil(Perfil perfil) throws AdministrativeException {
		try {
			perfilDao.update(perfil);
		} catch (AdministrativeException e) {
			throw new AdministrativeException(e.getMessage());
		}
	}

	public void guardarPerfil(Perfil perfil) throws AdministrativeException {
		try {
			perfilDao.guardar(perfil);
		} catch (AdministrativeException e) {
			throw new AdministrativeException(e.getMessage());
		}
	}
}

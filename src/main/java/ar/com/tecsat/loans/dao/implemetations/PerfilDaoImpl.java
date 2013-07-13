package ar.com.tecsat.loans.dao.implemetations;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import ar.com.tecsat.loans.dao.interfaces.PerfilDao;
import ar.com.tecsat.loans.exceptions.AdministrativeException;
import ar.com.tecsat.loans.modelo.Perfil;

/**
 * @author nicolas
 *
 */
@Stateless
public class PerfilDaoImpl implements PerfilDao {

	@PersistenceContext(unitName="Prest")
	private EntityManager em;
	
	@SuppressWarnings("unchecked")
	@Override
	public Perfil findPerfil() throws AdministrativeException {
		List<Perfil> perfiles = null;
		Query query = em.createNamedQuery("findPerfil");
		try {
			perfiles = ((List<Perfil>) query.getResultList());
		} catch (Exception e) {
			throw new AdministrativeException("Definir mensaje findPerfil");
		}
		if (perfiles.size() > 1) {
			throw new AdministrativeException("Error de datos");
		}
		if (perfiles.isEmpty()) {
			return null;
		}
		return perfiles.iterator().next();
	}

	@Override
	public void update(Perfil perfil) throws AdministrativeException {
		if (perfil == null) {
			throw new AdministrativeException("Definir mensaje de error update perfil");
		}
		try {
			em.merge(perfil);
		} catch (Exception e) {
			throw new AdministrativeException("Definir mensaje de error update perfil");
		}
		
	}

	public void guardar(Perfil perfil) throws AdministrativeException {
		if (perfil == null) {
			throw new AdministrativeException("Definir mensaje error guardar perfil");
		}
		try {
			em.persist(perfil);
		} catch (Exception e) {
			throw new AdministrativeException(e.getMessage());
		}
	}

}

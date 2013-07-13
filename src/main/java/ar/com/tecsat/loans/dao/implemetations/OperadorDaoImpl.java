package ar.com.tecsat.loans.dao.implemetations;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import ar.com.tecsat.loans.dao.interfaces.OperadorDao;
import ar.com.tecsat.loans.exceptions.AdministrativeException;
import ar.com.tecsat.loans.modelo.Operador;

/**
 * @author nicolas
 *
 */
@Stateless
public class OperadorDaoImpl implements OperadorDao {

	@PersistenceContext(unitName="Prest")
	private EntityManager em;
	
	@SuppressWarnings("unchecked")
	@Override
	public Operador findByUserAndPass(String user, String pass) throws AdministrativeException {
		Query query = em.createNamedQuery("findByUserAndPass");
		query.setParameter("user", user);
		query.setParameter("pass", pass);
		List<Operador> resultList = query.getResultList();
		if(resultList.isEmpty()) {
			throw new AdministrativeException("El usuario no existe");
		}
		if(resultList.size() == 1) {
			return resultList.iterator().next();
		}
		throw new AdministrativeException("Pongase en contacto con el administrador");
	}

}

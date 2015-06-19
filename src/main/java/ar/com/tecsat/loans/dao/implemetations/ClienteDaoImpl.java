package ar.com.tecsat.loans.dao.implemetations;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import ar.com.tecsat.loans.bean.utils.ClienteFiltro;
import ar.com.tecsat.loans.dao.interfaces.ClienteDao;
import ar.com.tecsat.loans.exceptions.AdministrativeException;
import ar.com.tecsat.loans.modelo.Cliente;

import com.google.common.base.Strings;

/**
 * @author nicolas
 *
 */
@Stateless
public class ClienteDaoImpl implements ClienteDao{
	
	@PersistenceContext(unitName="Prest")
	private EntityManager em;
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Cliente> findClientes() throws AdministrativeException {
		List<Cliente> clientes = null;
		try {
			Query query = em.createNamedQuery("findClientes");
			clientes = query.getResultList();
		} catch (Exception e) {
			throw new AdministrativeException(e.getMessage());
		}
		return clientes;
	}

	@Override
	public void saveCliente(Cliente cliente) throws AdministrativeException {
		if(cliente == null) {
			throw new AdministrativeException("Operación no realizada");
		}
		try {
			em.persist(cliente);
		} catch (Exception e){
			throw new AdministrativeException("Operación no realizada");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Cliente findClienteByDni(String dni) throws AdministrativeException {
		List<Cliente> clientes = null;
		try {
			Query query = em.createNamedQuery("findClienteByDni");
			query.setParameter("dni", dni);
			clientes = query.getResultList();
		} catch (Exception e) {
			throw new AdministrativeException(e.getMessage());
		}
		if (clientes.isEmpty() || clientes == null) {
			return null;
		}
		if (clientes.size() > 1) {
			throw new AdministrativeException("Error a definir");
		}
		return clientes.iterator().next();
	}

	@Override
	public void deleteCliente(Cliente cliente) throws AdministrativeException {
		if(cliente == null){
			throw new AdministrativeException("Operación no realizada");
		}
		try {
			em.remove(em.getReference(Cliente.class,cliente.getCliId()));
		} catch (Exception e) {
			throw new AdministrativeException(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Cliente findClienteByPk(Integer cliId) throws AdministrativeException {
		List<Cliente> clientes = null;
		try {
			Query query = em.createNamedQuery("findByPk");
			query.setParameter("cliId", cliId.intValue());
			clientes = query.getResultList();
		} catch (Exception e) {
			throw new AdministrativeException(e.getMessage());
		}
		if (clientes.isEmpty() || clientes == null) {
			return null;
		}
		if (clientes.size() > 1) {
			throw new AdministrativeException("Error a definir");
		}
		return clientes.iterator().next();
	}
	
	@Override
	public List<Cliente> findByFilter(ClienteFiltro filtro) throws AdministrativeException {
		List<Cliente> clientes = null;
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Cliente> query = cb.createQuery(Cliente.class);
			Root<Cliente> root = query.from(Cliente.class);
			Predicate[] predicates = addPredicates(filtro, cb, root);
			if (predicates.length > 0){
				query.where(predicates);
			}
			clientes = (List<Cliente>) em.createQuery(query).getResultList();
		} catch (Exception e) {
			throw new AdministrativeException(e.getMessage());
		}
		return clientes;
	}

	private Predicate[] addPredicates(ClienteFiltro filtro, CriteriaBuilder cb, Root<Cliente> root) {
		List<Predicate> predicateList = new ArrayList<Predicate>();
		addCliNombre(filtro, cb, root, predicateList);
		addCliDni(filtro, cb, root, predicateList);
		addCliDomicilio(filtro, cb, root, predicateList);
		addCliBarrio(filtro, cb, root, predicateList);
		addCliLocalidad(filtro, cb, root, predicateList);
		addCliTelefono(filtro, cb, root, predicateList);
		addCliCelular(filtro, cb, root, predicateList);
		addCliMail(filtro, cb, root, predicateList);
		addBanco(filtro, cb, root, predicateList);
		addCliCuenta(filtro, cb, root, predicateList);
		addCliCbu(filtro, cb, root, predicateList);
		
		Predicate[] predicates = new Predicate[predicateList.size()];
	    predicateList.toArray(predicates);
		return predicates;
	}

	private void addCliBarrio(ClienteFiltro filtro, CriteriaBuilder cb, Root<Cliente> root,
			List<Predicate> predicateList) {
		if (!Strings.isNullOrEmpty(filtro.getCliLocalidad())) {
			predicateList.add(cb.like(root.get("cliBarrio").as(String.class), "%"+filtro.getCliBarrio()+"%"));
		}		
	}

	private void addBanco(ClienteFiltro filtro, CriteriaBuilder cb, Root<Cliente> root, List<Predicate> predicateList) {
		if (!Strings.isNullOrEmpty(filtro.getBanco())) {
			predicateList.add(cb.like(root.get("cliBanco").as(String.class), "%"+filtro.getBanco()+"%"));
		}
	}

	private void addCliCbu(ClienteFiltro filtro, CriteriaBuilder cb, Root<Cliente> root, List<Predicate> predicateList) {
		if (!Strings.isNullOrEmpty(filtro.getCliCbu())) {
			predicateList.add(cb.like(root.get("cliCbu").as(String.class), "%"+filtro.getCliCbu()+"%"));
		}
	}

	private void addCliCuenta(ClienteFiltro filtro, CriteriaBuilder cb, Root<Cliente> root,
			List<Predicate> predicateList) {
		if (!Strings.isNullOrEmpty(filtro.getCliCuenta())) {
			predicateList.add(cb.like(root.get("cliCuenta").as(String.class), "%"+filtro.getCliCuenta()+"%"));
		}
	}

	private void addCliMail(ClienteFiltro filtro, CriteriaBuilder cb, Root<Cliente> root, List<Predicate> predicateList) {
		if (!Strings.isNullOrEmpty(filtro.getCliMail())) {
			predicateList.add(cb.like(root.get("cliMail").as(String.class), "%"+filtro.getCliMail()+"%"));
		}
	}

	private void addCliCelular(ClienteFiltro filtro, CriteriaBuilder cb, Root<Cliente> root,
			List<Predicate> predicateList) {
		if (!Strings.isNullOrEmpty(filtro.getCliCelular())) {
			predicateList.add(cb.like(root.get("cliCelular").as(String.class), "%"+filtro.getCliCelular()+"%"));
		}
	}

	private void addCliTelefono(ClienteFiltro filtro, CriteriaBuilder cb, Root<Cliente> root,
			List<Predicate> predicateList) {
		if (!Strings.isNullOrEmpty(filtro.getCliTelefono())) {
			predicateList.add(cb.like(root.get("cliTelefono").as(String.class), "%"+filtro.getCliTelefono()+"%"));
		}
	}

	private void addCliLocalidad(ClienteFiltro filtro, CriteriaBuilder cb, Root<Cliente> root,
			List<Predicate> predicateList) {
		if (!Strings.isNullOrEmpty(filtro.getCliLocalidad())) {
			predicateList.add(cb.like(root.get("cliLocalidad").as(String.class), "%"+filtro.getCliLocalidad()+"%"));
		}		
	}

	private void addCliDomicilio(ClienteFiltro filtro, CriteriaBuilder cb, Root<Cliente> root,
			List<Predicate> predicateList) {
		if (!Strings.isNullOrEmpty(filtro.getCliDireccion())) {
			predicateList.add(cb.like(root.get("cliDireccion").as(String.class), "%"+filtro.getCliDireccion()+"%"));
		}
	}

	private void addCliDni(ClienteFiltro filtro, CriteriaBuilder cb, Root<Cliente> root, List<Predicate> predicateList) {
		if (!Strings.isNullOrEmpty(filtro.getCliDni())) {
			Predicate cliDni = cb.like(root.get("cliDni").as(String.class), "%"+filtro.getCliDni()+"%");
			predicateList.add(cliDni);
		}
	}

	private void addCliNombre(ClienteFiltro filtro, CriteriaBuilder cb, Root<Cliente> root, List<Predicate> predicateList) {
		if (!Strings.isNullOrEmpty(filtro.getCliNombre())) {
			Predicate cliNombre = cb.like(root.get("cliNombre").as(String.class), "%"+ filtro.getCliNombre() +"%");
			predicateList.add(cliNombre);
		}
	}

	@Override
	public void guardar(Cliente cliente) throws AdministrativeException {
		try {
			em.persist(cliente);
		} catch (Exception e) {
			throw new AdministrativeException(e.getMessage());
		}
	}

	@Override
	public void update(Cliente cliente) throws AdministrativeException {
		try {
			em.merge(cliente);
		} catch (Exception e) {
			throw new AdministrativeException(e.getMessage());
		}
	}


}

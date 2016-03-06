package ar.com.tecsat.loans.dao.implemetations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import ar.com.tecsat.loans.bean.utils.PagoFiltro;
import ar.com.tecsat.loans.dao.interfaces.PagoDao;
import ar.com.tecsat.loans.exceptions.AdministrativeException;
import ar.com.tecsat.loans.modelo.Pago;

/**
 * @author nicolas
 * 
 */
@Stateless
public class PagoDaoImpl implements PagoDao {

	@PersistenceContext(unitName = "Prest")
	private EntityManager em;

	@SuppressWarnings("unchecked")
	@Override
	public List<Pago> findAllPagos() throws AdministrativeException {
		Query query = em.createNamedQuery("findAllPagos");
		return (List<Pago>) query.getResultList();
	}

	@Override
	public Pago findByPk(int pagoId) throws AdministrativeException {
		return em.find(Pago.class, pagoId);
	}

	@Override
	public List<Pago> findByFilter(PagoFiltro filtro) throws AdministrativeException {
		List<Pago> pagos = null;
		try {
			CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
			CriteriaQuery<Pago> query = criteriaBuilder.createQuery(Pago.class);
			Root<Pago> root = query.from(Pago.class);

			Predicate[] predicates = addPredicates(filtro, criteriaBuilder, root);
			if (predicates.length > 0) {
				query.where(predicates);
			}
			pagos = em.createQuery(query).getResultList();
		} catch (Exception e) {
			throw new AdministrativeException(e.getMessage());
		}
		return pagos;
	}

	/**
	 * @param filtro
	 * @param criteriaBuilder
	 * @param root
	 * @return
	 */
	private Predicate[] addPredicates(PagoFiltro filtro, CriteriaBuilder criteriaBuilder, Root<Pago> root) {
		List<Predicate> predicateList = new ArrayList<Predicate>();

		addCliente(filtro, criteriaBuilder, root, predicateList);
		addPrestamo(filtro, criteriaBuilder, root, predicateList);
		addNumeroCuota(filtro, criteriaBuilder, root, predicateList);
		addImporte(filtro, criteriaBuilder, root, predicateList);
		addFechaPago(filtro, criteriaBuilder, root, predicateList);
		Predicate[] predicates = new Predicate[predicateList.size()];
		predicateList.toArray(predicates);
		return predicates;
	}
	
	private void addNumeroCuota(PagoFiltro filtro, CriteriaBuilder criteriaBuilder, Root<Pago> root,
			List<Predicate> predicateList) {
		if (filtro.getNumeroCuota() != null) {
			Predicate numeroCuota = criteriaBuilder.equal(root.get("cuota").get("cuoNumero"), filtro.getNumeroCuota());
			predicateList.add(numeroCuota);
		}		
	}

	private void addFechaPago(PagoFiltro filtro, CriteriaBuilder criteriaBuilder, Root<Pago> root,
			List<Predicate> predicateList) {
		if (filtro.getPagFestadoDesde() != null && filtro.getPagFestadoHasta()!= null) {
			Date fechaDesde = filtro.getPagFestadoDesde();
			Date fechaHasta = filtro.getPagFestadoHasta();
			predicateList.add(criteriaBuilder.between(root.get("pagFestado").as(Date.class), fechaDesde, fechaHasta));
		}
	}
	

	private void addImporte(PagoFiltro filtro, CriteriaBuilder criteriaBuilder, Root<Pago> root,
			List<Predicate> predicateList) {
		if (filtro.getPagMonto() != null) {
			Predicate monto = criteriaBuilder.equal(root.get("pagMonto"), filtro.getPagMonto());
			predicateList.add(monto);
		}
	}

	private void addCliente(PagoFiltro filtro, CriteriaBuilder criteriaBuilder, Root<Pago> root,
			List<Predicate> predicateList) {
		if (filtro.getIdCliente() != null) {
			Predicate cliente = criteriaBuilder.equal(root.get("cuota").get("prestamo").get("cliente").get("cliId"), filtro.getIdCliente());
			predicateList.add(cliente);
		}
	}

	private void addPrestamo(PagoFiltro filtro, CriteriaBuilder criteriaBuilder, Root<Pago> root,
			List<Predicate> predicateList) {
		if (filtro.getIdPrestamo() != null) {
			Predicate prestamo = criteriaBuilder.equal(root.get("cuota").get("prestamo").get("id"), filtro.getIdPrestamo());
			predicateList.add(prestamo);
		}
	}
	
	@Override
	public void guardar(Pago pago) throws AdministrativeException {
		try {
			em.persist(pago);
		} catch (Exception e) {
			throw new AdministrativeException(e.getMessage());
		}
	}

	@Override
	public List<Pago> findByPrestamo(Integer id) throws AdministrativeException {
		List<Pago> pagos = null;
		try {
			CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
			CriteriaQuery<Pago> query = criteriaBuilder.createQuery(Pago.class);
			Root<Pago> root = query.from(Pago.class);
			Predicate prestamo = criteriaBuilder.equal(root.get("cuota").get("prestamo").get("id"), id);
			query.where(prestamo);
			pagos = em.createQuery(query).getResultList();
		} catch (Exception e) {
			throw new AdministrativeException(e.getMessage());
		}
		return pagos;
	}

	@Override
	public void eliminarPago(Pago pago) {
		em.remove(em.getReference(Pago.class, pago.getPagId()));
	}
}

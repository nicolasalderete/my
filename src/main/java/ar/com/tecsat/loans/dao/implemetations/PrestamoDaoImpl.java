package ar.com.tecsat.loans.dao.implemetations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import ar.com.tecsat.loans.bean.utils.PrestamoFiltro;
import ar.com.tecsat.loans.dao.interfaces.PrestamoDao;
import ar.com.tecsat.loans.exceptions.AdministrativeException;
import ar.com.tecsat.loans.modelo.Prestamo;
import ar.com.tecsat.loans.modelo.PrestamoEstado;

/**
 * @author nicolas
 * 
 */
@Stateless
public class PrestamoDaoImpl implements PrestamoDao {

	@PersistenceContext(unitName = "Prest", type = PersistenceContextType.TRANSACTION)
	private EntityManager em;

	@SuppressWarnings("unchecked")
	@Override
	public List<Prestamo> findAllPrestamos() throws AdministrativeException {
		Query query = em.createNamedQuery("findAllPrestamos");
		return (List<Prestamo>) query.getResultList();
	}

	@Override
	public Prestamo findByPk(int preId) throws AdministrativeException {
		return null;
	}

	@Override
	public void savePrestamo(Prestamo prestamo) throws AdministrativeException {
		try {
			em.persist(prestamo);
		} catch (Exception e) {
			throw new AdministrativeException(e.getMessage());
		}
	}

	@Override
	public List<Prestamo> findByFilter(PrestamoFiltro filtro) throws AdministrativeException {
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Prestamo> query = builder.createQuery(Prestamo.class);
			Root<Prestamo> prestamo = query.from(Prestamo.class);
			Predicate[] predicates = addPredicates(filtro, builder, prestamo);
			if (predicates.length > 0) {
				query.where(predicates);
			}
			return em.createQuery(query).getResultList();
		} catch (Exception e) {
			throw new AdministrativeException(e.getMessage());
		}
	}

	private Predicate[] addPredicates(PrestamoFiltro filtro, CriteriaBuilder builder, Root<Prestamo> prestamo) {
		List<Predicate> predicateList = new ArrayList<Predicate>();
		addCliente(filtro, builder, prestamo, predicateList);
		addMontoPrestamo(filtro, builder, prestamo, predicateList);
		addCantCuotas(filtro, builder, prestamo, predicateList);
		addTasa(filtro, builder, prestamo, predicateList);
		addFechaSolicitud(filtro, builder, prestamo, predicateList);
		addEstado(filtro, builder, prestamo, predicateList);

		Predicate[] predicates = new Predicate[predicateList.size()];
		predicateList.toArray(predicates);
		return predicates;
	}

	private void addEstado(PrestamoFiltro filtro, CriteriaBuilder builder, Root<Prestamo> prestamo,
			List<Predicate> predicateList) {
		// TODO Predicado Estado del prestamo
		if (filtro.getEstado() != null) {
			Predicate estado = builder.equal(prestamo.get("preEstado"), PrestamoEstado.valueOf(filtro.getEstado()));
			predicateList.add(estado);
		}
	}

	private void addFechaSolicitud(PrestamoFiltro filtro, CriteriaBuilder builder, Root<Prestamo> prestamo,
			List<Predicate> predicateList) {
		if (filtro.getFechaDesde() != null && filtro.getFechaDesde() != null) {
			Date fechaDesde = filtro.getFechaDesde();
			Date fechaHasta = filtro.getFechaHasta();
			predicateList.add(builder.between(prestamo.get("preFechaInicio").as(Date.class), fechaDesde, fechaHasta));
		}
	}

	private void addCliente(PrestamoFiltro filtro, CriteriaBuilder builder, Root<Prestamo> prestamo,
			List<Predicate> predicateList) {
		if (filtro.getIdCliente() != null) {
			Predicate cliente = builder.equal(prestamo.get("cliente").get("cliId"), filtro.getIdCliente());
			predicateList.add(cliente);
		}
	}

	private void addTasa(PrestamoFiltro filtro, CriteriaBuilder builder, Root<Prestamo> prestamo,
			List<Predicate> predicateList) {
		if (filtro.getTasa() != null) {
			Predicate tasa = null;
			if (filtro.getCondicionTasa().equals("ES_IGUAL")) {
				tasa = builder.equal(prestamo.get("preTasa"), filtro.getTasa().doubleValue());
			}
			if (filtro.getCondicionMonto().equals("ES_MENOR")) {
				tasa = builder.le(prestamo.get("preTasa").as(Number.class), filtro.getTasa().doubleValue());
			}
			if (filtro.getCondicionMonto().equals("ES_MAYOR")) {
				tasa = builder.ge(prestamo.get("preTasa").as(Number.class), filtro.getTasa().doubleValue());
			}
			predicateList.add(tasa);
		}
	}

	private void addMontoPrestamo(PrestamoFiltro filtro, CriteriaBuilder builder, Root<Prestamo> prestamo,
			List<Predicate> predicateList) {
		if (filtro.getCapital() != null) {
			Predicate monto = null;
			if (filtro.getCondicionMonto().equals("ES_IGUAL")) {
				monto = builder.equal(prestamo.get("preCapital"), filtro.getCapital());
			}
			if (filtro.getCondicionMonto().equals("ES_MENOR")) {
				monto = builder.le(prestamo.get("preCpital").as(Number.class), filtro.getCapital());
			}
			if (filtro.getCondicionMonto().equals("ES_MAYOR")) {
				monto = builder.ge(prestamo.get("preCapital").as(Number.class), filtro.getCapital());
			}
			predicateList.add(monto);
		}
	}

	private void addCantCuotas(PrestamoFiltro filtro, CriteriaBuilder builder, Root<Prestamo> prestamo,
			List<Predicate> predicateList) {

		if (filtro.getCantCuotas() != null) {
			Predicate cantCuotas = null;
			if (filtro.getCondicionCuotas().equals("ES_IGUAL")) {
				cantCuotas = builder.equal(prestamo.get("preCantCuotas"), filtro.getCantCuotas().intValue());
			}
			if (filtro.getCondicionCuotas().equals("ES_MENOR")) {
				cantCuotas = builder.le(prestamo.get("preCantCuotas").as(Number.class), filtro.getCantCuotas()
						.intValue());
			}
			if (filtro.getCondicionCuotas().equals("ES_MAYOR")) {
				cantCuotas = builder.ge(prestamo.get("preCantCuotas").as(Number.class), filtro.getCantCuotas()
						.intValue());
			}
			predicateList.add(cantCuotas);
		}
	}

	@Override
	public void actualizar(Prestamo prestamo) throws AdministrativeException {
		try {
			em.merge(prestamo);
		} catch (Exception e) {
			throw new AdministrativeException("Error al actualizar la cuota");
		}
	}

	@Override
	public List<Prestamo> findByIdCliente(Integer idCliente) throws AdministrativeException {
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Prestamo> query = builder.createQuery(Prestamo.class);
			Root<Prestamo> prestamo = query.from(Prestamo.class);

			List<Predicate> predicateList = new ArrayList<Predicate>();
			Predicate cliente = builder.equal(prestamo.get("cliente").get("cliId"), idCliente);
			predicateList.add(cliente);

			Predicate[] predicates = new Predicate[predicateList.size()];
			predicateList.toArray(predicates);
			if (predicates.length > 0) {
				query.where(predicates);
			}
			return em.createQuery(query).getResultList();
		} catch (Exception e) {
			throw new AdministrativeException(e.getMessage());
		}
	}

	@Override
	public void eliminarPrestamo(Prestamo prestamo) {
		em.remove(em.getReference(Prestamo.class, prestamo.getId()));
	}

}

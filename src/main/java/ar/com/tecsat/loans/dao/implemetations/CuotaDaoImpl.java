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

import org.joda.time.DateTime;

import ar.com.tecsat.loans.bean.CuotaEstado;
import ar.com.tecsat.loans.bean.utils.CuotaFiltro;
import ar.com.tecsat.loans.dao.interfaces.CuotaDao;
import ar.com.tecsat.loans.exceptions.AdministrativeException;
import ar.com.tecsat.loans.modelo.Cuota;

/**
 * @author nicolas
 * 
 */
@Stateless
public class CuotaDaoImpl implements CuotaDao {

	private static final String QUERY_FIND_CURRENT_DATE = "select * from cuota where "
			+ "cuo_estado NOT IN ('CANCELADA','VENCIDA','PAGO_INSUFICIENTE') "
			+ "and date(cuo_fvencimiento) = curdate()";

	private static final String QUERY_FIND_TREINTA_DATE = "select * from cuota where "
			+ "date(cuo_fvencimiento) = date_sub(curdate(), interval 1 month)";

	private static final String QUERY_FIND_QUINCE_DATE = "select * from cuota where "
			+ "date(cuo_fvencimiento) = date_sub(curdate(), interval 15 day)";

	@PersistenceContext(unitName = "Prest")
	private EntityManager em;

	@Override
	public List<Cuota> findByFilter(CuotaFiltro filtro) throws AdministrativeException {
		List<Cuota> cuotas = null;
		try {
			CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
			CriteriaQuery<Cuota> query = criteriaBuilder.createQuery(Cuota.class);
			Root<Cuota> root = query.from(Cuota.class);
			Predicate[] predicates = addPredicates(filtro, criteriaBuilder, root);
			if (predicates.length > 0) {
				query.where(predicates);
			}
			cuotas = em.createQuery(query).getResultList();
		} catch (Exception e) {
			throw new AdministrativeException(e.getMessage());
		}
		return cuotas;
	}

	private Predicate[] addPredicates(CuotaFiltro filtro, CriteriaBuilder criteriaBuilder, Root<Cuota> root) {
		List<Predicate> predicateList = new ArrayList<Predicate>();

		addCliente(filtro, criteriaBuilder, root, predicateList);
		addPrestamo(filtro, criteriaBuilder, root, predicateList);
		addNumeroCuota(filtro, criteriaBuilder, root, predicateList);
		addEstadoCuota(filtro, criteriaBuilder, root, predicateList);
		addMontoCuota(filtro, criteriaBuilder, root, predicateList);
		addFechaVto(filtro, criteriaBuilder, root, predicateList);

		Predicate[] predicates = new Predicate[predicateList.size()];
		predicateList.toArray(predicates);
		return predicates;
	}

	private void addFechaVto(CuotaFiltro filtro, CriteriaBuilder criteriaBuilder, Root<Cuota> root,
			List<Predicate> predicateList) {
		if (filtro.getFechaDesde() != null && filtro.getFechaDesde() != null) {
			Date fechaDesde = filtro.getFechaDesde();
			Date fechaHasta = filtro.getFechaHasta();
			predicateList.add(criteriaBuilder.between(root.get("cuoFechaVencimiento").as(Date.class), fechaDesde,
					fechaHasta));
		}
	}

	private void addMontoCuota(CuotaFiltro filtro, CriteriaBuilder criteriaBuilder, Root<Cuota> root,
			List<Predicate> predicateList) {
		if (filtro.getMontoCuota() != null) {
			Predicate numeroCuota = null;
			if (filtro.getCondicionCuota().equals("ES_IGUAL")) {
				numeroCuota = criteriaBuilder.equal(root.get("cuoImporte"), filtro.getMontoCuota());
			}
			if (filtro.getCondicionCuota().equals("ES_MENOR")) {
				numeroCuota = criteriaBuilder.equal(root.get("cuoImporte"), filtro.getMontoCuota());
			}
			if (filtro.getCondicionCuota().equals("ES_MAYOR")) {
				numeroCuota = criteriaBuilder.equal(root.get("cuoImporte"), filtro.getMontoCuota());
			}
			predicateList.add(numeroCuota);
		}
	}

	private void addNumeroCuota(CuotaFiltro filtro, CriteriaBuilder criteriaBuilder, Root<Cuota> root,
			List<Predicate> predicateList) {
		if (filtro.getNumeroCuota() != null) {
			Predicate cuota = null;
			if (filtro.getCondicionCuota().equals("ES_IGUAL")) {
				cuota = criteriaBuilder.equal(root.get("cuoNumero"), filtro.getNumeroCuota());
			}
			if (filtro.getCondicionCuota().equals("ES_MENOR")) {
				cuota = criteriaBuilder.equal(root.get("cuoNumero"), filtro.getNumeroCuota());
			}
			if (filtro.getCondicionCuota().equals("ES_MAYOR")) {
				cuota = criteriaBuilder.equal(root.get("cuoNumero"), filtro.getNumeroCuota());
			}
			predicateList.add(cuota);
		}
	}

	private void addEstadoCuota(CuotaFiltro filtro, CriteriaBuilder criteriaBuilder, Root<Cuota> root,
			List<Predicate> predicateList) {
		if (filtro.getEstadoCuota() != null) {
			Predicate prestamo = criteriaBuilder.equal(root.get("cuoEstado"), CuotaEstado.valueOf(filtro.getEstadoCuota()));
			predicateList.add(prestamo);
		}
	}

	private void addPrestamo(CuotaFiltro filtro, CriteriaBuilder criteriaBuilder, Root<Cuota> root,
			List<Predicate> predicateList) {
		if (filtro.getIdPrestamo() != null) {
			Predicate prestamo = criteriaBuilder.equal(root.get("prestamo").get("id"), filtro.getIdPrestamo());
			predicateList.add(prestamo);
		}
	}

	private void addCliente(CuotaFiltro filtro, CriteriaBuilder criteriaBuilder, Root<Cuota> root,
			List<Predicate> predicateList) {
		if (filtro.getIdCliente() != null) {
			Predicate cliente = criteriaBuilder.equal(root.get("prestamo").get("cliente").get("cliId"),
					filtro.getIdCliente());
			predicateList.add(cliente);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Cuota> findCuotas() throws AdministrativeException {
		List<Cuota> cuotas = null;
		try {
			Query query = em.createNamedQuery("findCuotas");
			cuotas = query.getResultList();
		} catch (Exception e) {
			throw new AdministrativeException(e.getMessage());
		}
		if (cuotas == null) {
			return null;
		}
		return cuotas;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Cuota> findCuotasByPrestamo(Integer preId) throws AdministrativeException {
		List<Cuota> cuotas = null;
		try {
			Query query = em.createNamedQuery("findCuotasByPrestamo");
			query.setParameter("prestamo", preId);
			cuotas = query.getResultList();
		} catch (Exception e) {
			throw new AdministrativeException(e.getMessage());
		}
		if (cuotas == null) {
			return null;
		}
		return cuotas;
	}

	@Override
	public void actualizar(Cuota cuota) throws AdministrativeException {
		try {
			em.merge(cuota);
		} catch (Exception e) {
			throw new AdministrativeException("Error al actualizar la cuota");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Cuota> findByFecha(Date start, Date end) throws AdministrativeException {
		List<Cuota> cuotas = null;
		try {
			Query query = em.createNamedQuery("findCuotasByFecha");
			query.setParameter("start", start);
			query.setParameter("end", end);
			cuotas = query.getResultList();
		} catch (Exception e) {
			throw new AdministrativeException("Error al acceder a la base.");
		}
		return cuotas;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Cuota> findByFechaVtoHoy() throws AdministrativeException {
		List<Cuota> cuotas = null;
		try {
			Query query = em.createNativeQuery(QUERY_FIND_CURRENT_DATE, Cuota.class);
			query.getResultList();
			cuotas = query.getResultList();
		} catch (Exception e) {
			throw new AdministrativeException("Error al acceder a la base.");
		}
		return cuotas;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Cuota> findByFechaVtoA(int dias) throws AdministrativeException {
		List<Cuota> cuotas = null;
		try {
			String code = dias == 30 ? QUERY_FIND_TREINTA_DATE : QUERY_FIND_QUINCE_DATE;
			Query query = em.createNativeQuery(code, Cuota.class);
			query.getResultList();
			cuotas = query.getResultList();
		} catch (Exception e) {
			throw new AdministrativeException("Error al acceder a la base.");
		}
		return cuotas;
	}

	@Override
	public Cuota findCuota(Cuota currentCuota) {
		return em.find(Cuota.class, currentCuota);
	}

	@Override
	public List<Cuota> findByFechaVto(DateTime hoy) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Cuota> criteriaQuery = builder.createQuery(Cuota.class);
		Root<Cuota> root = criteriaQuery.from(Cuota.class);

		List<Predicate> predicateList = addPredicates(root, builder);
		Predicate[] predicates = new Predicate[predicateList.size()];
		criteriaQuery.where(predicateList.toArray(predicates));

		return em.createQuery(criteriaQuery).getResultList();
	}

	private List<Predicate> addPredicates(Root<Cuota> root, CriteriaBuilder builder) {
		List<Predicate> predicates = new ArrayList<Predicate>();
		Date fechaVto = new DateTime().toDate();
		predicates.add(builder.lessThan(root.get("cuoFechaVencimiento").as(Date.class), fechaVto));
		predicates.add(builder.or(estadoIgual(root, builder, CuotaEstado.VIGENTE),
				estadoIgual(root, builder, CuotaEstado.PAGO_PARCIAL)));
		return predicates;
	}

	private Predicate estadoIgual(Root<Cuota> root, CriteriaBuilder builder, CuotaEstado cuotaEstado) {
		return builder.equal(root.get("cuoEstado").as(CuotaEstado.class), cuotaEstado);
	}
}

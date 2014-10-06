package ar.com.tecsat.loans.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.joda.time.DateTime;

import ar.com.tecsat.loans.bean.CuotaEstado;
import ar.com.tecsat.loans.bean.utils.CuotaFiltro;
import ar.com.tecsat.loans.bean.utils.Dias;
import ar.com.tecsat.loans.dao.interfaces.CuotaDao;
import ar.com.tecsat.loans.dao.interfaces.PagoDao;
import ar.com.tecsat.loans.dao.interfaces.PrestamoDao;
import ar.com.tecsat.loans.exceptions.AdministrativeException;
import ar.com.tecsat.loans.modelo.Cuota;
import ar.com.tecsat.loans.modelo.Pago;
import ar.com.tecsat.loans.modelo.Prestamo;
import ar.com.tecsat.loans.modelo.PrestamoEstado;
import ar.com.tecsat.loans.util.PagoHelper;

/**
 * @author nicolas
 * 
 */
@Stateless
public class CuotaService {

	@EJB
	private CuotaDao cuotaDao;

	@EJB
	private PagoDao pagoDao;
	
	@EJB
	private PrestamoDao prestamoDao;

	private PagoHelper pagoHelper = PagoHelper.getInstance();

	public List<Cuota> search(CuotaFiltro filtro) throws AdministrativeException {
		List<Cuota> cuotas = null;
		try {
			cuotas = cuotaDao.findByFilter(filtro);
		} catch (AdministrativeException e) {
			throw new AdministrativeException(e.getMessage());
		}
		if (cuotas == null || cuotas.isEmpty()) {
			throw new AdministrativeException("No hay registros");
		}
		return cuotas;
	}

	public List<Cuota> findCuotas() throws AdministrativeException {
		List<Cuota> cuotas = null;
		try {
			cuotas = cuotaDao.findCuotas();
		} catch (AdministrativeException e) {
			throw new AdministrativeException(e.getMessage());
		}
		return cuotas;
	}

	public List<Cuota> findCuotasByPrestamo(Prestamo prestamo) throws AdministrativeException {
		List<Cuota> cuotas = new ArrayList<Cuota>();
		try {
			cuotas = cuotaDao.findCuotasByPrestamo(prestamo.getId());
		} catch (AdministrativeException e) {
			throw new AdministrativeException(e.getMessage());
		}
		if (cuotas == null) {
			return new ArrayList<Cuota>();
		}
		return cuotas;
	}

	public void pagar(Cuota cuota, CuotaFiltro filtro) throws AdministrativeException {
		// si el importe de pago es mayor o igual al de la cuota
		if (elPagoEsMayorAlImporteDeLaCuota(cuota, filtro)) {
			throw new AdministrativeException(
					"El importe de pago debe ser igual o menor que el total a pagar de la cuota");
		} else if (elPagoEsIgualAlImporteDeLaCuota(cuota, filtro)) {
			pagoTotal(cuota, filtro);
		} else {
			// Si es menor
			pagoParcial(cuota, filtro);
		}
	}

	/**
	 * @param cuota
	 * @param filtro
	 * @return
	 */
	private boolean elPagoEsIgualAlImporteDeLaCuota(Cuota cuota, CuotaFiltro filtro) {
		return cuota.getCuoSaldo().compareTo(filtro.getImportePago()) == 0;
	}

	/**
	 * @param cuota
	 * @param filtro
	 * @return
	 */
	private boolean elPagoEsMayorAlImporteDeLaCuota(Cuota cuota, CuotaFiltro filtro) {
		return cuota.getCuoSaldo().compareTo(filtro.getImportePago()) == -1;
	}

	/**
	 * @param cuota
	 * @param filtro
	 * @throws AdministrativeException
	 */
	private void pagoParcial(Cuota cuota, CuotaFiltro filtro) throws AdministrativeException {
		// validarImporteDePago(cuota.getCuoSaldo(),
		// filtro.getImportePago());
		// validarPagoConSaldo(cuota, filtro);
		registrarPago(cuota, filtro);
		actualizaCuotaPagoParcial(cuota, filtro);
	}

	/**
	 * @param cuota
	 * @param filtro
	 * @throws AdministrativeException
	 */
	private void actualizaCuotaPagoParcial(Cuota cuota, CuotaFiltro filtro) throws AdministrativeException {
		cuota.setCuoPagoParcial(cuota.getCuoPagoParcial().add(filtro.getImportePago()));
		if (cuota.getCuoEstado().equals(CuotaEstado.VIGENTE)) {
			cuota.setCuoEstado(CuotaEstado.PAGO_PARCIAL);
		} else {
			cuota.setCuoEstado(CuotaEstado.PAGO_INSUFICIENTE);
		}
		cuotaDao.actualizar(cuota);
	}

	/**
	 * @param cuota
	 * @param filtro
	 * @throws AdministrativeException
	 */
	private void pagoTotal(Cuota cuota, CuotaFiltro filtro) throws AdministrativeException {
		registrarPago(cuota, filtro);
		actualizarCuotaPagoTotal(cuota, filtro);
	}

	/**
	 * @param cuota
	 * @param filtro
	 * @throws AdministrativeException
	 */
	private void actualizarCuotaPagoTotal(Cuota cuota, CuotaFiltro filtro) throws AdministrativeException {
		cuota.setCuoEstado(CuotaEstado.CANCELADA);
		cuotaDao.actualizar(cuota);
		if (debeCancelarPrestamo(cuota)) {
			Prestamo prestamo = cuota.getPrestamo();
			prestamo.setPreEstado(PrestamoEstado.CANCELADO);
			prestamoDao.actualizar(prestamo);
		}
	}

	/**
	 * @param cuota
	 * @return
	 */
	private boolean debeCancelarPrestamo(Cuota cuota) {
		List<Cuota> cuotas = cuota.getPrestamo().getCuotas();
		for (Cuota obj : cuotas) {
			if (!obj.isCancelada()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param cuota
	 * @param filtro
	 * @throws AdministrativeException
	 */
	private void registrarPago(Cuota cuota, CuotaFiltro filtro) throws AdministrativeException {
		Pago pago = pagoHelper.crearPago(cuota, filtro);
		pagoDao.guardar(pago);
	}

	/**
	 * @param treinta
	 *            TODO
	 * @return
	 */
	public List<Cuota> obtenerCuotasVencidasA(Dias dias) {
		List<Cuota> cuotas = new ArrayList<Cuota>();
		int cant = 0;
		switch (dias) {
		case QUINCE:
			cant = 15;
			break;
		case TREINTA:
			cant = 30;
			break;
		}
		try {
			cuotas = cuotaDao.findByFechaVtoA(cant);
		} catch (AdministrativeException e) {
			return cuotas;
		}
		return cuotas;
	}

	public void actualizarCuotaIntereses(Cuota cuota, CuotaFiltro filtro) throws AdministrativeException {
		cuota.setCuoInteresPunitorio(filtro.getInteresPunitorio());
		cuota.setCuoImporte(totalAPagar(cuota, filtro));
		cuotaDao.actualizar(cuota);
	}

	private BigDecimal totalAPagar(Cuota cuota, CuotaFiltro filtro) {
		return cuota.getCuoPura().add(cuota.getCuoInteres()).add(filtro.getInteresPunitorio());
	}

	public Cuota findCuota(Cuota currentCuota) {
		return cuotaDao.findCuota(currentCuota);
	}

	public String actualizarEstadoCuotasVencidas() throws AdministrativeException {
		DateTime hoy = new DateTime();
		List<Cuota> cuotas = cuotaDao.findByFechaVto(hoy);
		for (Cuota cuota : cuotas) {
			actualizarEstadoVencido(cuota);
			cuotaDao.actualizar(cuota);
		}
		return "Cantidad de cuotas vencidas: " + cuotas.size();
	}

	private void actualizarEstadoVencido(Cuota cuota) {
		if (cuota.getCuoEstado().equals(CuotaEstado.VIGENTE)){
			cuota.setCuoEstado(CuotaEstado.VENCIDA);
		}
		if (cuota.getCuoEstado().equals(CuotaEstado.PAGO_PARCIAL)){
			cuota.setCuoEstado(CuotaEstado.PAGO_PARCIAL_VENCIDO);
		}
	}
}

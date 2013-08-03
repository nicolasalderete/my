package ar.com.tecsat.loans.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ar.com.tecsat.loans.bean.CuotaEstado;
import ar.com.tecsat.loans.bean.utils.CuotaFiltro;
import ar.com.tecsat.loans.bean.utils.Dias;
import ar.com.tecsat.loans.dao.interfaces.CuotaDao;
import ar.com.tecsat.loans.dao.interfaces.PagoDao;
import ar.com.tecsat.loans.exceptions.AdministrativeException;
import ar.com.tecsat.loans.modelo.Cuota;
import ar.com.tecsat.loans.modelo.Pago;
import ar.com.tecsat.loans.modelo.Prestamo;
import ar.com.tecsat.loans.util.PagoHelper;
import ar.com.tecsat.loans.util.PrestamoConfiguration;

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

	private PrestamoConfiguration configuration = PrestamoConfiguration.getInstance();

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
		// si el importe de pago es menor o igual al de la cuota
		if (cuota.getCuoTotalPagar().compareTo(filtro.getImportePago()) == -1) {
			throw new AdministrativeException(
					"El importe de pago debe ser igual o menor que el total a pagar de la cuota");
		} else if (cuota.getCuoTotalPagar().compareTo(filtro.getImportePago()) == 0) {
			pagoTotal(cuota, filtro);
		} else {
			pagoParcial(cuota, filtro);
		}
	}

	/**
	 * @param cuota
	 * @param filtro
	 * @throws AdministrativeException
	 */
	private void pagoParcial(Cuota cuota, CuotaFiltro filtro) throws AdministrativeException {
		validarImporteDePago(cuota.getCuoTotalPagar(), filtro.getImportePago());
		validarPagoConSaldo(cuota, filtro);
		registrarPago(cuota, filtro);
		actualizaCuotaPagoParcial(cuota, filtro);
	}

	/**
	 * @param cuota
	 * @param filtro
	 * @throws AdministrativeException
	 */
	private void validarPagoConSaldo(Cuota cuota, CuotaFiltro filtro) throws AdministrativeException {
		if (cuota.getCuoTotalPagar().compareTo(filtro.getImportePago()) < 0) {
			throw new AdministrativeException("El importe de pago no puede ser mayor al Saldo de la cuota.");
		}
	}

	/**
	 * @param cuota
	 * @param filtro
	 * @throws AdministrativeException
	 */
	private void actualizaCuotaPagoParcial(Cuota cuota, CuotaFiltro filtro) throws AdministrativeException {
		cuota.setCuoSaldoFavor(cuota.getCuoSaldoFavor().add(filtro.getImportePago()));
		if (cuota.getCuoTotalPagar().compareTo(new BigDecimal(0)) == 0) {
			cuota.setCuoEstado(CuotaEstado.PAGO_INSUFICIENTE.toString());
		} else {
			cuota.setCuoEstado(CuotaEstado.PAGO_PARCIAL.toString());
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
		cuota.setCuoSaldoFavor(filtro.getImportePago());
		cuota.setCuoEstado(CuotaEstado.CANCELADA.toString());
		cuotaDao.actualizar(cuota);
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

	private void validarImporteDePago(BigDecimal cuota, BigDecimal pago) throws AdministrativeException {
		BigDecimal pagoMinimo = getPagoMinimo(cuota, pago);
		pagoIgualMayorAlPagoMinimo(pago, pagoMinimo);
	}

	/**
	 * @param pago
	 * @param pagoMinimo
	 * @throws AdministrativeException
	 */
	private void pagoIgualMayorAlPagoMinimo(BigDecimal pago, BigDecimal pagoMinimo) throws AdministrativeException {
		if (pagoMinimo.compareTo(pago) > 0)
			throw new AdministrativeException("El pago no puede ser inferior al pago minimo posible.");
	}

	private BigDecimal getPagoMinimo(BigDecimal cuota, BigDecimal pago) throws AdministrativeException {
		String prop = configuration.getPropertie("prestamo.importe.minimo.apagar");
		return cuota.multiply(new BigDecimal(prop).divide(new BigDecimal(100)));
	}

	public List<String> actualizarCuotasVencidasMasInteres(List<Cuota> cuotaVencidas, Dias dias) {
		List<String> result = new ArrayList<String>();
		for (Cuota cuota : cuotaVencidas) {
			if (dias.equals(Dias.TREINTA)) {
				actualizarCuotaVencidaA(cuota, result, CuotaEstado.VENCIDA_30_DIAS);
			}
			if (dias.equals(Dias.QUINCE)) {
				actualizarCuotaVencidaA(cuota, result, CuotaEstado.VENCIDA_15_DIAS);
			}
		}
		if (result.isEmpty()) {
			result.add("No hay cuotas para actualizar.\n\n");
		}
		return result;
	}

	/**
	 * @param cuota
	 * @param result
	 *            TODO
	 * @param cuotaEstado TODO
	 */
	private void actualizarCuotaVencidaA(Cuota cuota, List<String> result, CuotaEstado cuotaEstado) {
		try {
			if (pagoParcialmenteCuota(cuota)) {
				cuota.setCuoEstado(CuotaEstado.PAGO_INSUFICIENTE.toString());
			} else {
				cuota.setCuoEstado(cuotaEstado.toString());
			}
			if (debePagarIntereses(cuota)) {
				BigDecimal punitorio = cuota.getCuoInteres().divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP)
						.setScale(0);
				cuota.setCuoInteresPunitorio(cuota.getCuoInteresPunitorio().add(punitorio));
			}
			cuotaDao.actualizar(cuota);
			result.add(cuota.toString().concat("\n"));
		} catch (AdministrativeException e) {
			result.add(cuota.toString().concat(">>>").concat(e.getMessage()));
		}
	}

	/**
	 * @param cuota
	 *            TODO
	 * @return
	 */
	private boolean debePagarIntereses(Cuota cuota) {
		BigDecimal montoMinimoPagado = cuota.getCuoTotalPagar().divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
		return cuota.getCuoSaldoFavor().compareTo(montoMinimoPagado) < 0;
	}

	/**
	 * @return
	 */
	public List<Cuota> obtenerCuotasFechaVtoHoy() {
		List<Cuota> cuotas = new ArrayList<Cuota>();
		try {
			cuotas = cuotaDao.findByFechaVtoHoy();
		} catch (AdministrativeException e) {
			return cuotas;
		}
		return cuotas;
	}

	/**
	 * @param cuotasVencenHoy
	 */
	public List<String> actualizarCuotasVencidas(List<Cuota> cuotasVencenHoy) {
		List<String> result = new ArrayList<String>();
		for (Cuota cuota : cuotasVencenHoy) {
			if (noPagoLaCuota(cuota)) {
				actualizoEstadoVencida(cuota, result);
			}
			if (pagoParcialmenteCuota(cuota)) {
				actualizoPagoInsuficiente(cuota, result);
			}
		}
		if (result.isEmpty()) {
			result.add("No hay cuotas para actualizar.\n\n");
		}
		return result;
	}

	/**
	 * @param cuota
	 * @return
	 */
	private boolean noPagoLaCuota(Cuota cuota) {
		return cuota.getCuoSaldoFavor().compareTo(BigDecimal.valueOf(0)) == 0;
	}

	/**
	 * @param cuota
	 * @param result
	 */
	private void actualizoPagoInsuficiente(Cuota cuota, List<String> result) {
		try {
			cuota.setCuoEstado(CuotaEstado.PAGO_INSUFICIENTE.toString());
			cuotaDao.actualizar(cuota);
			result.add(cuota.toString().concat("\n"));
		} catch (AdministrativeException e) {
			result.add(cuota.toString().concat(">>>").concat(e.getMessage()));
		}
	}

	/**
	 * @param cuota
	 * @return
	 */
	private boolean pagoParcialmenteCuota(Cuota cuota) {
		return cuota.getCuoSaldoFavor().compareTo(BigDecimal.valueOf(0)) > 0;
	}

	/**
	 * @param cuota
	 * @param result
	 */
	private void actualizoEstadoVencida(Cuota cuota, List<String> result) {
		try {
			cuota.setCuoEstado(CuotaEstado.VENCIDA.toString());
			cuotaDao.actualizar(cuota);
			result.add(cuota.toString().concat("\n"));
		} catch (AdministrativeException e) {
			result.add(cuota.toString().concat(">>>").concat(e.getMessage()));
		}
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
}

package ar.com.tecsat.loans.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ar.com.tecsat.loans.bean.CuotaEstado;
import ar.com.tecsat.loans.bean.utils.CuotaFiltro;
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
		//si el importe de pago es menor o igual al de la cuota
		if (cuota.getCuoTotalPagar().compareTo(filtro.getImportePago()) == -1) {
			throw new AdministrativeException("El importe de pago debe ser igual o menor que el total a pagar de la cuota");
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
		BigDecimal pagoMinimo = getPagoMinimo(cuota,pago);
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

	public List<Cuota> actualizarCuotasVencidas() throws AdministrativeException {
		List<Cuota> cuotas = null;
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		start.set(Calendar.DATE, -1);
		try {
			cuotas = cuotaDao.findByFecha(start.getTime(), end.getTime());
			actualizarCuotas(cuotas);
		} catch (AdministrativeException e) {
			throw new AdministrativeException(e.getMessage());
		}
		return cuotas;
	}

	public List<Cuota> actualizarCuotasVencidasMasQuinceDias() throws AdministrativeException {
		List<Cuota> cuotas = null;
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		start.set(Calendar.DATE, -15);
		end.set(Calendar.DATE, -1);
		try {
			cuotas = cuotaDao.findByFecha(start.getTime(), end.getTime());
			actualizarCuotasVencidas(cuotas, 15);
		} catch (AdministrativeException e) {
			throw new AdministrativeException(e.getMessage());
		}
		return cuotas;
	}
	
	public List<Cuota> actualizarCuotasVencidasMasTreintaDias() throws AdministrativeException {
		List<Cuota> cuotas = null;
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		start.set(Calendar.DATE, -30);
		end.set(Calendar.DATE, -15);
		try {
			cuotas = cuotaDao.findByFecha(start.getTime(), end.getTime());
			actualizarCuotasVencidas(cuotas, 30);
		} catch (AdministrativeException e) {
			throw new AdministrativeException(e.getMessage());
		}
		return cuotas;
	}

	private void actualizarCuotasVencidas(List<Cuota> cuotas, int cantDias) throws AdministrativeException {
		if (cuotas != null) {
			for (Cuota cuota : cuotas) {
				cuota.setCuoEstado(CuotaEstado.VENCIDA.toString());
				BigDecimal monto = null;
				if (cantDias == 15) {
					monto = cuota.getCuoInteres().divide(new BigDecimal(2));
				} else {
					monto = cuota.getCuoInteres();
				}
				cuota.setCuoInteresPunitorio(monto);
				cuotaDao.actualizar(cuota);
			}
		} else {
			System.out.println("No hay cuotas por actualizar");
		}
	}

	private void actualizarCuotas(List<Cuota> cuotas) throws AdministrativeException {
		if (cuotas != null) {
			for (Cuota cuota : cuotas) {
				cuota.setCuoEstado(CuotaEstado.VENCIDA.toString());
				cuotaDao.actualizar(cuota);
			}
		} else {
			System.out.println("No hay cuotas por actualizar");
		}
	}
}

package ar.com.tecsat.loans.service;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.joda.time.DateTime;

import ar.com.tecsat.loans.bean.CuotaEstado;
import ar.com.tecsat.loans.bean.utils.CuotaFiltro;
import ar.com.tecsat.loans.dao.interfaces.CuotaDao;
import ar.com.tecsat.loans.dao.interfaces.PagoDao;
import ar.com.tecsat.loans.dao.interfaces.PrestamoDao;
import ar.com.tecsat.loans.exceptions.AdministrativeException;
import ar.com.tecsat.loans.modelo.Cuota;
import ar.com.tecsat.loans.modelo.HojaRuta;
import ar.com.tecsat.loans.modelo.Pago;
import ar.com.tecsat.loans.modelo.Prestamo;
import ar.com.tecsat.loans.modelo.PrestamoEstado;
import ar.com.tecsat.loans.util.OrderList;
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
	private PagoService pagoService;

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
		return OrderList.sortCuotas(cuotas);
	}

	public List<Cuota> findCuotas() throws AdministrativeException {
		List<Cuota> cuotas = null;
		try {
			cuotas = cuotaDao.findCuotas();
		} catch (AdministrativeException e) {
			throw new AdministrativeException(e.getMessage());
		}
		return OrderList.sortCuotas(cuotas);
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
		return OrderList.sortCuotas(cuotas);
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
		cuota.setCuoSaldo(cuota.getCuoSaldo().subtract(filtro.getImportePago()));
		if (isVigenteOParcial(cuota)) {
			cuota.setCuoEstado(CuotaEstado.PAGO_PARCIAL);
		} else {
			cuota.setCuoEstado(CuotaEstado.PAGO_INSUFICIENTE);
		}
		cuotaDao.actualizar(cuota);
	}

	private boolean isVigenteOParcial(Cuota cuota) {
		return cuota.getCuoEstado().equals(CuotaEstado.VIGENTE)
				|| cuota.getCuoEstado().equals(CuotaEstado.PAGO_PARCIAL);
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
		cuota.setCuoSaldo(cuota.getCuoSaldo().subtract(filtro.getImportePago()));
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
	 * @throws AdministrativeException 
	 */
	private boolean debeCancelarPrestamo(Cuota cuota) throws AdministrativeException {
		List<Cuota> cuotas = cuotaDao.findCuotasByPrestamo(cuota.getPrestamo().getId());
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

	public void actualizarCuotaIntereses(Cuota cuota, CuotaFiltro filtro) throws AdministrativeException {
		cuota.setCuoInteresPunitorio(cuota.getCuoInteresPunitorio().add(filtro.getInteresPunitorio()));
		cuota.setCuoImporte(cuota.getCuoImporte().add(filtro.getInteresPunitorio()));
		cuota.setCuoSaldo(cuota.getCuoImporte().subtract(cuota.getCuoPagoParcial()));
		cuotaDao.actualizar(cuota);
	}

	public Cuota findCuota(Cuota currentCuota) {
		return cuotaDao.findCuota(currentCuota);
	}

	public String actualizarEstadoCuotasVencidas() throws AdministrativeException {
		DateTime hoy = new DateTime().withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59);
		List<Cuota> cuotas = cuotaDao.findByFechaVto(hoy);
		for (Cuota cuota : cuotas) {
			actualizarEstadoVencido(cuota);
			cuotaDao.actualizar(cuota);
		}
		return "Cantidad de cuotas vencidas: " + cuotas.size();
	}

	private void actualizarEstadoVencido(Cuota cuota) {
		if (isVigenteOParcial(cuota)) {
			cuota.setCuoEstado(CuotaEstado.VENCIDA);
		}
		if (cuota.getCuoEstado().equals(CuotaEstado.PAGO_PARCIAL)) {
			cuota.setCuoEstado(CuotaEstado.PAGO_PARCIAL_VENCIDO);
		}
	}

	public JasperPrint createHojaRuta(List<Cuota> listaCuota, BufferedImage image, InputStream inputStream)
			throws AdministrativeException {

		JRBeanCollectionDataSource jrBeanDataSource = new JRBeanCollectionDataSource(translateHojaRuta(listaCuota));
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("logo", image);
			return JasperFillManager.fillReport(inputStream, parameters, jrBeanDataSource);
		} catch (JRException e) {
			throw new AdministrativeException("Error en el servicio de export pdf");
		}
	}

	private Collection<HojaRuta> translateHojaRuta(List<Cuota> listaCuota) {
		Collection<HojaRuta> listaRuta = new ArrayList<HojaRuta>();
		for (Cuota cuota : listaCuota) {
			HojaRuta ruta = new HojaRuta();
			ruta.setCuota(cuota.getCuoNumero());
			ruta.setDomicilio(cuota.getPrestamo().getCliente().getCliDireccion());
			ruta.setEntreCalles(cuota.getPrestamo().getCliente().getCliEntreCalle());
			ruta.setMonto(cuota.getCuoSaldo());
			ruta.setNombre(cuota.getPrestamo().getCliente().getCliNombre());
			listaRuta.add(ruta);
		}
		return listaRuta;
	}

	public void eliminarCuotas(Prestamo prestamo) throws AdministrativeException {
		List<Cuota> cuotasByPrestamo = cuotaDao.findCuotasByPrestamo(prestamo.getId());
		for (Cuota cuota : cuotasByPrestamo) {
			cuotaDao.eliminarCuota(cuota);
		}
	}

}

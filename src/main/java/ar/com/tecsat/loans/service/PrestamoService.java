package ar.com.tecsat.loans.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
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
import ar.com.tecsat.loans.bean.CuotaEstado;
import ar.com.tecsat.loans.bean.utils.PrestamoFiltro;
import ar.com.tecsat.loans.dao.interfaces.PrestamoDao;
import ar.com.tecsat.loans.exceptions.AdministrativeException;
import ar.com.tecsat.loans.modelo.Cliente;
import ar.com.tecsat.loans.modelo.Cuota;
import ar.com.tecsat.loans.modelo.Perfil;
import ar.com.tecsat.loans.modelo.Prestamo;
import ar.com.tecsat.loans.modelo.PrestamoEstado;
import ar.com.tecsat.loans.util.PrestamoHelper;

/**
 * @author nicolas
 * 
 */
@Stateless
public class PrestamoService {

	@EJB
	private PrestamoDao prestamoDao;

	@EJB
	private ClienteService clienteService;
	
	@EJB
	private CuotaService cuotaService;

	@EJB
	private PerfilService perfilService;

	public List<Prestamo> findAllPrestamos() throws AdministrativeException {
		List<Prestamo> listaPrestamos = null;
		try {
			listaPrestamos = prestamoDao.findAllPrestamos();
		} catch (AdministrativeException e) {
			throw new AdministrativeException(e.getMessage());
		}
		return listaPrestamos;
	}

	public void guardarPrestamo(Prestamo prestamo) throws AdministrativeException {
		if (isClienteValido(prestamo)) {
			prestamoDao.savePrestamo(prestamo);
		} else {
			throw new AdministrativeException("El cliente no es valido");
		}
	}

	/**
	 * @param prestamo
	 * @return
	 * @throws AdministrativeException 
	 */
	private boolean isClienteValido(Prestamo prestamo) throws AdministrativeException {
		Cliente cliente = clienteService.findClienteByPk(prestamo.getCliente().getCliId());
		if (cliente == null) {
			throw new AdministrativeException("El cliente no existe");
		}
		return true;
	}

	public List<Prestamo> findByFilter(PrestamoFiltro filtro) throws AdministrativeException {
		List<Prestamo> prestamos = null;
		try {
			prestamos = prestamoDao.findByFilter(filtro);
		} catch (AdministrativeException e) {
			throw new AdministrativeException(e.getMessage());
		}
		return prestamos;
	}

	public Prestamo crearPrestamo(PrestamoFiltro filtro) throws AdministrativeException {
		Cliente cliente = getCliente(filtro);
		Prestamo prestamo = PrestamoHelper.createPrestamo(filtro, cliente);
		return prestamo;

	}

	/**
	 * @param filtro
	 * @return
	 * @throws AdministrativeException 
	 */
	private Cliente getCliente(PrestamoFiltro filtro) throws AdministrativeException {
		Cliente cliente = null;
		cliente = clienteService.findClienteByPk(filtro.getIdCliente());
		return cliente;
	}

	public void cancelarPrestamo(Prestamo prestamo) throws AdministrativeException {
		List<Cuota> cuotas = prestamo.getCuotas();
		for (Cuota cuota : cuotas) {
			cuota.setCuoEstado(CuotaEstado.REFINANCIADO);
		}
		prestamo.setPreEstado(PrestamoEstado.REFINANCIADO);
		try {
			prestamoDao.actualizar(prestamo);
		} catch (AdministrativeException e) {
			throw new AdministrativeException("Error al actualizar el prestamo anterior");
		}
	}

	@SuppressWarnings("unchecked")
	public JasperPrint createReport(Prestamo prestamo, InputStream inputStream) throws AdministrativeException {
		JRBeanCollectionDataSource jrBeanDataSource = new JRBeanCollectionDataSource(getDatasource(prestamo));
		
		try {
			return JasperFillManager.fillReport(inputStream, setParameters(prestamo), jrBeanDataSource);
		} catch (JRException e) {
			throw new AdministrativeException("Error en el servicio de export pdf");
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map setParameters(Prestamo prestamo) throws AdministrativeException {
		Map parameters = new HashMap();
		parameters.put("titulo", "Reporte préstamo");
		parameters.put("fechaEmision", Calendar.getInstance().getTime());

		Perfil perfil = perfilService.findPerfil();
		parameters.put("perCelular", perfil.getPerCelular());

		parameters.put("cliente", prestamo.getCliente().getCliNombre());
		parameters.put("dni", prestamo.getCliente().getCliDni());
		parameters.put("telefono", prestamo.getCliente().getCliTelefono());
		parameters.put("direccion", prestamo.getCliente().getCliDireccion());
		parameters.put("entre_calles", prestamo.getCliente().getCliEntreCalle());
		parameters.put("localidad", prestamo.getCliente().getCliLocalidad());
		parameters.put("mail", prestamo.getCliente().getCliMail());

		parameters.put("capital", prestamo.getPreCapital());
		parameters.put("tasa", prestamo.getPreTasa());
		parameters.put("preFechaEntrega", prestamo.getPreFechaInicio());
		return parameters;
	}
	
	/**
	 * @param prestamo 
	 * @return
	 */
	private Collection<Cuota> getDatasource(Prestamo prestamo) {
		Collection<Cuota> lista = new ArrayList<Cuota>();
		try {
			lista = cuotaService.findCuotasByPrestamo(prestamo);
		} catch (AdministrativeException e) {
			e.printStackTrace();
		}
		return lista;
	}

}

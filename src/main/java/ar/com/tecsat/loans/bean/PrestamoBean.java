package ar.com.tecsat.loans.bean;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import ar.com.tecsat.loans.bean.utils.PrestamoFiltro;
import ar.com.tecsat.loans.controller.BasicController;
import ar.com.tecsat.loans.exceptions.AdministrativeException;
import ar.com.tecsat.loans.modelo.Cliente;
import ar.com.tecsat.loans.modelo.Cuota;
import ar.com.tecsat.loans.modelo.Perfil;
import ar.com.tecsat.loans.modelo.Prestamo;
import ar.com.tecsat.loans.modelo.TipoPrestamo;
import ar.com.tecsat.loans.service.ClienteService;
import ar.com.tecsat.loans.service.CuotaService;
import ar.com.tecsat.loans.service.PerfilService;
import ar.com.tecsat.loans.service.PrestamoService;

/**
 * @author nicolas
 * 
 */
@SuppressWarnings("serial")
@Named
@SessionScoped
public class PrestamoBean extends BasicController implements Serializable {

	private static final String REFINANCIAR = "refinanciar";
	
	// Propiedades
	private Prestamo prestamo;
	private List<Prestamo> listaPrestamo;
	private List<Cliente> listaCliente;
	private TipoPrestamo[] tipoPrestamo;

	private PrestamoFiltro filtro = new PrestamoFiltro();
	private boolean editPrestamo = false;
	private Stack<String> STEP = new Stack<String>();

	@EJB
	private PrestamoService prestamoService;
	
	@EJB
	private CuotaService cuotaService;

	@EJB
	private ClienteService clienteService;
	
	@EJB
	private PerfilService perfilService;

	// Actions
	public String init() {
		List<Prestamo> prestamos = null;
		try {
			prestamos = prestamoService.findAllPrestamos();
			inicializarForm();
		} catch (AdministrativeException e) {
			addMessageError(e.getMessage());
			return null;
		}
		cleanStack();
		if (prestamos.isEmpty() || prestamos == null) {
			addMessageWarn("No hay registros");
		}
		return FILTER;
	}

	public TipoPrestamo[] getTipoPrestamo() {
		return TipoPrestamo.values();
	}
	
	private void cleanStack() {
		STEP.clear();
	}

	public String buscar() {
		List<Prestamo> prestamos = null;
		try {
			prestamos = prestamoService.findByFilter(getFiltro());
		} catch (AdministrativeException e) {
			addMessageError(e.getMessage());
			return null;
		}
		if (prestamos == null || prestamos.isEmpty()) {
			addMessageWarn("No hay registros");
			return null;
		}
		saveStep();
		this.listaPrestamo = prestamos;
		return LIST;
	}

	public String stepBack() {
		String result = null;
		if (STEP.size() == 1) {
			result = STEP.get(0);
		} else {
			result = STEP.pop();
		}
		if (result.equals(FILTER))
			try {
				inicializarForm();
			} catch (AdministrativeException e) {
				addMessageError(e.getMessage());
				return null;
			}
		if (result.equals(SUMMARY))
			this.editPrestamo = false;
		return result;
	}

	public String filter() {
		try {
			inicializarForm();
		} catch (AdministrativeException e) {
			addMessageError(e.getMessage());
			return null;
		}
		return FILTER;
	}

	public String create() {
		try {
			inicializarForm();
		} catch (AdministrativeException e) {
			addMessageError(e.getMessage());
			return null;
		}
		saveStep();
		return CREATE;
	}

	public String cancel() {
		if (!getCurrentView().equals(SUMMARY)) {
			try {
				inicializarForm();
			} catch (AdministrativeException e) {
				addMessageError(e.getMessage());
				return null;
			}
		}
		return stepBack();
	}
	
	public String saveRefinanciar() {
		getFiltro().setPrestamo(getPrestamo());
		return save();
	}

	public String save() {
		Prestamo nuevoPrestamo = null;
		try {
			nuevoPrestamo = prestamoService.crearPrestamo(filtro);
		} catch (AdministrativeException e) {
			addMessageError(e.getMessage());
			return null;
		}
		if (nuevoPrestamo == null) {
			addMessageError("Error en el servicio");
			return null;
		}
		setPrestamo(nuevoPrestamo);
		saveStep();
		addMessageInfo("Verifique los datos ingresado y presione confirmar para finalizar con la operación");
		setEditPrestamo(false);
 		return CONFIRM;
	}

	
	public String refinan(Prestamo prestamo) {
		PrestamoFiltro filtro = new PrestamoFiltro();
		BigDecimal monto = getMonto(prestamo);
		filtro.setCapital(monto);
		filtro.setIdCliente(prestamo.getCliente().getCliId());
		setPrestamo(prestamo);
		setFiltro(filtro);
		saveStep();
		return REFINANCIAR;
	}
	
	/**
	 * @param prestamo
	 * @return
	 */
	private BigDecimal getMonto(Prestamo prestamo) {
		BigDecimal monto = new BigDecimal(0);
		List<Cuota> cuotas = prestamo.getCuotas();
		for (Cuota cuota : cuotas) {
			monto = monto.add(cuota.getCuoSaldo());
		}
		return monto;
	}

	public String confirm() {
		try {
			prestamoService.guardarPrestamo(getPrestamo());
			if (getFiltro().getPrestamo() != null) {
				prestamoService.cancelarPrestamo(getFiltro().getPrestamo());
			}
			inicializarForm();
		} catch (AdministrativeException e) {
			addMessageError(e.getMessage());
			return null;
		}
		addMessageInfo("Operación realizada");
		return init();
	}

	public String detalle(Prestamo pre) {
		this.prestamo = pre;
		saveStep();
		return SUMMARY;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void exportPdf() throws JRException, IOException {
		
		ServletOutputStream outputStream = null;
		try {
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
					.getResponse();
			
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename=\"report.pdf\"");
			
			outputStream = response.getOutputStream();
			Map parameters = new HashMap();
			parameters.put("titulo", "Reporte préstamo");
			parameters.put("fechaEmision", Calendar.getInstance().getTime());
			
			Perfil perfil = perfilService.findPerfil();
			parameters.put("perNombre", perfil.getPerNombre());
			parameters.put("perMail", perfil.getPerMail());
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
			
			String path = "/WEB-INF/reportes/reportprestamo.jrxml";
			
			InputStream jasperTemplate = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(path);

			JasperReport jasperReport = JasperCompileManager.compileReport(jasperTemplate);

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, getDatasource());
			JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
			
			FacesContext.getCurrentInstance().renderResponse();
			FacesContext.getCurrentInstance().responseComplete();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			outputStream.flush();
			outputStream.close();
		}
	}

	/**
	 * @return
	 */
	private JRBeanCollectionDataSource getDatasource() {
		Collection<Cuota> lista = null;
		try {
			lista = cuotaService.findCuotasByPrestamo(prestamo);
		} catch (AdministrativeException e) {
			e.printStackTrace();
		}
		JRBeanCollectionDataSource beans = new JRBeanCollectionDataSource(lista);
		return beans;
	}

	private void saveStep() {
		STEP.push(getCurrentView());
	}

	private void inicializarForm() throws AdministrativeException {
		List<Cliente> clientes = clienteService.findClientes();
		setListaCliente(clientes);
		this.filtro = new PrestamoFiltro();
		this.prestamo = new Prestamo();
		setEditPrestamo(false);
	}

	public Prestamo getPrestamo() {
		return prestamo;
	}

	public void setPrestamo(Prestamo prestamo) {
		this.prestamo = prestamo;
	}

	public List<Prestamo> getListaPrestamo() {
		return listaPrestamo;
	}

	public void setListaPrestamo(List<Prestamo> listaPrestamo) {
		this.listaPrestamo = listaPrestamo;
	}

	public List<Cliente> getListaCliente() {
		return listaCliente;
	}

	public void setListaCliente(List<Cliente> listaCliente) {
		this.listaCliente = listaCliente;
	}

	public boolean isEditPrestamo() {
		return editPrestamo;
	}

	public void setEditPrestamo(boolean editPrestamo) {
		this.editPrestamo = editPrestamo;
	}

	public PrestamoFiltro getFiltro() {
		return filtro;
	}

	public void setFiltro(PrestamoFiltro filtro) {
		this.filtro = filtro;
	}

	public Stack<String> getSTEP() {
		return STEP;
	}

	public void setSTEP(Stack<String> sTEP) {
		STEP = sTEP;
	}

	public void setTipoPrestamo(TipoPrestamo[] tipoPrestamo) {
		this.tipoPrestamo = tipoPrestamo;
	}

}

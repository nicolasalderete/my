package ar.com.tecsat.loans.bean;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Stack;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import ar.com.tecsat.loans.bean.utils.PrestamoFiltro;
import ar.com.tecsat.loans.controller.BasicController;
import ar.com.tecsat.loans.exceptions.AdministrativeException;
import ar.com.tecsat.loans.modelo.Cliente;
import ar.com.tecsat.loans.modelo.Cuota;
import ar.com.tecsat.loans.modelo.Prestamo;
import ar.com.tecsat.loans.modelo.TipoPrestamo;
import ar.com.tecsat.loans.service.ClienteService;
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
	private ClienteService clienteService;

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

	public void exportPdf() throws JRException, IOException {

		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

		InputStream inputStream = getFile(externalContext, "/WEB-INF/reportes/reportprestamo.jasper");
		if (inputStream == null) {
			throw new RuntimeException("Error al cargar la plantilla");
		}

		JasperPrint jasperPrint = null;
		try {
			jasperPrint = prestamoService.createReport(prestamo, inputStream);
		} catch (Exception e) {
			throw new RuntimeException("Error al compilar el reporte");
		} finally {
			inputStream.close();
		}
		
		HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ServletOutputStream outputStream = null;
		
		try {
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename=\"report.pdf\"");
			outputStream = response.getOutputStream();
			
			JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
			
		} catch (Exception e) {
			throw new RuntimeException("Error al exportar el pdf");
		} finally {
			outputStream.flush();
			outputStream.close();
			facesContext.renderResponse();
			facesContext.responseComplete();
		}
	}

	private InputStream getFile(ExternalContext externalContext, String path) {
		return externalContext.getResourceAsStream(path);
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

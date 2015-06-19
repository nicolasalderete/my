package ar.com.tecsat.loans.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import ar.com.tecsat.loans.bean.utils.PrestamoFiltro;
import ar.com.tecsat.loans.exceptions.AdministrativeException;
import ar.com.tecsat.loans.modelo.Cliente;
import ar.com.tecsat.loans.modelo.Cuota;
import ar.com.tecsat.loans.modelo.Prestamo;
import ar.com.tecsat.loans.modelo.TipoPrestamo;
import ar.com.tecsat.loans.pdf.AbstractReportBean;
import ar.com.tecsat.loans.service.ClienteService;
import ar.com.tecsat.loans.service.CuotaService;
import ar.com.tecsat.loans.service.PrestamoService;

/**
 * @author nicolas
 * 
 */
@SuppressWarnings("serial")
@Named
@SessionScoped
public class PrestamoBean extends AbstractReportBean implements Serializable {

	// Propiedades
	private Prestamo prestamo;
	private List<Prestamo> listaPrestamo;
	private List<Cliente> listaCliente;

	private PrestamoFiltro filtro = new PrestamoFiltro();
	private boolean editPrestamo = false;
	private Stack<String> STEP = new Stack<String>();

	@EJB
	private PrestamoService prestamoService;

	@EJB
	private ClienteService clienteService;
	
	@EJB
	private CuotaService cuotaService;

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
/*
	public String exportPdf() throws IOException {

		byte[] pdf = null;

		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

		InputStream inputStream = getFile(externalContext, "/WEB-INF/reportes/reportprestamo.jasper");
		
		BufferedImage image = null;
//		try {
//			image = ImageIO.read(externalContext.getResource("/WEB-INF/reportes/logo.jpg"));
//		} catch (IOException e) {
//			addMessageError("Error al cargar la plantilla");
//			return null;
//		}
		
		if (inputStream == null) {
			addMessageError("Error al cargar la plantilla");
			return null;
		}
		
		JasperPrint jasperPrint = null;
		try {
			jasperPrint = prestamoService.createReport(prestamo, inputStream, image);
		} catch (Exception e) {
			addMessageError("Error al compilar el reporte");
			return null;
		} finally {
			inputStream.close();
		}

		HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ServletOutputStream outputStream = null;

		try {
			outputStream = response.getOutputStream();
			JRPdfExporter exporter = new JRPdfExporter();
			exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			ByteArrayOutputStream pdfByteArray = new ByteArrayOutputStream();
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(pdfByteArray));
			exporter.exportReport();
			pdf = pdfByteArray.toByteArray();
			//JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename=\"reporte.pdf\"");

			pdfByteArray.close();
		} catch (Exception e) {
			addMessageError("Error al exportar el pdf");
			return null;
		} finally {
			outputStream.write(pdf);
			outputStream.flush();
			outputStream.close();
			facesContext.responseComplete();
		}
		return null;
	}
	
	public String eliminar() {
		try {
			prestamoService.borrarPrestamo(this.prestamo);
			inicializarForm();
		} catch (AdministrativeException e) {
			addMessageError(e.getMessage());
			return null;
		}
		return FILTER;
	}
*/
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
	
	private Collection<Cuota> getDatasource(Prestamo prestamo) {
		Collection<Cuota> lista = new ArrayList<Cuota>();
		try {
			lista = cuotaService.findCuotasByPrestamo(prestamo);
		} catch (AdministrativeException e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	public String exportPdf() throws IOException {
		Collection<Cuota> cuotas = getDatasource(prestamo);
		try {
			super.prepareReport(prestamo, cuotas);
        } catch (Exception e) {
            // make your own exception handling
            e.printStackTrace();
        }
        return null;
	}

	@Override
	protected String getCompileFileName() {
		return "reportprestamo";
	}

}

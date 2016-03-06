package ar.com.tecsat.loans.bean;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import ar.com.tecsat.loans.bean.utils.CuotaFiltro;
import ar.com.tecsat.loans.controller.BasicController;
import ar.com.tecsat.loans.exceptions.AdministrativeException;
import ar.com.tecsat.loans.modelo.Cliente;
import ar.com.tecsat.loans.modelo.Cuota;
import ar.com.tecsat.loans.modelo.Prestamo;
import ar.com.tecsat.loans.service.ClienteService;
import ar.com.tecsat.loans.service.CuotaService;
import ar.com.tecsat.loans.service.PrestamoService;
import ar.com.tecsat.loans.util.OrderList;

/**
 * @author nicolas
 * 
 */
@SuppressWarnings("serial")
@Named
@SessionScoped
public class CuotaBean extends BasicController implements Serializable {

	private Cuota cuota;
	private List<Cuota> listaCuota = new ArrayList<Cuota>();
	private List<Prestamo> listaPrestamo = new ArrayList<Prestamo>();
	private List<Cliente> listaCliente = new ArrayList<Cliente>();
	private CuotaFiltro filtro = new CuotaFiltro();
	private Stack<String> STEP = new Stack<String>();
	private boolean isVigente = true;

	@EJB
	private ClienteService clienteService;

	@EJB
	private PrestamoService prestamoService;

	@EJB
	private CuotaService cuotaService;

	public String init() {
		try {
			inicializarForm();
		} catch (AdministrativeException e) {
			addMessageError(e.getMessage());
			return null;
		}
		return FILTER;
	}

	public String search() {
		try {
			setListaCuota(cuotaService.search(filtro));
		} catch (AdministrativeException e) {
			addMessageError(e.getMessage());
			return null;
		}
		saveStep();
		return LIST;
	}

	public String crearPago(Cuota cuota) {
		try {
			evaluateRules(cuota);
		} catch (AdministrativeException e) {
			addMessageError(e.getMessage());
			return null;
		}
		setCuota(cuota);
		saveStep();
		return PAGO;
	}
	
	/**
	 * @param cuota
	 * @throws AdministrativeException
	 */
	private void evaluateRules(Cuota cuota) throws AdministrativeException {
		evaluateTotalPagar(cuota);
	}

	/**
	 * @param cuota
	 * @throws AdministrativeException
	 */
	private void evaluateTotalPagar(Cuota cuota) throws AdministrativeException {
		if (cuota.getCuoSaldo().compareTo(BigDecimal.valueOf(0)) <= 0) {
			throw new AdministrativeException("No es posible pagar la cuota");
		}
	}

	public String pagar() {
		try {
			cuotaService.pagar(cuota, filtro);
		} catch (AdministrativeException e) {
			this.addMessageError(e.getMessage());
			return null;
		}
		this.addMessageInfo("Operacion realizada");
		return init();
	}

	public String detail(Cuota cuota) {
		setCuota(cuota);
		setVigente(cuota.getCuoSaldo().compareTo(new BigDecimal(0)) > 0);
		saveStep();
		return SUMMARY;
	}

	public String stepBack() {
		String result = STEP.pop();
		if (result.equals(FILTER)) {
			try {
				inicializarForm();
			} catch (AdministrativeException e) {
				addMessageError(e.getMessage());
				return null;
			}
		}
		return result;
	}

	public String cancelPago() {
		String result = STEP.pop();
		return result;
	}

	private void inicializarForm() throws AdministrativeException {
		setCuota(new Cuota());
		try {
			setListaCliente(clienteService.findClientes());
		} catch (AdministrativeException e) {
			throw new AdministrativeException(e.getMessage());
		}
		setListaCuota(new ArrayList<Cuota>());
		setFiltro(new CuotaFiltro());
		STEP.clear();
	}

	public String modify() {
		setCuota(cuota);
		return INTERESES;
	}

	public String cancelUpdate() {
		return stepBack();
	}

	public String update() {
		try {
			cuotaService.actualizarCuotaIntereses(cuota, filtro);
			setFiltro(new CuotaFiltro());
		} catch (AdministrativeException e) {
			addMessageError(e.getMessage());
		}
		addMessageInfo("Operación realizada");
		return SUMMARY;
	}

	public String exportHojaRuta() throws IOException {

		byte[] pdf = null;

		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

		InputStream inputStream = getFile(externalContext, "/WEB-INF/reportes/reporthojaruta.jasper");
		BufferedImage image;
		try {
			image = ImageIO.read(externalContext.getResource("/WEB-INF/reportes/logo.jpg"));
		} catch (IOException e) {
			addMessageError("Error al cargar la plantilla");
			return null;
		}

		if (inputStream == null) {
			addMessageError("Error al cargar la plantilla");
			return null;
		}

		JasperPrint jasperPrint = null;
		try {
			jasperPrint = cuotaService.createHojaRuta(listaCuota, image, inputStream);
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
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename=\"hojaDeRuta.pdf\"");

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

	private void saveStep() {
		STEP.push(getCurrentView());
	}

	public Cuota getCuota() {
		return cuota;
	}

	public void setCuota(Cuota cuota) {
		this.cuota = cuota;
	}

	public List<Cuota> getListaCuota() {
		return listaCuota;
	}

	public void setListaCuota(List<Cuota> listaCuota) {
		this.listaCuota = listaCuota;
	}

	public List<Cliente> getListaCliente() {
		return listaCliente;
	}

	public void setListaCliente(List<Cliente> listaCliente) {
		this.listaCliente = listaCliente;
	}

	public CuotaFiltro getFiltro() {
		return filtro;
	}

	public void setFiltro(CuotaFiltro filtro) {
		this.filtro = filtro;
	}

	public Stack<String> getSTEP() {
		return STEP;
	}

	public void setSTEP(Stack<String> sTEP) {
		STEP = sTEP;
	}

	public List<Prestamo> getListaPrestamo() {
		return listaPrestamo;
	}

	public void setListaPrestamo(List<Prestamo> listaPrestamo) {
		this.listaPrestamo = listaPrestamo;
	}

	public boolean isVigente() {
		return isVigente;
	}

	public void setVigente(boolean isVigente) {
		this.isVigente = isVigente;
	}


	public void changeSelectPrestamo() {
		if (null == filtro.getIdCliente()) {
			this.setListaPrestamo(new ArrayList<Prestamo>());
		} else {
			List<Prestamo> lista = prestamoService.findByCliente(filtro.getIdCliente());
			this.setListaPrestamo(OrderList.sortPrestamos(lista));
		}
	}
	
	@SuppressWarnings("unchecked")
	public void exportPdf() throws JRException, IOException {
		String path = "/WEB-INF/reportes/reporteCuota.jrxml";

		InputStream jasperTemplate = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(path);

		JasperReport jasperReport = JasperCompileManager.compileReport(jasperTemplate);

		@SuppressWarnings("rawtypes")
		Map parameters = new HashMap();
		parameters.put("fechaEmision", Calendar.getInstance().getTime());
		parameters.put("cliente", cuota.getPrestamo().getCliente().getCliNombre());
		parameters.put("mail", cuota.getPrestamo().getCliente().getCliMail());
		parameters.put("cuota", cuota.toString());
		parameters.put("importe", cuota.getCuoImporte());
		parameters.put("total", cuota.getCuoSaldo());
		parameters.put("fechaPago", cuota.getCuoFechaVencimiento());
		
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
				.getResponse();

		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=\"report.pdf\"");

		ServletOutputStream outputStream = response.getOutputStream();
		JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);

		outputStream.flush();
		outputStream.close();
		FacesContext.getCurrentInstance().renderResponse();
		FacesContext.getCurrentInstance().responseComplete();
	}
	

}

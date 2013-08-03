package ar.com.tecsat.loans.bean;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Calendar;
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

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import ar.com.tecsat.loans.bean.utils.PagoFiltro;
import ar.com.tecsat.loans.controller.BasicController;
import ar.com.tecsat.loans.exceptions.AdministrativeException;
import ar.com.tecsat.loans.modelo.Cliente;
import ar.com.tecsat.loans.modelo.Pago;
import ar.com.tecsat.loans.modelo.Prestamo;
import ar.com.tecsat.loans.service.ClienteService;
import ar.com.tecsat.loans.service.PagoService;
import ar.com.tecsat.loans.service.PrestamoService;

/**
 * @author nicolas
 *
 */
@SuppressWarnings("serial")
@Named
@SessionScoped
public class PagoBean extends BasicController implements Serializable{

	private Pago pago;
	private List<Pago> listaPagos;
	private List<Cliente> listaClientes;
	private List<Prestamo> listaPrestamo;
	private PagoFiltro filtro;
	private boolean editPago;
	private Stack<String> STEP = new Stack<String>();
	
	@EJB
	private PrestamoService prestamoService;
	
	@EJB
	private ClienteService clienteService;
	
	@EJB
	private PagoService pagoService;
	
	public String init() {
		List<Pago> pagos = null;
		try {
			pagos = pagoService.findPagos();
			inicializarForm();
		} catch (AdministrativeException e) {
			addMessageError(e.getMessage());
			return null;
		}
		if (pagos == null){
			addMessageWarn("No hay registros");
		}
		clearCheckPoint();
		return FILTER;
	}
	
	public String search(){
		List<Pago> pagos = null;
		try {
			pagos = pagoService.finPagosByFilter(filtro);
		} catch (AdministrativeException e){
			addMessageError(e.getMessage());
			return null;
		}
		if (pagos == null || pagos.isEmpty()){
			addMessageError("No hay registros");
			return null;
		}
		setListaPagos(pagos);
		saveStep();
		return LIST;
	}
	
	private void saveStep() {
		STEP.push(getCurrentView());
	}

	private void inicializarForm() throws AdministrativeException {
		List<Cliente> clientes = clienteService.findClientes();
		setListaClientes(clientes);
		List<Prestamo> prestamos = prestamoService.findAllPrestamos();
		setListaPrestamo(prestamos);
		setFiltro(new PagoFiltro());
		setPago(new Pago());
	}
	
	public String stepBack() {
		String result = STEP.pop();
		return result;
	}
	
	public String detail(Pago pag) {
		this.pago = pag;
		saveStep();
		return SUMMARY;
	}
	
	@SuppressWarnings("unchecked")
	public void exportPdf() throws JRException, IOException {
		String path = "/WEB-INF/reportes/reporteCuota.jrxml";

		InputStream jasperTemplate = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(path);

		JasperReport jasperReport = JasperCompileManager.compileReport(jasperTemplate);

		@SuppressWarnings("rawtypes")
		Map parameters = new HashMap();
		parameters.put("fechaEmision", Calendar.getInstance().getTime());
		parameters.put("cliente", pago.getCuota().getPrestamo().getCliente().getCliNombre());
		parameters.put("mail", pago.getCuota().getPrestamo().getCliente().getCliMail());
		parameters.put("cuota", pago.getCuota().toString());
		parameters.put("importe", pago.getCuota().getCuoImporte());
		parameters.put("total", pago.getCuota().getCuoTotalPagar());
		parameters.put("fechaVto", pago.getCuota().getCuoFechaVencimiento());
		
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
	
	
	private void clearCheckPoint() {
		STEP.clear();
	}
	
	public Pago getPago() {
		return pago;
	}
	public void setPago(Pago pago) {
		this.pago = pago;
	}
	public List<Pago> getListaPagos() {
		return listaPagos;
	}
	public void setListaPagos(List<Pago> listaPagos) {
		this.listaPagos = listaPagos;
	}
	public PagoFiltro getFiltro() {
		return filtro;
	}
	public void setFiltro(PagoFiltro filtro) {
		this.filtro = filtro;
	}
	public boolean isEditPago() {
		return editPago;
	}
	public void setEditPago(boolean editPago) {
		this.editPago = editPago;
	}


	public Stack<String> getSTEP() {
		return STEP;
	}


	public void setSTEP(Stack<String> sTEP) {
		STEP = sTEP;
	}
	
	public List<Cliente> getListaClientes() {
		return listaClientes;
	}

	public void setListaClientes(List<Cliente> listaClientes) {
		this.listaClientes = listaClientes;
	}

	public List<Prestamo> getListaPrestamo() {
		return listaPrestamo;
	}

	public void setListaPrestamo(List<Prestamo> listaPrestamo) {
		this.listaPrestamo = listaPrestamo;
	}

}

package ar.com.tecsat.loans.bean;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
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
import ar.com.tecsat.loans.bean.utils.CuotaFiltro;
import ar.com.tecsat.loans.controller.BasicController;
import ar.com.tecsat.loans.exceptions.AdministrativeException;
import ar.com.tecsat.loans.modelo.Cliente;
import ar.com.tecsat.loans.modelo.Cuota;
import ar.com.tecsat.loans.modelo.Prestamo;
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
		List<Cuota> cuotas = null;
		try {
			cuotas = cuotaService.findCuotas();
			inicializarForm();
		} catch (AdministrativeException e) {
			addMessageError(e.getMessage());
			return null;
		}
		if (cuotas.isEmpty() || cuotas == null)
			addMessageWarn("No hay registros");
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
		setCuota(cuota);
		saveStep();
		return PAGO;
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
		setVigente(cuota.getCuoTotalPagar().compareTo(new BigDecimal(0)) > 0);
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

	public void exportPdf() throws JRException, IOException {
		String path = "/WEB-INF/reportes/reporteCuota.jrxml";

		InputStream jasperTemplate = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(path);

		JasperReport jasperReport = JasperCompileManager.compileReport(jasperTemplate);

		@SuppressWarnings("rawtypes")
		Map parameters = new HashMap();
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

	private void inicializarForm() throws AdministrativeException {
		setCuota(new Cuota());
		try {
			setListaCliente(clienteService.findClientes());
			setListaPrestamo(prestamoService.findAllPrestamos());
		} catch (AdministrativeException e) {
			throw new AdministrativeException(e.getMessage());
		}
		setListaCuota(new ArrayList<Cuota>());
		setFiltro(new CuotaFiltro());
		STEP.clear();
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

}

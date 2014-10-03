package ar.com.tecsat.loans.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

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
	private boolean editCuota = true;

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
		if (cuota.getCuoTotalPagar().compareTo(BigDecimal.valueOf(0)) <= 0) {
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
		setEditCuota(false);
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
	
	public String modify() {
		saveStep();
		setEditCuota(true);
		return SUMMARY;
	}
	
	public String cancelUpdate() {
		setEditCuota(false);
		return stepBack();
	}
	
	public String update() {
		try {
			cuotaService.actualizarCuotaIntereses(cuota, filtro);
		} catch (AdministrativeException e) {
			addMessageError(e.getMessage());
		}
		setEditCuota(false);
		addMessageInfo("Operación realizada");
		return SUMMARY;
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

	public boolean isEditCuota() {
		return editCuota;
	}

	public void setEditCuota(boolean editCuota) {
		this.editCuota = editCuota;
	}

}

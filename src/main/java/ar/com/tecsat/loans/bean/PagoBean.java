package ar.com.tecsat.loans.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Stack;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

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

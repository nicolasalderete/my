package ar.com.tecsat.loans.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import ar.com.tecsat.loans.bean.utils.ClienteFiltro;
import ar.com.tecsat.loans.controller.BasicController;
import ar.com.tecsat.loans.exceptions.AdministrativeException;
import ar.com.tecsat.loans.modelo.Cliente;
import ar.com.tecsat.loans.service.ClienteService;

/**
 * @author nicolas
 *
 */
@SuppressWarnings("serial")
@Named
@SessionScoped
public class ClienteBean extends BasicController implements Serializable{

	// Properties
	private Cliente cliente = new Cliente();
	private List<Cliente> listaCliente = new ArrayList<Cliente>();
	private boolean editCliente = false;
	private Stack<String> STEP = new Stack<String>();
	private ClienteFiltro filtro = new ClienteFiltro();
	
	@EJB
	private ClienteService clienteService;

	// Actions
	
	public String init(){
		inicializarForm();
		cleanStack();
		return FILTER;
	}
	
	private void cleanStack() {
		STEP.clear();
	}

	public String stepBack() {
		String result = null;
		if (STEP.size() == 1) {
			result = STEP.get(0);
		} else {
			result = STEP.pop();
		}
		if (result.equals(FILTER))
			inicializarForm();
		if (result.equals(SUMMARY))
			editCliente = false;
		return result ;
	}
	
	public String filter() {
		inicializarForm();
		return FILTER;
	}
	
	public String create() {
		saveStep();
		inicializarForm();
		return CREATE;
	}
	
	public String cancel() {
		if (!getCurrentView().equals(SUMMARY))
			inicializarForm();
		return stepBack();
	}
	
	public String list() {
		List<Cliente> clientes = null;
		try {
			clientes = clienteService.findClientes();
		} catch (AdministrativeException e) {
			addMessageError(e.getMessage());
		}
		if (clientes.isEmpty() || clientes == null){
			addMessageWarn("No hay registros");
			return LIST;
		}
		inicializarListado(clientes);
		return LIST;
	}
	
	public String detail(Cliente cli) {
		this.cliente = cli;
		saveStep();
		return SUMMARY;
	}
	
	public String search() {
		List<Cliente> clientes = null;
		try {
			clientes = clienteService.findByFilter(this.filtro);
		} catch (AdministrativeException e) {
			addMessageError(e.getMessage());
			return null;
		}
		if (clientes.isEmpty() || clientes == null){
			addMessageWarn("No hay registros");
			return null;
		}
		inicializarListado(clientes);
		saveStep();
		return LIST;
	}

	private void saveStep() {
		STEP.push(getCurrentView());
	}
	
	public String save() {
		Cliente cliente = null;
		try {
			cliente = clienteService.findClienteByDni(getCliente().getCliDni());
		} catch (AdministrativeException e) {
			addMessageError(e.getMessage());
			return null;
		}
		if (cliente == null) {
			addMessageInfo("Verifique los datos ingresado y presione confirmar para finalizar con la operación");
			setEditCliente(false);
			saveStep();
			return CONFIRM;
		}
		addMessageError("El cliente ya existe");
		return null;
	}
	
	public String confirm() {
		try {
			clienteService.guardarCliente(getCliente());
		} catch (AdministrativeException e) {
			addMessageError(e.getMessage());
			return null; 
		}
		addMessageInfo("Operación realizada");
		inicializarForm();
		return stepBack();
	}

	public String delete() {
		try {
			clienteService.eliminarCliente(getCliente());
		} catch (AdministrativeException e) {
			addMessageError(e.getMessage());
			return null;
		}
		listaCliente.remove(getCliente());
		addMessageInfo("Operación realizada");
		return stepBack();
	}
	
	public String deleteList(Cliente cli) {
		try {
			clienteService.eliminarCliente(cli);
		} catch (AdministrativeException e) {
			addMessageError(e.getMessage());
			return null;
		}
		this.listaCliente.remove(cli);
		addMessageInfo("Operación realizada");
		return null;
	}
	
	public String modify() {
		saveStep();
		setEditCliente(true);
		return SUMMARY;
	}

	public String update() {
		try {
			clienteService.actualizarCliente(getCliente());
		} catch (AdministrativeException e) {
			addMessageError(e.getMessage());
			return null;
		}
		addMessageInfo("Operación realizada");
		return stepBack();
	}
	private void inicializarListado(List<Cliente> clientes) {
		setListaCliente(clientes);
	}

	private void inicializarForm() {
		this.cliente = new Cliente();
		this.filtro = new ClienteFiltro();
		this.editCliente = false;
	}

	//Getters And Setters

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public List<Cliente> getListaCliente() {
		return listaCliente;
	}

	public void setListaCliente(List<Cliente> listaCliente) {
		this.listaCliente = listaCliente;
	}

	public boolean isEditCliente() {
		return editCliente;
	}

	public void setEditCliente(boolean editCliente) {
		this.editCliente = editCliente;
	}

	public ClienteFiltro getFiltro() {
		return filtro;
	}

	public void setFiltro(ClienteFiltro filtro) {
		this.filtro = filtro;
	}

	public Stack<String> getSTEP() {
		return STEP;
	}

	public void setSTEP(Stack<String> sTEP) {
		STEP = sTEP;
	}

}

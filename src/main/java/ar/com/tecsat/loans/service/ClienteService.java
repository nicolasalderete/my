package ar.com.tecsat.loans.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ar.com.tecsat.loans.bean.utils.ClienteFiltro;
import ar.com.tecsat.loans.dao.interfaces.ClienteDao;
import ar.com.tecsat.loans.exceptions.AdministrativeException;
import ar.com.tecsat.loans.modelo.Cliente;

/**
 * @author nicolas
 * 
 */
@Stateless
public class ClienteService {

	@EJB
	private ClienteDao clienteDao;

	public List<Cliente> findClientes() throws AdministrativeException {
		List<Cliente> clientes = null;
		try {
			clientes = clienteDao.findClientes();
		} catch (AdministrativeException e) {
			throw new AdministrativeException(e.getMessage());
		}
		return clientes;
	}

	public void eliminarCliente(Cliente cli) throws AdministrativeException {
		try {
			clienteDao.deleteCliente(cli);
		} catch (AdministrativeException e) {
			throw new AdministrativeException(e.getMessage());
		}
	}

	/**
	 * @param cliente
	 * @throws AdministrativeException 
	 */
	public boolean noExisteCliente(String dni) throws AdministrativeException {
		Cliente cliente = null;
		try {
			cliente = clienteDao.findClienteByDni(dni);
		} catch (AdministrativeException e) {
			throw new AdministrativeException(e.getMessage());
		}
		if (cliente == null) {
			return true;
		}
		return false;
	}

	/**
	 * @param idCliente
	 * @return
	 * @throws AdministrativeException 
	 */
	public Cliente findClienteByPk(Integer idCliente) throws AdministrativeException {
		Cliente cliente = null;
		try {
			cliente = clienteDao.findClienteByPk(idCliente.intValue());
		} catch (AdministrativeException e) {
			throw new AdministrativeException("Error al consultar los datos");
		}
		
		return cliente;
	}

	/**
	 * @param filtro
	 * @throws AdministrativeException 
	 */
	public List<Cliente> findByFilter(ClienteFiltro filtro) throws AdministrativeException {
		List<Cliente> clientes = null;
		try {
			clientes = clienteDao.findByFilter(filtro);
		} catch (AdministrativeException e) {
			throw new AdministrativeException(e.getMessage());
		}
		return clientes;
	}
	
	public void guardarCliente(Cliente cliente) throws AdministrativeException {
		try {
			if (noExisteCliente(cliente.getCliDni())) {
				clienteDao.guardar(cliente);
			} else {
				throw new AdministrativeException("El Cliente ya existe");
			}
		} catch (AdministrativeException e) {
			throw new AdministrativeException(e.getMessage());
		}
	}

	/**
	 * @param cliente
	 * @throws AdministrativeException 
	 */
	public void actualizarCliente(Cliente cli) throws AdministrativeException {
		try {
			clienteDao.update(cli);
		} catch (AdministrativeException e) {
			throw new AdministrativeException(e.getMessage());
		}
	}

	/**
	 * @param idCliente
	 * @return
	 * @throws AdministrativeException 
	 */
	public Cliente findClienteByDni(String dni) throws AdministrativeException {
		Cliente cliente = null;
		try {
			cliente = clienteDao.findClienteByDni(dni);
		} catch (AdministrativeException e) {
			throw new AdministrativeException(e.getMessage());
		}
		return cliente;
	}
}

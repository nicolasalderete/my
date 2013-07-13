package ar.com.tecsat.loans.dao.interfaces;

import java.util.List;

import javax.ejb.Local;

import ar.com.tecsat.loans.bean.utils.ClienteFiltro;
import ar.com.tecsat.loans.exceptions.AdministrativeException;
import ar.com.tecsat.loans.modelo.Cliente;

/**
 * @author nicolas
 *
 */
@Local
public interface ClienteDao {
	
	public List<Cliente> findClientes() throws AdministrativeException;
	
	public void saveCliente(Cliente cliente) throws AdministrativeException;
	
	public Cliente findClienteByDni(String dni) throws AdministrativeException;
	
	public void deleteCliente(Cliente cliente) throws AdministrativeException;
	
	public Cliente findClienteByPk(Integer CliId) throws AdministrativeException;

	public List<Cliente> findByFilter(ClienteFiltro filtro) throws AdministrativeException;

	public void guardar(Cliente cliente) throws AdministrativeException;

	public void update(Cliente cliente) throws AdministrativeException;

}

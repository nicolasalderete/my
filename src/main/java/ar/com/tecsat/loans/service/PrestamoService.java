package ar.com.tecsat.loans.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ar.com.tecsat.loans.bean.utils.PrestamoFiltro;
import ar.com.tecsat.loans.dao.interfaces.PrestamoDao;
import ar.com.tecsat.loans.exceptions.AdministrativeException;
import ar.com.tecsat.loans.modelo.Cliente;
import ar.com.tecsat.loans.modelo.Prestamo;
import ar.com.tecsat.loans.util.PrestamoHelper;

/**
 * @author nicolas
 * 
 */
@Stateless
public class PrestamoService {

	@EJB
	private PrestamoDao prestamoDao;

	@EJB
	private ClienteService clienteService;

	public List<Prestamo> findAllPrestamos() throws AdministrativeException {
		List<Prestamo> listaPrestamos = null;
		try {
			listaPrestamos = prestamoDao.findAllPrestamos();
		} catch (AdministrativeException e) {
			throw new AdministrativeException(e.getMessage());
		}
		return listaPrestamos;
	}

	public void guardarPrestamo(Prestamo prestamo) throws AdministrativeException {
		if (isClienteValido(prestamo)) {
			prestamoDao.savePrestamo(prestamo);
		} else {
			throw new AdministrativeException("El cliente no es valido");
		}
	}

	/**
	 * @param prestamo
	 * @return
	 * @throws AdministrativeException 
	 */
	private boolean isClienteValido(Prestamo prestamo) throws AdministrativeException {
		Cliente cliente = clienteService.findClienteByPk(prestamo.getCliente().getCliId());
		if (cliente == null) {
			throw new AdministrativeException("El cliente no existe");
		}
		return true;
	}

	public List<Prestamo> findByFilter(PrestamoFiltro filtro) throws AdministrativeException {
		List<Prestamo> prestamos = null;
		try {
			prestamos = prestamoDao.findByFilter(filtro);
		} catch (AdministrativeException e) {
			throw new AdministrativeException(e.getMessage());
		}
		return prestamos;
	}

	public Prestamo crearPrestamo(PrestamoFiltro filtro) throws AdministrativeException {
		Cliente cliente = getCliente(filtro);
		Prestamo prestamo = PrestamoHelper.createPrestamo(filtro, cliente);
		return prestamo;

	}

	/**
	 * @param filtro
	 * @return
	 * @throws AdministrativeException 
	 */
	private Cliente getCliente(PrestamoFiltro filtro) throws AdministrativeException {
		Cliente cliente = null;
		cliente = clienteService.findClienteByPk(filtro.getIdCliente());
		return cliente;
	}
}

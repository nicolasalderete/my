package ar.com.tecsat.loans.dao.interfaces;

import java.util.List;

import javax.ejb.Local;

import ar.com.tecsat.loans.bean.utils.PrestamoFiltro;
import ar.com.tecsat.loans.exceptions.AdministrativeException;
import ar.com.tecsat.loans.modelo.Prestamo;

/**
 * @author nicolas
 *
 */
@Local
public interface PrestamoDao {

	public List<Prestamo> findAllPrestamos() throws AdministrativeException;
	
	public Prestamo findByPk(int preId) throws AdministrativeException;
	
	public void savePrestamo(Prestamo prestamo) throws AdministrativeException;

	public List<Prestamo> findByFilter(PrestamoFiltro filtro) throws AdministrativeException;

	public void actualizar(Prestamo prestamo) throws AdministrativeException;

	public List<Prestamo> findByIdCliente(Integer idCliente) throws AdministrativeException;
}

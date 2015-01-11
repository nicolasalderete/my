package ar.com.tecsat.loans.dao.interfaces;

import java.util.List;

import javax.ejb.Local;

import ar.com.tecsat.loans.bean.utils.PagoFiltro;
import ar.com.tecsat.loans.exceptions.AdministrativeException;
import ar.com.tecsat.loans.modelo.Pago;

/**
 * @author nicolas
 *
 */
@Local
public interface PagoDao {
	
	public List<Pago> findAllPagos() throws AdministrativeException;
	
	public List<Pago> findByFilter(PagoFiltro filtro) throws AdministrativeException;
	
	public Pago findByPk(int pagoId) throws AdministrativeException;

	public void guardar(Pago pago) throws AdministrativeException;

	public List<Pago> findByPrestamo(Integer id) throws AdministrativeException;

	public void eliminarPago(Pago pago);

}

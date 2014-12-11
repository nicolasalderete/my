package ar.com.tecsat.loans.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ar.com.tecsat.loans.bean.utils.PagoFiltro;
import ar.com.tecsat.loans.dao.interfaces.PagoDao;
import ar.com.tecsat.loans.exceptions.AdministrativeException;
import ar.com.tecsat.loans.modelo.Pago;
import ar.com.tecsat.loans.util.OrderList;

/**
 * @author nicolas
 *
 */
@Stateless
public class PagoService {

	@EJB
	private PagoDao pagoDao;
	
	public List<Pago> findPagos() throws AdministrativeException {
		List<Pago> listPagos = null;
		try {
			listPagos = pagoDao.findAllPagos();
		} catch (AdministrativeException e) {
			throw new AdministrativeException(e.getMessage());
		}
		if (listPagos == null || listPagos.isEmpty()){
			return null;
		}
		return OrderList.sortPagos(listPagos);
	}

	public List<Pago> finPagosByFilter(PagoFiltro filtro) throws AdministrativeException {
		List<Pago> pagos = null;
		try {
			pagos = pagoDao.findByFilter(filtro);
		} catch (AdministrativeException e) {
			throw new AdministrativeException(e.getMessage());
		}
		return OrderList.sortPagos(pagos);
	}
	
}

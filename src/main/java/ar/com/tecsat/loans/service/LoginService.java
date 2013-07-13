package ar.com.tecsat.loans.service;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ar.com.tecsat.loans.bean.OperadorBean;
import ar.com.tecsat.loans.dao.interfaces.OperadorDao;
import ar.com.tecsat.loans.exceptions.AdministrativeException;
import ar.com.tecsat.loans.modelo.Operador;

/**
 * @author nicolas
 *
 */
@Stateless
public class LoginService {
	
	@EJB
	private OperadorDao operadorDao;
	
	public OperadorBean validarUsuario(String user, String pass) throws AdministrativeException {
		OperadorBean operadorBean = new OperadorBean();
		Operador operador = null;
		try {
			operador = operadorDao.findByUserAndPass(user, pass);
		} catch (AdministrativeException e) {
			throw new AdministrativeException(e.getMessage());
		}
		operadorBean.setUsername(operador.getOpeUsername());
		return operadorBean;
	}

	
	public void invalidate() {
		
	}
	
	

}

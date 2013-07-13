package ar.com.tecsat.loans.dao.interfaces;

import javax.ejb.Local;

import ar.com.tecsat.loans.exceptions.AdministrativeException;
import ar.com.tecsat.loans.modelo.Operador;

/**
 * @author nicolas
 *
 */
@Local
public interface OperadorDao {

	public Operador findByUserAndPass(String user, String pass) throws AdministrativeException;
}

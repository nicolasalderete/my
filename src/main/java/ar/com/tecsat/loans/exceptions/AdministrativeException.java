package ar.com.tecsat.loans.exceptions;

/**
 * @author nicolas
 *
 */
@SuppressWarnings("serial")
public class AdministrativeException extends Exception {

	public AdministrativeException() {
		super();
	}
	
	public AdministrativeException(String message) {
		super(message);
	}
	
	public AdministrativeException(String message, Throwable e){
		super(message, e);
	}
}

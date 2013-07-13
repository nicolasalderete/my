package ar.com.tecsat.loans.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * @author nicolas
 *
 */
@FacesValidator(value = "AlfaNumericValidator")
public class AlfaNumericValidator implements Validator {
	
	public static Pattern ALPHANUMERIC = Pattern.compile("[A-Za-z0-9]+");

	@Override
	public void validate(FacesContext arg0, UIComponent arg1, Object value) throws ValidatorException {
		if (checkAlphaNumeric(value.toString())) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nombre y Apellido invalidos: ",
					value.toString());
			throw new ValidatorException(msg);
		}
	}

	public static boolean checkAlphaNumeric(String s) {
		Matcher m = ALPHANUMERIC.matcher(s);
		return m.matches();
	}
	
	

}

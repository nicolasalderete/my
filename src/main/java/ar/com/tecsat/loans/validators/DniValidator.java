package ar.com.tecsat.loans.validators;

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
@FacesValidator(value = "DniValidator")
public class DniValidator implements Validator {
	
	private static final String NUM_PATTER = "[0-9]*";
	
	@Override
	public void validate(FacesContext arg0, UIComponent arg1, Object value) throws ValidatorException {
		if (!isNumber(value.toString())) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Número invalido ", value.toString());
			throw new ValidatorException(msg);
		}
	}
	
	private boolean isNumber(String arg2) {
		if (arg2.matches(NUM_PATTER))
			return true;
		else
			return false;
	}


}

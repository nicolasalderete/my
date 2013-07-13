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
@FacesValidator(value = "NumericValidator")
public class NumericValidator implements Validator {

	private static final String NUM_PATTER = "[0-9]*";

	@Override
	public void validate(FacesContext arg0, UIComponent arg1, Object arg2) throws ValidatorException {
		if (!isNumber(arg2.toString())) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Número invalido ", arg2.toString());
			throw new ValidatorException(msg);
		}
	}

	/**
	 * @param arg2
	 * @return
	 */
	private boolean isNumber(String arg2) {
		if (arg2.matches(NUM_PATTER))
			return true;
		else
			return false;
	}

}

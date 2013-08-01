package ar.com.tecsat.loans.validators;

import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.google.common.base.Strings;

/**
 * @author nicolas
 *
 */
@FacesValidator(value = "DniValidator")
public class DniValidator implements Validator {
	
	@Override
	public void validate(FacesContext contexto, UIComponent component, Object value) throws ValidatorException {
		checkIsNullOrValid(component, value);
	}
	
	/**
	 * @param component
	 * @param value
	 */
	private void checkIsValid(UIComponent component, Object value) {
		Pattern p = Pattern.compile("[^0-9]");
		if (p.matcher(value.toString()).find()) {
			throw new ValidatorException(getMessage(component, value, "%s debe tener solo números."));
		}
	}

	/**
	 * @param component
	 * @param value
	 */
	private void checkIsNullOrValid(UIComponent component, Object value) {
		if (checkIsNull(value)) {
			if (isRequired(component)) {
				throw new ValidatorException(getMessage(component, "", "%s es obligatorio."));
			}
		} else {
			checkIsValid(component, value);
		}
	}

	/**
	 * @param component
	 * @return
	 */
	private boolean isRequired(UIComponent component) {
		if (component.getAttributes().containsKey("requerido")) {
			return component.getAttributes().get("requerido").equals("true");
		} else {
			return false;
		}
	}
	
	/**
	 * @param value
	 * @return
	 */
	private boolean checkIsNull(Object value) {
		if (value != null) {
			return Strings.isNullOrEmpty(value.toString().trim());
		} else {
			return true;
		}
	}

	/**
	 * @param component
	 * @param value
	 * @param txt 
	 * @return
	 */
	private FacesMessage getMessage(UIComponent component, Object value, String txt) {
		String message = String.format(txt, component.getAttributes().get("label"));
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, value.toString());
		return msg;
	}
}

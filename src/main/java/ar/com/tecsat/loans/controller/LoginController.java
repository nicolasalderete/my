package ar.com.tecsat.loans.controller;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import ar.com.tecsat.loans.bean.OperadorBean;
import ar.com.tecsat.loans.exceptions.AdministrativeException;
import ar.com.tecsat.loans.service.LoginService;

/**
 * @author nicolas
 * 
 */
@SuppressWarnings("serial")
@Named
@RequestScoped
public class LoginController extends BasicController implements Serializable {

	@Inject
	private OperadorBean operadorBean;

	@EJB
	private LoginService loginService;
	
	private String user;

	private String pass;

	public String login() {
		try {
			operadorBean = loginService.validarUsuario(getUser(), getPass());
		} catch (AdministrativeException e) {
			addMessageError(e.getMessage());
			return FAILURE;
		}
		addParameterSessionMap(operadorBean, "operadorBean");
		addMessageInfo("Bienvenido!!");
		return HOME;
	}

	public String logOut() {
		loginService.invalidate();
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
		return LOGOUT;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
}

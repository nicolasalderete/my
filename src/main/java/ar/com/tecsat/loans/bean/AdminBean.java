package ar.com.tecsat.loans.bean;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import ar.com.tecsat.loans.controller.BasicController;

/**
 * @author nicolas
 *
 */
@Named
@SessionScoped
public class AdminBean extends BasicController implements Serializable {

	private static final long serialVersionUID = -3523412053530317923L;
	
	public String init() {
		return LIST;
	}

	
}

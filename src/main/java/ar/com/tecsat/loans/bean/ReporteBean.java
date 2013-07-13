package ar.com.tecsat.loans.bean;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import ar.com.tecsat.loans.controller.BasicController;

/**
 * @author nicolas
 *
 */
@SuppressWarnings("serial")
@Named
@SessionScoped
public class ReporteBean extends BasicController implements Serializable{

	public String init() {
		return LIST;
	}
}

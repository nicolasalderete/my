package ar.com.tecsat.loans.bean;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 * @author nicolas
 *
 */
@SuppressWarnings("serial")
@Named
@SessionScoped
public class OperadorBean implements Serializable {

	private String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}

package ar.com.tecsat.loans.util;

import java.util.ResourceBundle;

/**
 * @author nicolas
 *
 */
public class PrestamoConfiguration {
	
	private static PrestamoConfiguration INSTANCE = new PrestamoConfiguration();
	
	private ResourceBundle properties = ResourceBundle.getBundle("prestamos");
	
	public static PrestamoConfiguration getInstance() {
		return INSTANCE;
	}

	public ResourceBundle getProp() {
		return properties;
	}

	public void setProp(ResourceBundle prop) {
		this.properties = prop;
	}
	
	public String getPropertie(String key) {
		return this.properties.getString(key);
	}
	
}

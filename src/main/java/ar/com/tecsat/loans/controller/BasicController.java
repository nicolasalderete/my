package ar.com.tecsat.loans.controller;

import java.io.InputStream;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

public class BasicController  {

	public static final String SUCCESS = "success";
	public static final String FAILURE = "failure";
	public static final String HOME = "home";
	public static final String PERFIL = "perfil";
	
	public static final String LOGIN = "login";
	public static final String LOGOUT = "logout";
	
	public static final String FILTER = "filter";
	public static final String LIST = "list";
	public static final String CREATE = "create";
	public static final String SUMMARY = "summary";
	public static final String CONFIRM = "confirm";
	public static final String CANCELAR = "cancelar";
	public static final String PAGO = "pago";
	
	public void addMessageInfo(String message) {
		this.addMessage(FacesMessage.SEVERITY_INFO, message);
	}
	
	public void addMessageError(String message) {
		this.addMessage(FacesMessage.SEVERITY_ERROR, message);
	}
	
	public void addMessageWarn(String message) {
		this.addMessage(FacesMessage.SEVERITY_WARN, message);
	}
	
	private void addMessage(javax.faces.application.FacesMessage.Severity severity, String message) {
		FacesMessage facesMessage = new FacesMessage(message);
		facesMessage.setSeverity(severity);
		FacesContext.getCurrentInstance().addMessage(null, facesMessage);
	}

	public void addParameterSessionMap(Object obj, String key) {
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(key, obj);
	}
	
	public String getRequestParameterMap(String key){
		Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		return map.get(key);
	}

	public String forwardAction() {
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		return params.get("forward");
	}
	
	public String getCurrentView(){
		String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
		String result = viewId.substring(viewId.lastIndexOf("/") + 1, viewId.indexOf("."));
		return result;
	}

	protected InputStream getFile(ExternalContext externalContext, String path) {
		return externalContext.getResourceAsStream(path);
	}
}

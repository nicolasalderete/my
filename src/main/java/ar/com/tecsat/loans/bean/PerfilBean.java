package ar.com.tecsat.loans.bean;

import java.io.Serializable;
import java.util.Stack;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import ar.com.tecsat.loans.controller.BasicController;
import ar.com.tecsat.loans.exceptions.AdministrativeException;
import ar.com.tecsat.loans.modelo.Perfil;
import ar.com.tecsat.loans.service.PerfilService;

/**
 * @author nicolas
 *
 */
@SuppressWarnings("serial")
@Named
@SessionScoped
public class PerfilBean extends BasicController implements Serializable {

	private Perfil perfil;
	private boolean editPerfil = false;
	private Stack<String> STEP = new Stack<String>();
	
	@EJB
	private PerfilService perfilService;

	// Actions
	public String init() {
		Perfil per = null;
		try {
			per = perfilService.findPerfil();
		} catch (AdministrativeException e) {
			addMessageError(e.getMessage());
			return null;
		}
		if (per == null) {
			inicializarForm();
			addMessageWarn("Aun no ha cargado su perfil");
			return CREATE;
		}
		setPerfil(per);
		return SUMMARY;
	}
	
	private void inicializarForm() {
		setPerfil(new Perfil());
		setEditPerfil(false);
	}

	public String modify() {
		saveStep();
		setEditPerfil(true);
		return SUMMARY;
	}
	
	public String save() {
		saveStep();
		addMessageInfo("Para finalizar la operación presione confirmar");
		return CONFIRM;
	}
	
	public String confirm() {
		try {
			perfilService.guardarPerfil(getPerfil());
			setPerfil(perfilService.findPerfil());
		} catch (AdministrativeException e) {
			addMessageError(e.getMessage());
			return null;
		}
		STEP.clear();
		addMessageInfo("Operación realizada");
		return SUMMARY;
	}
	
	public String update() {
		try {
			perfilService.actualizarPerfil(getPerfil());
			setPerfil(perfilService.findPerfil());
		} catch (AdministrativeException e) {
			addMessageError(e.getMessage());
			return null;
		}
		addMessageInfo("Operación realizada");
		return stepBack();
	}
	
	public String cancel() {
		return stepBack();
	}
	
	public String stepBack() {
		setEditPerfil(false);
		return STEP.pop();
	}
	
	public void saveStep() {
		STEP.push(getCurrentView());
	}
	
	public Perfil getPerfil() {
		return perfil;
	}
	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}
	public boolean isEditPerfil() {
		return editPerfil;
	}
	public void setEditPerfil(boolean editPerfil) {
		this.editPerfil = editPerfil;
	}
	public Stack<String> getSTEP() {
		return STEP;
	}
	public void setSTEP(Stack<String> sTEP) {
		STEP = sTEP;
	}
	
	
}

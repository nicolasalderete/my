package ar.com.tecsat.loans.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import ar.com.tecsat.loans.bean.utils.PrestamoFiltro;
import ar.com.tecsat.loans.exceptions.AdministrativeException;
import ar.com.tecsat.loans.modelo.Cliente;
import ar.com.tecsat.loans.modelo.Cuota;
import ar.com.tecsat.loans.modelo.Prestamo;
import ar.com.tecsat.loans.modelo.TipoPrestamo;
import ar.com.tecsat.loans.pdf.AbstractReportBean;
import ar.com.tecsat.loans.service.ClienteService;
import ar.com.tecsat.loans.service.CuotaService;
import ar.com.tecsat.loans.service.PrestamoService;

/**
 * @author nicolas
 * 
 */
@SuppressWarnings("serial")
@Named
@SessionScoped
public class PrestamoBean extends AbstractReportBean implements Serializable {

	// Propiedades
	private Prestamo prestamo;
	private List<Prestamo> listaPrestamo;
	private List<Cliente> listaCliente;

	private PrestamoFiltro filtro = new PrestamoFiltro();
	private boolean editPrestamo = false;
	private Stack<String> STEP = new Stack<String>();

	@EJB
	private PrestamoService prestamoService;

	@EJB
	private ClienteService clienteService;
	
	@EJB
	private CuotaService cuotaService;

	// Actions
	public String init() {
		try {
			inicializarForm();
		} catch (AdministrativeException e) {
			addMessageError(e.getMessage());
			return null;
		}
		cleanStack();
		return FILTER;
	}

	public TipoPrestamo[] getTipoPrestamo() {
		return TipoPrestamo.values();
	}

	private void cleanStack() {
		STEP.clear();
	}

	public String buscar() {
		List<Prestamo> prestamos = null;
		try {
			prestamos = prestamoService.findByFilter(getFiltro());
		} catch (AdministrativeException e) {
			addMessageError(e.getMessage());
			return null;
		}
		if (prestamos == null || prestamos.isEmpty()) {
			addMessageWarn("No hay registros");
			return null;
		}
		saveStep();
		this.listaPrestamo = prestamos;
		return LIST;
	}

	public String stepBack() {
		String result = null;
		if (STEP.size() == 1) {
			result = STEP.get(0);
		} else {
			result = STEP.pop();
		}
		if (result.equals(FILTER))
			try {
				inicializarForm();
			} catch (AdministrativeException e) {
				addMessageError(e.getMessage());
				return null;
			}
		if (result.equals(SUMMARY))
			this.editPrestamo = false;
		return result;
	}

	public String filter() {
		try {
			inicializarForm();
		} catch (AdministrativeException e) {
			addMessageError(e.getMessage());
			return null;
		}
		return FILTER;
	}

	public String create() {
		try {
			inicializarForm();
		} catch (AdministrativeException e) {
			addMessageError(e.getMessage());
			return null;
		}
		saveStep();
		return CREATE;
	}

	public String cancel() {
		if (!getCurrentView().equals(SUMMARY)) {
			try {
				inicializarForm();
			} catch (AdministrativeException e) {
				addMessageError(e.getMessage());
				return null;
			}
		}
		return stepBack();
	}

	public String saveRefinanciar() {
		getFiltro().setPrestamo(getPrestamo());
		return save();
	}

	public String save() {
		Prestamo nuevoPrestamo = null;
		try {
			nuevoPrestamo = prestamoService.crearPrestamo(filtro);
		} catch (AdministrativeException e) {
			addMessageError(e.getMessage());
			return null;
		}
		if (nuevoPrestamo == null) {
			addMessageError("Error en el servicio");
			return null;
		}
		setPrestamo(nuevoPrestamo);
		saveStep();
		addMessageInfo("Verifique los datos ingresado y presione confirmar para finalizar con la operación");
		setEditPrestamo(false);
		return CONFIRM;
	}

	public String confirm() {
		try {
			prestamoService.guardarPrestamo(getPrestamo());
			if (getFiltro().getPrestamo() != null) {
				prestamoService.cancelarPrestamo(getFiltro().getPrestamo());
			}
			inicializarForm();
		} catch (AdministrativeException e) {
			addMessageError(e.getMessage());
			return null;
		}
		addMessageInfo("Operación realizada");
		return init();
	}

	public String detalle(Prestamo pre) {
		this.prestamo = pre;
		saveStep();
		return SUMMARY;
	}
	private void saveStep() {
		STEP.push(getCurrentView());
	}

	private void inicializarForm() throws AdministrativeException {
		List<Cliente> clientes = clienteService.findClientes();
		setListaCliente(clientes);
		this.filtro = new PrestamoFiltro();
		this.prestamo = new Prestamo();
		setEditPrestamo(false);
	}

	public Prestamo getPrestamo() {
		return prestamo;
	}

	public void setPrestamo(Prestamo prestamo) {
		this.prestamo = prestamo;
	}

	public List<Prestamo> getListaPrestamo() {
		return listaPrestamo;
	}

	public void setListaPrestamo(List<Prestamo> listaPrestamo) {
		this.listaPrestamo = listaPrestamo;
	}

	public List<Cliente> getListaCliente() {
		return listaCliente;
	}

	public void setListaCliente(List<Cliente> listaCliente) {
		this.listaCliente = listaCliente;
	}

	public boolean isEditPrestamo() {
		return editPrestamo;
	}

	public void setEditPrestamo(boolean editPrestamo) {
		this.editPrestamo = editPrestamo;
	}

	public PrestamoFiltro getFiltro() {
		return filtro;
	}

	public void setFiltro(PrestamoFiltro filtro) {
		this.filtro = filtro;
	}

	public Stack<String> getSTEP() {
		return STEP;
	}

	public void setSTEP(Stack<String> sTEP) {
		STEP = sTEP;
	}
	
	private Collection<Cuota> getDatasource(Prestamo prestamo) {
		Collection<Cuota> lista = new ArrayList<Cuota>();
		try {
			lista = cuotaService.findCuotasByPrestamo(prestamo);
		} catch (AdministrativeException e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	public String exportPdf() throws IOException {
		Collection<Cuota> cuotas = getDatasource(prestamo);
		try {
			super.prepareReport(prestamo, cuotas);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
	}

	@Override
	protected String getCompileFileName() {
		return "reportprestamo";
	}

}

package ar.com.tecsat.loans.util;

import ar.com.tecsat.loans.bean.utils.CuotaFiltro;
import ar.com.tecsat.loans.modelo.Cuota;
import ar.com.tecsat.loans.modelo.Pago;
import ar.com.tecsat.loans.modelo.PagoEstado;

/**
 * @author nicolas
 *
 */
public class PagoHelper {
	
	private static PagoHelper INSTANCE = null;
	
	public static PagoHelper getInstance() {
		if (INSTANCE == null)
			return new PagoHelper();
		return INSTANCE;
	}
	
	public Pago crearPago(Cuota cuota, CuotaFiltro filtro) {
		Pago newPago = new Pago();
		newPago.setCliente(cuota.getPrestamo().getCliente());
		newPago.setCuota(cuota);
		newPago.setPagFestado(filtro.getFechaPago());
		newPago.setPagMonto(filtro.getImportePago());
		newPago.setPrestamo(cuota.getPrestamo());
		newPago.setPagEstado(PagoEstado.PAGO_CANCELADO.toString());
		return newPago ;
	}
}

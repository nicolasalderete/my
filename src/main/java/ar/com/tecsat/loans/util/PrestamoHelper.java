package ar.com.tecsat.loans.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ar.com.tecsat.loans.bean.CuotaEstado;
import ar.com.tecsat.loans.bean.utils.PrestamoFiltro;
import ar.com.tecsat.loans.modelo.Cliente;
import ar.com.tecsat.loans.modelo.Cuota;
import ar.com.tecsat.loans.modelo.Prestamo;

/**
 * @author nicolas
 * 
 */
public class PrestamoHelper {

	private static PrestamoHelper INSTANCE = null;

	public static PrestamoHelper getInstance() {
		if (INSTANCE == null)
			return new PrestamoHelper();
		return INSTANCE;
	}

	public static Prestamo createPrestamo(PrestamoFiltro filtro, Cliente cliente) {

		Prestamo nuevoPrestamo = new Prestamo();
		nuevoPrestamo.setCliente(cliente);
		nuevoPrestamo.setPreCantCuotas(filtro.getCantCuotas());
		nuevoPrestamo.setPreCapital(filtro.getCapital());
		nuevoPrestamo.setPreTasaMensual(filtro.getTasa());
		nuevoPrestamo.setPreFechaInicio(filtro.getFechaSoli());
		nuevoPrestamo.setPreImporteCuota(getImporteDeLaCuota(filtro.getCantCuotas(), filtro.getCapital(), filtro.getTasa()));
		nuevoPrestamo.setPreInteresMensual(getInteresMensual(filtro.getCapital(), filtro.getTasa()));
		List<Cuota> cuotas = getCuotas(nuevoPrestamo);
		nuevoPrestamo.setCuotas(cuotas);
		return nuevoPrestamo;
	}

	/**
	 * @param nuevoPrestamo
	 * @return
	 */
	private static List<Cuota> getCuotas(Prestamo nuevoPrestamo) {
		List<Cuota> cuotas = new ArrayList<Cuota>();
		int cuotaNumero = 1;
		for (int i = 0; i < nuevoPrestamo.getPreCantCuotas(); i++) {
			Cuota cuota = new Cuota();
			cuota.setCuoImporte(nuevoPrestamo.getPreImporteCuota());
			cuota.setCuoSaldoDeudor(new BigDecimal(0));
			cuota.setCuoSaldoFavor(new BigDecimal(0));
			cuota.setCuoNumero(cuotaNumero);
			cuota.setCuoFechaVencimiento(getFechaVencimiento(nuevoPrestamo.getPreFechaInicio(), cuotaNumero));
			cuota.setPrestamo(nuevoPrestamo);
			cuota.setCuoInteres(nuevoPrestamo.getPreInteresMensual());
			cuota.setCuoInteresPunitorio(new BigDecimal(0));
			cuota.setCuoEstado(CuotaEstado.VIGENTE.toString());
			cuota.setCuoTasaMensual(nuevoPrestamo.getPreTasaMensual());
			cuota.setCuoSaldo(nuevoPrestamo.getPreImporteCuota());

			BigDecimal montoTotal = cuota.getCuoImporte().add(cuota.getCuoSaldoFavor().subtract(cuota.getCuoSaldoDeudor()))
					.add(cuota.getCuoInteresPunitorio());
			cuota.setCuoTotalPagar(montoTotal);
			cuotas.add(cuota);
			cuotaNumero++;
		}
		return cuotas;
	}

	/**
	 * @param preFinicio
	 * @param numeroCuota
	 * @return
	 */
	private static Date getFechaVencimiento(Date preFinicio, int numeroCuota) {
		Calendar fechaVto = Calendar.getInstance();
		fechaVto.setTime(preFinicio);
		fechaVto.add(Calendar.MONTH, numeroCuota);
		return fechaVto.getTime();
	}

	/**
	 * Retorna el importe de la cuota resultante del capital prestado
	 * la cantidad de cuotas y la tasa mensual
	 * 
	 * @param cantidadCuotas
	 * @param capital
	 * @param tasaMensual
	 * @return
	 */
	private static BigDecimal getImporteDeLaCuota(Integer cantidadCuotas, BigDecimal capital, Double tasaMensual) {
		BigDecimal interesesMensual = getInteresMensual(capital, tasaMensual);
		return redondearImporteCuota(cantidadCuotas, capital, interesesMensual);
	}

	/**
	 * @param cantidadCuotas
	 * @param capital
	 * @param interesesMensual
	 * @return
	 */
	private static BigDecimal redondearImporteCuota(Integer cantidadCuotas, BigDecimal capital,
			BigDecimal interesesMensual) {
		BigDecimal importeSinRedondeo = capital.divide(BigDecimal.valueOf(Long.valueOf(cantidadCuotas)), 0, RoundingMode.HALF_UP).add(interesesMensual);
		return importeSinRedondeo.movePointLeft(1).setScale(0, RoundingMode.HALF_UP).movePointRight(1);
	}

	/**
	 * Dado un capital y una tasa mensual, retorna el interes mensual que se
	 * adhiere al importe de cada una de las cuotas
	 * 
	 * @param capital
	 * @param tasaMensual
	 * @return
	 */
	private static BigDecimal getInteresMensual(BigDecimal capital, Double tasaMensual) {
		return capital.multiply(new BigDecimal(tasaMensual)).divide(new BigDecimal(100), 0, RoundingMode.HALF_UP);
	}
}

package ar.com.tecsat.loans.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

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
		nuevoPrestamo.setTipoPrestamo(filtro.getTipoPrestamo());

		int cantidadDeCuotas = calcularCantidadDeCuotas(filtro);
		nuevoPrestamo.setPreCantCuotas(cantidadDeCuotas);
		nuevoPrestamo.setPreCantMeses(filtro.getCantMeses());

		nuevoPrestamo.setPreCapital(filtro.getCapital());
		nuevoPrestamo.setPreTasaMensual(filtro.getTasa());
		nuevoPrestamo.setPreFechaInicio(filtro.getFechaSoli());

		// BigDecimal interesTotal = calcularInteresTotal(filtro);
		nuevoPrestamo.setPreInteresTotal(filtro.getInteresTotal());

		List<Cuota> cuotas = getCuotas(filtro.getCapital(), filtro.getInteresTotal(), cantidadDeCuotas, nuevoPrestamo);
		nuevoPrestamo.setCuotas(cuotas);
		return nuevoPrestamo;
	}

	private static BigDecimal calcularImporteDeLaCuota(int cantidadDeCuotas, BigDecimal interesTotal,
			BigDecimal capital) {
		BigDecimal totalPrestamo = interesTotal.add(capital);
		BigDecimal importeCuota = totalPrestamo.divide(BigDecimal.valueOf(Long.valueOf(cantidadDeCuotas)), 0, RoundingMode.HALF_UP);
		return importeCuota;
	}

//	private static BigDecimal calcularInteresTotal(PrestamoFiltro filtro) {
//		BigDecimal ctf = new BigDecimal(filtro.getTasa()).multiply(new BigDecimal(filtro.getCantMeses()));
//		return filtro.getCapital().multiply(ctf).divide(new BigDecimal(100));
//	}

	private static int calcularCantidadDeCuotas(PrestamoFiltro filtro) {
		return filtro.getTipoPrestamo().getValue() * filtro.getCantMeses();
	}

	/**
	 * @param cantidadDeCuotas 
	 * @param interesTotal 
	 * @param capital 
	 * @param nuevoPrestamo
	 * @return
	 */
	private static List<Cuota> getCuotas(BigDecimal capital, BigDecimal interesTotal, int cantidadDeCuotas, Prestamo nuevoPrestamo) {
		List<Cuota> cuotas = new ArrayList<Cuota>();
		int cuotaNumero = 1;
		for (int i = 0; i < nuevoPrestamo.getPreCantCuotas(); i++) {
			Cuota cuota = new Cuota();
			
			BigDecimal importeCuota = calcularImporteDeLaCuota(cantidadDeCuotas, interesTotal, capital);
			cuota.setCuoImporte(importeCuota);
			cuota.setCuoSaldoDeudor(new BigDecimal(0));
			cuota.setCuoSaldoFavor(new BigDecimal(0));
			cuota.setCuoNumero(cuotaNumero);
			cuota.setCuoFechaVencimiento(getFechaVencimiento(nuevoPrestamo, cuotaNumero));
			cuota.setPrestamo(nuevoPrestamo);
			cuota.setCuoInteresPunitorio(new BigDecimal(0));
			cuota.setCuoEstado(CuotaEstado.VIGENTE.toString());
			cuota.setCuoTasaMensual(nuevoPrestamo.getPreTasaMensual());
			cuota.setCuoSaldo(importeCuota);

			BigDecimal montoTotal = cuota.getCuoImporte()
					.add(cuota.getCuoSaldoFavor().subtract(cuota.getCuoSaldoDeudor()))
					.add(cuota.getCuoInteresPunitorio());
			cuota.setCuoTotalPagar(montoTotal);
			cuotas.add(cuota);
			cuotaNumero++;
		}
		return cuotas;
	}

	/**
	 * @param nuevoPrestamo
	 * @param numeroCuota
	 * @return
	 */
	private static Date getFechaVencimiento(Prestamo nuevoPrestamo, int numeroCuota) {
		Date result = null;
		switch (nuevoPrestamo.getTipoPrestamo()) {
			case DIARIO:
				result = calcularFechaVtoDiario(nuevoPrestamo, numeroCuota);
				break;
			case MENSUAL:
				result = calcularFechaVtoMensual(nuevoPrestamo, numeroCuota);
				break;
			case SEMANAL :
				result = calcularFechaVtoSemanal(nuevoPrestamo, numeroCuota);
				break;
		}
		return result;
	}

	private static Date calcularFechaVtoSemanal(Prestamo nuevoPrestamo, int numeroCuota) {
		DateTime dt = new DateTime(nuevoPrestamo.getPreFechaInicio());
		DateTime plusWeeks = dt.plusWeeks(numeroCuota);
		return plusWeeks.toDate();
	}

	private static Date calcularFechaVtoMensual(Prestamo nuevoPrestamo, int numeroCuota) {
		DateTime dt = new DateTime(nuevoPrestamo.getPreFechaInicio());
		DateTime plusMonths = dt.plusMonths(numeroCuota);
		return plusMonths.toDate();
	}

	private static Date calcularFechaVtoDiario(Prestamo nuevoPrestamo, int numeroCuota) {
		DateTime dt = new DateTime(nuevoPrestamo.getPreFechaInicio());
		DateTime plusDays = dt.plusDays(numeroCuota);
		return plusDays.toDate();
	}

	// /**
	// * Retorna el importe de la cuota resultante del capital prestado la
	// * cantidad de cuotas y la tasa mensual
	// *
	// * @param cantidadCuotas
	// * @param capital
	// * @param tasaMensual
	// * @return
	// */
	// private static BigDecimal getImporteDeLaCuota(Integer cantidadCuotas,
	// BigDecimal capital, Double tasaMensual) {
	// BigDecimal interesesMensual = getInteresMensual(capital, tasaMensual);
	// return redondearImporteCuota(cantidadCuotas, capital, interesesMensual);
	// }
	//
	// /**
	// * @param cantidadCuotas
	// * @param capital
	// * @param interesesMensual
	// * @return
	// */
	// private static BigDecimal redondearImporteCuota(Integer cantidadCuotas,
	// BigDecimal capital,
	// BigDecimal interesesMensual) {
	// BigDecimal importeSinRedondeo =
	// capital.divide(BigDecimal.valueOf(Long.valueOf(cantidadCuotas)), 0,
	// RoundingMode.HALF_UP).add(interesesMensual);
	// return importeSinRedondeo.movePointLeft(1).setScale(0,
	// RoundingMode.HALF_UP).movePointRight(1);
	// }
	//
	// /**
	// * Dado un capital y una tasa mensual, retorna el interes mensual que se
	// * adhiere al importe de cada una de las cuotas
	// *
	// * @param capital
	// * @param tasaMensual
	// * @return
	// */
	// private static BigDecimal getInteresMensual(BigDecimal capital, Double
	// tasaMensual) {
	// return capital.multiply(new BigDecimal(tasaMensual)).divide(new
	// BigDecimal(100), 0, RoundingMode.HALF_UP);
	// }
}

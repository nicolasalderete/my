package ar.com.tecsat.loans.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import ar.com.tecsat.loans.bean.CuotaEstado;
import ar.com.tecsat.loans.bean.utils.PrestamoFiltro;
import ar.com.tecsat.loans.exceptions.AdministrativeException;
import ar.com.tecsat.loans.modelo.Cliente;
import ar.com.tecsat.loans.modelo.Cuota;
import ar.com.tecsat.loans.modelo.Prestamo;
import ar.com.tecsat.loans.modelo.TipoPrestamo;

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

	public static Prestamo createPrestamo(PrestamoFiltro filtro, Cliente cliente) throws AdministrativeException {
		Prestamo nuevoPrestamo = new Prestamo();
		nuevoPrestamo.setCliente(cliente);
		nuevoPrestamo.setTipoPrestamo(filtro.getTipoPrestamo());
		nuevoPrestamo.setPreCantCuotas(filtro.getCantCuotas());
		nuevoPrestamo.setPreCapital(filtro.getCapital());
		nuevoPrestamo.setPreTasa(filtro.getTasa());
		nuevoPrestamo.setPreFechaInicio(filtro.getFechaSoli());
		nuevoPrestamo
				.setPreCuotaPura(calcularCuotaPura(nuevoPrestamo.getPreCapital(), nuevoPrestamo.getPreCantCuotas()));

		BigDecimal interesTotal = calcularInteresTotal(filtro);

		nuevoPrestamo.setPreInteresTotal(interesTotal);
		nuevoPrestamo.setPreMontoTotal(filtro.getCapital().add(interesTotal));

		List<Cuota> cuotas = getCuotas(nuevoPrestamo);
		nuevoPrestamo.setCuotas(cuotas);
		return nuevoPrestamo;
	}

	private static BigDecimal calcularCuotaPura(BigDecimal capital, Integer cantCuotas) {
		return capital.divide(BigDecimal.valueOf(cantCuotas), 0, RoundingMode.HALF_UP);
	}

	private static BigDecimal calcularInteresTotal(PrestamoFiltro filtro) {
		Double tasa = filtro.getTasa();
		return filtro.getCapital().multiply(BigDecimal.valueOf(tasa))
				.divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
	}

	private static BigDecimal calcularImporteDeLaCuota(int cantidadDeCuotas, BigDecimal interesTotal, BigDecimal capital) {
		BigDecimal totalPrestamo = interesTotal.add(capital);
		return totalPrestamo.divide(BigDecimal.valueOf(Long.valueOf(cantidadDeCuotas)), 0, RoundingMode.HALF_UP);
	}

	/**
	 * @param nuevoPrestamo
	 * @return
	 * @throws AdministrativeException
	 */
	private static List<Cuota> getCuotas(Prestamo nuevoPrestamo) throws AdministrativeException {
		List<Cuota> cuotas = new ArrayList<Cuota>();
		Integer cantCuotas = nuevoPrestamo.getPreCantCuotas();
		BigDecimal interesTotal = nuevoPrestamo.getPreInteresTotal();
		BigDecimal capital = nuevoPrestamo.getPreCapital();
		Date lastVto = nuevoPrestamo.getPreFechaInicio();
		for (int cuoNumero = 1; cuoNumero <= cantCuotas; cuoNumero++) {
			Cuota cuota = new Cuota();
			cuota.setCuoInteres(interesTotal.divide(new BigDecimal(cantCuotas.toString()), 0, RoundingMode.HALF_UP));
			BigDecimal importeCuota = calcularImporteDeLaCuota(cantCuotas, interesTotal, capital);
			cuota.setCuoImporte(importeCuota);
			cuota.setCuoPura(calcularCuotaPura(capital, cantCuotas));
			cuota.setCuoPagoParcial(new BigDecimal(0));
			cuota.setCuoNumero(cuoNumero);
			lastVto = calcularFechaVencimiento(nuevoPrestamo, cuoNumero, lastVto);
			cuota.setCuoFechaVencimiento(lastVto);
			cuota.setCuoInteresPunitorio(new BigDecimal(0));
			cuota.setCuoEstado(CuotaEstado.VIGENTE);
			cuota.setCuoSaldo(importeCuota);

			cuota.setPrestamo(nuevoPrestamo);
			cuotas.add(cuota);
		}
		return cuotas;
	}

	/**
	 * @param nuevoPrestamo
	 * @param numeroCuota
	 * @param lastVto
	 * @return
	 * @throws AdministrativeException
	 */
	private static Date calcularFechaVencimiento(Prestamo nuevoPrestamo, int numeroCuota, Date lastVto)
			throws AdministrativeException {
		DateTime plus = new DateTime(lastVto).withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59);
		DateTime day = null;
		if (isSunday(plus)) {
			day = plus.plusDays(1);
		} else {
			day = plus;
		}
		DateTime result = null;
		switch (nuevoPrestamo.getTipoPrestamo()) {
		case DIARIO:
			result = day.plusDays(TipoPrestamo.DIARIO.getValue());
			break;
		case MENSUAL:
			result = day.plusDays(TipoPrestamo.MENSUAL.getValue());
			break;
		case SEMANAL:
			result = day.plusDays(TipoPrestamo.SEMANAL.getValue());
			break;
		case QUINCENAL:
			result = day.plusDays(TipoPrestamo.QUINCENAL.getValue());
			break;
		default:
			throw new AdministrativeException("Error al calcular la fecha de vencimiento");
		}
		if (isSunday(result)) {
			return result.plusDays(1).toDate();
		} else {
			return result.toDate();
		}
	}

	private static boolean isSunday(DateTime result) {
		return result.getDayOfWeek() >= DateTimeConstants.SUNDAY;
	}
}

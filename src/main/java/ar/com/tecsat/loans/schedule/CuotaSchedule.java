package ar.com.tecsat.loans.schedule;

import static ar.com.tecsat.loans.bean.utils.Dias.QUINCE;
import static ar.com.tecsat.loans.bean.utils.Dias.TREINTA;

import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.Schedule;
import javax.ejb.Schedules;
import javax.ejb.Singleton;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import ar.com.tecsat.loans.modelo.Cuota;
import ar.com.tecsat.loans.service.CuotaService;

/**
 * @author nicolas
 * 
 */

@Singleton
@Lock
public class CuotaSchedule {

	@Resource(mappedName = "java:jboss/mail/Default")
	private Session mailSession;

	@EJB
	private CuotaService cuotaService;

	@SuppressWarnings("unused")
	@Schedules({ @Schedule(month = "*", dayOfMonth = "*", hour = "0", minute = "30") })
	private void helloWorld() throws MessagingException {
		System.out.println("Iniciando Scheduler..............................");
		StringBuffer content = new StringBuffer("");

		actualizacionDeCuotasQueVenceHoy(content);
		actualizacionDeCuotasVencidasMasQuinceDias(content);
		actualizacionDeCuotasVencidasMasTreintaDias(content);

		MimeMessage m = new MimeMessage(mailSession);
		Address from = new InternetAddress("localhost.com");
		Address[] to = new InternetAddress[] { new InternetAddress("nicolasalderete@gmail.com") };
		m.setFrom(from);
		m.setRecipients(Message.RecipientType.TO, to);
		m.setSubject("Actualizando cuotas");
		m.setSentDate(Calendar.getInstance().getTime());
		m.setContent(content, "text/plain");
		Transport.send(m);

		System.out.println("Finalizando Scheduler..............................");
	}

	/**
	 * @param content
	 */
	private void actualizacionDeCuotasVencidasMasTreintaDias(StringBuffer content) {
		List<Cuota> cuotaVencidaTreintaDias = cuotaService.obtenerCuotasVencidasA(TREINTA);
		content.append("Lista de cuotas vencidas con mas de treinta días.\n" +
				"-------------------------------------------------\n");
		List<String> result = cuotaService.actualizarCuotasVencidasMasInteres(cuotaVencidaTreintaDias, TREINTA);
		setContentListado(content, result);
	}

	/**
	 * @param content
	 */
	private void actualizacionDeCuotasVencidasMasQuinceDias(StringBuffer content) {
		List<Cuota> cuotaVencidaQuinceDias = cuotaService.obtenerCuotasVencidasA(QUINCE);
		content.append("Lista de cuotas vencidas con mas de quince días.\n" +
				"-------------------------------------------------\n");
		List<String> result = cuotaService.actualizarCuotasVencidasMasInteres(cuotaVencidaQuinceDias, QUINCE);
		setContentListado(content, result);
	}

	/**
	 * @param content
	 */
	private void actualizacionDeCuotasQueVenceHoy(StringBuffer content) {
		List<Cuota> cuotasVencenHoy = cuotaService.obtenerCuotasFechaVtoHoy();
		content.append("Lista de cuotas que vencen hoy.\n" +
								"-------------------------------\n");
		List<String> result = cuotaService.actualizarCuotasVencidas(cuotasVencenHoy);
		setContentListado(content, result);
	}

	/**
	 * @param content
	 * @param cuotasVencenHoy
	 */
	private void setContentListado(StringBuffer content, List<String> messages) {
		for (String message : messages) {
			content.append(message);
		}
	}


}

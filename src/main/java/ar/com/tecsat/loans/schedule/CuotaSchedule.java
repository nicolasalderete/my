package ar.com.tecsat.loans.schedule;

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

import ar.com.tecsat.loans.exceptions.AdministrativeException;
import ar.com.tecsat.loans.modelo.Cuota;
import ar.com.tecsat.loans.service.CuotaService;

/**
 * @author nicolas
 *
 */

@Singleton
@Lock
public class CuotaSchedule {
	
	@Resource(mappedName="java:jboss/mail/Default")
	private Session mailSession;
	
	@EJB
	private CuotaService cuotaService;

	@SuppressWarnings("unused")
	@Schedules({
		@Schedule(month="*", dayOfMonth="*", hour="0", minute="10")
	})
	private void helloWorld() throws MessagingException {
		System.out.println("Iniciando Scheduler..............................");
		
		
		
		
		MimeMessage m = new MimeMessage(mailSession);
        Address from = new InternetAddress("localhost.com");
        Address[] to = new InternetAddress[] {new InternetAddress("nicolasalderete@gmail.com") };

        try {
			List<Cuota> cuotasVencidas = cuotaService.actualizarCuotasVencidas();
			List<Cuota> cuotasVencidasMasQuinceDias = cuotaService.actualizarCuotasVencidasMasQuinceDias();
			List<Cuota> cuotasVencidasMasTreintaDias = cuotaService.actualizarCuotasVencidasMasTreintaDias();
		} catch (AdministrativeException e) {
			System.out.println(e.getMessage());
		}
        m.setFrom(from);
        m.setRecipients(Message.RecipientType.TO, to);
        m.setSubject("Actualizando cuotas vencidas");
        m.setSentDate(Calendar.getInstance().getTime());
        m.setContent("Mail sent from JBoss AS 7","text/plain");
        Transport.send(m);
        
        System.out.println("Finalizando Scheduler..............................");
	}
	

}

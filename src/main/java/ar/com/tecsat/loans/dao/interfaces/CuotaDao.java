package ar.com.tecsat.loans.dao.interfaces;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import org.joda.time.DateTime;

import ar.com.tecsat.loans.bean.utils.CuotaFiltro;
import ar.com.tecsat.loans.exceptions.AdministrativeException;
import ar.com.tecsat.loans.modelo.Cuota;

/**
 * @author nicolas
 *
 */
@Local
public interface CuotaDao {
	
	public List<Cuota> findByFilter(CuotaFiltro filtro) throws AdministrativeException;

	public List<Cuota> findCuotas() throws AdministrativeException;

	public List<Cuota> findCuotasByPrestamo(Integer preId) throws AdministrativeException;

	public void actualizar(Cuota cuota) throws AdministrativeException;

	public List<Cuota> findByFecha(Date start, Date end) throws AdministrativeException;

	public List<Cuota> findByFechaVtoHoy() throws AdministrativeException;

	public List<Cuota> findByFechaVtoA(int dias) throws AdministrativeException;

	public Cuota findCuota(Cuota currentCuota);

	public List<Cuota> findByFechaVto(DateTime hoy);

	public void eliminarCuota(Cuota cuota);
	
}

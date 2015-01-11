package ar.com.tecsat.loans.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ar.com.tecsat.loans.modelo.Cliente;
import ar.com.tecsat.loans.modelo.Cuota;
import ar.com.tecsat.loans.modelo.Pago;
import ar.com.tecsat.loans.modelo.Prestamo;

public class OrderList {

	public static List<Cliente> sortClient(List<Cliente> clientes) {
		Collections.sort(clientes, new Comparator<Cliente>() {

			@Override
			public int compare(Cliente o1, Cliente o2) {
				return o1.getCliNombre().compareTo(o2.getCliNombre());
			}
		});
		return clientes;
	}

	public static List<Prestamo> sortPrestamos(List<Prestamo> listaPrestamos) {
		Collections.sort(listaPrestamos, new Comparator<Prestamo>() {

			@Override
			public int compare(Prestamo o1, Prestamo o2) {
				return o1.getCliente().getCliNombre().compareTo(o2.getCliente().getCliNombre());
			}
		});
		return listaPrestamos;
	}

	public static List<Cuota> sortCuotas(List<Cuota> cuotas) {
		Collections.sort(cuotas, new Comparator<Cuota>() {

			@Override
			public int compare(Cuota o1, Cuota o2) {
				return o1.getPrestamo().getCliente().getCliNombre().compareTo(o2.getPrestamo().getCliente().getCliNombre());
			}
		});
		return cuotas;
	}

	public static List<Pago> sortPagos(List<Pago> pagos) {
		Collections.sort(pagos, new Comparator<Pago>() {

			@Override
			public int compare(Pago o1, Pago o2) {
				return o1.getPagId().compareTo(o2.getPagId());
				// TODO comentado por el momento
//				return o1.getPrestamo().getCliente().getCliNombre().compareTo(o2.getPrestamo().getCliente().getCliNombre());
			}
		});
		return pagos;
	}
	
}

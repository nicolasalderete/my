package ar.com.tecsat.loans.util;

import java.util.Comparator;

import ar.com.tecsat.loans.modelo.Cuota;

/**
 * @author nicolas
 *
 */
public class CuotaComparator implements Comparator<Cuota>{
	
	private static final CuotaComparator INSTANCE = new CuotaComparator();
	
	public static CuotaComparator getInstance() {
		return INSTANCE;
	}

	@Override
	public int compare(Cuota o1, Cuota o2) {
		return 0;
	}

}

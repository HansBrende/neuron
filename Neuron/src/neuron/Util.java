package neuron;

public interface Util {
	
	public static final double R = 8.3144621; // gas constant in J/(mol*K)
	public static final double e = 1.6021766E-19; //elementary charge in Coulombs
	public static final double N_A = 6.022141E23; //Avogadro's constant in 1/mol
	public static final double F = e * N_A; //Faraday's constant in C/mol
	
	public static double c_m(double permittivity, double innerRadius, double outerRadius) {
		return 2 * Math.PI * permittivity / Math.log(outerRadius / innerRadius);
	}
	//Approximation
	public static double c_m_approx(double specificCapacitance, double avgRadius) {
		return specificCapacitance * 2 * Math.PI * avgRadius;
	}
	
	public static double capacitance(double c_m, double length) {
		return c_m * length;
	}
	
	public static double r_m(double membraneResistivity, double membraneWidth, double avgRadius) {
		return membraneResistivity * membraneWidth / (2 * Math.PI * avgRadius);
	}
	
	public static double r_m(double specificMembraneResistance, double avgRadius) {
		return specificMembraneResistance / (2 * Math.PI * avgRadius);
	}
	
	public static double membraneResistance(double r_m, double length) {
		return r_m / length;
	}
	
	public static double r_a(double cytoResistivity, double innerRadius) {
		return cytoResistivity / (Math.PI * innerRadius * innerRadius);
	}
	
	public static double cytoResistance(double r_a, double length) {
		return r_a * length;
	}

}

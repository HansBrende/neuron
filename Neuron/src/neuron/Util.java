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
	
	public static final double CYTOPLASMIC_RESISTIVITY = .354; // ohms * meters http://www.neuron.yale.edu/phpbb/viewtopic.php?f=15&t=422

	public static final double MEMBRANE_THICKNESS = 6.0E-9; //http://www.scholarpedia.org/article/Electrical_properties_of_cell_membranes

	public static final double MEMBRANE_SPECIFIC_CAPACITANCE = 0.009; //in F/m^2
	public static final double MEMBRANE_PERMITTIVITY = MEMBRANE_THICKNESS * MEMBRANE_SPECIFIC_CAPACITANCE;
	public static double myelinWidth(double axonRadius) {//in meters
		return Math.log(2*axonRadius/1E-6 + 1)*1E-6; 
		// approximation based on: http://rspb.royalsocietypublishing.org/content/135/880/323.abstract
	}
	
	public static final double DIELECTRIC_CONSTANT_SQUID_MYELIN = 8.5; // http://www.pnas.org/content/early/2009/02/12/0813110106.full.pdf
	
	public static final double E_0 = 8.854187817E-12; // permittivity of free space in farads per meter
	public static final double SQUID_MYELIN_PERMITTIVITY = DIELECTRIC_CONSTANT_SQUID_MYELIN * E_0;
	public static final double AXON_RADIUS = .5E-6; //http://en.wikipedia.org/wiki/Axon
	public static final double INITIAL_SEGMENT_LENGTH = 25E-6; //http://en.wikipedia.org/wiki/Axon
	public static final double SHEATH_GAP = 1E-6; //http://en.wikipedia.org/wiki/Myelin_sheath_gap
	public static final double SHEATH_LENGTH = 1E-3; //http://en.wikipedia.org/wiki/Myelin_sheath_gap
	public static final double SHEATH_WIDTH = myelinWidth(AXON_RADIUS);
	
	//http://synapses.clm.utexas.edu/anatomy/dendrite/tables/table1.stm
	public static final double DENDRITE_LENGTH = 1000E-6; //http://synapses.clm.utexas.edu/anatomy/dendrite/tables/table1.stm
	public static final double SOMA_RADIUS = 16.5E-6; //http://synapses.clm.utexas.edu/anatomy/dendrite/tables/table1.stm
	public static final double PROXIMAL_DENDRITE_RADIUS = 2E-6;
	public static final double DISTAL_DENDRITE_RADIUS = .2E-6;
	
	//http://www.princeton.edu/~achaney/tmve/wiki100k/docs/Axon.html
	public static final double AXON_LENGTH = .005;
	

}

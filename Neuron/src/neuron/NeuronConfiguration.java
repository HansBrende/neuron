package neuron;


public abstract class NeuronConfiguration {
	
	public static enum Region {
		DENDRITE, SOMA, INITIAL_SEGMENT, MYELINATED_AXON, AXON_NODE
	}
	
	public final double length;
	
	public NeuronConfiguration(double length) {
		this.length = length;
	}
	
	public abstract double cytoRadius(double x);
	public abstract double cytoResistivity(double x);
	public abstract double membranePermittivity(double x);
	public abstract double membraneWidth(double x);
	
	public double c_m(double x) {
		double r = cytoRadius(x);
		return Util.c_m(membranePermittivity(x), r, r + membraneWidth(x));
	}
	
	public double r_a(double x) {
		return Util.r_a(cytoResistivity(x), cytoRadius(x));
	}
	
	public abstract double somaStart();
	
	public abstract double axonStart();
	
	public abstract double sheathGap();
	public abstract double dx();
	
	public abstract Region regionFor(double x);
	
	public abstract double I_inj(double x, double t);

}

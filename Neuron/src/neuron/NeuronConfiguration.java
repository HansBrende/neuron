package neuron;


public abstract class NeuronConfiguration {
	
	public static enum Region {
		DENDRITE, SOMA, MYELINATED_AXON, UNMYELINATED_AXON
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
	
	public abstract Region regionAt(double x);
	
	public abstract int myelinSheathCount();
	
	public abstract double somaStart();
	
	public abstract double unmyelinatedStart(int unmyelinatedAxonSegmentNumber);
	
	public abstract double myelinatedStart(int myelinatedAxonSegmentNumber);
	
	public abstract double I_inj(double x, double t);

}

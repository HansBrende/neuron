package neuron;

import hans.matrix.Matrix;
import hans.plot.PlotParams;
import hans.plot.PlotUtils;


public abstract class NeuronConfiguration {
	
	public static enum Region {
		DENDRITE, SOMA, HILLOCK, INITIAL_SEGMENT, MYELINATED_AXON, AXON_NODE
	}
	
	public final double length;
	
	public NeuronConfiguration(double length) {
		this.length = length;
	}
	
	public abstract double cytoRadius(double x);
	public abstract double cytoResistivity(double x);
	public abstract double membranePermittivity(double x);
	public abstract double membraneWidth(double x);
	
	public double avgRadius(double x) {
		return cytoRadius(x) + .5 * membraneWidth(x);
	}
	
	public double c_m(double x) {
		double r = cytoRadius(x);
		return Util.c_m(membranePermittivity(x), r, r + membraneWidth(x));
	}
	
	public double r_a(double x) {
		return Util.r_a(cytoResistivity(x), cytoRadius(x));
	}
	
	public void plot() {
		Matrix m = new Matrix(0, length, sheathGap() / 3, x->r_a(x));
		double high = 3e12;
		PlotUtils.plot(new PlotParams(m).yrange(-high, high).label("x (m)", "R (Ω/m)").title("Axial Resistance of Neuron").size(800, 400), m, "C:\\Users\\Hans\\Desktop\\neuronres.png");
	}
	
	public static void main(String[] args) {
		StandardConfiguration sc = new StandardConfiguration();
		sc.plot();
	}
	
	public abstract double somaStart();
	
	public abstract double axonStart();
	
	public abstract double sheathGap();
	public abstract double dx();
	
	public abstract Region regionFor(double x);
	
	public abstract double I_inj(double x, double t);

}

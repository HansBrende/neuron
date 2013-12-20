package graph;

import java.io.Serializable;
import java.util.function.DoubleFunction;

import neuron.BiVector;

public abstract class TemporalFunction implements DoubleFunction<BiVector[]>, Serializable {

	private static final long serialVersionUID = -1052777182911113352L;
	
	public final double dt;
	
	public TemporalFunction(double dt) {
		this.dt = dt;
	}

}

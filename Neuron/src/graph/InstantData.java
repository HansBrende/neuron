package graph;

import java.io.Serializable;

import neuron.BiVector;

public class InstantData implements Serializable {
	
	private static final long serialVersionUID = 8575520430665977260L;
	public final BiVector[] data;
	public final double instant;
	
	public InstantData(double instant, BiVector... data) {
		this.data = data;
		this.instant = instant;
	}

}

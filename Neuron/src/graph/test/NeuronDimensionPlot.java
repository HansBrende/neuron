package graph.test;

import graph.PlotParams;
import graph.Utils;


import neuron.StandardConfiguration;
import neuron.Util;

public class NeuronDimensionPlot {
	
	public static void main(String[] args) {
		StandardConfiguration config = new StandardConfiguration();
		PlotParams params = new PlotParams(0, config.length, -.00005, .00005).label("x (mm)", "y (mm)");
		params.scale(1000, 1000).lines().title("Neuron Dimensions");

		Utils.plot(params, Util.SHEATH_GAP / 3, x->config.cytoRadius(x) + config.membraneWidth(x) + .000004, 
				x->config.cytoRadius(x) + .000004,
				x->-config.cytoRadius(x) - config.membraneWidth(x) + .000004,
				x->-config.cytoRadius(x) + .000004);
	}

}

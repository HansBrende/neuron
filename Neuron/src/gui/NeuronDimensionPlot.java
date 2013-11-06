package gui;

import neuron.StandardConfiguration;
import neuron.Util;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NeuronDimensionPlot extends Application {
	
	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage s) throws Exception {
		s.setTitle("Neuron Dimensions");
		StandardConfiguration config = new StandardConfiguration();
		Plotter p = new Plotter(0, config.length, -.00005, .00005, 1000, 600, "x (mm)", "y (mm)", 1000, 1000);
		
		double dx = Util.SHEATH_GAP / 3;
		p.setDrawPoint(false);
		p.plot(x->config.cytoRadius(x) + config.membraneWidth(x) + .000004, dx);
		p.cutLine();
		p.plot(x ->config.cytoRadius(x) + .000004, dx);
		p.cutLine();
		p.plot(x->-config.cytoRadius(x) - config.membraneWidth(x) + .000004, dx);
		p.cutLine();
		p.plot(x->-config.cytoRadius(x) + .000004, dx);
		Scene scene = new Scene(p.getNode());
		s.setScene(scene);
		s.show();
	}

}

package gui;

import neuron.StandardConfiguration;
import neuron.Util;
import javafx.application.Application;
import javafx.scene.Group;
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
		Plotter p = new Plotter(0, config.length, -.00005, .00005, 1200, 400, "x (mm)", "y (mm)", 1000, 1000);
		
		double dx = Util.SHEATH_GAP / 3;
		p.setDrawPoint(false);
		p.plot(x->config.cytoRadius(x) + config.membraneWidth(x) + .000004, dx);
		p.cutLine();
		p.plot(x ->config.cytoRadius(x) + .000004, dx);
		p.cutLine();
		p.plot(x->-config.cytoRadius(x) - config.membraneWidth(x) + .000004, dx);
		p.cutLine();
		p.plot(x->-config.cytoRadius(x) + .000004, dx);
		
		s.setScene(new Scene(new Group(p.getCanvas())));
		//p.plot(Math::sin, .1);
		//TemporalPlot tp = new TemporalPlot(p, (x, t) -> Math.sin(x + t) / x, .1, .1);
		//tp.setScale(5);
		s.show();
		//tp.start();

		
	}

}

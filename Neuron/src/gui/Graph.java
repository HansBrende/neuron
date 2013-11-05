package gui;

import neuron.StandardConfiguration;
import neuron.Util;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Graph extends Application {
	
	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage s) throws Exception {
		StandardConfiguration config = new StandardConfiguration();
		Plotter p = new Plotter(0, config.length, -.00002, .00002, 1200, 400, "x (mm)", "y (mm)", 1000, 1000);
		System.out.println(Util.SHEATH_GAP);
		
		double dx = Util.SHEATH_GAP / 3;
		p.setDrawPoint(false);
		p.plot(x->config.cytoRadius(x) + config.membraneWidth(x), dx);
		s.setScene(new Scene(new Group(p.getCanvas())));
		//p.plot(Math::sin, .1);
		//TemporalPlot tp = new TemporalPlot(p, (x, t) -> Math.sin(x + t) / x, .1, .1);
		//tp.setScale(5);
		s.show();
		//tp.start();

		
	}

}

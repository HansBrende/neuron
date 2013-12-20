package gui;

import neuron.BiVector;
import graph.PlotParams;
import graph.TemporalFunction;
import graph.TemporalPlot;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GraphTestMotion extends Application {

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		PlotParams params = new PlotParams(-6, 6, -1, 1).label("x", "y").points(false).title("sin(x+t)/x");
		TemporalFunction func = new TemporalFunction(.0001) {
			private static final long serialVersionUID = -2112910739706568575L;
			@Override
			public BiVector[] apply(double value) {
				return new BiVector[] {
					new BiVector(x->Math.sin(x + value) / x, -6, 6, .01)	
				};
			}
		};
		TemporalPlot plot = new TemporalPlot(params, func);
		stage.setScene(new Scene(plot.getNode()));
		stage.show();
		plot.start();
	}

}

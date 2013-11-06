package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GraphTestMotion extends Application {
	
	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Plotter plotter = new Plotter(-6, 6, -1, 1, 600, 400, "x", "y", 1, 1);
		plotter.setDrawPoint(false);
		TemporalPlot plot = new TemporalPlot(plotter, (x,t)->Math.sin(x + t) / x, .01, .0001);
		stage.setScene(new Scene(plotter.getNode()));
		stage.show();
		plot.start();
	}

}

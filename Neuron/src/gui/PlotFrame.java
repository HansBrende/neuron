package gui;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PlotFrame extends Application {


	private Plotter plot = new Plotter(-4 * Math.PI, 4 * Math.PI, -1, 1, 600, 400, "x", "y");
	
	private TemporalPlot tplot = new TemporalPlot(plot, (x,t)->Math.sin(x + t), .1, .12);
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Drawing Operations Test");
		plot.setDrawLine(false);
		primaryStage.setScene(new Scene(new Group(plot.getCanvas())));
		tplot.setScale(5);
		primaryStage.show();
		tplot.start();
	}

}

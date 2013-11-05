package gui;

import neuron.Model.Neuron;
import neuron.ModelHH;
import neuron.BiVector;
import neuron.StandardConfiguration;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PlotFrame extends Application {


	private Plotter plot;
	private TemporalPlot tplot;

	public static void main(String[] args) {
		launch(args);
	}

	private Neuron neuron;
	private double dt = .0000000001;

	public void buildNeuron() {
		ModelHH model = new ModelHH();
		
		StandardConfiguration sc = new StandardConfiguration();

		neuron = model.build(sc);
	}

	public void buildTPlot() {
		plot = new Plotter(0, neuron.config.length, -100E-3, 100E-3, 600, 400, "x (mm)", "V (mV)", 1000, 1000);

		tplot = new TemporalPlot(plot, step -> {
			BiVector b = neuron.XV();
			neuron.step(step);
			return b;
		}, dt);
	}

	@Override
	public void start(Stage primaryStage) {
		buildNeuron();
		buildTPlot();

		Group g = new Group(plot.getCanvas());
//		TextField speedField = FXUtil.formattedTextField(true, FXUtil.DECIMAL,
//				() -> true);
//		Label speedlabel = new Label("Speed");
//		speedField.setOnAction(e -> {
//			String text = speedField.getText();
//			if (text.isEmpty() || text.equals("."))
//				speedField.setText(String.valueOf(tplot.getScale()));
//			else
//				tplot.setScale(Double.parseDouble(text));
//		});
//
//		HBox box = new HBox();
//		box.getChildren().setAll(speedlabel, speedField);

		primaryStage.setTitle("Drawing Operations Test");
		plot.setDrawPoint(false);

//		VBox vbox = new VBox();
//		vbox.getChildren().setAll(g, box);

		primaryStage.setScene(new Scene(g));
		primaryStage.show();
		tplot.start();
	}

}

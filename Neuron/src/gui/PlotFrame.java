package gui;

import neuron.Model;
import neuron.Model.Neuron;
import neuron.ModelHH;
import neuron.NeuronConfiguration;
import neuron.BiVector;
import neuron.StandardConfiguration;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PlotFrame extends Application {

	private Plotter plot = new Plotter(0, 1, -100E-3, 100E-3, 600, 400, "x", "y");

	private TemporalPlot tplot;

	public static void main(String[] args) {
		launch(args);
	}

	public void launch(Class<? extends Model> model,
			Class<? extends NeuronConfiguration> nc, double dx, double dt) {
		launch(model.getName(), nc.getName(), String.valueOf(dx),
				String.valueOf(dt));
	}

	private Neuron neuron;
	private double dt = .1;

	public void buildNeuron() {
		ModelHH model = new ModelHH(55E-3, -95E-3, -65E-3) {

			@Override
			public double gL(NeuronConfiguration config, double x) {
				return ModelHH.DEFAULT_G_L_MAX; //TODO
			}

			@Override
			public double gNa_max(NeuronConfiguration config, double x) {
				return ModelHH.DEFAULT_G_NA_MAX * Math.sin(x); //TODO
			}

			@Override
			public double gK_max(NeuronConfiguration config, double x) {
				return ModelHH.DEFAULT_G_K_MAX * Math.cos(x); //TODO
			}
		};
		
		StandardConfiguration sc = new StandardConfiguration();

		neuron = model.build(sc);
	}

	public void buildTPlot() {
		tplot = new TemporalPlot(plot, t -> {
			BiVector b = neuron.XV();
			neuron.incrementTime(dt, 1);
			return b;
		}, dt);
	}

	@Override
	public void start(Stage primaryStage) {
		buildNeuron();
		buildTPlot();

		Group g = new Group(plot.getCanvas());
		TextField speedField = FXUtil.formattedTextField(true, FXUtil.DECIMAL,
				() -> true);
		Label speedlabel = new Label("Speed");
		speedField.setOnAction(e -> {
			String text = speedField.getText();
			if (text.isEmpty() || text.equals("."))
				speedField.setText(String.valueOf(tplot.getScale()));
			else
				tplot.setScale(Double.parseDouble(text));
		});

		HBox box = new HBox();
		box.getChildren().setAll(speedlabel, speedField);

		primaryStage.setTitle("Drawing Operations Test");
		plot.setDrawPoint(false);

		VBox vbox = new VBox();
		vbox.getChildren().setAll(g, box);

		primaryStage.setScene(new Scene(vbox));
		primaryStage.show();
		tplot.setScale(.1);
		tplot.start();
	}

}

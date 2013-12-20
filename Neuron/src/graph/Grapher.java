package graph;

import java.io.File;
import java.util.List;

import neuron.BiVector;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Grapher extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		List<String> parameters = getParameters().getRaw();
		PlotParams params = Utils.importObject(parameters.get(0));
		stage.setTitle(params.title);
		Object data = Utils.importObject(parameters.get(1));
		if (data instanceof BiVector[]) {
			String filename = parameters.get(2);
			Plotter plotter = new Plotter(params);
			for (BiVector v : (BiVector[])data)
				plotter.plot(v);
			stage.setScene(new Scene(plotter));
			stage.show();
			
			if (filename != null && !filename.isEmpty())
				Utils.export(plotter, new File(filename));
		} else {
			TemporalPlot plotter = new TemporalPlot(params, (TemporalFunction)data);
			stage.setScene(new Scene(plotter.getNode()));
			stage.show();
			plotter.start();
		}
		
	}

}

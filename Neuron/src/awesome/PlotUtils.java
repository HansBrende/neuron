package awesome;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import matrix.Matrix;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

public class PlotUtils extends Application {
	
	public static void main(String[] args) {
		plot(new PlotParams(-10, 10, -3, 3), new Matrix(-10, 10, .1, Math::sin), "C:\\Users\\Hans\\Desktop\\sample.png");
	}
	
	public static void plot(PlotParams params, Matrix data, String filename) {
		String parms = SerialUtils.exportObject(params);
		String dat = SerialUtils.exportObject(data);
		Application.launch(PlotUtils.class, parms, dat, filename == null ? "" : filename);
	}
	
	public static void plot(PlotParams params, Matrix data) {
		plot(params, data, null);
	}

	@Override
	public void start(Stage stage) throws Exception {
		List<String> parameters = getParameters().getRaw();
		PlotParams params = SerialUtils.importObject(parameters.get(0));
		Matrix data = SerialUtils.importObject(parameters.get(1));
		String filename = parameters.size() > 2 ? parameters.get(2) : null;

		stage.setTitle(params.title);
		PlotCanvas canvas = new PlotCanvas(params);
		canvas.plotStats(Double.NaN, data);
		stage.setScene(new Scene(canvas));
		stage.show();
		if (filename != null && !filename.isEmpty()) {
			WritableImage image = canvas.canvas.snapshot(new SnapshotParameters(), null);
		    try {
		        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", new File(filename));
		    } catch (IOException e) {
		    	throw new IllegalArgumentException();
		    }
		}
	}

}

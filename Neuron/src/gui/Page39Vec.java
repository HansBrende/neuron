package gui;

import neuron.Vector;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import static java.lang.Math.*;

public class Page39Vec extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {

		Plotter plotter = new Plotter(-30, 50, -20, 120, 400, 400, "Time", "Voltage", 1, 1);
		plotter.setDrawPoint(true);

		stage.setScene(new Scene(plotter.getNode()));
		stage.show();
		stage.setTitle("Page 39 vec");

		// 0=K, 2=Na, 3=R
		Vector g = new Vector(36, 120, .3);
		Vector E = new Vector(-12, 115, 10.613);
		double I_ext = 0, V = -10;
		Vector x = new Vector(0, 0, 1);

		double dt = .01;

		for (double t = -30; t <= 50; t += dt) {
			if (abs(t - 10) < dt)
				I_ext = 10;
			if (abs(t - 40) < dt)
				I_ext = 0;
			double alpha0 = (10 - V) / (100 * (exp((10 - V) / 10) - 1));
			double alpha1 = (25 - V) / (10 * (exp((25 - V) / 10) - 1));
			double alpha2 = 0.07 * exp(-V / 20);
			 Vector alpha = new Vector(alpha0, alpha1, alpha2);
			double beta0 = .125 * exp(-V / 80);
			double beta1 = 4 * exp(-V / 18);
			double beta2 = 1 / (exp((30 - V) / 10) + 1);
			 Vector beta = new Vector(beta0, beta1, beta2);

			 Vector tau = alpha.plus(beta).inverse();

			 Vector x_0 = alpha.times(tau);
			 Vector tauInv = tau.inverse().times(dt);
			 x = tauInv.negate().plus(1).times(x).plus(tauInv.times(x_0));

			double gnmh0 = g.get(0) * pow(x.get(0), 4);
			double gnmh1 = g.get(1) * pow(x.get(1), 3) * x.get(2);
			double gnmh2 = g.get(2);

			 Vector gnmh = new Vector(gnmh0, gnmh1, gnmh2);

			 Vector I = gnmh.times(E.negate().plus(V));

			V = V + dt * (I_ext - I.sum());

			plotter.plot(t, V);
		}

	}

}

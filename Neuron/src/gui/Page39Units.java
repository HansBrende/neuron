package gui;

import static java.lang.Math.abs;
import static java.lang.Math.exp;
import static java.lang.Math.pow;


import graph.PlotParams;
import graph.Plotter;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import neuron.Vector;

public class Page39Units extends Application {
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {

		PlotParams params = new PlotParams().xrange(-30, 50).yrange(-100, 100).label("t (ms)", "V");

		Plotter plotter = new Plotter(params);
		stage.setScene(new Scene(plotter));
		stage.show();
		stage.setTitle("Page 39 units");

		// 0=K, 2=Na, 3=R
		
		Vector g = new Vector(36, 120, .3);
		Vector E = new Vector(-95, 55, -65);
		double I_ext = 0, V = -.010;
		Vector x = new Vector(0, 0, 1);

		double dt = .01;

		for (double t = -30; t <= 50; t += dt) {
			double mV = V;
			if (abs(t - 10) < dt)
				I_ext = 40;
			if (abs(t - 40) < dt)
				I_ext = 0;
			double alpha0 = (10 - mV) / (100 * (exp((10 - mV) / 10) - 1));
			double alpha1 = (25 - mV) / (10 * (exp((25 - mV) / 10) - 1));
			double alpha2 = 0.07 * exp(-mV / 20);
			 Vector alpha = new Vector(alpha0, alpha1, alpha2);
			double beta0 = .125 * exp(-mV / 80);
			double beta1 = 4 * exp(-mV / 18);
			double beta2 = 1 / (exp((30 - mV) / 10) + 1);
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

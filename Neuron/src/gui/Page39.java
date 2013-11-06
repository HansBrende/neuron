package gui;

import neuron.Vector;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import static java.lang.Math.*;

public class Page39 extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {

		Plotter plotter = new Plotter(-30, 50, -65, 65, 400, 400, "Time",
				"Voltage", 1, 1);
		plotter.setDrawPoint(false);

		stage.setScene(new Scene(plotter.getNode()));
		stage.setTitle("Page39");
		stage.show();

		// 0=K, 2=Na, 3=R
		Vector g = new Vector(36, 120, .3);
		//Vector E = new Vector(-12, 115, 10.613);
		Vector E = new Vector(-95, 55, -65);
		double I_ext = 0, V = -10;
		// Vector x = new Vector(0, 0, 1);
		double n = 0, m = 0, h = 1;

		double dt = .01;

		for (double t = -30; t <= 50; t += dt) {
			if (abs(t - 10) < dt)
				I_ext = 0;
			if (abs(t - 40) < dt)
				I_ext = 0;
			double alpha0 = (10 - V) / (100 * (exp((10 - V) / 10) - 1));
			double alpha1 = (25 - V) / (10 * (exp((25 - V) / 10) - 1));
			double alpha2 = 0.07 * exp(-V / 20);
			// Vector alpha = new Vector(alpha0, alpha1, alpha2);
			double beta0 = .125 * exp(-V / 80);
			double beta1 = 4 * exp(-V / 18);
			double beta2 = 1 / (exp((30 - V) / 10) + 1);
			// Vector beta = new Vector(beta0, beta1, beta2);

			// Vector tau = alpha.plus(beta).inverse();
			double tau0 = 1 / (alpha0 + beta0);
			double tau1 = 1 / (alpha1 + beta1);
			double tau2 = 1 / (alpha2 + beta2);
			// Vector x_0 = alpha.times(tau);
			double n_0 = alpha0 * tau0;
			double n_1 = alpha1 * tau1;
			double n_2 = alpha2 * tau2;
			// Vector tauInv = tau.inverse().times(dt);
			// x = tauInv.negate().plus(1).times(x).plus(tauInv.times(x_0));
			n = (1 - dt / tau0) * n + dt / tau0 * n_0;
			m = (1 - dt / tau1) * m + dt / tau1 * n_1;
			h = (1 - dt / tau2) * h + dt / tau2 * n_2;

			double gnmh0 = g.get(0) * pow(n, 4);
			double gnmh1 = g.get(1) * pow(m, 3) * h;
			double gnmh2 = g.get(2);

			// Vector gnmh = new Vector(gnmh0, gnmh1, gnmh2);

			// Vector I = gnmh.times(E.negate().plus(V));
			double I0 = gnmh0 * (V - E.get(0));
			double I1 = gnmh1 * (V - E.get(1));
			double I2 = gnmh2 * (V - E.get(2));

			V = V + dt * (I_ext - (I0 + I1 + I2));

			plotter.plot(t, V);
		}
		
		//plotter.export(new File("C:\\Users\\Hans\\Desktop\\page39.png"));

	}

}

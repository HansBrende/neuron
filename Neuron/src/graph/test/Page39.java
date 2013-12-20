package graph.test;

import graph.PlotParams;
import graph.Utils;
import neuron.BiVector;
import neuron.Vector;
import static java.lang.Math.*;

public class Page39 {

	public static void main(String[] args) {
		PlotParams params = new PlotParams(-30, 50, -20, 120).label("t (ms)", "V (mV)");
		params.title("Page 39 Actual Representation").lines();
		// 0=K, 2=Na, 3=R
		Vector g = new Vector(36, 120, .3);
		Vector E = new Vector(-12, 115, 10.613);
		double[] x = new double[] {0, 0, 1};
		double[] I_V = new double[] {0, -10};
		double dt = .01;
		
		BiVector data = new BiVector(t->{
			if (abs(t - 10) < dt)
				I_V[0] = 10;
			if (abs(t - 40) < dt)
				I_V[0] = 0;
			double I_ext = I_V[0], V = I_V[1];
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
			tauInv.negate().plus(1).times(new Vector(x)).plus(tauInv.times(x_0)).toArray(x);

			double gnmh0 = g.get(0) * pow(x[0], 4);
			double gnmh1 = g.get(1) * pow(x[1], 3) * x[2];
			double gnmh2 = g.get(2);

			Vector gnmh = new Vector(gnmh0, gnmh1, gnmh2);

			Vector I = gnmh.times(E.negate().plus(V));

			return I_V[1] = V + dt * (I_ext - I.sum());
		}, -30, 50, dt);
		Utils.plot(params, data);
	}

//	@Override
//	public void start(Stage stage) throws Exception {
//
//		PlotParams params = new PlotParams(-30, 50, -20, 120).label("Time", "Voltage");
//		params.title("Page 39 Actual Representation").lines();
//		Plotter plotter = new Plotter(params);
//
//		stage.setScene(new Scene(plotter));
//		stage.show();
//
//		// 0=K, 2=Na, 3=R
//		Vector g = new Vector(36, 120, .3);
//		Vector E = new Vector(-12, 115, 10.613);
//		double I_ext = 0, V = -10;
//		Vector x = new Vector(0, 0, 1);
//
//		double dt = .01;
//
//		for (double t = -30; t <= 50; t += dt) {
//			if (abs(t - 10) < dt)
//				I_ext = 10;
//			if (abs(t - 40) < dt)
//				I_ext = 0;
//			double alpha0 = (10 - V) / (100 * (exp((10 - V) / 10) - 1));
//			double alpha1 = (25 - V) / (10 * (exp((25 - V) / 10) - 1));
//			double alpha2 = 0.07 * exp(-V / 20);
//			Vector alpha = new Vector(alpha0, alpha1, alpha2);
//			double beta0 = .125 * exp(-V / 80);
//			double beta1 = 4 * exp(-V / 18);
//			double beta2 = 1 / (exp((30 - V) / 10) + 1);
//			Vector beta = new Vector(beta0, beta1, beta2);
//
//			Vector tau = alpha.plus(beta).inverse();
//
//			Vector x_0 = alpha.times(tau);
//			Vector tauInv = tau.inverse().times(dt);
//			x = tauInv.negate().plus(1).times(x).plus(tauInv.times(x_0));
//
//			double gnmh0 = g.get(0) * pow(x.get(0), 4);
//			double gnmh1 = g.get(1) * pow(x.get(1), 3) * x.get(2);
//			double gnmh2 = g.get(2);
//
//			Vector gnmh = new Vector(gnmh0, gnmh1, gnmh2);
//
//			Vector I = gnmh.times(E.negate().plus(V));
//
//			V = V + dt * (I_ext - I.sum());
//
//			plotter.plot(t, V);
//		}
//
//	}

}

package graph.test;

import java.util.ArrayList;

import hans.plot.PlotParams;
import hans.matrix.Matrix;
import hans.plot.PlotUtils;
import static java.lang.Math.*;


public class Page39Matrix {

	public static void main(String[] args) {
		PlotParams params = new PlotParams(-30, 50, -20, 120).label("t (ms)", "V (mV)");
		params.title("Page 39 Actual Representation").lines();
		// 0=K, 2=Na, 3=R
		Matrix g = Matrix.row(36, 120, .3);
		Matrix E = Matrix.row(-12, 115, 10.613);
		double[] x = new double[] {0, 0, 1};
		double[] I_V = new double[] {0, -10};
		double dt = .01;
		
		ArrayList<double[]> plot = new ArrayList<>();
		for (double t = -30; t < 50; t += dt){
			if (abs(t - 10) < dt)
				I_V[0] = 10;
			if (abs(t - 40) < dt)
				I_V[0] = 0;
			double I_ext = I_V[0], V = I_V[1];
			
			double alpha0 = (10 - V) / (100 * (exp((10 - V) / 10) - 1));
			double alpha1 = (25 - V) / (10 * (exp((25 - V) / 10) - 1));
			double alpha2 = 0.07 * exp(-V / 20);
			Matrix alpha = Matrix.row(alpha0, alpha1, alpha2);
			double beta0 = .125 * exp(-V / 80);
			double beta1 = 4 * exp(-V / 18);
			double beta2 = 1 / (exp((30 - V) / 10) + 1);
			Matrix beta = Matrix.row(beta0, beta1, beta2);

			Matrix tau = alpha.add(beta).op(v-> 1/v);

			Matrix x_0 = alpha.sctimes(tau);
			Matrix tauInv = tau.op(v->dt/v);
			x = tauInv.op(v->1-v).sctimes(Matrix.row(x)).add(tauInv.sctimes(x_0)).getRow(0);

			Matrix gnmh = Matrix.row(pow(x[0], 4), pow(x[1], 3) * x[2], 1).op((v,y)->v*y, g);

			Matrix I = gnmh.sctimes(E.op(d->V-d));

			plot.add(new double[] { t, I_V[1] = V + dt * (I_ext - I.sum())});
		};
		PlotUtils.plot(params, Matrix.columns(plot));
	}
}

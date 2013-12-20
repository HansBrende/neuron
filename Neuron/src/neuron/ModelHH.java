package neuron;

import java.util.ArrayList;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

import static java.lang.Math.*;

public class ModelHH extends Model {
	
	public static final ArrayList<Double> ts = new ArrayList<>();
	public static final ArrayList<Double> ms = new ArrayList<>();
	public static final ArrayList<Double> hs = new ArrayList<>();
	public static final ArrayList<Double> vs = new ArrayList<>();


	// TAKEN FROM PAGE 39
	public static final double DEFAULT_G_K_MAX = 360; // S/m^2
	public static final double DEFAULT_G_NA_MAX = 1200;
	public static final double DEFAULT_G_L_MAX = 3.0;
	
	public Vector gMax(NeuronConfiguration config, double x) {
		double r = config.avgRadius(x);
		DoubleUnaryOperator g_m = g -> Util.g_m(g, r);
		switch (config.regionFor(x)) {
		case AXON_NODE:
			return new Vector(DEFAULT_G_NA_MAX, DEFAULT_G_K_MAX, DEFAULT_G_L_MAX).op(g_m);
		case DENDRITE:
		case SOMA:
			return new Vector(DEFAULT_G_NA_MAX / 1000, DEFAULT_G_K_MAX, DEFAULT_G_L_MAX).op(g_m); //http://en.wikipedia.org/wiki/Axon_hillock
			//not sure about max potassium conductance here
		case HILLOCK:
		case INITIAL_SEGMENT:
			return new Vector(DEFAULT_G_NA_MAX / 10, DEFAULT_G_K_MAX, DEFAULT_G_L_MAX).op(g_m); //http://en.wikipedia.org/wiki/Axon_hillock
			//not sure about max potassium conductance here
		case MYELINATED_AXON:
			return new Vector(0, 0, 0).op(g_m);
		}
		throw new IllegalStateException();
	}

	public ModelHH(double E_Na, double E_K, double E_L) {
		super(new Vector(E_Na, E_K, E_L));
	}
	
	public ModelHH() {
		this(55E-3, -95E-3, -65E-3);
	}

	@Override
	protected Function<State, Vector> g() {
		return new Function<State, Vector>() {
			private double n, m, h = 1;

			public Vector apply(State state) {
			NeuronConfiguration c = state.config;
			double mV = state.V * 1000, dt = state.dt, x = state.x;
			
//			double prevM = m, prevH = h;
			
			double alpha0 = (10 - mV) / (100 * (exp((10 - mV) / 10) - 1));
			double alpha1 = (25 - mV) / (10 * (exp((25 - mV) / 10) - 1));
			double alpha2 = 0.07 * exp(-mV / 20);
			
			double beta0 = .125 * exp(-mV / 80);
			double beta1 = 4 * exp(-mV / 18);
			double beta2 = 1 / (exp((30 - mV) / 10) + 1);
			
			double tau0 = 1 / (alpha0 + beta0);
			double tau1 = 1 / (alpha1 + beta1);
			double tau2 = 1 / (alpha2 + beta2);
			
			double n_0 = alpha0 * tau0;
			double n_1 = alpha1 * tau1;
			double n_2 = alpha2 * tau2;
			
			n = (1 - dt / tau0) * n + dt / tau0 * n_0;
			m = (1 - dt / tau1) * m + dt / tau1 * n_1;
			h = (1 - dt / tau2) * h + dt / tau2 * n_2;
			
//			double[] tau_n_0 = tau_n_0(mV), tau_m_0 = tau_m_0(mV), tau_h_0 = tau_h_0(mV);
//			double tn = dt / tau_n_0[0], tm = dt / tau_m_0[0], th = dt / tau_h_0[0];
//			n = (1 - tn) * n + tn * tau_n_0[1];
//			m = (1 - tm) * m + tm * tau_m_0[1];
//			h = (1 - th) * h + th * tau_h_0[1];
			Vector gMax = gMax(c, x);
			double gK = gMax.get(1) * n * n * n * n;
			double gMax_0 = gMax.get(0);
			double gNa = gMax_0 * m * m * m * h;
			double gL = gMax.get(2);
//			if (!Double.isFinite(gNa)) {
//				System.out.println("previous V: " + mV);
//				System.out.println("gMax.get(0): " + gMax_0);
//				System.out.println("m: " + m);
//				System.out.println("h: " + h);
//				System.out.println("n: " + n);
//				Plotter plotter = new Plotter(0, state.t, -20, 20, 1000, 800, "t", "mh", 1, 1);
//				plotter.setDrawLine(false);
//				Stage stage = new Stage();
//				stage.setScene(new Scene(plotter.getNode()));
//				stage.show();
//				for (int index = 0; index < ts.size(); index++) {
//					double t = ts.get(index);
//					double m = ms.get(index);
//					double h = hs.get(index);
//					System.out.println("t: " + t + "m: " + m + "; h: " + h);
//
//					if (abs(m) < 20 || abs(h) < 20) {
//					plotter.plot(t, m);
//					plotter.plot(t, h);
//					} else {
//						break;
//					}
//				}
//				plotter.export(new File("C:\\Users\\Hans\\Desktop\\MH.png"));
//				System.exit(0);
//				//m: -5.549830187507807E244 these are the problems!
//				//h: 3.1765315618821984E201
//			}
//			if (abs(state.x - 0.0020580696255775685) < .00000001) {
//				ts.add(state.t);
//				ms.add(m);
//				hs.add(h);
//				vs.add(state.V);
//				System.out.println("t: " + state.t + "; n: " + n + "; m: " + m + "; h: " + h + "; Vprev: " + state.V);
//				if (abs(m) > 10E80 || abs(h) > 10E80)
//					System.exit(0);
//			}
			return new Vector(gNa, gK, gL);
			}
		};
	}
	
//	public static double[] tau_n_0(double mV) {
//		double alpha_n = (10 - mV) / (100 * (Math.exp((10 - mV) / 10) - 1));
//		double tau_n = 1 / (alpha_n + 0.125 * Math.exp(-mV / 80));
//		return new double[] { tau_n, tau_n * alpha_n };
//	}
//
//	public static double[] tau_m_0(double mV) {
//		double alpha_m = (25 - mV) / (10 * (Math.exp((25 - mV) / 10) - 1));
//		double tau_m = 1 / (alpha_m + 4 * Math.exp(-mV / 18));
//		return new double[] { tau_m, tau_m * alpha_m };
//	}
//
//	public static double[] tau_h_0(double mV) {
//		double alpha_h = 0.07 * Math.exp(-mV / 20);
//		double tau_h = 1 / (alpha_h + 1 / (Math.exp((30 - mV) / 10) + 1));
//		return new double[] { tau_h, tau_h * alpha_h };
//
//	}

	@Override
	protected double restingPotential() {
		return -.065;
	}

}

package neuron;

import java.util.function.Function;

public class ModelHH extends Model {


	// TAKEN FROM PAGE 39
	public static final double DEFAULT_G_K_MAX = 360; // S/m^2
	public static final double DEFAULT_G_NA_MAX = 1200;
	public static final double DEFAULT_G_L_MAX = 3.0;
	
	public Vector gMax(NeuronConfiguration config, double x) {
		switch (config.regionFor(x)) {
		case AXON_NODE:
			return new Vector(DEFAULT_G_NA_MAX, DEFAULT_G_K_MAX, DEFAULT_G_L_MAX);
		case DENDRITE:
		case SOMA:
			return new Vector(DEFAULT_G_NA_MAX / 1000, DEFAULT_G_K_MAX, DEFAULT_G_L_MAX); //http://en.wikipedia.org/wiki/Axon_hillock
			//not sure about max potassium conductance here
		case HILLOCK:
		case INITIAL_SEGMENT:
			return new Vector(DEFAULT_G_NA_MAX / 10, DEFAULT_G_K_MAX, DEFAULT_G_L_MAX); //http://en.wikipedia.org/wiki/Axon_hillock
			//not sure about max potassium conductance here
		case MYELINATED_AXON:
			return new Vector(0, 0, 0);
		}
		throw new IllegalStateException();
	}

	public ModelHH(double E_Na, double E_K, double E_L) {
		super(new Vector(E_Na, E_K, E_L));
	}
	
	public ModelHH() {
		this(55E-3, -95E-3, -65E-3);
	}

	private double n, m, h;

	@Override
	protected Function<State, Vector> g() {
		return state -> {
			NeuronConfiguration c = state.config;
			double mV = state.V * 1000, dt = state.dt, x = state.x;
			double[] tau_n_0 = tau_n_0(mV), tau_m_0 = tau_m_0(mV), tau_h_0 = tau_h_0(mV);
			double tn = dt / tau_n_0[0], tm = dt / tau_m_0[0], th = dt
					/ tau_h_0[0];
			n = (1 - tn) * n + tn * tau_n_0[1];
			m = (1 - tm) * m + tm * tau_m_0[1];
			h = (1 - th) * h + th * tau_h_0[1];
			Vector gMax = gMax(c, x);
			double gK = gMax.get(1) * n * n * n * n;
			double gNa = gMax.get(0) * m * m * m * h;
			double gL = gMax.get(2);
			return new Vector(gNa, gK, gL);
		};
	}

	public static double[] tau_n_0(double mV) {
		double alpha_n = (10 - mV) / (100 * (Math.exp((10 - mV) / 10) - 1));
		double tau_n = 1 / (alpha_n + 0.125 * Math.exp(-mV / 80));
		return new double[] { tau_n, tau_n * alpha_n };
	}

	public static double[] tau_m_0(double mV) {
		double alpha_m = (25 - mV) / (10 * (Math.exp((25 - mV) / 10) - 1));
		double tau_m = 1 / (alpha_m + 4 * Math.exp(-mV / 18));
		return new double[] { tau_m, tau_m * alpha_m };
	}

	public static double[] tau_h_0(double mV) {
		double alpha_h = 0.07 * Math.exp(-mV / 20);
		double tau_h = 1 / (alpha_h + 1 / (Math.exp((30 - mV) / 10) + 1));
		return new double[] { tau_h, tau_h * alpha_h };

	}

	@Override
	protected double restingPotential() {
		return E.get(2);
	}

}

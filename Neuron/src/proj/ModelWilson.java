package proj;

import java.util.function.Function;

public abstract class ModelWilson extends Model {
	
	//constants from page 43 of textbook
	public static double DEFAULT_E_T = 120E-3;
	public static double DEFAULT_E_NA = 50E-3;
	public static double DEFAULT_E_K = -95E-3; //E_R = E_K
	public static double DEFAULT_G_NA = 10;
	public static double DEFAULT_G_K = 260;
	public static double DEFAULT_T_T = 14E-3;
	public static double DEFAULT_T_R = 45E-3;
	public static double DEFAULT_C = 100E-6/1E-2; //Farads/meter

	public ModelWilson(double E_Na, double E_K, double E_T) {
		super(new Vector(E_Na, E_K, E_T, E_K));
	}
	
	public ModelWilson(double E_Na, double E_K) {
		this(E_Na, E_K, DEFAULT_E_T);
	}
	
	private double R, T, H;

	@Override
	protected Function<State, Vector> g() {
		return state -> {
			NeuronConfiguration c = state.config;
			double mV = state.V * 1000, dt = state.dt, x = state.x;
			double tR = dt/tau_R(c, x), tT = dt/tau_T(c, x), tH = dt/tau_H(c, x);
			R = (1 - tR) * R + tR * R0(mV);
			H = (1 - tH) * H + tH * 3 * T;
			T = (1 - tT) * T + tT * T0(mV);
			double na = gNa(c, x, mV);
			double k = R * gK(c, x);
			double t = T * gT(c, x);
			double h = H * gH(c, x);
			return new Vector(na, k, t, h);
		};
	}
	
	private static double R0(double mV) {
		return 1.24 + 0.037 * mV + 3.2E-4 * mV*mV;
	}
	
	private static double T0(double mV) {
		return 4.205 + 0.116 * mV + 8E-4 * mV*mV;
	}
	
	protected static double gNa(double mV) { //??? units???? significance??? ... ???????
		return (17.8 + .476*mV + 33.8E-4*mV*mV) * 10;
	}
	
	public abstract double gNa(NeuronConfiguration config, double x, double V);
	public abstract double gK(NeuronConfiguration config, double x);
	public abstract double gT(NeuronConfiguration config, double x);
	public abstract double gH(NeuronConfiguration config, double x);
	
	public abstract double tau_R(NeuronConfiguration config, double x);
	public abstract double tau_T(NeuronConfiguration config, double x);
	public abstract double tau_H(NeuronConfiguration config, double x);

}

package neuron;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

public abstract class Model {
	
	public class State {
		public final double x, t, dt, V;
		public final NeuronConfiguration config;
		public State(double x, double t, double dt, double V, NeuronConfiguration config) {
			this.x = x;
			this.t = t;
			this.dt = dt;
			this.V = V;
			this.config = config;
		}
	}
	
	public final Vector E;
		
	public Model(Vector E) {
		this.E = E;
	}
	
	protected abstract double restingPotential();
	
	protected abstract Function<State, Vector> g();
	
	public Neuron build(NeuronConfiguration nc) {
		return new Neuron(nc);
	}
	
	public class Neuron extends AbstractList<Sliver> {
		
		private double elapsedSeconds;

		private final ArrayList<Sliver> slivers = new ArrayList<>();
		
		private final Function<State, Vector> g;
		
		public final NeuronConfiguration config;
		public final int stepCount;
		public final double dx;
		
		private Neuron(NeuronConfiguration nc) {
			
			config = nc;
			stepCount = (int)(nc.length / nc.dx());
			dx = config.length / stepCount;
			g = g();
			for (int x = 0; x < stepCount; x++)
				slivers.add(new Sliver(this, x));
		}
		
		public double elapsedSeconds() {
			return elapsedSeconds;
		}
		
		public void step(double dt) {
			for (Sliver sliver : slivers)
				sliver.calculateStep(dt);
			for (Sliver sliver : slivers)
				sliver.propagateStep();
			elapsedSeconds += dt;
		}
		
//		public void incrementTime(double seconds, int numSteps) {
//			double dt = seconds / numSteps;
//			for (int x = 0; x < numSteps; x++) {
//				elapsedSeconds += dt;
//				for (Sliver sliver : slivers)
//						sliver.calculateStep(dt);
//				for (Sliver sliver : slivers)
//					sliver.propagateStep();
//			}
//		}
		
		public double I_ion(State state) {
			
			Vector v = g.apply(state);
			double d = E.negate().plus(state.V).dot(v);
//			if (!Double.isFinite(d)) {
//				System.out.println("E_Na: " + E.get(0));
//				System.out.println("E_K: " + E.get(1));
//				System.out.println("E_L: " + E.get(2));
//				System.out.println("V: " + state.V);
//				System.out.println("g_Na: " + v.get(0));
//				System.out.println("g_K: " + v.get(1));
//				System.out.println("g_L: " + v.get(2));
//				System.exit(0);
//			}
			return d;
		}
		
		@Override
		public Sliver get(int index) {
			return slivers.get(index);
		}

		@Override
		public int size() {
			return stepCount;
		}
		
		public BiVector XV() {
			double[] x = slivers.stream().mapToDouble(sliver -> sliver.x).toArray();
			double[] v = slivers.stream().mapToDouble(sliver -> sliver.V()).toArray();
			return new BiVector(x, v);
		}
		
	}
	
	public class Sliver {
		
		public final Neuron neuron;
		public final int index;
		public final double dx, x, c_m, r_a;
		public final DoubleSupplier time;
		public final Sliver previous;
		public final ToDoubleFunction<State> I_ion;
		public final DoubleUnaryOperator I_inj;

		private double V, temporaryV;
		
		private Sliver(Neuron n, int i) {
			neuron = n;
			x = (index = i) * (dx = n.dx);
			c_m = n.config.c_m(x);
			r_a = n.config.r_a(x);
			time = n::elapsedSeconds;
			I_ion = n::I_ion;
			I_inj = t -> n.config.I_inj(x, t);
			V = restingPotential();
			previous = index == 0 ? this : n.get(index - 1);

		}
		
		public void calculateStep(double dt) {
			State state = new State(x, time.getAsDouble(), dt, V, neuron.config);
			double i = I_ion.applyAsDouble(state) + I_inj.applyAsDouble(state.t);
			temporaryV = ((previous.V + next().V - 2*V)/(r_a*dx*dx) - i) * dt/c_m + V;
//			if (!Double.isFinite(temporaryV)) {
//				System.out.println("x: " + x);
//				System.out.println("region: " + neuron.config.regionFor(x));
//				System.out.println("new V: " + temporaryV);
//				System.out.println("previous.V: " + previous.V);
//				System.out.println("next.V: " + next().V);
//				System.out.println("old V: " + V);
//				System.out.println("r_a: " + r_a);
//				System.out.println("dx: " + dx);
//				System.out.println("dx*dx*r_a: " + r_a*dx*dx);
//				System.out.println("dt: " + dt);
//				System.out.println("i: " + i);
//				System.out.println("dt/c_m: " + dt/c_m);
//				System.exit(0);
//			}
		}
		
		private Sliver next() {
			return index == neuron.stepCount - 1 ? this : neuron.get(index + 1);
		}
		
		public void propagateStep() {
			V = temporaryV;
		}
		
		public double V() {
			return V;
		}
		
	}
	
}

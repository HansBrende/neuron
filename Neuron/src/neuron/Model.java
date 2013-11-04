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
	
	protected abstract Function<State, Vector> g();
	
	public Neuron build(NeuronConfiguration nc, int numSteps) {
		return new Neuron(nc, numSteps);
	}
	
	public class Neuron extends AbstractList<Sliver> {
		
		private double elapsedSeconds;

		private final ArrayList<Sliver> slivers = new ArrayList<>();
		
		private final Function<State, Vector> g;
		
		public final NeuronConfiguration config;
		public final int stepCount;
		public final double dx;
		
		private Neuron(NeuronConfiguration nc, int numSteps) {
			config = nc;
			stepCount = numSteps;
			dx = config.length / numSteps;
			g = g();
			for (int x = 0; x < numSteps; x++)
				slivers.add(new Sliver(this, x));
		}
		
		public double elapsedSeconds() {
			return elapsedSeconds;
		}
		
		public void incrementTime(double seconds, int numSteps) {
			double dt = seconds / numSteps;
			for (int x = 0; x < numSteps; x++) {
				elapsedSeconds += dt;
				for (Sliver sliver : slivers)
						sliver.calculateStep(dt);
				for (Sliver sliver : slivers)
					sliver.propagateStep();
			}
		}
		
		public double I_ion(State state) {
			return E.negate().plus(state.V).dot(g.apply(state));
		}
		
		@Override
		public Sliver get(int index) {
			return slivers.get(index);
		}

		@Override
		public int size() {
			return stepCount;
		}
		
	}
	
	public class Sliver {
		
		public final Neuron neuron;
		public final int index;
		public final double dx, x, c_m, r_a;
		public final DoubleSupplier time;
		public final Sliver previous, next;
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
			previous = index == 0 ? this : n.get(index - 1);
			next = index == n.stepCount - 1 ? this : n.get(index + 1);
		}
		
		public void calculateStep(double dt) {
			State state = new State(x, time.getAsDouble(), dt, V, neuron.config);
			double i = I_ion.applyAsDouble(state) + I_inj.applyAsDouble(state.t);
			temporaryV = ((previous.V + next.V - 2*V)/(r_a*dx*dx) - i) * dt/c_m + V;
		}
		
		public void propagateStep() {
			V = temporaryV;
		}
		
		public double V() {
			return V;
		}
		
	}
	
}

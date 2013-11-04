package neuron;

import java.util.function.DoubleUnaryOperator;

public class BiVector {
	
	public final int length;
	public final double[] x, y;
	
	public BiVector(double[] x, double[] y) {
		if (x.length != y.length)
			throw new IllegalArgumentException();
		this.x = x;
		this.y = y;
		this.length = x.length;
	}
	
	public BiVector(DoubleUnaryOperator op, double xmin, double xmax, int steps) {
		this(new double[steps], new double[steps]);
		double dx = (xmax - xmin) / steps;
		for (int i = 0; i < steps; i++)
			y[i] = op.applyAsDouble(x[i] = xmin + dx*i);
	}
	
	public BiVector(DoubleUnaryOperator op, double xmin, double xmax, double dx) {
		this(op, xmin, xmax, (int)((xmax - xmin)/dx));
	}

}

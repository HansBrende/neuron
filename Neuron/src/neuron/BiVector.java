package neuron;

import java.io.Serializable;
import java.util.function.DoubleUnaryOperator;

public class BiVector implements Serializable {
	
	private static final long serialVersionUID = -6572142955459912648L;
	
	public final int length;
	public final double[] x, y;
	
	public BiVector(double[] x, double[] y) {
		if (x.length != y.length)
			throw new IllegalArgumentException();
		this.x = x;
		this.y = y;
		this.length = x.length;
	}
	
	public BiVector y(int index, double value) {
		y[index] = value;
		return this;
	}
	
	public BiVector x(int index, double value) {
		x[index] = value;
		return this;
	}
	
	public BiVector xy(int index, double x, double y) {
		this.x[index] = x;
		this.y[index] = y;
		return this;
	}
	
	public BiVector(int length) {
		this(new double[length], new double[length]);
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

package proj;

import java.util.Arrays;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntToDoubleFunction;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Vector {
	
	private static int length(Vector...vectors) {
		if (vectors.length == 0)
			throw new IllegalArgumentException();
		int length = 1;
		for (Vector v : vectors)
			if (length == 1)
				length = v.length;
			else if (v.length != 1 && v.length != length)
				throw new IllegalArgumentException();
		return length;
	}
	
	public static interface MultiOp {
		double apply(double... ds);
		default Vector apply(Vector...vs) {
			int len = length(vs);
			Vector v = new Vector(len);
			for (int x = 0; x < len; x++) {
				double[] ds = new double[vs.length];
				for (int y = 0; y < vs.length; y++) {
					Vector vy = vs[y];
					ds[y] = vy.values[vy.length == 1 ? 0 : x];
				}
				v.values[x] = apply(ds);
			}
			return v;
		}
		default Vector apply(Object...ns) {
			Vector[] vectors = new Vector[ns.length];
			for (int x = 0; x < ns.length; x++) {
				Object o = ns[x];
				if (o instanceof Vector)
					vectors[x] = (Vector)o;
				else if (o instanceof Number)
					vectors[x] = new Vector(((Number)o).doubleValue());
				else if (o instanceof double[])
					vectors[x] = new Vector((double[])o);
				else if (o instanceof int[])
					vectors[x] = new Vector(IntStream.of((int[])o).asDoubleStream().toArray());
				else if (o instanceof long[])
					vectors[x] = new Vector(LongStream.of((long[])o).asDoubleStream().toArray());
				else if (o instanceof Number[])
					vectors[x] = new Vector(Stream.of((Number[])o).mapToDouble(n -> n.doubleValue()).toArray());
			}
			return apply(vectors);
		}
	}
	
	public final int length;
	
	private final double[] values;
	
	private Vector(int length) {
		values = new double[this.length = length];
	}
	
	public Vector(double...values) {
		this.values = Arrays.copyOf(values, this.length = values.length);
	}
	
	public Vector(int length, IntToDoubleFunction filler) {
		this(length);
		for (int x = 0; x < length; x++)
			values[x] = filler.applyAsDouble(x);
	}
	
	public Vector(int length, double value) {
		this(length, x -> value);
	}	
	
	public Vector op(DoubleUnaryOperator op) {
		return new Vector(length, x -> op.applyAsDouble(values[x]));
	}
	
	public Vector op(DoubleBinaryOperator op, Vector t) {
		MultiOp o = x->op.applyAsDouble(x[0], x[1]);
		return o.apply(this, t);
	}

	public static Vector op(MultiOp op, Vector...vs) {
		return op.apply(vs);
	}
	
	public static Vector op(MultiOp op, Object...vs) {
		return op.apply(vs);
	}

	
	public Vector plus(Vector t) {
		return op((x,y)->x+y, t);
	}
	
	public Vector minus(Vector t) {
		return op((x,y)->x-y, t);
	}
	
	public Vector times(Vector t) {
		return op((x,y)->x*y, t);
	}
	
	public Vector divide(Vector t) {
		return op((x,y)->x/y, t);
	}
	
	public Vector pow(Vector t) {
		return op(Math::pow, t);
	}
	
	public Vector plus(double... d) {
		return plus(new Vector(d));
	}
	
	public Vector minus(double... d) {
		return minus(new Vector(d));
	}
	
	public Vector times(double... d) {
		return times(new Vector(d));
	}
	
	public Vector divide(double... d) {
		return divide(new Vector(d));
	}
	
	public Vector pow(double... d) {
		return pow(new Vector(d));
	}
	
	public Vector negate() {
		return times(-1);
	}
	
	public Vector inverse() {
		return pow(-1);
	}
	
	public double get(int index) {
		return values[index];
	}
	
	public double sum() {
		double total = 0;
		for (int x = 0; x < length; x++)
			total += values[x];
		return total;
	}
	
	public double dot(Vector other) {
		return times(other).sum();
	}
	
	public double dot(double...ds) {
		return dot(new Vector(ds));
	}
	
}

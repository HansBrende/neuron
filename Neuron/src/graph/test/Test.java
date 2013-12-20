package graph.test;

import java.util.function.DoubleUnaryOperator;
import java.util.stream.IntStream;

import neuron.BiVector;
import static java.lang.Math.*;
import graph.PlotParams;
import graph.Utils;

public class Test {
	
	public static void main(String[] args) {
		PlotParams params = new PlotParams().range(0, 10, 0, 1).title("Hans's Awesome Graphing Software").label("time", "active nodes");
		int theta = 3;
		double C = 10, a0 = 0.17;
		DoubleUnaryOperator op = a -> (1-a)*(1-exp(-a*C)*range(theta).mapToDouble(n->pow((a*C), n)/fact(n)).sum());
		BiVector vector = new BiVector(30).y(0, a0);
		IntStream.range(1, vector.length).forEachOrdered(x -> vector.xy(x, x, op.applyAsDouble(vector.y[x - 1])));
		Utils.plot("C://Users//Hans//Desktop//graph.png", params, vector);
	}
	
	public static IntStream range(int x) {
		return IntStream.range(0, x);
	}
	
	public static int fact(int x) {
		if (x == 0)
			return 1;
		return IntStream.rangeClosed(1, x).reduce((z,y)->z*y).getAsInt();
	}

}

package neuron;

import java.util.ArrayList;

import awesome.PlotParams;
import awesome.PlotUtils;
import matrix.Matrix;
import neuron.Model.Neuron;
import neuron.Model.Sliver;

public class NeuronDriver {
	
	public static void main(String[] args) {
		ModelHH model = new ModelHH();
		Neuron neuron = model.build(new StandardConfiguration());
		double dt = .001;
		ArrayList<double[]> points = new ArrayList<>();
		double time = 0;
		double d = .5;
		while (true) {
			if (Math.abs(time - d) < dt) {
				for (Sliver s : neuron) {
					points.add(new double[] {
							s.x, s.V()
					});
				}
				break;
			}
			neuron.step(dt);
			time += dt;
		}
		Matrix m = Matrix.columns(points);
		System.out.println(m.getRowMatrix(0).getSubMatrix(0, 0, 16000, 18000));
		System.out.println(m.getRowMatrix(1).getSubMatrix(0, 0, 16000, 18000));
		PlotParams params = new PlotParams(m).yrange(-.1, .1).label("x (m)", "V (V)").title("Voltage at time t = " + d + " s across the Neuron");
		PlotUtils.plot(params, m, "C:\\Users\\Hans\\Desktop\\crap.png");
	}


}

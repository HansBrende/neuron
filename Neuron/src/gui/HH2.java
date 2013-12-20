package gui;

import java.util.ArrayList;

import neuron.BiVector;

public class HH2 {
	
	private ArrayList<HH> list = new ArrayList<HH>();
	
	private double dx = .01;
	
	public HH2() {

		for (int x = 0; x < 1000; x++) {
			list.add(new HH());
		}
	}
	
	public void step(double t, double dt) {
		for (int x = 0; x < 1000; x++) {
			double prev = x == 0 ? -65 : list.get(x - 1).V();
			double old = list.get(x).V();
			double next = x == 999 ? -65 : list.get(x + 1).V();
			double extraCurrent = prev + next - 2*old;
			extraCurrent = extraCurrent / (220*dx * dx);
			list.get(x).startStep((x < 40 ? 0 : x < 70 ? 200 : 0) + extraCurrent, dt);
		}
		for (HH h : list)
			h.finishStep();
	}
	
	public BiVector get() {
		double[] x = new double[list.size()];
		for (int y = 0; y < x.length; y++)
			x[y] = y*dx;
		double[] v = list.stream().mapToDouble(sliver -> sliver.V()).toArray();
		return new BiVector(x, v);
	}

}

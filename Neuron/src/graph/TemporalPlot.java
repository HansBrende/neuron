package graph;

import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.text.TextAlignment;
import neuron.BiVector;

public class TemporalPlot extends AnimationTimer {

	private final Plotter plot;
	private final double height;

	private final TemporalFunction it;
	private double time = 0;
	
	public Group getNode() {
		return plot;
	}

	public TemporalPlot(PlotParams params, TemporalFunction it) {
		this.plot = new Plotter(params);
		this.it = it;
		this.height = params.top + params.bottom + params.height - 10;
	}
	
	public void start() {		
		super.start();
	}
		
	private static final long framePeriod = 33_333_333;

	@Override
	public void handle(long nanos) {
		long next = nanos + framePeriod;
		plot.reset();
		BiVector[] v = it.apply(time);
		double[] stats = plot.plotGetStats(v);
		TextAlignment ta = plot.gc.getTextAlign();
		plot.gc.setTextAlign(TextAlignment.LEFT);
		String[] formats = new String[stats.length];
		for (int x = -1; x < formats.length - 1; x++) {
			double value = x == -1 ? time : stats[x];
			double vabs = Math.abs(value);
			formats[x + 1] = vabs >= 1000000 || vabs < .001 ? "%.1e" : "%.3f";
		}
		String format = "t=" + formats[0] + " s     min=" + formats[1] + "; max=" + formats[2] + "; avg=" + formats[3] + "; num points=" + (int)stats[stats.length - 1];
		plot.gc.strokeText(String.format(format, time, stats[0], stats[1], stats[2]), 10, height);
		
		plot.gc.setTextAlign(ta);
		time += it.dt;
		while (next > System.nanoTime()) {
			it.apply(time);
			time += it.dt;
		}
		
	}
}



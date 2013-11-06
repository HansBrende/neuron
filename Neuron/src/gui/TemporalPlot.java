package gui;

import java.util.function.DoubleBinaryOperator;

import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.text.TextAlignment;
import neuron.BiVector;

public class TemporalPlot extends AnimationTimer {
	
	public static interface DoubleBiFunction {
		BiVector apply(double t, double dt);
	}

	private final Plotter plot;
	private final double dt;

	private final DoubleBiFunction it;
	private double time = 0;
	
	public Canvas getCanvas() {
		return plot.getCanvas();
	}
	
	public Group getNode() {
		return plot.getNode();
	}

	public TemporalPlot(Plotter plot, DoubleBiFunction it, double dt) {
		this.plot = plot;
		this.it = it;
		this.dt = dt;
	}
	
	public TemporalPlot(Plotter plot, DoubleBinaryOperator op, double dx, double dt) {
		this(plot, (t, step)->new BiVector(x->op.applyAsDouble(x, t), plot.xmin, plot.xmax, dx), dt);
	}
	
	public void start() {		
		super.start();
	}
		
	private static final long framePeriod = 33_333_333;

	@Override
	public void handle(long nanos) {
		long next = nanos + framePeriod;
		plot.reset();
		BiVector v = it.apply(time, dt);
		double[] stats = plot.plotGetStats(v);
		TextAlignment ta = plot.gc.getTextAlign();
		plot.gc.setTextAlign(TextAlignment.LEFT);
		String[] formats = new String[stats.length + 1];
		for (int x = -1; x < stats.length; x++) {
			double value = x == -1 ? time : stats[x];
			double vabs = Math.abs(value);
			formats[x + 1] = vabs >= 1000000 || vabs < .001 ? "%.1e" : "%.3f";
		}
		String format = "t=" + formats[0] + " s     min=" + formats[1] + "; max=" + formats[2] + "; avg=" + formats[3] + "; num points=" + v.length;
		plot.gc.strokeText(String.format(format, time, stats[0], stats[1], stats[2]), 10, plot.top + plot.height + plot.bottom - 10);
		
		plot.gc.setTextAlign(ta);
		time += dt;
		while (next > System.nanoTime()) {
			it.apply(time, dt);
			time += dt;
		}
		
	}
}



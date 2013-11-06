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
		plot.plot(it.apply(time, dt));
		TextAlignment ta = plot.gc.getTextAlign();
		plot.gc.setTextAlign(TextAlignment.LEFT);
		double vabs = Math.abs(time);
		String format = vabs >= 1000000 || vabs < .001 ? "t=%.1e s" : "t=%.3f s";
		plot.gc.strokeText(String.format(format, time), 10, plot.top + plot.height + plot.bottom - 10);
		plot.gc.setTextAlign(ta);
		time += dt;
		while (next > System.nanoTime()) {
			it.apply(time, dt);
			time += dt;
		}
		
	}
}



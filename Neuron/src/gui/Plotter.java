package gui;

import java.util.function.DoubleUnaryOperator;

import neuron.BiVector;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class Plotter {
	
	
	public final Canvas canvas;
	public final GraphicsContext gc;

	public final double xmin, xmax, ymin, ymax;
	public final int length, height;

	public final int top = 50, left = 50, bottom = 50, right = 50;
	private double lastx, lasty;
	private boolean lines = true, points = true;
	public final double xscale, yscale;
	
	private String xlabel, ylabel;
	
	public void setDrawLine(boolean b) {
		lines = b;
	}
	
	public void setDrawPoint(boolean b) {
		points = b;
	}
	
	public Canvas getCanvas() {
		return canvas;
	}

	public Plotter(double xmin, double xmax, double ymin, double ymax,
			int length, int height, String xlabel, String ylabel, double xscale, double yscale) {
		this.xscale = xscale;
		this.yscale = yscale;
		this.xmin = xmin;
		this.ymin = ymin;
		this.xmax = xmax;
		this.ymax = ymax;
		this.length = length;
		this.height = height;
		this.xlabel = xlabel;
		this.ylabel = ylabel;
		canvas = new Canvas(length + left + right, height + top + bottom);
		gc = canvas.getGraphicsContext2D();
		gc.setTextAlign(TextAlignment.CENTER);
		reset();
	}
	
	public void reset() {
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, length + left + right, height + top + bottom);
		gc.setFill(Color.BLACK);
		double y0 = y(0), x0 = x(0);
		gc.strokeLine(x(xmin), y0, x(xmax), y0);
		gc.strokeLine(x0, y(ymin), x0, y(ymax));
		double dx = (xmax - xmin) / 10, dy = (ymax - ymin) / 10;
		for (int x = 0; x <= 10; x++) {
			double value = xmin + x * dx;
			double xdx = x(value);
			gc.strokeLine(xdx, y0 + 3, xdx, y0 - 3);
			double val = value * xscale;
			double vabs = Math.abs(val);
			if (value > 1E-16 || value < -1E-16) {
				String format = vabs >= 1000 || vabs < .001 ? "%.1e" : (vabs > 9 ? "%.1f" : "%.3f");
				gc.strokeText(String.format(format, val), xdx, y0 + 12);
			}
		}
		gc.setTextAlign(TextAlignment.RIGHT);
		for (int y = 0; y <= 10; y++) {
			double value = ymin + y * dy;
			double ydy = y(value);
			gc.strokeLine(x0 + 3, ydy, x0 - 3, ydy);
			double val = value * yscale;
			double vabs = Math.abs(val);
			if (value > 1E-16 || value < -1E-16) {
				String format = vabs >= 1000 || vabs < .001 ? "%.1e" : (vabs > 9 ? "%.1f" : "%.3f");
				gc.strokeText(String.format(format, val), x0 - 8, ydy);
			}
		}
		gc.setTextAlign(TextAlignment.LEFT);
		if (xlabel != null)
			gc.strokeText(xlabel, left + length + 5, y(0));
		gc.setTextAlign(TextAlignment.CENTER);
		if (ylabel != null)
			gc.strokeText(ylabel, x(0), top - 15);
		lastx = Double.NaN;
		lasty = Double.NaN;
	}

	public double x(double x) {
		return length / (xmax - xmin) * (x - xmin) + left;
	}

	public double y(double y) {
		return height / (ymin - ymax) * (y - ymax) + top;
	}

	public void plot(double x, double y) {
		double xx = x(x), yy = y(y);
		if (lines && !Double.isNaN(lastx))
			gc.strokeLine(lastx, lasty, xx, yy);
		if (points)
			gc.fillOval(xx - 1.5, yy - 1.5, 3, 3);
		lastx = xx;
		lasty = yy;
	}

	public void plot(BiVector v) {
		for (int i = 0; i < v.length; i++)
			plot(v.x[i], v.y[i]);
	}

	public void plot(DoubleUnaryOperator f, double dx) {
		for (double x = xmin; x <= xmax; x += dx)
			plot(x, f.applyAsDouble(x));
	}
	

}

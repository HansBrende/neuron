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

	public final int top = 30, left = 30, bottom = 30, right = 30;
	private double lastx, lasty;
	private boolean lines = true, points = true;
	
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
			int length, int height, String xlabel, String ylabel) {
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
	
	private double xmid() {
		return left + length / 2;
	}
	
	private double ymid() {
		return top + height / 2;
	}
	
	public void reset() {
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, length + left + right, height + top + bottom);
		gc.setFill(Color.BLACK);
		gc.strokeLine(x(xmin), y(0), x(xmax), y(0));
		gc.strokeLine(x(0), y(ymin), x(0), y(ymax));
		double dx = (xmax - xmin) / 10, dy = (ymax - ymin) / 10, xmid = xmid(), ymid = ymid();
		for (int x = 0; x <= 10; x++) {
			double value = xmin + x * dx;
			double xdx = x(value);
			gc.strokeLine(xdx, ymid + 3, xdx, ymid - 3);
			if (value >= .1 || value <= -.1)
				gc.strokeText(String.format("%.1f", value), xdx, ymid + 12);
		}
		gc.setTextAlign(TextAlignment.RIGHT);
		for (int y = 0; y <= 10; y++) {
			double value = ymin + y * dy;
			double ydy = y(value);
			gc.strokeLine(xmid + 3, ydy, xmid - 3, ydy);
			if (value >= .1 || value <= -.1)
				gc.strokeText(String.format("%.1f", value), xmid - 8, ydy);
		}
		gc.setTextAlign(TextAlignment.LEFT);
		if (xlabel != null)
			gc.strokeText(xlabel, left + length + 5, y(0));
		gc.setTextAlign(TextAlignment.CENTER);
		if (ylabel != null)
			gc.strokeText(ylabel, x(0), top - 5);
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

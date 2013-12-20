package hans.plot;

import hans.matrix.Matrix;

import java.io.Serializable;

import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class PlotCanvas extends Group {
	
	public static void main(String[] args) {
		System.out.println(Serializable.class.isAssignableFrom(String.class));
	}
	
	
	public final Canvas canvas;
	public final GraphicsContext gc;

	private final double xmin, xmax, ymin, ymax;
	private final double length, height;
	private final double xtics, ytics;

	private final double top, left, bottom, right;
	private double lastx, lasty;
	private final boolean lines, points;
	private final double xscale, yscale;
	
	private String xlabel, ylabel, title;
	
	public void cutLine() {
		lastx = Double.NaN;
		lasty = Double.NaN;
	}
	

	public PlotCanvas(PlotParams params) {
		this.left = params.left;
		this.right = params.right;
		this.top = params.top;
		this.bottom = params.bottom;
		this.xscale = params.xscale;
		this.yscale = params.yscale;
		this.xmin = params.xmin;
		this.ymin = params.ymin;
		this.xmax = params.xmax;
		this.ymax = params.ymax;
		this.length = params.width;
		this.height = params.height;
		this.xlabel = params.xlabel;
		this.ylabel = params.ylabel;
		this.title = params.title;
		this.lines = params.lines;
		this.points = params.points;
		this.xtics = params.xtics;
		this.ytics = params.ytics;
		canvas = new Canvas(length + left + right, height + top + bottom);
		getChildren().add(canvas);
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
		double dx = (xmax - xmin) / xtics, dy = (ymax - ymin) / ytics;
		for (int x = 0; x <= xtics; x++) {
			double value = xmin + x * dx;
			double xdx = x(value);
			gc.strokeLine(xdx, y0 + 3, xdx, y0 - 3);
			double val = value * xscale;
			double vabs = Math.abs(val);
			if (value > 1E-16 || value < -1E-16) {
				String format = vabs >= 1000 || vabs < .001 ? "%.1e" : (vabs > 9 ? "%.1f" : "%.3f");
				gc.fillText(String.format(format, val), xdx, y0 + 12);
			}
		}
		gc.setTextAlign(TextAlignment.RIGHT);
		for (int y = 0; y <= ytics; y++) {
			double value = ymin + y * dy;
			double ydy = y(value);
			gc.strokeLine(x0 + 3, ydy, x0 - 3, ydy);
			double val = value * yscale;
			double vabs = Math.abs(val);
			if (value > 1E-16 || value < -1E-16) {
				String format = vabs >= 1000 || vabs < .001 ? "%.1e" : (vabs > 9 ? "%.1f" : "%.3f");
				gc.fillText(String.format(format, val), x0 - 8, ydy);
			}
		}
		gc.setTextAlign(TextAlignment.LEFT);
		if (xlabel != null)
			gc.strokeText(xlabel, left + length + 5, y(0));
		gc.setTextAlign(TextAlignment.CENTER);
		if (ylabel != null)
			gc.strokeText(ylabel, x(0), top - 15);
		if (title != null)
			gc.strokeText(title, left + length/2, top /3);
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

	public void plot(Matrix m) {
		reset();
		double[] points = m.getRow(0);
		for (int y = 1; y < m.rowCount(); y++) {
			double[] ys = m.getRow(y);
			for (int i = 0; i < points.length; i++)
				plot(points[i], ys[i]);
			cutLine();
		}
	}
	
	public void plotStats(double t, Matrix m) {
		plot(m);
		m = m.getSubMatrix(1, m.rowCount() - 1, 0, m.columnCount() - 1);
		double[] stats = new double[] {
				Double.POSITIVE_INFINITY, //min
				Double.NEGATIVE_INFINITY, //max
				0, //sum
				0, //count
				0, //found number
		};
		m.accumulate(stats, (x,y)->y<x?y:x, (x,y)->y>x?y:x, (x,y)->x+y, (x,y)->x + 1, (x,y)-> x + (Double.isNaN(y) ? 0 : 1));
		double count = stats[3], avg = stats[2] / count;
		double min = Double.NaN, max = Double.NaN;
		if (stats[4] > 0) {
			min = stats[0];
			max = stats[1];
		}
		TextAlignment ta = gc.getTextAlign();
		gc.setTextAlign(TextAlignment.LEFT);
		String format = Double.isNaN(t) ? "" : "t=" + statf(t) + " s";
		format += "     min=" + statf(min) + "; max=" + statf(max) + "; avg=" + statf(avg) + "; num points=" + count;
		gc.strokeText(Double.isNaN(t) ? String.format(format, min, max, avg) : String.format(format, t, min, max, avg), 10, height);
		gc.setTextAlign(ta);
	}
	
//	public void plotStats(Matrix m) {
//		reset();
//		double[] stats = plotGetStats(m);
//		TextAlignment ta = gc.getTextAlign();
//		gc.setTextAlign(TextAlignment.LEFT);
//		String[] formats = new String[stats.length];
//		for (int x = -1; x < formats.length - 1; x++) {
//			double value = x == -1 ? 0 : stats[x];
//			double vabs = Math.abs(value);
//			formats[x + 1] = vabs >= 1000000 || vabs < .001 ? "%.1e" : "%.3f";
//		}
//		String format = "     min=" + formats[1] + "; max=" + formats[2] + "; avg=" + formats[3] + "; num points=" + (int)stats[stats.length - 1];
//		gc.strokeText(String.format(format, stats[0], stats[1], stats[2]), 10, height);
//		gc.setTextAlign(ta);
//	}
//	
//	public double[] plotGetStats(Matrix m) {
//		double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY, sum = 0, count = 0;
//		boolean foundone = false;
//		double[] xs = m.getRow(0);
//		for (int row = 1; row < m.rowCount(); row++) {
//			for (int i = 0; i < xs.length; i++) {
//				double yval = m.getEntry(row, i);
//				plot(xs[i], yval);
//				foundone = foundone || !Double.isNaN(yval);
//				if (yval < min)
//					min = yval;
//				if (yval > max)
//					max = yval;
//				sum += yval;
//				count++;
//			}
//			cutLine();
//		}
//		return foundone ? new  double[] {min, max, sum / count, count} : new double[] {Double.NaN, Double.NaN, Double.NaN, count};
//	}
//	
	private static String statf(double d) {
		double abs = Math.abs(d);
		return abs >= 1000000 || abs < .001 ? "%.1e" : "%.3f";
	}

}
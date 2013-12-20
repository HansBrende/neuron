package graph;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.Base64;
import java.util.Iterator;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Stream;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;

import neuron.BiVector;

public abstract class Utils {
	
	public static void main(String[] args) {
		Biop op = new Biop(.01) {
			private static final long serialVersionUID = -4807036769149405103L;
			@Override
			public double applyAsDouble(double x, double t) {
				return Math.sin(x + t)/x;
			}
		}, op2 = new Biop(.1) {
			private static final long serialVersionUID = 6860730059474973969L;
			@Override
			public double applyAsDouble(double x, double t) {
				return Math.sin(x);
			}
		};
		plot(new PlotParams().lines().range(-6, 6, -1, 1).label("x", "sin(x + t)/x"), .0001, op, op2);	
	}
	
	private Utils() {
	}
	
	public static void plot(String filename, PlotParams params, BiVector... data) {
		Application.launch(Grapher.class, exportObject(params), exportObject(data), filename);
	}
	
	public static void plot(PlotParams params, BiVector...data) {
		plot("", params, data);
	}
	
	public static void plot(String filename, PlotParams params, double dx, DoubleUnaryOperator... op) {
		BiVector[] data = new BiVector[op.length];
		for (int x = 0; x < op.length; x++) {
			DoubleUnaryOperator o = op[x];
			data[x] = new BiVector(val -> o.applyAsDouble(val), params.xmin, params.xmax, dx);
		}
		plot(filename, params, data);
	}
	
	public static void plot(PlotParams params, double dx, DoubleUnaryOperator...op) {
		plot("", params, dx, op);
	}
		
	public static void plot(PlotParams params, TemporalFunction timeFunction) {
		Application.launch(Grapher.class, exportObject(params), exportObject(timeFunction));
	}
	
	public static void plot(PlotParams params, double dt, Biop... op) {
		TemporalFunction func = new TemporalFunction(dt) {

			private static final long serialVersionUID = -3398613328970885071L;

			public BiVector[] apply(double t) {
				BiVector[] vs = new BiVector[op.length];
				for (int x = 0; x < op.length; x++) {
					Biop o = op[x];
					vs[x] = new BiVector(val->o.applyAsDouble(val, t), params.xmin, params.xmax, o.dx);
				}
				return vs;
			}
		};
		plot(params, func);
	}
	
	private static final String hashcode = "0a918e72df340dc1239e87ba4";
	
	public static void exportPlot(String name, Iterable<InstantData> function, double tmax) {
		try {
			File file = new File(name);
			if (file.isFile()) {
				try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
					Object o = in.readObject();
					if (!hashcode.equals(o))
						throw new IllegalStateException();
				}
			} else
				file.createNewFile();
			try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
				out.writeDouble(tmax);
				for (InstantData v : function) {
					if (v.instant <= tmax)
						out.writeObject(v);
					else 
						break;
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public static void exportPlot(String name, DoubleFunction<BiVector[]> function, final double tmin, final double tmax, final double dt) {
		Iterable<InstantData> it = () -> new Iterator<InstantData>() {
			private double time = tmin;
			public InstantData next() {
				if (time > tmax)
					throw new IndexOutOfBoundsException();
				InstantData data = new InstantData(time, function.apply(time));
				time += dt;
				return data;
			}
			
			public boolean hasNext() {
				return time <= tmax;
			}
		};
		exportPlot(name, it, tmax);
	}
	
	public static void exportPlot(String name, double xmin, double xmax, double dx, double tmin, double tmax, double dt, DoubleBinaryOperator... ops) {
		exportPlot(name, t -> Stream.of(ops).map(op->new BiVector(x->op.applyAsDouble(x, t), xmin, xmax, dx)).toArray(i->new BiVector[i]), tmin, tmax, dt);
	}
	
	public static void exportPlot(String name, BiVector... data) {
		exportPlot(name, t->data, 0, 0, 1);
	}
	
	public static void exportPlot(String name, double xmin, double xmax, double dx, DoubleUnaryOperator...ops) {
		exportPlot(name, (BiVector[])Stream.of(ops).map(op->new BiVector(op, xmin, xmax, dx)).toArray(i->new BiVector[i]));
	}
	

	public static String exportObject(Object object) {
		try {
			ByteArrayOutputStream str = new ByteArrayOutputStream();
			ObjectOutputStream stream = new ObjectOutputStream(str);
			stream.writeObject(object);
			return Base64.getEncoder().encodeToString(str.toByteArray());
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	
	@SuppressWarnings("unchecked")
	public static <T> T importObject(String s) {
		try {
			byte[] bytes = Base64.getDecoder().decode(s);
			ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(bytes));
			return (T)stream.readObject();
		} catch (IOException | ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public static String debugString(Object o) {
		StringBuilder sb = new StringBuilder(o.getClass() + "[\n");
		for (Field f : o.getClass().getDeclaredFields()) {
			sb.append(f.getName()).append('=');
			try {
				sb.append(f.get(o));
			} catch (Exception e) {
				sb.append("Unknown");
			}
			sb.append("\n");
		}
		return sb.toString() + "]\n";
	}
	
	public static void export(Node node, File file) {
		WritableImage image = node.snapshot(new SnapshotParameters(), null);
	    try {
	        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
	    } catch (IOException e) {
	    	throw new IllegalArgumentException();
	    }
	}
	
	public static void export(Scene node, File file) {
		WritableImage image = node.snapshot(new WritableImage((int)node.getWidth(), (int)node.getHeight()));
	    try {
	        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
	    } catch (IOException e) {
	    	throw new IllegalArgumentException();
	    }
	}

}

package graph;

import java.io.Serializable;
import java.util.function.DoubleBinaryOperator;

public abstract class Biop implements DoubleBinaryOperator, Serializable {

	private static final long serialVersionUID = 4649334749975474644L;
	
	public final double dx;
	
	public Biop(double dx) {
		this.dx = dx;
	}

}

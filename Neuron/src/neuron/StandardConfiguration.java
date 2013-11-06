package neuron;

public class StandardConfiguration extends NeuronConfiguration {

	private static final double somaStart = Util.DENDRITE_LENGTH;
	private static final double dendriteB = Util.DISTAL_DENDRITE_RADIUS;
	private static final double dendriteMaxR = Util.PROXIMAL_DENDRITE_RADIUS;
	private static final double dendriteM = (dendriteMaxR - dendriteB)
			/ somaStart;

	private static final double somaR = Util.SOMA_RADIUS;
	private static final double somaR2 = somaR * somaR;
	private static final double somaRx1 = Math.sqrt(somaR2 - dendriteMaxR
			* dendriteMaxR);
	private static final double somaRx2 = Math.sqrt(somaR2 - Util.AXON_RADIUS
			* Util.AXON_RADIUS);
	private static final double soma_h = somaStart + somaRx1;
	private static final double axonStart = soma_h + somaRx2;
	private static final double firstSheathStart = axonStart
			+ Util.INITIAL_SEGMENT_LENGTH;

	public StandardConfiguration() {
		super(axonStart + Util.AXON_LENGTH);
	}

	@Override
	public double cytoRadius(double x) {
		if (x < 0)
			throw new IndexOutOfBoundsException();
		if (x < somaStart)
			return x * dendriteM + dendriteB;
		if (x < axonStart)
			return Math.sqrt(somaR2 - (x - soma_h) * (x - soma_h));
		if (x < length)
			return Util.AXON_RADIUS;
		throw new IndexOutOfBoundsException();
	}

	@Override
	public double cytoResistivity(double x) {
		return Util.CYTOPLASMIC_RESISTIVITY;
	}

	@Override
	public double membranePermittivity(double x) {
		return Util.MEMBRANE_PERMITTIVITY;
	}

	@Override
	public Region regionFor(double x) {
		if (x < 0)
			throw new IndexOutOfBoundsException();
		if (x < somaStart)
			return Region.DENDRITE;
		if (x < axonStart) {
			if (x < axonStart - .1 * somaRx2)
				return Region.HILLOCK;
			return Region.SOMA;
		}
		if (x < firstSheathStart)
			return Region.INITIAL_SEGMENT;
		x = x - firstSheathStart;
		while (x < length) {
			if (x < Util.SHEATH_LENGTH)
				return Region.MYELINATED_AXON;
			x = x - Util.SHEATH_LENGTH;
			if (x < Util.SHEATH_GAP)
				return Region.AXON_NODE;
			x = x - Util.SHEATH_GAP;
		}
		throw new IndexOutOfBoundsException();
	}

	@Override
	public double membraneWidth(double x) {
		if (x < 0)
			throw new IndexOutOfBoundsException();
		if (x < firstSheathStart)
			return Util.MEMBRANE_THICKNESS;
		x = x - firstSheathStart;
		while (x < length) {
			if (x < Util.SHEATH_LENGTH)
				return Util.SHEATH_WIDTH;
			x = x - Util.SHEATH_LENGTH;
			if (x < Util.SHEATH_GAP)
				return Util.MEMBRANE_THICKNESS;
			x = x - Util.SHEATH_GAP;
		}
		throw new IndexOutOfBoundsException();
	}

	@Override
	public double somaStart() {
		return somaStart;
	}

	public double axonStart() {
		return axonStart;
	}

	public double sheathGap() {
		return Util.SHEATH_GAP;
	}

	@Override
	public double I_inj(double x, double t) {
//		double length = Util.DENDRITE_LENGTH / 30;
//		double start = Util.DENDRITE_LENGTH / 2;
//		
//		if (x >= start && x < start + length && t < .1E-6)
//			return .02;
		return 0;
	}

	public double dx() {
		return Util.SHEATH_GAP / 3;
	}

}

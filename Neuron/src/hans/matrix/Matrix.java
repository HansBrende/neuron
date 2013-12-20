package hans.matrix;

import java.io.Serializable;
import java.util.List;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleUnaryOperator;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.linear.MatrixDimensionMismatchException;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.NonSquareMatrixException;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.DefaultRealMatrixPreservingVisitor;
import org.apache.commons.math3.linear.DefaultRealMatrixChangingVisitor;

public class Matrix implements Serializable {

	private static final long serialVersionUID = -9133319956655307670L;
	
	public final RealMatrix matrix;
	
	public Matrix(int rows, int cols) {
		this(MatrixUtils.createRealMatrix(rows, cols));
	}

	public Matrix(double[][] data) {
		this(MatrixUtils.createRealMatrix(data));
	}

	public Matrix(RealMatrix matrix) {
		this.matrix = matrix;
	}
	
	public Matrix(double xmin, double xmax, double dx, DoubleUnaryOperator...ops) {
		this(ops.length + 1, (int)Math.ceil((xmax - xmin)/dx));
		int cols = columnCount();
		double[] firstRow = row(cols).walk((row, col, val) -> xmin + dx*col).getRow(0);
		setRow(0, firstRow);
		for (int row = 0; row < ops.length; row++) {
			DoubleUnaryOperator op = ops[row];
			setRowMatrix(row + 1, row(cols).walk((r, col, val) -> op.applyAsDouble(firstRow[col])));
		}
	}
	
	public static Matrix columns(List<double[]> columns) {
		int colsize = columns.size();
		Matrix m = new Matrix(columns.get(0).length, colsize);
		for (int x = 0; x < colsize; x++)
			m.setColumn(x, columns.get(x));
		return m;
	}
	
	public static Matrix rows(List<double[]> rows) {
		int rowsize = rows.size();
		Matrix m = new Matrix(rowsize, rows.get(0).length);
		for (int x = 0; x < rowsize; x++)
			m.setRow(x, rows.get(x));
		return m;
	}
	
	public static Matrix column(double... column) {
		return new Matrix(MatrixUtils.createColumnRealMatrix(column));
	}
	
	public static Matrix row(double... row) {
		return new Matrix(MatrixUtils.createRowRealMatrix(row));
	}
	
	public static Matrix row(int length) {
		return row(new double[length]);
	}
	
	public static Matrix column(int length) {
		return column(new double[length]);
	}

	public Matrix visit(final DoubleConsumer d) {
		matrix.walkInOptimizedOrder(new DefaultRealMatrixPreservingVisitor() {
			public void visit(int row, int column, double value) {
				d.accept(value);
			}
		});
		return this;
	}

	public Matrix visit(final EntryConsumer d) {
		matrix.walkInOptimizedOrder(new DefaultRealMatrixPreservingVisitor() {
			public void visit(int row, int column, double value) {
				d.accept(row, column, value);
			}
		});
		return this;
	}

	public Matrix walk(final DoubleUnaryOperator d) {
		matrix.walkInOptimizedOrder(new DefaultRealMatrixChangingVisitor() {
			public double visit(int row, int column, double value) {
				return d.applyAsDouble(value);
			}
		});
		return this;
	}

	public Matrix walk(final EntryToDouble d) {
		matrix.walkInOptimizedOrder(new DefaultRealMatrixChangingVisitor() {
			public double visit(int row, int column, double value) {
				return d.apply(row, column, value);
			}
		});
		return this;
	}
	
	public Matrix op(final DoubleUnaryOperator d) {
		return copy().walk(d);
	}
	
	public Matrix op(final EntryToDouble d) {
		return copy().walk(d);
	}
	
	public Matrix op(DoubleBinaryOperator op, Matrix m) {
		int rows = rowCount(), cols = columnCount();
		if (rows != m.rowCount() || cols != m.columnCount())
			throw new MatrixDimensionMismatchException(m.rowCount(), m.columnCount(), rows, cols);
		double[][] data = getData().clone(), mdata = m.getData();
		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++)
				data[row][col] = op.applyAsDouble(data[row][col], mdata[row][col]);
		return new Matrix(data);
	}

	public boolean isSquare() {
		return matrix.isSquare();
	}

	public int rowCount() {
		return matrix.getRowDimension();
	}

	public int columnCount() {
		return matrix.getColumnDimension();
	}

	public Matrix copy() {
		return new Matrix(matrix.copy());
	}

	public Matrix add(Matrix m) throws MatrixDimensionMismatchException {
		return new Matrix(matrix.add(m.matrix));
	}

	public Matrix subtract(Matrix m) throws MatrixDimensionMismatchException {
		return new Matrix(matrix.subtract(m.matrix));
	}

	public Matrix times(Matrix m) throws DimensionMismatchException {
		return new Matrix(matrix.multiply(m.matrix));
	}
	
	public Matrix sctimes(Matrix m) {
		return op((x,y)->x*y, m);
	}

	public Matrix preMultiply(Matrix m)
			throws DimensionMismatchException {
		return new Matrix(matrix.preMultiply(m.matrix));
	}

	public Matrix power(int p) throws NotPositiveException,
			NonSquareMatrixException {
		return new Matrix(matrix.power(p));
	}

	public double[][] getData() {
		return matrix.getData();
	}

	public double getNorm() {
		return matrix.getNorm();
	}

	public double getFrobeniusNorm() {
		return matrix.getFrobeniusNorm();
	}

	public Matrix getSubMatrix(int startRow, int endRow, int startColumn,
			int endColumn) throws OutOfRangeException,
			NumberIsTooSmallException {
		return new Matrix(matrix.getSubMatrix(startRow, endRow, startColumn,
				endColumn));
	}

	public Matrix getSubMatrix(int[] selectedRows, int[] selectedColumns)
			throws NullArgumentException, NoDataException, OutOfRangeException {
		return new Matrix(matrix.getSubMatrix(selectedRows, selectedColumns));
	}

	public void copySubMatrix(int startRow, int endRow, int startColumn,
			int endColumn, double[][] destination) throws OutOfRangeException,
			NumberIsTooSmallException, MatrixDimensionMismatchException {
		matrix.copySubMatrix(startRow, endRow, startColumn, endColumn,
				destination);
	}

	public void copySubMatrix(int[] selectedRows, int[] selectedColumns,
			double[][] destination) throws OutOfRangeException,
			NullArgumentException, NoDataException,
			MatrixDimensionMismatchException {
		matrix.copySubMatrix(selectedRows, selectedColumns, destination);
	}

	public void setSubMatrix(double[][] subMatrix, int row, int column)
			throws NoDataException, OutOfRangeException,
			DimensionMismatchException, NullArgumentException {
		matrix.setSubMatrix(subMatrix, row, column);
	}

	public Matrix getRowMatrix(int row) throws OutOfRangeException {
		return new Matrix(matrix.getRowMatrix(row));
	}

	public void setRowMatrix(int row, Matrix m) throws OutOfRangeException,
			MatrixDimensionMismatchException {
		matrix.setRowMatrix(row, m.matrix);
	}

	public Matrix getColumnMatrix(int column) throws OutOfRangeException {
		return new Matrix(matrix.getColumnMatrix(column));
	}

	public void setColumnMatrix(int column, Matrix m)
			throws OutOfRangeException, MatrixDimensionMismatchException {
		matrix.setColumnMatrix(column, m.matrix);
	}

	public double[] getRow(int row) throws OutOfRangeException {
		return matrix.getRow(row);
	}

	public void setRow(int row, double[] array) throws OutOfRangeException,
			MatrixDimensionMismatchException {
		matrix.setRow(row, array);
	}

	public double[] getColumn(int column) throws OutOfRangeException {
		return matrix.getColumn(column);
	}

	public void setColumn(int column, double[] array)
			throws OutOfRangeException, MatrixDimensionMismatchException {
		matrix.setColumn(column, array);
	}

	public double getEntry(int row, int column) throws OutOfRangeException {
		return matrix.getEntry(row, column);
	}

	public void setEntry(int row, int column, double value)
			throws OutOfRangeException {
		matrix.setEntry(row, column, value);
	}

	public void addToEntry(int row, int column, double increment)
			throws OutOfRangeException {
		matrix.addToEntry(row, column, increment);
	}

	public void multiplyEntry(int row, int column, double factor)
			throws OutOfRangeException {
		matrix.multiplyEntry(row, column, factor);
	}

	public Matrix transpose() {
		return new Matrix(matrix.transpose());
	}

	public double getTrace() throws NonSquareMatrixException {
		return matrix.getTrace();
	}

	public double[] operate(double[] v) throws DimensionMismatchException {
		return matrix.operate(v);
	}

	public double[] preMultiply(double[] v) throws DimensionMismatchException {
		return matrix.preMultiply(v);
	}

	public double sum(DoubleUnaryOperator op) {
		double[] d = { 0 };
		visit(op == null ? v -> d[0] += v : v -> d[0] += op.applyAsDouble(v));
		return d[0];
	}
	
	public void accumulate(double[] initialValues, DoubleBinaryOperator...ops) {
		visit(v -> {
			for (int x = 0; x < initialValues.length; x++)
				initialValues[x] = ops[x].applyAsDouble(initialValues[x], v);
		});
	}
	
	public double[] accumulate(DoubleBinaryOperator...ops) {
		double[] doubs = new double[ops.length];
		accumulate(doubs);
		return doubs;
	}
	
	public double accumulate(double initialValue, DoubleBinaryOperator op) {
		double[] d = new double[] {initialValue};
		accumulate(d, op);
		return d[0];
	}
	
	public double accumulate(DoubleBinaryOperator op) {
		return accumulate(0, op);
	}
	
	public double sum() {
		return sum(null);
	}
	
	public String toString() {
		return matrix.toString();
	}

}

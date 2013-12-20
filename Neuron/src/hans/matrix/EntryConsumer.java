package hans.matrix;

public interface EntryConsumer {
	
	void accept(int row, int col, double val);

}

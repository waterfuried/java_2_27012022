package lesson2;

public class MyArrayDataException extends Exception {
    int row, col;

    MyArrayDataException(String message, int row, int col) {
        super(message + " [" + row + ", " + col + "]");
        setCellIndex(row, col);
    }

    final void setCellIndex (int row, int col) {
        this.row = row;
        this.col = col;
    }
}
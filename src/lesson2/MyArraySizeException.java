package lesson2;

public class MyArraySizeException extends IllegalArgumentException {
    MyArraySizeException() {
        super();
    }

    MyArraySizeException(String message) {
        super(message);
    }
}
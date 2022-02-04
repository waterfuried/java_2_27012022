package lesson2;

/*
	1. Напишите метод, на вход которого подаётся двумерный строковый массив размером 4х4,
	   при подаче массива другого размера необходимо бросить исключение MyArraySizeException.
	2. Далее метод должен пройтись по всем элементам массива, преобразовать в int, и просуммировать.
	   Если в каком-то элементе массива преобразование не удалось (например, в ячейке лежит символ
	   или текст вместо числа), должно быть брошено исключение MyArrayDataException, с детализацией,
	   в какой именно ячейке лежат неверные данные.
	3. В методе main() вызвать полученный метод, обработать возможные исключения MySizeArrayException
	   и MyArrayDataException, и вывести результат расчета.
*/
public class Main {
    final static int SIZE = 4;

    final static String ERR_MSG_WRONG_SIZE = "Размерность массива отличается от " + SIZE + "x" + SIZE;
    final static String ERR_MSG_WRONG_DATA = "некорректные данные в ячейке";

    static int sumArray (String[][] strings) throws MyArraySizeException, MyArrayDataException {
        if (strings == null || strings.length != SIZE) {
            throw new MyArraySizeException(ERR_MSG_WRONG_SIZE);
        } else {
            for (int i = 0; i < SIZE; i++) {
                if (strings[i] == null || strings[i].length != SIZE) {
                    throw new MyArraySizeException(ERR_MSG_WRONG_SIZE);
                }
            }
        }
        sum = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                try {
                    sum += Integer.parseInt(strings[i][j]);
                } catch (Exception ex) {
                    throw new MyArrayDataException(ERR_MSG_WRONG_DATA, i, j);
                }
            }
        }
        return sum;
    }

    static int sum;

    static boolean trySum (String[][] arr) {
        boolean bOkay = true;
        sum = 0;
        try {
            sum = sumArray(arr);
        } catch (Exception ex) {
            bOkay = false;
            System.err.println(ex.getClass().getSimpleName() + ": " + ex.getMessage());
        }
        return bOkay;
    }

    static void printSum () {
        System.out.println("Сумма элементов массива равна " + sum);
    }

    public static void main (String[] args) {
        String[][] strBad = {
                { "-1000", "4444" },
                { "0", "-4", "75" },
                { "-33", "+33", "0" },
                { "999" },
                { "-7", "123", "567" },
                { "890", "-777.0", "123.456", "0.1" },
                { "11", "22", "-3333" }
        };
        String[][] strOk = {
                { "-1000", "4444", "333", "-7777" },
                { "0", "-4", "75", "-2345" },
                { "-33", "+33", "+0", "-0" },
                { "999", "888", "-777.004", "-666" },
        };

        if (trySum(strOk)) {
            printSum();
            if (trySum(strBad)) {
                printSum();
            }
        }
    }
}
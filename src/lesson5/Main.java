package lesson5;

import java.util.*;
/*
1. Необходимо написать два метода, которые делают следующее:
	1) Создают одномерный длинный массив, например:

	static final int size = 10000000;
	static final int h = size / 2;
	float[] arr = new float[size];

	2) Заполняют этот массив единицами;
	3) Засекают время выполнения: long a = System.currentTimeMillis();
	4) Проходят по всему массиву и для каждой ячейки считают новое значение по формуле:
	arr[i] = (float)(arr[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
	5) Проверяется время окончания метода System.currentTimeMillis();
	6) В консоль выводится время работы: System.out.println(System.currentTimeMillis() - a);

	Отличие первого метода от второго:
		Первый просто бежит по массиву и вычисляет значения.
		Второй разбивает массив на два массива, в двух потоках высчитывает новые значения
        	и потом склеивает эти массивы обратно в один.

	Пример деления одного массива на два:

		System.arraycopy(arr, 0, a1, 0, h);
		System.arraycopy(arr, h, a2, 0, h);

	Пример обратной склейки:

		System.arraycopy(a1, 0, arr, 0, h);
		System.arraycopy(a2, 0, arr, h, h);

	Примечание:
		System.arraycopy() – копирует данные из одного массива в другой:
			System.arraycopy(массив-источник, откуда начинаем брать данные из массива-источника,
        				 массив-назначение, откуда начинаем записывать данные в массив-назначение,
					 сколько ячеек копируем)

	По замерам времени:
	- Для первого метода надо считать время только на цикл расчета:

		for (int i = 0; i < size; i++) {
			arr[i] = (float)(arr[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
		}

	- Для второго метода замеряете время разбивки массива на 2, просчета каждого из двух массивов и склейки.
*/
public class Main {
    static long testAdvantage (boolean singleThread) {
        final int SIZE = 10000000;
        final int HALFSIZE = SIZE / 2;

        float[] arr = new float[SIZE];
        Arrays.fill(arr, 1);
        long a = System.currentTimeMillis();

        if (singleThread) {
            // одним потоком
            for (int i = 0; i < SIZE; i++) {
                arr[i] = (float)(arr[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
            }
        } else {
            // двумя потоками:
            // 1) разделить массив на две части
            float[] a1 = new float[HALFSIZE], a2 = new float[HALFSIZE];
            System.arraycopy(arr, 0, a1, 0, HALFSIZE);
            System.arraycopy(arr, HALFSIZE, a2, 0, HALFSIZE);

            // 2) вычислить элементы двух массивов
            Thread t1 = new Thread(() -> {
                for (int i = 0; i < HALFSIZE; i++) {
                    a1[i] = (float)(arr[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
                }
            });
            Thread t2 = new Thread(() -> {
                for (int i = 0; i < HALFSIZE; i++) {
                    int j = HALFSIZE + i;
                    a2[i] = (float)(arr[j] * Math.sin(0.2f + j / 5) * Math.cos(0.2f + j / 5) * Math.cos(0.4f + j / 2));
                }
            });

            t1.start();
            t2.start();
            try {
                 t1.join();
                 t2.join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            // 3) склеить два массива в один
            System.arraycopy(a1, 0, arr, 0, HALFSIZE);
            System.arraycopy(a2, 0, arr, HALFSIZE, HALFSIZE);
        }

	    return System.currentTimeMillis() - a;
    }

    public static void main(String[] args) {
        System.out.println("Вычисление элементов массива...");
        System.out.println("\tодним потоком - " + (testAdvantage(true) / 1000f) + "с");
        System.out.println("\tдвумя потоками - " + (testAdvantage(false) / 1000f) + "с");
    }
}
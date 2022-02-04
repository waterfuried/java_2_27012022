package lesson1;

public interface Movable extends Jumpable, Runnable {
    // встречая препятствие, объект сам понимает, как его преодолеть - бежать или прыгать
    default boolean overcome (int typeof, int value) {
        switch (typeof) {
            case 0: return run(value);
            case 1: return jump(value);
            default: return idle();
        }
    }

    // отсутствие действий при встрече неизвестного препятствия
    default boolean idle() {
        System.out.print("не может совершать действий с неопознанным типом препятствия");
        return true;
    }

    // имя или обозначение движущегося
    String getDesignation();
}

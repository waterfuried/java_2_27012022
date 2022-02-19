package lesson6;

public interface Informative {
    int PORT = 8189; // список занятых портов - netstat -a
    String ADDRESS = "localhost";

    String LOGOUT = "/end";

    String ERR_SERVER_CONNECTION = "< ОШИБКА СВЯЗИ С СЕРВЕРОМ >";
    String ERR_NETWORK_CONNECTION = "СЕТЕВАЯ ОШИБКА СВЯЗИ";
    String ERR_CLOSE_SOCKET = "ОШИБКА ЗАКРЫТИЯ СОКЕТА";
    String ERR_PORT_BUSY = "ОШИБКА ЗАПУСКА СЕРВЕРА - ПОРТ %d УЖЕ ЗАНЯТ";

    default void alertNetworkProblem (String msg, Integer val, boolean networkError, boolean networkProblem) {
        if (networkError && !networkProblem)
            if (val == null)
                System.out.println(msg);
            else
                System.out.printf(msg + "\n", val);
    }

    // выводить свои сообщения в консоли зеленым цветом, полученные - серым
    default void printMessage(String msg, boolean mine) {
        System.out.print("\033[3" + (mine ? 2 : 7) + "m" + msg);
    }

    default boolean nonEmpty (String s) {
        return s != null && s.replaceAll("\\s", "").length() > 0;
    }

    void setupNetworkError(String msg, Integer val);
    void setupNetworkError(String msg);
    void setupNetworkProblem();
    void clearNetworkProblem();
}

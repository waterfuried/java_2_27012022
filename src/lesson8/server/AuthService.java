package lesson8.server;

public interface AuthService {
    /**
     * Метод получения никнейма по логину и паролю
     * @return null если учетная запись не найдена
     * @return nickname в противном случае
     * */
    String getNickname (String login, String password);

    /**
     * метод для регистрации учетной записи
     * @return true при успешной регистрации
     * @return false в противном случае (если логин/никнейм заняты)
     * */
    boolean registered (String login, String password, String nickname);
}
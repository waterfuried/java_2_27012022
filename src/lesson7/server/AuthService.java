package lesson7.server;

public interface AuthService {
    /**
     * Метод получения никнейма по логину и паролю
     * @return null если учетная запись не найдена
     * @return nickname в противном случае
     * */
    String getNickname (String login, String password);
}

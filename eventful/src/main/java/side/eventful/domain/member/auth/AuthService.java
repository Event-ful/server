package side.eventful.domain.member.auth;

public interface AuthService {
    void login(AuthCommand.Login command);
    void logout();
}

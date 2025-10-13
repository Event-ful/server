package side.eventful.domain.member.auth;

import side.eventful.domain.member.Member;

public interface AuthService {
    void login(AuthCommand.Login command);
    void logout();
    Member getAuthenticatedMember();
}

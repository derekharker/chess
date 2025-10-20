package dataaccess.interfaces;

public interface AuthDAO {
    String createAuth(String username);
    void clearAuths();
    String getUsernameFromAuth(String authToken);
    void deleteAuth(String authToken);
    boolean isVerifiedAuth(String authToken);
}

package dataaccess.interfaces;

import model.UserData;

public interface UserDAO {
    UserData createUser(UserData newUser);
    void clearUsers();
    boolean isVerifiedUser(String password, String username);
    boolean userExists(String username);
}
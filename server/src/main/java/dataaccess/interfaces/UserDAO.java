package dataaccess.interfaces;

import model.UserData;

public interface UserDAO {
    UserData createUser(UserData newUser);
    void clearUsers();
    boolean isVerifiedUser(String username, String password);
    UserData getUser(String username);
}
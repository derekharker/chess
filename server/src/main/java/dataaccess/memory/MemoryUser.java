package dataaccess.memory;

import dataaccess.interfaces.UserDAO;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUser implements UserDAO {

    private final Map<String, UserData> users = new HashMap<>();

    public MemoryUser() {
    }

    @Override
    public boolean isVerifiedUser(String username, String password) {

        UserData user = users.get(username);

        if (user == null) {
            return false;
        }

        return user.password().equals(password);
    }

    public void clearUsers() {
        users.clear();
    }

    public UserData createUser(UserData newUser) {

        users.put(newUser.username(), newUser);

        return newUser;
    }

    public UserData getUser(String username) {

        if (!users.containsKey(username)) {
            return null;
        }

        return users.get(username);
    }
}

package dataaccess.memory;

import model.UserData;

import dataaccess.interfaces.UserDAO;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryUser implements UserDAO {
    private Collection<UserData> userDataList = new ArrayList<>();

    public MemoryUser() {

    }

    @Override
    public boolean isVerifiedUser(String username, String password) {
        for (UserData currUser : userDataList) {
            if (currUser.username().equals(username) && currUser.password().equals(password)) {
                return true;
            }
        }
        return false;
    }

    public void clearUsers() {
        userDataList.clear();
    }

    public UserData createUser(UserData newUser) {
        userDataList.add(newUser);
        return newUser;
    }

    public boolean userExists(String username) {
        for (UserData currentUser : userDataList) {
            if (currentUser.username().equals(username)) {
                return true;
            }
        };
        return false;
    }
}

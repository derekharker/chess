package dataaccess.memory;

import dataaccess.interfaces.AuthDAO;
import model.AuthData;

import java.util.UUID;
import java.util.ArrayList;
import java.util.Collection;

public class MemoryAuth implements AuthDAO {
    private Collection<AuthData> authList = new ArrayList<>();

    public MemoryAuth() {

    }

    @Override
    public String createAuth(String username) {
        String newAuth = UUID.randomUUID().toString();
        AuthData newAuthor = new AuthData(username, newAuth);
        authList.add(newAuthor);
        return newAuth;
    }

    @Override
    public boolean isVerifiedAuth(String authToken) {
        for (AuthData auth: authList) {
            System.out.println("Entered Auth Token is: " + authToken + "\n Current Auth Token is: " + auth.authToken());
            if (auth.authToken().equals(authToken)){
                return true;
            }
        }
    }
}

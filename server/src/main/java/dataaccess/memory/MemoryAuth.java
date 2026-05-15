package dataaccess.memory;

import dataaccess.interfaces.AuthDAO;
import model.AuthData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemoryAuth implements AuthDAO {

    private final Map<String, String> authTokens = new HashMap<>();

    public MemoryAuth() {
    }

    @Override
    public String createAuth(String username) {

        String token = UUID.randomUUID().toString();

        authTokens.put(token, username);

        return token;
    }

    public void clearAuths() {
        authTokens.clear();
    }

    @Override
    public String getUsernameFromAuth(String authToken) {

        if (!authTokens.containsKey(authToken)) {
            return null;
        }

        return authTokens.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {

        if (authTokens.containsKey(authToken)) {
            authTokens.remove(authToken);
        }
    }

    @Override
    public boolean isVerifiedAuth(String authToken) {

        for (String token : authTokens.keySet()) {

            System.out.println(
                    "Entered Auth Token is: " + authToken +
                            "\nCurrent Auth Token is: " + token
            );

            if (token.equals(authToken)) {
                return true;
            }
        }

        return false;
    }
}

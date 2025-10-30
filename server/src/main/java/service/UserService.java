package service;

import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.UserDAO;
import model.UserData;
import request.LoginRequest;
import request.RegisterRequest;
import response.LoginResponse;
import response.LogoutResponse;
import response.RegisterResponse;

public class UserService {

    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        if (loginRequest.username() == null || loginRequest.password() == null) {
            return new LoginResponse(null, null, ErrorMessages.BADREQUEST);
        }

        try {
            if (!userDAO.isVerifiedUser(loginRequest.username(), loginRequest.password())) {
                return new LoginResponse(null, null, ErrorMessages.UNAUTHORIZED);
            }
            System.out.println("Chillin out here");
            return new LoginResponse(loginRequest.username(), authDAO.createAuth(loginRequest.username()), null);

        } catch (Exception e) {
            System.out.println("Database error in login: " + e.getMessage());
            throw new RuntimeException("Error: Database connection failed", e);
        }
    }


    public RegisterResponse register(RegisterRequest regRequest) {
        System.out.println("Trying to register!");

        if (regRequest.username() == null || regRequest.password() == null || regRequest.email() == null) {
            return new RegisterResponse(null, null, ErrorMessages.BADREQUEST);
        }
        if (userDAO.userExists(regRequest.username())) {
            return new RegisterResponse(null, null, ErrorMessages.ALREADYTAKEN);
        }
        UserData testUser = userDAO.createUser(new UserData(regRequest.username(), regRequest.password(), regRequest.email()));
        if (testUser.username() == null) {
            return new RegisterResponse(null, null, ErrorMessages.SQLERROR);
        }
        String authToken = authDAO.createAuth(regRequest.username());

        if (authToken == null) {
            return new RegisterResponse(null, null, ErrorMessages.SQLERROR);
        }

        return new RegisterResponse(regRequest.username(), authToken, null);
    }

    public LogoutResponse logout(String authToken) {
        try {
            if (!authDAO.isVerifiedAuth(authToken)) {
                return new LogoutResponse(ErrorMessages.UNAUTHORIZED);
            }
            authDAO.deleteAuth(authToken);
            return new LogoutResponse(null);
        } catch (Exception e) {
            System.err.println("Database error in logout: " + e.getMessage());
            throw new RuntimeException("Error: Database connection failed", e);
        }
    }


}

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

        if (!userDAO.isVerifiedUser(loginRequest.username(), loginRequest.password())) {
            return new LoginResponse(null, null, ErrorMessages.UNAUTHORIZED);
        }
        return new LoginResponse(loginRequest.username(), authDAO.createAuth(loginRequest.username()), null);
    }

    public RegisterResponse register(RegisterRequest regRequest) {
        if (regRequest.username() == null || regRequest.password() == null || regRequest.email() == null) {
            return new RegisterResponse(null, null, ErrorMessages.BADREQUEST);
        }
        if (userDAO.userExists(regRequest.username())) {
            return new RegisterResponse(null, null, ErrorMessages.ALREADYTAKEN);
        }
        userDAO.createUser(new UserData(regRequest.username(), regRequest.password(), regRequest.email()));
        String authToken = authDAO.createAuth(regRequest.username());

        return new RegisterResponse(regRequest.username(), authToken, null);
    }

    //logout
    public LogoutResponse logout(String authToken) {
        if (!authDAO.isVerifiedAuth(authToken)) {
            return new LogoutResponse(ErrorMessages.UNAUTHORIZED);
        }
        authDAO.deleteAuth(authToken);
        return new LogoutResponse(null);
    }


}

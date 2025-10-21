package handler;

import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;

public class JoinGameHandler {

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public JoinGameHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }


}

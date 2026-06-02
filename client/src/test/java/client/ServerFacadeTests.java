package client;

import org.junit.jupiter.api.*;
import server.Server;
import ui.ClientException;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clear() throws Exception {
        facade.clear();
    }

    @Test
    void registerSuccess() throws ClientException {
        var authData = facade.register("player1", "password", "p1@email.com");

        Assertions.assertNotNull(authData);
        Assertions.assertEquals("player1", authData.username());
        Assertions.assertNotNull(authData.authToken());
        Assertions.assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void clearSuccess() throws ClientException {
        Assertions.assertDoesNotThrow(() -> facade.clear());
    }

    @Test
    void registerFailUsernameTaken() throws ClientException {
        facade.register("p1", "passwd", "email@gmail.com");

        Assertions.assertThrows(ClientException.class, () -> {
            facade.register("p1", "mdmd", "email222@gmail.com");
        });
    }

}

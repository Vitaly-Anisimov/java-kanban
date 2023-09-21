package manager.http;

import exception.ClientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class KVTaskClientTest {

    KeyValueClient kvTaskClient;
    KVServer kvServer;

    @BeforeEach
    public void setup() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        kvTaskClient = new KVTaskClient("http://localhost:" + KVServer.PORT);
    }

    @Test
    public void testGetInterruptedException() {
        kvServer.stop();
        assertThrows(ClientException.class, () -> {
            KeyValueClient testClient = new KVTaskClient("http://localhost:" + KVServer.PORT);
        });
    }

}
package manager.client;

import exception.KVTaskInterruptedOrIOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.KVServer;

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
        assertThrows(KVTaskInterruptedOrIOException.class, () -> {
            KeyValueClient testClient = new KVTaskClient("http://localhost:" + KVServer.PORT);
        });
    }

}
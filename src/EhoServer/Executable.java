package EhoServer;

import java.io.IOException;
import java.net.Socket;

public interface Executable {
    void execute(String message, Socket clientSocket) throws IOException;
}

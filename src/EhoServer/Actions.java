package EhoServer;

import java.io.IOException;
import java.net.Socket;

public enum Actions implements Executable{
    CHANGE_NAME("/name"){
        @Override
        public void execute(String message, Socket clientSocket) throws IOException {
            ConnectionHandling.changeName(message, clientSocket);
        }
    },
    WHO_IN_CHAT("/list"){
        @Override
        public void execute(String message, Socket clientSocket) throws IOException {
            ConnectionHandling.whoInChat(clientSocket);
        }
    },
    PRIVATE_MESSAGE("/whisper"){
        @Override
        public void execute(String message, Socket clientSocket) throws IOException {
            ConnectionHandling.privateMessage(message);
        }
    };


    private final String actionName;

    Actions(String actionName) {
        this.actionName = actionName;
    }

    public static void actionDo(String message, Socket clientSocket) throws IOException {
        boolean isDone = false;
        for (Actions action: Actions.values()) {
            if(action.actionName.equals(ConnectionHandling.takeFistWordInMessage(message))){
                action.execute(message, clientSocket);
                isDone = true;
                break;
            }
        }
        if(!isDone){
            ConnectionHandling.sendResponseToAll(message, clientSocket);
        }
    }
}

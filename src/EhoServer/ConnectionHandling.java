package EhoServer;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ConnectionHandling {
    private static Map<Socket, PrintWriter> connectedSocketsWriters = new HashMap<>();
    private static Map<Integer, String> clientPortsNames = new HashMap<>();
    private static List<String> clientNames = new ArrayList<>(List.of("Bob", "Bill", "Mary"));



    public static void handle(Socket clientSocket){
        giveName(clientSocket);
        System.out.println(clientSocket);
        try (Scanner reader = getReader(clientSocket); PrintWriter writer = getWriter(clientSocket)){
            saveSocketAndWriter(clientSocket, writer);
            sendResponse("Привет " + getGivenName(clientSocket), writer);
            while (true){
                String message = reader.nextLine();
                if(isEmptyMsg(message) || isQuitMsg(message)){
                    break;
                }
                Actions.actionDo(message, clientSocket);
            }
        }catch (NoSuchElementException e){
            System.out.printf("Клиент %s закрыл соединение!", getClientName(clientSocket));
            takeGivenName(clientSocket);
            removeSocketAndWriter(clientSocket);
        }catch (IOException e){
            System.out.printf("Клиент отключился: %s%n", getClientName(clientSocket));
            takeGivenName(clientSocket);
            removeSocketAndWriter(clientSocket);
        }
    }
    private static PrintWriter getWriter(Socket socket) throws IOException{
        OutputStream stream = socket.getOutputStream();
        return new PrintWriter(stream);
    }
    private static Scanner getReader(Socket socket) throws IOException{
        InputStream stream = socket.getInputStream();
        InputStreamReader input = new InputStreamReader(stream, StandardCharsets.UTF_8);
        return new Scanner(input);
    }
    private static boolean isQuitMsg(String message){
        return "bye".equalsIgnoreCase(message);
    }
    private static boolean isEmptyMsg(String message){
        return message == null || message.isBlank();
    }
    private synchronized static void giveName(Socket clientSocket){
        String name = nameCheck(clientSocket);
        clientPortsNames.put(clientSocket.getPort(), name);
        clientNames.remove(name);
    }
    private synchronized static void takeGivenName(Socket clientSocket){
        clientNames.add(clientPortsNames.get(clientSocket.getPort()));
        clientPortsNames.remove(clientSocket.getPort());
    }
    private synchronized static String getGivenName(Socket clientSocket){
        return clientPortsNames.get(clientSocket.getPort());
    }

    private synchronized static String nameCheck(Socket clientSocket){
        if(clientNames.size() == 0){
            return "Name-" + clientSocket.getPort();
        }
        else {
            return clientNames.get(0);
        }
    }
    private synchronized static boolean haveName(String name){
        for (String clientName: clientNames) {
            if(clientName.equals(name)){
                return true;
            }
        }
        return false;
    }

    private synchronized static String getClientName(Socket clientSocket){
        return clientPortsNames.get(clientSocket.getPort());
    }

    private synchronized static void saveSocketAndWriter(Socket clientSocket, PrintWriter writer){
        connectedSocketsWriters.put(clientSocket, writer);
    }
    private synchronized static void removeSocketAndWriter(Socket clientSocket){
        connectedSocketsWriters.remove(clientSocket);
    }

    private static synchronized void sendResponse(String response, PrintWriter writer) throws IOException{
        writer.write(response);
        writer.write(System.lineSeparator());
        writer.flush();
    }
    protected static synchronized void sendResponseToAll(String message, Socket senderSocket) throws IOException {
        for (Socket socket: connectedSocketsWriters.keySet()) {
            if(socket.getPort() != senderSocket.getPort()){
                sendResponse(getGivenName(senderSocket) + ": " + message, connectedSocketsWriters.get(socket));
            }
        }
    }
    protected synchronized static void changeName(String message, Socket clientSocket) throws IOException {
        List<String> listMessage = removeFirsWordInMessage(message);
        boolean nameIs = haveName(listMessage.get(0));

        if(!nameIs){
            sendResponse("Вы теперь известны как " + listMessage.get(0), connectedSocketsWriters.get(clientSocket));
            String messageToSend = "Пользователь " + getGivenName(clientSocket) + " теперь известен как " + listMessage.get(0);
            sendResponseToAll(messageToSend, clientSocket);
            clientNames.add(clientPortsNames.get(clientSocket.getPort()));
            clientPortsNames.put(clientSocket.getPort(), listMessage.get(0));
        }else {
            sendResponse("Такое имя уже есть", connectedSocketsWriters.get(clientSocket));
        }

    }
    protected synchronized static void whoInChat(Socket clientSocket) throws IOException {
        StringBuilder whoInChat = new StringBuilder();
        for (String name: clientPortsNames.values()) {
            whoInChat.append(name).append(" ");
        }
        sendResponse(whoInChat.toString(), connectedSocketsWriters.get(clientSocket));
    }

    protected synchronized static void privateMessage(String message) throws IOException {
        List<String> listMessage = removeFirsWordInMessage(message);
        Integer port = null;
        Socket recipient = null;

        Set<Map.Entry<Integer, String>> mapEntry = clientPortsNames.entrySet();
        for (Map.Entry<Integer, String> entry: mapEntry) {
            if(listMessage.get(0).equals(entry.getValue())){
                port = entry.getKey();
            }
        }
        for (Socket socket: connectedSocketsWriters.keySet()) {
            if(socket.getPort() == port){
                recipient = socket;
            }
        }

        assert recipient != null;
        PrintWriter writer = getWriter(recipient);
        listMessage.remove(0);
        sendResponse(convertListToString(listMessage), writer);
    }
    private static synchronized List<String> convertStringToList(String string){
        return new ArrayList<>(Arrays.asList(string.split("\\s")));
    }
    protected static synchronized String takeFistWordInMessage(String message){
        List<String> listMessage = convertStringToList(message);
        return listMessage.get(0);
    }
    private static synchronized List<String> removeFirsWordInMessage(String message){
        List<String> listMessage = convertStringToList(message);
        listMessage.remove(0);
        return listMessage;
    }
    private static synchronized String convertListToString(List<String> list){
        StringBuilder message = new StringBuilder();
        list.forEach(message::append);
        return message.toString();
    }
}

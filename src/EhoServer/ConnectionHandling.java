package EhoServer;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ConnectionHandling {
    private static Map<Integer, String> clientPortsNames = new HashMap<>();
    private static List<String> clientNames = List.of("Bob", "Bill", "Mary", "Jack");


    //метод совершает работу с клиентом через сокет к которому он подключился
    public static void handle(Socket clientSocket){
        assignName(clientSocket);
        //получаем поток input и создаем поток output из сокета
        try (Scanner reader = getReader(clientSocket); PrintWriter writer = getWriter(clientSocket)){
            //метод позволяет отправить сообщение через объект типа PrintWriter
            sendResponse("Привет " + getClientName(clientSocket), writer);
            while (true){
                //считываем сообщение с помощью сканера
                String message = reader.nextLine();
                if(isEmptyMsg(message) || isQuitMsg(message)){
                    break;
                }
                //отправляем ответ
                sendResponse(message.toUpperCase(), writer);
            }
        }catch (NoSuchElementException e){
            System.out.printf("Клиент %s закрыл соединение!", getClientName(clientSocket));
        }catch (IOException e){
            System.out.printf("Клиент отключился: %s%n", getClientName(clientSocket));
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
    private static void sendResponse(String response, PrintWriter writer) throws IOException{
        writer.write(response);
        writer.write(System.lineSeparator());
        writer.flush();
    }

    private static void assignName(Socket clientSocket){
        String name = nameCheck(clientSocket);
        clientPortsNames.put(clientSocket.getPort(), name);
        clientNames.remove(name);
    }

    private static String nameCheck(Socket clientSocket){
        if(clientNames.isEmpty()){
            return "Name-" + clientSocket.getPort();
        }
        else {
            return clientNames.get(new Random().nextInt(clientNames.size() - 1));
        }
    }

    private static String getClientName(Socket clientSocket){
        return clientPortsNames.get(clientSocket.getPort());
    }
}

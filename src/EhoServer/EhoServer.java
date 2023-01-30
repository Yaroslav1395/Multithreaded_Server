package EhoServer;

import Actions.Actions;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EhoServer {
    private final int port;
    private final ExecutorService poll = Executors.newCachedThreadPool();
    private EhoServer(int port) {
        this.port = port;
    }
    public static EhoServer bindToPort(int port){
        return new EhoServer(port);
    }
    public void run(){
        //создаем объект ServerSocket для того, чтобы получить сокет
        try(ServerSocket server = new ServerSocket(port)){
            while (!server.isClosed()){
                //ставим сокет в режим прослушивания сети
                Socket clientSocket = server.accept();
                //если к сокету подключились, передаем работу в пул потоков
                // через функциональный интерфейс runnable
                poll.submit(() -> ConnectionHandling.handle(clientSocket));

            }
        }
        catch (NoSuchElementException e){
            System.out.println("Операция поиска элемента в объекте завершилась неудачей");
        }
        catch (IOException e){
            String msg = "Вероятнее всего порт %s занят";
            System.out.printf(msg, port);
            e.printStackTrace();
        }
    }
}


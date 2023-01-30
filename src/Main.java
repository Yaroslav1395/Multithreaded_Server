import EhoServer.EhoServer;

public class Main {
    public static void main(String[] args) {
        EhoServer.bindToPort(8788).run();
    }
}
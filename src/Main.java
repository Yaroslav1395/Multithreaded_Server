import EhoServer.EhoServer;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        EhoServer.bindToPort(8788).run();
    }
}
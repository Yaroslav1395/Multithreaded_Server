package Actions;

import java.time.Instant;
import java.time.LocalTime;

public enum Actions implements Performable{
    REVERSE("reverse"){
        @Override
        public String execute(String massage) {
            massage = massage.toLowerCase().replace("reverse", "");
            return new StringBuilder(massage).reverse().toString();
        }
    },
    DATE("date"){
        @Override
        public String execute(String massage) {
            return Instant.now().toString();
        }
    },
    TIME("time"){
        @Override
        public String execute(String massage) {
            return LocalTime.now().toString();
        }
    },
    UPPER("upper"){
        @Override
        public String execute(String massage) {
            return massage.toUpperCase().replace("UPPER", "");
        }
    },
    BYE("bye"){
        @Override
        public String execute(String massage) {
            System.out.println("Соединение закрыто");
            return massage;
        }
    };
    private String key;

    Actions(String key) {
        this.key = key;
    }

    public static String chooseAction(String massage){
        String[] strArray = massage.split("\\s");
        for (Actions action: Actions.values()) {
            if(action.key.equals(strArray[0].toLowerCase())){
                return action.execute(massage);
            }
        }
        return massage;
    }
}

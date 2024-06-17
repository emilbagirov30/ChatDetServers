import java.util.Random;

public class ChatDetDetector {
    static Random detector = new Random();
    static int result;
    public static String analysis (String message){
        result = detector.nextInt(2);
        return String.valueOf(result);
    }
}

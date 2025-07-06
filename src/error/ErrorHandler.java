// === error/ErrorHandler.java ===
package error;

public class ErrorHandler {
    public static void fatal(String message) {
        System.err.println("❌ HATA: " + message);
        System.exit(1);
    }
}

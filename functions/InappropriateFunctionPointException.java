package functions;

public class InappropriateFunctionPointException extends Exception {
    
    public InappropriateFunctionPointException() {
        super("Некорректная операция с точкой функции");
    }
    
    public InappropriateFunctionPointException(String message) {
        super(message);
    }
    
    public InappropriateFunctionPointException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public InappropriateFunctionPointException(double x, double leftX, double rightX) {
        super("Координата X = " + x + " должна находиться в интервале (" + leftX + ", " + rightX + ")");
    }
    
    
    public InappropriateFunctionPointException(double x) {
        super("Точка с координатой X = " + x + " уже существует в функции");
    }
    
    public InappropriateFunctionPointException(double newX, double prevX, double nextX, String info) {
        super("Координата X = " + newX + " должна быть между " + prevX + " и " + nextX + " (" + info + ")");
    }
}
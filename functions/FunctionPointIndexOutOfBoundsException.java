package functions;

public class FunctionPointIndexOutOfBoundsException extends IndexOutOfBoundsException {
    public FunctionPointIndexOutOfBoundsException() {
        super("Выход за границы набора точек функции");
    }
    
    public FunctionPointIndexOutOfBoundsException(String message) {
        super(message);
    }
    
    public FunctionPointIndexOutOfBoundsException(int index) {
        super("Индекс " + index + " выходит за границы набора точек функции");
    }
    
    public FunctionPointIndexOutOfBoundsException(int index, int minIndex, int maxIndex) {
        super("Индекс " + index + " выходит за допустимый диапазон [" + minIndex + ", " + maxIndex + "]");
    }
}
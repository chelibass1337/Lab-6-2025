package functions;

import functions.meta.Composition;
import functions.meta.Mult;
import functions.meta.Power;
import functions.meta.Scale;
import functions.meta.Shift;
import functions.meta.Sum;

public final class Functions {
    public static Function shift(Function f, double shiftX, double shiftY) {
        return new Shift(f, shiftX, shiftY);
    }

    public static Function scale(Function f, double scaleX, double scaleY) {
        return new Scale(f, scaleX, scaleY);
    }
    
    public static Function power(Function f, double power) {
        return new Power(f, power); 
    }

    public static Function sum(Function f1, Function f2) {
        return new Sum(f1, f2);
    }

    public static Function mult(Function f1, Function f2) {
        return new Mult(f1, f2);
    }

    public static Function composition(Function f1, Function f2) {
        return new Composition(f1, f2);
    }

    public static double integrate(Function function, double leftX, double rightX, double step) {
        if (step <= 0) {
            throw new IllegalArgumentException("Шаг дискретизации должен быть положительным: " + step);
        }

        if (leftX >= rightX) {
            throw new IllegalArgumentException(
                String.format("Левая граница (%.6f) должна быть меньше правой (%.6f)", leftX, rightX));
        }

        double funcLeftBorder = function.getLeftDomainBorder();
        double funcRightBorder = function.getRightDomainBorder();
        
        if (leftX < funcLeftBorder || rightX > funcRightBorder) {
            throw new IllegalArgumentException(
                String.format("Интервал [%.6f, %.6f] выходит за границы области определения [%.6f, %.6f]",
                    leftX, rightX, funcLeftBorder, funcRightBorder));
        }

        double integralSum = 0.0;
        double currentX = leftX;

        while (currentX < rightX) {
            double currentStep = Math.min(step, rightX - currentX);
            double nextX = currentX + currentStep;
            
            double y1 = function.getFunctionValue(currentX);
            double y2 = function.getFunctionValue(nextX);
            
            if (Double.isNaN(y1) || Double.isNaN(y2)) {
                throw new IllegalArgumentException(
                    String.format("Функция возвращает NaN в точке x=%.6f или x=%.6f", currentX, nextX));
            }
            
            integralSum += (y1 + y2) * currentStep / 2.0;
            currentX = nextX;
        }
        
        return integralSum;
    }
}
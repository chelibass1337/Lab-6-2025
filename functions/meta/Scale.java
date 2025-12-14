package functions.meta;

import functions.Function;

public class Scale implements Function {
    private Function Func;
    private double scaleX, scaleY;

    public Scale(Function Func, double scaleX, double scaleY) {
        if (Func == null){
            throw new IllegalArgumentException("Функция не может быть null");
        }
        this.Func = Func;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    @Override
    public double getLeftDomainBorder() {
        if (scaleX > 0){
            return Func.getLeftDomainBorder() * scaleX;
        } else if (scaleX < 0){
            return Func.getRightDomainBorder() * scaleX;
        } else {
            return Double.NaN;
        }

    }

    @Override
    public double getRightDomainBorder() {
        if (scaleX > 0){
            return Func.getRightDomainBorder() * scaleX;
        } else if (scaleX < 0){
            return Func.getLeftDomainBorder() * scaleX;
        } else {
            return Double.NaN;
        }
    }

    @Override
    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()){
            return Double.NaN;
        }
        return Func.getFunctionValue(x * scaleX) * scaleY;
    }
}
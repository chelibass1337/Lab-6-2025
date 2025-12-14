package functions.meta;

import functions.Function;

public class Shift implements Function {
    Function Func;
    double shiftX, shiftY;

    public Shift(Function Func, double shiftX, double shiftY){
        if (Func == null){
            throw new IllegalArgumentException("Функция не может быть null");
        }
        this.Func = Func;
        this.shiftX = shiftX;
        this.shiftY = shiftY;
    }

    @Override
    public double getLeftDomainBorder(){
        return Func.getLeftDomainBorder() - shiftX; 
    }

    @Override
    public double getRightDomainBorder(){
        return Func.getRightDomainBorder() - shiftY;  
    }

    @Override
    public double getFunctionValue(double x){
        return Func.getFunctionValue(x + shiftX) + shiftY;
    }
    
}
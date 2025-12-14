package functions.meta;

import functions.Function;

public class Mult implements Function {
    private Function Func1;
    private Function Func2;

    public Mult(Function Func1, Function Func2) throws IllegalArgumentException {
        if (Func1 == null || Func2 == null){
            throw new IllegalArgumentException("Функции не могут быть null");
        }
        this.Func1 = Func1;
        this.Func2 = Func2;
    }

    @Override
    public double getLeftDomainBorder() {
        return Math.max(Func1.getLeftDomainBorder(), Func2.getLeftDomainBorder());
    }

    @Override
    public double getRightDomainBorder() {
        return Math.min(Func1.getRightDomainBorder(), Func2.getRightDomainBorder());
    }
    
    @Override
    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()){
            return Double.NaN;
        }
        return Func1.getFunctionValue(x) * Func2.getFunctionValue(x);
    }
}
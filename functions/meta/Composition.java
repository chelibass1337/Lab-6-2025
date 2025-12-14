package functions.meta;

import functions.Function;

public class Composition implements Function {
    private Function Func1;
    private Function Func2;

    public Composition(Function Func2, Function Func1) {
        if (Func1 == null || Func2 == null){
            throw new IllegalArgumentException("Функции не могут быть null");
        }
        this.Func1 = Func1;
        this.Func2 = Func2;
    }

    @Override
    public double getLeftDomainBorder() {
        return Func1.getLeftDomainBorder();
    }

    @Override
    public double getRightDomainBorder() {
        return Func2.getRightDomainBorder();
    }

    @Override
    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()){
            return Double.NaN;
        }
        return Func1.getFunctionValue(Func2.getFunctionValue(x));
    }
}
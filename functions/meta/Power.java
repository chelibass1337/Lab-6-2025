package functions.meta;

import functions.Function;

public class Power implements Function {
    private Function Func;
    private double cons;

    public Power(Function Func, double cons) {
        if (Func == null){
            throw new IllegalArgumentException("Функция не могут быть null");
        }
        this.Func = Func;
        this.cons = cons;
    }

    @Override
    public double getLeftDomainBorder() {
        return Func.getLeftDomainBorder();
    }

    @Override
    public double getRightDomainBorder() {
        return Func.getRightDomainBorder();
    }

    @Override
    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()){
            return Double.NaN;
        }
        return Math.pow(Func.getFunctionValue(x), cons);
    }
}
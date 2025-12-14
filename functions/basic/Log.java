package functions.basic;

import functions.Function;

public class Log implements Function {
    private double base;
    
    public Log(double base) {
        if (base <= 0 || base == 1) {
            throw new IllegalArgumentException("Основание логарифма должно быть > 0 и ≠ 1");
        }
        this.base = base;
    }
    
    @Override
    public double getLeftDomainBorder() {
        return 0;
    }
    
    @Override
    public double getRightDomainBorder() {
        return Double.POSITIVE_INFINITY;
    }
    
    @Override
    public double getFunctionValue(double x) {
        if (x <= 0) {
            return Double.NaN;
        }
        return Math.log(x) / Math.log(base);
    }
    
    public double getBase() {
        return base;
    }
    
    @Override
    public String toString() {
        return String.format("log_%.4f(x)", base);
    }
}
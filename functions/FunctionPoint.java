package functions;

import java.util.Objects;

public class FunctionPoint implements Cloneable{
    private double x;
    private double y;
    
    public FunctionPoint(double x, double y){
        this.x = x;
        this.y = y;
    }
    
    public FunctionPoint(FunctionPoint point){
        this.x = point.x;
        this.y = point.y;
    }
    
    public FunctionPoint(){
        x = 0;
        y = 0;
    }
    
    public void setX(double x){
        this.x = x;
    }
    
    public void setY(double y){
        this.y = y;
    }
    
    public double getX(){
        return x;
    }
    
    public double getY(){
        return y;
    }
    @Override
    public String toString() {
        return "(" + x + "; " + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FunctionPoint otherPoint = (FunctionPoint) o;
        return Math.abs(this.x - otherPoint.x) < 1e-9 && Math.abs(this.y - otherPoint.y) < 1e-9;
    }

   @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public FunctionPoint clone(){
        return new FunctionPoint(this.x, this.y);
    }

}
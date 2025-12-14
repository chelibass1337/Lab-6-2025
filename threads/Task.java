package threads;

import functions.Function;


public class Task {
    private Function function;
    private double leftBorder;
    private double rightBorder;
    private double step;
    private int taskCount;
    private volatile boolean taskReadyForIntegration = false;

    public Task(int taskCount) {
        this.taskCount = taskCount;
    }

    public Task() {
        this.taskCount = 0;
    }

    public Function getFunction() {
        return function;
    }

    public synchronized void setFunction(Function function) {
        this.function = function;
        this.taskReadyForIntegration = true;
        notify();
    }

    public double getLeftBorder() {
        return leftBorder;
    }

    public void setLeftBorder(double leftBorder) {
        this.leftBorder = leftBorder;
    }

    public double getRightBorder() {
        return rightBorder;
    }

    public void setRightBorder(double rightBorder) {
        this.rightBorder = rightBorder;
    }

    public double getStep() {
        return step;
    }

    public void setStep(double step) {
        this.step = step;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    public double getTaskCount() {
        return taskCount;
    }
    
    public synchronized boolean isTaskReadyForIntegration() {
        return taskReadyForIntegration;
    }
    
    public synchronized void resetTaskReadiness() {
        this.taskReadyForIntegration = false;
    }
}
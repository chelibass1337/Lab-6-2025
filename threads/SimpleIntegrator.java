package threads;

import functions.Functions;

public class SimpleIntegrator implements Runnable {
    private final Task task;
    
    public SimpleIntegrator(Task task) {
        this.task = task;
    }
    
    @Override
    public void run() {
        System.out.println("SimpleIntegrator: начало работы");
        
        for (int i = 0; i < task.getTaskCount(); i++) {
            try {
                synchronized (task) {
                    while (task.getFunction() == null) {
                        task.wait();
                    }
                    
                    double left = task.getLeftBorder();
                    double right = task.getRightBorder();
                    double step = task.getStep();
                    
                    double result = Functions.integrate(task.getFunction(), left, right, step);
                    
                    System.out.printf("(Integrator %d) на [%.4f, %.4f], шаг=%.6f Result=%.6f\n",
                        i, left, right, step, result);
                    
                    task.setFunction(null);
                    
                    task.notifyAll();
                }
                
                
            } catch (InterruptedException e) {
                System.out.println("SimpleIntegrator прерван");
                Thread.currentThread().interrupt();
                return;
            } catch (IllegalArgumentException e) {
                System.out.printf("(Integrator %d) ОШИБКА: %s\n", i, e.getMessage());
            }
        }
        
        System.out.println("SimpleIntegrator: завершение работы");
    }
}
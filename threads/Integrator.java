package threads;
import functions.Functions;
import java.util.concurrent.Semaphore;

public class Integrator extends Thread {
    private final Task task;
    private final Semaphore dataReady;
    private final Semaphore dataProcessed;

    public Integrator(Task task, Semaphore dataReady, Semaphore dataProcessed) {
        this.task = task;
        this.dataReady = dataReady;
        this.dataProcessed = dataProcessed;
    }

    @Override
    public void run() {
        System.out.println("Integrator: начало работы");
        
        for (int i = 0; i < task.getTaskCount(); i++) {
            try {
                dataReady.acquire();
                
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }
                
                double left = task.getLeftBorder();
                double right = task.getRightBorder();
                double step = task.getStep();


                    double result = Functions.integrate(task.getFunction(), left, right, step);
                    System.out.printf("(Integrator %d) на [%.6f, %.6f], шаг=%.6f result = %.6f \n", 
                        i, left, right, step, result);
                
                
                dataProcessed.release();
                
                
            } catch (InterruptedException e) {
                System.out.println("Integrator прерван");
                Thread.currentThread().interrupt();
                return;
            } catch (IllegalArgumentException e) {
                System.out.printf("(Integrator %d) ОШИБКА вычисления: %s\n", i, e.getMessage());
                dataProcessed.release(); 
            }
        }
        
        System.out.println("Integrator: завершение работы");
    }
}
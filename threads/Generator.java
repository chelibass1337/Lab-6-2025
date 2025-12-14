package threads;
import functions.*;
import functions.basic.*;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Generator extends Thread {
    private final Task task;
    private final Semaphore dataReady;
    private final Semaphore dataProcessed;
    private final Random random = new Random();

    public Generator(Task task, Semaphore dataReady, Semaphore dataProcessed) {
        this.task = task;
        this.dataReady = dataReady;
        this.dataProcessed = dataProcessed;
    }

    @Override
    public void run() {
        System.out.println("Generator: начало работы");
        
        for (int i = 0; i < task.getTaskCount(); i++) {
            try {
                dataProcessed.acquire();
                
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }
                
                Function function;
                int funcType = random.nextInt(3);
                switch (funcType) {
                    case 0:
                        function = new Exp();
                        break;
                    case 1:
                        function = new Sin();
                        break;
                    case 2:
                        double base = 1 + random.nextDouble() * 9;
                        function = new Log(base);
                        break;
                    default:
                        function = new Exp();
                }
                
                double left = random.nextDouble() * 5;
                double right = left + 1 + random.nextDouble() * 4;
                double step = 0.01 + random.nextDouble() * 0.1;
                
                task.setFunction(function);
                task.setLeftBorder(left);
                task.setRightBorder(right);
                task.setStep(step);
                
                System.out.printf("(Generator %d) Создано: %s на [%.6f, %.6f], шаг=%.6f\n",
                    i, function.getClass().getSimpleName(), left, right, step);
                
                dataReady.release();
                
                
            } catch (InterruptedException e) {
                System.out.println("Generator прерван");
                Thread.currentThread().interrupt();
                return;
            } catch (IllegalArgumentException e) {
                System.out.printf("(Generator %d) ОШИБКА генерации: %s\n", i, e.getMessage());
                dataReady.release(); // Все равно освобождаем семафор
            }
        }
        
        System.out.println("Generator: завершение работы");
    }
}
package threads;

import functions.Function;
import functions.basic.Log;

import java.util.Random;

public class SimpleGenerator implements Runnable {
    private final Task task;
    private final Random random = new Random();
    private volatile boolean running = true;
    
    public SimpleGenerator(Task task) {
        this.task = task;
    }
    
    @Override
    public void run() {
        System.out.println("SimpleGenerator: начало работы (поток: " + Thread.currentThread().getName() + ")");
        
        for (int i = 0; i < task.getTaskCount() && running; i++) {
            try {
                double base = 1.0 + random.nextDouble() * 9.0;
                Function logFunction = new Log(base);
                
                double left = random.nextDouble() * 100.0;
                
                double right = 100.0 + random.nextDouble() * 100.0;
                
                double step = random.nextDouble();
                
                synchronized (task) {
                    task.setFunction(logFunction);
                    task.setLeftBorder(left);
                    task.setRightBorder(right);
                    task.setStep(step);
                    

                    System.out.printf("(SimpleGenerator %d) Задание: левая граница = %.6f, правая граница = %.6f, шаг = %.6f (поток: %s)%n",
                        i + 1, left, right, step, Thread.currentThread().getName());
                }
                
                Thread.sleep(5);
                
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка при генерации: " + e.getMessage());
            } catch (InterruptedException e) {
                System.out.println("SimpleGenerator: прерван во время сна");
                Thread.currentThread().interrupt();
                running = false;
                break;
            }
        }
        
        if (running) {
            System.out.println("Конец simpleThreads (SimpleGenerator завершил работу)");
        } else {
            System.out.println("SimpleGenerator: работа прервана досрочно");
        }
    }
    
    public void stop() {
        running = false;
    }
}
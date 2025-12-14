import functions.*;
import functions.basic.*;
import java.io.*;
import java.util.Random;
import java.util.concurrent.Semaphore;
import threads.*;

public class Main {

    public static void main(String[] args) throws IOException, FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException, ClassNotFoundException, InterruptedException {
        System.out.println("\t\t Нахождение приближенного решения интеграла");
        Intergral();
        
        //System.out.println("\n\t\t Генерирование и решение Task-ов последовательно\n");
        //nonThread();
        
        //System.out.println("\n\t\t Генерирование и решение Task-ов c помощью SimpleGenerator и SimpleIntegrator\n");
        //simpleThreads();

        System.out.println("\n\t\t Генерирование и решение Task-ов c помощью Generator и Integrator\n");
        complicatedThreads();

    }

    public static void Intergral(){
        Function exp = new Exp();
        int leftX = 0;
        int rightX = 1;
        double step = 0.000125;
        double theoreticalResult = Math.E - 1;
        double result = Functions.integrate(exp, leftX, rightX, step);

        while(Math.abs(theoreticalResult - result) > 1e-8){
            result = Functions.integrate(exp, leftX, rightX, step);
            step = step / 2;
        }
        System.out.printf("\nЗначение интерграла = %.7f c точностью 1e-7 и с шагом = %.7f", result, step);
        System.out.printf("\nЗначение интеграла %.7f\n", theoreticalResult);
    }
    
    public static void nonThread() {
        System.out.println("Последовательная версия (nonThread)");

        Random random = new Random();
        Task task = new Task();
        task.setTaskCount(100);

        for (int i = 0; i < task.getTaskCount(); i++) {
            try {
                double base = 1 + random.nextDouble() * 9;
                Function logFunction = new Log(base);

                double left = random.nextDouble() * 100;
                double right = 100 + random.nextDouble() * 100;
                double step = random.nextDouble();

                task.setFunction(logFunction);
                task.setLeftBorder(left);
                task.setRightBorder(right);
                task.setStep(step);

                System.out.printf("Задание: левая граница = %.6f, правая граница = %.6f, шаг = %.6f%n", left, right, step);
                double result = Functions.integrate(logFunction, left, right, step);
                System.out.printf("Результат: левая граница = %.6f, правая граница = %.6f, шаг = %.6f, значение интеграла =  %.6f", left, right, step, result);
                System.out.printf("\n---------\n");
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка при итерации " + (i + 1) + ": " + e.getMessage());
            }
        }
        System.out.println("Конец последовательной версии(nonThread)");
    }

    public static void simpleThreads() throws InterruptedException{
        Task task = new Task();
        task.setTaskCount(100);

        Thread simpleGenerator = new Thread(new SimpleGenerator(task));
        Thread simpleIntegrator = new Thread(new SimpleIntegrator(task));

        simpleGenerator.setPriority(Thread.MIN_PRIORITY);
        simpleIntegrator.setPriority(Thread.MAX_PRIORITY);

        simpleIntegrator.start();
        simpleGenerator.start();
    }

    public static void complicatedThreads() throws InterruptedException{
        Task task = new Task();
        task.setTaskCount(100);
        Semaphore dataReady = new Semaphore(0);
        Semaphore dataProcessed = new Semaphore(1);

        Generator generator = new Generator(task, dataReady, dataProcessed);
        Integrator integrator = new Integrator(task, dataReady, dataProcessed);
        
        generator.start();
        integrator.start();

        try {

        Thread.sleep(50);
        
        generator.interrupt();
        integrator.interrupt();
        
        generator.join();
        integrator.join();
            
        } catch (InterruptedException e) {
            System.out.println("Main был остановлен");
        }
    }
}
    
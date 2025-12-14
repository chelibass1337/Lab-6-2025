package functions;

import java.io.*;

public class ArrayTabulatedFunction implements TabulatedFunction, Externalizable {
    private static final int DEFAULT_CAPACITY = 16;
    private FunctionPoint[] points;
    private int pointsCount;
    private static final double EPSILON = 1e-10;

    // Конструктор по умолчанию для Externalizable
    public ArrayTabulatedFunction() {
        initArrays(DEFAULT_CAPACITY);
        pointsCount = 0;
    }

    // Конструктор из массива точек
    public ArrayTabulatedFunction(FunctionPoint[] points) throws IllegalArgumentException {
        if (points.length < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2, получено: " + points.length);
        }
        
        // Проверка упорядоченности по X
        for (int i = 1; i < points.length; i++) {
            if (points[i].getX() < points[i - 1].getX() - EPSILON) {
                throw new IllegalArgumentException("Массив не упорядочен по координатам X");
            }
        }
        
        initArrays(Math.max(points.length * 2, DEFAULT_CAPACITY));
        this.pointsCount = points.length;
        
        for (int i = 0; i < points.length; i++) {
            this.points[i] = new FunctionPoint(points[i]);
        }
    }

    // Конструктор 1: равномерное разбиение с нулевыми значениями
    public ArrayTabulatedFunction(double leftX, double rightX, int pointsCount) 
            throws IllegalArgumentException {
        
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница (" + leftX + 
                    ") должна быть меньше правой (" + rightX + ")");
        }
        
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2, получено: " + pointsCount);
        }
        
        initArrays(Math.max(pointsCount * 2, DEFAULT_CAPACITY));
        this.pointsCount = pointsCount;

        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            points[i] = new FunctionPoint(x, 0);
        }
    }

    // Конструктор 2: равномерное разбиение с заданными значениями Y
    public ArrayTabulatedFunction(double leftX, double rightX, double[] values) 
            throws IllegalArgumentException {
        
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница (" + leftX + 
                    ") должна быть меньше правой (" + rightX + ")");
        }
        
        if (values.length < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2, получено: " + values.length);
        }
        
        initArrays(Math.max(values.length * 2, DEFAULT_CAPACITY));
        this.pointsCount = values.length;

        double step = (rightX - leftX) / (values.length - 1);
        for (int i = 0; i < values.length; i++) {
            double x = leftX + i * step;
            points[i] = new FunctionPoint(x, values[i]);
        }
    }

    // Инициализация массива
    private void initArrays(int capacity) {
        points = new FunctionPoint[capacity];
        for (int i = 0; i < capacity; i++) {
            points[i] = new FunctionPoint();
        }
    }

    // ==================== Реализация методов интерфейса TabulatedFunction ====================

    @Override
    public double getLeftDomainBorder() {
        if (pointsCount == 0) {
            return Double.NaN;
        }
        return points[0].getX();
    }

    @Override
    public double getRightDomainBorder() {
        if (pointsCount == 0) {
            return Double.NaN;
        }
        return points[pointsCount - 1].getX();
    }

    @Override
    public double getFunctionValue(double x) {
        if (pointsCount == 0) {
            return Double.NaN;
        }
        
        double leftBorder = getLeftDomainBorder();
        double rightBorder = getRightDomainBorder();
        
        if (x < leftBorder - EPSILON || x > rightBorder + EPSILON) {
            return Double.NaN;
        }

        // Проверка на точное совпадение с границами
        if (Math.abs(x - leftBorder) < EPSILON) {
            return points[0].getY();
        }
        if (Math.abs(x - rightBorder) < EPSILON) {
            return points[pointsCount - 1].getY();
        }

        // Поиск интервала для интерполяции
        for (int i = 0; i < pointsCount - 1; i++) {
            double x1 = points[i].getX();
            double x2 = points[i + 1].getX();
            
            if (x >= x1 - EPSILON && x <= x2 + EPSILON) {
                // Точное совпадение с одной из точек интервала
                if (Math.abs(x - x1) < EPSILON) {
                    return points[i].getY();
                }
                if (Math.abs(x - x2) < EPSILON) {
                    return points[i + 1].getY();
                }
                
                // Линейная интерполяция
                double y1 = points[i].getY();
                double y2 = points[i + 1].getY();
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
        }
        
        return Double.NaN;
    }

    @Override
    public int getPointsCount() {
        return pointsCount;
    }

    @Override
    public FunctionPoint getPoint(int index) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index, 0, pointsCount - 1);
        }
        return new FunctionPoint(points[index]);
    }

    @Override
    public void setPoint(int index, FunctionPoint point) 
            throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException {
        
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index, 0, pointsCount - 1);
        }
        
        double newX = point.getX();
        double currentX = points[index].getX();
        
        // Если X не изменился, просто меняем Y
        if (Math.abs(newX - currentX) < EPSILON) {
            points[index].setY(point.getY());
            return;
        }
        
        // Проверка границ для новой координаты X
        if (index > 0) {
            double prevX = points[index - 1].getX();
            if (newX <= prevX + EPSILON) {
                throw new InappropriateFunctionPointException(
                    "Новая координата X (" + newX + ") должна быть больше предыдущей (" + prevX + ")");
            }
        }
        
        if (index < pointsCount - 1) {
            double nextX = points[index + 1].getX();
            if (newX >= nextX - EPSILON) {
                throw new InappropriateFunctionPointException(
                    "Новая координата X (" + newX + ") должна быть меньше следующей (" + nextX + ")");
            }
        }
        
        // Устанавливаем новые координаты
        points[index].setX(newX);
        points[index].setY(point.getY());
    }

    @Override
    public double getPointX(int index) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index, 0, pointsCount - 1);
        }
        return points[index].getX();
    }

    @Override
    public void setPointX(int index, double x) 
            throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException {
        
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index, 0, pointsCount - 1);
        }
        
        double currentX = points[index].getX();
        if (Math.abs(x - currentX) < EPSILON) {
            return; // X не изменился
        }
        
        // Проверка границ для новой координаты X
        if (index > 0) {
            double prevX = points[index - 1].getX();
            if (x <= prevX + EPSILON) {
                throw new InappropriateFunctionPointException(
                    "Новая координата X (" + x + ") должна быть больше предыдущей (" + prevX + ")");
            }
        }
        
        if (index < pointsCount - 1) {
            double nextX = points[index + 1].getX();
            if (x >= nextX - EPSILON) {
                throw new InappropriateFunctionPointException(
                    "Новая координата X (" + x + ") должна быть меньше следующей (" + nextX + ")");
            }
        }
        
        points[index].setX(x);
    }

    @Override
    public double getPointY(int index) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index, 0, pointsCount - 1);
        }
        return points[index].getY();
    }

    @Override
    public void setPointY(int index, double y) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index, 0, pointsCount - 1);
        }
        points[index].setY(y);
    }

    @Override
    public void deletePoint(int index) 
            throws FunctionPointIndexOutOfBoundsException, IllegalStateException {
        
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index, 0, pointsCount - 1);
        }
        
        if (pointsCount <= 2) {
            throw new IllegalStateException("Невозможно удалить точку: минимальное количество точек - 2");
        }
        
        // Удаление точки со сдвигом
        if (index < pointsCount - 1) {
            System.arraycopy(points, index + 1, points, index, pointsCount - index - 1);
        }
        
        // Очищаем последнюю ячейку
        points[pointsCount - 1] = new FunctionPoint();
        pointsCount--;
    }

    @Override
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        // Поиск позиции для вставки
        int insertIndex = 0;
        while (insertIndex < pointsCount && 
               point.getX() > points[insertIndex].getX() + EPSILON) {
            insertIndex++;
        }

        // Проверка на дублирование координаты X
        if (insertIndex < pointsCount && 
            Math.abs(point.getX() - points[insertIndex].getX()) < EPSILON) {
            throw new InappropriateFunctionPointException(
                "Точка с координатой X = " + point.getX() + " уже существует");
        }

        // Увеличение массива при необходимости
        if (pointsCount == points.length) {
            int newCapacity = points.length * 2;
            FunctionPoint[] newArray = new FunctionPoint[newCapacity];
            System.arraycopy(points, 0, newArray, 0, pointsCount);
            
            for (int i = pointsCount; i < newCapacity; i++) {
                newArray[i] = new FunctionPoint();
            }
            
            points = newArray;
        }

        // Сдвиг элементов для освобождения места
        if (insertIndex < pointsCount) {
            System.arraycopy(points, insertIndex, points, insertIndex + 1, pointsCount - insertIndex);
        }
        
        // Вставка новой точки (создаем копию)
        points[insertIndex] = new FunctionPoint(point);
        pointsCount++;
    }

    public void printPoints() {
        System.out.println("Табулированная функция (массив, " + pointsCount + " точек):");
        
        if (pointsCount == 0) {
            System.out.println("  Функция не содержит точек");
            return;
        }
        
        for (int i = 0; i < pointsCount; i++) {
            System.out.printf("  [%d]: (%.4f, %.4f)%n", i, points[i].getX(), points[i].getY());
        }
    }

    // ==================== Переопределенные методы Object ====================

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < pointsCount; i++) {
            sb.append("(")
              .append(String.format("%.3f", points[i].getX()))
              .append("; ")
              .append(String.format("%.3f", points[i].getY()))
              .append(")");
            if (i < pointsCount - 1) {
                sb.append(", ");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TabulatedFunction)) return false;
        
        if (o instanceof ArrayTabulatedFunction) {
            // Оптимизированное сравнение для объектов того же класса
            ArrayTabulatedFunction other = (ArrayTabulatedFunction) o;
            
            if (this.pointsCount != other.pointsCount) return false;
            
            for (int i = 0; i < pointsCount; i++) {
                // Сравниваем координаты с учетом погрешности EPSILON
                if (Math.abs(this.points[i].getX() - other.points[i].getX()) > EPSILON ||
                    Math.abs(this.points[i].getY() - other.points[i].getY()) > EPSILON) {
                    return false;
                }
            }
            return true;
        } else {
            // Общее сравнение для любых реализаций TabulatedFunction
            TabulatedFunction other = (TabulatedFunction) o;
            
            if (this.pointsCount != other.getPointsCount()) return false;
            
            for (int i = 0; i < pointsCount; i++) {
                try {
                    FunctionPoint thisPoint = this.getPoint(i);
                    FunctionPoint otherPoint = other.getPoint(i);
                    
                    if (Math.abs(thisPoint.getX() - otherPoint.getX()) > EPSILON ||
                        Math.abs(thisPoint.getY() - otherPoint.getY()) > EPSILON) {
                        return false;
                    }
                } catch (FunctionPointIndexOutOfBoundsException e) {
                    return false; // Не должно происходить, но для надежности
                }
            }
            return true;
        }
    }

    @Override
    public int hashCode() {
        int result = pointsCount; // Включаем количество точек для различия функций с разным числом точек
        
        for (int i = 0; i < pointsCount; i++) {
            // Используем хэш-код точек, но преобразуем double к int для хэширования
            // Округляем значения для учета погрешности
            long xBits = Double.doubleToLongBits(points[i].getX());
            long yBits = Double.doubleToLongBits(points[i].getY());
            
            int xHash = (int)(xBits ^ (xBits >>> 32));
            int yHash = (int)(yBits ^ (yBits >>> 32));
            
            // Комбинируем хэши с помощью XOR
            result = result ^ xHash ^ yHash;
        }
        
        return result;
    }

    @Override
    public TabulatedFunction clone() {
        // Создаем копию массива точек
        FunctionPoint[] pointsCopy = new FunctionPoint[pointsCount];
        for (int i = 0; i < pointsCount; i++) {
            pointsCopy[i] = new FunctionPoint(points[i]); // Глубокое копирование
        }
        
        // Создаем новый объект с копией точек
        try {
            return new ArrayTabulatedFunction(pointsCopy);
        } catch (IllegalArgumentException e) {
            // Не должно происходить, так как мы копируем валидные данные
            return null;
        }
    }

    // ==================== Реализация методов Externalizable ====================

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(pointsCount);
        for (int i = 0; i < pointsCount; i++) {
            out.writeDouble(points[i].getX());
            out.writeDouble(points[i].getY());
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        pointsCount = in.readInt();
        initArrays(Math.max(pointsCount * 2, DEFAULT_CAPACITY));
        
        for (int i = 0; i < pointsCount; i++) {
            double x = in.readDouble();
            double y = in.readDouble();
            points[i] = new FunctionPoint(x, y);
        }
    }
}
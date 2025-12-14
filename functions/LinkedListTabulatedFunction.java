package functions;

import java.io.Serializable;

public class LinkedListTabulatedFunction implements TabulatedFunction, Serializable {
    private class FunctionNode {
        private FunctionPoint point;
        private FunctionNode prev;
        private FunctionNode next;
        
        public FunctionNode(FunctionPoint point, FunctionNode prev, FunctionNode next) {
            this.point = point;
            this.prev = prev;
            this.next = next;
        }

        public FunctionPoint getPoint() {
            return point;
        }

        public void setPoint(FunctionPoint point) {
            this.point = point;
        }

        public FunctionNode getPrev() {
            return prev;
        }

        public void setPrev(FunctionNode prev) {
            this.prev = prev;
        }

        public FunctionNode getNext() {
            return next;
        }

        public void setNext(FunctionNode next) {
            this.next = next;
        }
    }
    
    private FunctionNode head;
    private int pointsCount;
    private static final double EPSILON = 1e-9;
    
    public LinkedListTabulatedFunction(FunctionPoint[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }
        for (int i = 1; i < points.length; i++) {
            if (points[i].getX() < points[i - 1].getX()) {
                throw new IllegalArgumentException("Массив не упорядочен по координатам X");
            }
        }
        pointsCount = 0;
        head = new FunctionNode(null, null, null);
        head.setNext(head);
        head.setPrev(head);
        for (FunctionPoint point : points) {
            addNodeToTail(new FunctionPoint(point));
        }
    }

    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница больше или равна правой");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }
        this.pointsCount = 0;
        head = new FunctionNode(null, null, null);
        head.setNext(head);
        head.setPrev(head);
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            addNodeToTail(new FunctionPoint(leftX + step * i, 0));
        }
    }

    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница больше или равна правой");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }
        this.pointsCount = 0;
        head = new FunctionNode(null, null, null);
        head.setNext(head);
        head.setPrev(head);
        double step = (rightX - leftX) / (values.length - 1);
        for (int i = 0; i < values.length; i++) {
            addNodeToTail(new FunctionPoint(leftX + step * i, values[i]));
        }
    }

    private FunctionNode getNodeByIndex(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " вне границ");
        }

        FunctionNode current;
        if (index < pointsCount / 2) {
            current = head.getNext();
            for (int i = 0; i < index; i++) {
                current = current.getNext();
            }
        } else {
            current = head;
            for (int i = pointsCount; i > index; i--) {
                current = current.getPrev();
            }
        }
        return current;
    }
    
    private FunctionNode addNodeToTail(FunctionPoint point) {
        FunctionNode newNode = new FunctionNode(point, head.getPrev(), head);
        FunctionNode tail = head.getPrev();
        tail.setNext(newNode);
        head.setPrev(newNode);
        pointsCount++;
        return newNode;
    }

    private FunctionNode addNodeByIndex(int index, FunctionPoint point) {
        if (index < 0 || index > pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " вне границ");
        }
        if (index == pointsCount) {
            return addNodeToTail(point);
        }
        FunctionNode nextNode = getNodeByIndex(index);
        FunctionNode prevNode = nextNode.getPrev();
        FunctionNode newNode = new FunctionNode(point, prevNode, nextNode);
        prevNode.setNext(newNode);
        nextNode.setPrev(newNode);
        pointsCount++;
        return newNode;
    }

    private FunctionNode deleteNodeByIndex(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " вне границ");
        }
        FunctionNode nodeToDelete = getNodeByIndex(index);
        FunctionNode prevNode = nodeToDelete.getPrev();
        FunctionNode nextNode = nodeToDelete.getNext();
        prevNode.setNext(nextNode);
        nextNode.setPrev(prevNode);
        pointsCount--;
        return nodeToDelete;
    }
    
    @Override
    public double getLeftDomainBorder() {
        if (pointsCount == 0) return Double.NaN;
        return head.getNext().getPoint().getX();
    }
    
    @Override
    public double getRightDomainBorder() {
        if (pointsCount == 0) return Double.NaN;
        return head.getPrev().getPoint().getX();
    }

    @Override
    public double getFunctionValue(double x) {
        if (pointsCount == 0) return Double.NaN;
        
        double left = getLeftDomainBorder();
        double right = getRightDomainBorder();
        
        if (x < left - EPSILON || x > right + EPSILON) {
            return Double.NaN;
        }
        
        FunctionNode current = head.getNext();
        while (current != head) {
            FunctionNode next = current.getNext();
            if (next == head) break;
            
            double x1 = current.getPoint().getX();
            double x2 = next.getPoint().getX();
            
            if (x >= x1 - EPSILON && x <= x2 + EPSILON) {
                if (Math.abs(x - x1) < EPSILON) return current.getPoint().getY();
                if (Math.abs(x - x2) < EPSILON) return next.getPoint().getY();
                
                double y1 = current.getPoint().getY();
                double y2 = next.getPoint().getY();
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
            current = next;
        }
        return Double.NaN;
    }

    @Override
    public int getPointsCount() {
        return pointsCount;
    }
    
    @Override
    public FunctionPoint getPoint(int index) {
        return new FunctionPoint(getNodeByIndex(index).getPoint());
    }

    @Override
    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        FunctionNode node = getNodeByIndex(index);
        double newX = point.getX();
        
        FunctionNode prev = node.getPrev();
        FunctionNode next = node.getNext();
        
        if (prev != head && newX <= prev.getPoint().getX() + EPSILON) {
            throw new InappropriateFunctionPointException("Некорректная координата X");
        }
        if (next != head && newX >= next.getPoint().getX() - EPSILON) {
            throw new InappropriateFunctionPointException("Некорректная координата X");
        }
        
        node.setPoint(new FunctionPoint(point));
    }

    @Override
    public double getPointX(int index) {
        return getNodeByIndex(index).getPoint().getX();
    }

    @Override
    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        FunctionNode node = getNodeByIndex(index);
        
        FunctionNode prev = node.getPrev();
        FunctionNode next = node.getNext();
        
        if ((prev != head && x <= prev.getPoint().getX() + EPSILON) ||
            (next != head && x >= next.getPoint().getX() - EPSILON)) {
            throw new InappropriateFunctionPointException("Некорректная координата X");
        }
        
        node.getPoint().setX(x);
    }

    @Override
    public double getPointY(int index) {
        return getNodeByIndex(index).getPoint().getY();
    }

    @Override
    public void setPointY(int index, double y) {
        getNodeByIndex(index).getPoint().setY(y);
    }
    
    @Override
    public void deletePoint(int index) {
        if (pointsCount <= 2) {
            throw new IllegalStateException("Невозможно удалить точку: минимум 2 точки");
        }
        deleteNodeByIndex(index);
    }

    @Override
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        if (pointsCount == 0 || point.getX() > getRightDomainBorder() + EPSILON) {
            addNodeToTail(new FunctionPoint(point));
            return;
        }
        
        if (point.getX() < getLeftDomainBorder() - EPSILON) {
            addNodeByIndex(0, new FunctionPoint(point));
            return;
        }
        
        int i = 0;
        FunctionNode current = head.getNext();
        while (current != head && point.getX() > current.getPoint().getX() + EPSILON) {
            current = current.getNext();
            i++;
        }
        
        if (current != head && Math.abs(point.getX() - current.getPoint().getX()) < EPSILON) {
            throw new InappropriateFunctionPointException("Точка с таким X уже существует");
        }
        
        addNodeByIndex(i, new FunctionPoint(point));
    }

    // ==================== Переопределенные методы Object ====================
    
    @Override
    public String toString() {
        if (pointsCount == 0) return "{}";
        
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        
        FunctionNode current = head.getNext();
        while (current != head) {
            FunctionPoint point = current.getPoint();
            sb.append(String.format("(%.3f; %.3f)", point.getX(), point.getY()));
            
            current = current.getNext();
            if (current != head) sb.append(", ");
        }
        
        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TabulatedFunction)) return false;
        
        TabulatedFunction other = (TabulatedFunction) o;
        if (this.pointsCount != other.getPointsCount()) return false;
        
        if (o instanceof LinkedListTabulatedFunction) {
            LinkedListTabulatedFunction otherList = (LinkedListTabulatedFunction) o;
            
            FunctionNode current1 = this.head.getNext();
            FunctionNode current2 = otherList.head.getNext();
            
            while (current1 != this.head && current2 != otherList.head) {
                FunctionPoint p1 = current1.getPoint();
                FunctionPoint p2 = current2.getPoint();
                
                if (!p1.equals(p2)) return false;
                
                current1 = current1.getNext();
                current2 = current2.getNext();
            }
            return true;
        } else {
            for (int i = 0; i < pointsCount; i++) {
                FunctionPoint p1 = this.getPoint(i);
                FunctionPoint p2 = other.getPoint(i);
                if (!p1.equals(p2)) return false;
            }
            return true;
        }
    }

    @Override
    public int hashCode() {
        int result = pointsCount;
        FunctionNode current = head.getNext();
        while (current != head) {
            result ^= current.getPoint().hashCode();
            current = current.getNext();
        }
        return result;
    }

    @Override
    public TabulatedFunction clone() {
        FunctionPoint[] pointsArray = new FunctionPoint[pointsCount];
        FunctionNode current = head.getNext();
        for (int i = 0; i < pointsCount; i++) {
            pointsArray[i] = new FunctionPoint(current.getPoint());
            current = current.getNext();
        }
        return new LinkedListTabulatedFunction(pointsArray);
    }
}
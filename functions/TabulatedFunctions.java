package functions;

import java.io.*;

public final class TabulatedFunctions {

    public static TabulatedFunction tabulate(Function function, double leftX, double rightX, int pointsCount) {
        if(leftX >= rightX){
            throw new IllegalArgumentException("Левая граница больше или равна правой");
        }
        if (leftX < function.getLeftDomainBorder() || rightX > function.getRightDomainBorder()) {
            throw new IllegalArgumentException("Заданные границы выходят за область определения");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Требуется не менее 2 точек");
        }
        FunctionPoint[] points = new FunctionPoint[pointsCount];
        double step = (rightX - leftX) / (pointsCount - 1);
        
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            points[i] = new FunctionPoint(x, function.getFunctionValue(x));
        }
        
        return new ArrayTabulatedFunction(points);
    }

    public static void outputTabulatedFunction(TabulatedFunction function, OutputStream out) throws IOException {
        DataOutputStream dataOut = new DataOutputStream(out);
        dataOut.writeInt(function.getPointsCount());
        for (int i = 0; i < function.getPointsCount(); ++i) {
            FunctionPoint point = function.getPoint(i);
            dataOut.writeDouble(point.getX());
            dataOut.writeDouble(point.getY());
        }
        dataOut.flush();
    }

    public static TabulatedFunction inputTabulatedFunction(InputStream in) throws IOException {
        DataInputStream dis = new DataInputStream(in); 
        int pointCount = dis.readInt();
        FunctionPoint[] points = new FunctionPoint[pointCount];

        for (int i = 0; i < pointCount; i++) {
            points[i] = new FunctionPoint(dis.readDouble(), dis.readDouble());
        }
        return new ArrayTabulatedFunction(points); 
    }

    public static void writeTabulatedFunction(TabulatedFunction function, Writer out) throws IOException {
        BufferedWriter Writer = new BufferedWriter(out);
        int pointsCount = function.getPointsCount();
        Writer.write(" " + pointsCount);

        for (int i = 0; i < pointsCount; i++) {
            FunctionPoint point = function.getPoint(i);
            Writer.write("\n " + point.getX());
            Writer.write(" " + point.getY());
        }
        Writer.flush();
    }

    public static TabulatedFunction readTabulatedFunction(Reader in) throws IOException {
        StreamTokenizer st = new StreamTokenizer(in);
        st.nextToken();
        int pointsCount = (int) st.nval;
        FunctionPoint[] points = new FunctionPoint[pointsCount];
        
        for (int i = 0; i < pointsCount; i++) {
            st.nextToken();
            double x = st.nval;
            st.nextToken();
            double y = st.nval;
            points[i] = new FunctionPoint(x, y);
        }

        return new ArrayTabulatedFunction(points);
    }
}

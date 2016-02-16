
package ksom;

public class Node {
    private static final int vectorSize = 3;
    private double[] vector = new double[vectorSize];
    public Node(){

        for(int i = 0; i < vectorSize; i++){
            vector[i] = Math.random();
        }

    }
    public double getComponent(int pos){
        return vector[pos];
    }
    public void setComponent(int pos, double comp){
        vector[pos] = comp;
    }
}

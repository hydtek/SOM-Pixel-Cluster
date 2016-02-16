package ksom;

import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;

public class Main {

    private static final int iDim = 200;
    private static int red[][]    = new int[iDim][iDim];
    private static int green[][]  = new int[iDim][iDim];
    private static int blue[][]   = new int[iDim][iDim];

    static public void readFile() {
        int height = iDim;
        int width  = iDim;
        File f = new File("image.png");
        try {
            BufferedImage bi = ImageIO.read(f);
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    int mypixel = bi.getRGB(i, j);
                    Color c     = new Color(mypixel);
                    red[i][j]   = c.getRed();
                    green[i][j] = c.getGreen();
                    blue[i][j]  = c.getBlue();
                }
            }
        } catch (Exception e) {
        }
    }

    public static void main(String[] args) {
        String input = "";
        double learnRate = 0.0;
        int iteration    = 0;
        readFile();
        DataInputStream in = new DataInputStream(System.in);
        System.out.println("Please Enter the Learning Rate between 0 - 1");
        System.out.println();
        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("Invalid Input :");
        }
        learnRate = Double.parseDouble(input);
        System.out.println("Please enter an integer that represents the number of iterations to run for: 1 iteration = 50 input vectors");
        System.out.println();
        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("Invalid Input :");
        }
        iteration = Integer.parseInt(input);
        new Som(red, green, blue, iteration, learnRate);
    }

}

package ksom;

import java.awt.Color;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class Som extends JFrame {

    private static final int iDim = 200;
    private static final int bmuDim = 50;
    Node[][] kohMap = new Node[bmuDim][bmuDim];
    double[][] red = new double[iDim][iDim];
    double[][] green = new double[iDim][iDim];
    double[][] blue = new double[iDim][iDim];
    private double constLearnRate;
    private double radius;
    private int iteration;
    private double learnRate;

    //default constructor
    public Som() {
        radius = 0;
        learnRate = 0;
        iteration = 0;
        constLearnRate = 0;
    }

    public Som(int[][] r, int[][] g, int[][] b, int e, double lRate) {
        int x = 0;
        int y = 0;
        int l = 0;
        iteration = e;
        learnRate = lRate;
        constLearnRate = learnRate;
        radius = (double) bmuDim / 2.0;
        int count = 0;
        double numInputsPerIteration = 50;
        for (int i = 0; i < iDim; i++) {
            for (int j = 0; j < iDim; j++) {
                red[i][j] = r[i][j];
                red[i][j] = (1.0 / 255.0) * red[i][j];
                green[i][j] = g[i][j];
                green[i][j] = (1.0 / 255.0) * green[i][j];
                blue[i][j] = b[i][j];
                blue[i][j] = (1.0 / 255.0) * blue[i][j];
            }
        }
        initializeMap();
        List<String> list = initTrain();
        for (int k = 0; k < iteration; k++) {
            Collections.shuffle(list);

            for (l = 0; l < numInputsPerIteration; l++) {
                int comma = 1;
                for (int m = 0; m < list.get(l).length(); m++) {
                    if (list.get(l).charAt(m) == ',') {
                        comma = m;
                    }
                }
                x = Integer.parseInt(list.get(l).substring(0, comma));
                y = Integer.parseInt(list.get(l).substring((comma + 1), list.get(l).length()));
                training(x, y);
            }
            BufferedImage image;
            image = writeFile(count);
            displayFile(count, image);
            count++;
            numInputsPerIteration += 50;
            if (l == 40000) {
                l = 0;
                numInputsPerIteration = 50;
            }
            updateLearningRate(k, iteration);
            updateRadius(k, iteration);
            writeFile(k);
            if (k == (iteration - 1) || k % 50 == 0) {
                writeFile2(k);
                System.out.println("done : " + k);
            }
        }
    }

    private void training(int x, int y) {
        int lowx = 0;
        int lowy = 0;
        double distance = 0;
        double temp = 0;
        int tie = 0;
        for (int i = 0; i < bmuDim; i++) {
            for (int j = 0; j < bmuDim; j++) {
                temp = euclidean(x, y, i, j);
                if (i == 0 && j == 0) {
                    distance = temp;
                } else if (distance > temp) {
                    lowx = i;
                    lowy = j;
                    distance = temp;
                    tie = 0;
                } else if (distance == temp) {
                    tie++;
                }
            }
        }
        updateWeights(lowx, lowy, x, y);
    }

    private void updateLearningRate(int e, int eTot) {
        double e1 = e;
        double e2 = eTot;
        learnRate = constLearnRate * (Math.exp((-((e1) / (e2)))));
    }

    private void updateRadius(int e, int eTot) {
        double rad = (double) bmuDim / 2.0;
        double e2 = eTot;
        double e1 = e;
        double mTimeConst = (double) eTot / Math.log(rad);
        radius = rad * (Math.exp(-(e1 / mTimeConst)));
    }

    private double euclidean(int x, int y, int kx, int ky) {
        double distance = 0;
        distance = Math.pow((red[x][y] - kohMap[kx][ky].getComponent(0)), 2) + Math.pow((green[x][y] - kohMap[kx][ky].getComponent(1)), 2) + Math.pow((blue[x][y] - kohMap[kx][ky].getComponent(2)), 2);
        return distance;
    }

    private void updateWeights(int bmux, int bmuy, int x, int y) {
        int startx = 0;
        int starty = 0;
        int endx = 0;
        int endy = 0;

        startx = bmux - (int) Math.floor(radius);
        if (startx < 0) {
            startx = 0;
        }
        starty = bmuy - (int) Math.floor(radius);
        if (starty < 0) {
            starty = 0;
        }
        endx = bmux + (int) Math.floor(radius);
        if (endx > bmuDim) {
            endx = bmuDim;
        }
        endy = bmuy + (int) Math.floor(radius);
        if (endy > bmuDim) {
            endy = bmuDim;
        }

        for (int i = startx; i < endx; i++) {
            for (int j = starty; j < endy; j++) {
                double dist = Math.pow((bmuy - j), 2) + Math.pow((bmux - i), 2);
                if (dist <= radius) {
                    double guas = (Math.exp((-1.0 * (Math.pow(dist, 2))) / (2 * Math.pow(radius, 2))));
                    kohMap[i][j].setComponent(0, (kohMap[i][j].getComponent(0) + (guas * learnRate * (red[x][y] - kohMap[i][j].getComponent(0)))));
                    kohMap[i][j].setComponent(1, (kohMap[i][j].getComponent(1) + (guas * learnRate * (green[x][y] - kohMap[i][j].getComponent(1)))));
                    kohMap[i][j].setComponent(2, (kohMap[i][j].getComponent(2) + (guas * learnRate * (blue[x][y] - kohMap[i][j].getComponent(2)))));
                }
            }
        }
    }

    private void initializeMap() {
        for (int i = 0; i < bmuDim; i++) {
            for (int j = 0; j < bmuDim; j++) {
                kohMap[i][j] = new Node();
            }
        }
    }

    private List<String> initTrain() {
        int x = 0;
        int y = 0;
        String fileOrder[] = new String[iDim * iDim];
        for (int z = 0; z < (iDim * iDim); z++) {
            fileOrder[z] = "" + x + "," + y;
            y++;
            if ((z + 1) % iDim == 0 && z != 0) {
                y = 0;
                x++;
            }
        }
        List<String> list = Arrays.asList(fileOrder);
        return list;
    }

    private BufferedImage writeFile(int k) {
        int height = bmuDim;
        int width = bmuDim;
        int argb = 0;
        int r = 0;
        int g = 0;
        int b = 0;
        double r1;
        double g1;
        double b1;
        BufferedImage nbi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                r1 = kohMap[i][j].getComponent(0) * 255.0;
                g1 = kohMap[i][j].getComponent(1) * 255.0;
                b1 = kohMap[i][j].getComponent(2) * 255.0;
                r = (int) (Math.floor(r1));
                g = (int) (Math.floor(g1));
                b = (int) (Math.floor(b1));
                argb = (0xFF << 24) + (r << 16) + (g << 8) + b;
                nbi.setRGB(i, j, argb);
            }
        }
        return nbi;
    }

    private void displayFile(int k, BufferedImage image) {
        setSize(500, 500);
        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK);
        ImageIcon icon = new ImageIcon(image);
        JLabel label = new JLabel();
        label.setLocation(20, 20);
        label.setIcon(icon);
        panel.add(label);
        this.getContentPane().add(panel);
        setVisible(true);
    }

    private void writeFile2(int k) {
        int height = bmuDim;
        int width = bmuDim;
        int argb = 0;
        int r = 0;
        int g = 0;
        int b = 0;
        double r1;
        double g1;
        double b1;
        try {
            BufferedImage nbi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    r1 = kohMap[i][j].getComponent(0) * 255.0;
                    g1 = kohMap[i][j].getComponent(1) * 255.0;
                    b1 = kohMap[i][j].getComponent(2) * 255.0;
                    r = (int) (Math.floor(r1));
                    g = (int) (Math.floor(g1));
                    b = (int) (Math.floor(b1));
                    argb = (0xFF << 24) + (r << 16) + (g << 8) + b;
                    nbi.setRGB(i, j, argb);
                }
            }
            File outfile = new File("Color Map " + k);
            System.out.println(ImageIO.write(nbi, "png", outfile));
        } catch (Exception e) {
        }
    }
}

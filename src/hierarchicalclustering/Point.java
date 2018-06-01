/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hierarchicalclustering;

/**
 *
 * @author Nadian
 */
public class Point {
    private double mIndex1;
    private double mIndex2;
    private double mIndex3;
    private double mIndex4;
    private double mLabel;
    
    
    public Point() {
        this(0, 0, 0, 0, 0);
    }

    public Point(double index1, double index2, double index3, double index4) {
        this(index1, index2, index3, index4, 0);
    }

    public Point(double index1, double index2, double index3, double index4, double label) {
        mIndex1 = index1;
        mIndex2 = index2;
        mIndex3 = index3;
        mIndex4 = index4;
        mLabel = label;
    }

    public double getmIndex3() {
        return mIndex3;
    }

    public void setmIndex3(double mIndex3) {
        this.mIndex3 = mIndex3;
    }

    public double getmIndex4() {
        return mIndex4;
    }

    public void setmIndex4(double mIndex4) {
        this.mIndex4 = mIndex4;
    }  

    public double getmIndex1() {
        return mIndex1;
    }

    public void setmIndex1(double mIndex1) {
        this.mIndex1 = mIndex1;
    }

    public double getmIndex2() {
        return mIndex2;
    }

    public void setmIndex2(double mIndex2) {
        this.mIndex2 = mIndex2;
    }

    public double getmLabel() {
        return mLabel;
    }

    public void setmLabel(double mLabel) {
        this.mLabel = mLabel;
    }
   
}

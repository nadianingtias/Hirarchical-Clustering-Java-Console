/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hierarchicalclustering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Scanner;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 * @author Nadian
 */
public class HierarchicalClustering {
    private static int size = 150;
    private static ArrayList<Point> trainingPoints = new ArrayList<>();
    private static ArrayList<Point> oriTrainingPoints = new ArrayList<>();
    
    private static ArrayList<Point> centroid = new ArrayList<>();
    private static double[][] cArrayDistances = new double[size][size];
    private static int k;
    private static Double[] cpi = new Double[3];
    
    private static final String TRAINING_DATA_FILES = "E:\\Users\\Nadian\\Documents\\NetBeansProjects\\HierarchicalClustering\\src\\resource\\iris.txt";
    private static final String ORI_TRAINING_DATA_FILES = "E:\\Users\\Nadian\\Documents\\NetBeansProjects\\HierarchicalClustering\\src\\resource\\iris.txt";

    
    private static NumberFormat formatter = new DecimalFormat("#0.00");  
    private static Scanner scanner = new Scanner(System.in);
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        initDataPoint();
        
        System.out.println("Data Training IRIS");
            printPointData(trainingPoints);
        System.out.println("Centroid Awal");
//            printPointData(centroid);
        System.out.print("Masukkan jumlah kluster : ");
            k = scanner.nextInt();
            
        while(centroid.size()>k && cArrayDistances.length>k){
            //DO THE Hierarchical algorithm
            makeCentArrayDistances(); //1. membuat array of (JARAK centroid ke centroid)
            double minDistance = getMinValueOfCArrayDistances(); //2. mencari nilai minimum jarak centroid
            System.out.println("\nDistance Minimum : "+ formatter.format(minDistance));
            
            int[] indexOfMinDistance = getIndexOf2D(cArrayDistances, minDistance); //3. mencari index (2 kluster terdekat) dari nilai minimum jarak centroid
            System.out.println("index Merge (Cluster that will be merged) : " + indexOfMinDistance[0] +"  --and-- "+indexOfMinDistance[1]);
            
            centroid = doMergeCluster(centroid, indexOfMinDistance); //4. Gabungkan 2 Kluster yang terdekat
            trainingPoints = rearrangeLabelCentroid(trainingPoints, indexOfMinDistance); //5. Update data set dengan label kluster yang baru
            System.out.println("Data Training IRIS after arrange :");
//            printPointData(trainingPoints);
//            printPointDataLabelOnly(trainingPoints); //HASIL PELABELAN
            
            System.out.println("\n==============="+centroid.size()+"===============\n\n");
        } 
        printPointData(trainingPoints);
        if(k==3){
            for(int i=0 ; i< trainingPoints.size(); i++)
                trainingPoints.get(i).setmLabel(trainingPoints.get(i).getmLabel()+1);
                
            oriTrainingPoints = setData(ORI_TRAINING_DATA_FILES);
            
            for(int i=0 ; i<k ; i++)
                analyzeCPI(i+1);

            Double min = Arrays.stream(cpi)
                    .min(Comparator.comparingDouble(value -> value))
                    .orElse(null);
        
            System.out.println("Hasil CPI kesimpulan : " + formatter.format(min));
            System.out.println(" ");
            
            System.out.println("bandingkan hasil:");
            compareLabel(oriTrainingPoints, trainingPoints);
            
        }
    }
    private static ArrayList<Point> setData(String filePath) {
        ArrayList<String[]> resultList = new ArrayList<>(); // List untuk menampung sementara isi file
        ArrayList<Point> dataPoints = new ArrayList<>();
        try {
            File file = new File(filePath); // Membuat object file dari file yang diinginkan
            BufferedReader br = new BufferedReader(new FileReader(file)); // Membuat object untuk membaca file
            String str = "";
            /*
             * Looping untuk membaca keseluruhan isi file
             */
            while (str != null) {
                str = br.readLine(); // Mengambil setiap baris file
                if (str != null) // Jika baris ada
                {    resultList.add(str.split(",")); // Memisahkan setiap kata menurut tab ("   ")
                }
            }
        } catch (FileNotFoundException e) { // Jika file tidak ditemukan
            System.out.println("File tidak ditemukan");
        } catch (IOException e) { // Jika terjadi kesalahan saat membaca file
            System.out.println("Kesalahan saat membaca file");
        } finally {
            if (resultList.size() > 0) { // Jika file tidak kosong
                for (String[] strings : resultList) {
                    Point point;

                    double index1 = Double.parseDouble(strings[0]);
                    double index2 = Double.parseDouble(strings[1]);
                    double index3 = Double.parseDouble(strings[2]);
                    double index4 = Double.parseDouble(strings[3]);
                    double label = Double.parseDouble(strings[4]);
                    point = new Point(index1, index2, index3, index4, label);
                    dataPoints.add(point);
                }
            }
        }
        return dataPoints; // Mengembalikan data
    }
    private static void printPointData(ArrayList<Point> Points) {  
        for (Point point : Points) {        //print datatraining
            System.out.println(point.getmIndex1() + " " + point.getmIndex2() + " " + point.getmIndex3() + " " + point.getmIndex4() + " " + point.getmLabel());
        }
    }

    private static void deleteLabel(ArrayList<Point> Points) {
        for(Point point : Points){
            point.setmLabel(0);
        }
    }

    private static void initLabelCentroid(ArrayList<Point> Points) {
        for(int i=0 ; i < Points.size() ; i++){
            Points.get(i).setmLabel(i);
        }
    }

    private static ArrayList<Point> rearrangeLabelCentroid(ArrayList<Point> Points, int[] var) {
        int max = Arrays.stream(var).max().getAsInt();
        int min = Arrays.stream(var).min().getAsInt();
        
        for(int i=0 ; i < Points.size() ; i++){
            if(Points.get(i).getmLabel() == max){
                Points.get(i).setmLabel(min);
            }
            else if(Points.get(i).getmLabel() > max && (min != max )){
                double tempLabel = Points.get(i).getmLabel();
                Points.get(i).setmLabel(tempLabel-1);
            }
        }
         return Points;
    }

    private static void initDataPoint() {
        trainingPoints = setData(TRAINING_DATA_FILES);
        centroid = setData(TRAINING_DATA_FILES);
        deleteLabel(trainingPoints);
        deleteLabel(centroid); 
        initLabelCentroid(trainingPoints);
    }

    private static int[] getIndexOf2D(double[][] array2D, double value) {
        int[] var = new int[2];
        for(int i=0; i<array2D.length ; i++){
//            System.out.println(array2D.length);
            for(int j=0; j < array2D.length ; j++){
                if(array2D[i][j] == value)
                {
                    var[0]=i;
                    var[1]=j;
                    break;
                }
            }
        }
        return var;
    }

    private static ArrayList<Point> doMergeCluster(ArrayList<Point> mCentroid, int[] varMerge) {
            IntStream stream = Stream.of(varMerge).flatMapToInt(x -> Arrays.stream(x));
            int min = stream.min().getAsInt();
            IntStream stream2 = Stream.of(varMerge).flatMapToInt(x -> Arrays.stream(x));
            int max = stream2.max().getAsInt();
//            System.out.println(min);
//preparing new point of merged cluster
            Point dist = new Point();
                    dist.setmIndex1(0.5*(mCentroid.get(min).getmIndex1() + mCentroid.get(max).getmIndex1()));
                    dist.setmIndex2(0.5*(mCentroid.get(min).getmIndex2() + mCentroid.get(max).getmIndex2()));
                    dist.setmIndex3(0.5*(mCentroid.get(min).getmIndex3() + mCentroid.get(max).getmIndex3()));
                    dist.setmIndex4(0.5*(mCentroid.get(min).getmIndex4() + mCentroid.get(max).getmIndex4()));
//merge max indexed value into min indexed value
            mCentroid.set(min, dist);
            mCentroid.remove(max);
            
//            System.out.println(dist.getmIndex1() + " " + dist.getmIndex2() + " " + dist.getmIndex3() + " " + dist.getmIndex4() + " " + dist.getmLabel());
//            System.out.println(mCentroid.get(min).getmIndex1() + " " + mCentroid.get(min).getmIndex2() + " " + mCentroid.get(min).getmIndex3() + " " + mCentroid.get(min).getmIndex4() + " " + mCentroid.get(min).getmLabel());
            
            return mCentroid;
    }

    private static void makeCentArrayDistances() {
        cArrayDistances = new double[centroid.size()][centroid.size()];
//        for(int i=0; i<centroid.size() ; i++){
//            for(int j=0; j<centroid.size() ; j++){
//                System.out.print("kosongkah : "+formatter.format(cArrayDistances[i][j]) + " ");
//            }
//            System.out.println(" ");
//        }
        Point dist = new Point();
        System.out.println("Array of Distance : ");
        for(int i=0; i<centroid.size() ; i++){
            for(int j=0; j<(i+1) ; j++){
//                get Distance between data i and data j
                    dist.setmIndex1(centroid.get(i).getmIndex1() - centroid.get(j).getmIndex1());
                    dist.setmIndex2(centroid.get(i).getmIndex2() - centroid.get(j).getmIndex2());
                    dist.setmIndex3(centroid.get(i).getmIndex3() - centroid.get(j).getmIndex3());
                    dist.setmIndex4(centroid.get(i).getmIndex4() - centroid.get(j).getmIndex4());
                    if(i==j)
                        cArrayDistances[i][j] = 999;
                    else
                        cArrayDistances[i][j] = Math.sqrt(Math.pow(dist.getmIndex1(),2)+Math.pow(dist.getmIndex2(),2)+Math.pow(dist.getmIndex3(),2)+Math.pow(dist.getmIndex4(),2));
//                    System.out.print(formatter.format(cArrayDistances[i][j]) + " ");
            }
            System.out.println("");
        }
    }

    private static double getMinValueOfCArrayDistances() {
        Stream<double[]> temp = Stream.of(cArrayDistances);
        DoubleStream Stream = temp.flatMapToDouble(x -> Arrays.stream(x)); // Cant print Stream<int[]> directly, convert / flat it to IntStream 
        double mMinDistance = Stream.min().getAsDouble();
        return mMinDistance;
    }

    private static void printPointDataLabelOnly(ArrayList<Point> Points) {
        int i=1;
        for (Point point : Points) {        //print datatraining Label Only
            
                System.out.print(" " + point.getmLabel());
//            if((i%10)==0)
//                System.out.println(" ");
            i++;
        }
    }
    
    private static void analyzeCPI(double s) {
         ArrayList<Point> PointS = new ArrayList<>();
         ArrayList<Point> PointResultS = new ArrayList<>();
         Point CentroidS = new Point(0,0,0,0,0);
         Point CentroidResultS = new Point(0,0,0,0,0);
         double kol1,kol2,kol3,kol4;
         kol1=kol2=kol3=kol4= 0;
         double kolR1,kolR2,kolR3,kolR4;
         kolR1=kolR2=kolR3=kolR4= 0;
         double d;
         
         for(int i=0 ; i< oriTrainingPoints.size() ; i++){
             if(oriTrainingPoints.get(i).getmLabel()== s)
             {
                 PointS.add(oriTrainingPoints.get(i));
             }
             if(trainingPoints.get(i).getmLabel()==s){
                 PointResultS.add(trainingPoints.get(i));
             }
         }
         
         for(int i=0 ; i<PointResultS.size();i++){
//             System.out.println(PointResultS.get(i).getmIndex1() + " " + PointResultS.get(i).getmIndex2() + " " + PointResultS.get(i).getmIndex3() + " " + PointResultS.get(i).getmIndex4() + " " + PointResultS.get(i).getmLabel());
             CentroidResultS.setmLabel(s);
                 kolR1 += PointResultS.get(i).getmIndex1();
                 kolR2 += PointResultS.get(i).getmIndex2();
                 kolR3 += PointResultS.get(i).getmIndex3();
                 kolR4 += PointResultS.get(i).getmIndex4();
         }
         CentroidResultS.setmIndex1(kolR1/PointResultS.size());
         CentroidResultS.setmIndex2(kolR2/PointResultS.size());
         CentroidResultS.setmIndex3(kolR3/PointResultS.size());
         CentroidResultS.setmIndex4(kolR4/PointResultS.size());
         System.out.println("Centroid HASIL dalam Kluster " + s);
         System.out.println( formatter.format(CentroidResultS.getmIndex1()) + " " + formatter.format(CentroidResultS.getmIndex2()) + " " + formatter.format(CentroidResultS.getmIndex3()) + " " + formatter.format(CentroidResultS.getmIndex4()) + " " + CentroidResultS.getmLabel());
         
         for(int i=0 ; i<PointS.size();i++){
//             System.out.println(PointS.get(i).getmIndex1() + " " + PointS.get(i).getmIndex2() + " " + PointS.get(i).getmIndex3() + " " + PointS.get(i).getmIndex4() + " " + PointS.get(i).getmLabel());
             CentroidS.setmLabel(s);
                 kol1 += PointS.get(i).getmIndex1();
                 kol2 += PointS.get(i).getmIndex2();
                 kol3 += PointS.get(i).getmIndex3();
                 kol4 += PointS.get(i).getmIndex4();
         }
         CentroidS.setmIndex1(kol1/PointS.size());
         CentroidS.setmIndex2(kol2/PointS.size());
         CentroidS.setmIndex3(kol3/PointS.size());
         CentroidS.setmIndex4(kol4/PointS.size());
         System.out.println("Centroid REAL dalam Kluster " + s);
         System.out.println(formatter.format(CentroidS.getmIndex1()) + " " + formatter.format(CentroidS.getmIndex2()) + " " + formatter.format(CentroidS.getmIndex3()) + " " + formatter.format(CentroidS.getmIndex4()) + " " + CentroidS.getmLabel());
             
         
         Point dist = new Point();
         dist.setmIndex1(CentroidResultS.getmIndex1() - CentroidS.getmIndex1());
         dist.setmIndex2(CentroidResultS.getmIndex2() - CentroidS.getmIndex2());
         dist.setmIndex3(CentroidResultS.getmIndex3() - CentroidS.getmIndex3());
         dist.setmIndex4(CentroidResultS.getmIndex4() - CentroidS.getmIndex4());
         System.out.println("Matriks Jarak : " + formatter.format(dist.getmIndex1()) +"," + formatter.format(dist.getmIndex2()) + "," + formatter.format(dist.getmIndex3()) +"," + formatter.format(dist.getmIndex4()));
//         System.out.println(dist.getmIndex2());
//         System.out.println(dist.getmIndex3());
//         System.out.println(dist.getmIndex4());
         
         
         cpi[(int)s-1] = Math.sqrt(Math.pow(dist.getmIndex1(),2)+Math.pow(dist.getmIndex2(),2)+Math.pow(dist.getmIndex3(),2)+Math.pow(dist.getmIndex4(),2));
         System.out.println("CPI Label "+ s +" :"+ formatter.format(cpi[(int)s-1]));
         System.out.println("---------------------------------");
         System.out.println("");
    }

    private static void compareLabel(ArrayList<Point> oriPoints, ArrayList<Point> Points) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        int i=1;
        for (int i =1 ; i<= Points.size() ; i++) {        //print datatraining Label Only
                System.out.print(" " + oriPoints.get(i-1).getmLabel() + " " + Points.get(i-1).getmLabel() + "||");
            if((i%10)==0)
                System.out.println(" ");
        }
    }
}
//dummy
//DO THE Hierarcical algorithm
          //finding min value
////      // 1. Arrays.stream -> IntStream 
//        DoubleStream Stream1 = Arrays.stream(cArrayDistances);
//        Stream1.forEach(x -> System.out.println(x));
//        // 2. Stream.of -> Stream<int[]>
//        Stream<double[]> temp = Stream.of(cArrayDistances);
//        DoubleStream Stream = temp.flatMapToDouble(x -> Arrays.stream(x)); // Cant print Stream<int[]> directly, convert / flat it to IntStream 
//        double minDistance = Stream.min().getAsDouble();
//        System.out.println( "Minimum Distance is : " + minDistance);
//        int[] varMerge = new int[2];
//        varMerge = getIndexOf2D(cArrayDistances, minDistance);
//        System.out.println("index Merge : " + varMerge[0] +" n "+varMerge[1]);
//        centroid = doMergeCluster(centroid, varMerge);
//        trainingPoints = rearrangeLabelCentroid(trainingPoints, varMerge);
//        System.out.println("Data Training IRIS after arrange");
//        printPointData(trainingPoints);
     
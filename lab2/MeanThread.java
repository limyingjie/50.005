package com.example.yingjie.term5.week5.Lab2;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MeanThread {
    public static void main(String[] args) throws InterruptedException, IOException {
        // TODO: read data from external file and store it in an array
        // Note: you should pass the file as a first command line argument at runtime.
        //args=new String[]{"C:\\Users\\YingJie\\Desktop\\CSE_Lab2\\input.txt","2048"};
        InputStream is = new BufferedInputStream(new FileInputStream(args[0]));

        byte[] c = new byte[1024];
        ArrayList<Integer> numbers=new ArrayList<>();
        String number="";
        int readChars = 0;
        while ((readChars = is.read(c)) != -1) {
            for (int i = 0; i < readChars; ++i) {
                if (c[i] == ' ') {
                    numbers.add(Integer.parseInt(number));
                    number="";
                } else{number+=(char)c[i];}
            }
        }
        is.close();

        System.out.println(numbers.size()+" values read.");
        // define number of threads
        int NumOfThread = Integer.valueOf(args[1]);// this way, you can pass number of threads as
        // a second command line argument at runtime.

        // TODO: partition the array list into N subArrays, where N is the number of threads
        ArrayList<ArrayList<Integer>> partitions = new ArrayList<>();
        int remainder=numbers.size()%NumOfThread;
        final int valuesPerThread=numbers.size()/NumOfThread;
        int startIndex=0;
        int endIndex=valuesPerThread;
        for (int i=1; i<=NumOfThread;i++){
            if (remainder>0){endIndex++;remainder--;}
            //System.out.println(startIndex+"-"+endIndex);
            partitions.add( new ArrayList<>(numbers.subList(startIndex,endIndex)) );
            startIndex=endIndex;
            endIndex+=valuesPerThread;
        }

        // TODO: start recording time
        final long startTime = System.currentTimeMillis();

        // TODO: create N threads and assign subArrays to the threads so that each thread computes mean of
        // its repective subarray. For example,

//        MeanMultiThread thread1 = new MeanMultiThread(subArray1);
//        MeanMultiThread threadn = new MeanMultiThread(subArrayn);
        //Tip: you can't create big number of threads in the above way. So, create an array list of threads.
        ArrayList<MeanMultiThread> threads= new ArrayList<>();
        for (ArrayList<Integer> partition: partitions){
            //System.out.println(partition.size());
            threads.add(new MeanMultiThread(partition));
        }

        // TODO: start each thread to execute your computeMean() function defined under the run() method
        //so that the N mean values can be computed. for example,

        for (MeanMultiThread thread : threads){
            thread.start();
        }
        for (MeanMultiThread thread : threads){
            thread.join();
        }

        ArrayList<Double> temporalMeans=new ArrayList<>();
        // TODO: show the N mean values
        for (int i=1;i<=threads.size();i++){
            System.out.println("Temporal mean value of thread "+i+" is "+threads.get(i-1).getMean());
            temporalMeans.add(threads.get(i-1).getMean());
        }

        // TODO: store the temporal mean values in a new array so that you can use that
        /// array to compute the global mean.

        // TODO: compute the global mean value from N mean values.
        double means=0;
        for (int i=0;i<temporalMeans.size();i++){
            means+=temporalMeans.get(i);
        }
        double globalMean=means/temporalMeans.size();

        // TODO: stop recording time and compute the elapsed time

        System.out.println("The global mean value is "+globalMean);
        final long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime) );

    }
}
//Extend the Thread class
class MeanMultiThread extends Thread {
    private ArrayList<Integer> list;
    private double mean;
    MeanMultiThread(ArrayList<Integer> array) {
        list = array;
    }
    public double getMean() {
        return mean;
    }
    public void run() {
        // TODO: implement your actions here, e.g., computeMean(...)
        mean = computeMean(list);
    }
    public double computeMean(ArrayList<Integer> list){
        double sum=0;
        for (int value : list){
            sum+=value;
        }
        return sum/list.size();
    }
}

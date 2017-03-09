package com.example.yingjie.term5.week5.Lab2;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class MedianThread {

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

        // TODO: create N threads and assign subArrays to the threads so that each thread sorts
        // its repective subarray. For example,
        ArrayList<MedianMultiThread> threads= new ArrayList<>();
        for (ArrayList<Integer> partition: partitions){
            //System.out.println(partition.size());
            threads.add(new MedianMultiThread(partition));
        }
        //Tip: you can't create big number of threads in the above way. So, create an array list of threads.

        // TODO: start each thread to execute your sorting algorithm defined under the run() method, for example,
        for (MedianMultiThread thread : threads){
            thread.start();
        }
        for (MedianMultiThread thread : threads){
            thread.join();
        }

        // TODO: use any merge algorithm to merge the sorted subarrays and store it to another array, e.g., sortedFullArray.
        ArrayList<ArrayList<Integer>> subArrays=new ArrayList<>();
        for (int i=1;i<=threads.size();i++){
            subArrays.add(threads.get(i-1).getInternal());
        }
        ArrayList<Integer> sortedFullArray=new ArrayList<>();
        for (int i=1;i<=numbers.size();i++){
            int minimum=99999;
            int subArrayIndex=9999;
            for (int j=0;j<subArrays.size();j++){
                if (subArrays.get(j).size()!=0){
                    if (subArrays.get(j).get(0)<minimum){
                        minimum=subArrays.get(j).get(0);
                        subArrayIndex=j;
                    }
                }
            }
            sortedFullArray.add(minimum);
            subArrays.get(subArrayIndex).remove(0);
        }

        //TODO: get median from sortedFullArray

        double median=computeMedian(sortedFullArray);

        // TODO: stop recording time and compute the elapsed time
        final long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime) );

        // TODO: printout the final sorted array
        System.out.println(sortedFullArray.toString());

        // TODO: printout median
        System.out.println("The Median value is "+median);


    }

    public static double computeMedian(ArrayList<Integer> inputArray) {
        if (inputArray.size()%2==1){
            return inputArray.get(inputArray.size()/2);
        } else {
            return (inputArray.get(inputArray.size()/2)+inputArray.get(inputArray.size()/2-1))/2;
        }
    }

}

// extend Thread
class MedianMultiThread extends Thread {
    private ArrayList<Integer> list;

    public ArrayList<Integer> getInternal() {
        return list;
    }

    MedianMultiThread(ArrayList<Integer> array) {
        list = array;
    }

    public void run() {
        // called by object.start()
        mergeSort(list);

    }

    // TODO: implement merge sort here, recursive algorithm
    public void mergeSort(ArrayList<Integer> array) {
        Collections.sort(array);
    }
}

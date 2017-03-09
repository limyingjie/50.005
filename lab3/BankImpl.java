package com.example.yingjie.term5.week6.lab3;

import java.util.Arrays;

public class BankImpl {
	private int numberOfCustomers;	// the number of customers
	private int numberOfResources;	// the number of resources

	private int[] available; 	// the available amount of each resource
	private int[][] maximum; 	// the maximum demand of each customer
	private int[][] allocation;	// the amount currently allocated
	private int[][] need;		// the remaining needs of each customer
	
	public BankImpl (int[] resources, int numberOfCustomers) {

        this.numberOfResources=resources.length;

        this.numberOfCustomers=numberOfCustomers;

        this.available=resources;

        maximum=new int[numberOfCustomers][numberOfResources];
        allocation=new int[numberOfCustomers][numberOfResources];
        need=new int[numberOfCustomers][numberOfResources];

	}
	
	public int getNumberOfCustomers() {
        return numberOfCustomers;
	}

	public void addCustomer(int customerNumber, int[] maximumDemand) {
        maximum[customerNumber]=maximumDemand;
        need[customerNumber]=maximumDemand;

	}

	public void getState() {
		// TODO: print the current state with a tidy format

        System.out.println("\nAvailable\n"
                +Arrays.toString(available).replaceAll("[\\[\\],]",""));

        System.out.println("\nMaximum");
        for (int i=0;i<numberOfCustomers;i++){
            System.out.println(Arrays.toString(maximum[i]).replaceAll("[\\[\\],]",""));
        }

        System.out.println("\nAllocation");
        for (int i=0;i<numberOfCustomers;i++){
            System.out.println(Arrays.toString(allocation[i]).replaceAll("[\\[\\],]",""));
        }

        System.out.println("\nNeed");
        for (int i=0;i<numberOfCustomers;i++){
            System.out.println(Arrays.toString(need[i]).replaceAll("[\\[\\],]",""));
        }

	}

	public synchronized boolean requestResources(int customerNumber, int[] request) {

        System.out.println("Customer "+customerNumber+" request:\n"
                +Arrays.toString(request).replaceAll("[\\[\\],]",""));

        for(int i=0;i<request.length;i++){
            if(request[i]>need[customerNumber][i]){return false;}
        }

        for(int i=0;i<request.length;i++){
            if(request[i]>available[i]){return false;}
        }

        if (checkSafe(customerNumber,request)){
            // NOTE: NEED AND MAXIMUM SOMEHOW POINT TO THE SAME ARRAY ON MY COMPUTER,
            // SO THIS HACK IS NECESSARY.
            int[][] tempMaximum=new int[maximum.length][maximum[0].length];
            for (int i = 0; i < tempMaximum.length; i++) {
                for (int j=0;j<tempMaximum[0].length;j++){
                    tempMaximum[i][j]=maximum[i][j];

                }
            }

            for(int i = 0; i < this.numberOfResources; i++){
                this.available[i] -= request[i];
                this.allocation[customerNumber][i] += request[i];
                this.need[customerNumber][i] -= request[i];
            }

            this.maximum=tempMaximum;
            return true;
        } else{
            System.out.println("Request denied.");
            return false;
        }

	}

	public synchronized void releaseResources(int customerNumber, int[] release) {
        for(int i=0;i<release.length;i++){
            available[i]+=release[i];
            need[customerNumber][i]+=release[i];
            allocation[customerNumber][i]-=release[i];
        }
        System.out.println("Customer "+customerNumber+" releases:\n"
                +Arrays.toString(release).replaceAll("[\\[\\],]",""));

	}

	private synchronized boolean checkSafe(int customerNumber, int[] request) {
		// TODO: check if the state is safe
        int[] available=new int[numberOfResources];
        int[][] need=new int[numberOfCustomers][numberOfResources];
        int[][] allocation=new int[numberOfCustomers][numberOfResources];

        int[] work=new int[numberOfResources];

        for (int i = 0; i < numberOfResources; i++) {
            available[i]=this.available[i]-request[i];
            work[i]=available[i];
            for (int j = 0; j <numberOfCustomers; j++) {
                if(j==customerNumber){
                    need[customerNumber][i]=this.need[customerNumber][i]-request[i];
                    allocation[customerNumber][i]=this.allocation[customerNumber][i]+request[i];
                } else{
                    need[j][i]=this.need[j][i];
                    allocation[j][i]=this.allocation[j][i];
                }
            }
        }

        boolean[] done =new boolean[numberOfCustomers];
        Arrays.fill(done,false);
        int doneCount=0;
        boolean possible=true;

        while(possible){
            possible=false;
            for(int i=0;i<numberOfCustomers;i++){
                if(!done[i] && enoughResources(need[i],work)){
                    possible=true;
                    for (int j=0;j<numberOfResources;j++){
                        work[j]+=allocation[i][j];
                        done[i]=true;
                    }
                    doneCount++;
                }
            }
        }
        return (doneCount==done.length);
        //return true;
	}

    private synchronized boolean enoughResources(int[] need,int[] work){
        for(int i=0;i<need.length;i++){
            if (need[i]>work[i]){return false;}
        }
        return true;
    }

}
package com;

class Process{
    static int globalCount = 1;
    int arrive;
    int exec;
    int waiting = 0;
    int plus = 0;

    public Process(int arrive, int exec) {
        this.arrive = arrive;
        this.exec = exec;
    }

    static Process [] createProcesses(int n){
        Process [] result = new Process[n];
        result[0] = (new Process(0,  (int)(Math.random() * 10) + 1));
        for(int i = 1; i < n; i++){
            result[i] = (new Process((int)(Math.random() * 2) + i,  (int)(Math.random() * 10) + 1));
        }
        return result;
    }

    static void runSRTF(Process [] lst, int time){
        for(int i = 1; i <= time; i++){
            if(allZero(lst)) break;
            int idx = minExecIndex(lst);
            updateWaiting(lst, idx);
            lst = minusArriveTime(lst);
            if(lst[idx].exec != 0) lst[idx].exec--;
            System.out.printf("%3d (%3d): \n", i, globalCount++);
            printExec(lst);
        }
    }

    private static void updateWaiting(Process[] lst, int except) {
        for(int i = 0; i < lst.length; i++){
            if(i == except) continue;
            if(lst[i].arrive == 0 && lst[i].exec != 0) lst[i].waiting++;
        }
    }

    private static boolean allZero(Process[] lst) {
        for (Process process : lst) {
            if (process.exec != 0) return false;
        }
        return true;
    }

    private static Process[] minusArriveTime(Process[] lst){
        for (Process process : lst) {
            if (process.arrive != 0) process.arrive--;
        }
        return lst;
    }

    static private int minExecIndex(Process[] lst){
        int min = 100000000;
        int idx = 0;
        for(int i = 0; i < lst.length; i++){
            if(lst[i].arrive == 0 && lst[i].exec < min && lst[i].exec > 0){
                min = lst[i].exec;
                idx = i;
            }
        }
        return idx;
    }

    static void printExec(Process[] lst){
        System.out.print("Exec :    ");
        for(Process process : lst){
            System.out.printf("%3d ", process.exec);
        }
        System.out.print("|\n");
        System.out.print("Arrrive : ");
        for(Process process : lst){
            System.out.printf("%3d ", process.arrive);
        }
        System.out.print("|\n");
        System.out.print("Waiting : ");
        for(Process process : lst){
            System.out.printf("%3d ", process.waiting);
        }
        System.out.print("|\n----------");
        for(int i = 0; i < lst.length; i++)
            System.out.print("----");
        System.out.print("+\n");
    }

    static void runRR(Process[] lst, int quantum, int time){
        int currIdx = 0;
        int counter = 0;
        for(int i = 1; i <= time; i++) {
            if (allZero(lst)) break;
            int flag = 0;
            if(counter == quantum) {
                currIdx = getNextIdx(lst, currIdx);
                counter = 0;
            }
            while(lst[currIdx].exec == 0 && flag < lst.length) {
                currIdx = getNextIdx(lst, currIdx);
                counter = 0;
                flag++;
            }
            if(lst[currIdx].exec != 0 && counter < quantum) {
                lst[currIdx].exec--;
                counter++;
            }
            minusArriveTime(lst);
            updateWaiting(lst, currIdx);
            System.out.printf("%3d (%3d): \n", i, globalCount++);
            printExec(lst);
        }
    }

    static int getNextIdx(Process[] lst, int currIdx){
        if(currIdx == -1) return 0;
        if(currIdx >= lst.length - 1) return 0;
        if(lst[currIdx++].arrive != 0) return 0;
        return currIdx++;
    }

    static void runAll(int processorTime, Process[] srtf, Process [] rr, int quantum){

        while (!allZero(rr) || !allZero(srtf)) {
            if (!allZero(rr)) {
                System.out.println("------------------------------------- INTERACTIVE ------------------------------------");
                Process.runRR(rr, quantum, (int) (processorTime * 0.8));
                System.out.println("---------------------------------------- BACK ----------------------------------------");
            }
            Process.runSRTF(srtf, (int) (processorTime * 0.2));
        }
    }

    static double averageWait(Process[] lst){
        int sum = 0;
        for(Process process : lst){
            sum += process.waiting;
        }
        return (double) sum / lst.length;
    }
}





public class Main {

    public static void main(String[] args) {
        Process [] SRTF = Process.createProcesses(6);
        Process [] RR = Process.createProcesses(5);
        System.out.println("SRTF processes:");
        Process.printExec(SRTF);
        System.out.println("RR processes:");
        Process.printExec(RR);
        Process.runAll(20, SRTF, RR, 2);

        System.out.printf("Average wait time for SRTF: %5.5f\n", Process.averageWait(SRTF));
        System.out.printf("Average wait time for RR: %5.5f \n", Process.averageWait(RR));

    }
}


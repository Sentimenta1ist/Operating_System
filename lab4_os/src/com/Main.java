package com;

import java.util.Scanner;

class Cluster{
    final int id;
    int next;

    Cluster(int id){
        this.id = id;
        this.next = 0;
    }

    void setNext(int next){
        this.next = next;
    }
}

class File{
    String filename;
    int firstCluster;
    int size; // in clusters

    File(String filename, int firstCluster, int size){
        this.filename = filename;
        this.firstCluster = firstCluster;
        this.size = size;
    }
}

class FAT {
    Cluster[] table = new Cluster[256];
    File[] rootDirectory = new File[10];
    int rootDirectorySize = 0;

    FAT(){
        for(int i = 0; i < 256; i++){
            table[i] = new Cluster(i);
        }
    }

    void printClusters(){
        System.out.print("\n\n---------------------------------------------------------------------------------------");
        System.out.print("\n      | ");
        for(int i = 0; i < 16; i++) System.out.printf("0x%02X ", i);
        System.out.print("\n------|--------------------------------------------------------------------------------");
        for(int i = 0; i < table.length; i++){
            if(i % 16 == 0) {
                System.out.print("\n");
                System.out.printf("|0x%02X | ", i / 16);
            }
            System.out.printf("0x%02X ", table[i].next);
        }
    }

    void printRootTable(){
        System.out.println("\nRoot directory table:");
        for (int i = 0; i < rootDirectory.length; i++) {
            if (rootDirectory[i] != null) {
                System.out.printf("Index: %d, Filename: '%s', First cluster: 0x%02X, Size: %d clusters%n", i,
                        rootDirectory[i].filename, rootDirectory[i].firstCluster, rootDirectory[i].size);
            }
        }
    }

    void createTest0(){
        rootDirectory[rootDirectorySize] = new File("foo.txt", 0x70, 9);
        rootDirectorySize++;
        table[0x70].setNext(0x71);
        table[0x71].setNext(0x72);
        table[0x72].setNext(0x55);

        table[0x55].setNext(0x56);
        table[0x56].setNext(0x03);

        table[0x03].setNext(0x04);
        table[0x04].setNext(0x9c);

        table[0x9c].setNext(0x9D);
        table[0x9D].setNext(0xff);
    }

    int addCluster() {
        for (int i = 1; i < table.length; i++) {
            if (table[i].next == 0x00) {
                for (int j = i + 1; j < table.length; j++) {
                    if (table[j].next == 0x00) {
                        table[i].next = j;
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    int addLastCluster(){
        for(int i = 1; i < table.length; i++) {
            if (table[i].next == 0x00) {
                table[i].next = 0xFF;
                return i;
            }
        }
        return -1;
    }

    void addClusterToExist(int index) {
        for(int i = 1; i < table.length; i++) {
            if (table[i].next == 0x00) {
                table[i].next = rootDirectory[index].firstCluster;
                rootDirectory[index].firstCluster = i;
                rootDirectory[index].size++;
                break;
            }
        }
    }

    void addFile(String name, int size){
        if(size == 0) return;
        if(size == 1) {
            rootDirectory[rootDirectorySize] = new File(name, addLastCluster(), size);
            rootDirectorySize++;
            return;
        }
        rootDirectory[rootDirectorySize] = new File(name, addCluster(), size);
        rootDirectorySize++;

        for(int i = 0; i < size - 2; i++) {
            addCluster();
        }

        addLastCluster();
    }

    void increaseFile(int index, int number){
        for(int i = 0; i < number; i++) { addClusterToExist(index); }
    }

    void deleteFile(int index){
        int next = rootDirectory[index].firstCluster;
        do{
            int tmp = table[next].next;
            table[next].next = 0x00;
            next = tmp;
            rootDirectory[index].size--;

        }while(rootDirectory[index].size != 0);

        rootDirectory[index] = null;
    }

    public void printArrayOfClusters(int index) {
        int curr = rootDirectory[index].firstCluster;
        System.out.print("Cluster array of file #" + index + ": ");
        System.out.printf("0x%02X ", curr);
        do{
            curr = table[curr].next;
            System.out.printf("0x%02X ", curr);
        }while(curr != 0xFF);
    }
}




public class Main {

    private static void createFile(FAT fat){
        Scanner sc = new Scanner(System.in);
        System.out.print("Input name: ");
        String name = sc.nextLine();
        System.out.print("Input size: ");
        int size = sc.nextInt();
        fat.addFile(name, size);
        System.out.println("File created! Check FAT table.");
    }

    private static void removeFile(FAT fat){
        Scanner sc = new Scanner(System.in);
        System.out.print("Input index: ");
        int index = sc.nextInt();
        fat.deleteFile(index);
        System.out.println("File removed! Check FAT table.");
    }

    private static void increaseFile(FAT fat){
        Scanner sc = new Scanner(System.in);
        System.out.print("Input index: ");
        int index = sc.nextInt();
        System.out.print("Input amount of increasing:");
        int amount = sc.nextInt();
        fat.increaseFile(index, amount);
        System.out.println("File increased! Check FAT table.");
    }

    private static void printArrayOfClusters(FAT fat){
        Scanner sc = new Scanner(System.in);
        System.out.print("Input index: ");
        int index = sc.nextInt();
        fat.printArrayOfClusters(index);
    }

    public static void main(String[] args) {
        FAT fat = new FAT();
        fat.createTest0();
        Scanner sc = new Scanner(System.in);
        int n = 1;
        while (n != 0){
            System.out.println("\n\nMENU:");
            System.out.println("1. Print FAT table");
            System.out.println("2. Print directory");
            System.out.println("3. Create file");
            System.out.println("4. Increase file size");
            System.out.println("5. Remove file");
            System.out.println("6. Print array of clusters");
            System.out.println("0. Exit");
            n = sc.nextInt();

            switch(n){
                case 1: fat.printClusters(); break;
                case 2: fat.printRootTable(); break;
                case 3: createFile(fat); break;
                case 4: increaseFile(fat); break;
                case 5: removeFile(fat); break;
                case 6: printArrayOfClusters(fat); break;
                default: break;
            }
        }
    }
}

package com;

//memory tasks

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

class Memory {
    PhysicalPage [] physicalPages = new PhysicalPage[7];

    void print(){
        System.out.println("----------------OP-MEMORY--------------");
        for(PhysicalPage page : physicalPages) {
            if(page != null){
                String time = page.time.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                System.out.printf("| Process %3d| Section %3d|   Page %3d|  (%s)\n", page.processNum, page.secNum, page.virtualPage.number, time);
            }
            else{
                System.out.print("|            |            |           |\n");
            }
        }
        System.out.println("--------------------------------------");
    }

    Page getPage(Page page){
        for(int i = 0; i < physicalPages.length; i++){
            if(physicalPages[i] != null && physicalPages[i].virtualPage == page){
                return page;
            }
        }
        return null;
    }

    boolean full(){
        for(PhysicalPage page : physicalPages){
            if(page == null) return false;
        }
        return true;
    }

    void removePage(Page page){
        for(int i = 0; i < physicalPages.length; i++){
            if(physicalPages[i] != null && physicalPages[i].virtualPage == page){
                physicalPages[i] = null;
                page.phys_num = null;
                page.presence = 0;
                break;
            }
        }
    }

    void removeOldest(){
        int oldesIndex = 0;
        LocalDateTime time = physicalPages[0].time;
        for(int i = 0; i < physicalPages.length; i++){
            if(time.isAfter(physicalPages[i].time)){
                oldesIndex = i;
                time = physicalPages[i].time;
            }
        }
        physicalPages[oldesIndex].virtualPage.presence = 0;
        physicalPages[oldesIndex].virtualPage.phys_num = null;
        physicalPages[oldesIndex] = null;
    }

    void removeProcess(int index){
        for(int i = 0; i < physicalPages.length; i++){
            if(physicalPages[i] != null && physicalPages[i].processNum == index){
                physicalPages[i].virtualPage.phys_num = null;
                physicalPages[i].virtualPage.presence = 0;
                physicalPages[i] = null;
            }
        }

    }

    void addPage(int process, int section, Page page){
        /*try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        if(full()){
            removeOldest();
        }
        for(int i = 0; i < physicalPages.length; i++){
            if(physicalPages[i] == null){
                physicalPages[i] = new PhysicalPage( process, section, page);
                page.phys_num = i;
                page.presence = 1;
                break;
            }
        }

    }
}

class PhysicalPage {
    int processNum;
    int secNum;
    Page virtualPage;
    LocalDateTime time;

    PhysicalPage(int processNum, int secNum, Page virtualPage) {
        this.processNum = processNum;
        this.secNum = secNum;
        this.virtualPage = virtualPage;
        time = LocalDateTime.now();
    }
}

class Process {
    final int number;
    final Section[] sections;

    Process(int number, Section[] sections) {
        this.number = number;
        this.sections = sections;
    }

    void print() {
        System.out.println(" PROCESS #" + number);
        for(Section section : sections){
            section.print();
        }
        System.out.println('\n');
    }
}

class Section {
    final int number;
    final Page[] pages;

    Section(int number, Page[] pages) {
        this.number = number;
        this.pages = pages;
    }

    void print() {
        System.out.println("\t Section #" + number);
        for(Page page : pages){
            page.print();
        }
    }
}

class Page {
    final int number;
    int presence;
    Integer phys_num = null;

    Page(int number, int presence) {
        this.number = number;
        this.presence = presence;
    }

    void setPhysNum(int num){
        phys_num = num;
    }

    void print(){
        System.out.printf("\t\t Page #%d, %d, Phys: %d\n", number, presence, phys_num);
    }
}

class Processes {
    Process process1 = new Process(1,
            new Section[] {
                    new Section(0, new Page[] {
                            new Page(0, 0),
                            new Page(1, 0),
                            new Page(2, 0)}),
                    new Section(1, new Page[] {
                            new Page(0, 0),
                            new Page(1, 0),
                            new Page(2, 0)}),
                    new Section(2, new Page[] {
                            new Page(0, 0),
                            new Page(1, 0),
                            new Page(2, 0)})
            });
    Process process2 = new Process(2,
            new Section[] {
                    new Section(0, new Page[] {
                            new Page(0, 0)}),
                    new Section(1, new Page[] {
                            new Page(0, 0),
                            new Page(1, 0)})
            });

    Process getProcess(int num){
        return switch (num) {
            case 1 -> process1;
            case 2 -> process2;
            // . . .
            default -> throw new IllegalArgumentException();
        };
    }

    void printAll(){
        process1.print();
        process2.print();
    }
}


public class Main {

    static void menu(){
        System.out.println("     MENU:      ");
        System.out.println(" 1. Print operating memory");
        System.out.println(" 2. Print process");
        System.out.println(" 3. Load page of process");
        System.out.println(" 4. Unload page of process");
        System.out.println(" 5. Remove process");
        System.out.println(" 6. Get physical addr");
        System.out.println(" 0. Exit");
    }

    static void processPrint(Processes processes){
        Scanner sc = new Scanner(System.in);
        System.out.print("Input number of process (or input 0 for print all processes): ");
        int n = sc.nextInt();
        if(n == 0) processes.printAll();
        else processes.getProcess(n).print();
    }

    static void clearProcess(Processes processes, Memory memory){
        Scanner sc = new Scanner(System.in);
        System.out.print("Input number of process: ");
        int n = sc.nextInt();
        Process currentProc = processes.getProcess(n);
        memory.removeProcess(n);
        System.out.print("Process unloaded.\n");
    }

    static void processUnload(Processes processes, Memory memory){
        Scanner sc = new Scanner(System.in);
        System.out.print("Input number of process: ");
        int n = sc.nextInt();
        Process currentProc = processes.getProcess(n);

        System.out.print("Input virtual addr(XXXYYYCCC): ");
        String virtualAddr = sc.nextLine();
        virtualAddr = sc.nextLine();
        int sectionNum = Integer.parseInt(virtualAddr.substring(0,3), 2);
        int pageNum = Integer.parseInt(virtualAddr.substring(3,6), 2);
        Page currentPage = currentProc.sections[sectionNum].pages[pageNum];
        memory.removePage(currentPage);
        System.out.print("Page unloaded.\n");

    }


    static void processLoad(Processes processes, Memory memory){
        Scanner sc = new Scanner(System.in);
        System.out.print("Input number of process: ");
        int n = sc.nextInt();
        Process currentProc = processes.getProcess(n);

        System.out.print("Input virtual addr(XXXYYYCCC): ");
        String virtualAddr = sc.nextLine();
        virtualAddr = sc.nextLine();
        int sectionNum = Integer.parseInt(virtualAddr.substring(0,3), 2);
        int pageNum = Integer.parseInt(virtualAddr.substring(3,6), 2);
        Page currentPage = currentProc.sections[sectionNum].pages[pageNum];
        if(memory.getPage(currentPage) == null){
            memory.addPage(n, sectionNum, currentPage);
            System.out.printf("Page loaded. Physical addr : [0x%s|%s]\n", Integer.toBinaryString(currentPage.phys_num), virtualAddr.substring(6));
        }
        else{
            System.out.printf("Page was loaded. Physical addr : [0x%s|%s]\n", Integer.toBinaryString(currentPage.phys_num), virtualAddr.substring(6));
        }

    }

    static void getPhysicalAddr(Processes processes, Memory memory){
        Scanner sc = new Scanner(System.in);
        System.out.print("Input number of process: ");
        int n = sc.nextInt();
        Process currentProc = processes.getProcess(n);

        System.out.print("Input virtual addr(XXXYYYCCC): ");
        String virtualAddr = sc.nextLine();
        virtualAddr = sc.nextLine();
        int sectionNum = Integer.parseInt(virtualAddr.substring(0,3), 2);
        int pageNum = Integer.parseInt(virtualAddr.substring(3,6), 2);
        Page currentPage = currentProc.sections[sectionNum].pages[pageNum];
        if(memory.getPage(currentPage) == null){
            memory.addPage(n, sectionNum, currentPage);
            System.out.printf("Page loaded just now. Physical addr : [0x%s|%s]\n", Integer.toBinaryString(currentPage.phys_num), virtualAddr.substring(6));
        }
        else{
            System.out.printf("Page was loaded. Physical addr : [0x%s|%s]\n", Integer.toBinaryString(currentPage.phys_num), virtualAddr.substring(6));
        }

    }


    public static void main(String[] args) {
        Memory memory = new Memory();
        Processes processes = new Processes();

        memory.addPage(5,1,new Page(5,5));
        memory.addPage(5,2,new Page(5,5));
        memory.addPage(5,3,new Page(5,5));
        //main loop
        int temp = 1;

        while(temp != 0){

            Scanner sc = new Scanner(System.in);
            menu();
            temp = sc.nextInt();
            switch (temp){
                case 1: memory.print(); break;
                case 2: processPrint(processes); break;
                case 3: processLoad(processes, memory); break;
                case 4: processUnload(processes, memory); break;
                case 5: clearProcess(processes, memory); break;
                case 6: getPhysicalAddr(processes, memory); break;
                default: break;
            }
        }
    }
}

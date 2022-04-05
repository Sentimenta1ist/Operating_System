package com;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

class Coin {
    int size;
    int amount;
    Coin(int size, int amount){
        this.size = size;
        this.amount = amount;
    }

}

class Bank {
    static Coin [] bankCoins = {
            new Coin(50, 5),
            new Coin(25, 10),
            new Coin(10, 15),
            new Coin(5, 20),
            new Coin(2, 25),
            new Coin(1, 50),
    };

    static List<Coin> needCoins = Arrays.stream(bankCoins).map( coin -> new Coin(coin.size, 0)).collect(Collectors.toList());

    static void printBank(){
        System.out.println("Це всі наші гроші ДИВІТЬСЯ :");
        for(Coin coin : bankCoins){
            System.out.printf("%3d $ - %3d coins\n", coin.size, coin.amount);
        }
    }

    static boolean getChange(int n) {
        for (int i = 0; i < bankCoins.length; i++) {
            int need = Math.floorDiv(n, bankCoins[i].size);
            if (bankCoins[i].amount - need < 0)
                needCoins.get(i).amount = bankCoins[i].amount;
            else needCoins.get(i).amount = need;
            n -= bankCoins[i].size * needCoins.get(i).amount;
        }
        if(n != 0) return false;
        for (int i = 0; i < bankCoins.length; i++){
            bankCoins[i].amount -= needCoins.get(i).amount;
        }
        return true;
    }
}

class PostBox{
    static boolean sent = false;
    static int change = -1;
    static int signal = 0;
}

class ThreadA extends Thread{
    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n\n+------------------------------------------------------------------------+");
        System.out.println("|   Привіт! Ви можете купити квиток на літак (Ящко не хворієте COVID)!   |");
        System.out.println("+------------------------------------------------------------------------+\n");
        System.out.print(" Щоб увімкнути автомат натисніть '0': ");
        PostBox.signal = sc.nextInt();
        while(PostBox.signal != 9) {
            if (!PostBox.sent) {
                System.out.println("+------------------------------------------------------+");
                System.out.println("|  Вимкнути автомат.                    Натисніть '9'  |");
                System.out.println("|  Білет до Києва.   Вартість 28 коп.   Натисніть '1'  |");
                System.out.println("|  Білет до Москви.  Вартість 37 коп.   Натисніть '2'  |");
                System.out.println("|  Білет до Лондона. Вартість 50 коп.   Натисніть '3'  |");
                System.out.println("|  Білет до Берліна. Вартість 77 коп.   Натисніть '4'  |");
                System.out.println("|  Білет до Парижа.  Вартість 91 коп.   Натисніть '5'  |");
                System.out.println("+------------------------------------------------------+");
                System.out.print("    Оберіть напрямок: ");
                PostBox.signal = sc.nextInt();

                if (PostBox.signal == 9) {
                    System.out.println("Гарного настрою та не хворійте!");
                    break;
                }

                System.out.print("Введіть гроші будь ласка: ");
                int money = sc.nextInt();
                int price = -1;
                boolean bad = false;
                switch (PostBox.signal) {
                    case 1:
                        price = 28;
                        break;
                    case 2:
                        price = 37;
                        break;
                    case 3:
                        price = 50;
                        break;
                    case 4:
                        price = 77;
                        break;
                    case 5:
                        price = 91;
                        break;
                    default:
                        bad = true;
                        break;
                }
                if (bad) {
                    System.out.println("Невідомий напрямок. ВИБАЧТЕ. Оберіть знов.");
                    continue;
                }
                if (money - price < 0) {
                    while (money - price < 0) {
                        System.out.println("Маловато ви дали. ЩЕ :)");
                        money += sc.nextInt();

                    }
                }
                PostBox.change = money - price;
                PostBox.sent = true;

            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Bank.printBank();
    }
}

class ThreadB extends Thread{

    @Override
    public void run() {
        while (PostBox.signal != 9) {
            Scanner sc = new Scanner(System.in);
            if (PostBox.sent) {
                if (Bank.getChange(PostBox.change)) {
                    System.out.printf("Ваша решта '%d' та ваш квиток. Отримайте.\n", PostBox.change);
                } else {
                    System.out.printf("Вибачте. Заберіть гроші. Немає такої решти (%d коп.) :( ", PostBox.change);
                }
                System.out.println("Продовжити купування, натисніть '0'. Вимкнути апарат, натисніть '9'.");
                PostBox.signal = sc.nextInt();
                PostBox.sent = false;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

public class Main {
    public static void main(String[] args) {
        ThreadA a = new ThreadA();
        ThreadB b = new ThreadB();
        a.start();
        b.start();
    }
}
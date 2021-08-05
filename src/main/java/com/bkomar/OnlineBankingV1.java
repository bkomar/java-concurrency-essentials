package com.bkomar;

import java.util.UUID;

public class OnlineBankingV1 {

    private int account = 10;

    public void transfer(int amount) {
        String txUuid = UUID.randomUUID().toString();
        System.out.println("UUID: " + txUuid + " | Transferred amount = " + amount);
        account += amount;
        System.out.println("UUID: " + txUuid + " | Balance = " + account);
    }

    public static void main(String[] args) {
        OnlineBankingV1 b1 = new OnlineBankingV1();
        b1.transfer(10);
        b1.transfer(-15);
    }
}

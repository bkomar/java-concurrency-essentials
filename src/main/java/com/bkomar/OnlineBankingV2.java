package com.bkomar;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OnlineBankingV2 {

    private int account = 10;
    private Object lock = new Object();

    public void transfer(int amount, String txUuid) {
           synchronized (lock) {
               account = account + amount;
               System.out.println(
                       String.format("Transaction uuid %s | Transferred amount: %s | Balance: %s", txUuid, amount,
                               account));
           }

    }

    public static void main(String[] args) {
        OnlineBankingV2 b2 = new OnlineBankingV2();

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(b2, 5));
        transactions.add(new Transaction(b2, 8));
        transactions.add(new Transaction(b2, -5));
        transactions.add(new Transaction(b2, -8));
        transactions.add(new Transaction(b2, 2));
        transactions.add(new Transaction(b2, -12));
        transactions.add(new Transaction(b2, -5));
        transactions.add(new Transaction(b2, -2));
        transactions.add(new Transaction(b2, 7));
        transactions.add(new Transaction(b2, 6));
        transactions.add(new Transaction(b2, 3));
        transactions.add(new Transaction(b2, -9));

        transactions.forEach(t -> t.start());
    }

    static class Transaction extends Thread {

        private OnlineBankingV2 banking;
        private int transferAmount;
        private String txUuid;

        public Transaction(OnlineBankingV2 banking, int transferAmount) {
            this.txUuid = UUID.randomUUID().toString();
            this.banking = banking;
            this.transferAmount = transferAmount;
        }

        @Override
        public void run() {
            banking.transfer(transferAmount, txUuid);
        }
    }
}

package com.bkomar;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OnlineBankingV3 {

    private int account = 10;

    public synchronized void transfer(int amount, String txUuid) {
        try {
            while (!Thread.currentThread().isInterrupted() && amount < 0 && account < Math.abs(amount)) {
                System.out.println(String.format("Waiting for the enough money. Transfer: %s,  Balance: %s, Tx: %s",
                        amount, account, txUuid));
                wait(3000);
            }
            if (!Thread.currentThread().isInterrupted()) {
                account = account + amount;
                System.out.println(String.format("Transaction uuid %s | Transferred amount: %s | Balance: %s",
                        txUuid, amount, account));
            }
            notifyAll();
        } catch (InterruptedException ex) {
            System.out.println("Transaction uuid : " + txUuid + " was cancelled");
        }
    }

    public static void main(String[] args) {
        OnlineBankingV3 b3 = new OnlineBankingV3();

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(b3, -12));
        transactions.add(new Transaction(b3, 5));
        transactions.add(new Transaction(b3, 8));
        transactions.add(new Transaction(b3, -5));
        transactions.add(new Transaction(b3, -8));
        transactions.add(new Transaction(b3, 2));
        transactions.add(new Transaction(b3, -5));
        transactions.add(new Transaction(b3, -2));
        transactions.add(new Transaction(b3, 7));
        transactions.add(new Transaction(b3, 6));
        transactions.add(new Transaction(b3, 3));
        transactions.add(new Transaction(b3, -9));

        transactions.forEach(t -> {
            t.start();
        });

    }

    static class Transaction extends Thread {

        private OnlineBankingV3 banking;
        private int transferAmount;
        private String txUuid;

        public Transaction(OnlineBankingV3 banking, int transferAmount) {
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

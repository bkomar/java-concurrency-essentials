package com.bkomar;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OnlineBankingV4 {

    private int account;

    private Lock lock;
    private Condition noBalanceCondition;
    private AtomicInteger income;
    private AtomicInteger expenses;


    public OnlineBankingV4(int initialAmount) {
        this.account = initialAmount;
        this.lock = new ReentrantLock();
        this.noBalanceCondition = lock.newCondition();

        this.income = new AtomicInteger(0);
        this.expenses = new AtomicInteger(0);
    }

    public void transfer(int amount, String txUuid) {
        try {
            lock.lock();
            while (!Thread.currentThread().isInterrupted() && amount < 0 && account < Math.abs(amount)) {
                System.out.println(String.format("Waiting for the enough money. Transfer: %s,  Balance: %s, Tx: %s",
                        amount, account, txUuid));
                noBalanceCondition.await(1000, TimeUnit.MILLISECONDS);
            }
            account = account + amount;
            System.out.println(String.format("Transaction uuid %s | Transferred amount: %s | Balance: %s",
                    txUuid, amount, account));
            noBalanceCondition.signalAll();
        } catch (InterruptedException ex) {
            System.out.println("Transaction uuid : " + txUuid + " was cancelled");
        } finally {
            lock.unlock();
        }
        calculateMoneyTransfers(amount);
    }


    private void calculateMoneyTransfers(int amount) {
        if (amount < 0) {
            expenses.addAndGet(amount);
        } else {
            income.addAndGet(amount);
        }
    }

    public static void main(String[] args) {
        OnlineBankingV4 b4 = new OnlineBankingV4(10);

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(b4, -12));
        transactions.add(new Transaction(b4, 5));
        transactions.add(new Transaction(b4, 8));
        transactions.add(new Transaction(b4, -5));
        transactions.add(new Transaction(b4, -8));
        transactions.add(new Transaction(b4, 2));
        transactions.add(new Transaction(b4, -5));
        transactions.add(new Transaction(b4, -2));
        transactions.add(new Transaction(b4, 7));
        transactions.add(new Transaction(b4, 6));
        transactions.add(new Transaction(b4, 3));
        transactions.add(new Transaction(b4, -9));

        transactions.forEach(t -> {
            executorService.submit(t);
        });

        executorService.shutdown();
        try {
            executorService.awaitTermination(50000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            e.printStackTrace();
        }
        System.out.println("Total Income: " + b4.income.get());
        System.out.println("Total Expenses: " + b4.expenses.get());
    }

    static class Transaction implements Runnable {

        private OnlineBankingV4 banking;
        private int transferAmount;
        private String txUuid;

        public Transaction(OnlineBankingV4 banking, int transferAmount) {
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

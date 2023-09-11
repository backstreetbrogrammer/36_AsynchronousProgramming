package com.backstreetbrogrammer.ch02_chainingTasks;

import com.backstreetbrogrammer.model.Database;
import com.backstreetbrogrammer.model.Email;
import com.backstreetbrogrammer.model.MarketData;

import java.util.concurrent.*;

public class ChainingTasksDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        runBlocking();
        runAsync();
    }

    private static void runAsync() {
        final CompletableFuture<MarketData> marketDataCF = CompletableFuture.supplyAsync(() -> getMarketData());
        final CompletableFuture<Database> dbCF = marketDataCF.thenApply(marketData -> writeToDB(marketData));
        final CompletableFuture<Email> emailCF = dbCF.thenApply(db -> emailDatabaseDetails(db));

        CompletableFuture.allOf(marketDataCF, dbCF, emailCF).join();

        // continue....
    }

    private static void runBlocking() throws ExecutionException, InterruptedException {
        final ExecutorService executor = Executors.newFixedThreadPool(4);

        final Future<MarketData> futureMarketData = executor.submit(() -> getMarketData());
        final MarketData marketData = futureMarketData.get();

        final Future<Database> futureDB = executor.submit(() -> writeToDB(marketData));
        final Database db = futureDB.get();

        final Future<Email> futureEmail = executor.submit(() -> emailDatabaseDetails(db));
        final Email email = futureEmail.get();

        // continue....
    }

    private static MarketData getMarketData() {
        return new MarketData("Reuters", "META",
                              ThreadLocalRandom.current().nextDouble(40D, 60D));
    }

    private static Database writeToDB(final MarketData marketData) {
        // some DB operations
        final Database db = new Database("Oracle", "MarketData-Meta");
        System.out.printf("Written MarketData [%s] to DB [%s]%n", marketData, db);
        return db;
    }

    private static Email emailDatabaseDetails(final Database db) {
        // email logic...
        final Email email = new Email("guidemy@mail.com", "rishi@mail.com",
                                      "chaining tasks", "this course is awesome");
        System.out.printf("Email DB [%s] details to [%s]%n", db, email);
        return email;
    }
}

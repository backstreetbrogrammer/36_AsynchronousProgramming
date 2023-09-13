package com.backstreetbrogrammer.ch02_chainingAndSplittingTasks;

import com.backstreetbrogrammer.model.MarketData;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class CompletableFutureAnyOfDemo {

    public static void main(final String[] args) {
        final ThreadLocalRandom random = ThreadLocalRandom.current();

        final Supplier<MarketData> fetchMarketDataReuters =
                () -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(random.nextLong(80L, 120L));
                    } catch (final InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return new MarketData("Reuters", "META", random.nextDouble(40D, 60D));
                };

        final Supplier<MarketData> fetchMarketDataBloomberg =
                () -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(random.nextLong(80L, 120L));
                    } catch (final InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return new MarketData("Bloomberg", "META", random.nextDouble(30D, 70D));
                };

        final Supplier<MarketData> fetchMarketDataExegy =
                () -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(random.nextLong(80L, 120L));
                    } catch (final InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return new MarketData("Exegy", "META", random.nextDouble(40D, 80D));
                };

        final CompletableFuture<MarketData> cfReuters = CompletableFuture.supplyAsync(fetchMarketDataReuters);
        final CompletableFuture<MarketData> cfBloomberg = CompletableFuture.supplyAsync(fetchMarketDataBloomberg);
        final CompletableFuture<MarketData> cfExegy = CompletableFuture.supplyAsync(fetchMarketDataExegy);

        CompletableFuture.anyOf(cfReuters, cfBloomberg, cfExegy) // CompletableFuture<Object>
                         .thenAccept(System.out::println)        // CompletableFuture<Void>
                         .join();

        System.out.printf("cfReuters = %s%n", cfReuters);
        System.out.printf("cfBloomberg = %s%n", cfBloomberg);
        System.out.printf("cfExegy = %s%n", cfExegy);
    }
}

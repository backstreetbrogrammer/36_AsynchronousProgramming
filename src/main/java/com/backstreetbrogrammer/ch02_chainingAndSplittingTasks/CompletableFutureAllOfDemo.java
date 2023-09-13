package com.backstreetbrogrammer.ch02_chainingAndSplittingTasks;

import com.backstreetbrogrammer.model.MarketData;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;

public class CompletableFutureAllOfDemo {

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

        final CompletableFuture<Void> done = CompletableFuture.allOf(cfReuters, cfBloomberg, cfExegy);

        final MarketData bestMarketData =
                done.thenApply(v -> Stream.of(cfReuters, cfBloomberg, cfExegy)  // Stream<CompletableFuture<MarketData>>
                                          .map(CompletableFuture::join)         // Stream<MarketData>
                                          .min(comparing(MarketData::getPrice)) // Optional<MarketData>
                                          .orElseThrow()
                              ).join();

        System.out.printf("Best Priced Market Data: %s%n", bestMarketData);
    }
}

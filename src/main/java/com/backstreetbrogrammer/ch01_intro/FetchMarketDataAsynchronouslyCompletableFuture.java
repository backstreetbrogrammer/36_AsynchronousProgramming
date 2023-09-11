package com.backstreetbrogrammer.ch01_intro;

import com.backstreetbrogrammer.model.MarketData;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class FetchMarketDataAsynchronouslyCompletableFuture {

    public static void main(final String[] args) {
        run();
    }

    public static void run() {
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

        final var marketDataTasks =
                List.of(fetchMarketDataReuters, fetchMarketDataBloomberg, fetchMarketDataExegy);

        final Instant start = Instant.now();

        // run all the tasks asynchronously
        final List<CompletableFuture<MarketData>> futures = new ArrayList<>();
        for (final Supplier<MarketData> task : marketDataTasks) {
            final CompletableFuture<MarketData> future = CompletableFuture.supplyAsync(task);
            futures.add(future);
        }

        final List<MarketData> marketDataList = new ArrayList<>();
        for (final CompletableFuture<MarketData> future : futures) {
            final MarketData marketData = future.join();
            marketDataList.add(marketData);
        }

        final MarketData bestMarketData =
                marketDataList.stream()
                              .min(Comparator.comparing(MarketData::getPrice))
                              .orElseThrow();

        final long timeElapsed = Duration.between(start, Instant.now()).toMillis();
        System.out.printf("Best price [CF ] = %s (%d ms)%n", bestMarketData, timeElapsed);
    }

}

package com.backstreetbrogrammer.ch01_intro;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class FetchMarketDataSynchronously {

    public static void main(final String[] args) {
        run();
    }

    public static void run() {
        final ThreadLocalRandom random = ThreadLocalRandom.current();

        final Callable<MarketData> fetchMarketDataReuters =
                () -> {
                    TimeUnit.MILLISECONDS.sleep(random.nextLong(80L, 120L));
                    return new MarketData("Reuters", "META", random.nextDouble(40D, 60D));
                };

        final Callable<MarketData> fetchMarketDataBloomberg =
                () -> {
                    TimeUnit.MILLISECONDS.sleep(random.nextLong(80L, 120L));
                    return new MarketData("Bloomberg", "META", random.nextDouble(30D, 70D));
                };

        final Callable<MarketData> fetchMarketDataExegy =
                () -> {
                    TimeUnit.MILLISECONDS.sleep(random.nextLong(80L, 120L));
                    return new MarketData("Exegy", "META", random.nextDouble(40D, 80D));
                };

        final var marketDataTasks =
                List.of(fetchMarketDataReuters, fetchMarketDataBloomberg, fetchMarketDataExegy);

        final Instant start = Instant.now();
        final MarketData bestMarketData =
                marketDataTasks.stream()
                               .map(FetchMarketDataSynchronously::fetchMarketData)
                               .min(Comparator.comparing(MarketData::getPrice))
                               .orElseThrow();
        final long timeElapsed = Duration.between(start, Instant.now()).toMillis();
        System.out.printf("Best price [SYNC ] = %s (%d ms)%n", bestMarketData, timeElapsed);
    }

    private static MarketData fetchMarketData(final Callable<MarketData> task) {
        try {
            return task.call();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

}

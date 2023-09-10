package com.backstreetbrogrammer.ch01_intro;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;

public class FetchMarketDataAsynchronouslyExecutorService {

    public static void main(final String[] args) throws ExecutionException, InterruptedException {
        run();
    }

    public static void run() throws ExecutionException, InterruptedException {
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

        final var executor = Executors.newFixedThreadPool(4);
        final Instant start = Instant.now();

        // run all the tasks asynchronously
        final List<Future<MarketData>> futures = executor.invokeAll(marketDataTasks);

        final List<MarketData> marketDataList = new ArrayList<>();
        for (final Future<MarketData> future : futures) {
            final MarketData marketData = future.get();
            marketDataList.add(marketData);
        }

        final MarketData bestMarketData =
                marketDataList.stream()
                              .min(Comparator.comparing(MarketData::getPrice))
                              .orElseThrow();

        final long timeElapsed = Duration.between(start, Instant.now()).toMillis();
        System.out.printf("Best price [ES ] = %s (%d ms)%n", bestMarketData, timeElapsed);

        executor.shutdown();
    }

}

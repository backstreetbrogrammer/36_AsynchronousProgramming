package com.backstreetbrogrammer.ch01_intro;

import java.util.concurrent.ExecutionException;

public class FetchMarketDataAll {

    public static void main(final String[] args) throws ExecutionException, InterruptedException {
        FetchMarketDataSynchronously.run();
        FetchMarketDataAsynchronouslyExecutorService.run();
        FetchMarketDataAsynchronouslyCompletableFuture.run();
    }

}

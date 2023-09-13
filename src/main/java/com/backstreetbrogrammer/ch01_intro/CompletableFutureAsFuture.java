package com.backstreetbrogrammer.ch01_intro;

import java.util.concurrent.*;

public class CompletableFutureAsFuture {

    public Future<String> calculateAsync() {
        final CompletableFuture<String> completableFuture = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            TimeUnit.MILLISECONDS.sleep(500L);
            completableFuture.complete("Hello Students");
            return null;
        });

        return completableFuture;
    }

    public static void main(final String[] args) throws ExecutionException, InterruptedException {
        final CompletableFutureAsFuture obj = new CompletableFutureAsFuture();
        final Future<String> completableFuture = obj.calculateAsync();
        final String result = completableFuture.get(); // blocking call
        System.out.println(result);
    }
}

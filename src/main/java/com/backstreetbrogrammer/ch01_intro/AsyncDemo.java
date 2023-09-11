package com.backstreetbrogrammer.ch01_intro;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AsyncDemo {

    public static void main(final String[] args) throws ExecutionException, InterruptedException {
        final CompletableFuture<String> greetings = CompletableFuture.supplyAsync(() -> "Hello Students");
        final CompletableFuture<Integer> greetingsLength = greetings.thenApply(value -> {
            System.out.println(Thread.currentThread().getName());
            return value.length();
        });
        System.out.println(greetingsLength.get());
    }

}

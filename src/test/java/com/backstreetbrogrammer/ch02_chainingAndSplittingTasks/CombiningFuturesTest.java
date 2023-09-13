package com.backstreetbrogrammer.ch02_chainingAndSplittingTasks;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CombiningFuturesTest {

    @Test
    void testThenComposeMethod() throws ExecutionException, InterruptedException {
        final CompletableFuture<String> completableFuture
                = CompletableFuture.supplyAsync(() -> "Hello")
                                   .thenCompose(s -> CompletableFuture.supplyAsync(
                                           () -> String.format("%s Students", s)));

        assertEquals("Hello Students", completableFuture.get());
    }

    @Test
    void testThenCombineMethod() throws ExecutionException, InterruptedException {
        final CompletableFuture<String> completableFuture
                = CompletableFuture.supplyAsync(() -> "Hello")
                                   .thenCombine(CompletableFuture.supplyAsync(
                                           () -> " Students"), (s1, s2) -> s1 + s2);

        assertEquals("Hello Students", completableFuture.get());
    }

    @Test
    void testThenAcceptBoth() throws ExecutionException, InterruptedException {
        final CompletableFuture<Void> completableFuture
                = CompletableFuture.supplyAsync(() -> "Hello")
                                   .thenAcceptBoth(CompletableFuture.supplyAsync(() -> " Students"),
                                                   (s1, s2) -> System.out.println(s1 + s2));
        assertNull(completableFuture.get());
    }
}

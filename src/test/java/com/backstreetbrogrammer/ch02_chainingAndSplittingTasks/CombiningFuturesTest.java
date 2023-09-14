package com.backstreetbrogrammer.ch02_chainingAndSplittingTasks;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

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

    @Test
    void testSortAndMerge() throws ExecutionException, InterruptedException {
        final int[] array = new int[]{2, 29, 3, 0, 11, 8, 32, 94, 9, 1, 7};

        final CompletableFuture<int[]> completableFuture
                = CompletableFuture.supplyAsync(() -> Arrays.stream(array)
                                                            .filter(i -> i % 2 == 0) // evens
                                                            .sorted()
                                                            .toArray())
                                   .thenCombine(CompletableFuture.supplyAsync(
                                                        () -> Arrays.stream(array)
                                                                    .filter(i -> i % 2 != 0) // odds
                                                                    .sorted()
                                                                    .toArray()),
                                                (sortedEvens, sortedOdds) ->
                                                        IntStream.concat(Arrays.stream(sortedEvens),
                                                                         Arrays.stream(sortedOdds)).toArray());

        final int[] mergedArray = completableFuture.get();
        for (final int ele : mergedArray) {
            System.out.printf("%d, ", ele);
        }
        System.out.println();
    }
}

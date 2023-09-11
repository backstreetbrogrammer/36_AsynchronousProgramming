package com.backstreetbrogrammer.ch01_intro;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AsyncDemoTest {

    @Test
    void testNonAsyncMethod() throws ExecutionException, InterruptedException {
        final CompletableFuture<String> greetings = CompletableFuture.supplyAsync(() -> "Hello Students");
        final CompletableFuture<Integer> greetingsLength = greetings.thenApply(value -> {
            System.out.println(Thread.currentThread().getName());
            return value.length();
        });
        assertEquals(14, greetingsLength.get());
    }

    @Test
    void testAsyncMethod() throws ExecutionException, InterruptedException {
        final CompletableFuture<String> greetings = CompletableFuture.supplyAsync(() -> "Hello Students");
        final CompletableFuture<Integer> greetingsLength = greetings.thenApplyAsync(value -> {
            System.out.println(Thread.currentThread().getName());
            return value.length();
        });
        assertEquals(14, greetingsLength.get());
    }

    @Test
    void testAsyncMethodUsingExecutor() throws ExecutionException, InterruptedException {
        final Executor testExecutor = Executors.newFixedThreadPool(4);
        final CompletableFuture<String> greetings = CompletableFuture.supplyAsync(() -> "Hello Students");
        final CompletableFuture<Integer> greetingsLength = greetings.thenApplyAsync(value -> {
            System.out.println(Thread.currentThread().getName());
            return value.length();
        }, testExecutor);
        assertEquals(14, greetingsLength.get());
    }

}

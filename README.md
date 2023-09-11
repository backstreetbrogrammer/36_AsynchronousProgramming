# Asynchronous Programming in Java

> This is a tutorials course covering asynchronous programming in Java.

Tools used:

- JDK 11
- Maven
- JUnit 5, Mockito
- IntelliJ IDE

## Table of contents

1. [Introduction to asynchronous programming](https://github.com/backstreetbrogrammer/36_AsynchronousProgramming#chapter-01-introduction-to-asynchronous-programming)
    - [Synchronous vs Asynchronous](https://github.com/backstreetbrogrammer/36_AsynchronousProgramming#synchronous-vs-asynchronous)
    - [Introduction to CompletableFuture](https://github.com/backstreetbrogrammer/36_AsynchronousProgramming#introduction-to-completablefuture)
    - [Interview Problem 1 (SCB): Design an API to fetch the best market data from different providers](https://github.com/backstreetbrogrammer/36_AsynchronousProgramming#interview-problem-1-scb-design-an-api-to-fetch-the-best-market-data-from-different-providers)
2. [Chaining tasks](https://github.com/backstreetbrogrammer/36_AsynchronousProgramming#chapter-02-chaining-tasks)
3. Splitting tasks
4. Controlling threads executing tasks
5. Error handling
6. Best patterns

---

## Chapter 01. Introduction to asynchronous programming

Having **high performance** and **availability** are essential parts of modern software development.

In other words, we need to write applications which has **maximum throughput** and **lowest latency**.

**Throughput**

> Throughput is a measure of how many units of information a system can process in a given amount of time.

For example, we need to improve performances for Input / Output operations.

Input / Output operations may be:

- accessing data from a disk
- over a network
- from a database

We need to understand the **asynchronous** programming paradigm to improve the **throughput** of our applications.

### Synchronous vs Asynchronous

**Synchronous**

> We need to wait for a task to complete to continue to work.

**Code snippet**

```
HTTPClient client = ...;
String response = client.get("https://github.com/backstreetbrogrammer/data");
```

We need to wait for the server to send us the response to process it. It may be several 100ms. In the meantime, our CPU
is doing nothing.

It's similar to a **blocking** operation in which the calling thread waits until the operation in another thread
completes before continuing with its execution:

![Blocking](Blocking.PNG)

Here, the tasks execute **sequentially**. `Thread 1` is blocked by `Thread 2`. In other words, `Thread 1` can't continue
with its execution until `Thread 2` finishes processing its tasks.

```
Synchronous == Blocking
```

A **synchronous** code is always **blocking**. It will slow down our application if it blocks for a long time.

**Asynchronous**

> The code we write will be executed at some point in the future.

**Code snippet**

```
List<String> strings = ...;
strings.forEach(s -> System.out.println(s));
```

Printing the elements of the list is done between `0` and `N` times => at some point in the future.

A **non-blocking** operation allows threads to perform multiple computations simultaneously without having to wait for
each task to complete.

The current thread can continue with its execution while the other threads perform tasks in parallel:

![NonBlocking](NonBlocking.PNG)

In the example above, `Thread 2` isn't blocking the execution of `Thread 1`. Furthermore, both threads are running their
tasks concurrently.

Beside improving the performance, we can decide what to do with the result once the non-blocking operation finishes with
execution.

Asynchronous programming may be used to avoid blocking calls and make our application faster.

The main advantage of using `CompletableFuture` is its ability to chain multiple tasks together that will be executed
without blocking the current thread. Therefore, we can say the `CompletableFuture` is **non-blocking**.

However, **asynchronous** and **synchronous** are **_NOT_** related to **concurrent** programming.

**Asynchronous** programming may rely on concurrency, but not always.

```
Asynchronous + Concurrency
```

Running a blocking code in another thread is a way to avoid blocking the main thread of our application.

```
        ExecutorService service = ...;
        HTTPClient client = ...;
        Future<String> future =
                service.submit(() – >
                        client.get("https://github.com/backstreetbrogrammer/data"));
        // do some other stuff
        String response = future.get();
```

The call to `get()` is still a **blocking** call, but blocks another thread and not the `main` thread. Our application
thread is free to do something else.

We can get the response through this `future` object By calling `future.get()`, which is a blocking call.

### Introduction to CompletableFuture

Java 8's Concurrent API introduced `CompletableFuture`, a valuable tool for simplifying asynchronous and non-blocking
programming.

The `CompletableFuture` class implements `CompletionStage` interface and the `Future` interface.

`CompletableFuture` offers an extensive API consisting of more than `50` methods. Many of these methods are available in
two variants: **non-async** and **async**.

**Non-async methods**

```
    @Test
    void testNonAsyncMethod() throws ExecutionException, InterruptedException {
        final CompletableFuture<String> greetings = CompletableFuture.supplyAsync(() -> "Hello Students");
        final CompletableFuture<Integer> greetingsLength = greetings.thenApply(value -> {
            System.out.println(Thread.currentThread().getName());
            return value.length();
        });
        assertEquals(14, greetingsLength.get());
    }
```

**Sample output**

```
ForkJoinPool.commonPool-worker-3
(or)
main
```

When utilizing `thenApply()`, we pass a function as a parameter that takes the previous value of the `CompletableFuture`
as input, performs an operation, and returns a new value. Consequently, a fresh `CompletableFuture` is created to
encapsulate the resulting value.

The function passed as a parameter to `thenApply()` will be executed by the thread that directly interacts with
`CompletableFuture`'s API, in our case, the `main` thread **OR** `ForkJoinPool.commonPool()` thread.

**Async methods**

The majority of methods within the API possess an **asynchronous** counterpart. We can use these `async` variants to
ensure that the intermediate operations are executed on a separate thread pool. Let's change the previous code example
and switch from `thenApply()` to `thenApplyAsync()`:

```
    @Test
    void testAsyncMethod() throws ExecutionException, InterruptedException {
        final CompletableFuture<String> greetings = CompletableFuture.supplyAsync(() -> "Hello Students");
        final CompletableFuture<Integer> greetingsLength = greetings.thenApplyAsync(value -> {
            System.out.println(Thread.currentThread().getName());
            return value.length();
        });
        assertEquals(14, greetingsLength.get());
    }
```

**Sample output**

```
ForkJoinPool.commonPool-worker-3
```

If we use the `async` methods without explicitly providing an `Executor`, the functions will be executed using
`ForkJoinPool.commonPool()` thread.

Printing the same example using `Executor`:

```
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
```

**Sample output**

```
pool-1-thread-1
```

This is the thread name given to the thread pool in `Executor`.

As expected, when using the overloaded method, the `CompletableFuture` will no longer use the common `ForkJoinPool`
threads but the `Executor` thread pool.

### Interview Problem 1 (SCB): Design an API to fetch the best market data from different providers

We have 3 different stock market data providers which provide market data in real time.

Design an API to select the market data which has got the best (lowest) price to buy a stock.

**Solution**

`MarketData` class:

```java
public class MarketData {

    private final String server;
    private final String symbol;
    private final double price;

    public MarketData(final String server, final String symbol, final double price) {
        this.server = server;
        this.symbol = symbol;
        this.price = price;
    }

    public String getServer() {
        return server;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "MarketData{" +
                "server='" + server + '\'' +
                ", symbol='" + symbol + '\'' +
                ", price=" + price +
                '}';
    }
}
```

- First Approach: Using **Synchronous** API

```java
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
```

**Sample Output**

```
Best price [SYNC ] = MarketData{server='Exegy', symbol='META', price=41.398256277344316} (360 ms)
```

Running it multiple times gives us an average of `280-380 ms`.

- Second Approach: Using **Asynchronous** API with `ExecutorService`

```java
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;

public class FetchMarketDataAsynchronouslyExecutorService {

    public static void main(final String[] args) throws ExecutionException, InterruptedException {
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
```

**Sample Output**

```
Best price [ES ] = MarketData{server='Reuters', symbol='META', price=56.48773820323322} (130 ms)
```

Running it multiple times gives us an average of `120-135 ms`.

- Final Approach: Using **Asynchronous** API with `CompletableFuture`

```java
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
```

**Sample Output**

```
Best price [CF ] = MarketData{server='Bloomberg', symbol='META', price=35.023580893186185} (143 ms)
```

Running it multiple times gives us an average of `130-145 ms`.

- Running all the tasks together to better compare the performance

```java
import java.util.concurrent.ExecutionException;

public class FetchMarketDataAll {

    public static void main(final String[] args) throws ExecutionException, InterruptedException {
        FetchMarketDataSynchronously.run();
        FetchMarketDataAsynchronouslyExecutorService.run();
        FetchMarketDataAsynchronouslyCompletableFuture.run();
    }

}
```

**Sample Output**

```
Best price [SYNC ] = MarketData{server='Reuters', symbol='META', price=58.52626690961024} (316 ms)
Best price [ES ] = MarketData{server='Reuters', symbol='META', price=56.48773820323322} (123 ms)
Best price [CF ] = MarketData{server='Reuters', symbol='META', price=56.48773820323322} (126 ms)
```

As seen from the above results, fetching the data **asynchronously** helps to increase the **throughput** of the
application.

---

## Chapter 02. Chaining tasks

We can trigger an **async** task after the **completion** of another **async** task. In this way, we can chain
multiple **async** tasks.

Let's take an example where:

- we get **MarketData** from a provider
- then we store it in **Database**
- then we send the **Database** details in the **email** to the backoffice team.

**Code snippet**

```
        final ExecutorService executor = Executors.newFixedThreadPool(4);

        final Future<MarketData> futureMarketData = executor.submit(() -> getMarketData());
        final MarketData marketData = futureMarketData.get();

        final Future<Database> futureDB = executor.submit(() -> writeToDB(marketData));
        final Database db = futureDB.get();

        final Future<Email> futureEmail = executor.submit(() -> emailDatabaseDetails(db));
        final Email email = futureEmail.get();

        // continue....
```

A thread is a scarce resource and blocking a thread for a long time is expensive!

We want to avoid having to get the result back to the `main` thread.

The **solution** is to trigger a task on the outcome of another task.

We can also trigger **multiple** tasks after the outcome of **one** task and also trigger **one** task after the outcome
of **multiple** tasks.

**Code snippet**

```
        final CompletableFuture<MarketData> marketDataCF = CompletableFuture.supplyAsync(() -> getMarketData());
        final CompletableFuture<Database> dbCF = marketDataCF.thenApply(marketData -> writeToDB(marketData));
        final CompletableFuture<Email> emailCF = dbCF.thenApply(db -> emailDatabaseDetails(db));

        // continue....
        
        // if want to wait for all the independent tasks like CountDownLatch
        // CompletableFuture.allOf(marketDataCF, dbCF, emailCF).join();
```

Besides having `thenApply()` method, there are several other methods available in
[CompletionStage API](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/CompletionStage.html)

The main methods are:

```
stage.thenApply(x -> square(x))                // Function
      .thenAccept(x -> System.out.print(x))    // Consumer
      .thenRun(() -> System.out.println());    // Runnable
```


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

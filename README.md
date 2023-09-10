# Asynchronous Programming in Java

> This is a tutorials course covering asynchronous programming in Java.

Tools used:

- JDK 11
- Maven
- JUnit 5, Mockito
- IntelliJ IDE

## Table of contents

1. [Introduction to asynchronous programming](https://github.com/backstreetbrogrammer/36_AsynchronousProgramming#chapter-01-introduction-to-asynchronous-programming)
2. Chaining tasks
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




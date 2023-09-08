# Asynchronous Programming in Java

> This is a tutorials course covering asynchronous programming in Java.

Tools used:

- JDK 11
- Maven
- JUnit 5, Mockito
- IntelliJ IDE

## Table of contents

1. Introduction to asynchronous programming
2. Chaining tasks
3. Splitting tasks
4. Controlling threads executing tasks
5. Error handling
6. Best patterns

---

## Chapter 01. Introduction to asynchronous programming

Having **high performance** and **availability** are essential parts of modern software development.

This can be simply termed as we need to write applications which has **maximum throughput** and **lowest latency**.

**Throughput**

> Throughput is a measure of how many units of information a system can process in a given amount of time.

For example, we need to improve performances for Input / Output operations.

Input / Output operations may be:

- accessing data from a disk
- over a network
- from a database

We need to understand the **asynchronous** programming paradigm to improve the **throughput** of our applications.


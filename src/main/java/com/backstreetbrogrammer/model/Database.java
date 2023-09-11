package com.backstreetbrogrammer.model;

public class Database {

    private final String databaseName;
    private final String tableName;

    public Database(final String databaseName, final String tableName) {
        this.databaseName = databaseName;
        this.tableName = tableName;
    }

    @Override
    public String toString() {
        return "Database{" +
                "databaseName='" + databaseName + '\'' +
                ", tableName='" + tableName + '\'' +
                '}';
    }
}

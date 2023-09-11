package com.backstreetbrogrammer.model;

public class Email {

    private final String recipient;
    private final String sender;
    private final String subject;
    private final String body;

    public Email(final String recipient, final String sender, final String subject, final String body) {
        this.recipient = recipient;
        this.sender = sender;
        this.subject = subject;
        this.body = body;
    }

    @Override
    public String toString() {
        return "Email{" +
                "recipient='" + recipient + '\'' +
                ", sender='" + sender + '\'' +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                '}';
    }

}

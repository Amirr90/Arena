package com.e.arena.Model;

public class TransactionModel {

    private String to;
    private String from;
    private String tranasction_id;
    private long complete_at;
    private long credits;
    private long timestamp;

    public TransactionModel(String to, String tranasction_id) {
        this.to = to;
        this.tranasction_id = tranasction_id;
    }

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }

    public String getTranasction_id() {
        return tranasction_id;
    }

    public long getComplete_at() {
        return complete_at;
    }

    public long getCredits() {
        return credits;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

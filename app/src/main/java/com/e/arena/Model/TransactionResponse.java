package com.e.arena.Model;

public class TransactionResponse {
    private boolean status;
    private String msg;
    private double tId;

    public TransactionResponse(boolean status, String msg, double tId) {
        this.status = status;
        this.msg = msg;
        this.tId = tId;
    }

    public boolean isStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public double gettId() {
        return tId;
    }
}

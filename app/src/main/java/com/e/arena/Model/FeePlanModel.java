package com.e.arena.Model;

import java.util.List;

public class FeePlanModel {

    double totalSaving;
    double totalFeePaid;
    double payToTutor;
    double TotalCommisionForCompney;
    List<FeePlanModel2> FeeData;


    public FeePlanModel() {
    }

    public double getTotalSaving() {
        return totalSaving;
    }

    public double getTotalFeePaid() {
        return totalFeePaid;
    }

    public double getPayToTutor() {
        return payToTutor;
    }

    public double getTotalCommisionForCompney() {
        return TotalCommisionForCompney;
    }

    public List<FeePlanModel2> getFeeData() {
        return FeeData;
    }
}

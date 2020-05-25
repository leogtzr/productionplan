package com.production.domain;

public class WorkOrderInformation {

    private String partNumber;
    private String workOrder;
    private double runHours;
    private double setupHours;
    private int qty;
    // TODO: this field is filled by the "Age by WC.xlsx" file.
    private int age;

    public WorkOrderInformation(String partNumber, String workOrder, double runHours, double setupHours, int qty) {
        this.partNumber = partNumber;
        this.workOrder = workOrder;
        this.runHours = runHours;
        this.setupHours = setupHours;
        this.qty = qty;
    }

    public WorkOrderInformation() {
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getWorkOrder() {
        return workOrder;
    }

    public void setWorkOrder(String workOrder) {
        this.workOrder = workOrder;
    }

    public double getRunHours() {
        return runHours;
    }

    public void setRunHours(double runHours) {
        this.runHours = runHours;
    }

    public double getSetupHours() {
        return setupHours;
    }

    public void setSetupHours(double setupHours) {
        this.setupHours = setupHours;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    @Override
    public String toString() {
        return "WorkOrderInformation{" +
                "partNumber='" + partNumber + '\'' +
                ", workOrder='" + workOrder + '\'' +
                ", runHours=" + runHours +
                ", setupHours=" + setupHours +
                ", qty=" + qty +
                '}';
    }
}

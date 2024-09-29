package com.app.ecarepro.ui;

public class RPT {
    private String AttDate;
    private String Temp;
    private String MarkedTime;

    public String getRFTagIn() {
        return RFTagIn;
    }

    public void setRFTagIn(String RFTagIn) {
        this.RFTagIn = RFTagIn;
    }

    public String getRFTagOut() {
        return RFTagOut;
    }

    public void setRFTagOut(String RFTagOut) {
        this.RFTagOut = RFTagOut;
    }

    private String RFTagIn;
    private String RFTagOut;
    private int Status;
    private int ActID;

    public Boolean getLate() {
        return isLate;
    }

    public void setLate(Boolean late) {
        isLate = late;
    }

    private Boolean isLate;
    private String Title;

    private int isWorking;

    public String getMarkedTime() {
        return MarkedTime;
    }

    public void setMarkedTime(String markedTime) {
        MarkedTime = markedTime;
    }

    private int Duration;

    private String FromDate;

    private String TillDate;

    public String getTemp() {
        return Temp;
    }

    public void setTemp(String temp) {
        Temp = temp;
    }

    public int getActID() {
        return this.ActID;
    }

    public void setActID(int ActID) {
        this.ActID = ActID;
    }

    public String getTitle() {
        return this.Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public int getIsWorking() {
        return this.isWorking;
    }

    public void setIsWorking(int isWorking) {
        this.isWorking = isWorking;
    }

    public int getDuration() {
        return this.Duration;
    }

    public void setDuration(int Duration) {
        this.Duration = Duration;
    }

    public String getFromDate() {
        return this.FromDate;
    }

    public void setFromDate(String FromDate) {
        this.FromDate = FromDate;
    }

    public String getTillDate() {
        return this.TillDate;
    }

    public void setTillDate(String TillDate) {
        this.TillDate = TillDate;
    }

    public String getAttDate() {
        return this.AttDate;
    }

    public void setAttDate(String AttDate) {
        this.AttDate = AttDate;
    }

    public int getStatus() {
        return this.Status;
    }

    public void setStatus(int Status) {
        this.Status = Status;
    }
}
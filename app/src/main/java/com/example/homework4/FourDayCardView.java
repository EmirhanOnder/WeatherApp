package com.example.homework4;

public class FourDayCardView {
    String day;
    String tempMax;
    String tempMin;
    String iconPath;

    public FourDayCardView(String day, String tempMax, String tempMin, String iconPath) {
        this.day = day;
        this.tempMax = tempMax;
        this.tempMin = tempMin;
        this.iconPath = iconPath;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTempMax() {
        return tempMax;
    }

    public void setTempMax(String tempMax) {
        this.tempMax = tempMax;
    }

    public String getTempMin() {
        return tempMin;
    }

    public void setTempMin(String tempMin) {
        this.tempMin = tempMin;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }
}

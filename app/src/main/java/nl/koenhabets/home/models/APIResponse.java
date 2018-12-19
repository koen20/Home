package nl.koenhabets.home.models;

import com.google.gson.annotations.SerializedName;

public class APIResponse {

    private final String nextSubject;

    @SerializedName("next-appointment")
    private final String nextAppointment;

    @SerializedName("outside-temp")
    private final float temperatureOutside;

    @SerializedName("inside-temp")
    private final float temperatureInside;

    @SerializedName("sleeping")
    private final boolean sleeping;

    @SerializedName("light-A")
    private final boolean lightA;

    @SerializedName("light-B")
    private final boolean lightB;

    @SerializedName("light-C")
    private final boolean lightC;

    @SerializedName("alarmEnabled")
    private final boolean alarmEnabled;

    @SerializedName("motionEnabled")
    private final boolean motionEnabled;

    @SerializedName("fishLastFed")
    private final String fishLastFed;

    @SerializedName("pcOn")
    private final boolean pcOn;

    @SerializedName("feedInterval")
    private final int feedInterval;

    private final int foodDaysLeft;
    private final boolean espLed;
    private final boolean ledStrip;
    private final int ledRed;
    private final int ledGreen;
    private final int ledBlue;
    private boolean lamp1;

    public APIResponse(String nextSubject, String nextAppointment, float temperatureOutside, float temperatureInside, boolean sleeping,
                       boolean lightA, boolean lightB, boolean lightC, boolean alarmEnabled, boolean motionEnabled, String fishLastFed,
                       boolean pcOn, int feedInterval, int foodDaysLeft, boolean espLed, boolean ledStrip, int ledRed, int ledGreen, int ledBlue, boolean lamp1) {
        this.nextSubject = nextSubject;
        this.nextAppointment = nextAppointment;
        this.temperatureOutside = temperatureOutside;
        this.temperatureInside = temperatureInside;
        this.sleeping = sleeping;
        this.lightA = lightA;
        this.lightB = lightB;
        this.lightC = lightC;
        this.alarmEnabled = alarmEnabled;
        this.motionEnabled = motionEnabled;
        this.fishLastFed = fishLastFed;
        this.pcOn = pcOn;
        this.feedInterval = feedInterval;
        this.foodDaysLeft = foodDaysLeft;
        this.espLed = espLed;
        this.ledStrip = ledStrip;
        this.ledRed = ledRed;
        this.ledGreen = ledGreen;
        this.ledBlue = ledBlue;
        this.lamp1 = lamp1;
    }

    public String getNextSubject() {
        return nextSubject;
    }

    public String getNextAppointment() {
        return nextAppointment;
    }

    public float getTemperatureOutside() {
        return temperatureOutside;
    }

    public float getTemperatureInside() {
        return temperatureInside;
    }

    public boolean isSleeping() {
        return sleeping;
    }

    public boolean getLightA() {
        return lightA;
    }

    public boolean getLightB() {
        return lightB;
    }

    public boolean getLightC() {
        return lightC;
    }

    public boolean getAlarmEnabled() {
        return alarmEnabled;
    }

    public boolean getMotionEnabled() {
        return motionEnabled;
    }

    public String getFishLastFed() {
        return fishLastFed;
    }

    public boolean getPcOn() {
        return pcOn;
    }

    public int getFeedInterval() {
        return feedInterval;
    }

    public int getFoodDaysLeft() {
        return foodDaysLeft;
    }

    public boolean isEspLed() {
        return espLed;
    }

    public boolean isLedStrip() {
        return ledStrip;
    }

    public int getLedRed() {
        return ledRed;
    }

    public int getLedGreen() {
        return ledGreen;
    }

    public int getLedBlue() {
        return ledBlue;
    }

    public boolean isLamp1() {
        return lamp1;
    }
}

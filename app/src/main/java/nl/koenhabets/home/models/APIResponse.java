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

    public APIResponse(String nextSubject, String nextAppointment, float temperatureOutside, float temperatureInside, boolean sleeping, boolean lightA, boolean lightB, boolean lightC, boolean alarmEnabled) {
        this.nextSubject = nextSubject;
        this.nextAppointment = nextAppointment;
        this.temperatureOutside = temperatureOutside;
        this.temperatureInside = temperatureInside;
        this.sleeping = sleeping;
        this.lightA = lightA;
        this.lightB = lightB;
        this.lightC = lightC;
        this.alarmEnabled = alarmEnabled;
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
}

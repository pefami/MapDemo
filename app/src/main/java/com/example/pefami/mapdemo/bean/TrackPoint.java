package com.example.pefami.mapdemo.bean;

/**
 * Created by pefami on 2016/9/17.
 */
public class TrackPoint {
    private String trackid;
    private double lantitude;
    private double longitude;
    private long time;
    private double speed;

    public TrackPoint() {
    }
    public TrackPoint(String trackid, double lantitude, double longitude, long time, double speed) {
        this.trackid = trackid;
        this.lantitude = lantitude;
        this.longitude = longitude;
        this.time = time;
        this.speed = speed;
    }

    public String getTrackid() {
        return trackid;
    }

    public void setTrackid(String trackid) {
        this.trackid = trackid;
    }

    public double getLantitude() {
        return lantitude;
    }

    public void setLantitude(double lantitude) {
        this.lantitude = lantitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    @Override
    public String toString() {
        return "TrackPoint{" +
                "trackid='" + trackid + '\'' +
                ", lantitude=" + lantitude +
                ", longitude=" + longitude +
                ", time=" + time +
                ", speed=" + speed +
                '}';
    }
}

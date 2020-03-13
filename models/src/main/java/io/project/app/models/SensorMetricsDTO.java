package io.project.app.models;

import java.io.Serializable;
import java.util.Objects;

public class SensorMetricsDTO implements Serializable {

    public SensorMetricsDTO() {
    }

    private String sensorId;

    private long timestamp;

    private double ambientTemperature;

    public double humidity;

    public double photosensor;

    public double radiationLevel;

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getAmbientTemperature() {
        return ambientTemperature;
    }

    public void setAmbientTemperature(double ambientTemperature) {
        this.ambientTemperature = ambientTemperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getPhotosensor() {
        return photosensor;
    }

    public void setPhotosensor(double photosensor) {
        this.photosensor = photosensor;
    }

    public double getRadiationLevel() {
        return radiationLevel;
    }

    public void setRadiationLevel(double radiationLevel) {
        this.radiationLevel = radiationLevel;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SensorMetricsDTO other = (SensorMetricsDTO) obj;
        if (this.timestamp != other.timestamp) {
            return false;
        }
        if (Double.doubleToLongBits(this.ambientTemperature) != Double.doubleToLongBits(other.ambientTemperature)) {
            return false;
        }
        if (Double.doubleToLongBits(this.humidity) != Double.doubleToLongBits(other.humidity)) {
            return false;
        }
        if (Double.doubleToLongBits(this.photosensor) != Double.doubleToLongBits(other.photosensor)) {
            return false;
        }
        if (Double.doubleToLongBits(this.radiationLevel) != Double.doubleToLongBits(other.radiationLevel)) {
            return false;
        }
        return Objects.equals(this.sensorId, other.sensorId);
    }

    @Override
    public String toString() {
        return "SensorMetricsDTO{" + "sensorId=" + sensorId + ", timestamp=" + timestamp + ", ambientTemperature=" + ambientTemperature + ", humidity=" + humidity + ", photosensor=" + photosensor + ", radiationLevel=" + radiationLevel + '}';
    }

}

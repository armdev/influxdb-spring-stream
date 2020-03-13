package io.project.app.iot.model;

import java.io.Serializable;
import java.time.Instant;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Measurement(name = "sensors")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SensorMetrics implements Serializable {   
    
    @Column(name = "time")
    private Instant time;
     
    @Column(name = "sensorId")
    private String sensorId;
    
    @Column(name = "timestamp")
    private long timestamp;

    @Column(name = "ambientTemperature")
    private double ambientTemperature;

    @Column(name = "humidity")
    public double humidity;

    @Column(name = "photosensor")
    public double photosensor;

    @Column(name = "radiationLevel")
    public double radiationLevel;

}




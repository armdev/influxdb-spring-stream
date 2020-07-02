/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.project.app.iot.services;

import io.project.app.iot.model.SensorMetrics;
import io.project.app.iot.repositories.CollectorRepository;
import io.project.app.models.SensorMetricsDTO;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author root
 */
@Service
@Slf4j
public class CollectorService {

    @Autowired
    private CollectorRepository collectorRepository;

    public void saveStream(final SensorMetricsDTO deviceMetrics) {
        log.info("Save new stream with device id " + deviceMetrics.getSensorId());
        collectorRepository.saveStreamData(deviceMetrics);
    }

    public List<SensorMetrics> getAllMetrics() {
        log.info("get All metrics ");
        return collectorRepository.getAllMetrics();
    }

    public List<SensorMetrics> getAllMetricsForSensor(String sensorId) {
        log.info("Get All metrics with sensor id ");
        return collectorRepository.getLastLoadMetrics(sensorId, 10);

    }

    public List<SensorMetrics> getMaxRadiationForSensor(String sensorId) {
        log.info("Get getMaxRadiationForSensor ");
        return collectorRepository.getMaxRadiationForSensor(sensorId);

    }
}

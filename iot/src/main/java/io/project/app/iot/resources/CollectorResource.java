/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.project.app.iot.resources;

import io.project.app.iot.model.DeviceMetrics;
import io.project.app.iot.services.CollectorService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author root
 */
@RestController
@RequestMapping("/api/v2/sensors")
public class CollectorResource {

    @Autowired
    private CollectorService collectorService;

    @PostMapping(path = "/push", produces = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Operation Success"),
        @ApiResponse(code = 201, message = "Operation done"),
        @ApiResponse(code = 400, message = "Could not done operation")
    }
    )
    public ResponseEntity<?> push(@RequestBody DeviceMetrics deviceMetrics) {
        collectorService.saveStream(deviceMetrics);
        return ResponseEntity.status(HttpStatus.OK).body("Stream pushed");
    }

    @GetMapping(path = "/fetch/metrics", produces = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Operation Success"),
        @ApiResponse(code = 201, message = "Operation done"),
        @ApiResponse(code = 400, message = "Could not done operation")
    }
    )
    public ResponseEntity<?> fetch() {
        final List<DeviceMetrics> allMetrics = collectorService.getAllMetrics();
        return ResponseEntity.status(HttpStatus.OK).body(allMetrics);
    }

    @GetMapping(path = "/fetch/metrics/id", produces = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Operation Success"),
        @ApiResponse(code = 201, message = "Operation done"),
        @ApiResponse(code = 400, message = "Could not done operation")
    }
    )
    public ResponseEntity<?> get(@RequestParam String sensorId) {
        final List<DeviceMetrics> allMetrics = collectorService.getAllMetricsForSensor(sensorId);
        return ResponseEntity.status(HttpStatus.OK).body(allMetrics);
    }

}

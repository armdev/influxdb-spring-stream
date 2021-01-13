package io.project.app.iot.resources;

import io.project.app.iot.model.SensorMetrics;
import io.project.app.iot.services.CollectorService;
import io.project.app.models.SensorMetricsDTO;
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
    public ResponseEntity<?> push(@RequestBody SensorMetricsDTO deviceMetrics) {
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
        final List<SensorMetrics> allMetrics = collectorService.getAllMetrics();
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
        final List<SensorMetrics> allMetrics = collectorService.getAllMetricsForSensor(sensorId);
        return ResponseEntity.status(HttpStatus.OK).body(allMetrics);
    }
    
    
    
    @GetMapping(path = "/fetch/metrics/id/radiation", produces = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Operation Success"),
        @ApiResponse(code = 201, message = "Operation done"),
        @ApiResponse(code = 400, message = "Could not done operation")
    }
    )
    public ResponseEntity<?> radiationLevel(@RequestParam String sensorId) {
        final List<SensorMetrics> allMetrics = collectorService.getMaxRadiationForSensor(sensorId);
        return ResponseEntity.status(HttpStatus.OK).body(allMetrics);
    }

}

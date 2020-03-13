package io.project.app.sensor.data.publisher;

import io.project.app.models.SensorMetricsDTO;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

@Controller
@Service
@Data
@Slf4j
public class DataStreamingResource {

    private String[] sensorsArray = new String[]{"sensor1", "sensor2", "sensor3", "sensor4"};

    private List<String> sensors = Arrays.asList(sensorsArray);

    private List<SensorMetricsDTO> dataList = new ArrayList<>();

    @MessageMapping("send.data")
    public Flux<SensorMetricsDTO> dataGenerator() {
        return Flux.fromIterable(this.getDataList())
                .delayElements(Duration.ofMillis(100));

    }

    @Scheduled(fixedDelay = 30000)
    public void dataPublisher() {
        log.info("Starting new stream data pushing");
        SensorMetricsDTO sensorMetricsDTO = null;
        for (String sensor : sensors) {
            sensorMetricsDTO = new SensorMetricsDTO();
            sensorMetricsDTO.setAmbientTemperature(this.getNextDouble());
            sensorMetricsDTO.setHumidity(this.getNextDouble());
            sensorMetricsDTO.setPhotosensor(this.getNextDouble());
            sensorMetricsDTO.setRadiationLevel(this.getNextDouble());
            sensorMetricsDTO.setTimestamp(this.getNextTimestamp());
            sensorMetricsDTO.setSensorId(sensor);
            dataList.add(sensorMetricsDTO);
        }

    }

    public double getNextDouble() {
        double min = 1;
        double max = 50;
        Random r = new Random();
        return min + (max - min) * r.nextDouble();
    }

    public long getNextTimestamp() {
        long time = new Date(System.currentTimeMillis()).getTime();
        return time;
    }

}

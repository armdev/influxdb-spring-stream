package io.project.app.sensor.data.publisher;

import io.project.app.models.SensorMetricsDTO;
import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
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

    private String[] sensorsArray = new String[]{"sensor1", "sensor2", "sensor3", "sensor4", "sensor5"};

    private List<String> sensors = Arrays.asList(sensorsArray);

    private Set<SensorMetricsDTO> dataList = new HashSet<>();

    @MessageMapping("send.data")
    public Flux<SensorMetricsDTO> dataGenerator() {
        Set<SensorMetricsDTO> sendList = this.getDataList();
        log.info("List size " + sendList.size());
        return Flux.fromIterable(sendList)
                .delayElements(Duration.ofMillis(100));

    }

    @Scheduled(fixedDelay = 300)
    //@Scheduled(initialDelay = 1000 * 30, fixedDelay=Long.MAX_VALUE)
    public void dataPublisher() {
        dataList = new HashSet<>();
        SensorMetricsDTO sensorMetricsDTO = new SensorMetricsDTO();
        for (Iterator<String> it = sensors.iterator(); it.hasNext();) {
            String sensor = it.next();
            sensorMetricsDTO = new SensorMetricsDTO();
            sensorMetricsDTO.setAmbientTemperature(this.getNextDouble());
            sensorMetricsDTO.setHumidity(this.getNextDouble());
            sensorMetricsDTO.setPhotosensor(this.getNextDouble());
            sensorMetricsDTO.setRadiationLevel(this.getNextDouble());
            sensorMetricsDTO.setTimestamp(this.getNextTimestamp());
            sensorMetricsDTO.setSensorId(sensor);
            log.info("Starting new stream for sensor " + sensor);
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

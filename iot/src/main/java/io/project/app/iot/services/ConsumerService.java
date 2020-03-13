package io.project.app.iot.services;

import io.project.app.models.SensorMetricsDTO;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;

import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ConsumerService {

    private final Mono<RSocketRequester> requesterMono;

    @Autowired
    private CollectorService collectorService;

    public ConsumerService(RSocketRequester.Builder builder) {
        this.requesterMono = builder
                .dataMimeType(MediaType.APPLICATION_CBOR)
                .connectTcp("sensor", 2050).retry(5).cache();
    }

    @Scheduled(fixedDelay = 3000)
    public Flux<SensorMetricsDTO> get() {
        Flux<SensorMetricsDTO> take = this.requesterMono
                .flatMapMany(req
                        -> req.route("send.data")
                        .retrieveFlux(SensorMetricsDTO.class))
                .take(10);
        List<SensorMetricsDTO> blockFirst = take.buffer().blockFirst();
        for (SensorMetricsDTO sm : blockFirst) {
            log.info("Data from sensor " + sm.getSensorId());
            collectorService.saveStream(sm);
        }

        return take;

    }

}

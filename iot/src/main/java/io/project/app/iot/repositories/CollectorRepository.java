package io.project.app.iot.repositories;

import io.project.app.iot.model.SensorMetrics;
import io.project.app.models.SensorMetricsDTO;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;

@Repository
@Qualifier("Influx")
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class CollectorRepository {
    
    private InfluxDB influxDB;
    
    @Value("${db.username}")
    private String username;
    
    @Value("${db.password}")
    private String password;
    
    @Value("${db.port}")
    private int port;
    
    @Value("${db.name}")
    private String dbName;
    
    @Value("${db.url}")
    private String databaseUrl;
    
    @Value("${db.store_metrics}")
    private String storeMetrics;
    
    @Value("${db.metricsEnabled}")
    private Boolean metricsEnabled;
    
    private static final String RETENTION_POLICY = "defaultPolicy";
    
    @PostConstruct
    private void init() {
        if (!metricsEnabled) {
            return;
        }
        
        final String url = "http://" + databaseUrl + ":" + port;
        try {
            log.info("Attempting connect to influxDB at {}", url);
            this.influxDB = InfluxDBFactory.connect(url, username, password);
            // create db if not exists
            if (!influxDB.databaseExists(dbName)) {
                influxDB.createDatabase(dbName);
                influxDB.createRetentionPolicy(RETENTION_POLICY, dbName, storeMetrics, 1, true);
                influxDB.setRetentionPolicy(RETENTION_POLICY);
                influxDB.setDatabase(dbName);
            }
        } catch (Exception e) {
            log.error("Reconnect to DB");
            this.influxDB = InfluxDBFactory.connect(url, username, password);
            influxDB.createDatabase(dbName);
            influxDB.createRetentionPolicy(RETENTION_POLICY, dbName, storeMetrics, 1, true);
            influxDB.setRetentionPolicy(RETENTION_POLICY);
            influxDB.setDatabase(dbName);
        }
    }
    
    public void saveStreamData(final SensorMetricsDTO deviceMetrics) {
        if (!metricsEnabled) {
            return;
        }
        
        deviceMetrics.setTimestamp(new Date(System.currentTimeMillis()).getTime());
        BatchPoints batchPoints = getBatchPoints();
        Point point = Point.measurement("sensors")
                .tag("sensorId", deviceMetrics.getSensorId())
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("sensorId", deviceMetrics.getSensorId())
                .addField("timestamp", deviceMetrics.getTimestamp())
                .addField("ambientTemperature", deviceMetrics.getAmbientTemperature())
                .addField("humidity", deviceMetrics.getHumidity())
                .addField("photosensor", deviceMetrics.getPhotosensor())
                .addField("radiationLevel", deviceMetrics.getRadiationLevel())
                .build();
        
        batchPoints.point(point);
        
        this.write(batchPoints);
    }
    
    private BatchPoints getBatchPoints() {
        return BatchPoints
                .database(dbName)
                .retentionPolicy(RETENTION_POLICY)
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();
    }
    
    private void write(final BatchPoints batchPoints) {
        influxDB.write(batchPoints);
    }
    
    public List<SensorMetrics> getLastLoadMetrics(final String sensorId, int count) {
        String query = "SELECT * FROM sensors where sensorId='%s' ORDER BY time DESC LIMIT " + count;
        query = String.format(query, sensorId);
        QueryResult queryResult = influxDB.query(new Query(query, dbName));
        InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
        List<SensorMetrics> workloadPoints = resultMapper.toPOJO(queryResult, SensorMetrics.class);
        Collections.reverse(workloadPoints);
        return workloadPoints;
    }
    
    public List<SensorMetrics> getAllMetrics() {
        String query = "SELECT * FROM sensors WHERE timestamp > 0 ORDER BY time DESC LIMIT 10";
        query = String.format(query);
        
        QueryResult queryResult = influxDB.query(new Query(query, dbName));
        InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
        List<SensorMetrics> workloadPoints = resultMapper.toPOJO(queryResult, SensorMetrics.class);
        Collections.reverse(workloadPoints);
        return workloadPoints;
    }
    
    public List<SensorMetrics> getMaxRadiationForSensor(final String sensorId) {
        String query = "SELECT MAX(radiationLevel) FROM sensors where sensorId='%s' GROUP BY time(3600m) ";
        query = String.format(query, sensorId);
        QueryResult queryResult = influxDB.query(new Query(query, dbName));
        log.info("queryResult " + queryResult.toString());
        InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
        List<SensorMetrics> workloadPoints = resultMapper.toPOJO(queryResult, SensorMetrics.class);
        Collections.reverse(workloadPoints);
        return workloadPoints;
    }

    //https://github.com/influxdata/influxdb-java
    // https://github.com/influxdata/influxdb-java/blob/master/src/test/java/org/influxdb/querybuilder/BuiltQueryTest.java
    //https://influxdbcom.readthedocs.io/en/latest/content/docs/v0.6/api/aggregate_functions/
    //https://github.com/influxdata/influxdb-java/blob/master/src/test/java/org/influxdb/InfluxDBTest.java
}

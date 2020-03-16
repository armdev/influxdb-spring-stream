package io.project.app.iot.repositories;

import io.project.app.iot.model.SensorMetrics;
import io.project.app.iot.services.DBConnect;
import io.project.app.models.SensorMetricsDTO;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;
import org.springframework.beans.factory.annotation.Autowired;

@Repository
@Qualifier("influx")
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class CollectorRepository {

    @Autowired
    private DBConnect dBConnect;

    public void saveStreamData(final SensorMetricsDTO deviceMetrics) {

        deviceMetrics.setTimestamp(new Date(System.currentTimeMillis()).getTime());
        BatchPoints batchPoints = getBatchPoints();
        Point point = Point.measurement("sensors")
                .tag("atag", deviceMetrics.getSensorId())
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
                .database(dBConnect.getDbName())
                .retentionPolicy(dBConnect.getRETENTION_POLICY())
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();
    }

    private void write(final BatchPoints batchPoints) {
        dBConnect.getInfluxDB().write(batchPoints);
    }

    public List<SensorMetrics> getLastLoadMetrics(final String sensorId, int count) {
        String query = "SELECT * FROM sensors where sensorId='%s' ORDER BY time DESC LIMIT " + count;
        query = String.format(query, sensorId);
        QueryResult queryResult = dBConnect.getInfluxDB().query(new Query(query, dBConnect.getDbName()));
        log.info(queryResult.getResults().toString());
        InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
        List<SensorMetrics> workloadPoints = resultMapper.toPOJO(queryResult, SensorMetrics.class);
        Collections.reverse(workloadPoints);
        return workloadPoints;
    }

    public List<SensorMetrics> getAllMetrics() {
        String query = "SELECT * FROM sensors WHERE timestamp > 0 ORDER BY time DESC LIMIT 10";
        query = String.format(query);

        QueryResult queryResult = dBConnect.getInfluxDB().query(new Query(query, dBConnect.getDbName()));
        InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
        List<SensorMetrics> workloadPoints = resultMapper.toPOJO(queryResult, SensorMetrics.class);
        Collections.reverse(workloadPoints);
        return workloadPoints;
    }

    public List<SensorMetrics> getMaxRadiationForSensor(final String sensorId) {
        String query = "SELECT MAX(radiationLevel) FROM sensors where sensorId='%s' GROUP BY time(3600m) ";
        query = String.format(query, sensorId);
        QueryResult queryResult = dBConnect.getInfluxDB().query(new Query(query, dBConnect.getDbName()));
        
        log.info("QueryResult " + queryResult.toString());
        
        QueryResult.Series get = queryResult.getResults().get(0).getSeries().get(0);
        
        
        
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

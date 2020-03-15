package io.project.app.iot.services;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.BatchOptions;
import org.influxdb.dto.Query;
import org.springframework.stereotype.Service;

@Qualifier("DBConnect")
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Data
@Service
public class DBConnect {

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

    private String RETENTION_POLICY = "defaultPolicy";

    @PostConstruct
    private void init() {
        if (!metricsEnabled) {
            return;
        }
        final String url = "http://" + databaseUrl + ":" + port;
        this.influxDB = InfluxDBFactory.connect(url, username, password);

        try {
            log.info("Attempting connect to influxDB at {}", url);

            this.influxDB = InfluxDBFactory.connect(url, username, password);

            influxDB.query(new Query("CREATE DATABASE " + dbName));
            influxDB.setDatabase(dbName);

            influxDB.query(new Query("CREATE RETENTION POLICY " + RETENTION_POLICY + " ON " + dbName + " DURATION 30h REPLICATION 2 SHARD DURATION 30m DEFAULT"));
            influxDB.setRetentionPolicy(RETENTION_POLICY);

            influxDB.enableBatch(BatchOptions.DEFAULTS);
        } catch (Exception e) {
            log.error("Reconnect to DB");
            this.influxDB = InfluxDBFactory.connect(url, username, password);

            influxDB.query(new Query("CREATE DATABASE " + dbName));
            influxDB.setDatabase(dbName);

            influxDB.query(new Query("CREATE RETENTION POLICY " + RETENTION_POLICY + " ON " + dbName + " DURATION 30h REPLICATION 2 SHARD DURATION 30m DEFAULT"));
            influxDB.setRetentionPolicy(RETENTION_POLICY);

            influxDB.enableBatch(BatchOptions.DEFAULTS);
        }
    }

    //https://github.com/influxdata/influxdb-java
    // https://github.com/influxdata/influxdb-java/blob/master/src/test/java/org/influxdb/querybuilder/BuiltQueryTest.java
    //https://influxdbcom.readthedocs.io/en/latest/content/docs/v0.6/api/aggregate_functions/
    //https://github.com/influxdata/influxdb-java/blob/master/src/test/java/org/influxdb/InfluxDBTest.java
}

package com.ivansoft.alexa.skills.sismos.lamda1.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "earthquakes_by_timestamp")
public class AmazonDynamoEarthquakeModel {
    @DynamoDBHashKey
    private int id;

    @DynamoDBAttribute
    private String earthquakes;

    @DynamoDBAttribute
    private String timestamp;

    public AmazonDynamoEarthquakeModel() {

    }

    public AmazonDynamoEarthquakeModel(int id, String earthquakes, String timestamp) {
        this.id = id;
        this.earthquakes = earthquakes;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public String getEarthquakes() {
        return earthquakes;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEarthquakes(String earthquakes) {
        this.earthquakes = earthquakes;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "AmazonDynamoDBEarthquakeModel{" +
                "id=" + id +
                ", earthquakes='" + earthquakes + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}

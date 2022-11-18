package com.ivansoft.alexa.skills.sismos.lamda1.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.ivansoft.alexa.skills.sismos.lamda1.model.AmazonDynamoEarthquakeModel;

public class AmazonDynamoService {
    /**
     * Get entity of model of earthquake
     * @return get query result
     */
    public AmazonDynamoEarthquakeModel getData() {
        DynamoDBMapper dynamoDBMapper = getDynamoDBMapper();

        // Query
        AmazonDynamoEarthquakeModel partitionKey = new AmazonDynamoEarthquakeModel();
        partitionKey.setId(1);
        DynamoDBQueryExpression<AmazonDynamoEarthquakeModel> queryExpression = new DynamoDBQueryExpression<AmazonDynamoEarthquakeModel>()
                .withHashKeyValues(partitionKey);

        // Run query
        PaginatedQueryList<AmazonDynamoEarthquakeModel> result = dynamoDBMapper.query(AmazonDynamoEarthquakeModel.class, queryExpression);
        return result.get(0);
    }

    /**
     * Save entity model of earthquake
     * @param data string of speech for alexa
     * @param timestamp unix epoch
     */
    public void saveData(String data, String timestamp) {
        DynamoDBMapper dynamoDBMapper = getDynamoDBMapper();

        AmazonDynamoEarthquakeModel earthquakeModel = new AmazonDynamoEarthquakeModel();
        earthquakeModel.setId(1);
        earthquakeModel.setEarthquakes(data);
        earthquakeModel.setTimestamp(timestamp);

        dynamoDBMapper.save(earthquakeModel);
    }

    /**
     * Get DynamoDB Mapper
     * @return DynamoDBMapper
     */
    private DynamoDBMapper getDynamoDBMapper() {
        AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.defaultClient();
        return new DynamoDBMapper(amazonDynamoDB);
    }
}

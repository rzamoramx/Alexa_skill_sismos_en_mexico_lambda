package com.ivansoft.alexa.skills.sismos.lamda1;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.ivansoft.alexa.skills.sismos.lamda1.component.DataEarhQuakeComponent;

public class Lambda1Handler implements RequestHandler<Object, String> {
    @Override
    public String handleRequest(Object input, Context context) {
        DataEarhQuakeComponent dataEarhQuakeComponent = new DataEarhQuakeComponent();
        return dataEarhQuakeComponent.getEarthquakes();
    }
}

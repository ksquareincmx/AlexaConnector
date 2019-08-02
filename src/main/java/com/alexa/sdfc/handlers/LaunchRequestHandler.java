package com.alexa.sdfc.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.LaunchRequest;
import com.amazon.ask.model.Response;

import java.io.Serializable;
import java.util.Optional;

import static com.amazon.ask.request.Predicates.requestType;

public class LaunchRequestHandler implements RequestHandler {

   
    public boolean canHandle(HandlerInput input) {
        return input.matches(requestType(LaunchRequest.class));
    }

   
    public Optional<Response> handle(HandlerInput input) {
        String speechText = "Welcome to the Project office Skill! Hope you are having a wonderfull day, Seems like you got 5 important emails in your inbox. How can i help you?";
        String repromptText = "You can create new Saleforce account by saying, Create new account for Mike";
        return input.getResponseBuilder()
                .withSpeech(speechText)
                .withSimpleCard("HelloWorld", speechText)
                .withReprompt(repromptText)
                .build();
    }

}

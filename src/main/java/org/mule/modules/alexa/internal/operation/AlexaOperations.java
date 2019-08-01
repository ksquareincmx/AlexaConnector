package org.mule.modules.alexa.internal.operation;

import static org.mule.runtime.api.meta.ExpressionSupport.SUPPORTED;
import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.SerializationUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.mule.modules.alexa.api.domain.AlexaRequestURL;
import org.mule.modules.alexa.api.domain.intents.IntentValueParam;
import org.mule.modules.alexa.internal.connection.AlexaConnection;
import org.mule.modules.alexa.internal.util.AlexaRequestBuilder;
import org.mule.modules.alexa.internal.util.AlexaRequestUtility;
import org.mule.runtime.api.meta.ExpressionSupport;

import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.metadata.MetadataKeyId;
import org.mule.runtime.extension.api.annotation.metadata.OutputResolver;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.NullSafe;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.display.ClassValue;
import org.springframework.util.ClassUtils;

import com.amazon.ask.Skill;
import com.amazon.ask.Skills;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.dispatcher.request.handler.impl.LaunchRequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.request.handler.GenericRequestHandler;

/**
 * This class is a container for operations, every public method in this class
 * will be taken as an extension operation.
 */
public class AlexaOperations {

	private AlexaRequestUtility alexaRequestUtility = new AlexaRequestUtility();
	private AlexaRequestBuilder alexaRequestBuilder = new AlexaRequestBuilder();

	@MediaType(value = ANY, strict = false)
	public String createSkill(@Connection AlexaConnection alexaConnection, @Expression(SUPPORTED) String vendorId,
			@Expression(SUPPORTED) String summary, @Expression(SUPPORTED) List<String> examplePhrases,
			@Expression(SUPPORTED) List<String> keywords, @Expression(SUPPORTED) String skillName,
			@Expression(SUPPORTED) String description, @Expression(SUPPORTED) String endpoint,
			@Optional @NullSafe @Expression(ExpressionSupport.NOT_SUPPORTED) @ParameterDsl(allowReferences = false) List<IntentValueParam> intents) {

		System.out.println("Alexa Authorization  Token ======> " + alexaConnection.getAccessToken());

		String alexaRequest = alexaRequestBuilder.createAlexaSkillRequestBuilder(vendorId, summary, examplePhrases, keywords, skillName, description, endpoint);
		String createSkillResponse = alexaRequestUtility.doPost(AlexaRequestURL.CREATE_ALEXA_SKILL, alexaConnection.getAccessToken(), alexaRequest);
		System.out.println("Create Alexa Skill Response ===> " + createSkillResponse);
		
		try {
			Object obj = new JSONParser().parse(createSkillResponse);
			JSONObject jo = (JSONObject) obj;
			String newskillId = (String) jo.get("skillId");
			
			System.out.println("Skill ID after parsing" + newskillId);
			
			String updateSkillRequest = alexaRequestBuilder.updateCreatedSkill(newskillId, intents);
			return alexaRequestUtility.doPut(String.format(AlexaRequestURL.UPDATE_ALEXA_SKILL, newskillId)  , alexaConnection.getAccessToken(), updateSkillRequest);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@MediaType(value = ANY, strict = false)
	public String getSkillInfo(@Connection AlexaConnection alexaConnection, @Expression(SUPPORTED) String skillId) {
		System.out.println("Alexa Authorization  Token ======> " + alexaConnection.getAccessToken());
		return alexaRequestUtility.doGet(AlexaRequestURL.GET_ALEXA_INFO, alexaConnection.getAccessToken(), skillId);
	}
	
	@MediaType(value = ANY, strict = false)
	public String UseExistingSkill(@Connection AlexaConnection alexaConnection, 
			@Expression(SUPPORTED) String skillId,
			@Expression(SUPPORTED) String stage, 
			@Expression(SUPPORTED) String requestType,
			@Expression(SUPPORTED) String intentName, 
			@Expression(SUPPORTED) String inputString,
			@Optional @NullSafe @Expression(ExpressionSupport.NOT_SUPPORTED) @ParameterDsl(allowReferences = false) Map<String, String> testSlots)
			throws MalformedURLException, ProtocolException {
		
		System.out.println("Alexa Authorization  Token ======> " + alexaConnection.getAccessToken());
		
		String testAlexaSkillJsonRequest = alexaRequestBuilder.getAlexaRequestJson(skillId, requestType, testSlots, intentName);
		String response = alexaRequestUtility.doPost(String.format(AlexaRequestURL.TEST_ALEXA_SKILL, skillId, stage), alexaConnection.getAccessToken(), testAlexaSkillJsonRequest);
		
		Object obj = null;
		try {
			obj = new JSONParser().parse(response);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JSONObject jo = (JSONObject) obj;
		JSONObject name = (JSONObject) jo.get("result");
		JSONObject skillExecutionInfo = (JSONObject) name.get("skillExecutionInfo");
		JSONObject invocationResponse = (JSONObject) skillExecutionInfo.get("invocationResponse");
		JSONObject body1 = (JSONObject) invocationResponse.get("body");
		JSONObject responseone = (JSONObject) body1.get("response");
		JSONObject card = (JSONObject) responseone.get("card");
		return (String) card.get("content");
	}
	
	
	@MediaType(value = ANY, strict = false)
	public String DeleteSkill(@Connection AlexaConnection alexaConnection, @Expression(SUPPORTED) String skillId) {
		System.out.println("Alexa Authorization  Token ======> " + alexaConnection.getAccessToken());
		return alexaRequestUtility.doGet(AlexaRequestURL.GET_ALEXA_INFO, alexaConnection.getAccessToken(), skillId);
	}
	
	@MediaType(value = ANY, strict = false)
	public String customSkill( @Expression(ExpressionSupport.NOT_SUPPORTED) @ParameterDsl(allowReferences = false) List<String> handlers){
		
		List<GenericRequestHandler> handlerObjects = new ArrayList<GenericRequestHandler>();
		
		try {
          List<RequestHandler> handlerObjects1 = new ArrayList<RequestHandler>();
			
			String className = "com.alexa.sdfc.handlers.LaunchRequestHandler"; 
			
			
			// Get Class instance
			Class<?> child = Class.forName(className);
			ClassLoader classLoader = child.getClassLoader();
			System.out.println("The system classLoader = "+ ClassLoader.getSystemClassLoader() +
					"\n The classLoader loading our classes is = "+ classLoader);
			
			Class<?> parent = classLoader.loadClass("com.amazon.ask.dispatcher.request.handler.RequestHandler");
			System.out.println("Child -----> " + child.getClassLoader());
			System.out.println("Parent -----> " + parent.getClassLoader());
			
			//Class<?> requestHandler = (Class<?>) child.newInstance(); 
			RequestHandler requestHandler = (RequestHandler) parent.cast(child.newInstance());
			System.out.println("Object ---> "+ requestHandler);

			
			//RequestHandler requestHandler = (RequestHandler) parent.cast(child.newInstance());
			//System.out.println("Object ---> "+ requestHandler);

			

			//Skill skill = Skills.standard().addRequestHandler((GenericRequestHandler<HandlerInput, java.util.Optional<Response>>) handlerObjects1).build();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		return "";
	}
}

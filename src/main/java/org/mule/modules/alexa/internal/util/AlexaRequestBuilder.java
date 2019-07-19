package org.mule.modules.alexa.internal.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.mule.modules.alexa.api.domain.intents.IntentValueParam;
import org.mule.modules.alexa.api.domain.intents.PromptParams;
import org.mule.modules.alexa.api.domain.intents.SlotParams;
import org.mule.modules.alexa.api.domain.test.Application;
import org.mule.modules.alexa.api.domain.test.Body;
import org.mule.modules.alexa.api.domain.test.Context;
import org.mule.modules.alexa.api.domain.test.Intent;
import org.mule.modules.alexa.api.domain.test.Reqest;
import org.mule.modules.alexa.api.domain.test.Request;
import org.mule.modules.alexa.api.domain.test.Session;
import org.mule.modules.alexa.api.domain.test.SkillRequest;
import org.mule.modules.alexa.api.domain.test.SlotName;
import org.mule.modules.alexa.api.domain.test.Sstem;
import org.mule.modules.alexa.api.domain.test.User;


import com.google.gson.Gson;

public class AlexaRequestBuilder {

	/**
	 * TODO
	 */
	public String createAlexaSkillRequestBuilder(String vendorId, String summary, List<String> examplePhrases,
			List<String> keywords, String skillName, String description, String endpoint) {

		String createSkillJsonTemplet = "{\"vendorId\":\"%s\",\"manifest\":{\"publishingInformation\":{\"locales\": {\"en-US\": {\"summary\": \"%s\",\"examplePhrases\": [%s],\"keywords\": [%s],\"name\": \"%s\",\"description\": \"%s\"}},\"isAvailableWorldwide\": false,\"testingInstructions\": \"1) Say 'Alexa, discover my devices' 2) Say 'Alexa, turn on sample lights'\",\"category\": \"SMART_HOME\",\"distributionCountries\": [\"US\",\"GB\"]},\"apis\": {\"custom\": {\"endpoint\": {\"uri\": \"%s\"}}},\"manifestVersion\": \"1.0\",\"privacyAndCompliance\": {\"allowsPurchases\": false,\"locales\": {\"en-US\": {\"termsOfUseUrl\": \"http://www.termsofuse.sampleskill.com\",\"privacyPolicyUrl\": \"http://www.myprivacypolicy.sampleskill.com\"}},\"isExportCompliant\": true,\"isChildDirected\": false,\"usesPersonalInfo\": false}}}";

		String examplePhrasesString = "";
		for (String example : examplePhrases) {
			examplePhrasesString = String.format("%s,\"%s\"", examplePhrasesString, example);

		}
		examplePhrasesString = examplePhrasesString.substring(1, examplePhrasesString.length());

		String keywordsString = "";
		for (String keyword : keywords) {
			keywordsString = String.format("%s,\"%s\"", keywordsString, keyword);

		}

		keywordsString = keywordsString.substring(1, keywordsString.length());

		String createSkillJson = String.format(createSkillJsonTemplet, vendorId, summary, examplePhrasesString,
				keywordsString, skillName, description, endpoint);
		System.out.println("Create Skill JSON ----> " + createSkillJson);

		return createSkillJson;
	}

	/**
	 * TODO
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String updateCreatedSkill(String newskillId, List<IntentValueParam> intents) {

		String request = null;
		
		try {

			if (intents != null) {
				JSONArray intentArray = new JSONArray();

				
				for (IntentValueParam valueParam : intents) {
					String intentName = valueParam.getIntentName();

					JSONObject jsonObject = new JSONObject();
					JSONObject jsonObject2 = new JSONObject();
					JSONObject jsonObject3 = new JSONObject();
					JSONObject jsonObject4 = new JSONObject();
					JSONObject subdialogjsonObject = new JSONObject();
					JSONObject promtsobject1 = new JSONObject();
					JSONArray promtarray1 = new JSONArray();
					JSONObject cancelIntentjsonObj = new JSONObject();

					jsonObject.put("samples", valueParam.getSamples());

					// creating slots array object
					List<SlotParams> slots = valueParam.getSlots();

					JSONArray jsonArray = new JSONArray();

					int i = 0;
					for (SlotParams slot : slots) {
						JSONObject slotJsonObject = new JSONObject();

						String slotName = slot.getSlotName();
						String slotType = slot.getSlotType();

						slotJsonObject.put("name", slotName);
						slotJsonObject.put("type", slotType);
						slotJsonObject.putIfAbsent("samples", slot.getSamples());

						jsonArray.put(i, slotJsonObject);
						i = i + 1;
					}

					// creating promt array object
					List<PromptParams> promt = valueParam.getPromts();
					JSONArray promtjsonArray = new JSONArray();
					int a = 0;
					for (PromptParams Promt : promt) {
						JSONObject promtJsonObject = new JSONObject();

						String promtType = Promt.getPromtType();
						String promtValue = Promt.getPromtValue();

						promtJsonObject.put("type", promtType);
						promtJsonObject.put("value", promtValue);

						promtjsonArray.put(a, promtJsonObject);
						a = a + 1;
					}

					// creating dialog array object

					JSONArray dialogJsonArray = new JSONArray();
					JSONObject dialogjsonObject = new JSONObject();

					dialogjsonObject.put("name", intentName);
					dialogjsonObject.put("confirmationRequired", new Boolean(false));
					dialogjsonObject.put("prompts", new JSONObject());

					dialogJsonArray.put(dialogjsonObject);

					subdialogjsonObject.put("intents", dialogJsonArray);
					// subdialogjsonObject1.put("dialog", subdialogjsonObject);

					promtsobject1.put("variations", promtjsonArray);
					promtsobject1.put("id", "123456");

					promtarray1.put(promtsobject1);
					// promtsobject2.put("prompts", promtarray1);

					jsonObject.put("slots", jsonArray);
					jsonObject.put("name", intentName);
					cancelIntentjsonObj.put("name", "AMAZON.CancelIntent");
					cancelIntentjsonObj.put("samples", new JSONArray());
					intentArray.put(cancelIntentjsonObj);
					intentArray.put(jsonObject);

					jsonObject2.put("invocationName", "my space facts");
					jsonObject2.putIfAbsent("intents", intentArray);
					jsonObject3.put("languageModel", jsonObject2);

					jsonObject4.put("interactionModel", jsonObject3);
					jsonObject3.put("dialog", subdialogjsonObject);
					jsonObject3.put("prompts", promtarray1);

					request = jsonObject4.toJSONString();

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return request;
	}
	
	public String getAlexaRequestJson(String applicationID, String requestType, Map<String, String> slots, String intentName) {
		Application application = new Application(applicationID);
		User user = new User("amzn1.ask.account.12345ABCDEFGH");
		
		Session session = new Session();
		session.setOne(true);
		session.setSessionId("aaf7b112-434c-11e7-2563-6bbd1672c748");
		session.setApplication(application);
		session.setAttributes(new HashMap());
		session.setUser(user);
		Sstem system = new Sstem(application, user);
		Context context = new Context(system);
		Intent intent = new Intent();

		Map<String, SlotName> slotNames = new HashMap<>();

		for (Map.Entry<String, String> slot : slots.entrySet()) {
			SlotName slotname = new SlotName();
			slotname.setConfirmationStatus("NONE");
			slotname.setName(slot.getKey());
			slotname.setValue(slot.getValue());

			slotNames.put(slot.getKey(), slotname);
		}

		intent.setName(intentName);
		intent.setSlots(slotNames);

		TimeZone tz = TimeZone.getTimeZone("CST");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
		df.setTimeZone(tz);
		String nowAsISO = df.format(new Date());
		Reqest reqest = new Reqest(requestType, "c03faf54-684d-11e7-6249-6bbd1825c634", nowAsISO, "en-US", intent);
		Body body = new Body("1.0", session, context, reqest);
		SkillRequest skillRequest = new SkillRequest(body);
		Request request = new Request("Default", skillRequest);

		Gson gson = new Gson();
		String jsonInputString = gson.toJson(request).replace("\"one\"", "\"new\"");

		System.out.println("jsonstring---->" + jsonInputString);
		return jsonInputString;
	}
}

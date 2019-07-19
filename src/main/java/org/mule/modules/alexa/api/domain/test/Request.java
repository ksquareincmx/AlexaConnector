package org.mule.modules.alexa.api.domain.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;


public class Request {

	
	private String endpointRegion;
	private SkillRequest skillRequest;
	
	

	public Request(String endpointRegion, SkillRequest skillRequest) {
		super();
		this.endpointRegion = endpointRegion;
		this.skillRequest = skillRequest;
	}

	public String getEndpointRegion() {
		return endpointRegion;
	}

	public void setEndpointRegion(String endpointRegion) {
		this.endpointRegion = endpointRegion;
	}

	public SkillRequest getSkillrequest() {
		return skillRequest;
	}

	

	

	public static void main(String[] args) {
		
		
		//Map<String, Boolean> prop= new HashedMap();
		//prop.put("new", true);
		Application application= new Application("xxx-4566-xxx");
		User user =new User("xxx---999---xxx");
		//Session session = new Session(true , "", application , new HashedMap(), user);
		Session session = new Session();
		session.setOne(true);
		session.setSessionId("xxxx-123-xx");
		session.setApplication(application);
		session.setAttributes(new HashMap());
		session.setUser(user);
		Sstem system =new Sstem(application, user);
		Context context= new Context(system);
		Intent intent =new Intent();
			
		List<String> dummySlots = new ArrayList<String>();
		dummySlots.add("Activity");
		dummySlots.add("User");
		
		Map<String, SlotName> slotNames = new HashMap<>();
		
		Iterator<String> it = dummySlots.iterator();
		while (it.hasNext()) {
			String name = (String) it.next();
			
			SlotName slotname= new SlotName();
			slotname.setConfirmationStatus("NONE");
			slotname.setName(name);
			slotname.setValue(name);
			
			slotNames.put(name, slotname);
		}
	
		
		
		intent.setName("custom web");
		intent.setSlots(slotNames);
		
		Reqest reqest= new Reqest("LaunchRequest", "xxx-567-xxx", "xxx-xyx--xx", "en-US", intent);
		Body body =new Body("1.0",session, context,reqest);
		SkillRequest skillRequest = new SkillRequest(body);
				Request request= new Request("NA", skillRequest );
				
				Gson gson = new Gson();
				String one= gson.toJson(request).replace("\"one\"" ,"\"new\"")  ;
				
				System.out.println(one);
		//ObjectMapper objectMapper = new ObjectMapper();
		
	}

	
	
}

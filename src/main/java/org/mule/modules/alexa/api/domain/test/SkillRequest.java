package org.mule.modules.alexa.api.domain.test;

public class SkillRequest {

	private Body body;

	public SkillRequest(Body body) {
		super();
		this.body = body;
	}

	@Override
	public String toString() {
		return "SkillRequest [body=" + body + "]";
	}

	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}
	
}

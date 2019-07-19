package org.mule.modules.alexa.internal.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AlexaRequestUtility {

	public String doGet(String urlString, String basicAuth, String skillId) {
		String response = null;
		try {
			URL url = new URL(String.format(urlString, skillId));
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestProperty("Authorization", basicAuth);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/json");

			int status = connection.getResponseCode();

			System.out.println("GET Response Status ===> " + status);

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer content = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}
			in.close();

			response = content.toString();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	public String doPost(String urlString, String basicAuth, String postRequestJson) {
		String response = null;
		try {
			URL url = new URL(urlString);
			System.out.println(" post url--->" + url.toString());

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestProperty("Authorization", basicAuth);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");

			// Send post request
			connection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(postRequestJson);
			wr.flush();
			wr.close();

			int status = connection.getResponseCode();

			System.out.println("POST Response Status ===> " + status);

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer content = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}
			in.close();

			response = content.toString();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}
	
	public String doPut(String urlString, String basicAuth, String postRequestJson) {
		String response = null;
		try {
			URL url = new URL(urlString);
			System.out.println("put url--->" + url.toString());

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestProperty("Authorization", basicAuth);
			connection.setRequestMethod("PUT");
			connection.setRequestProperty("Content-Type", "application/json");

			// Send put request
			connection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(postRequestJson);
			wr.flush();
			wr.close();

			int status = connection.getResponseCode();

			System.out.println("PUT Response Status ===> " + status);

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer content = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}
			in.close();

			response = content.toString();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}
	
}

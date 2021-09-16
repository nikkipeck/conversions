package com.romans.server;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Version;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * RomanNumeralServerClient
 * injects RomanNumeralServer
 * sets up an HttpClient 
 * serves as an integration testing option
 * 
 * @author nikki
 *
 */

public class RomanNumeralServerClient {
	
	static Injector injector = Guice.createInjector(new ServerModule());
    static RomanNumeralServer server = injector.getInstance(RomanNumeralServer.class);
	
	public static void main(String[] args) {
		try {
			server.startServer();
			//String urlStr = "http://localhost:8080/romannumeral?min={4}&max={7}";
			//String urlStr = "http://localhost:8080/romannumeral?query={23}"; //XXIII
			String urlStr = "http://localhost:8080/romannumeral";
			URL url= new URL(urlStr);
			URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
			    		
			HttpClient client = HttpClient.newHttpClient();
	   		HttpRequest request = HttpRequest.newBuilder()
	   				.version(Version.HTTP_1_1)
	                .uri(URI.create(uri.toASCIIString()))
	                .build();
	
	        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
	        System.out.println("" + response.statusCode());
	        System.out.println(response.body());
	        
	        server.stopServer();
		} catch(Exception e) {
			e.printStackTrace();
		}
    }
}
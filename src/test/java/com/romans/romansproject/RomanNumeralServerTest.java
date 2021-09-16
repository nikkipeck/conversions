package com.romans.romansproject;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.net.http.HttpClient.Version;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.romans.server.RomanNumeralServer;
import com.romans.server.ServerModule;

public class RomanNumeralServerTest {
	
	static Injector injector = Guice.createInjector(new ServerModule());
    static RomanNumeralServer server = injector.getInstance(RomanNumeralServer.class);
	
    @Before
    public void setup(){
    	server.startServer();    	
    }
    
    @After
    public void teardown() {
    	server.stopServer();
    }
    
    @Test //server can serve
    public void test400() {
    	try {
    		URL url= new URL("http://localhost:8080/romannumeral");
		    assertEquals(400, getServerResponse(url).statusCode());
    	}
		catch(Exception e) {
			 e.printStackTrace();
		}
    }
    
    @Test //query 1-3999 returns proper value
    public void testSuccessfulQuery() {		
    	try {
    		URL url = new URL("http://localhost:8080/romannumeral?query={23}");
	    	HttpResponse response = getServerResponse(url);			
		    assertEquals(200, response.statusCode());
		    assertEquals("{\"input\":\"23\",\"output\":\"XXIII\"}", response.body());
    	}
		catch(Exception e) {
			 e.printStackTrace();
		}    	
    }
    
    @Test //query out of range fails
    public void testOutofRangeQuery() {		
    	try {
    		URL url = new URL("http://localhost:8080/romannumeral?query={80645489808}");
	    	HttpResponse response = getServerResponse(url);			
		    assertEquals(400, response.statusCode());
		    assertEquals("query value must be of type int values 1-3,999. Query string format example: query={11}", response.body());
    	}
		catch(Exception e) {
			 e.printStackTrace();
		}    	
    }
    
    
    @Test //query banana fails
    public void testBadQuery() {
    	try {
    		URL url = new URL("http://localhost:8080/romannumeral?query={banana}");
	    	HttpResponse response = getServerResponse(url);			
		    assertEquals(400, response.statusCode());
		    assertEquals("query value must be of type int values 1-3,999. Query string format example: query={11}", response.body());
    	}
		catch(Exception e) {
			 e.printStackTrace();
		}    	
    }
    
    @Test //min/max 1-3,999 returns proper value
    public void testSuccessfulRange() {		
    	try {
    		URL url = new URL("http://localhost:8080/romannumeral?min={4}&max={7}");
	    	HttpResponse response = getServerResponse(url);			
		    assertEquals(200, response.statusCode());
		    assertEquals("{\"conversions\":[{\"input\":\"4\",\"output\":\"IV\"},{\"input\":\"5\",\"output\":\"V\"},{\"input\":\"6\",\"output\":\"VI\"},{\"input\":\"7\",\"output\":\"VII\"}]}", response.body());
    	}
		catch(Exception e) {
			 e.printStackTrace();
		}    	
    }
    
    @Test //min/max out of range fails
    public void testOutofRangeRange() {		
    	try {
    		URL url = new URL("http://localhost:8080/romannumeral?min={4}&max={4000}");
	    	HttpResponse response = getServerResponse(url);			
		    assertEquals(400, response.statusCode());
		    assertEquals("min and max must be values between 1 and 3,999: 4000 not allowed", response.body());
    	}
		catch(Exception e) {
			 e.printStackTrace();
		}    	
    }
    
    @Test //min/max bad data fails
    public void testBadRange() {		
    	try {
    		URL url = new URL("http://localhost:8080/romannumeral?min={sugar}&max={4000}");
	    	HttpResponse response = getServerResponse(url);			
		    assertEquals(400, response.statusCode());
		    assertEquals("min and max values must be of type int. Query string format example: min={1}&max={3}", response.body());
    	}
		catch(Exception e) {
			 e.printStackTrace();
		}    	
    }
    
    /*getServerResponse(Url url)
     * helper method to return an HttpResponse from the server running on 8080*/
    private HttpResponse getServerResponse(URL url) {
    	HttpResponse<String> response = null;
    	try {
    		URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
		
			HttpClient client = HttpClient.newHttpClient();
				HttpRequest request = HttpRequest.newBuilder()
						.version(Version.HTTP_1_1)
		            .uri(URI.create(uri.toASCIIString()))
		            .build();
		
		    response = client.send(request, HttpResponse.BodyHandlers.ofString());
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return response;
    }

}

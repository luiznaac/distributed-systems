package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Map;

public class server {
    
    public static boolean resourceOneBeingUsed;
    public static LinkedList<Integer> resourceOneList;
    
    public static boolean resourceTwoBeingUsed;
    public static LinkedList<Integer> resourceTwoList;

    public static void main(String[] args) throws Exception {
        
        resourceOneBeingUsed = false;
        resourceTwoBeingUsed = false;
        resourceOneList = new LinkedList<Integer>();
        resourceTwoList = new LinkedList<Integer>();

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/check-resource", new AccessResourceIntent());
        server.createContext("/free-resource", new FreeResource());
        server.setExecutor(null); // creates a default executor
        server.start();
    }
    
    static class AccessResourceIntent implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if (t.getRequestMethod().equals("POST")){
                InputStream requestBody = t.getRequestBody();
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Integer> jsonMap = mapper.readValue(requestBody, Map.class);

                String response = "{\"available\": ";
                Integer desiredResource = jsonMap.get("resource");
                Integer clientPort = jsonMap.get("port");

                if (desiredResource == 1) {
                    response += String.valueOf(!resourceOneBeingUsed) + "}";
                    if (resourceOneBeingUsed) {
                        resourceOneList.add(clientPort);
                    }
                    resourceOneBeingUsed = true;
                } else {
                    response += String.valueOf(!resourceTwoBeingUsed) + "}";
                    if (resourceTwoBeingUsed) {
                        resourceTwoList.add(clientPort);
                    }
                    resourceTwoBeingUsed = true;
                }

                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();

                System.out.println("Client on port " + clientPort + " requested resource " + desiredResource);
            }
        }
    }

    static class FreeResource implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if (t.getRequestMethod().equals("POST")){
                InputStream requestBody = t.getRequestBody();
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Integer> jsonMap = mapper.readValue(requestBody, Map.class);

                String response = "{\"response\": \"Ok\"}";

                if (jsonMap.get("resource") == 1) {
                    System.out.println("Freeing resource 1");
                    resourceOneBeingUsed = false;

                    Integer nextClientPort = resourceOneList.poll();
                    if(nextClientPort != null){
                        sendClientResponseWithAvailableResource("1", nextClientPort);
                        resourceOneBeingUsed = true;
                    }

                } else {
                    System.out.println("Freeing resource 2");
                    resourceTwoBeingUsed = false;

                    Integer nextClientPort = resourceTwoList.poll();
                    if(nextClientPort != null){
                        sendClientResponseWithAvailableResource("2", nextClientPort);
                        resourceTwoBeingUsed = true;
                    }
                }

                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }
    
    public static void sendClientResponseWithAvailableResource(String resourceName, int clientPort) throws IOException {
        String       postUrl       = "http://127.0.0.1:" + clientPort + "/resource-available";// put in your url
        Gson         gson          = new Gson();
        HttpClient   httpClient    = HttpClientBuilder.create().build();
        HttpPost     post          = new HttpPost(postUrl);
        
        JsonResponse resource = new JsonResponse();
        resource.setResource(resourceName);
        
        StringEntity postingString = new StringEntity(gson.toJson(resource));
        post.setEntity(postingString);
        post.setHeader("Content-type", "application/json");
        HttpResponse  response = httpClient.execute(post);
        System.out.println("Telling client on port "+ clientPort + " that resource " + resourceName + " is available");
    }
}

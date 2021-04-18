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
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;

public class server {
    
    public static boolean resourceOneBeingUsed;
    public static LinkedHashSet<Integer> resourceOneList;
    
    public static boolean resourceTwoBeingUsed;
    public static LinkedHashSet<Integer> resourceTwoList;

    public static void main(String[] args) throws Exception {
        
        resourceOneBeingUsed = false;
        resourceTwoBeingUsed = false;
        resourceOneList = new LinkedHashSet<Integer>();  
        resourceTwoList = new LinkedHashSet<Integer>();  

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

                if (jsonMap.get("resource") == 1) {
                    response += String.valueOf(!resourceOneBeingUsed) + "}";
                    if (resourceOneBeingUsed) {
                        resourceOneList.add(jsonMap.get("port"));
                    }
                    resourceOneBeingUsed = true;
                } else {
                    response += String.valueOf(!resourceTwoBeingUsed) + "}";
                    if (resourceTwoBeingUsed) {
                        resourceTwoList.add(jsonMap.get("port"));
                    }
                    resourceTwoBeingUsed = true;
                }

                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
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
                    resourceOneBeingUsed = false;

                    Iterator<Integer> itr = resourceOneList.iterator();
                    while(itr.hasNext()){
                        int port = itr.next();
                        sendClientResponseWithAvailableResource("1", port);
                        itr.remove();
                        break;
                    }

                } else {
                    resourceTwoBeingUsed = false;

                    Iterator<Integer> itr = resourceTwoList.iterator();
                    while(itr.hasNext()){
                        int port = itr.next();
                        sendClientResponseWithAvailableResource("2", port);
                        itr.remove();
                        break;
                    }
                }

                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }
    
    public static void sendClientResponseWithAvailableResource(String resourceName, int clientPort) throws MalformedURLException, IOException {
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
    }
}

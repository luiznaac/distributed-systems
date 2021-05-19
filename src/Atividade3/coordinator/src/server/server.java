package server;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.client.HttpClient;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

public class server {

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new Home());
        server.createContext("/book", new Book());
        server.setExecutor(null); 
        server.start();
    }
    
        static class Home implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if (t.getRequestMethod().equals("GET")){
                String page = Files.readString(Paths.get("./airMoreles.html")); 
                
                page = page.replace(":resposta", "");
                
                t.sendResponseHeaders(200, page.length());
                OutputStream os = t.getResponseBody();
                os.write(page.getBytes());
                os.close();

                System.out.println("Página carregada");
            }
        }
    }
    
    static class Book implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if (t.getRequestMethod().equals("POST")){
                InputStream requestBody = t.getRequestBody();
                
                String result = IOUtils.toString(requestBody, StandardCharsets.UTF_8);
                String[] splitPai = result.split("&");
                String userId = splitPai[0].split("=")[1];
                String room = splitPai[1].split("=")[1];
                String seat = splitPai[2].split("=")[1];
                System.out.println( userId + " " + room 
                        + " " + seat);
                
                String page = Files.readString(Paths.get("./airMoreles.html"));
                
                System.out.println("Fez parse do Json");
                String hotelTransaction = postToTransactionServer(room, "8081", 
                        "bookRoom", userId);
                String airLineTransaction = postToTransactionServer(seat, "8082", 
                        "bookSeat", userId);
                
                
                if (canCommit(hotelTransaction, "8081") &&
                        canCommit(airLineTransaction, "8082")) {
                    finishTransaction(hotelTransaction, "8081");
                    finishTransaction(airLineTransaction, "8082");
                    page = page.replace(":resposta", userId + ", seu quarto " + room 
                        + " e assento " + seat + " foram reservados com sucesso!");
                } else {
                    rollBack(hotelTransaction, "8081");
                    rollBack(airLineTransaction, "8082");
                    page = page.replace(":resposta", userId + ", seu quarto " + room 
                        + " e assento " + seat + " não puderam ser reservados.");
                }
                
                t.sendResponseHeaders(200, page.length());
                OutputStream os = t.getResponseBody();
                os.write(page.getBytes());
                os.close();
            }
        }
    }
        
    public static boolean canCommit(String transaction, String serverPort) throws IOException {
        String       postUrl       = "http://127.0.0.1:" + serverPort + "/canCommit";
        Gson         gson          = new Gson();
        HttpClient httpClient = new DefaultHttpClient();
        final HttpParams httpParams = httpClient.getParams();

        HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
        HttpConnectionParams.setSoTimeout(httpParams, 30000);
        HttpPost     post          = new HttpPost(postUrl);
        
        TidPostObject resource = new TidPostObject();
        resource.setTid(transaction);
        
        StringEntity postingString = new StringEntity(gson.toJson(resource));
        post.setEntity(postingString);
        post.setHeader("Content-type", "application/json");
        
        try {
            HttpResponse  response = httpClient.execute(post);
            HttpEntity entity = response.getEntity();
            if (entity != null) {

                InputStream instream = entity.getContent();
                String response_string = convertStreamToString(instream);
                System.out.println("RESPONSE: " + response_string);
                JSONObject myObject = new JSONObject(response_string);
                instream.close();
                return Boolean.parseBoolean(myObject.get("response").toString());
            }
        } catch(Exception e){ 
            return false;
        }
        return false;
    }
    
    public static boolean rollBack(String transaction, String serverPort) throws IOException {
        String       postUrl       = "http://127.0.0.1:" + serverPort + "/rollbackTransaction";
        Gson         gson          = new Gson();
        HttpClient   httpClient    = HttpClientBuilder.create().build();
        HttpPost     post          = new HttpPost(postUrl);
        
        TidPostObject resource = new TidPostObject();
        resource.setTid(transaction);
        
        StringEntity postingString = new StringEntity(gson.toJson(resource));
        post.setEntity(postingString);
        post.setHeader("Content-type", "application/json");
        HttpResponse  response = httpClient.execute(post);
        HttpEntity entity = response.getEntity();

        if (entity != null) {

            InputStream instream = entity.getContent();
            String response_string = convertStreamToString(instream);
            System.out.println("RESPONSE: " + response_string);
            JSONObject myObject = new JSONObject(response_string);
            instream.close();
            return myObject.get("status").toString().equals("rollbacked");
        }
        return false;
    }
    
    public static String finishTransaction(String transaction, String serverPort) throws IOException {
        String       postUrl       = "http://127.0.0.1:" + serverPort + "/finishTransaction";
        Gson         gson          = new Gson();
        HttpClient   httpClient    = HttpClientBuilder.create().build();
        HttpPost     post          = new HttpPost(postUrl);
        
        TidPostObject resource = new TidPostObject();
        resource.setTid(transaction);
        
        StringEntity postingString = new StringEntity(gson.toJson(resource));
        post.setEntity(postingString);
        post.setHeader("Content-type", "application/json");
        HttpResponse  response = httpClient.execute(post);
        HttpEntity entity = response.getEntity();

        if (entity != null) {

            InputStream instream = entity.getContent();
            String response_string = convertStreamToString(instream);
            System.out.println("RESPONSE: " + response_string);
            JSONObject myObject = new JSONObject(response_string);
            instream.close();
            return myObject.get("status").toString();
        }
        return "not ok";
    }
    
    public static String postToTransactionServer(String value, String serverPort, 
            String endpoint, String userId) throws IOException {
        String       postUrl       = "http://127.0.0.1:" + serverPort + "/" + endpoint;
        System.out.println(postUrl);
        Gson         gson          = new Gson();
        HttpClient   httpClient    = HttpClientBuilder.create().build();
        HttpPost     post          = new HttpPost(postUrl);
        
        StringEntity postingString;
        if (endpoint.equals("bookRoom")) {
            RoomPostObject resource = new RoomPostObject();
            resource.setRoomNumber(value);
            resource.setUserId(userId);
            postingString = new StringEntity(gson.toJson(resource));
        } else {
            SeatPostObject resource = new SeatPostObject();
            resource.setSeatNumber(value);
            resource.setUserId(userId);
            postingString = new StringEntity(gson.toJson(resource));
        }
        
        
        post.setEntity(postingString);
        post.setHeader("Content-type", "application/json");
        
        HttpResponse  response = httpClient.execute(post);
        HttpEntity entity = response.getEntity();

        if (entity != null) {

            InputStream instream = entity.getContent();
            String response_string = convertStreamToString(instream);
            System.out.println("RESPONSE: " + response_string);
            JSONObject myObject = new JSONObject(response_string);
            instream.close();
            return myObject.get("tid").toString();
        }
               
        return "not ok";
        
    }
    
    private static String convertStreamToString(InputStream is) throws IOException {

    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    StringBuilder sb = new StringBuilder();

    String line = null;
    while ((line = reader.readLine()) != null) {
        sb.append(line + "\n");
    }
    try {
        is.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
    return sb.toString();
}
    
}
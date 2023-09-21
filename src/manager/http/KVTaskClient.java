package manager.http;
import exception.KVTaskClientBadStatusCodeException;
import exception.ClientException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;

public class KVTaskClient implements KeyValueClient {
    private final String url;
    private final String apiToken;

    private String registerApiToken (String url) {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response;
        URI uri = URI.create(url + "/register");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Accept", "application/json")
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new ClientException(e.getMessage());
    }
        return response.body();
    }

    public KVTaskClient(String url) {
        this.url = url;
        this.apiToken = registerApiToken(url);
    }

    @Override
    public void put(String key, String json) {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response;
        URI uri = URI.create(url + "/save/" + key + "?API_TOKEN=" + apiToken);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json, Charset.defaultCharset()))
                .uri(uri)
                .header("Accept", "application/json")
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new KVTaskClientBadStatusCodeException("Bad status = " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new ClientException(e.getMessage());
        }
    }

    @Override
    public String load(String key) {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response;
        URI uri = URI.create(url + "/load/" + key + "?API_TOKEN=" + apiToken);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Accept", "application/json")
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new KVTaskClientBadStatusCodeException("Bad status = " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new ClientException(e.getMessage());
        }
        return response.body();
    }
}

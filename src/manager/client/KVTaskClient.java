package manager.client;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
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
        throw new RuntimeException();
    }
        return response.body();
    }

    public KVTaskClient(String url) {
        this.url = url;
        this.apiToken = registerApiToken(url);
    }

    public void put(String key, String json) {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response;
        URI uri = URI.create(url + "/save/" + key + "?API_TOKEN=" + apiToken);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(uri)
                .header("Accept", "application/json")
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException();
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException();
        }
    }

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
                throw new RuntimeException();
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException();
        }
        return response.body();
    }
}

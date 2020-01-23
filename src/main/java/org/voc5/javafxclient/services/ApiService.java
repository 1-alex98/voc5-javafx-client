package org.voc5.javafxclient.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import org.voc5.javafxclient.dto.Vocabulary;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ApiService {
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    private static ApiService ourInstance = new ApiService();
    private String url = "https://api.voc5.org/";
    private boolean loggedIn = false;
    private OkHttpClient plainClient = new OkHttpClient();
    private OkHttpClient loggedInClient;
    private String email;
    private Gson gson = new Gson();
    private String password;

    private ApiService() {
    }

    public static ApiService getInstance() {
        if (ourInstance == null) {
            ourInstance = new ApiService();
        }
        return ourInstance;
    }

    public CompletableFuture<String> login(String email, String password) {
        return CompletableFuture.supplyAsync(() -> {
            Request request = new Request.Builder()
                    .url(url + "login")
                    .header("email", email)
                    .header("password", password)
                    .build();
            try {
                try (Response response = plainClient.newCall(request).execute()) {
                    String responseString = Objects.requireNonNull(response.body()).string();
                    if (response.code() != 200) {
                        return responseString;
                    } else {
                        loggedIn = true;
                        this.email = email;
                        this.password = password;
                        creatLoggedInClient();
                        return null;
                    }
                }
            } catch (Exception e) {
                return "A connection error happened during login";
            }

        });
    }

    public CompletableFuture<List<Vocabulary>> getAllVocabulary() {
        return CompletableFuture.supplyAsync(() -> {
            Request request = new Request.Builder()
                    .url(url + "voc")
                    .build();
            try {
                try (Response response = loggedInClient.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        return gson.fromJson(response.body().string(), new TypeToken<List<Vocabulary>>() {
                        }.getType());
                    } else {
                        throw new IllegalStateException("Api return error: " + (response.body() == null ? "" : response.body().string()) + " and status code:" + response.code());
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void creatLoggedInClient() {
        loggedInClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    Request newRequest;

                    newRequest = request.newBuilder()
                            .addHeader("email", email)
                            .addHeader("password", password)
                            .build();
                    return chain.proceed(newRequest);
                }).build();
    }

    public String getEmail() {
        return email;
    }

    public void reset() {
        ourInstance = null;
    }

    public CompletableFuture<Void> updateVocabulary(Vocabulary vocabulary) {
        return CompletableFuture.supplyAsync(() -> {
            Request request = new Request.Builder()
                    .patch(RequestBody.create(gson.toJson(vocabulary), JSON))
                    .url(url + "voc/" + vocabulary.getId())
                    .build();
            try {
                try (Response response = loggedInClient.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        return null;
                    } else {
                        throw new IllegalStateException("Api return error: " + (response.body() == null ? "" : response.body().string()) + " and status code:" + response.code());
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<Void> delete(int id) {
        return CompletableFuture.supplyAsync(() -> {
            Request request = new Request.Builder()
                    .delete()
                    .url(url + "voc/" + id)
                    .build();
            try {
                try (Response response = loggedInClient.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        return null;
                    } else {
                        throw new IllegalStateException("Api return error: " + (response.body() == null ? "" : response.body().string()) + " and status code:" + response.code());
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<Void> saveVocabulary(Vocabulary vocabulary) {
        return CompletableFuture.supplyAsync(() -> {
            Request request = new Request.Builder()
                    .post(RequestBody.create(gson.toJson(vocabulary), JSON))
                    .url(url + "voc")
                    .build();
            try {
                try (Response response = loggedInClient.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        return null;
                    } else {
                        throw new IllegalStateException("Api return error: " + (response.body() == null ? "" : response.body().string()) + " and status code:" + response.code());
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<Vocabulary> getRandomVocabulary() {
        return CompletableFuture.supplyAsync(() -> {
            Request request = new Request.Builder()
                    .url(url + "voc/random")
                    .build();
            try {
                try (Response response = loggedInClient.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        return gson.fromJson(response.body().string(), Vocabulary.class);
                    } else {
                        throw new IllegalStateException("Api return error: " + (response.body() == null ? "" : response.body().string()) + " and status code:" + response.code());
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}

package io.readme;

import java.io.IOException;

import java.util.Base64;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public final class Api {
  private final OkHttpClient client;

  public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

  private final String BUILD_HOST = "https://api.readme.build";
  private final String BUILD_VERSION = "v1";
  private final String API_KEY_BASE64;

  public Api(String API_KEY) {
    this.client = ApiUtils.createClient();

    this.API_KEY_BASE64 = new String(Base64.getEncoder().encode(API_KEY.getBytes()));
  }

  public ApiResponse run(String service, String action, String json) throws IOException {
    //Calls service with action and a JSON serilized copy of data
    String[] service_parts = service.split("@");
    String service_url = service;
    String service_version = null;

    if(service_parts.length == 3) {
      //The service value was passed in as @team/service@version
      service_url = "@"+service_parts[1];
      service_version = service_parts[2];
    }

    String url = String.format("%s/%s/run/%s/%s", BUILD_HOST, BUILD_VERSION, service_url, action);
    RequestBody body = RequestBody.create(JSON, json);
    Request.Builder request = new Request.Builder()
                                 .url(url)
                                 .addHeader("Authorization", "Basic "+this.API_KEY_BASE64+":")
                                 .addHeader("X-Build-Meta-Language", "java@1.8.0")
                                 .addHeader("X-Build-Meta-SDK", "api-java@0.0.1")
                                 .post(body);
    if(service_version != null) {
      request = request.addHeader("X-Build-Version-Override", service_version);
    }

    try (Response response = client.newCall(request.build()).execute()) {
      return new ApiResponse(response);
    }
  }
}

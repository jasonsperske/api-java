package io.readme;

import java.io.IOException;

import okhttp3.Response;

public final class ApiResponse {
  public final int statusCode;
  public final String body;

  public ApiResponse(Response response) throws IOException {
    this.statusCode = response.code();
    this.body = response.body().string();
  }
}

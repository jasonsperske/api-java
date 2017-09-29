package io.readme;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ApiTests {
  private String API_KEY = "[YOUR API_KEY]";

  @Test
  void shouldCallKnownApi() throws IOException {
    Api api = new Api(API_KEY);
    ApiResponse res = api.run("math", "add", "{\"numbers\": [1,2,3]}");

    assertEquals(200, res.statusCode);
    assertEquals("6", res.body);
  }
}

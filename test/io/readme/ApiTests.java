package io.readme;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Properties;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ApiTests {
  private static String API_KEY;

  static {
    try {
      Properties config = ApiUtils.loadProperties("readme.properties");
      API_KEY = config.getProperty("API_KEY");
    } catch(IOException ex) {
      System.err.println("Missing readme.properties");
    }
  }

  @Test
  void shouldCallKnownApi() throws IOException {
    Api api = new Api(API_KEY);
    ApiResponse res = api.run("math", "add", "{\"numbers\": [1,2,3]}");

    assertEquals(200, res.statusCode);
    assertEquals("6", res.body);
  }
}

package com.automation.tests.day8;
import com.automation.utilities.ConfigurationReader;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.*;

import io.restassured.http.ContentType;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;

public class BadSSLTest {
    @BeforeAll
    public static void setup() {
        baseURI = "https://untrusted-root.badssl.com/";
    }

    @Test
    @DisplayName("Access website with bad SSL")
    public void test(){
        get().then().statusCode(200); //SSLHandshakeException thrown
    }

    @Test
    @DisplayName("Access website with bad SSL (Solution)")
    public void test2(){
     Response response =
              given()
                    .contentType(ContentType.JSON)
                    .relaxedHTTPSValidation() /* This means that you'll trust all hosts regardless if the SSL certificate is invalid */
              .when().get().prettyPeek();
     System.out.println(response.statusCode());
     Assertions.assertEquals(200, response.getStatusCode());
    }


}

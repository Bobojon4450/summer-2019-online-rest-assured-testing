package com.automation.tests.day2;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;

public class MetaWeatherTests {

    private String baseURI = "https://www.metaweather.com/api/";

    /*
      /api/location/search/?query=san
      /api/location/search/?query=london
      /api/location/search/?lattlong=36.96,-122.02
      /api/location/search/?lattlong=50.068,-5.316
    */


    @Test
    public void test() {
        Response response =
                given()
                        .baseUri(baseURI).basePath("location/search")
                        .queryParam("query", "Richmond").get();
        assertEquals(200, response.getStatusCode());
        System.out.println(response.prettyPrint());
    }

    @Test /* The way of writing test above in different way */
    public void testCopy() {
        Response response =
                given()
                        .queryParam("query", "Richmond")
                        .get(baseURI + "location/search");
        assertEquals(200, response.getStatusCode());
        System.out.println(response.prettyPrint());
    }

    @Test
    public void test2() {
        Response response =
                given()
                        .pathParam("woeid", "2480894")
                        .baseUri(baseURI).get("location/{woeid}");
        System.out.println(response.prettyPrint());
        String cookieValue = response.getCookie("cookieName");
        System.out.println("cookieValue: "+cookieValue);
    }
}

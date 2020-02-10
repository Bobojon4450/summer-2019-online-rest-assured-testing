package com.automation.tests.day8;

import com.automation.utilities.ConfigurationReader;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;

public class OMDBTestAPIKey {

    @BeforeAll
    public static void setup() {
        baseURI = ConfigurationReader.getProperty("omdb_uri");
    }

    @Test
    @DisplayName("Verify that unauthorized user cannot get info about movies from OMDB api")
    public void test(){
        given()
            .contentType(ContentType.JSON)
            .queryParam("t", "Home Alone")
            .baseUri(baseURI)
        .when()
                .get().prettyPeek()
        .then()                                                  /*  Unauthorized */
                .assertThat().statusCode(401).body("Error", is("No API key provided."));
    }

    @Test
    @DisplayName("Verify that Macauly Culkin appears in actors list for Home Alone movie")
    public void test2(){
        given()
                .contentType(ContentType.JSON)
                .queryParam("apikey", "22eef029")
                .queryParam("t", "Home Alone")
        .when()
                .get().prettyPeek()
        .then()
                .assertThat().statusCode(200).body("Actors", containsString("Macaulay Culkin"));
    }

    @Test
    @DisplayName("Verify that Macauly Culkin appears in actors list for Home Alone movie")
    public void test2Copy(){
        Response response = given()
                .contentType(ContentType.JSON)
                .queryParam("apikey", "22eef029")
                .queryParam("t", "Home Alone")
        .when()
                .get();

        response.then().assertThat().statusCode(200).body("Actors", containsString("Macaulay Culkin"));
        /*Here we utilized the Map because we have a single object in response. Otherwise we use a list of maps */
        Map<String, ?> payload = response.getBody().as(Map.class);
        System.out.println(payload);

        // Map is a collection of entries. Iterates the entries of the single map.
        for (Map.Entry<String, ?> each: payload.entrySet()) {
            System.out.println("Key: "+each.getKey() +" "+" Value: "+each.getValue());
        }






    }





}












 /*  Here is your key: 22eef029
        Please append it to all of your API requests,
        OMDb API: http://www.omdbapi.com/?i=tt3896198&apikey=22eef029
        Click the following URL to activate your key: http://www.omdbapi.com/apikey.aspx?VERIFYKEY=3e767970-da55-4dc5-a9ff-267b67d8b68c
        If you did not make this request, please disregard this email.*/
package com.automation.tests.day8;
import com.automation.utilities.ConfigurationReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.*;

import io.restassured.http.ContentType;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.get;

public class BearerTokenTestsWithBookIt {

    @BeforeAll
    public static void setup() {     baseURI = ConfigurationReader.getProperty("bookit.qa1") ;  }

    @Test
    @DisplayName("Get list of rooms")
    public void test(){
        Response response =
                given()
                    .header("Authorization", getToken())
                .when()
                    .get("/api/rooms").prettyPeek();
    }




    /**
     * Method that generates access token
     * @return bearer token
     */
    public String getToken() {
        Response response =
                given()
                        .queryParam("email", ConfigurationReader.getProperty("team.leader.email"))
                        .queryParam("password", ConfigurationReader.getProperty("team.leader.password"))
                        .when()
                        .get("/sign").prettyPeek();
        return response.jsonPath().getString("accessToken");
    }

}

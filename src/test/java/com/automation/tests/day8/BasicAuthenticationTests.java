package com.automation.tests.day8;

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

public class BasicAuthenticationTests {
    @BeforeAll
    public static void setup() {
        baseURI = "http://practice.cybertekschool.com";
    }

    @Test
    @DisplayName("basic authentication test")
    public void test(){
        given()
                .auth().basic("admin", "admin")
        .when()
                .basePath("/basic_auth").get().prettyPeek()
        .then().statusCode(200);
    }


}

package com.automation.tests.day7;

import com.automation.utilities.ConfigurationReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.automation.pojos.Spartan;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeAll;

import com.automation.pojos.Job;
import com.automation.pojos.Locations;
import com.automation.utilities.ConfigurationReader;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.BeforeAll;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;
import static io.restassured.RestAssured.baseURI;

public class SpartansTestDay {

    @BeforeAll
    public static void setup() {
        baseURI = ConfigurationReader.getProperty("spartan.uri");
    }

    @Test
    @DisplayName("Add new user by using external JSON file")
    public void test(){
        File file = new File(System.getProperty("user.dir") + "/spartan.json");
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(file)
        .when()
                .post("/spartans").prettyPeek()//helps to trace the error
        .then()
                .assertThat().statusCode(201)
                .body("success", is("A Spartan is Born!"));
    }

    @Test
    @DisplayName("Add new user by using Map<>")
    public void test2(){
        Map<String, Object> spartan = new HashMap<>();
        spartan.put("phone", 12345678910L);
        spartan.put("gender", "Male");
        spartan.put("name", "Vasya");

                given()
                        .accept(ContentType.JSON)
                        .contentType(ContentType.JSON)
                        .body(spartan)
                .when()
                        .post("/spartans").prettyPeek()
                .then()
                        .assertThat().statusCode(201)
                        .body("success", is("A Spartan is Born!"))//success comes from json
                        .body("data.name", is("Vasya"))
                        .body("data.gender", is("Male"))
                        .body("data.phone", is(1177080808));//data.phone comes from json
    }

                         /*{
                            "success": "A Spartan is Born!",
                            "data": {
                                "id": 157,
                                "name": "AutomationTest",
                                "gender": "Male",
                                "phone": 1177080808
                            }*/

    @Test
    @DisplayName("update spartan only name PATCH")
    public void test3(){
        Map<String, Object> update = new HashMap<>();
        update.put("name", "Safina");
        update.put("gender", "Female");

        given()
                .contentType(ContentType.JSON)
                .body(update)
                .pathParam("id", 1)
        .when()
                .patch("/spartans/{id}").prettyPeek()
        .then().assertThat().statusCode(204);

        /* accept(ContentType.JSON) : no payload are received here */
    }



}

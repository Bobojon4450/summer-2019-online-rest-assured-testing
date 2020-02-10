package com.automation.tests.day9;
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

public class PreemptiveAuthentication {

    @BeforeAll
    public static void setup() {     baseURI = "http://practice.cybertekschool.com";  }

    @Test
    @DisplayName("Normal basic authentication")
    public void test(){

        /*  Actually, it will make 2 calls in the background:
            1st: with no credentials, then will get 401,
            to ensure that only requested server will get credentials
            and then 2nd call will be with credentials  */
        given()
                .auth().basic("admin", "admin")
        .when()
                .get("/basic_auth").prettyPeek()
        .then().assertThat().statusCode(200);
    }


    @Test
    @DisplayName("Preemtive authentication")
    public void test2(){
        /* It sends credentials with 1st request
           The benefit is that you load network less that with 2 calls */
        given()
                .auth().preemptive().basic("admin", "admin")
        .when()
                .get("/basic_auth").prettyPeek()
        .then().assertThat().statusCode(200);
    }


}

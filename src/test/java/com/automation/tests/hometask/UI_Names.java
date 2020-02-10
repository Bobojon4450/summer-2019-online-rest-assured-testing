package com.automation.tests.hometask;

import com.automation.pojos.Student;
import com.automation.utilities.ConfigurationReader;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static org.hamcrest.Matchers.*;
import javax.swing.text.AbstractDocument;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;
import static io.restassured.RestAssured.baseURI;

public class UI_Names {

    @BeforeAll
    public static void setUp() {baseURI = "https://uinames.com/api/"; }


    @Test
    @DisplayName("Verify status code 200, content type application/json; charset=utf-8\n" +
                 "Verify that name, surname, gender, region fields have value")
    public void NoParamsTest(){
           given()
                 .accept(ContentType.JSON)
                 .baseUri(baseURI)
           .when()
                   .get()
           .then()
                   .assertThat().statusCode(200).contentType(ContentType.JSON.withCharset("utf-8"))
                   .body("name", is(notNullValue()))
                   .body("surname", is(notNullValue()))
                   .body("gender", is(notNullValue()))
                   .body("region", is(notNullValue()));
    }

    @Test
    @DisplayName("Verify status code 200, content type application/json; charset=utf-8\n" +
            "Verify that name, surname, gender, region fields have value")
    public void NoParamsTest2(){
        Response response = given().get();
        String responseStr = response.headers().toString()+"\n\n"+response.body().prettyPrint();
        System.out.println(responseStr);
    }

    @Test
    @DisplayName("Verify that value of gender field is same from step 1")
    public void GenderTest(){
        given()
                .accept(ContentType.JSON)
                .queryParam("gender", "female")
                .baseUri(baseURI).get().prettyPeek()
        .then()
            .assertThat().statusCode(200).contentType(ContentType.JSON.withCharset("utf-8"))
            .assertThat().body("gender", is("female"));
    }

    @Test
    @DisplayName("Verify that value of gender field is same from step 1\n" +
                 "Verify that value of region field is same from step 1")
    public void TwoparamsTest(){
        given()
                .accept(ContentType.JSON)
                .queryParam("region", "germany")
                .queryParam("gender", "male")
                .baseUri(baseURI)
         .when()
                .get()
        .then()
                .assertThat().statusCode(200)
                .assertThat().contentType(ContentType.JSON.withCharset("utf-8"))
                .body("gender", is("male"))
                .body("region", is("Germany"));
    }

    @Test
    @DisplayName("Verify status code 400 and status line contains Bad Request")
    public void InvalidGenderTest(){
        given()
                .accept(ContentType.JSON)
                .queryParam("gender", "samochka")
        .when()
                .get().prettyPeek()
        .then()
                .assertThat().statusCode(400)
                .statusLine(containsStringIgnoringCase("400 Bad Request"))
                .body("error", is("Invalid gender"));
    }

    @Test
    @DisplayName("Verify status code 400 and status line contains Bad Request\n" +
                 "Verify that value of error field is Region or language not found")
    public void InvalidRegionTest(){
        given()
                .accept(ContentType.JSON)
                .queryParam("region", "maskvabad")
        .when()
                .get().prettyPeek()
        .then()
                .assertThat().statusCode(400).statusLine(containsStringIgnoringCase("Bad Request"))
                .body("error", is("Region or language not found"));;
    }


    @Test
    @DisplayName("Verify status code 400 and status line contains Bad Request\n" +
            "Verify that value of error field is Region or language not found")
    public void AmountAndRegionsTest(){
        given()
                .accept(ContentType.JSON)
                .queryParam("region", "New Zealand")
                .queryParam("amount", "2")
        .when()
                .get().prettyPeek()
        .then()
                .assertThat().statusCode(200).contentType(ContentType.JSON.withCharset("utf-8"))
                .body("[0].name", is(not("[1].name")))
                .body("[0].surname", is(not("[1].surname")));
    }

    @Test
    @DisplayName("Verify status code 200, content type application/json; charset=utf-8\n" +
                 "Verify that all objects the response have the same region and gender passed in step 1")
    public void ThreeParamsTest(){
        given()
                .accept(ContentType.JSON)
                .queryParam("region", "Romania")
                .queryParam("gender", "female")
                .queryParam("amount", 2)
         .when()
                .get().prettyPeek()
         .then()
                .assertThat().statusCode(200).contentType(ContentType.JSON.withCharset("utf-8"))
                .body("[0].region", is("Romania"))
                .body("[0].gender", is("female"))
                .body("[1].region", is("Romania"))
                .body("[1].gender", is("female"));
    }

    @Test
    @DisplayName("Verify status code 200, content type application/json; charset=utf-8\n" +
                 "Verify that number of objects returned in the response is same as the amount passed in step 1")
    public void AmountCountTest(){
        Response response =
                given()
                    .accept(ContentType.JSON)
                    .queryParam("amount", 2)
            .when()
                    .get().prettyPeek();
        assertEquals(200, response.getStatusCode());
        assertEquals("application/json; charset=utf-8", ContentType.JSON.withCharset("utf-8"));
        List<?> list = response.body().jsonPath().get();
        assertTrue(list.size() == 2);
    }


}

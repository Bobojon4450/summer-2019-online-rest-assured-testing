package com.automation.tests.day2;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ExchangeRatesAPITest {

    private String baseURI = "http://api.openrates.io/";


    @Test
    public void test1() {
        Response response = given().baseUri(baseURI+"latest").get();
        assertEquals(200, response.getStatusCode());
        System.out.println(response.prettyPrint());
    }

    @Test
    public void test2() {
        Response response = given().header("accept", "application/json").get(baseURI+"latest");
        assertEquals(200, response.getStatusCode());
        assertEquals("application/json", response.getHeader("Content-Type"));
        assertEquals("application/json", response.getContentType());
    }

    @Test
    public void test3() {
        Response response = given().header("accept", "application/json").get(baseURI+"latest?base=USD");
        assertEquals(200, response.getStatusCode());
        assertEquals("application/json", response.getContentType());
        System.out.println(response.prettyPrint());
    }

    @Test   /*    Same test with different approach */
    public void test4(){
        //#TASK: get currency exchange rate for dollar. By default it's euro.
        /* Response response = given().get(baseURI+"latest?base=USD"); */
        Response response = given().baseUri(baseURI).
                basePath("latest").
                queryParam("base", "USD").get();
        assertEquals(200, response.getStatusCode());
        System.out.println(response.prettyPrint());
    }

    @Test
    public void test5() {
        Response response = given().baseUri(baseURI).basePath("latest").queryParam("base", "GBP").get();
        assertEquals(200, response.getStatusCode());
        String todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        System.out.println("Today's date: "+todayDate);
        assertTrue(response.getBody().asString().contains(todayDate));
    }

    /* let's get currency exchange rate for year 2000
       GET https://api.exchangeratesapi.io/history?start_at=2018-01-01&end_at=2018-09-01&base=USD&symbols=ILS,JPY,EUR*/

    @Test
    public void test6() {
        Response response = given().baseUri(baseURI+"history").queryParam("start_at", "2000-01-01").
                queryParam("end_at", "2000-12-31").
                queryParam("base", "USD").
                queryParam("symbols", "EUR", "JPY", "GBP").get();
        System.out.println(response.prettyPrint());
    }

    @Test
    public void test7() {
        Response response = given().baseUri(baseURI+"latest").
                queryParam("base", "USD").get();
        assertEquals(200, response.getStatusCode());
        System.out.println(response.prettyPrint());

    }

    @Test
    public void test8() {
        Response response = given().
                baseUri(baseURI + "latest").
                queryParam("base", "USD").
                get();
        String body = response.getBody().asString();
        assertEquals(200, response.getStatusCode());
        assertTrue(body.contains("\"base\":\"USD\""));
    }
}

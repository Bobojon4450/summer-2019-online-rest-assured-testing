package com.automation.tests.day2;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ExchangeRatesAPITest {

    private String baseURI = "http://api.openrates.io/";


    @Test
    public void test1() {
        Response response =
                given()
                        .baseUri(baseURI+"latest").get();
        assertEquals(200, response.getStatusCode());
        System.out.println(response.prettyPrint());
    }

    @Test
    public void test2() {
        Response response =
                given()
                        .header("accept", "application/json")
                        .get(baseURI+"latest");
        assertEquals(200, response.getStatusCode());
        //verify that data is coming as json
        assertEquals("application/json", response.getHeader("Content-Type"));
        //another way
        assertEquals("application/json", response.getContentType());
    }

    @Test
    public void test2Copy() {
        Response response =
                given()
                        .header("accept", "application/json")
                        .get(baseURI + "latest");
        assertEquals(200, response.getStatusCode());
        assertEquals("application/json", response.getContentType());
        assertEquals("application/json", response.getHeader("Content-type"));
    }


    // TASK: get currency exchange rate for dollar. By default it's euro.
    @Test
    public void test3() {
        Response response =
                given()
                        .header("accept", "application/json")
                        .get(baseURI+"latest?base=USD");
        assertEquals(200, response.getStatusCode());
        assertEquals("application/json", response.getContentType());
        System.out.println(response.prettyPrint());
    }

    @Test
    public void test3Copy() {
        Response response =
                given()
                        .header("accept", "application/json")
                        .baseUri(baseURI).basePath("latest") //latest is a path
                        .queryParam("base","USD").get();
        assertEquals(200, response.getStatusCode());
        assertEquals("application/json", response.getContentType());
        System.out.println(response.prettyPrint());
    }



    @Test   /*    Same test with different approach */
    public void test4(){
        /* TASK: get currency exchange rate for dollar. By default it's euro */
        /* Response response = given().get(baseURI+"latest?base=USD"); */
        Response response =
                given()
                        .baseUri(baseURI).basePath("latest")
                        .queryParam("base", "USD").get();//get(): HTTP method
        assertEquals(200, response.getStatusCode());
        System.out.println(response.prettyPrint());
    }

    @Test
    public void test5() {
        Response response =
                given()
                        .baseUri(baseURI).basePath("latest")
                        .queryParam("base", "GBP").get();
        assertEquals(200, response.getStatusCode());
        String todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        System.out.println("Today's date: "+todayDate);
        System.out.println(response.prettyPrint());
        assertEquals(todayDate,response.getBody().asString());
        assertTrue(response.getBody().asString().contains(todayDate));
    }

/* let's get currency exchange rate for year 2000
   GET https://api.exchangeratesapi.io/history?start_at=2018-01-01&end_at=2018-09-01&base=USD&symbols=ILS,JPY,EUR*/

    @Test
    public void test6() {
        Response response =
                given()
                        .baseUri(baseURI).basePath("history")
                        .queryParam("start_at", "2000-01-01")
                        .queryParam("end_at", "2000-12-31")
                        .queryParam("base", "USD")
                        .queryParam("symbols", "EUR", "JPY", "GBP").get();
        System.out.println(response.prettyPrint());
    }

    @Test
    public void test7() {
        Response response =
                given()
                        .baseUri(baseURI).basePath("latest")
                        .queryParam("base", "USD").get();
        assertEquals(200, response.getStatusCode());
        System.out.println(response.prettyPrint());
    }

    @Test
    public void test8() {
        Response response =
                given()
                        .baseUri(baseURI).basePath("latest")
                        .queryParam("base", "USD").get();
        String body = response.getBody().asString();
        assertEquals(200, response.getStatusCode());
        assertTrue(body.contains("\"base\":\"USD\""));
    }

    @Test
    public void test9(){
        Response response =
                given()
                        .accept(ContentType.JSON)
                        .baseUri(baseURI).basePath("latest")
                        .queryParam("base", "USD").get();
        assertEquals(200, response.getStatusCode());
        String body = response.getBody().asString();
        assertTrue(body.contains("\"base\":\"USD\""));
        System.out.println(response.prettyPrint());
    }
}

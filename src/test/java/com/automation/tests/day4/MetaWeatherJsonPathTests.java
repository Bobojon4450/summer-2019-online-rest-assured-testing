package com.automation.tests.day4;

import com.automation.utilities.ConfigurationReader;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import com.automation.utilities.ConfigurationReader;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import java.util.*;
import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static io.restassured.RestAssured.baseURI;

public class MetaWeatherJsonPathTests {

    @BeforeAll
    public static void setUp() {
        baseURI = ConfigurationReader.getProperty("meta.weather");
    }

    /**
     * Given accept type is JSON
     * When users sends a GET request to "/search"
     * And query parameter is 'New'
     * Then user verifies that payload contains 5 objects
     */
    @Test
    @DisplayName("Verify there 5 cities")
    public void test() {
        Response response = given()
                .accept(ContentType.JSON)
                .baseUri(baseURI).basePath("/search")
                .queryParam("query", "New").get().prettyPeek();
        assertEquals(200, response.getStatusCode());
        List<?> list = response.thenReturn().jsonPath().getList("woeid");
        assertEquals(5, list.size());
    }

    @Test
    @DisplayName("Verify there 5 cities")
    public void test_Copy() {
        given()
                .accept(ContentType.JSON)
                .basePath("/search")
                .queryParam("query", "New")
                .when().get()
                .then().assertThat().statusCode(200)
                .body("", hasSize(5))
                .log().body(true);
    }

    /**
     * Given accept type is JSON
     * When users sends a GET request to "/search"
     * And query parameter is New
     * Then user verifies that 1st object has following info:
     * |title   |location_type|woeid  |latt_long          |
     * |New York|City         |2459115|40.71455,-74.007118|
     */
    @Test
    @DisplayName("Verifies that 1st city has following info: New York, City, 2459115, 40.71455,-74.007118")
    public void test2() {
        given()
                .accept(ContentType.JSON)
                .basePath("/search")
                .queryParam("query", "new").get()
        .then()
                .assertThat().statusCode(200)
                .body("title[0]", is("New York"))
                .body("location_type[0]", is("City"))
                .body("woeid[0]", is(2459115))
                .body("latt_long[0]", is("40.71455,-74.007118"))
        .and()
                .log().body(true);
    }

    @Test
    @DisplayName("Verifies that 1st city has following info: New York, City, 2459115, 40.71455,-74.007118")
    public void test2_Copy() {
        Map<String, String> expected = new HashMap<>();
        expected.put("title", "New York");
        expected.put("location_type", "City");
        expected.put("woeid", "2459115");
        expected.put("latt_long", "40.71455,-74.007118");

        Response response = given().
                accept(ContentType.JSON).
                queryParam("query", "New").
        when().
                get("/search");
        JsonPath jsonPath = response.jsonPath();
        /* String.class will force jsonPath to return map with String as key and value */
        assertEquals(expected, jsonPath.getMap("[0]", String.class, String.class));
        /* for first title, title[0], but for first object, we can say just [0] */
        List<Map<String, ?>> values = jsonPath.get();
        for (Map<String, ?> value : values) {
            System.out.println(value.keySet());
        }//[title, location_type, woeid, latt_long]

        for (Map<String, ?> value : values) {
            System.out.println(value.entrySet());
        }//[title=New York, location_type=City, woeid=2459115, latt_long=40.71455,-74.007118]

        for (Map<String, ?> value : values) {
            System.out.println(value);
        }//{title=New York, location_type=City, woeid=2459115, latt_long=40.71455,-74.007118}
    }

    /**
     * Given accept type is JSON
     * When users sends a GET request to "/search"
     * And query parameter is 'Las'
     * Then user verifies that payload  contains following titles:
     * |Glasgow  |
     * |Dallas   |
     * |Las Vegas|
     */
    @Test
    public void test3() {
        given()
                .accept(ContentType.JSON)
                .queryParam("query", "Las")
                .basePath("/search")
        .when()
                .get()
        .then()
                .assertThat().statusCode(200)
                .body("title", contains("Glasgow", "Dallas", "Las Vegas"))
                .log().body(true);
    }

    @Test
    public void test3_Copy() {
        given()
                .accept(ContentType.JSON)
                .queryParam("query", "Las")
                .basePath("/search")
        .when()
                .get()
        .then()
                .assertThat().statusCode(200)
                .body("title", hasItems("Glasgow", "Dallas", "Las Vegas"))
                .log().body(true);
    }

    /*  hasItems - exact match      */
    /*  contains - partial match    */

    /**
     * Given accept type is JSON
     * When users sends a GET request to "/search"
     * And query parameter is 'Las'
     * Then verify that every item in payload has location_type City
     */
    @Test
    public void test4() {
        given()
                .accept(ContentType.JSON)
                .basePath("/search")
                .queryParam("query", "Las").get().prettyPeek()
        .then()
                .assertThat().statusCode(200)
                .body("location_type", everyItem(is("City")));
    }

    /**
     * Given accept type is JSON
     * When users sends a GET request to "/location"
     * And path parameter is '44418'
     * Then verify following that payload contains weather forecast sources
     * |BBC                 |
     * |Forecast.io         |
     * |HAMweather          |
     * |Met Office          |
     * |OpenWeatherMap      |
     * |Weather Underground |
     * |World Weather Online|
     */

    @Test
    @DisplayName("")
    public void test5() {
        List<String> expected = new ArrayList<>();
        expected.add("BBC");
        expected.add("Forecast.io");
        expected.add("HAMweather");
        expected.add("Met Office");
        expected.add("OpenWeatherMap");
        expected.add("Weather Underground");
        expected.add("World Weather Online");

        Response response = given()
                .accept(ContentType.JSON)
                .pathParam("woeid", "44418")
                .basePath("/location/{woeid}").get(); // = new Response;
        assertEquals(200, response.getStatusCode());
         /*JsonPath jsonPath = response.jsonPath();
         List<String> actual = jsonPath.getList("sources.title");*/
         //another approach
        List<String> actual = response.jsonPath().getList("sources.title");
        assertEquals(expected, actual);
    }


}
/*
 @Test
        @DisplayName("Verify that payload contains 5 objects")
        public void test1(){
          Response response =   given().accept(ContentType.JSON).
                  queryParam("query","New").
                  get("location/search");
            assertEquals(200, response.getStatusCode());
            response.prettyPrint();
            JsonPath json = response.thenReturn().jsonPath();
            List<Map<?,?>>list = json.get();
            for(Map<?,?> map: list){
                System.out.println(map);
            }
            assertTrue(list.size()==5);
        }
*/
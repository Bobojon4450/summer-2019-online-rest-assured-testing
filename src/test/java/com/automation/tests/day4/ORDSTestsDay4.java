package com.automation.tests.day4;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import com.automation.utilities.ConfigurationReader;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.hamcrest.MatcherAssert;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.concurrent.TimeUnit;
import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class ORDSTestsDay4 {

    @BeforeAll
    public static void setUp(){
        baseURI = ConfigurationReader.getProperty("ords.uri");
    }

    /** Given accept type is JSON
     *  When users sends a GET request to "/employees"
     *  Then status code is 200
     *  And Content type is application/json
     *  And response time is less than 3 seconds  */
    @Test
    @DisplayName("Verify that response time is less then 3 seconds")
    public void test1(){
       given()
                .accept(ContentType.JSON)
        .when()
                .get("/employees")
        .then()
                .assertThat().statusCode(200)
                .contentType(ContentType.JSON)
                .time(lessThan(3L),TimeUnit.SECONDS)
        .and()
               .log().all(true);
    }

    /**
    Given accept type is JSON
    And parameters: q = country_id = US
    When users sends a GET request to "/countries"
    Then status code is 200
    And Content type is application/json
    And country_name from payload is "United States of America"
    {"country_id":"US"}
 */
    @Test
    @DisplayName("Verify that country_name is \"United States of America\"")
    public void test2(){
        given()
                .accept(ContentType.JSON).pathParam("country_id", "US")
         .when()
                .basePath("/countries/{country_id}").get()
         .then()
                .assertThat().statusCode(200)
                .contentType(ContentType.JSON) // verify that service returned JSON content
                .body("country_name", is("United States of America"))
                .log().all(true);
    }

    @Test
    @DisplayName("Verify that country_name is \"United States of America\"")
    public void test3(){
        given()
                .accept(ContentType.JSON)
                .queryParam("q", "{\"country_id\": \"US\"}")
        .when().
                get("countries")
        .then()
                .assertThat().contentType(ContentType.JSON)
                .statusCode(200)
                .body("items[0].country_name", is("United States of America"))
                .log().all(true);
    }

    @Test
    @DisplayName("Verify that href link is printed")
    public void test4(){
        Response response=
                given()
                        .accept(ContentType.JSON)
                        .queryParam("q", "{\"country_id\": \"US\"}")
                .when()
                        .get("countries"); // == new Response;
        JsonPath jsonPath = response.jsonPath();
        /* If I don't put index, I will get collection of properties (only if they exist) */
        List<?> links = jsonPath.getList("items.links.href");
        for (Object link : links ) {
            System.out.println(link);
        }
    }


    /**
     * Verify that ORDS returns only 25 countries.
     * 	Given accept content type as JSON
     * 	When user makes GET request to “/countries”
     * 	Then user verifies that payload contains only 25 countries
     * * get values as list and assert size. */
    @Test
    @DisplayName("Verify that country items is 25")
    public void test5(){
        List<?> list =
                given()
                        .accept(ContentType.JSON).basePath("countries").get().prettyPeek()
                .thenReturn()
                        .jsonPath().getList("items"); // items is key, the rest are objects
                assertEquals(25, list.size());
    }

    /**
     * given path parameter is "/countries" and countries id is 2
     * when user makes get request
     * then assert that status code is 200
     * and verify that body returns following country names
     *  |Argentina                |
     *  |Brazil                   |
     *  |Canada                   |
     *  |Mexico                   |
     *  |United States of America |
     *
     */
    @Test
    @DisplayName("Verify that verify that body returns following country names \"Argentina\", \"Brazil\", \"Canada\", \"Mexico\", \"United States of America\"")
    public void test6(){ //same as Arrays.asList()
        List<String> expected = List.of("Argentina", "Brazil", "Canada", "Mexico", "United States of America");
        Response response =
                given()
                        .accept(ContentType.JSON)
                        .queryParam("q", "{\"region_id\": 2}")
        .when()
                .basePath("/countries").get().prettyPeek();
        List<String> actual = response.jsonPath().getList("items.country_name");
        assertEquals(expected, actual);

        /* With assertThat() */
        given()
            .accept(ContentType.JSON)
            .queryParam("q", "{\"region_id\": 2}")
            .when().basePath("/countries").get()
            .then().assertThat().body("items.country_name", contains("Argentina", "Brazil", "Canada", "Mexico", "United States of America"));
    }

    /**
     * given path parameter is "/employees"
     * when user makes get request
     * then assert that status code is 200
     * Then user verifies that every employee has positive salary
     *
     */
    @Test
    @DisplayName("Verify that employee has a positive salary")
    public void test7(){
        given()
                .accept(ContentType.JSON)
        .when()
                .basePath("/employees").get()
        .then()
                .assertThat().statusCode(200)
                .body("items.salary", everyItem(greaterThan(0)));
                                           /* substitution for forLoop */
    }

    /**
     * given path parameter is "/employees/{id}"
     * and path parameter is 101
     * when user makes get request
     * then assert that status code is 200
     * and verifies that phone number is 515-123-4568
     */
    @Test
    @DisplayName("Verify that phone number is 515-123-4568")
    public void test8() {
        Response response = given()
                        .accept(ContentType.JSON)
                    .when()
                        .get("/employees/{id}", 101);
        assertEquals(200, response.getStatusCode());
        String expected = "515-123-4568";
        String actual = response.jsonPath().get("phone_number");
        expected = expected.replace("-", ".");
        assertEquals(expected, actual);
    }


    @Test
    @DisplayName("Verify that country items is 25")
    public void test8Copy() {
        Response response = given()
                .accept(ContentType.JSON)
                .pathParam("id", 101)
                .when().basePath("/employees/{id}").get();
        assertEquals(200, response.getStatusCode());
        String expected = "515-123-4568";
        String actual = response.jsonPath().get("phone_number");
        expected = expected.replace("-", ".");
        assertEquals(expected, actual);
    }


    /**
     * given path parameter is "/employees"
     * when user makes get request
     * then assert that status code is 200
     * and verify that body returns following salary information after sorting from higher to lower
     *  24000, 17000, 17000, 12008, 11000,
     *  9000, 9000, 8200, 8200, 8000,
     *  7900, 7800, 7700, 6900, 6500,
     *  6000, 5800, 4800, 4800, 4200,
     *  3100, 2900, 2800, 2600, 2500
     *
     */
    @Test
    @DisplayName("Verify that country items is 25")
    public void test9() {
        List<Integer> expectedSalaries = List.of(24000, 17000, 17000, 12008, 11000,
                9000, 9000, 8200, 8200, 8000,
                7900, 7800, 7700, 6900, 6500,
                6000, 5800, 4800, 4800, 4200,
                3100, 2900, 2800, 2600, 2500);
        Response response = given()
                .accept(ContentType.JSON)
        .when()
               .basePath("/employees").get();
        assertEquals(200,response.getStatusCode());

        List<Integer> actualSalary = response.thenReturn().jsonPath().getList("items.salary");
        Collections.sort(actualSalary, Collections.reverseOrder());
        System.out.println("expectedSalary: "+expectedSalaries);
        System.out.println("actualSalary  : "+actualSalary);
        assertEquals(expectedSalaries,actualSalary,"Salaries are not matching");
        assertEquals(expectedSalaries, actualSalary);
    }

    /** ####TASK#####
     *  Given accept type as JSON
     *  And path parameter is id with value 2900
     *  When user sends get request to /locations/{id}
     *  Then user verifies that status code is 200
     *  And user verifies following json path contains following entries:
     *      |street_address         |city  |postal_code|country_id|state_province|
     *      |20 Rue des Corps-Saints|Geneva|1730       |CH        |Geneve        |
     */
    @Test   //entry == key=value
    @DisplayName("Verify that")
    public void test10() {
        given()
                .accept(ContentType.JSON)
                .pathParam("id",2900)
        .when()
                .basePath("locations/{id}").get()
        .then()
                .assertThat()
                            .statusCode(200)
                            .body("", hasEntry("street_address", "20 Rue des Corps-Saints"))
                            .body("", hasEntry("city", "Geneva"))
                            .body("", hasEntry("postal_code", "1730"))
                            .body("", hasEntry("country_id", "CH"))
                            .body("", hasEntry("state_province", "Geneve"))
         .and()
                .log().all(true);
    }

    @Test   //entry == (key=value)
    @DisplayName("Verify that country items is 25")
    public void test10_Copy() {
        given().
               accept(ContentType.JSON).
               pathParam("id", 2900).
        when().
               get("/locations/{id}").
        then().
               assertThat().statusCode(200).
                    body("street_address", is("20 Rue des Corps-Saints")).
                    body("city", is("Geneva")).
                    body("postal_code", is("1730")).
                    body("country_id", is("CH")).
                    body("state_province", is("Geneve")).
                    log().all(true);
    }


}

/*@Test
public void test8(){
    List<Integer> salaries=given().
            accept(ContentType.JSON).when().
            get("/employees").thenReturn().jsonPath().get("items.salary");
    Collections.sort(salaries,Collections.reverseOrder());
    System.out.println(salaries);
    for(int i=0;i<salaries.size()-1;i++){
    assertTrue(salaries.get(i)>=salaries.get(i+1));
}}*/

//List<Integer> actualSalary = response.thenReturn().jsonPath().getList("items.salary", String.class);
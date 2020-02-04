package com.automation.tests.day5;

import org.junit.jupiter.api.BeforeAll;
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
import org.hamcrest.MatcherAssert;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

public class ORDSTestsDay5 {

    @BeforeAll
    public static void setup() {
        baseURI = ConfigurationReader.getProperty("ords.uri");
    }

    /**
     * WARMUP
     * given path parameter is "/employees"
     * when user makes get request
     * then user verifies that status code is 200
     * and user verifies that average salary is grater that 5000
     */
    @Test
    @DisplayName("Verify that average salary is grater than $5000")
    public void test(){
        Response response = given()
                .accept(ContentType.JSON)
                .basePath("/employees")
         .when()
                .get().prettyPeek();
        /*response.then().assertThat().statusCode(200).body("", hamcrest);*/
        assertEquals(200, response.getStatusCode());

        /* JsonPath has json file collection */
        List<Integer> salary = response.jsonPath().getList("items.salary");
        System.out.println(salary);
        int sum = 0;
        for(int sal: salary ){
            sum += sal;
        }
        int average = sum / salary.size();
        assertTrue(average > 5000, "Error: actually average salary is lower then 5000: "+average);
    }


    @Test
    @DisplayName("Verify that average salary is grater than $5000")
    public void testCopy(){
        Response response =
                given()
                        .accept(ContentType.JSON)
                .when()
                        .get("/employees");
        assertTrue(response.getStatusCode() == 200);
        List<Integer> salary = response.jsonPath().getList("items.salary");
        double average = salary.stream().mapToDouble(p -> p).average().getAsDouble();
        assertTrue(average > 5000);
        System.out.println(average);
    }


    /*Response response=
        given()
        .accept(ContentType.JSON)
.when()
        .get("/employees");
assertTrue(response.getStatusCode()==200);
List<Integer>salary=response.jsonPath().getList("items.salary");
double average = salary.stream().mapToDouble(p->p).average().getAsDouble();
assertTrue(average>5000);*/

}

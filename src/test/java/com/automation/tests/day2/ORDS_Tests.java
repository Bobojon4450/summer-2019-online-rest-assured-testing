package com.automation.tests.day2;

import io.restassured.http.Header;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;

public class ORDS_Tests {

    /* Address of ORDS web service, that is running on AWS ec2.
       dara is coming from SQL Oracle data base to this web service
       during back-end testing with SQL developer and JDBC API
       we were accessing data base directly
       now, we gonna access web service  */
    private String baseURI = "http://ec2-3-91-88-86.compute-1.amazonaws.com:1000/ords/hr";

    @Test
    public void test1() {
        Response response = given().get(baseURI + "/employees");
        System.out.println(response.getBody().asString());
        assertEquals(200, response.getStatusCode());
        System.out.println(response.prettyPrint());
    }

    //#TASK: get employee with id 100 and verify that response returns status code 200 also, print body
    @Test
    public void test2() {
        Response response = given().header("accept", "application/json").get(baseURI + "/employees/100");
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);
        System.out.println(response.prettyPrint());

        System.out.println("What kind of content server sends to you? : " + response.getHeader("Content-Type"));
    }

    //    #Task: perform GET request to /regions, print body and all headers.
    @Test
    public void test3() {
        Response response = given().header("accept", "application/json").get(baseURI + "/regions");
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);
        System.out.println(response.getBody().asString());
        System.out.println(response.getHeaders() + "\n");
        for (Header header : response.getHeaders()) {
            System.out.println(header);
        }
    }




}

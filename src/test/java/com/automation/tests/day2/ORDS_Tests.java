package com.automation.tests.day2;

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
    public void test1(){
        Response response = given().get(baseURI+"/employees");
        System.out.println(response.getBody().asString());
        assertEquals(200, response.getStatusCode());
        System.out.println(response.prettyPrint());
    }

    @Test
    public void test2(){

    }



}

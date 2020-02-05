package com.automation.tests.day7;

import com.automation.pojos.Student;
import com.automation.utilities.ConfigurationReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;
import static io.restassured.RestAssured.baseURI;

public class PreschoolTest {

    @BeforeAll
    public static void setup() {
        baseURI = ConfigurationReader.getProperty("school_uri");
    }

    @Test
    @DisplayName("Get student with id 2633 and convert payload into POJO")
    public void test(){
        Response response=
                given()
                    .accept(ContentType.JSON)
                    .pathParam("id", 2633)
                .when()
                    .get("/student/{id}");
        Student student = response.jsonPath().getObject("students[0]", Student.class);//students is an array[]
        System.out.println(student);

        assertEquals(2633, student.getStudentId());
        assertEquals(11, student.getBatch());
        assertEquals("123456", student.getAdmissionNo());
        assertEquals("7925 Jones Branch Dr #3300", student.getContact().getPermanentAddress());
        assertEquals(7925, student.getCompany().getAddress().getZipCode());
    }
}

//$2a$10$4e/8Jj9erHTC18PevBnVEeMsjnq3exG4OrEStxP1EepFY0A0VVb7y

//9f88e495d5eebedb573b7cf9fbde37de84eee34

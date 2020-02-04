package com.automation.tests.day5;
import com.automation.pojos.Job;
import com.automation.pojos.Locations;
import com.automation.utilities.ConfigurationReader;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.baseURI;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.path.json.JsonPath;
import java.util.*;
import static io.restassured.RestAssured.given;

public class POJOTesting {

    @BeforeAll
    public static void setup() {
        baseURI = ConfigurationReader.getProperty("ords.uri");
    }

    @Test   /* Deserialization : JSON to POJO */
    @DisplayName("Get job info from JSON and convert it into POJO")
    public void test(){
        Response response =
                given()
                    .accept(ContentType.JSON)
                .when()
                    .baseUri(baseURI).basePath("/jobs").get();/* returns json format, assigns it to response */
        JsonPath jsonPath = response.jsonPath();
        Job job = jsonPath.getObject("items[0]", Job.class);
        System.out.println(job.getMax_salary());
        System.out.println(job);
    }


    @Test   /* Serialize : POJO to JSON */
    @DisplayName("Convert from POJO to JSON")
    public void test2(){
        Job sdet = new Job("SDET", "Software Engineer in Test",5000, 20000);
        System.out.println("POJO: "+sdet);

        Gson gson = new Gson();
        gson.toJson(sdet);    /*Java(POJO) to Json serialization */
        System.out.println("JSON: "+gson.toJson(sdet));

        String textJson = gson.toJson(sdet);
        System.out.println("textJson: "+textJson);
    }


    @Test
    @DisplayName("Convert Json into Collection of POJO's")
    public void test3(){
        Response response =
                given()
                    .accept(ContentType.JSON)
                .when()
                    .basePath("/jobs").get();
        JsonPath jsonPath = response.jsonPath();
        List<Job> jobs = jsonPath.getList("items", Job.class);
        for (Job job: jobs) {
            System.out.println(job.getJob_title());
        }
    }

    @Test
    @DisplayName("Convert Json into Location POJO, get collection of POJO's")
    public void test4(){
        Locations locations = new Locations();
        Response response =
                given()
                    .accept(ContentType.JSON)
                .when()
                    .basePath("/locations").get();

        JsonPath jsonPath = response.jsonPath();
        List<Locations> loc = jsonPath.getList("items", Locations.class);
        for (Locations each : loc ) {
            System.out.println(each);
        }
    }

    @Test
    @DisplayName("Convert Json into Location POJO, get collection of POJO's")
    public void test5(){
        Response response = given()
                .accept(ContentType.JSON)
        .when()
                .get("/locations/{location_id}", 2500);//returns single line of result
        Locations locations =  response.jsonPath().getObject("", Locations.class);
        System.out.println(locations);

        Response response2 =
                given().
                    accept(ContentType.JSON).
                when().
                    get("/locations"); //returns list of responses
        List<Locations> location = response2.jsonPath().getList("items", Locations.class);
        for(Locations l: location){
            System.out.println(l);
        }
    }

    @Test
    public void test5Copy(){
        given().get("/locations")
                .jsonPath().getList("items", Locations.class)
                .forEach(System.out::println);
    }
}

        /* given().get("/locations")
          .jsonPath().getList("items",Location.class)
          .forEach(System.out::println); */
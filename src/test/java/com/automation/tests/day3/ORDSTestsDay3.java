package com.automation.tests.day3;

import com.automation.utilities.ConfigurationReader;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.BeforeAll;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.concurrent.TimeUnit;
import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.*;

public class ORDSTestsDay3 {

    @BeforeAll
    public static void setUp(){
        baseURI = ConfigurationReader.getProperty("ords.uri");
    }

    @Test
    public void test1(){
        given()
                .accept(ContentType.JSON).basePath("/employees").get()
        .then()
                .assertThat().statusCode(200)
        .and()
                .assertThat().contentType(ContentType.JSON)
        .and()
                .log().all(true); //prints all
                 /*.log().ifError();//prints in case of error*/
    }

    @Test
    public void test2(){
        given()
                .accept(ContentType.JSON).pathParam("id", 100)
        .when()
                .get("/employees/{id}") /* .basePath("/employees/{id}").get()  could be done this way also */
        .then()
                .assertThat().statusCode(200)
        .and()
                .assertThat().body("employee_id", is(100))
        .and()
                .assertThat().body("department_id", is(90))
        .and()
                .assertThat().body("last_name", is("King"))
        .and()
                .log().all(true);
    }

    @Test
    public void test2Copy(){
        given()
                .accept(ContentType.JSON).pathParam("id", 100)
         .when()
                .baseUri(baseURI).basePath("/employees/{id}").get() /* .basePath("/employees/{id}").get()  could be done this way also */
         .then()
                .assertThat().statusCode(200)
         .and()
                .assertThat().body("employee_id", is(100))
         .and()
                .assertThat().body("department_id", is(90))
         .and()
                .assertThat().body("last_name", is("King"))
         .and()
                .log().all(true);
    }

    @Test
    public void test3(){    /* Is another way of asserting by chaining the assertions */
        given()
                .accept(ContentType.JSON).pathParam("id", 100)
        .when()
                .basePath("/employees/{id}").get()
        .then()
                .assertThat().statusCode(200)
        .and()
                .assertThat().body("employee_id", is(100),
                "department_id", is(90),
                "last_name", is("King"))
        .and()
                .log().all();
    }

    @Test
    public void test4(){
        given().
                accept(ContentType.JSON).pathParam("id", 1)
        .when()
                .basePath("/regions/{id}").get()
        .then()
                .assertThat().statusCode(200)
                .assertThat().body("region_id", is(1),
                "region_name", is("Europe"))
                .time(lessThan(2L), TimeUnit.SECONDS)
        .and()
                .log().body(true); /* log body in pretty format. all = header + body + status code*/
                /*.extract().response();
                .and().log().all();*/
    }

    @Test
    public void test4Copy(){ // Another way of implementing above test in separate assert bodies
        given().
                accept(ContentType.JSON).pathParam("id", 1)
        .when()
                .basePath("/regions/{id}").get()
        .then()
                .assertThat().statusCode(200)
         .and()
                .assertThat().body("region_id", is(1))
         .and()
                .assertThat().body("region_name", is("Europe"))
        .and()
                .assertThat().time(lessThan(2L), TimeUnit.SECONDS)
         .and()
                .log().body(true);
                /* log body in pretty format. all = header + body + status code*/
    }


    @Test
    public void test5(){
        JsonPath json =
                given()
                       .accept(ContentType.JSON)
         .when()
                .baseUri(baseURI).basePath("/employees").get().
        thenReturn().jsonPath(); // Json

        /* items[employee1, employee2, employee3] | items[0] = employee1.first_name = Steven */

        String nameOfFirstEmployee = json.getString("items[0].first_name");
        String nameOfLastEmployee = json.getString("items[-1].first_name");
        System.out.println("nameOfFirstEmployee: "+nameOfFirstEmployee);
        System.out.println("nameOfLastEmployee: "+nameOfLastEmployee);

        /* In JSON, employee looks like object that consists of params and
           their values. We can parse that json object and store in the map.  */
        Map<String, ?> firstEmployee = json.get("items[0]");// [] is an Object in JS
        System.out.println(firstEmployee);

        /*  since firstEmployee is a map (key-value pair, we can iterate through it by using Entry.
            entry represents one key=value pair) put '?' as a value (Map<String, ?>), because
            there are values of different data type in the firstEmployee: String, Integer, etc..
            if you put String as value, you might get some casting exception that cannot convert
            from integer(or something else) to String   */
        for(Map.Entry<String, ?> each : firstEmployee.entrySet()){
            System.out.println("Key: "+ each.getKey()+", Value: "+each.getValue());
        }

    /*  Get and print all last names. 'items' is an Object. Whenever you need to read some property
        from the Object, you put 'object.property' but, if response has multiple objects, we can
        get property from every object  */
        List<String> fNames = json.getList("items.first_name");
        for(String str : fNames) {
            System.out.println("fNames: " + str);
        }

        List<Map<String, ?>> jsonList = json.getList("items"); //returns 25 Objects. key=items, values objects
        System.out.println(jsonList.size());
        for (Map<String, ?> each : jsonList) {
            System.out.println(each);
        }
    }

    @Test
    public void test6(){
        JsonPath json =
                given().
                        accept(ContentType.JSON).
                when().
                        get("/countries").
                thenReturn().jsonPath();
        List<Map<String, ?>> jsonList = json.get("items.country_name"); // [Argentina, Australia ....]
                System.out.println(jsonList);
    }

    /* write a code to get info from /countries as List<Map<String, ?>>
       prettyPrint() - print json/xml/html in nice format and returns string, thus we cannot retrieve jsonPath without extraction...
       prettyPeek()  - does same job, but return Response object, and from that object we can get json path.*/
    @Test
    public void test7(){
        JsonPath json =
                given().
                        accept("application/json").
                when()
                      .baseUri(baseURI).basePath("/countries").get()
                .thenReturn().jsonPath();
        List<HashMap<String, ?>> allCountries = json.get("items");

       /*  when we read data from json response, values are not only Strings
           so if we are not sure that all values will have same data type
           we can put '?'   */
        for (HashMap<String,?> each : allCountries) {
            System.out.println(each);
        }
    }

    @Test
    public void test8(){
        List<Integer> salaries =
                given()
                        .accept("application/json")
                 .when()
                        .get("/employees")
                 .thenReturn().jsonPath().get("items.salary");
        System.out.println(salaries);
        Collections.sort(salaries);
        Collections.reverse(salaries);
        System.out.println(salaries);
    }

    @Test
    public void test9(){
        List<String> phoneNum =
                given()
                        .accept("application/json")
                .when()
                        .get("/employees")
                .thenReturn().jsonPath().get("items.phone_number");
        System.out.println(phoneNum);
        /*List<String>phone = new ArrayList<>();
        for(String str : phoneNum){
            phone.add(phoneNum.get(0).replace(".", "-"));
        }*/
        phoneNum.replaceAll(p -> p.replace(".", "-"));
        System.out.println(phoneNum);
    }


    /** ####TASK#####
     *  Given accept type as JSON
     *  And path parameter is id with value 1700
     *  When user sends get request to /locations
     *  Then user verifies that status code is 200
     *  And user verifies following json path information:
     *      |location_id|postal_code|city   |state_province|
     *      |1700       |98199      |Seattle|Washington    |
     *
     */
    @Test
    public void test10(){
        Response response =
                given()
                        .accept(ContentType.JSON)
                        .pathParam("id", 1700)
                .when()
                        .get("/locations/{id}");
        response.
                then().assertThat().body("location_id", is(1700))
                .assertThat().body("postal_code", is("98199"))
                .assertThat().body("city", is("Seattle"))
                .assertThat().body("state_province", is("Washington"))
         .and()
                .log().body(true);
    }

}

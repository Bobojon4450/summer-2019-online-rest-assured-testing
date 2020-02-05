package com.automation.tests.day6;
import com.automation.pojos.Spartan;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeAll;

import com.automation.pojos.Job;
import com.automation.pojos.Locations;
import com.automation.utilities.ConfigurationReader;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.BeforeAll;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;

public class SpartanTests {

    @BeforeAll
    public static void setup() {
        baseURI = ConfigurationReader.getProperty("spartan.uri");
    }

    /**
     * given accept content type as JSON
     * when user sends GET request to /spartans
     * then user verifies that status code is 200
     * and user verifies that content type is JSON
     */

    @Test
    @DisplayName("Verify that /spartans end-point returns 200 and content type as JSON")
    public void test1() {
       /*   web service may return different content type
            and to request JSON, you can just put in the given part  ContentType.JSON
            If you want to ask for XML, you can put ContentType.XML
            but, if web service configured only for JSON
            it will not give you anything else
            GET, PUT, POST, DELETE, etc. - HTTP verbs or methods
            GET - to get the data from web service
            PUT - update existing record
            DELETE - delete something, like delete spartan
            PATCH - partial update of existing record   */
        given().
                accept(ContentType.JSON).
        when()
                .get("/spartans").prettyPeek()
        .then().assertThat().
                    statusCode(200).
                    contentType(ContentType.JSON);
    }


    /**TASK
     * given accept content type as XML
     * when user sends GET request to /spartans
     * then user verifies that status code is 200
     * and user verifies that content type is XML
     */
    @Test
    @DisplayName("Verify that /spartans end-point returns 200 and content type as XML")
    public void test2() {
        //asking
        //accept(ContentType.XML). <- you are asking for payload format as XML from web service
        //receiving
        //contentType(ContentType.XML) <- you are verifying that payload's format is XML
        given().
                accept(ContentType.XML).
        when().
                get("/spartans").prettyPeek().
        then()
                .assertThat().
                    statusCode(200).
                    contentType(ContentType.XML);
    }

    /**TASK
     * given accept content type as JSON
     * when user sends GET request to /spartans
     * then user saves payload into collection
     * We can convert payload (JSON body for example) into collection.
     * if it's a single variable: "name" : "James", we acn store in String or List<String>
     * If, there multiple names in the payload, we cannot use single String as a storage
     * instead, use List<String>
     * If payload returns object:
     *  {
     *  "name": "James"
     *  "age" 25
     *  }
     *  Then, we can store this object (on our, java side, as POJO or Map<String, ?>)
     *  If it's a POJO, we need to create corresponding POJO class, in order to map properties
     *  from json and java object:
     *          Java class         JSON file
     *      private String name |  "name"
     *      private int age     |  "age"
     *
     *      If you want to use different variable name in Java class, use @SerializedName annotation
     *
     *          Java class            JSON file
     *      @SerializedName("name")
     *      private String firstName |  "name"
     *      private int age          |  "age"
     *
     *      otherwise,  Gson, Jackson, or any other Json parser, will not be able to map properties correctly.
     *      Serialization - from POJO (java object) to stream of bytes, let's say to JSON
     *      Deserialization - from stream of bytes, let's say JSON into POJO (java object)
     *
     *      If payload returns array ob objects:
     *
     *
     *          [
     *              {
     *                  "id": 202,
     *                  "name": "Helen Highwater",
     *                  "gender": "Female",
     *                  "phone": 60242012223
     *              },
     *              {
     *                  "id": 203,
     *                  "name": "Ellie Noise",
     *                  "gender": "Female",
     *                  "phone": 37812781233
     *              },
     *          ]
     *
     *          Then we can store this payload as List<Map<?, ?>>>
     *          or like list of POJO's List<Spartan>
     */
    @Test
    @DisplayName("Save payload into java collection")
    public void test3() {
        Response response =
                given().
                    contentType(ContentType.JSON).
                when().
                    get("/spartans");

        /*get(): returns The object matching the Object graph. This may be any primitive type, a List or a Map.*/
        List<Map<String, ?>> collection = response.jsonPath().get();

        for (Map<String, ?> map : collection) {
            System.out.println(map);
        }
    }

    /**
     * TASK
     * given accept content type as JSON
     * when user sends GET request to /spartans
     * then user saves payload into collection of Spartan
     */
    @Test
    @DisplayName("Save payload into java collection of Spartan")
    public void test4() {
        Response response =
                given().
                    contentType(ContentType.JSON).
                when().
                    get("/spartans");
        //whenever you see: Class object = response.jsonPath.getObject() | deserialization
        List<Spartan> collection = response.jsonPath().getList("", Spartan.class);//returns a list of Objects

        for (Spartan spartan : collection) {
            System.out.println(spartan);// this prints info because of overridden method toString();
        }
    }

    /**TASK
     * given accept content type as JSON
     * when user sends POST request to /spartans
     * and user should be able to create new spartan
     * |gender|name           |phone     |
     * | male |Mister Twister |5712134235|
     * then user verifies that status code is 201
     * <p>
     * 201 - means created. Whenever you POST something, you should get back 201 status code
     *       in case of created record
     */
    @Test
    @DisplayName("Create new spartan and verify that status code is 201")
    public void test5() {
/* builder pattern, one of the design patterns in OOP instead of having too many different constructors
   we can use builder pattern and chain with{propertyName} methods to specify properties of an object*/
        Spartan spartan1 = new Spartan().
                            withGender("Male").
                            withName("Some User").
                            withPhone(5712134235L);// this is called as an

        Spartan spartan = new Spartan();
        spartan.setGender("Male");//Male or Female
        spartan.setName("Erhan");
        spartan.setPhone(5712132121L); //at least 10 digits

        Response response =
                given().
                    contentType(ContentType.JSON).
                    body(spartan).//here spartan is serialized to json by body using gson
                when().
                    post("/spartans");
        assertEquals(201, response.getStatusCode(), "Status code is wrong!");
        assertEquals("application/json", response.getContentType(), "Content type is invalid!");
        assertEquals(response.jsonPath().getString("success"), "A Spartan is Born!");

        response.prettyPrint();

        Spartan spartan_from_response = response.jsonPath().getObject("data", Spartan.class);
        System.out.println("Spartan id: " + spartan_from_response.getSpartanId());

        //delete spartan that you've just created
        when()
                .delete("/spartans/{id}", spartan_from_response.getSpartanId()).prettyPeek().
         then()
                .assertThat().statusCode(204);
    }

    @Test
    @DisplayName("Delete user")
    public void test6() {

        int idOfTheUserThatYouWantToDelete = 105;

        Response response = when().delete("/spartans/{id}", idOfTheUserThatYouWantToDelete);

        response.prettyPeek();
    }

    @Test
    @DisplayName("Delete half of the records")
    public void test7() {
        int idOfTheUserThatYouWantToDelete = 106;
        Response response = given().
                accept(ContentType.JSON).
                when().get("/spartans");
        //I collected all user id's
        List<Integer> userIDs = response.jsonPath().getList("id");

        //I sorted user id's in descending order
        Collections.sort(userIDs, Collections.reverseOrder());
        System.out.println("Before: " + userIDs);

        //I went through half of the collection, and deleted half of the users
        //userIDs.size()/2 - represents half of the spartans
        for (int i = 0; i < userIDs.size() / 2; i++) {
            //will delete spartan based on id that you specify
            when().delete("/spartans/{id}", userIDs.get(i));
        }

        /* Response response2 = when().delete("/spartans/{id}", idOfTheUserThatYouWantToDelete);
           response.prettyPeek();*/
    }

    //    "Phone number should be at least 10 digit and UNIQUE!!",
    @Test
    @DisplayName("Add 10 test users to Spartan app")
    public void test8() {

        Faker faker = new Faker();

        for (int i = 0; i < 50; i++) {
            Spartan spartan = new Spartan();

            spartan.setName(faker.name().firstName());
            //remove all non-digits
            // replaceAll() < takes regex (regular expression)
            // regex - it's a pattern, means that 1 character can represent group of chars/symbols/digits
            // \\D - everything that is not a digit (0-9)

            String phone = faker.phoneNumber().subscriberNumber(11).replaceAll("\\D", "");
//           phone.matches("\\d"); check if this string contains only digits
//           phone.matches("[a-x]"); check if this string contains letters in the range from a to x
            //convert from String to Long
            spartan.setPhone(Long.parseLong(phone));
            spartan.setGender("Female");
            System.out.println(spartan);

            Response response = given().
                    contentType(ContentType.JSON).
                    body(spartan).
                    when().post("/spartans").prettyPeek();

            //whenever you successfully add new spartan, you will get this message: "A Spartan is Born!",
            System.out.println(response.jsonPath().getString("success"));
            //verify that response status code is 201,
            //in our case 201 means that post request went well
            assertEquals(201, response.getStatusCode());
        }
    }


    @Test
    @DisplayName("Update spartan")
    public void test9() {
        Spartan spartan = new Spartan().
                withGender("Male").
                withName("Guru of Java").
                withPhone(9999999999L);

        Response response =
                given().
                    accept(ContentType.JSON).
                    contentType(ContentType.JSON).
                    body(spartan).
                    pathParam("id", 380).
                    put("/spartans/{id}").prettyPeek();
        /* Put update existing record also when you make PUT request, you need to specify entire body
           post - create new one
           we never POST/PUT id, it must be auto generated
           if it's not like this - it's a bug   */

        // contentType(ContentType.JSON) in the given()
        // you tell to the web service, what data you are sending
    }

    @Test
    @DisplayName("Update only name with PATCH")
    public void test10(){
        /*  We can use Map instead of POJO    */
        Map<String, String> update = new HashMap<>();
        update.put("name", "Safa");

        Response response = given().
                accept(ContentType.JSON).
                contentType(ContentType.JSON).
                body(update).
                pathParam("id", 1).
                patch("/spartans/{id}");

        response.prettyPrint();

        //POST - add new spartan
        //PUT - update existing one, but you have to specify all properties(fields)
        //PATCH - update existing one, but ypu may specify one or more properties to update
    }

}

package com.automation.tests.day9;

import com.automation.pojos.Room;
import com.automation.utilities.APIUtilities;
import com.automation.utilities.ConfigurationReader;
import com.automation.utilities.ExcelUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import io.restassured.http.ContentType;

import javax.print.DocFlavor;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.get;


public class BookitTest {

    @BeforeAll
    public static void setup() {  baseURI = ConfigurationReader.getProperty("bookit.qa1");  }

    /**
     * Given accept content type as JSON
     * When user sends get requests to /api/rooms
     * Then user should get 401 status code
     */
    @Test
    @DisplayName("Verify that user cannot access BookIt api without providing credentials")
    public void test(){
        given()
                .accept(ContentType.JSON)
        .when()
                .basePath("/api/rooms").get().prettyPeek()
        .then()
                .assertThat().statusCode(401);
        //this service doesn't return 401, it returns 422
        //is it correct or wrong? good time talk to developer and check business requirements
        // Refer to  business docs
    }

    /**
     * Given accept content type as JSON
     * And user provides invalid token
     * When user sends get requests to /api/rooms
     * Then user should get 422 status code
     */
    @Test
    @DisplayName("Verify that system doesn't accept invalid token")
    public void test2(){
        given()
                .accept(ContentType.JSON)
                .auth().oauth2("invalid_token")
        .when()
                .get("/api/rooms")
        .then()
                .assertThat().statusCode(422).log().all(true);//500 server crashed
    }

    /**given valid bearer token
     * when user performs GET request to "/api/rooms"
     * then user should get list of rooms in the payload
     * and status code 200
     */
    @Test
    @DisplayName("")
    public void test3(){
        given()
                .accept(ContentType.JSON)
                .auth().oauth2(APIUtilities.getTokenForBookit())
        .when()
                .get("/api/rooms").prettyPeek();
    }


    @Test
    @DisplayName("Get all rooms and deserialize it into collection of Rooms")
    public void test4(){
        Response response = given().auth().oauth2(APIUtilities.getTokenForBookit())
                .accept(ContentType.JSON)
        .when()
                .get("/api/rooms").prettyPeek();
        List<Room> rooms = response.jsonPath().getList("", Room.class);
        System.out.println("Rooms number: "+rooms.size());
        for (Room each : rooms) {
            System.out.println(each.getName());
        }
    }


    @Test
    @DisplayName("Verify that B12 exist")
    public void test5_1(){
        given()
                .auth().oauth2(APIUtilities.getTokenForBookit())
        .when()
                .get("/api/batches/{batch-number}", 12)
                .then().assertThat().statusCode(200).log().body(true);
    }
    //    campus-location required name of the campus which team will be added to
    //    batch-number required number of the batch which team will be added to
    //    team-name	required name of the team, should be unique per campus



    @Test
    @DisplayName("Create a new team in B12 (negative scenario)")
    public void test6(){
        given().auth().oauth2(APIUtilities.getTokenForBookit())
                .queryParam("campus-location", "VA")
                .queryParam("batch-number", 12)
                .queryParam("team-name", "Hackers")
         .when()
                .post("/api/teams/team")
         .then().log().all(true);
        // Only teacher allowed to modify database. <---- authorization
        // 403 Forbidden - that means you are not authorized to do this
    }

    /**
     * given valid token is provided for teacher
     * and user provides following query parameters
     * |campus-location|batch-number|team-name     |
     * |     VA        |    12      |Online_Hackers|
     * when user performs POST request to "/api/teams/team"
     * then user should verify that status code is 201
     */
    @Test
    @DisplayName("Create a new team in B12 (positive)")
    public void test7() {
        given().auth().oauth2(APIUtilities.getTokenForBookit("teacher"))
                .queryParam("campus-location", "VA")
                .queryParam("batch-number", 12)
                .queryParam("team-name", "DDos_Attackers")
        .when()
                .post("/api/teams/team")
        .then().log().all(true);
        // Only teacher allowed to modify database. <---- authorization
    }



    //test: add yourself to the team
    /**
     *  {
     *             "id": 5443,
     *             "name": "Online_Hackers",
     *             "members": [
     *
     *             ]
     *         },
     */
    /**
     * POST /api/students/student
     * <p>
     * Query Parameters
     * Parameter	    Demand	    Description
     * first-name	    required	first name of the student
     * last-name	    required	last name of the student
     * email	        required	email of the student, will be used for an authentication
     * password	        required	password of the account, will be used for an authentication
     * role      	    required	role of the student, [student-team-leader, student-team-member]
     * campus-location	required	name of the campus which student will be added to
     * batch-number	    required	number of the batch which student will be added to
     * team-name	    required	name of the team which student will be added to
     * <p>
     * given valid token is provided for student team leader
     * and user provides following query parameters
     * |first-name  |last-name    |email         |password    |role               |campus-location|batch-number|team-name      |
     * |    YourName|YourLastName |temp@email.com| anyPassword|student-team-member|      VA       |    12      | Online_Hackers|
     * when user performs POST request to "/api/students/student"
     * then user should verify that status code is 403
     */

    @Test
    @DisplayName("Create a new student(negative testing)")
    public void test8() {           // HTTP/1.1 403 Forbidden
        given()
                .auth().oauth2(APIUtilities.getTokenForBookit())
                .queryParam("first-name", "Adam")
                .queryParam("last-name", "Adamsho")
                .queryParam("email", "otto.ramelo@oolloo.org")
                .queryParam("password", "1212")
                .queryParam("role", "student-team-member")
                .queryParam("campus-location", "VA")
                .queryParam("batch-number", "5")
                .queryParam("team-name", "Javengers")
        .when()
                .post("/api/students/student")
        .then().assertThat().statusCode(403).log().all(true).body("", contains("only teacher allowed to modify database."));
    }


    /**
     * POST /api/students/student
     * <p>
     * Query Parameters
     * Parameter	    Demand	    Description
     * first-name	    required	first name of the student
     * last-name	    required	last name of the student
     * email	        required	email of the student, will be used for an authentication
     * password	        required	password of the account, will be used for an authentication
     * role      	    required	role of the student, [student-team-leader, student-team-member]
     * campus-location	required	name of the campus which student will be added to
     * batch-number	    required	number of the batch which student will be added to
     * team-name	    required	name of the team which student will be added to
     * <p>
     * given valid token is provided for teacher
     * and user provides following query parameters
     * |first-name  |last-name    |email         |password    |role               |campus-location|batch-number|team-name      |
     * |    YourName|YourLastName |temp@email.com| anypassword|student-team-member|      VA       |    12      | Online_Hackers|
     * when user performs POST request to "/api/students/student"
     * then user should verify that status code is 403
     */
    @Test
    @DisplayName("Create a new student (positive test)")
    public void test9() {
        given().
                auth().oauth2(APIUtilities.getTokenForBookit("teacher")).
                queryParam("first-name", "Ali").
                queryParam("last-name", "Ali").
                queryParam("email", "fabir77574@era7mail.com").
                queryParam("password", "3333").
                queryParam("role", "student-team-member").
                queryParam("campus-location", "VA").
                queryParam("batch-number", 12).
                queryParam("team-name", "Online_Hackers").
        when().
                post("/api/students/student").
        then().statusCode(201).log().all(true);
    }
    /* HTTP/1.1 201 Created
       Connection: close
       Date: Fri, 07 Feb 2020 18:09:12 GMT
       Content-Type: application/json
       Server: Jetty(9.4.8.v20171121)
       Via: 1.1 vegur                */

    //let's try to delete some student

    //DELETE /api/students/{id}
    @Test
    @DisplayName("Delete student (negative)")
    public void test10() {
        given().
                auth().oauth2(APIUtilities.getTokenForBookit()).
        when().
                delete("/api/students/{id}", 5443).
        then().assertThat().statusCode(403).log().all(true)
              .assertThat().body("",contains("only teacher allowed to modify database."));
    }

    @Test
    @DisplayName("Delete student (positive)")
    public void test11() {
        given().
                auth().oauth2(APIUtilities.getTokenForBookit("teacher")).
        when().
                delete("/api/students/{id}", 5443).
        then().assertThat().
                statusCode(not(403)).log().all(true)
        .and()
              .assertThat().body("", not(contains("only teacher allowed to modify database.")));
        // statusCode(not(403)) - to ensure that status code is anything but not 403
    }


    //let's find how to delete all users from Online_Hackers team
    @Test
    @DisplayName("Delete all people from Online_Hackers team")
    public void test12() {
        /* I created toke only once, first of all, token doesn't expire that fast
           plus, this token in bookIt never expire, it's a bug(it's not a bug, it's a feature) */

        //if we gonna generate token for every request, it will overload network.
        String token = APIUtilities.getTokenForBookit("teacher");
        Response response =
                given()
                    .auth().oauth2(token)
                .when()
                    .get("/api/teams/{id}", 5443);
        /*response.then().log().all(true);*/

       /* members.id
          "members":  - it's an array of students
          if you need 1st person: members[0]
          if you need last one: members[-1]
          collect all properties: members.property_name                    */
        List<Integer> allIds = response.jsonPath().getList("members.id");
        System.out.println(allIds);

        for (int i = 0; i < allIds.size(); i++) {
            given().auth().oauth2(token).when().delete("/api/students/{id}", allIds.get(i))
            .then().assertThat().statusCode(204).log().ifError();
            /*. log().ifError(); --- will print response info into console, in case of error
                if request was successful and assertion passed you will not see response output
                why? because it's too much print in console */
            System.out.println("Deleted students with id: "+allIds.get(i));
        }
    }

    @Test
    @DisplayName("Get team info")
    public void test13() {
        String token = APIUtilities.getTokenForBookit("teacher");
        Response response =
                given().
                    auth().oauth2(token).
                when().
                    get("/api/teams/{id}", 5443);

        response.then().log().all(true);
    }


    @Test
    @DisplayName("Add new students from excel file")
    public void test14() {
        String filePath = System.getProperty("user.dir") + "/src/test/resources/test_data/bookit_test_data.xlsx";
        ExcelUtil excelUtil = new ExcelUtil(filePath, "students");
         //this returns info about data as a list of Maps
        for (Map<String, String> map : excelUtil.getDataList()) {
            System.out.println(map);
        }

        //get the data from excel file
        List<Map<String, String>> students = excelUtil.getDataList();
        //generate token
        String token = APIUtilities.getTokenForBookit("teacher");
        /*  loop through the collection of students and add them one by one
            we can specify query parameters one by one or put them as a map
            make sure that key names in the map matching query parameters */
        for (Map<String, String> each : students){
            given()
                    .auth().oauth2(token)
                    .queryParams(each)
            .when()
                    .post("/api/students/student")
            .then()
                    .assertThat().statusCode(201).log().ifError();
        }
    }



}

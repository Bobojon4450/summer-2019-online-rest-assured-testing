package com.automation.utilities;

import com.automation.pojos.Spartan;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.*;

public class APIUtilities {

    private static  String URI = ConfigurationReader.getProperty("spartan.uri");
    /**
     * This method can POST new spartan
     *
     * @param spartan POJO
     * @return response object
     */
    public static  Response postSpartan(Spartan spartan) {
        Response response = given().
                contentType(ContentType.JSON).
                    basePath(URI).
                    body(spartan).
                when().
                    post("/spartans");
        return response;
    }

    /**
     * This method can POST new spartan
     *
     * @param filePath to the Spartan external JSON file
     * @return response object
     */
    public static Response postSpartan(String filePath) {
        File file = new File(filePath);
        RestAssured.baseURI = ConfigurationReader.getProperty("spartan.uri");
        Response response = given().
                contentType(ContentType.JSON).
                body(file).
                when().
                post("/spartans");
        return response;
    }

    /**
     * This method can POST new spartan
     *
     * @param spartan as map
     * @return response object
     */
    public static Response postSpartan(Map<String, ?> spartan) {
        RestAssured.baseURI = ConfigurationReader.getProperty("spartan.uri");
        Response response = given().
                contentType(ContentType.JSON).
                body(spartan).
        when().
                post("/spartans");
        return response;
    }

    /**
     * Method that generates access token
     *
     * @return bearer token
     */
    public static String getTokenForBookit() {
        Response response = given().
                queryParam("email", ConfigurationReader.getProperty("team.leader.email")).
                queryParam("password", ConfigurationReader.getProperty("team.leader.password"))
        .when().
                get("/sign").prettyPeek();
        return response.jsonPath().getString("accessToken");
    }


    /**
     * Method that generates access token
     * @param role - type of user. allowed types: student team leader, student team member and teacher
     * @return bearer token
     */
    public static String getTokenForBookit(String role) {
        String userName = "";
        String password = "";
        if (role.toLowerCase().contains("lead")) {
            userName = ConfigurationReader.getProperty("team.leader.email");
            password = ConfigurationReader.getProperty("team.leader.password");
        } else if (role.toLowerCase().contains("teacher")) {
            userName = ConfigurationReader.getProperty("teacher.email");
            password = ConfigurationReader.getProperty("teacher.password");
        } else if (role.toLowerCase().contains("member")) {
            userName = ConfigurationReader.getProperty("team.member.email");
            password = ConfigurationReader.getProperty("team.member.password");
        } else {
            throw new RuntimeException("Invalid user type!");
        }
        Response response = given().
                queryParam("email", userName).
                queryParam("password", password).
         when().
                get("/sign").prettyPeek();
        return response.jsonPath().getString("accessToken");
    }



    /**
     * Method to delete spartan
     * @param id of spartan that you would like to delete
     * @return response object that you can assert later
     */
    public static Response deleteSpartanById(int id) {
        RestAssured.baseURI = ConfigurationReader.getProperty("spartan.uri");
        return RestAssured.when().delete("/spartans/{id}", id);
    }

    /**
     * Delete all spartans
     * @return response
     */
    public static void deleteAllSpartans(){
        Response response = given().
                basePath(baseURI).
                accept(ContentType.JSON).
                when().
                get("/spartans");
        //I collected all user id's
        List<Integer> userIDs = response.jsonPath().getList("id");
        for(int i=0; i< userIDs.size();i++){
            //will delete spartan based on id that you specify
            when().delete("/spartans/{id}", userIDs.get(i)).then().assertThat().statusCode(204);
            System.out.println("Deleted spartan with id: "+userIDs.get(i));
        }
    }


    /**         Java Class Object convert to Physical json file
     *
     *      convert JAVA Class Object into JSON format, then creating new
     *      physical json file at your local desk.
     *
     *      The json file is to be displayed left side of your IDE.
     *
     *      for example;
     *       Student student= new Student();
     *       the student must contains JSON object in first
     *
     * @param object  : Class object , such as Student student==> object is student
     * @param givenNewFileName  : new file name which you will create. such as "student123.json"
     * @throws java.io.IOException
     */
    public static void createJsonFileByJavaObject(Object object, String givenNewFileName) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<String> list = List.of(gson.toJson(object).split(""));
        FileWriter bw = null;
        try{
            bw = new FileWriter(givenNewFileName);
            for (String each : list) {
                bw.write(each);
            }
            bw.flush();
        }catch (IOException E){
        }
    }
}

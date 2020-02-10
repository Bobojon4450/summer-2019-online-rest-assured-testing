package com.automation.tests.hometask;

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
import org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import javax.print.DocFlavor;

import java.util.*;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.get;

public class GitHub_API_Testing {

    @BeforeAll
    public static void setup() {  baseURI = ConfigurationReader.getProperty("github.uri");  }

        /*
    {
    "login": "cucumber",
    "id": 320565,
    "node_id": "MDEyOk9yZ2FuaXphdGlvbjMyMDU2NQ==",
    "url": "https://api.github.com/orgs/cucumber",
    "repos_url": "https://api.github.com/orgs/cucumber/repos",
    "events_url": "https://api.github.com/orgs/cucumber/events",
    "hooks_url": "https://api.github.com/orgs/cucumber/hooks",
    "issues_url": "https://api.github.com/orgs/cucumber/issues",
    "members_url": "https://api.github.com/orgs/cucumber/members{/member}",
    "public_members_url": "https://api.github.com/orgs/cucumber/public_members{/member}",
    "avatar_url": "https://avatars0.githubusercontent.com/u/320565?v=4",
    "description": "Cucumber Open",
    "name": "Cucumber",
    "company": null,
    "blog": "https://cucumber.io/tools/cucumber-open/",
    "location": null,
    "email": "cukes@googlegroups.com",
    "is_verified": false,
    "has_organization_projects": true,
    "has_repository_projects": true,
    "public_repos": 86,
    "public_gists": 0,
    "followers": 0,
    "following": 0,
    "html_url": "https://github.com/cucumber",
    "created_at": "2010-07-01T22:47:35Z",
    "updated_at": "2019-12-18T12:08:31Z",
    "type": "Organization"
}*/
    @Test
    @DisplayName("Verify status code 200, " +
                 "content type application/json; charset=utf-8"+
                 "Verify value of the login field is cucumber" +
                 "Verify value of the name field is cucumber" +
                 "Verify value of the id field is 320565"       )
    public void test(){
        Response response =
        given()
                .queryParam("org", "cucumber")
                .baseUri(baseURI).basePath("/orgs/:org")
        .when()
                .get().prettyPeek();
        response.then()
                .assertThat().statusCode(200)
                .contentType(ContentType.JSON.withCharset("utf-8"));

        Map<String, ?> list = response.jsonPath().get();
        assertEquals(list.get("login"), "cucumber");
        assertEquals(list.get("name"), ("Cucumber"));
        assertEquals(list.get("id"), 320565);
        assertEquals(response.jsonPath().get("login"), "cucumber");
    }

    @Test
    @DisplayName("Verify error message")
    public void test2(){
        given()
                .accept(ContentType.XML)
                .queryParam("org", "cucumber")
                .baseUri(baseURI).basePath("/orgs/:org")
        .when()
                .get().prettyPeek()
        .then()
                .assertThat().statusCode(415)
                .contentType(ContentType.JSON.withCharset("utf-8"))
                .statusLine(containsString("Unsupported Media Type"));
    }

    @Test
    @DisplayName("Number of repositories")
    public void test3() {
       Response response = given()
                .queryParam("org", "cucumber")
                .baseUri(baseURI).basePath("/orgs/:org").get().prettyPeek();

        int extract = response.jsonPath().get("public_repos");
        Response response1 = given()
                .queryParam("org", "cucumber").
                get("/orgs/:org/repos").prettyPeek();
        List<Map<String,?>>maps = response1.jsonPath().get();
//        assertEquals(extract, maps.size());
    }

    @Test
    @DisplayName("Repository id information")
    public void test4() {
        Response response =
         given()
                .queryParam("org", "cucumber")
                .get("/orgs/:org/repos");
        List<Integer>list = response.jsonPath().get("id");
        Set<Integer>set = new HashSet<>(list);
        assertEquals(list.size(), set.size());

        List<String>list2 = response.jsonPath().get("node_id");
        Set<String>set2 = new HashSet<>(list2);
        assertEquals(list2.size(), set2.size());

        /*System.out.println("list2.size(): "+list2.size());
        System.out.println("set2.size(): "+set2.size());
        System.out.println("list.size(): "+list.size());
        System.out.println("set.size(): "+set.size());*/
    }

    @Test
    @DisplayName("Repository owner information")
    public void test5() {
        Response response =
          given()
                .queryParam("org", "cucumber")
          .when()
                .get("/orgs/:org").prettyPeek();
        int id = response.jsonPath().get("id");
        List<Integer>list = given()
                            .queryParam("org", "cucumber")
                     .when()
                            .get("/orgs/:org/repos").thenReturn().jsonPath().get("owner.id");
        for (int each : list) {
            assertEquals(id, each);
        }
    }


    @Test
    @DisplayName("Ascending order by full_name sort")
    public void test7() {
        List<String> rawList = given()
                .queryParam("sort", "full_name")
                .pathParam("org", "cucumber")
                /*.pathParam("org", "cucumber")*/
        .when()
                .get("/orgs/{org}/repos").thenReturn().jsonPath().get("name");
       List<String> expected = rawList;
       Collections.sort(expected);
       assertEquals(expected, rawList);

        for (String each : rawList) {
            System.out.println(each);
        }
        System.out.println(rawList);
        System.out.println(expected);
    }


    @Test
    @DisplayName("Descending order by full_name sort")
    public void test8() {
        List<String > actualNames = given()
                .queryParam("sort", "full_name")
                .queryParam("direction", "desc")
                .pathParam("org", "cucumber")
        .when()
                .get("/orgs/{org}/repos").jsonPath().get("name");
        List<String> expected = new ArrayList<>(actualNames);
        Collections.sort(expected, Collections.reverseOrder());
        assertEquals(expected, actualNames);

        System.out.println(expected);
        System.out.println(actualNames);
    }

    @Test
    @DisplayName("Default sort")
    public void test9() {
        List<String> list =
                given()
                    .pathParam("org", "cucumber")
                .when()
                    .get("/orgs/{org}/repos").jsonPath().get("created_at");
        List<String> expected = new ArrayList<>(list);
        Collections.sort(expected, Collections.reverseOrder());
        assertEquals(expected, list, "Failed since the order is not in descending");
        System.out.println(list);
        System.out.println(expected);
    }



    @Test
    public void test6(){
        Response response1 = given()
                .queryParam("org", "cucumber").
                        get("/orgs/:org/repos").prettyPeek();
    }
}

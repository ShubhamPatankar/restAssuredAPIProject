import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Created by U0159765 on 6/16/2017.
 */
public class getPostCodes {

    public static Response response;

    String longitudeValue;
    String latitudeValue;

    //******** Scenario : To get longitude, latitude values from postcode ********//
    @Test
    public void getPostalCodesDetails() {


        String getPostalCodesUri = "http://api.postcodes.io/postcodes/RM6 4HT";

        Map<String, String> queryParamTestData = new HashMap<>();
        queryParamTestData.put("postcodes", "RM64HT");

        response = given().header("Content-Type", "application/json").
                when().log().all().get(getPostalCodesUri).
                then().log().all().statusCode(200).extract().response();

        longitudeValue = response.body().jsonPath().getString("result.longitude");
        System.out.println("Value of Longitude is : " + longitudeValue);

        latitudeValue = response.body().jsonPath().getString("result.latitude");
        System.out.println("Value of Latitude is : " + latitudeValue);

    }


    //******** Scenario : To validate admin_county array values using the Latitude and Longitude values above ********//
    @Test
    public void validateAdminCounty() {

        List<String> list = new ArrayList();

        String endpoint = "http://api.postcodes.io/outcodes";

        Map<String, String> queryParam = new HashMap<>();
        queryParam.put("lon", longitudeValue);
        queryParam.put("lat", latitudeValue);

        System.out.println(queryParam);

        response = given().header("Content-Type", "application/json").queryParams(queryParam).
                when().log().all().get(endpoint).
                then().log().all().statusCode(200).extract().response();

        System.out.println("Response form Lo and Lat is : " + response.asString());

        System.out.println("Values in array result : " + response.body().path("result"));

        list = response.body().path("result");

        List<String> admin_ward_values = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {

            admin_ward_values = response.body().path("result[" + i + "].admin_ward");

            ///******* Fetching particular values of admin_county from list ********8/

            System.out.println("values in admin ward : " + admin_ward_values.get(1));
        }
    }

    //****** Method to read test data from Json file  *****//

    public static Map<String, Object> jsonToMap ( String path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.readValue(new File(path), new TypeReference<Map<String, Object>>() {
        });
        return map;
    }

    //******** Scenario : To create postal code using given test data in json file ********//
    @Test
     public void createPostCodes() throws IOException{

        String endPoint = "http://api.postcodes.io/postcodes";

        Map<String, Object> testData = jsonToMap("src/test/java/testData.json");
        System.out.println(testData);

        List <String> List1;

        //******** Making a POST request to create Post codes *********//

        response = given().header("Content-Type", "application/json").body(testData).
                    when().post(endPoint).
                    then().log().all().statusCode(200).extract().response();

        System.out.println("post response : " + response.asString());

        List1 = response.body().path("result");
        System.out.println("result has : " + List1);

        System.out.println(response.body().path("result"));

        for(int j=0; j<List1.size(); j++){
            if(response.body().path("result.postcode").toString().equals(testData.get(j)));

            System.out.println(response.body().path("result.latitude"));

        }


        }


}
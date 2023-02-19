package testscripts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class DemoTest {

	@Test
	public void phoneNumbersTypeTest() {
		RestAssured.baseURI = "https://0e686aed-6e36-4047-bcb4-a2417455c2d7.mock.pstmn.io";

		Response res = RestAssured.given().headers("Accept", "application/json").when().get("/test");

		System.out.println(res.asPrettyString());
		List<String> listOfPhoneType = res.jsonPath().getList("phoneNumbers.type");
		// System.out.println(listOfType);
		List<String> expectedListOfPhoneType = new ArrayList<String>();
		expectedListOfPhoneType.add("iPhone");
		expectedListOfPhoneType.add("home");
		Assert.assertTrue(listOfPhoneType.equals(expectedListOfPhoneType));

	}

	@Test
	public void phoneNumbersTest() {
		RestAssured.baseURI = "https://0e686aed-6e36-4047-bcb4-a2417455c2d7.mock.pstmn.io";

		Response res = RestAssured.given().headers("Accept", "application/json").when().get("/test");

		// System.out.println(res.asPrettyString());
		List<Object> listOfPhoneNumber = res.jsonPath().getList("phoneNumbers");
		// System.out.println(listOfPhoneNumber.size());
		// System.out.println(listOfPhoneNumber);

		for (Object obj : listOfPhoneNumber) {
			Map<String, String> mapOfPhoneNumber = (Map<String, String>) obj;
			if (mapOfPhoneNumber.get("type").equals("iPhone"))
				Assert.assertTrue(mapOfPhoneNumber.get("number").startsWith("3456"));
			else if (mapOfPhoneNumber.get("type").equals("home"))
				Assert.assertTrue(mapOfPhoneNumber.get("number").startsWith("0123"));
			System.out.println(mapOfPhoneNumber.get("type") + "=>" + mapOfPhoneNumber.get("number"));
		}

	}

}
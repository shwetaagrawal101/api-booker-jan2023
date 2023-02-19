package testscripts;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import constants.Status_Code;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import pojo.request.createbooking.Bookingdates;
import pojo.request.createbooking.CreateBookingRequest;

public class CreateBookingTest {

	String token;
	int bookingId;
	CreateBookingRequest payload;

	private void validateResponse(Response res, CreateBookingRequest payload, String object) {
		Assert.assertEquals(res.jsonPath().getString(object + "firstname"), payload.getFirstname());
		Assert.assertEquals(res.jsonPath().getString(object + "lastname"), payload.getLastname());
		Assert.assertEquals(res.jsonPath().getInt(object + "totalprice"), payload.getTotalprice());
		Assert.assertEquals(res.jsonPath().getBoolean(object + "depositpaid"), payload.isDepositpaid());
		Assert.assertEquals(res.jsonPath().getString(object + "additionalneeds"), payload.getAdditionalneeds());
		Assert.assertEquals(res.jsonPath().getString(object + "bookingdates.checkin"),
				payload.getBookingdates().getCheckin());
		Assert.assertEquals(res.jsonPath().getString(object + "bookingdates.checkout"),
				payload.getBookingdates().getCheckout());
	}

	@BeforeMethod
	public void generateToken() {
		RestAssured.baseURI = "https://restful-booker.herokuapp.com";
		Response res = RestAssured
				.given()
				//.log()
				//.all()
				.header("content-Type", "application/json")
				.body("{\n" + "    \"username\" : \"admin\",\n" + "    \"password\" : \"password123\"\n" + "}").when()
				.post("/auth");
				//.then()
				//.assertThat()
				//.statusCode(200)
				//.log()
				//.all()
				//.extract()
				//.response();
		// System.out.println(res.statusCode());
		// System.out.println(res.asPrettyString());
		Assert.assertEquals(res.statusCode(), 200);
		token = res.jsonPath().getString("token");
		// System.out.println(token);
	}

	@Test(priority = 1)
	public void createBookingTestWithPojo() {
		Bookingdates bookingdates = new Bookingdates();
		bookingdates.setCheckin("2023-05-02");
		bookingdates.setCheckout("2023-05-02");

		payload = new CreateBookingRequest();
		payload.setFirstname("Shweta");
		payload.setLastname("Agrawal");
		payload.setTotalprice(155);
		payload.setDepositpaid(true);
		payload.setAdditionalneeds("WIFI");
		payload.setBookingdates(bookingdates);

		Response res = RestAssured
				.given()
				.headers("Content-Type", "application/json")
				.headers("Accept", "application/json")
				.body(payload)
				.when()
				//.log()
				//.all()
				.post("/booking");
		Assert.assertEquals(res.statusCode(), Status_Code.OK);
		bookingId = res.jsonPath().getInt("bookingid");
		// String checkIn = res.jsonPath().getString("booking.firstname");
		// System.out.println(bookingId);
		// System.out.println(checkIn);
		// Assert.assertTrue(bookingId > 0);
		validateResponse(res, payload, "booking.");
	}

	@Test(priority = 2)
	public void updateBookingIdTest() {
		//bookingId = 2522;
		payload.setFirstname("Shweta");
		Response res = RestAssured
				.given()
				.headers("Content-Type", "application/json")
				.headers("Accept", "application/json")
				.headers("Cookie", "token=" + token)
				.pathParam("bookingId", bookingId)
				.body(payload)
				.when()
				//.log()
				//.all()
				.put("/booking/{bookingId}");
		Assert.assertEquals(res.getStatusCode(), Status_Code.OK);
		// System.out.println(res.asPrettyString());
		CreateBookingRequest responseBody = res.as(CreateBookingRequest.class);
		Assert.assertTrue(responseBody.equals(payload));
	}
	
	@Test(priority = 3)
	public void partialUpdateBookingIdTest() {
		Response res = RestAssured
				.given()
				.headers("Content-Type", "application/json")
				.headers("Accept", "application/json")
				.headers("Cookie", "token=" + token)
				.pathParam("bookingId", bookingId)
				.body("{\r\n"
						+ "    \"firstname\" : \"Sweety\",\r\n"
						+ "    \"lastname\" : \"Jain\"\r\n"
						+ "}")
				.when()
				//.log()
				//.all()
				.patch("/booking/{bookingId}");
		Assert.assertEquals(res.getStatusCode(), Status_Code.OK);
		//System.out.println(res.asPrettyString());
		Assert.assertEquals(res.jsonPath().getString("firstname"), "Sweety");
		Assert.assertEquals(res.jsonPath().getString("lastname"), "Jain");
	}
	
	@Test(priority = 4)
	public void deleteBookingTest() {
		Response res = RestAssured
				.given()
				.headers("Content-Type", "application/json")
				.headers("Cookie", "token=" + token)
				.pathParam("bookingId", bookingId)
				.when()
				//.log()
				//.all()
				.delete("/booking/{bookingId}");
		Assert.assertEquals(res.getStatusCode(), Status_Code.CREATED);
		//System.out.println(res.asPrettyString());
	}
	
	@Test(priority = 5)
	public void validateBookingIdAfterDeleteBooking() {
		Response res = RestAssured
				.given()
				.headers("Content-Type", "application/json")
				.headers("Cookie", "token=" + token)
				.when()
				//.log()
				//.all()
				.get("/booking");
		Assert.assertEquals(res.getStatusCode(), Status_Code.OK);
		List<Integer> listOfAllBookingIds = res.jsonPath().getList("bookingid");
		//System.out.println(listOfAllBookingIds);
		Assert.assertFalse(listOfAllBookingIds.contains(bookingId));
		//System.out.println(res.asPrettyString());
	}	
	
	@Test(priority = 2, enabled = false)
	public void getBookingIdTest() {
		Response res = RestAssured
				.given()
				.headers("Accept", "application/json")
				.when()
				//.log()
				//.all()
				.get("/booking/" + bookingId);
		Assert.assertEquals(res.getStatusCode(), Status_Code.OK);
		// System.out.println(res.asPrettyString());
		validateResponse(res, payload, "");
	}

	@Test(priority = 3, enabled = false)
	public void getBookingIdDeserializedTest() {
		// bookingId = 2522;
		Response res = RestAssured
				.given()
				.headers("Accept", "application/json")
				.when()
				//.log()
				//.all()
				.get("/booking/" + bookingId);
		Assert.assertEquals(res.getStatusCode(), Status_Code.OK);
		// System.out.println(res.asPrettyString());
		CreateBookingRequest responseBody = res.as(CreateBookingRequest.class);
		Assert.assertTrue(responseBody.equals(payload));
	}

	@Test(enabled = false)
	public void createBookingTest() {
		Response res = RestAssured
				.given()
				//.log()
				//.all()
				.headers("Content-Type", "application/json")
				.headers("Accept", "application/json")
				.body("{\n" + "    \"firstname\" : \"Jim\",\n" + "    \"lastname\" : \"Brown\",\n"
						+ "    \"totalprice\" : 111,\n" + "    \"depositpaid\" : true,\n" + "    \"bookingdates\" : {\n"
						+ "        \"checkin\" : \"2018-01-01\",\n" + "        \"checkout\" : \"2019-01-01\"\n"
						+ "    },\n" + "    \"additionalneeds\" : \"Breakfast\"\n" + "}")
				.when()
				.post("/booking");
		System.out.println(res.statusCode());
		System.out.println(res.statusLine());
		Assert.assertEquals(res.getStatusCode(), Status_Code.OK);

	}

	@Test(enabled = false)
	public void createBookingTestInPlanMode() {
		RestAssured.baseURI = "https://restful-booker.herokuapp.com";
		String payload = "{\n" + "    \"username\" : \"admin\",\n" + "    \"password\" : \"password123\"\n" + "}";
		RequestSpecification reqSpec = RestAssured.given();
		reqSpec.baseUri("https://restful-booker.herokuapp.com/");
		reqSpec.headers("content-Type", "application/json");
		reqSpec.body(payload);
		Response res = reqSpec.post("/auth");
		Assert.assertEquals(res.statusCode(), 200);
		System.out.println(res);
	}

	@Test(enabled = false)
	public void createBookingTestWithPojo1() {
		Bookingdates bookingdates = new Bookingdates();
		bookingdates.setCheckin("2023-05-02");
		bookingdates.setCheckout("2023-05-07");

		payload = new CreateBookingRequest();
		payload.setFirstname("Anand Darsh");
		payload.setLastname("Agrawal");
		payload.setTotalprice(124);
		payload.setDepositpaid(true);
		payload.setAdditionalneeds("breakfast");
		payload.setBookingdates(bookingdates);

		Response res = RestAssured
				.given()
				.headers("Content-Type", "application/json")
				.headers("Accept", "application/json")
				.body(payload)
				//.log()
				//.all()
				.when()
				.post("/booking");
		Assert.assertEquals(res.getStatusCode(), Status_Code.OK);
		int bookingId = res.jsonPath().getInt("bookingid");
		Assert.assertTrue(bookingId > 0);
		Assert.assertTrue(Integer.valueOf(res.jsonPath().getInt("bookingid")) instanceof Integer);
	}

	@Test(priority = 1, enabled = false)
	public void getAllBookingIdsTest() {

		Response res = RestAssured
				.given()
				.headers("Accept", "application/json")
				.when()
				//.log()
				//.all()
				.get("/booking");
		Assert.assertEquals(res.getStatusCode(), Status_Code.OK);
		List<Integer> listOfBookingIds = res.jsonPath().getList("bookingid");
		System.out.println(listOfBookingIds.size());
		Assert.assertTrue(listOfBookingIds.contains(bookingId));
	}

}

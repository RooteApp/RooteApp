package yelp;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import android.util.Log;

import com.roote.entity.Address;
import com.roote.entity.Business;
import com.roote.entity.Deal;

public class YelpAPI {

  private static final String API_HOST = "api.yelp.com";
  private static final String DEFAULT_TERM = "seefu-hair";
  private static final double DEFAULT_LATITUDE = 43.6501295;
  private static final double DEFAULT_LONGITUDE = -79.3833457;
  private static final int DEFAULT_RADIUS = 20000; // 20 Km
  private static final int SEARCH_LIMIT = 20;
  private static final String SEARCH_PATH = "/v2/search";
  private static final String BUSINESS_PATH = "/v2/business";

  /*
   * Update OAuth credentials below from the Yelp Developers API site:
   * http://www.yelp.com/developers/getting_started/api_access
   */
  private static final String CONSUMER_KEY = "tYAC91cOW9mPQxtphTk4Ug";
  private static final String CONSUMER_SECRET = "l9EjpvFPgL6mlh0XA8dAuWC4bX0";
  private static final String TOKEN = "DPn5P3k8Gj9CPaQAyJNIhyXX3GCPGWWO";
  private static final String TOKEN_SECRET = "e_MePvKw5URokUGGdgp8olYS-6k";

  OAuthService service;
  Token accessToken;

  /**
   * Setup the Yelp API OAuth credentials.
   * 
   * @param consumerKey Consumer key
   * @param consumerSecret Consumer secret
   * @param token Token
   * @param tokenSecret Token secret
   */
  public YelpAPI(String consumerKey, String consumerSecret, String token, String tokenSecret) {
    this.service =
        new ServiceBuilder().provider(TwoStepOAuth.class).apiKey(consumerKey)
            .apiSecret(consumerSecret).build();
    this.accessToken = new Token(token, tokenSecret);
  }

  /**
   * Creates and sends a request to the Search API by term and location.
   * <p>
   * See <a href="http://www.yelp.com/developers/documentation/v2/search_api">Yelp Search API V2</a>
   * for more info.
   * 
   * @param term <tt>String</tt> of the search term to be queried
   * @param ll <tt>String</tt> of the location
   * @param r <tt>String</tt> of the search radius
   * @return <tt>String</tt> JSON Response
   */
  public String searchForBusinessesByLocation(String term, String ll, String radiusFilter) {
    OAuthRequest request = createOAuthRequest(SEARCH_PATH);
    request.addQuerystringParameter("term", term);
    request.addQuerystringParameter("ll", ll);
    request.addQuerystringParameter("r", radiusFilter);
    request.addQuerystringParameter("limit", String.valueOf(SEARCH_LIMIT));
    return sendRequestAndGetResponse(request);
  }

  /**
   * Creates and sends a request to the Business API by business ID.
   * <p>
   * See <a href="http://www.yelp.com/developers/documentation/v2/business">Yelp Business API V2</a>
   * for more info.
   * 
   * @param businessID <tt>String</tt> business ID of the requested business
   * @return <tt>String</tt> JSON Response
   */
  public String searchByBusinessId(String businessID) {
    OAuthRequest request = createOAuthRequest(BUSINESS_PATH + "/" + businessID);
    return sendRequestAndGetResponse(request);
  }

  /**
   * Creates and returns an {@link OAuthRequest} based on the API endpoint specified.
   * 
   * @param path API endpoint to be queried
   * @return <tt>OAuthRequest</tt>
   */
  private OAuthRequest createOAuthRequest(String path) {
    OAuthRequest request = new OAuthRequest(Verb.GET, "http://" + API_HOST + path);
    return request;
  }

  /**
   * Sends an {@link OAuthRequest} and returns the {@link Response} body.
   * 
   * @param request {@link OAuthRequest} corresponding to the API request
   * @return <tt>String</tt> body of API response
   */
  private String sendRequestAndGetResponse(OAuthRequest request) {
    System.out.println("Querying " + request.getCompleteUrl() + " ...");
    this.service.signRequest(this.accessToken, request);
    Response response = request.send();
    return response.getBody();
  }

  /**
   * Queries the Search API based on the command line arguments and takes the first result to query
   * the Business API.
   * 
   * @param yelpApi <tt>YelpAPI</tt> service instance
   * @param yelpApiCli <tt>YelpAPICLI</tt> command line arguments
   */
  private static ArrayList<Business> queryAPI(YelpAPI yelpApi, YelpAPICLI yelpApiCli) {
    String searchResponseJSON =
        yelpApi.searchForBusinessesByLocation(yelpApiCli.getTerm(), yelpApiCli.getLl(), yelpApiCli.getRadiusFilter());

    JSONParser parser = new JSONParser();
    JSONObject response = null;
    try {
      response = (JSONObject) parser.parse(searchResponseJSON);
    } catch (ParseException pe) {
      System.out.println("Error: could not parse searchResponseJSON response:");
      System.out.println(searchResponseJSON);
      System.exit(1);
    }

    ArrayList<Business> businesses = new ArrayList<Business>();
    JSONArray businessesJSON = (JSONArray) response.get("businesses");
    
    for(int i = 0; i< businessesJSON.size(); i++){
    	JSONObject businessJSON = (JSONObject) businessesJSON.get(i);
    	
    	if (businessJSON.get("is_closed").toString() == "false"){
    		String businessResponseJSON = yelpApi.searchByBusinessId(businessJSON.get("id").toString());
    		JSONObject responseBusiness = null;
    		try {
    			  responseBusiness = (JSONObject) parser.parse(businessResponseJSON);
    		} catch (ParseException pe) {
    		      System.out.println("Error: could not parse businessResponseJSON response:");
    		      System.out.println(businessResponseJSON);
    		      System.exit(1);
    		}
    		try{
    		// Find address
    		JSONObject businessLocation = (JSONObject) responseBusiness.get("location");
    		@SuppressWarnings("unchecked")
			ArrayList<String> businessNeighborhoods = (ArrayList<String>) businessLocation.get("neighborhoods");
    		@SuppressWarnings("unchecked")
			ArrayList<String> businessAddress = (ArrayList<String>) businessLocation.get("address");
    		Address address = new Address();
    		
	    		address.setStreet(businessAddress.get(0).toString());
	    		address.setNeighbourhood(businessNeighborhoods.get(0).toString());
	    		address.setCity(businessLocation.get("city").toString());
	    		address.setPostalCode(businessLocation.get("postal_code").toString());
	    		address.setState(businessLocation.get("state_code").toString());
	    		address.setCountry(businessLocation.get("country_code").toString());
    		
    		// Find deals
    		ArrayList<Deal> deals = new ArrayList<Deal>();
    		JSONArray businessDeals = (JSONArray) responseBusiness.get("deals");
    		if(businessDeals != null){
	    		for(int j = 0; j < businessDeals.size(); j++){
	    			JSONObject businessDeal = (JSONObject) businessDeals.get(j);
	    			Deal deal = new Deal();
	    			deal.setTitle(businessDeal.get("title").toString());
	    			deal.setLink(businessDeal.get("url").toString());
	    			deal.setCurrency(businessDeal.get("currency_code").toString());
	    			deal.setImage(businessDeal.get("image_url").toString());
	    			deal.setPopular(Boolean.valueOf(businessDeal.get("is_popular").toString()));
	    			deals.add(deal);
	    		}
    		}
    		
    		// Create business object
    		Business business = new Business();
    	
    		business.setName(responseBusiness.get("name").toString());
    		business.setPhone(responseBusiness.get("display_phone").toString());
    		business.setType(yelpApiCli.getTerm());
    		business.setLink(responseBusiness.get("mobile_url").toString());
    		business.setAddress(address);
    		business.setDeals(deals);
    		business.setOverallRating(Double.parseDouble(responseBusiness.get("rating").toString()));
    		business.setNumberOfReviews(Integer.parseInt(responseBusiness.get("review_count").toString()));
    		business.setPhoto(responseBusiness.get("image_url").toString());
    		
    		// Latitude and Longitude coordinate 
    		JSONObject llJSON = (JSONObject) businessLocation.get("coordinate");
    		business.setLongitude(Double.parseDouble(llJSON.get("longitude").toString()));
    		business.setLatitude(Double.parseDouble(llJSON.get("latitude").toString()));
    		business.setDistance(Double.parseDouble(businessJSON.get("distance").toString()));
    		System.out.println(business);
    		businesses.add(business);
    		}
    		catch(Exception e){
    			Log.i("ErrorYelp", e.toString());
    		}
    		
    		
    	}
    }
	return businesses;
  }


  public static ArrayList<Business> getBusinesses(String term, double latitude, double longitude) {
	    YelpAPICLI yelpApiCli = new YelpAPICLI(term, (latitude + "," + longitude), String.valueOf(DEFAULT_RADIUS));

	    YelpAPI yelpApi = new YelpAPI(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);
	    return queryAPI(yelpApi, yelpApiCli);
  }
  

  /**
   * Main entry for sample Yelp API requests.
   * <p>
   * After entering your OAuth credentials, execute <tt><b>run.sh</b></tt> to run this example.
   */
  public static void main() {
    YelpAPICLI yelpApiCli = new YelpAPICLI(DEFAULT_TERM, (DEFAULT_LATITUDE + "," + DEFAULT_LONGITUDE), String.valueOf(DEFAULT_RADIUS));

    YelpAPI yelpApi = new YelpAPI(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);
    queryAPI(yelpApi, yelpApiCli);
  }
}

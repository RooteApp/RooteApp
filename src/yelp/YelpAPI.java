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

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.util.Log;

import com.roote.Utils.LevenshteinCalculator;
import com.roote.entity.Address;
import com.roote.entity.Business;
import com.roote.entity.Deal;
import com.roote.entity.Importer;

public class YelpAPI {

  private static final String API_HOST = "api.yelp.com";
  private static final String DEFAULT_TERM = "starbucks";
  private static final double DEFAULT_LATITUDE = 43.6501295;
  private static final double DEFAULT_LONGITUDE = -79.3833457;
  private static final int DEFAULT_RADIUS = 10000; // 10 Km
  private static final int SEARCH_LIMIT = 10;
  private static final double DEFAULT_FILTER_VALUE = 0.3;
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
    try{
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
	    			// Find address
	    			JSONObject businessLocation = null;
	    			try{
	    				businessLocation = (JSONObject) responseBusiness.get("location");
	    			}catch(Exception e){
	    				Log.i("ErrorYelp", e.toString());
	    			}
	    			Address address = new Address();
				try{
	    				@SuppressWarnings("unchecked")
	    				ArrayList<String> businessNeighborhoods = (ArrayList<String>) businessLocation.get("neighborhoods");
	    				address.setNeighbourhood(businessNeighborhoods.get(0).toString());
				}catch(Exception e){
					Log.i("ErrorYelp", e.toString());
				}
				try{
					@SuppressWarnings("unchecked")
					ArrayList<String> businessAddress = (ArrayList<String>) businessLocation.get("address");
					address.setStreet(businessAddress.get(0).toString());
				}catch(Exception e){
					Log.i("ErrorYelp", e.toString());
				}
				try{
		    			address.setCity(businessLocation.get("city").toString());
				}catch(Exception e){
					Log.i("ErrorYelp", e.toString());
				}
				try{
		    			address.setPostalCode(businessLocation.get("postal_code").toString());
				}catch(Exception e){
					Log.i("ErrorYelp", e.toString());
				}
				try{
					address.setState(businessLocation.get("state_code").toString());
				}catch(Exception e){
					Log.i("ErrorYelp", e.toString());
				}
				try{
					address.setCountry(businessLocation.get("country_code").toString());
				}catch(Exception e){
					Log.i("ErrorYelp", e.toString());
				}
				
	    			// Find deals
	    			ArrayList<Deal> deals = new ArrayList<Deal>();
	    			JSONArray businessDeals = null;
	    			try{
	    				businessDeals = (JSONArray) responseBusiness.get("deals");
	    			}catch(Exception e){
	    				Log.i("ErrorYelp", e.toString());
	    			}
	    			if(businessDeals != null){
	    				for(int j = 0; j < businessDeals.size(); j++){
	    					JSONObject businessDeal = null;
	    					try{
	    						businessDeal = (JSONObject) businessDeals.get(j);
	    					}catch(Exception e){
	    	    					Log.i("ErrorYelp", e.toString());
	    	    				}
	    					Deal deal = new Deal();
	    					try{
	    						deal.setTitle(businessDeal.get("title").toString());
	    					}catch(Exception e){
		    					Log.i("ErrorYelp", e.toString());
		    				}
	    					try{
	    						deal.setLink(businessDeal.get("url").toString());
	    					}catch(Exception e){
		    					Log.i("ErrorYelp", e.toString());
		    				}
	    					try{
	    						deal.setCurrency(businessDeal.get("currency_code").toString());
	    					}catch(Exception e){
		    					Log.i("ErrorYelp", e.toString());
		    				}
	    					try{
	    						deal.setImage(businessDeal.get("image_url").toString());
	    					}catch(Exception e){
		    					Log.i("ErrorYelp", e.toString());
		    				}
	    					try{
	    						deal.setPopular(Boolean.valueOf(businessDeal.get("is_popular").toString()));
	    					}catch(Exception e){
		    					Log.i("ErrorYelp", e.toString());
		    				}
	    					try{
	    						deals.add(deal);
	    					}catch(Exception e){
		    					Log.i("ErrorYelp", e.toString());
		    				}
	    				}
	    			}
	    		// Create business object
	    		Business business = new Business(); 
	    		try{
	    			business.setName(responseBusiness.get("name").toString());
	    		}catch(Exception e){
	    			Log.i("ErrorYelp", e.toString());
			}
	    		try{
	    			business.setPhone(responseBusiness.get("display_phone").toString());
	    		}catch(Exception e){
	    			Log.i("ErrorYelp", e.toString());
			}
	    		try{
	    			business.setType(yelpApiCli.getTerm());
	    		}catch(Exception e){
	    			Log.i("ErrorYelp", e.toString());
			}
	    		try{
	    			business.setLink(responseBusiness.get("mobile_url").toString());
	    		}catch(Exception e){
	    			Log.i("ErrorYelp", e.toString());
			}
	    		try{
	    			business.setAddress(address);
	    		}catch(Exception e){
	    			Log.i("ErrorYelp", e.toString());
			}
	    		try{
	    			business.setDeals(deals);
	    		}catch(Exception e){
	    			Log.i("ErrorYelp", e.toString());
			}
	    		try{
	    			business.setOverallRating(Double.parseDouble(responseBusiness.get("rating").toString()));
	    		}catch(Exception e){
	    			Log.i("ErrorYelp", e.toString());
			}
	    		try{
	    			business.setRatingImage(responseBusiness.get("rating_img_url").toString());
	    		}catch(Exception e){
	    			Log.i("ErrorYelp", e.toString());
			}
	    		try{
	    			business.setNumberOfReviews(Integer.parseInt(responseBusiness.get("review_count").toString()));
	    		}catch(Exception e){
	    			Log.i("ErrorYelp", e.toString());
			}
	    		try{
	    			business.setPhoto(responseBusiness.get("image_url").toString());
	    		}catch(Exception e){
	    			Log.i("ErrorYelp", e.toString());
			}
	    		
	    		// Latitude and Longitude coordinate 
	    		JSONObject llJSON = null;
	    		try{
	    			llJSON = (JSONObject) businessLocation.get("coordinate");
	    		}catch(Exception e){
	    			Log.i("ErrorYelp", e.toString());
			}
	    		try{
	    			business.setLongitude(Double.parseDouble(llJSON.get("longitude").toString()));
	    		}catch(Exception e){
	    			Log.i("ErrorYelp", e.toString());
			}
	    		try{
	    			business.setLatitude(Double.parseDouble(llJSON.get("latitude").toString()));
	    		}catch(Exception e){
	    			Log.i("ErrorYelp", e.toString());
			}
	    		try{
	    			business.setDistance(Double.parseDouble(businessJSON.get("distance").toString()));
	    		}catch(Exception e){
	    			Log.i("ErrorYelp", e.toString());
			}
	    		System.out.println(business);
	    		businesses.add(business);
	    		}
	    	}
	    return businesses;
    }catch(Exception e){
    		Log.i("ErrorYelp", e.toString());
    	}
    return null;
}
	


  @SuppressLint("DefaultLocale")
public static ArrayList<Business> getBusinesses(String term, double latitude, double longitude, AssetManager assetManager) {
	    YelpAPICLI yelpApiCli = new YelpAPICLI(term, (latitude + "," + longitude), String.valueOf(DEFAULT_RADIUS));

	    YelpAPI yelpApi = new YelpAPI(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);
	    ArrayList<Business> businesses = queryAPI(yelpApi, yelpApiCli);
	    ArrayList<Business> validBusinesses = new ArrayList<Business>();

	    if(businesses != null){
		    // Check for local businesses
		    boolean valid = true;
	    		for(int i = 0; i < businesses.size(); i++){
		    		for(Importer importer : Importer.importers){	
		    			if(importer != null && businesses.get(i) != null &&
		    			   importer.getCompanyName() != null && businesses.get(i).getName() != null &&
		    			   importer.getCompanyName().toLowerCase().contains(businesses.get(i).getName().toLowerCase())){
		    				valid = false;
		    				break;
		    			}
		    			//double result = (Double) LevenshteinCalculator.calculateDistance(businesses.get(i).getName(), importer.getCompanyName());
		    			// Remove business with name similarity with importer 
		    			//if(result <= DEFAULT_FILTER_VALUE){
		    			//	Log.i("Yelp-CSV", "Eliminated: " + Double.toString(result) + " ---> " +businesses.get(i).getName() + "---->" + importer.getCompanyName());
		    			//	valid = false;
		    			//	break;
		    			//}
		    		}
		    		if(valid == true){
		    			validBusinesses.add(businesses.get(i));
		    		}
		    		valid = true;
		    }
	    }
	    Log.i("Yelp", "Galvao ACABOOOUUUU!!!");
	    return validBusinesses;
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

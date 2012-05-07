package com.coffeeandpower.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.activity.ActivityEnterInviteCode;
import com.coffeeandpower.activity.ActivityJobCategory;
import com.coffeeandpower.activity.ActivitySettings;
import com.coffeeandpower.activity.ActivityUserDetails;
import com.coffeeandpower.activity.ActivityWallet;
import com.coffeeandpower.cont.ChatMessage;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.Education;
import com.coffeeandpower.cont.Review;
import com.coffeeandpower.cont.Transaction;
import com.coffeeandpower.cont.User;
import com.coffeeandpower.cont.UserResume;
import com.coffeeandpower.cont.UserShort;
import com.coffeeandpower.cont.UserSmart;
import com.coffeeandpower.cont.UserTransaction;
import com.coffeeandpower.cont.Venue;
import com.coffeeandpower.cont.VenueSmart;
import com.coffeeandpower.cont.VenueSmart.CheckinData;
import com.coffeeandpower.cont.Work;
import com.coffeeandpower.inter.OAuthService;
import com.google.android.maps.GeoPoint;

public class HttpUtil {

	private AbstractHttpClient client;


	public HttpUtil(){

		this.client = getThreadSafeClient();
	}


	/**
	 * Get Resume for user woth userId
	 * @param userId
	 * @return
	 */
	public DataHolder getResumeForUserId (int userIdForUrl){

		DataHolder result = new DataHolder(AppCAP.HTTP_ERROR, "Internet connection error", null);

		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost post = new HttpPost(AppCAP.URL_WEB_SERVICE + AppCAP.URL_API);

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		try {
			params.add(new BasicNameValuePair("action", "getResume"));
			params.add(new BasicNameValuePair("user_id", URLEncoder.encode(userIdForUrl + "", "utf-8")));

			post.setEntity(new UrlEncodedFormEntity(params));

			// Execute HTTP Post Request
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();  

			String responseString = EntityUtils.toString(resEntity); 
			RootActivity.log("HttpUtil_getResumeForUserId: " + responseString);

			if (responseString!=null){

				JSONObject json = new JSONObject(responseString);
				if (json!=null){

					JSONObject payload = json.optJSONObject("payload");
					if (payload!=null){

						String nickName = "";
						String statusText = "";
						String urlPhoto = "";
						String urlPhotoLarge = "";
						String joined = "";
						String bio = "";

						// Stats
						int totalEarned = 0;
						int totalTipsEarned = 0;
						int totalMissionCountAsRecipient = 0;
						int distinctTipPayers = 0;
						int totalSpent = 0;
						int totalTipsSpent = 0;
						int totalMissionCountAsPayer = 0;
						int distinctTipRecipients = 0;
						double totalEarnedFromMe = 0.00d;
						int totalMissionsFromMe = 0;
						int totalMissionsAsAgent = 0;
						int totalMissionsAsClient = 0;
						int totalMissions = 0;
						String totalFunded = "";

						String skillSet = "";
						String hourlyBillingRate = "";

						// Verified
						String verifiedLinkedIn = "";
						String linkedInProfileLink = "";
						String verifiedFacebook = "";
						String facebookProfileLink = "";
						String verifiedMobile = "";

						String trusted = "";
						String jobTitle = "";

						// work
						// education

						// Check In Data
						int checkInId = 0;
						int userId = 0;
						double lat = 0;
						double lng = 0;
						String checkInDate = "";
						String checkIn = "";
						String checkOutDate = "";
						String checkOut = "";
						String foursquare = "";
						String foursquareId = "";
						String venueName = "";
						String venueAddress = "";
						String city = "";
						String state = "";
						String zip = "";
						String phone = "";
						String icon = "";
						String visible = "";
						String photoUrlUnUsed = ""; // Unused
						String formattedPhone = "";
						int usersHere = 0;

						// Reviews
						String reviewsPage = "";
						int reviewsTotal = 0;
						String reviewsRecords = "";
						String reviewsLoveReceived = "";

						ArrayList<Review> reviews = new ArrayList<Review>();
						ArrayList<Education> education = new ArrayList<Education>();
						ArrayList<Work> work = new ArrayList<Work>();

						double locationLat = 0;
						double locationLng = 0;

						//************* PARSE JSON ************************************************
						nickName = payload.optString("nickname");
						statusText = payload.optString("status_text");
						urlPhoto = payload.optString("urlPhoto");
						urlPhotoLarge = payload.optString("urlThumbnail");
						joined = payload.optString("joined");
						bio = payload.optString("bio");
						skillSet = payload.optString("skillSet");
						hourlyBillingRate = payload.optString("hourly_biling_rate");
						trusted = payload.optString("trusted");
						jobTitle = payload.optString("job_title");

						JSONObject objLocation = payload.optJSONObject("location");
						if (objLocation!=null){
							locationLat = objLocation.optDouble("lat");
							locationLng = objLocation.optDouble("lng");
						}

						JSONObject objStats = payload.optJSONObject("stats");
						if (objStats!=null){
							totalEarned = objStats.optInt("totalEarned");
							totalTipsEarned = objStats.optInt("totalTipsEarned");
							totalMissionCountAsRecipient = objStats.optInt("totalMissionCountAsRecipient");
							distinctTipPayers = objStats.optInt("distinctTipPayers");
							totalSpent = objStats.optInt("totalSpent");
							totalTipsSpent = objStats.optInt("totalTipsSpent");;
							totalMissionCountAsPayer = objStats.optInt("totalMissionCountAsPayer");
							distinctTipRecipients = objStats.optInt("distinctTipRecipients");
							totalEarnedFromMe = objStats.optDouble("totalEarnedFromMe");
							totalMissionsFromMe = objStats.optInt("totalMissionsFromMe");
							totalMissionsAsAgent = objStats.optInt("totalMissionsAsAgent");
							totalMissionsAsClient = objStats.optInt("totalMissionsAsClient");
							totalMissions = objStats.optInt("totalMissions");
							totalFunded = objStats.optString("totalFunded");
						}

						JSONObject objVerified = payload.optJSONObject("verified");
						if (objVerified!=null){

							JSONObject objLinkedIn = objVerified.optJSONObject("linkedin");
							if (objLinkedIn!=null){
								verifiedLinkedIn = objLinkedIn.optString("verified");
								linkedInProfileLink = objLinkedIn.optString("profileLink");
							}

							JSONObject objFacebook = objVerified.optJSONObject("facebook");
							if (objFacebook!=null){
								verifiedFacebook = objFacebook.optString("verified");
								facebookProfileLink = objFacebook.optString("profileLink");
							}

							JSONObject objMobile = objVerified.optJSONObject("mobile");
							if (objMobile!=null){
								verifiedMobile = objMobile.optString("verified");
							}
						}

						JSONObject objCheckInData = payload.optJSONObject("checkin_data");
						if (objCheckInData!=null){

							checkInId = objCheckInData.optInt("id");
							userId = objCheckInData.optInt("userid");
							lat = objCheckInData.optDouble("lat");
							lng = objCheckInData.optDouble("lng");
							checkInDate = objCheckInData.optString("checkin_date");
							checkIn = objCheckInData.optString("checkin");
							checkOutDate = objCheckInData.optString("checkout_date");
							checkOut = objCheckInData.optString("checkout");
							foursquare = objCheckInData.optString("foursquare");
							foursquareId = objCheckInData.optString("foursquare_id");
							venueName = objCheckInData.optString("name");
							venueAddress = objCheckInData.optString("address");
							city = objCheckInData.optString("city");
							state = objCheckInData.optString("state");
							zip = objCheckInData.optString("zip");
							phone = objCheckInData.optString("phone");
							icon = objCheckInData.optString("icon");
							visible = objCheckInData.optString("visible");
							photoUrlUnUsed = objCheckInData.optString("photo_url");
							formattedPhone = objCheckInData.optString("formatted_phone");
							usersHere = objCheckInData.optInt("users_here");
						}

						// Check in history, I took only first TWO results, but maybe we need more than two....
						JSONArray arrayCheckIn = payload.optJSONArray("checkin_history");
						ArrayList<Venue> checkinhistoryArray = new ArrayList<Venue>();
						if (arrayCheckIn!=null){

							for (int x=0; x<arrayCheckIn.length(); x++){

								if (x<3){

									JSONObject objFromArray = arrayCheckIn.optJSONObject(x);
									if (objFromArray!=null){

										Venue venue = new Venue();
										venue.setCheckinsCount(objFromArray.optInt("count"));
										venue.setId(objFromArray.optString("foursquare_id"));
										venue.setName(objFromArray.optString("name"));
										venue.setAddress(objFromArray.optString("address"));
										venue.setCity(objFromArray.optString("city"));
										venue.setState(objFromArray.optString("state"));
										venue.setPostalCode(objFromArray.optString("zip"));
										venue.setPhone(objFromArray.optString("phone"));
										venue.setPhotoUrl(objFromArray.optString("photo_url"));

										checkinhistoryArray.add(venue);
									}
								}
							}
						}

						JSONObject objReview = payload.optJSONObject("reviews");
						if (objReview!=null){
							reviewsPage = objReview.optString("page");
							reviewsLoveReceived = objReview.optString("love_received");
							reviewsTotal = objReview.optInt("total");
							reviewsRecords = objReview.optString("records");

							JSONArray reviewsArray = objReview.optJSONArray("rows");
							if (reviewsArray!=null){

								for (int x=0; x<reviewsArray.length(); x++){

									JSONObject objReviewFromArray = reviewsArray.optJSONObject(x);
									if (objReviewFromArray!=null){

										reviews.add(new Review(objReviewFromArray.optInt("id"), 
												objReviewFromArray.optString("author"), 
												objReviewFromArray.optString("title"), 
												objReviewFromArray.optString("type"), 
												objReviewFromArray.optString("create_time"), 
												objReviewFromArray.optString("skill"), 
												objReviewFromArray.optString("rating"), 
												objReviewFromArray.optString("is_love"), 
												objReviewFromArray.optString("tip_amount"), 
												objReviewFromArray.optString("review"), 
												objReviewFromArray.optString("ratingImage"), 
												objReviewFromArray.optString("relativeTime")));
									}
								}
							}
						}

						// Get Education data
						JSONArray arrayEdu = payload.optJSONArray("education");
						if (arrayEdu!=null){

							for (int x=0; x<arrayEdu.length(); x++){

								JSONObject objEdu = arrayEdu.optJSONObject(x);
								if (objEdu!=null){
									education.add(new Education(objEdu.optString("school"), 
											objEdu.optInt("startDate"), 
											objEdu.optInt("endDate"), 
											objEdu.optString("concentrations"), 
											objEdu.optString("degree")));
								}
							}
						}

						// Get Work data
						JSONArray arrayWork = payload.optJSONArray("work");
						if (arrayWork!=null){

							for (int x=0; x<arrayWork.length(); x++){

								JSONObject objWork = arrayWork.optJSONObject(x);
								if (objWork!=null){

									// code
								}
							}
						}


						// [0] Object UserResume, [1] array list favourite checkin locatios
						ArrayList<Object> tempHolder = new ArrayList<Object>();
						tempHolder.add(new UserResume(nickName, statusText, urlPhoto, urlPhotoLarge, joined, bio, totalEarned, 
								totalTipsEarned, totalMissionCountAsRecipient, distinctTipPayers, totalSpent, totalTipsSpent, 
								totalMissionCountAsPayer, distinctTipRecipients, totalEarnedFromMe, totalMissionsFromMe, 
								totalMissionsAsAgent, totalMissionsAsClient, totalMissions, totalFunded, skillSet, hourlyBillingRate, 
								verifiedLinkedIn, linkedInProfileLink, verifiedFacebook, facebookProfileLink, verifiedMobile, trusted, 
								jobTitle, checkInId, userId, lat, lng, checkInDate, checkIn, checkOutDate, checkOut, foursquare, 
								foursquareId, venueName, venueAddress, city, state, zip, phone, icon, visible, photoUrlUnUsed, formattedPhone, 
								usersHere, reviewsPage, reviewsTotal, reviewsRecords, reviewsLoveReceived, reviews, education, work, locationLat, locationLng));
						tempHolder.add(checkinhistoryArray);

						result.setResponseCode(AppCAP.HTTP_REQUEST_SUCCEEDED);
						result.setObject(tempHolder);
					}

					return result;
				}
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return result;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return result;

		} catch (IOException e) {
			e.printStackTrace();
			return result;

		} catch (JSONException e) {
			e.printStackTrace();
			return result;
		}
		return result;
	}


	/**
	 * Get user data with userID
	 * @param userId
	 * @return
	 */
	public DataHolder getCheckInDataWithUserId (int userId){

		DataHolder result = new DataHolder(AppCAP.HTTP_ERROR, "Internet connection error", null);

		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost post = new HttpPost(AppCAP.URL_WEB_SERVICE + AppCAP.URL_API);

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		try {
			params.add(new BasicNameValuePair("action", "getUserCheckInData"));
			params.add(new BasicNameValuePair("user_id", URLEncoder.encode(userId + "", "utf-8")));

			post.setEntity(new UrlEncodedFormEntity(params));

			// Execute HTTP Post Request
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();  

			String responseString = EntityUtils.toString(resEntity); 
			RootActivity.log("HttpUtil_getCheckInDataWithUserId: " + responseString);

			if (responseString!=null){

				JSONObject json = new JSONObject(responseString);
				if (json!=null){

					result.setResponseCode(AppCAP.HTTP_REQUEST_SUCCEEDED);
					return result;
				}
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return result;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return result;

		} catch (IOException e) {
			e.printStackTrace();
			return result;

		} catch (JSONException e) {
			e.printStackTrace();
			return result;
		}
		return result;
	}


	/**
	 * Get Bitmap (profile photos) from URL
	 * @param url
	 * @return
	 */
	public static DataHolder getBitmapFromURL (String url){

		DataHolder result = new DataHolder(AppCAP.HTTP_ERROR, "Internet connection error", null);

		URL myFileUrl =null;   
		Bitmap bmImg = null;

		try {
			myFileUrl= new URL(url);
		} catch (MalformedURLException e) {
			RootActivity.log("HttpUtil_getBitmapFromURL url:" + url);
			e.printStackTrace();
		}

		try {
			HttpURLConnection conn= (HttpURLConnection)myFileUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();

			bmImg = BitmapFactory.decodeStream(is);

			result.setResponseCode(AppCAP.HTTP_REQUEST_SUCCEEDED);
			result.setObject(bmImg);

		} catch (IOException e) {
			e.printStackTrace();
			return result;
		}

		return result;
	}




	/**
	 * Upload user profile image
	 * @return
	 */
	public DataHolder uploadUserProfilePhoto (){

		DataHolder result = new DataHolder(AppCAP.HTTP_ERROR, "Internet connection error", null);

		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost post = new HttpPost(AppCAP.URL_WEB_SERVICE + AppCAP.URL_API);

		MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);  

		File file = new File(Environment.getExternalStorageDirectory() + ActivitySettings.IMAGE_FOLDER, "photo_profile.jpg");

		try {
			multipartEntity.addPart("action", new StringBody("setUserProfileData"));
			multipartEntity.addPart("profile", new FileBody(file));

			post.setEntity(multipartEntity);

			// Execute HTTP Post Request
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();  

			String responseString = EntityUtils.toString(resEntity); 
			RootActivity.log("HttpUtil_uploadUserProfilePhoto: " +responseString);

			if (responseString!=null){

				JSONObject json = new JSONObject(responseString);
				if (json!=null){
					result.setResponseCode(AppCAP.HTTP_REQUEST_SUCCEEDED);
					result.setObject(json.opt("message"));

					JSONObject params = json.optJSONObject("params");
					if (params!=null){
						AppCAP.setLocalUserPhotoURL(params.optString("thumbnail"));
						AppCAP.setLocalUserPhotoLargeURL(params.optString("picture"));
					}

				}
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return result;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return result;

		} catch (IOException e) {
			e.printStackTrace();
			return result;

		} catch (JSONException e) {
			e.printStackTrace();
			return result;
		}
		return result;
	}


	/**
	 * Send One on One chat message
	 * @param userId
	 * @param message
	 * @return
	 */
	public DataHolder sendOneOnOneChatMessage (int userId, String message){

		DataHolder result = new DataHolder(AppCAP.HTTP_ERROR, "Internet connection error", null);

		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		HttpPost post = new HttpPost(AppCAP.URL_WEB_SERVICE + AppCAP.URL_API);

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		try {
			params.add(new BasicNameValuePair("action", "oneOnOneChatFromMobile"));
			params.add(new BasicNameValuePair("message", URLEncoder.encode(message, "utf-8")));
			params.add(new BasicNameValuePair("toUserId", URLEncoder.encode(userId+"", "utf-8")));

			post.setEntity(new UrlEncodedFormEntity(params));

			// Execute HTTP Post Request
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();  

			String responseString = EntityUtils.toString(resEntity); 
			RootActivity.log("HttpUtil_sendOneOnOneChatMessage: " + responseString);

			if (responseString!=null){

				if (responseString.equals("0")){
					result.setResponseCode(AppCAP.HTTP_REQUEST_SUCCEEDED);
				}
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return result;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return result;

		} catch (IOException e) {
			e.printStackTrace();
			return result;
		}
		return result;
	}


	/**
	 * Send love review
	 * @param user
	 * @param review
	 * @return
	 */
	public DataHolder sendReview (UserResume user, String review){

		DataHolder result = new DataHolder(AppCAP.HTTP_ERROR, "Internet connection error", null);

		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost post = new HttpPost(AppCAP.URL_WEB_SERVICE + "reviews.php");

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		try {
			params.add(new BasicNameValuePair("action", "makeMobileReview"));
			params.add(new BasicNameValuePair("recipientID", URLEncoder.encode(user.getUserId()+"", "utf-8")));
			params.add(new BasicNameValuePair("reviewText", URLEncoder.encode(review, "utf-8")));
			if (user.getLat()!=0 && user.getLng()!=0){
				params.add(new BasicNameValuePair("lat", URLEncoder.encode(user.getLat()+"", "utf-8")));
				params.add(new BasicNameValuePair("lng", URLEncoder.encode(user.getLng()+"", "utf-8")));
			}
			if (ActivityUserDetails.isUserHereNow(user)){
				params.add(new BasicNameValuePair("foursquare", URLEncoder.encode(user.getFoursquareId()+"", "utf-8")));
			}

			post.setEntity(new UrlEncodedFormEntity(params));

			// Execute HTTP Post Request
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();  

			String responseString = EntityUtils.toString(resEntity); 
			RootActivity.log("HttpUtil_sendReview: " +responseString);

			if (responseString!=null){

				JSONObject json = new JSONObject(responseString);
				if (json!=null){

					boolean res = json.optBoolean("succeeded");
					result.setObject(res);

					String message = json.optString("message");
					result.setResponseMessage(message);
					result.setResponseCode(AppCAP.HTTP_REQUEST_SUCCEEDED);
					return result;
				}
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return result;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return result;

		} catch (IOException e) {
			e.printStackTrace();
			return result;

		} catch (JSONException e) {
			e.printStackTrace();
			return result;
		}
		return result;
	}


	/**
	 * Get chat history with user
	 * @param userId
	 * @return
	 */
	public DataHolder getOneOnOneChatHistory (int userId){

		DataHolder result = new DataHolder(AppCAP.HTTP_ERROR, "Internet connection error", null);

		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost post = new HttpPost(AppCAP.URL_WEB_SERVICE + AppCAP.URL_API);

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		try {
			params.add(new BasicNameValuePair("action", "getOneOnOneChatHistory"));
			params.add(new BasicNameValuePair("other_user", URLEncoder.encode(userId+"", "utf-8")));

			post.setEntity(new UrlEncodedFormEntity(params));

			// Execute HTTP Post Request
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();  

			String responseString = EntityUtils.toString(resEntity); 
			RootActivity.log("HttpUtil_getOneOnOneChatHistory: " +responseString);

			if (responseString!=null){

				JSONObject json = new JSONObject(responseString);
				if (json!=null){

					if (json.optInt("error")==0){

						ArrayList<ChatMessage> messages = new ArrayList<ChatMessage>();
						JSONArray arrayChat = json.optJSONArray("chat");
						if (arrayChat!=null){

							for (int x=0; x<arrayChat.length(); x++){

								JSONObject objMess = arrayChat.optJSONObject(x);
								if (objMess!=null){
									messages.add(new ChatMessage(objMess.optInt("id"), 
											objMess.optInt("user_id"), 
											objMess.optString("entry_text"), 
											objMess.optString("nickname"), 
											objMess.optString("date"), 
											objMess.optString("photo_url"), 
											objMess.optInt("receiving_user_id"), 
											objMess.optInt("offer_id")));	
								}
							}
						}
						result.setObject(messages);
						result.setResponseCode(AppCAP.HTTP_REQUEST_SUCCEEDED);
						return result;
					} else {
						result.setResponseCode(AppCAP.HTTP_ERROR);
						result.setResponseMessage("Error loading chat...");
					}
				}
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return result;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return result;

		} catch (IOException e) {
			e.printStackTrace();
			return result;

		} catch (JSONException e) {
			e.printStackTrace();
			return result;
		}
		return result;
	}


	/**
	 * Change user profile data
	 * @param user
	 * @return
	 */
	public DataHolder setUserProfileData (User user, boolean isEmailChanged){

		DataHolder result = new DataHolder(AppCAP.HTTP_ERROR, "Internet connection error", null);

		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost post = new HttpPost(AppCAP.URL_WEB_SERVICE + AppCAP.URL_API);

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		try {
			params.add(new BasicNameValuePair("action", "setUserProfileData"));
			params.add(new BasicNameValuePair("nickname", URLEncoder.encode(user.getNickName()+"", "utf-8")));
			if (isEmailChanged){
				params.add(new BasicNameValuePair("email", URLEncoder.encode(user.getUserName()+"", "utf-8")));
			}
			post.setEntity(new UrlEncodedFormEntity(params));

			// Execute HTTP Post Request
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();  

			String responseString = EntityUtils.toString(resEntity); 
			RootActivity.log("HttpUtil_setUserProfileData: " +responseString);

			if (responseString!=null){

				JSONObject json = new JSONObject(responseString);
				if (json!=null){

					boolean res = json.optBoolean("succeeded");
					result.setObject(res);

					String message = json.optString("message");
					result.setResponseMessage(message);
					result.setResponseCode(AppCAP.HTTP_REQUEST_SUCCEEDED);
					return result;
				}
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return result;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return result;

		} catch (IOException e) {
			e.printStackTrace();
			return result;

		} catch (JSONException e) {
			e.printStackTrace();
			return result;
		}
		return result;
	}
	
	
	/**
	 * Set Notification Settings
	 * @param distance
	 * @param checkedInOnly
	 * @return
	 */
	public DataHolder setNotificationSettings (String distance, boolean checkedInOnly){
		DataHolder result = new DataHolder(AppCAP.HTTP_ERROR, "Internet connection error", null);

		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost post = new HttpPost(AppCAP.URL_WEB_SERVICE + AppCAP.URL_API);

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		try {
			params.add(new BasicNameValuePair("action", "setNotificationSettings"));
			params.add(new BasicNameValuePair("push_distance", URLEncoder.encode(distance+"", "utf-8")));
			params.add(new BasicNameValuePair("checked_in_only", checkedInOnly ? "1" : "0"));
			
			post.setEntity(new UrlEncodedFormEntity(params));

			// Execute HTTP Post Request
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();  

			String responseString = EntityUtils.toString(resEntity); 
			RootActivity.log("HttpUtil_setNotificationSettings: " +responseString);

			if (responseString!=null){

			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return result;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return result;

		} catch (IOException e) {
			e.printStackTrace();
			return result;
		}
		return result;
	}



	/**
	 * Get users checked in around me
	 * @param venue
	 * @return
	 */
	/*
	public DataHolder getCheckedInBoundsOverTime (MapView mapView){

		DataHolder result = new DataHolder(AppCAP.HTTP_ERROR, "Internet connection error", null);

		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		GeoPoint pointCenterMap = mapView.getMapCenter();
		int lngSpan = mapView.getLongitudeSpan();
		int latSpan = mapView.getLatitudeSpan();

		GeoPoint sw = new GeoPoint(pointCenterMap.getLatitudeE6() - latSpan/2, pointCenterMap.getLongitudeE6() - lngSpan/2);
		GeoPoint ne = new GeoPoint(pointCenterMap.getLatitudeE6() + latSpan/2, pointCenterMap.getLongitudeE6() + lngSpan/2);

		float numberOfDays = 7.0f;

		HttpGet get = new HttpGet(AppCAP.URL_WEB_SERVICE + AppCAP.URL_API + 
				"?action=getCheckedInBoundsOverTime" + 
				"&sw_lat=" + (sw.getLatitudeE6() / 1E6) + 
				"&sw_lng=" + (sw.getLongitudeE6() / 1E6) + 
				"&ne_lat=" + (ne.getLatitudeE6() / 1E6) + 
				"&ne_lng=" + (ne.getLongitudeE6() / 1E6) + 
				"&checked_in_since=" + (System.currentTimeMillis() /1000 - (86400 * numberOfDays)) + 
				"&group_users=1" + 
				"&version=0.1");

		try {

			// Execute HTTP Get Request
			HttpResponse response = client.execute(get);
			HttpEntity resEntity = response.getEntity();  

			String responseString = EntityUtils.toString(resEntity); 
			RootActivity.log("HttpUtil_getCheckedInBoundsOverTime: " +responseString);

			if (responseString!=null){

				JSONObject json = new JSONObject(responseString);
				if (json!=null){

					boolean res = json.optBoolean("error");
					if (!res){

						JSONArray payload = json.optJSONArray("payload");
						if (payload!=null){

							ArrayList<UserSmart> mapUsersArray = new ArrayList<UserSmart>();

							for (int m=0; m<payload.length(); m++){

								JSONObject item = payload.optJSONObject(m);
								if (item!=null){

									int checkInId = item.optInt("checkin_id");
									int userId= item.optInt("id");
									String nickName = item.optString("nickname");
									String statusText = item.optString("status_text");
									String photo = item.optString("photo");  // ???
									String majorJobCategory = item.optString("major_job_category");
									String minorJobCategory = item.optString("minor_job_category");
									String headLine = item.optString("headline");
									String fileName = item.optString("filename");
									double lat = item.optDouble("lat");
									double lng = item.optDouble("lng");
									int checkedIn = item.optInt("checked_in");
									String foursquareId = item.optString("foursquare");
									String venueName = item.optString("venue_name");
									int checkInCount = item.optInt("checkin_count");
									String skills = item.optString("skills");
									boolean met = item.optBoolean("met");

									mapUsersArray.add(new UserSmart(checkInId, userId, nickName, statusText, photo, majorJobCategory, minorJobCategory, 
											headLine, fileName, lat, lng, checkedIn, foursquareId, venueName, checkInCount, skills, met));
								}
							}

							result.setResponseCode(AppCAP.HTTP_REQUEST_SUCCEEDED);
							result.setObject(mapUsersArray);
						}

					} else {
						// we have unknown error
						result.setResponseMessage("Unknown error");
					}

					return result;
				}
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return result;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return result;

		} catch (IOException e) {
			e.printStackTrace();
			return result;

		} catch (JSONException e) {
			e.printStackTrace();
			return result;
		}
		return result;
	}
	*/

	
	/**
	 * Get Venues And Users With Checkins In Bounds During Interval
	 * @param data
	 * @return Object[]   [0]venues   [1]users
	 */
	public DataHolder getVenuesAndUsersWithCheckinsInBoundsDuringInterval (double data[], int numberOfDays){

		DataHolder result = new DataHolder(AppCAP.HTTP_ERROR, "Internet connection error", null);
		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		String userLat = "";
		String userLng = "";

		if (data[4]!=0 && data[5]!=0){
			userLat = "&user_lat=" + data[4];
			userLng = "&user_lng=" + data[5];
		}

		HttpGet get = new HttpGet(AppCAP.URL_WEB_SERVICE + AppCAP.URL_API + 
				"?action=getVenuesAndUsersWithCheckinsInBoundsDuringInterval" + 
				"&sw_lat=" + data[0] + 
				"&sw_lng=" + data[1] + 
				"&ne_lat=" + data[2] + 
				"&ne_lng=" + data[3] + 
				"&checked_in_since=" + (System.currentTimeMillis() /1000 - (86400 * numberOfDays)) + 
				userLat + 
				userLng +
				"&version=0.1");
		//Log.d("LOG", "time interval: " + (System.currentTimeMillis() /1000 - (86400 * numberOfDays)));
		try {

			// Execute HTTP Get Request
			HttpResponse response = client.execute(get);
			HttpEntity resEntity = response.getEntity();  

			String responseString = EntityUtils.toString(resEntity); 
			RootActivity.log("HttpUtil_getVenuesAndUsersWithCheckinsInBoundsDuringInterval: " + responseString);

			if (responseString!=null){

				JSONObject json = new JSONObject(responseString);
				if (json!=null){

					JSONObject objPayload = json.optJSONObject("payload");
					if (objPayload!=null){

						// Array Venues
						ArrayList<VenueSmart> venues = new ArrayList<VenueSmart>();
						JSONArray arrayVenues = objPayload.optJSONArray("venues");
						if (arrayVenues!=null){
							for (int x=0; x<arrayVenues.length(); x++){

								JSONObject objVenue = arrayVenues.optJSONObject(x);
								if (objVenue!=null){

									ArrayList<CheckinData> arrayCheckins = new ArrayList<VenueSmart.CheckinData>();
									JSONObject objUsersFromVenue = objVenue.optJSONObject("users");
									if (objUsersFromVenue!=null){

										JSONArray userIds = objUsersFromVenue.names();
										if (userIds!=null){

											for (int y=0; y<userIds.length(); y++){

												JSONObject o = objUsersFromVenue.optJSONObject(userIds.getString(y));
												if (o!=null){
													int userId = 0;
													try{
														userId = Integer.parseInt(userIds.getString(y));
													} catch(NumberFormatException e){}

													arrayCheckins.add(new CheckinData(userId, 
															o.optInt("checkin_count"), 
															o.optInt("checked_in")));
												}
											}
										}
									}

									venues.add(new VenueSmart(objVenue.optString("name"), 
											objVenue.optString("address"), 
											objVenue.optString("city"), 
											objVenue.optString("state"), 
											objVenue.optString("distance"), 
											objVenue.optString("foursquare"), 
											objVenue.optInt("checkins"), 
											objVenue.optInt("checkins_for_week"),
											objVenue.optInt("checkins_for_interval"),
											objVenue.optString("photo_url"),
											objVenue.optString("phone"), 
											objVenue.optString("formatted_phone"), 
											objVenue.optDouble("lat"), 
											objVenue.optDouble("lng"), 
											arrayCheckins));
								}
							}
						}

						// Array Users
						ArrayList<UserSmart> users = new ArrayList<UserSmart>();
						JSONArray arrayUsers = objPayload.optJSONArray("users");
						if (arrayUsers!=null){

							boolean isFirstInList1 = false;
							boolean isFirstInList0 = false;

							for (int x=0; x<arrayUsers.length(); x++){
								JSONObject objUser = arrayUsers.optJSONObject(x);
								if (objUser!=null){

									UserSmart singleUserMap =  new UserSmart(
											objUser.optInt("checkin_id"), 
											objUser.optInt("id"),
											objUser.optString("nickname"),
											objUser.optString("status_text"), 
											objUser.optString("photo"),
											objUser.optString("major_job_category"),
											objUser.optString("minor_job_category"), 
											// smarterer_name !?!?!?!??!
											objUser.optString("headline"), 
											objUser.optString("filename"), 
											objUser.optDouble("lat"),
											objUser.optDouble("lng"), 
											objUser.optInt("checked_in"), 
											objUser.optString("foursquare"), 
											objUser.optString("venue_name"),
											objUser.optInt("checkin_count"), 
											objUser.optString("skills"),
											objUser.optBoolean("met"));

									// if herenow exist, than that value will be used
									if (singleUserMap.getCheckedIn()==1){
										if (!isFirstInList1){
											singleUserMap.setFirstInList(true);
											isFirstInList1 = !isFirstInList1;
										} 
									} else {
										if (!isFirstInList0){
											singleUserMap.setFirstInList(true);
											isFirstInList0 = !isFirstInList0;
										}
									}

									users.add(singleUserMap);
								}
							}
						}

						result.setObject(new Object[]{venues, users});
					}
					result.setResponseCode(AppCAP.HTTP_REQUEST_SUCCEEDED);
					return result;
				}
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return result;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return result;

		} catch (IOException e) {
			e.printStackTrace();
			return result;

		} catch (JSONException e) {
			e.printStackTrace();
			return result;
		}
		return result;
	}


	/**
	 * Get users checked in venue
	 * @param venue
	 * @return
	 */
	public DataHolder getUsersCheckedInAtFoursquareID (String foursquareId){

		DataHolder result = new DataHolder(AppCAP.HTTP_ERROR, "Internet connection error", null);

		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost post = new HttpPost(AppCAP.URL_WEB_SERVICE + AppCAP.URL_API);

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		try {
			params.add(new BasicNameValuePair("action", "getUsersCheckedIn"));
			params.add(new BasicNameValuePair("foursquare", URLEncoder.encode(foursquareId+"", "utf-8")));

			post.setEntity(new UrlEncodedFormEntity(params));

			// Execute HTTP Post Request
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();  

			String responseString = EntityUtils.toString(resEntity); 
			RootActivity.log("HttpUtil_getUsersCheckedIn: " +responseString);

			if (responseString!=null){

				JSONObject json = new JSONObject(responseString);
				if (json!=null){

					ArrayList<UserShort> usersArray = new ArrayList<UserShort>();

					JSONObject objPayload = json.optJSONObject("payload");
					if (objPayload!=null){

						int count = objPayload.optInt("count");  // I will use arratUsers.size() instead of count

						JSONArray arrayUsers = objPayload.optJSONArray("users");
						if (arrayUsers!=null){

							for (int i=0; i<arrayUsers.length(); i++){

								JSONObject obj = arrayUsers.optJSONObject(i);
								if (obj!=null){

									int id = obj.optInt("id");
									String nickName = obj.optString("nickname");
									String statusText = obj.optString("status_text");
									String about = obj.optString("about");
									String joinDate = obj.optString("join_date");
									String imageURL = obj.optString("imageUrl");
									String hourlyBilingRate = obj.optString("hourly_biling_rate");

									usersArray.add(new UserShort(id, nickName, statusText, about, joinDate, imageURL, hourlyBilingRate));
								}
							}
						}
					}
					result.setResponseCode(AppCAP.HTTP_REQUEST_SUCCEEDED);
					result.setObject(usersArray);
					return result;
				}
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return result;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return result;

		} catch (IOException e) {
			e.printStackTrace();
			return result;

		} catch (JSONException e) {
			e.printStackTrace();
			return result;
		}
		return result;
	}


	/**
	 * Send contact request to userId
	 * @param userId
	 * @return
	 */
	public DataHolder sendContactRequestToUserId (int userId){

		DataHolder result = new DataHolder(AppCAP.HTTP_ERROR, "Internet connection error", null);

		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost post = new HttpPost(AppCAP.URL_WEB_SERVICE + AppCAP.URL_API);

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		try {
			params.add(new BasicNameValuePair("action", "sendContactRequest"));
			params.add(new BasicNameValuePair("acceptor_id", URLEncoder.encode(userId + "", "utf-8")));

			post.setEntity(new UrlEncodedFormEntity(params));

			// Execute HTTP Post Request
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();  

			String responseString = EntityUtils.toString(resEntity); 
			RootActivity.log("HttpUtil_sendContactRequestToUserId: " +responseString);

			if (responseString!=null){

				JSONObject json = new JSONObject(responseString);
				if (json!=null){

					result.setResponseCode(AppCAP.HTTP_REQUEST_SUCCEEDED); // change this
					return result;
				}
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return result;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return result;

		} catch (IOException e) {
			e.printStackTrace();
			return result;

		} catch (JSONException e) {
			e.printStackTrace();
			return result;
		}
		return result;
	}



	/**
	 * Send accept contact request from userId
	 * @param userId
	 * @return
	 */
	public DataHolder sendAcceptContactRequestFromUserId (int userId){

		DataHolder result = new DataHolder(AppCAP.HTTP_ERROR, "Internet connection error", null);

		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost post = new HttpPost(AppCAP.URL_WEB_SERVICE + AppCAP.URL_API);

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		try {
			params.add(new BasicNameValuePair("action", "acceptContactRequest"));
			params.add(new BasicNameValuePair("initiator_id", URLEncoder.encode(userId + "", "utf-8")));

			post.setEntity(new UrlEncodedFormEntity(params));

			// Execute HTTP Post Request
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();  

			String responseString = EntityUtils.toString(resEntity); 
			RootActivity.log("HttpUtil_sendAcceptContactRequestFromUserId: " +responseString);

			if (responseString!=null){

				JSONObject json = new JSONObject(responseString);
				if (json!=null){

					result.setResponseCode(AppCAP.HTTP_REQUEST_SUCCEEDED); // change this
					return result;
				}
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return result;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return result;

		} catch (IOException e) {
			e.printStackTrace();
			return result;

		} catch (JSONException e) {
			e.printStackTrace();
			return result;
		}
		return result;
	}



	/**
	 * Send F2F invite
	 * @param userId
	 * @return
	 */
	public DataHolder sendF2FInvite (int userId){

		DataHolder result = new DataHolder(AppCAP.HTTP_ERROR, "Internet connection error", null);

		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost post = new HttpPost(AppCAP.URL_WEB_SERVICE + AppCAP.URL_API);

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		try {
			params.add(new BasicNameValuePair("action", "f2fInvite"));
			params.add(new BasicNameValuePair("greeted_id", URLEncoder.encode(userId + "", "utf-8")));

			post.setEntity(new UrlEncodedFormEntity(params));

			// Execute HTTP Post Request
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();  

			String responseString = EntityUtils.toString(resEntity); 
			RootActivity.log("HttpUtil_sendF2FInvite: " +responseString);

			if (responseString!=null){

				JSONObject json = new JSONObject(responseString);
				if (json!=null){

					result.setResponseCode(AppCAP.HTTP_REQUEST_SUCCEEDED); // change this
					return result;
				}
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return result;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return result;

		} catch (IOException e) {
			e.printStackTrace();
			return result;

		} catch (JSONException e) {
			e.printStackTrace();
			return result;
		}
		return result;
	}


	/**
	 * Send F2F accept
	 * @param userId
	 * @return
	 */
	public DataHolder sendF2FAccept (int userId){

		DataHolder result = new DataHolder(AppCAP.HTTP_ERROR, "Internet connection error", null);

		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost post = new HttpPost(AppCAP.URL_WEB_SERVICE + AppCAP.URL_API);

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		try {
			params.add(new BasicNameValuePair("action", "f2fAccept"));
			params.add(new BasicNameValuePair("greeter_id", URLEncoder.encode(userId + "", "utf-8")));

			post.setEntity(new UrlEncodedFormEntity(params));

			// Execute HTTP Post Request
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();  

			String responseString = EntityUtils.toString(resEntity); 
			RootActivity.log("HttpUtil_sendF2FAccept: " +responseString);

			if (responseString!=null){

				JSONObject json = new JSONObject(responseString);
				if (json!=null){

					result.setResponseCode(AppCAP.HTTP_REQUEST_SUCCEEDED); // change this
					return result;
				}
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return result;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return result;

		} catch (IOException e) {
			e.printStackTrace();
			return result;

		} catch (JSONException e) {
			e.printStackTrace();
			return result;
		}
		return result;
	}


	/**
	 * Send F2F decline
	 * @param userId
	 * @return
	 */
	public DataHolder sendF2FDecline (int userId){

		DataHolder result = new DataHolder(AppCAP.HTTP_ERROR, "Internet connection error", null);

		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost post = new HttpPost(AppCAP.URL_WEB_SERVICE + AppCAP.URL_API);

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		try {
			params.add(new BasicNameValuePair("action", "f2fDecline"));
			params.add(new BasicNameValuePair("greeter_id", URLEncoder.encode(userId + "", "utf-8")));

			post.setEntity(new UrlEncodedFormEntity(params));

			// Execute HTTP Post Request
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();  

			String responseString = EntityUtils.toString(resEntity); 
			RootActivity.log("HttpUtil_sendF2FDecline: " +responseString);

			if (responseString!=null){

				JSONObject json = new JSONObject(responseString);
				if (json!=null){

					result.setResponseCode(AppCAP.HTTP_REQUEST_SUCCEEDED); // change this
					return result;
				}
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return result;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return result;

		} catch (IOException e) {
			e.printStackTrace();
			return result;

		} catch (JSONException e) {
			e.printStackTrace();
			return result;
		}
		return result;
	}


	/**
	 * Send F2F verify
	 * @param userId
	 * @param password
	 * @return
	 */
	public DataHolder sendF2FVerify (int userId, String password){

		DataHolder result = new DataHolder(AppCAP.HTTP_ERROR, "Internet connection error", null);

		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost post = new HttpPost(AppCAP.URL_WEB_SERVICE + AppCAP.URL_API);

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		try {
			params.add(new BasicNameValuePair("action", "f2fVerify"));
			params.add(new BasicNameValuePair("greeter_id", URLEncoder.encode(userId + "", "utf-8")));
			params.add(new BasicNameValuePair("password", URLEncoder.encode(password +"", "utf-8")));

			post.setEntity(new UrlEncodedFormEntity(params));

			// Execute HTTP Post Request
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();  

			String responseString = EntityUtils.toString(resEntity); 
			RootActivity.log("HttpUtil_sendF2FVerify: " +responseString);

			if (responseString!=null){

				JSONObject json = new JSONObject(responseString);
				if (json!=null){

					result.setResponseCode(AppCAP.HTTP_REQUEST_SUCCEEDED); // change this
					return result;
				}
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return result;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return result;

		} catch (IOException e) {
			e.printStackTrace();
			return result;

		} catch (JSONException e) {
			e.printStackTrace();
			return result;
		}
		return result;
	}



	/**
	 * Get contact list
	 * @return
	 */
	public DataHolder getContactList (){

		DataHolder result = new DataHolder(AppCAP.HTTP_ERROR, "Internet connection error", null);

		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost post = new HttpPost(AppCAP.URL_WEB_SERVICE + AppCAP.URL_API);

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		try {
			params.add(new BasicNameValuePair("action", "getContactList"));

			post.setEntity(new UrlEncodedFormEntity(params));

			// Execute HTTP Post Request
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();  

			String responseString = EntityUtils.toString(resEntity); 
			RootActivity.log("HttpUtil_getContactList: " +responseString);

			if (responseString!=null){

				JSONObject json = new JSONObject(responseString);
				if (json!=null){

					result.setResponseCode(AppCAP.HTTP_REQUEST_SUCCEEDED); // change this
					return result;
				}
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return result;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return result;

		} catch (IOException e) {
			e.printStackTrace();
			return result;

		} catch (JSONException e) {
			e.printStackTrace();
			return result;
		}
		return result;
	}



	/**
	 * Get invitation code for specified location
	 * @param lat
	 * @param lng
	 * @return
	 */
	public DataHolder getInvitationCodeForLocation (double lat, double lng){

		DataHolder result = new DataHolder(AppCAP.HTTP_ERROR, "Internet connection error", null);

		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost post = new HttpPost(AppCAP.URL_WEB_SERVICE + AppCAP.URL_API);

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		try {
			params.add(new BasicNameValuePair("action", "getInvitationCode"));
			params.add(new BasicNameValuePair("lat", URLEncoder.encode(lat+"", "utf-8")));
			params.add(new BasicNameValuePair("lng", URLEncoder.encode(lng +"", "utf-8")));

			post.setEntity(new UrlEncodedFormEntity(params));

			// Execute HTTP Post Request
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();  

			String responseString = EntityUtils.toString(resEntity); 
			RootActivity.log("HttpUtil_getInvitationCodeForLocation: " +responseString);

			if (responseString!=null){

				JSONObject json = new JSONObject(responseString);
				if (json!=null){

					result.setResponseCode(AppCAP.HTTP_REQUEST_SUCCEEDED); // change this
					return result;
				}
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return result;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return result;

		} catch (IOException e) {
			e.printStackTrace();
			return result;

		} catch (JSONException e) {
			e.printStackTrace();
			return result;
		}
		return result;
	}



	/**
	 * Enter Invitation code
	 * @param invitationCode
	 * @param lat
	 * @param lng
	 * @return
	 */
	public DataHolder enterInvitationCode (String invitationCode, double lat, double lng){

		DataHolder result = new DataHolder(AppCAP.HTTP_ERROR, "Internet connection error", null);

		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost post = new HttpPost(AppCAP.URL_WEB_SERVICE + AppCAP.URL_API);

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		try {
			params.add(new BasicNameValuePair("action", "enterInvitationCode"));
			params.add(new BasicNameValuePair("lat", URLEncoder.encode(lat+"", "utf-8")));
			params.add(new BasicNameValuePair("lng", URLEncoder.encode(lng +"", "utf-8")));
			params.add(new BasicNameValuePair("invite_code", URLEncoder.encode(invitationCode +"", "utf-8")));

			post.setEntity(new UrlEncodedFormEntity(params));

			// Execute HTTP Post Request
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();  

			String responseString = EntityUtils.toString(resEntity); 
			RootActivity.log("HttpUtil_enterInvitationCode: " +responseString);

			if (responseString!=null){

				JSONObject json = new JSONObject(responseString);
				if (json!=null){

					if (json.optBoolean("error")){
						result.setResponseCode(AppCAP.HTTP_ERROR);
						result.setResponseMessage(json.optString("payload"));
					} else {
						result.setResponseCode(ActivityEnterInviteCode.HANDLE_ENTER_INV_CODE); 
					}
					return result;
				}
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return result;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return result;

		} catch (IOException e) {
			e.printStackTrace();
			return result;

		} catch (JSONException e) {
			e.printStackTrace();
			return result;
		}
		return result;
	}


	/**
	 * Save User Job Category
	 * @param majorJobCategory
	 * @param minorJobCategory
	 * @return
	 */
	public DataHolder saveUserJobCategory (String majorJobCategory, String minorJobCategory){

		DataHolder result = new DataHolder(AppCAP.HTTP_ERROR, "Internet connection error", null);

		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost post = new HttpPost(AppCAP.URL_WEB_SERVICE + AppCAP.URL_API);

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		try {
			params.add(new BasicNameValuePair("action", "updateJobCategories"));
			params.add(new BasicNameValuePair("major_job_category", URLEncoder.encode(majorJobCategory +"", "utf-8")));
			params.add(new BasicNameValuePair("minor_job_category", URLEncoder.encode(minorJobCategory +"", "utf-8")));

			post.setEntity(new UrlEncodedFormEntity(params));

			// Execute HTTP Post Request
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();  

			String responseString = EntityUtils.toString(resEntity); 
			RootActivity.log("HttpUtil_saveUserMajorJobCategory: " +responseString);

			if (responseString!=null){

				JSONObject json = new JSONObject(responseString);
				if (json!=null){

					if (json.optBoolean("error")){
						result.setResponseCode(AppCAP.HTTP_ERROR);
						result.setResponseMessage(json.optString("payload"));
					} else {
						result.setResponseCode(ActivityJobCategory.HANDLE_UPLOAD_JOBS_INFO);
						result.setResponseMessage(json.optString("payload"));
					}
					return result;
				}
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return result;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return result;

		} catch (IOException e) {
			e.printStackTrace();
			return result;

		} catch (JSONException e) {
			e.printStackTrace();
			return result;
		}
		return result;
	}


	/**
	 * Save user smarterer name
	 * @param name
	 * @return
	 */
	public DataHolder saveUserSmartererName (String name){

		DataHolder result = new DataHolder(AppCAP.HTTP_ERROR, "Internet connection error", null);

		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost post = new HttpPost(AppCAP.URL_WEB_SERVICE + AppCAP.URL_API);

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		try {
			params.add(new BasicNameValuePair("action", "saveUserSmartererName"));
			params.add(new BasicNameValuePair("name", URLEncoder.encode(name +"", "utf-8")));

			post.setEntity(new UrlEncodedFormEntity(params));

			// Execute HTTP Post Request
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();  

			String responseString = EntityUtils.toString(resEntity); 
			RootActivity.log("HttpUtil_saveUserSmartererName: " +responseString);

			if (responseString!=null){

				JSONObject json = new JSONObject(responseString);
				if (json!=null){

					result.setResponseCode(AppCAP.HTTP_REQUEST_SUCCEEDED); // change this
					return result;
				}
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return result;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return result;

		} catch (IOException e) {
			e.printStackTrace();
			return result;

		} catch (JSONException e) {
			e.printStackTrace();
			return result;
		}
		return result;
	}


	/**
	 * Check out user from location
	 * @return
	 */
	public DataHolder checkOut(){

		DataHolder result = new DataHolder(AppCAP.HTTP_ERROR, "Internet connection error", null);

		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost post = new HttpPost(AppCAP.URL_WEB_SERVICE + AppCAP.URL_API);

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		try {
			params.add(new BasicNameValuePair("action", "checkout"));

			post.setEntity(new UrlEncodedFormEntity(params));

			// Execute HTTP Post Request
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();  

			String responseString = EntityUtils.toString(resEntity); 
			RootActivity.log("HttpUtil_checkOut: " +responseString);

			if (responseString!=null){

				JSONObject json = new JSONObject(responseString);
				if (json!=null){

					result.setResponseCode(UserAndTabMenu.HANDLE_CHECK_OUT);
					return result;
				}
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return result;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return result;

		} catch (IOException e) {
			e.printStackTrace();
			return result;

		} catch (JSONException e) {
			e.printStackTrace();
			return result;
		}
		return result;
	}


	/**
	 * Check in user to location
	 * @return
	 */
	public DataHolder checkIn(Venue venue, int checkInTime, int checkOutTime, String statusText){

		DataHolder result = new DataHolder(AppCAP.HTTP_ERROR, "Internet connection error", null);

		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost post = new HttpPost(AppCAP.URL_WEB_SERVICE + AppCAP.URL_API);

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		try {
			params.add(new BasicNameValuePair("action", "checkin"));
			params.add(new BasicNameValuePair("lat", Double.toString(venue.getLat())));
			params.add(new BasicNameValuePair("lng", Double.toString(venue.getLng())));
			params.add(new BasicNameValuePair("venue_name", URLEncoder.encode(venue.getName()+"", "utf-8")));
			params.add(new BasicNameValuePair("checkin", checkInTime + ""));
			params.add(new BasicNameValuePair("checkout", checkOutTime + ""));
			params.add(new BasicNameValuePair("foursquare", URLEncoder.encode(venue.getId()+"", "utf-8")));
			params.add(new BasicNameValuePair("address", URLEncoder.encode(venue.getAddress()+"", "utf-8")));
			params.add(new BasicNameValuePair("city", URLEncoder.encode(venue.getCity()+"", "utf-8")));
			params.add(new BasicNameValuePair("state", URLEncoder.encode(venue.getState()+"", "utf-8")));
			params.add(new BasicNameValuePair("zip", URLEncoder.encode(venue.getPostalCode()+"", "utf-8")));
			params.add(new BasicNameValuePair("phone", ""));
			params.add(new BasicNameValuePair("status", URLEncoder.encode(statusText+"", "utf-8")));

			post.setEntity(new UrlEncodedFormEntity(params));

			// Execute HTTP Post Request
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();  

			String responseString = EntityUtils.toString(resEntity); 
			RootActivity.log("HttpUtil_checkIn: " +responseString);

			if (responseString!=null){

				JSONObject json = new JSONObject(responseString);
				if (json!=null){

					String res = json.optString("response");
					if (res.equals("1")){
						result.setResponseCode(AppCAP.HTTP_REQUEST_SUCCEEDED);
					}
					return result;
				}
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return result;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return result;

		} catch (IOException e) {
			e.printStackTrace();
			return result;

		} catch (JSONException e) {
			e.printStackTrace();
			return result;
		}

		return result;

	}


	/**
	 * Get venues near my location
	 * @param gp GeoPoint with my coordinates
	 * @param number of displayed venues
	 * @return
	 */
	public DataHolder getVenuesCloseToLocation(GeoPoint gp, int number){

		double latFromGp = gp.getLatitudeE6() / 1E6;
		double lngFromGp = gp.getLongitudeE6() / 1E6;

		DataHolder result = new DataHolder(AppCAP.HTTP_ERROR, "Internet connection error", null);

		//HttpClient client = getThreadSafeClient();
		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpGet get = new HttpGet(AppCAP.URL_FOURSQUARE + "&limit=" + number + "&ll=" + latFromGp +"," + lngFromGp);

		try {
			// Execute HTTP Get Request
			HttpResponse responseClient = client.execute(get);
			HttpEntity resEntity = responseClient.getEntity();  

			String responseString = EntityUtils.toString(resEntity); 
			RootActivity.log("HttpUtil_getVenuesCloseToLocation " + responseString);

			if (responseString!=null){
				JSONObject json = new JSONObject(responseString);

				JSONObject meta = json.optJSONObject("meta");
				if (meta!=null){

					int code = meta.optInt("code");
					if (code==200){

						JSONObject response = json.optJSONObject("response");
						if (response!=null){

							JSONArray venues = response.optJSONArray("venues");
							if (venues!=null){

								ArrayList<Venue> venuesArray = new ArrayList<Venue>();

								for(int m=0; m<venues.length(); m++){

									JSONObject venue = venues.optJSONObject(m);
									if (venue!=null){

										String id = venue.optString("id");
										String name = venue.optString("name");
										String address = "";
										String crossStreet = "";
										double lat = 0;
										double lng = 0;
										int  distance = 0;
										String postalCode = "";
										String city = "";
										String state = "";
										String country = "";
										String categoryName = "";
										String categoryPluralName = "";
										String categoryShortName = "";
										int checkinsCount = 0;
										int usersCount = 0;
										int tipCount = 0;
										int hereNowCount = 0;


										// Location Object
										JSONObject locationObj = venue.optJSONObject("location");
										if (locationObj!=null){

											address = locationObj.optString("address");
											crossStreet = locationObj.optString("crossStreet");
											lat = locationObj.optDouble("lat");
											lng = locationObj.optDouble("lng");
											distance = locationObj.optInt("distance");
											postalCode = locationObj.optString("postalCode");
											city = locationObj.optString("city");
											state = locationObj.optString("state");
											country = locationObj.optString("country");
										}

										// Categories Array
										JSONArray categoriesArray = venue.optJSONArray("categories");
										if (categoriesArray!=null){

											if (categoriesArray.length()>0){

												JSONObject cat = categoriesArray.optJSONObject(0);
												if (cat!=null){

													categoryName = cat.optString("name");
													categoryPluralName = cat.optString("pluralName");
													categoryShortName = cat.optString("shortName");
												}
											}
										}

										// Stats Object
										JSONObject statsObj = venue.optJSONObject("stats");
										if (statsObj!=null){

											checkinsCount = statsObj.optInt("checkinsCount");
											usersCount = statsObj.optInt("usersCount");
											tipCount = statsObj.optInt("tipCount");
										}

										// HereNow Object
										JSONObject hereNowObj = venue.optJSONObject("hereNow");
										if (hereNowObj!=null){

											hereNowCount = hereNowObj.optInt("count");
										}

										venuesArray.add(new Venue(id, name, address, crossStreet, lat, lng, distance, postalCode, city, state, 
												country, categoryName, categoryPluralName, categoryShortName, checkinsCount, usersCount, tipCount, hereNowCount, "", ""));
									}
								}

								result.setResponseCode(code);
								result.setObject(venuesArray);
								return result;
							}
						}

					} else {
						result.setResponseCode(code);
						return result;
					}

				}
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return result;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return result;

		} catch (IOException e) {
			e.printStackTrace();
			return result;

		} catch (JSONException e) {
			e.printStackTrace();
			return result;

		}

		return result;
	}

	/**
	 * Get User transaction data
	 * @return
	 */
	public DataHolder getUserTransactionData (){

		DataHolder result = new DataHolder(AppCAP.HTTP_ERROR, "Internet connection error", null);

		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost post = new HttpPost(AppCAP.URL_WEB_SERVICE + AppCAP.URL_API);

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("action", "getTransactionData"));

		try {
			post.setEntity(new UrlEncodedFormEntity(params));

			// Execute HTTP Post Request
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();  

			String responseString = EntityUtils.toString(resEntity); 
			RootActivity.log("HttpUtil_getUserTrasactionData: " +responseString);

			if (responseString!=null){

				JSONObject json = new JSONObject(responseString);
				if (json!=null){

					JSONObject objPayload = json.optJSONObject("payload");
					if (objPayload!=null){
						ArrayList<Transaction> transactions = new ArrayList<Transaction>();

						// Implement Transactions here !!!!!!!!!!!!!
						//
						//

						result.setResponseCode(ActivityWallet.HANDLE_GET_TRANSACTION_DATA);
						result.setObject(new UserTransaction(
								objPayload.optInt("userid"), 
								objPayload.optString("nickname"), 
								objPayload.optString("username"), 
								objPayload.optString("status_text"), 
								objPayload.optString("status"), 
								objPayload.optString("active"), 
								objPayload.optString("photo"), 
								objPayload.optString("photo_large"), 
								objPayload.optDouble("lat"), 
								objPayload.optDouble("lng"), 
								objPayload.optInt("favorite_enabled"), 
								objPayload.optInt("favorite_count"), 
								objPayload.optInt("my_favorite_count"), 
								objPayload.optInt("money_received"), 
								objPayload.optInt("offers_paid"), 
								objPayload.optInt("balance"), 
								transactions));
					}
					return result;
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return result;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return result;

		} catch (IOException e) {
			e.printStackTrace();
			return result;

		} catch (JSONException e) {
			e.printStackTrace();
			return result;
		}
		return result;
	}



	/**
	 * Get user data for logged user
	 * @return
	 */
	public DataHolder getUserData(){

		DataHolder result = new DataHolder(AppCAP.HTTP_ERROR, "Internet connection error", null);

		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost post = new HttpPost(AppCAP.URL_WEB_SERVICE + AppCAP.URL_API);

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("action", "getUserData"));

		try {

			post.setEntity(new UrlEncodedFormEntity(params));

			// Execute HTTP Post Request
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();  

			String responseString = EntityUtils.toString(resEntity); 
			RootActivity.log("HttpUtil_getUserData: " +responseString);

			// Save cookies to share session with WebView
			client.getCookieStore().getCookies();
			List<Cookie> cookies = client.getCookieStore().getCookies();
			String cookieString = "";
			for (int i = 0; i < cookies.size(); i++) {
				Cookie cookie = cookies.get(i);
				cookieString += cookie.getName() +"="+cookie.getValue();//+"; domain="+cookie.getDomain();
			}
			AppCAP.setCookieString(cookieString);
			Log.d("LOG", "Cookie: " + AppCAP.getCookieString());

			if (responseString!=null){

				JSONObject json = new JSONObject(responseString);
				if (json!=null){

					int userId = json.optInt("userid");
					String nickName = json.optString("nickname");
					String userName = json.optString("username");
					String statusText = json.optString("status_text");
					String status = json.optString("status");
					String active = json.optString("active");
					String photo = json.optString("photo");
					String photoLarge = json.optString("photo_large");
					double lat = json.optDouble("lat");
					double lng = json.optDouble("lng");
					int favoriteEnabled = json.optInt("favorite_enabled");
					int favoriteCount = json.optInt("favorite_count");
					int myFavoriteCount = json.optInt("my_favorite_count");
					int moneyReceived = json.optInt("money_received");
					int offersPaid = json.optInt("offers_paid");
					int balance = json.getInt("balance");

					AppCAP.setLocalUserPhotoLargeURL(photoLarge);
					AppCAP.setLocalUserPhotoURL(photo);

					result.setObject(new User(userId, favoriteEnabled, favoriteCount, myFavoriteCount, moneyReceived, 
							offersPaid, balance, nickName, userName, statusText, status, active, photo, photoLarge, lat, lng));
					result.setResponseCode(AppCAP.HTTP_REQUEST_SUCCEEDED);
					return result;
				}


			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return result;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return result;

		} catch (IOException e) {
			e.printStackTrace();
			return result;

		} catch (JSONException e) {
			e.printStackTrace();
			return result;
		}

		return result;
	}


	/** 
	 * Get Notification Settings
	 * @return
	 */
	public DataHolder getNotificationSettings (){
		DataHolder result = new DataHolder(AppCAP.HTTP_ERROR, "Internet connection error", null);

		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		HttpPost post = new HttpPost(AppCAP.URL_WEB_SERVICE + AppCAP.URL_API);

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("action", "getNotificationSettings"));

		try {
			post.setEntity(new UrlEncodedFormEntity(params));

			// Execute HTTP Post Request
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();  

			String responseString = EntityUtils.toString(resEntity); 
			RootActivity.log("HttpUtil_getNotificationSettings: " +responseString);

			if (responseString!=null){

				JSONObject json = new JSONObject(responseString);
				if (json!=null){
					
					JSONObject objPayload = json.optJSONObject("payload");
					if (objPayload!=null){
						
						result.setObject(new Object[]{objPayload.optString("push_distance"), json.optString("checked_in_only")});
					}
				}
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return result;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return result;

		} catch (IOException e) {
			e.printStackTrace();
			return result;

		} catch (JSONException e) {
			e.printStackTrace();
			return result;
		}
		return result;
	}
	
	
	
	/**
	 * Sign up for a new account
	 * @param userName
	 * @param password
	 * @param confPassword
	 * @param nickName
	 * @return
	 */
	public DataHolder signup(String userName, String password, String nickName ){

		DataHolder result = new DataHolder(AppCAP.HTTP_ERROR, "Internet connection error", null);

		//HttpClient client = getThreadSafeClient();
		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost post = new HttpPost(AppCAP.URL_WEB_SERVICE + AppCAP.URL_SIGNUP);

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("action", "signup"));
		params.add(new BasicNameValuePair("signupUsername", userName));
		params.add(new BasicNameValuePair("signupPassword", password));
		params.add(new BasicNameValuePair("signupConfirm", password));
		params.add(new BasicNameValuePair("signupNickname", nickName));
		params.add(new BasicNameValuePair("signupAcceptTerms", "1"));
		params.add(new BasicNameValuePair("type", "json"));

		try {

			post.setEntity(new UrlEncodedFormEntity(params));

			// Execute HTTP Post Request
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();  

			String responseString = EntityUtils.toString(resEntity); 
			RootActivity.log("HttpUtil_signup: " + EntityUtils.toString(post.getEntity())+":"+responseString);

			if (responseString!=null){

				JSONObject json = new JSONObject(responseString);
				Boolean succeeded = json.optBoolean("succeeded");
				String mess = json.optString("message");

				result.setResponseMessage(mess);

				if (succeeded){

					result.setResponseCode(AppCAP.HTTP_REQUEST_SUCCEEDED);
					return result;

				} else {
					result.setResponseCode(AppCAP.ERROR_SUCCEEDED_SHOW_MESS);
					return result;
				}
			}


		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return result;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return result;

		} catch (IOException e) {
			e.printStackTrace();
			return result;

		} catch (JSONException e) {
			e.printStackTrace();
			return result;

		}

		return result;

	}

	/**
	 * Sign up for a new account using a 3rd party
	 * @param userId
	 * @param token
	 * @return
	 */
	public DataHolder signupViaOAuthService(OAuthService service){

		DataHolder result = new DataHolder(AppCAP.HTTP_ERROR, "Internet connection error", null);

		//HttpClient client = getThreadSafeClient();
		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost post = new HttpPost(AppCAP.URL_WEB_SERVICE + AppCAP.URL_SIGNUP);

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		String serviceName = service.getServiceNameSignUp();
		params.add(new BasicNameValuePair("action", "signup"));
		params.add(new BasicNameValuePair("oauth_token", service.getAccessToken()));
		params.add(new BasicNameValuePair("oauth_secret", service.getAccessTokenSecret()));
		params.add(new BasicNameValuePair(serviceName + "_connect", "1"));
		params.add(new BasicNameValuePair(serviceName + "_id", service.getUserId()));
		params.add(new BasicNameValuePair("signupUsername", service.getUserName()));
		params.add(new BasicNameValuePair("signupNickname", service.getUserNickName()));
		params.add(new BasicNameValuePair("signupPassword", service.getUserPassword()));
		params.add(new BasicNameValuePair("signupConfirm", service.getUserPassword()));		
		params.add(new BasicNameValuePair("type", "json"));

		try {

			post.setEntity(new UrlEncodedFormEntity(params));

			// Execute HTTP Post Request
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();  

			String responseString = EntityUtils.toString(resEntity); 
			RootActivity.log("HttpUtil_signup: " + EntityUtils.toString(post.getEntity())+":"+responseString);

			if (responseString!=null){

				JSONObject json = new JSONObject(responseString);
				Boolean succeeded = json.optBoolean("succeeded");
				String mess = json.optString("message");

				result.setResponseMessage(mess);

				if (succeeded){

					result.setResponseCode(AppCAP.HTTP_REQUEST_SUCCEEDED);
					return result;

				} else {
					result.setResponseCode(AppCAP.ERROR_SUCCEEDED_SHOW_MESS);
					return result;
				}
			}


		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return result;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return result;

		} catch (IOException e) {
			e.printStackTrace();
			return result;

		} catch (JSONException e) {
			e.printStackTrace();
			return result;

		}

		return result;

	}

	/**
	 * Login 
	 * @param userName
	 * @param password
	 * @return
	 */
	public DataHolder login(String userName, String password){

		DataHolder result = new DataHolder(AppCAP.HTTP_ERROR, "Internet connection error", null);

		//HttpClient client = getThreadSafeClient();
		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost post = new HttpPost(AppCAP.URL_WEB_SERVICE + AppCAP.URL_LOGIN);

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("action", "login"));
		params.add(new BasicNameValuePair("username", userName));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("type", "json"));

		try {

			post.setEntity(new UrlEncodedFormEntity(params));

			// Execute HTTP Post Request
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();  

			String responseString = EntityUtils.toString(resEntity); 
			RootActivity.log("HttpUtil_login: " + EntityUtils.toString(post.getEntity())+":"+responseString);

			if (responseString!=null){

				JSONObject json = new JSONObject(responseString);
				Boolean succeeded = json.optBoolean("succeeded");
				String mess = json.optString("message");

				result.setResponseMessage(mess);

				if (succeeded){
					/*
					JSONObject paramsObj = json.optJSONObject("params");
					if (paramsObj!=null){

						JSONObject userObj = paramsObj.optJSONObject("user");
						if (userObj!=null){

							int userId = userObj.optInt("id");
							String nickName = userObj.optString("nickname");

							result.setObject(new User(userId, nickName));
						}
					}
					 */
					result.setResponseCode(AppCAP.HTTP_REQUEST_SUCCEEDED);
					return result;

				} else {
					result.setResponseCode(AppCAP.ERROR_SUCCEEDED_SHOW_MESS);
					return result;
				}
			}


		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return result;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return result;

		} catch (IOException e) {
			e.printStackTrace();
			return result;

		} catch (JSONException e) {
			e.printStackTrace();
			return result;

		}

		return result;
	}

	public DataHolder loginViaOAuthService(OAuthService service){

		DataHolder result = new DataHolder(AppCAP.HTTP_ERROR, "Internet connection error", null);

		//HttpClient client = getThreadSafeClient();
		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost post = new HttpPost(AppCAP.URL_WEB_SERVICE + AppCAP.URL_LOGIN);

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		String serviceName = service.getServiceNameLogin();
		String serviceSignUp = service.getServiceNameSignUp();
		params.add(new BasicNameValuePair("action", "login" + serviceName));
		params.add(new BasicNameValuePair("oauth_token", service.getAccessToken()));
		params.add(new BasicNameValuePair("oauth_secret", service.getAccessTokenSecret()));
		params.add(new BasicNameValuePair("login_" + serviceSignUp + "_connect", "1"));
		params.add(new BasicNameValuePair("login_" + serviceSignUp + "_id", service.getUserId()));
		params.add(new BasicNameValuePair("type", "json"));

		try {

			post.setEntity(new UrlEncodedFormEntity(params));

			// Execute HTTP Post Request
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();  

			String responseString = EntityUtils.toString(resEntity); 
			RootActivity.log("HttpUtil_login: " + EntityUtils.toString(post.getEntity())+":"+responseString);

			if (responseString!=null){

				JSONObject json = new JSONObject(responseString);
				Boolean succeeded = json.optBoolean("succeeded");
				String mess = json.optString("message");

				result.setResponseMessage(mess);

				if (succeeded){
					/*
					JSONObject paramsObj = json.optJSONObject("params");
					if (paramsObj!=null){

						JSONObject userObj = paramsObj.optJSONObject("user");
						if (userObj!=null){

							int userId = userObj.optInt("id");
							String nickName = userObj.optString("nickname");

							result.setObject(new User(userId, nickName));
						}
					}
					 */
					result.setResponseCode(AppCAP.HTTP_REQUEST_SUCCEEDED);
					return result;

				} else {
					result.setResponseCode(AppCAP.ERROR_SUCCEEDED_SHOW_MESS);
					return result;
				}
			}


		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return result;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return result;

		} catch (IOException e) {
			e.printStackTrace();
			return result;

		} catch (JSONException e) {
			e.printStackTrace();
			return result;

		}

		return result;
	}


	/**
	 * Returns the Http client that is safe to use with threads
	 * @return
	 */
	private Object mLock = new Object();
	private CookieStore mCookie = null;

	private  DefaultHttpClient getThreadSafeClient()  {
		DefaultHttpClient client = new DefaultHttpClient();
		synchronized (mLock) {
			if (mCookie == null) {
				mCookie = client.getCookieStore();
			} else {
				client.setCookieStore(mCookie);
			}
		}
		ClientConnectionManager mgr = client.getConnectionManager();
		HttpParams params = client.getParams();
		client = new DefaultHttpClient(new ThreadSafeClientConnManager(params, mgr.getSchemeRegistry()), params);

		workAroundReverseDnsBugInHoneycombAndEarlier(client);
		return client;

	}

	private void workAroundReverseDnsBugInHoneycombAndEarlier(org.apache.http.client.HttpClient client) {
		// Android had a bug where HTTPS made reverse DNS lookups (fixed in Ice Cream Sandwich) 
		// http://code.google.com/p/android/issues/detail?id=13117
		SocketFactory socketFactory = new LayeredSocketFactory() {
			SSLSocketFactory delegate = SSLSocketFactory.getSocketFactory();
			@Override public Socket createSocket() throws IOException {
				return delegate.createSocket();
			}
			@Override public Socket connectSocket(Socket sock, String host, int port,
					InetAddress localAddress, int localPort, HttpParams params) throws IOException {
				return delegate.connectSocket(sock, host, port, localAddress, localPort, params);
			}
			@Override public boolean isSecure(Socket sock) throws IllegalArgumentException {
				return delegate.isSecure(sock);
			}
			@Override public Socket createSocket(Socket socket, String host, int port,
					boolean autoClose) throws IOException {
				injectHostname(socket, host);
				return delegate.createSocket(socket, host, port, autoClose);
			}
			private void injectHostname(Socket socket, String host) {
				try {
					Field field = InetAddress.class.getDeclaredField("hostName");
					field.setAccessible(true);
					field.set(socket.getInetAddress(), host);
				} catch (Exception ignored) {
				}
			}
		};

		client.getConnectionManager().getSchemeRegistry()
		.register(new Scheme("https", socketFactory, 443));
	}

}

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
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
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
import com.coffeeandpower.activity.ActivitySettings;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.MapUserData;
import com.coffeeandpower.cont.User;
import com.coffeeandpower.cont.Venue;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class HttpUtil {

	private HttpClient client;


	public HttpUtil(){

		this.client = getThreadSafeClient();
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
	 * Get users checked in around me
	 * @param venue
	 * @return
	 */
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

							ArrayList<MapUserData> mapUsersArray = new ArrayList<MapUserData>();

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

									mapUsersArray.add(new MapUserData(checkInId, userId, nickName, statusText, photo, majorJobCategory, minorJobCategory, 
											headLine, fileName, lat, lng, checkedIn, foursquareId, venueName, checkInCount, skills, met));
								}
							}

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



	/**
	 * Get users checked in venue
	 * @param venue
	 * @return
	 */
	public DataHolder getUsersCheckedInAtFoursquareID (Venue venue){

		DataHolder result = new DataHolder(AppCAP.HTTP_ERROR, "Internet connection error", null);

		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost post = new HttpPost(AppCAP.URL_WEB_SERVICE + AppCAP.URL_API);

		List<NameValuePair> params = new ArrayList<NameValuePair>(2);

		try {
			params.add(new BasicNameValuePair("action", "getUsersCheckedIn"));
			params.add(new BasicNameValuePair("foursquare", URLEncoder.encode(venue.getId()+"", "utf-8")));

			post.setEntity(new UrlEncodedFormEntity(params));

			// Execute HTTP Post Request
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();  

			String responseString = EntityUtils.toString(resEntity); 
			RootActivity.log("HttpUtil_getUsersCheckedIn: " +responseString);

			if (responseString!=null){

				JSONObject json = new JSONObject(responseString);
				if (json!=null){

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

		List<NameValuePair> params = new ArrayList<NameValuePair>(1);

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

		List<NameValuePair> params = new ArrayList<NameValuePair>(13);

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
												country, categoryName, categoryPluralName, categoryShortName, checkinsCount, usersCount, tipCount, hereNowCount));
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
	 * Get user data for logged user
	 * @return
	 */
	public DataHolder getUserData(){

		DataHolder result = new DataHolder(AppCAP.HTTP_ERROR, "Internet connection error", null);

		//HttpClient client = getThreadSafeClient();
		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost post = new HttpPost(AppCAP.URL_WEB_SERVICE + AppCAP.URL_API);

		List<NameValuePair> params = new ArrayList<NameValuePair>(1);
		params.add(new BasicNameValuePair("action", "getUserData"));

		try {

			post.setEntity(new UrlEncodedFormEntity(params));

			// Execute HTTP Post Request
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();  

			String responseString = EntityUtils.toString(resEntity); 
			RootActivity.log("HttpUtil_getUserData: " +responseString);

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

		List<NameValuePair> params = new ArrayList<NameValuePair>(7);

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

		List<NameValuePair> params = new ArrayList<NameValuePair>(4);

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


	/**
	 * Logout current user
	 * @return
	 */
	public DataHolder logout(){

		DataHolder result = new DataHolder(AppCAP.HTTP_ERROR, "Internet connection error", null);

		//HttpClient client = getThreadSafeClient();
		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpGet get = new HttpGet(AppCAP.URL_WEB_SERVICE + AppCAP.URL_LOGOUT);

		try {
			// Execute HTTP Get Request
			HttpResponse response = client.execute(get);
			HttpEntity resEntity = response.getEntity();  

			String responseString = EntityUtils.toString(resEntity); 
			RootActivity.log("HttpUtil_logout: " +responseString);

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

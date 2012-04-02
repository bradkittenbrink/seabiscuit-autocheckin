package com.coffeeandpower.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.Socket;
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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.User;
import com.coffeeandpower.cont.Venue;
import com.google.android.maps.GeoPoint;

public class HttpUtil {

	private HttpClient client;
	
	
	public HttpUtil(){
		
		this.client = getThreadSafeClient();
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

		HttpGet post = new HttpGet(AppCAP.URL_WEB_SERVICE + AppCAP.URL_API+"?action=getUserData");

		//List<NameValuePair> params = new ArrayList<NameValuePair>(1);

		//params.add(new BasicNameValuePair("action", "getUserData"));

		try {

			//post.setEntity(new UrlEncodedFormEntity(params));

			// Execute HTTP Post Request
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();  

			String responseString = EntityUtils.toString(resEntity); 
			RootActivity.log("HttpUtil_getUserData: " +responseString);

			if (responseString!=null){

				JSONObject json = new JSONObject(responseString);
				
				

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

					JSONObject paramsObj = json.optJSONObject("params");
					if (paramsObj!=null){

						JSONObject userObj = paramsObj.optJSONObject("user");
						if (userObj!=null){

							int userId = userObj.optInt("id");
							String nickName = userObj.optString("nickname");

							result.setObject(new User(userId, nickName));
						}
					}

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

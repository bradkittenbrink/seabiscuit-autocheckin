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
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
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
import org.json.JSONException;
import org.json.JSONObject;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.cont.ResponseData;

public class HttpUtil {


	/**
	 * Get user data for logged user
	 * @return
	 */
	public static ResponseData getUserData(){

		ResponseData result = new ResponseData(AppCAP.HTTP_ERROR, "Internet connection error", null);

		HttpClient client = getThreadSafeClient();
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
			RootActivity.log(EntityUtils.toString(post.getEntity())+":"+responseString);

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
	public static ResponseData signup(String userName, String password, String nickName ){

		ResponseData result = new ResponseData(AppCAP.HTTP_ERROR, "Internet connection error", null);
		
		HttpClient client = getThreadSafeClient();
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
			RootActivity.log(EntityUtils.toString(post.getEntity())+":"+responseString);

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
	public static ResponseData login(String userName, String password){
		
		ResponseData result = new ResponseData(AppCAP.HTTP_ERROR, "Internet connection error", null);

		HttpClient client = getThreadSafeClient();
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
			RootActivity.log(EntityUtils.toString(post.getEntity())+":"+responseString);

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
	 * Returns the Http client that is safe to use with threads
	 * @return
	 */
	private static DefaultHttpClient getThreadSafeClient()  {
		DefaultHttpClient client = new DefaultHttpClient();
		ClientConnectionManager mgr = client.getConnectionManager();
		HttpParams params = client.getParams();
		client = new DefaultHttpClient(new ThreadSafeClientConnManager(params, mgr.getSchemeRegistry()), params);

		workAroundReverseDnsBugInHoneycombAndEarlier(client);
		return client;

	}

	private static void workAroundReverseDnsBugInHoneycombAndEarlier(org.apache.http.client.HttpClient client) {
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

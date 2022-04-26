package com.codepath.apps.restclienttemplate;


import android.util.Log;

import com.parse.twitter.ParseTwitterUtils;


import java.io.IOException;
import java.util.List;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthProvider;

public class TwitterClient {
	private static final String TAG = "TwitterClient";

	public static String baseUrl = "https://api.twitter.com/1.1";

	public static String getApiUrl(String endpoint) {
		return baseUrl + "/" + endpoint;
	}

	public static OkHttpClient client = new OkHttpClient();
	public static OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer(
			ParseTwitterUtils.getTwitter().getConsumerKey(),
			ParseTwitterUtils.getTwitter().getConsumerSecret()
	);

	public TwitterClient() {
	}


	public void exampleQuery(Callback callback) {
		try {
			Request request = new Request.Builder()
				.url(getApiUrl("users/lookup.json"))
				.build();
			Request signedRequest = (Request) consumer.sign(request).unwrap();
			client.newCall(signedRequest).enqueue(callback);
		} catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException e) {
			e.printStackTrace();
		}
	}

	public static void getUser(Callback callback,String id){
		try {
			HttpUrl.Builder urlBuilder = HttpUrl.parse(getApiUrl("users/show.json")).newBuilder();
			urlBuilder.addQueryParameter("id",id);
			String url = urlBuilder.build().toString();
			Request request = new Request.Builder()
					.url(url)
					.build();
			Request signedRequest = (Request) consumer.sign(request).unwrap();
			client.newCall(signedRequest).enqueue(callback);
		} catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException e) {
			e.printStackTrace();
		}
	}

	public static void getUsers(Callback callback, String ... ids) {
		try {
			HttpUrl.Builder urlBuilder = HttpUrl.parse(getApiUrl("users/lookup.json")).newBuilder();
			urlBuilder.addQueryParameter("include_entities", String.valueOf(false));
			urlBuilder.addQueryParameter("skip_status", String.valueOf(true));
			// Join ids
			StringBuilder user_ids = new StringBuilder();
			for(int i = 0; i < ids.length; i++) {
				if (i == ids.length - 1) {
					user_ids.append(ids[i]);
				} else {
					user_ids.append(ids[i]).append(',');
				}
			}
			urlBuilder.addQueryParameter("user_id", user_ids.toString());
			String url = urlBuilder.build().toString();
			Request request = new Request.Builder()
					.url(url)
					.build();
			Request signedRequest = (Request) consumer.sign(request).unwrap();
			client.newCall(signedRequest).enqueue(callback);
		} catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException e) {
			e.printStackTrace();
		}
	}

	// include_user_entities=false&count={{FRIENDS_PICK_MAX}}&skip_status=true
	public static Response getFriendsUserObjects() {
		consumer.setTokenWithSecret(ParseTwitterUtils.getTwitter().getAuthToken(),
									ParseTwitterUtils.getTwitter().getAuthTokenSecret());
		try {
			HttpUrl.Builder urlBuilder = HttpUrl.parse(getApiUrl("friends/list.json")).newBuilder();
			urlBuilder.addQueryParameter("include_user_entities", String.valueOf(false));
			urlBuilder.addQueryParameter("skip_status", String.valueOf(true));
			String url = urlBuilder.build().toString();
			Request request = new Request.Builder()
					.url(url)
					.build();
			Request signedRequest = (Request) consumer.sign(request).unwrap();
			return client.newCall(signedRequest).execute();
		} catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void fetchFriendIds(Callback callback) {
		try {
			Request request = new Request.Builder()
					.url(getApiUrl("friends/ids.json"))
					.build();
			Request signedRequest = (Request) consumer.sign(request).unwrap();
			client.newCall(signedRequest).enqueue(callback);
		} catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException e) {
			e.printStackTrace();
		}
	}

	public void fetchUserTimeline(String userId, Callback callback) {
		try {
			HttpUrl.Builder urlBuilder = HttpUrl.parse(getApiUrl("statuses/user_timeline.json")).newBuilder();
			urlBuilder.addQueryParameter("user_id", userId)
					.addQueryParameter("include_rts", String.valueOf(false))
					.addQueryParameter("tweet_mode", "extended")
					.addQueryParameter("count", String.valueOf(5));
			String url = urlBuilder.build().toString();
			Request request = new Request.Builder()
					.url(url)
					.build();
			Request signedRequest = (Request) consumer.sign(request).unwrap();
			client.newCall(signedRequest).enqueue(callback);
		} catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException e) {
			e.printStackTrace();
		}
	}

	public static void fetchTweets(Callback callback, String ... ids) {
		StringBuilder idsCommaSeparatedBuilder = new StringBuilder();
		for (String id: ids) {
			idsCommaSeparatedBuilder.append(id);
			idsCommaSeparatedBuilder.append(",");
		}
		String idsCommaSeparated = idsCommaSeparatedBuilder.substring(0, idsCommaSeparatedBuilder.length() - 1);

		try {
			HttpUrl.Builder urlBuilder = HttpUrl.parse(getApiUrl("statuses/lookup.json")).newBuilder();
			urlBuilder
					.addQueryParameter("id", idsCommaSeparated)
					.addQueryParameter("tweet_mode", "extended")
					.addQueryParameter("map", "true");
			String url = urlBuilder.build().toString();
			Request request = new Request.Builder()
					.url(url)
					.get()
					.build();
			Request signedRequest = (Request) consumer.sign(request).unwrap();
			client.newCall(signedRequest).enqueue(callback);
		} catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException e) {
			e.printStackTrace();
		}
	}
}

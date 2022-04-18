package com.codepath.apps.restclienttemplate;


import com.parse.twitter.ParseTwitterUtils;


import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;

public class TwitterClient {
	public static String baseUrl = "https://api.twitter.com/1.1";

	public static String getApiUrl(String endpoint) {
		return baseUrl + "/" + endpoint;
	}

	public static OkHttpClient client = new OkHttpClient();
	public static OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer(
			ParseTwitterUtils.getTwitter().getConsumerKey(),
			ParseTwitterUtils.getTwitter().getConsumerSecret()
	);


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
					.addQueryParameter("count", String.valueOf(25));
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
}

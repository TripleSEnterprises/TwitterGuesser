package com.codepath.apps.restclienttemplate;


import com.parse.Parse;
import com.parse.twitter.ParseTwitterUtils;

import java.io.IOException;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;

public class TwitterClient {
	String baseUrl = "https://api.twitter.com/1.1";

	String getApiUrl(String endpoint) {
		return baseUrl + "/" + endpoint;
	}

	OkHttpClient client = new OkHttpClient();
	OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer(
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
			Request request = new Request.Builder()
					.url(getApiUrl(String.format("status/user_timeline?user_id=%s", userId)))
					.build();
			Request signedRequest = (Request) consumer.sign(request).unwrap();
			client.newCall(signedRequest).enqueue(callback);
		} catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException e) {
			e.printStackTrace();
		}
	}
}

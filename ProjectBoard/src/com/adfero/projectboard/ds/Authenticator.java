package com.adfero.projectboard.ds;

import org.apache.http.HttpRequest;

public interface Authenticator {
	public long getAccountID();
	public void addAuthorizationHeaders(HttpRequest request);
}

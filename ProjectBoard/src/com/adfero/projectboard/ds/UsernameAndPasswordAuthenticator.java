package com.adfero.projectboard.ds;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import org.apache.http.HttpRequest;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;
import org.json.JSONException;
import org.json.JSONObject;
import com.adfero.projectboard.R;
import android.content.res.Resources;

public class UsernameAndPasswordAuthenticator implements Authenticator {
	private long accountID;
	private String username;
	private String password;
	private String userInfo;
	
	public UsernameAndPasswordAuthenticator(Resources resources) throws IOException, JSONException {
		InputStream inStream = resources.openRawResource(R.raw.credentials);
		Reader in = new InputStreamReader(inStream, "UTF-8");
		int i = 0;
		char[] buffer = new char[256];
		StringBuilder sb = new StringBuilder();
		while((i = in.read(buffer)) > 0) {
			sb.append(buffer,0,i);
		}
		JSONObject data = new JSONObject(sb.toString());
		this.accountID = data.getLong("accountID");
		this.username = data.getString("username");
		this.password = data.getString("password");
		this.userInfo = data.getString("userinfo");
	}

	@Override
	public long getAccountID() {
		return this.accountID;
	}

	@Override
	public void addAuthorizationHeaders(HttpRequest request) {
		request.setHeader("User-info",this.userInfo);
		request.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(this.username,this.password), "UTF-8", false));
	}

}

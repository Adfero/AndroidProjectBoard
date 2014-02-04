package com.adfero.projectboard.ds;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.adfero.projectboard.model.Project;
import com.adfero.projectboard.model.TodoList;
import android.os.AsyncTask;

public class BasecampDS extends AsyncTask<Void, Void, Void> {
	private BasecampDSListener listener;
	private Authenticator auth;
	private Exception exception;
	private List<Project> projects;
	private List<Project> cachedProjects;
	private File cacheDir;
	
	public BasecampDS(File cacheDir) {
		this.cacheDir = cacheDir;
	}
	
	public void setAuthenticator(Authenticator auth) {
		this.auth = auth;
	}
	
	public void setBasecampDCListenr(BasecampDSListener l) {
		this.listener = l;
	}
	
	public Exception getException() {
		return this.exception;
	}
	
	public List<Project> getProjects() {
		return this.projects;
	}
	
	public List<Project> getCachedProjects() {
		return this.cachedProjects;
	}
	
	public File getCacheFile() {
		return new File(this.cacheDir,"projects.cache");
	}

	@Override
	protected Void doInBackground(Void... arg0) {
		try {
			this.readCache();
			this.publishProgress();
			this.loadProjects();
			this.loadTodoListsForAllProjects();
			Collections.sort(this.projects);
			this.writeCache();
		} catch (Exception e) {
			this.exception = e;
		}
		return null;
	}
	
	@Override
    protected void onPostExecute(Void result) {
		if (this.listener != null) {
			if (this.exception != null) {
				this.listener.encounteredException(this);
			} else {
				this.listener.dataLoaded(this);
			}
		}
	}
	
	@Override
	protected void onProgressUpdate(Void... prog) {
		if (this.cachedProjects != null && this.listener != null) {
			this.listener.cacheDataLoaded(this);
		}
	}
	
	private void loadProjects() throws NoAuthenticatorException, IOException, JSONException {
		String projectJSON = this.loadContent("projects");
		JSONArray projectsArray = new JSONArray(projectJSON);
		this.projects = new ArrayList<Project>(projectsArray.length());
		for(int i=0;i<projectsArray.length();i++) {
			JSONObject projectData = projectsArray.getJSONObject(i);
			Project project = new Project(projectData);
			this.projects.add(project);
		}
	}
	
	private void loadTodoListsForAllProjects() throws NoAuthenticatorException, IOException, JSONException, ParseException {
		for(Project project : this.projects) {
			String todolistsJSON = this.loadContent(String.format("projects/%d/todolists", project.getID()));
			JSONArray todolistsArray = new JSONArray(todolistsJSON);
			for(int i=0;i<todolistsArray.length();i++) {
				int todoListID = todolistsArray.getJSONObject(i).getInt("id");
				String todoListJSON = this.loadContent(String.format("projects/%d/todolists/%d", project.getID(), todoListID));
				JSONObject todoListData = new JSONObject(todoListJSON);
				TodoList todolist = new TodoList(todoListData);
				project.getTodosLists().add(todolist);
			}
		}
	}
	
	private String loadContent(String endpoint) throws NoAuthenticatorException, IOException {
		HttpClient client = new DefaultHttpClient();
		String url = this.buildURLForEndpoint(endpoint);
		System.out.println(url);
		HttpGet get = new HttpGet(url);
		this.prepareRequest(get);
		HttpResponse response = client.execute(get);
		if (response.getStatusLine().getStatusCode() == 200) {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
				StringBuffer result = new StringBuffer();
				String line;
				while((line = reader.readLine()) != null) {
					result.append(line);
				}
				return result.toString();
			} else {
				throw new IOException("Bad server data.");
			}
		} else {
			throw new IOException("Bad server response code: " + response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
		}
	}
	
	private void prepareRequest(HttpRequest req) throws NoAuthenticatorException {
		if (this.auth != null) {
			this.auth.addAuthorizationHeaders(req);
		} else {
			throw new NoAuthenticatorException("No Authenticator setup!");
		}
	}
	
	private String buildURLForEndpoint(String ep) throws NoAuthenticatorException {
		if (this.auth != null) {
			return String.format("https://basecamp.com/%d/api/v1/%s.json", this.auth.getAccountID(), ep);
		} else {
			throw new NoAuthenticatorException("No Authenticator setup!");
		}
	}
	
	private void writeCache() {
		try {
			OutputStream file = new FileOutputStream(this.getCacheFile());
			OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(buffer);
			output.writeObject(this.projects);
			output.close();
			buffer.close();
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void readCache() {
		try {
			InputStream file = new FileInputStream(this.getCacheFile());
			InputStream buffer = new BufferedInputStream(file);
			ObjectInput input = new ObjectInputStream (buffer);
			this.cachedProjects = (List<Project>) input.readObject();
			input.close();
			buffer.close();
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public interface BasecampDSListener {
		public void dataLoaded(BasecampDS source);
		public void encounteredException(BasecampDS source);
		public void cacheDataLoaded(BasecampDS source);
	}
	
	public class NoAuthenticatorException extends Exception {
		private static final long serialVersionUID = 1L;
		public NoAuthenticatorException(String s) {
			super(s);
		}
	}
}

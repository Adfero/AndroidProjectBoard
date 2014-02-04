package com.adfero.projectboard.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;

public class Todo extends AbstractBascampObject implements Comparable<Todo> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String content;
	private Date dueDate;
	
	public Todo(JSONObject source) throws JSONException, ParseException {
		super(source);
		this.content = source.getString("content");
		if (source.has("due_at") && source.getString("due_at") != null && !source.getString("due_at").equals("null")) {
			String dueDateString = source.getString("due_at");
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
			this.dueDate = formatter.parse(dueDateString);
		}
	}

	public String getContent() {
		return content;
	}
	
	public Date getDueDate() {
		return this.dueDate;
	}

	@Override
	public int compareTo(Todo another) {
		if (this == another) {
			return 0;
		} else if (this.getDueDate() != null && another.getDueDate() != null) {
			return this.getDueDate().compareTo(another.getDueDate());
		} else if (this.getDueDate() != null) {
			return 1;
		} else if (another.getDueDate() != null) {
			return -1;
		} else {
			return 0;
		}
	}
	
	@Override
	public String toString() {
		return "    * " + this.getContent() + "\n";
	}
}

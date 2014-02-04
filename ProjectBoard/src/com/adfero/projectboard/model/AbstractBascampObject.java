package com.adfero.projectboard.model;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class AbstractBascampObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	
	public AbstractBascampObject(JSONObject source) throws JSONException {
		this.id = source.getInt("id");
	}
	
	public int getID() {
		return this.id;
	}
}

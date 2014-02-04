package com.adfero.projectboard.model;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TodoList extends AbstractBascampObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private List<Todo> todos;

	public TodoList(JSONObject source) throws JSONException, ParseException {
		super(source);
		this.name = source.getString("name");
		if (source.has("todos") && source.getJSONObject("todos").has("remaining")) {
			JSONArray todosData = source.getJSONObject("todos").getJSONArray("remaining");
			this.todos = new ArrayList<Todo>(todosData.length());
			for(int i=0;i<todosData.length();i++) {
				JSONObject todoData = todosData.getJSONObject(i);
				Todo todo = new Todo(todoData);
				this.todos.add(todo);
			}
			Collections.sort(this.todos);
		} else {
			this.todos = new ArrayList<Todo>();
		}
	}

	public String getName() {
		return name;
	}

	public List<Todo> getTodos() {
		return todos;
	}
	
	@Override
	public String toString() {
		String str = "  + "+this.getName()+"\n";
		for(Todo todo : this.getTodos()) {
			str += todo.toString();
		}
		return str;
	}
}

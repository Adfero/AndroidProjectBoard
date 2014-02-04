package com.adfero.projectboard.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public class Project extends AbstractBascampObject implements Comparable<Project> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private Collection<TodoList> todoLists;
	
	public Project(JSONObject source) throws JSONException {
		super(source);
		this.name = source.getString("name");
		this.todoLists = new LinkedList<TodoList>();
	}
	
	public String getName() {
		return this.name;
	}

	public Collection<TodoList> getTodosLists() {
		return todoLists;
	}

	public void setTodoLists(Collection<TodoList> todoLists) {
		this.todoLists = todoLists;
	}
	
	public List<Todo> getAllTodos() {
		List<Todo> todos = new ArrayList<Todo>();
		for(TodoList list : this.getTodosLists()) {
			todos.addAll(list.getTodos());
		}
		Collections.sort(todos);
		return todos;
	}
	
	public Todo getNextTodo() {
		List<Todo> all = this.getAllTodos();
		for(Todo t : all) {
			if (t.getDueDate() != null) {
				return t;
			}
		}
		return all.size() > 0 ? all.get(0) : null;
	}
	
	public Todo getLastTodo() {
		List<Todo> all = this.getAllTodos();
		return all.size() > 0 ? all.get(all.size()-1) : null;
	}
	
	@Override
	public String toString() {
		String str = "- "+this.getName()+"\n";
		for(TodoList list : this.getTodosLists()) {
			str += list.toString();
		}
		return str;
	}

	@Override
	public int compareTo(Project arg0) {
		return arg0.getAllTodos().size() - this.getAllTodos().size();
	}
}

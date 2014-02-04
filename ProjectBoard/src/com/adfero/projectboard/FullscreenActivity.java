package com.adfero.projectboard;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import com.adfero.projectboard.ds.BasecampDS;
import com.adfero.projectboard.ds.BasecampDS.BasecampDSListener;
import com.adfero.projectboard.ds.UsernameAndPasswordAuthenticator;
import com.adfero.projectboard.model.Project;
import com.adfero.projectboard.model.Todo;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.GridLayout.LayoutParams;
import android.widget.TextView;

public class FullscreenActivity extends Activity {
    private List<Project> projects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        loadProjects();
        setupBackgroundChanger();
    }
    
    private void loadProjects() {
    	try {
	    	BasecampDS ds = new BasecampDS(this.getCacheDir());
	        ds.setAuthenticator(new UsernameAndPasswordAuthenticator(this.getResources()));
	        ds.setBasecampDCListenr(new BasecampDSListener() {
				@Override
				public void dataLoaded(BasecampDS source) {
					projects = source.getProjects();
					drawProjects();
				}
				@Override
				public void encounteredException(BasecampDS source) {
					source.getException().printStackTrace();
				}
				@Override
				public void cacheDataLoaded(BasecampDS source) {
					projects = source.getCachedProjects();
					drawProjects();
				}
	        });
	        ds.execute();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    private void setupBackgroundChanger() {
    	final Handler h = new Handler();
    	final int wait = 60000;
    	final ColorGenerator gen = new ColorGenerator(this.getResources());
    	final View contentView = findViewById(R.id.fullscreen_content);
    	h.postDelayed(new Runnable() {
    		@Override
            public void run() {
    			contentView.setBackgroundColor(gen.currentColor());
    			h.postDelayed(this, wait);
    		}
    	},wait);
    	contentView.setBackgroundColor(gen.currentColor());
    }

    private void drawProjects() {
    	GridLayout layout = (GridLayout)this.findViewById(R.id.fullscreen_content);
    	LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	layout.removeAllViews();
    	SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d",Locale.US);
    	int margin = (int)this.getResources().getDimension(R.dimen.tile_margin);
    	int width = ((layout.getWidth() - (layout.getColumnCount() * 2 * margin)) / layout.getColumnCount());
    	int height = ((layout.getHeight()-(layout.getRowCount() * 2 * margin)) / layout.getRowCount());
    	int row = 0;
    	int col = 0;
    	for(Project project : this.projects) {
    		if (project.getAllTodos().size() > 0) {
	    		View projectView = inflater.inflate(R.layout.project_tile, null);
	    		TextView title = (TextView)projectView.findViewById(R.id.pt_title);
	    		TextView todos = (TextView)projectView.findViewById(R.id.pt_todos);
	    		TextView next = (TextView)projectView.findViewById(R.id.pt_next_todo_value);
	    		TextView last = (TextView)projectView.findViewById(R.id.pt_last_todo_value);
	    		
	    		title.setText(project.getName());
	    		todos.setText(String.valueOf(project.getAllTodos().size()));
	    		Todo nextTodo = project.getNextTodo();
	    		Todo lastTodo = project.getLastTodo();
	    		next.setText(nextTodo != null && nextTodo.getDueDate() != null ? formatter.format(nextTodo.getDueDate()) : "");
	    		last.setText(lastTodo != null && lastTodo.getDueDate() != null ? formatter.format(lastTodo.getDueDate()) : "");
	    	
	    		layout.addView(projectView);
	    		GridLayout.LayoutParams params = (LayoutParams) projectView.getLayoutParams();
	    		params.columnSpec = GridLayout.spec(col);
	    		params.rowSpec = GridLayout.spec(row);
	    		params.width = width;
	    		params.height = height;
	    		params.setMargins(margin, margin, margin, margin);
	    		projectView.setLayoutParams(params);
	    		
	    		col++;
	    		if (col >= layout.getColumnCount()) {
	    			col = 0;
	    			row++;
	    		}
    		}
    	}
    }
}

package com.google.ezflashcards;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	// method called when "View Project" button is clicked
	public void viewProject(View view)
	{
		// create intent for ViewProjectActivity
		Intent intent = new Intent(this, ViewProjectActivity.class);
		// start the activity 
		startActivity(intent);
	}
	
	// method called when "New Project" button is clicked
	public void newProject(View view)
	{
		// create intent for ViewProjectActivity
		Intent intent = new Intent(this, NewProjectActivity.class);
		// start the activity 
		startActivity(intent);
	}

}

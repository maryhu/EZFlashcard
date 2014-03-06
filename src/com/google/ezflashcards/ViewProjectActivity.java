package com.google.ezflashcards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;

public class ViewProjectActivity extends Activity {

	// reference to tableLayout to contain the buttons for quizzes
	private TableLayout buttonTableLayout;
	// arrayLists to contain the names and numbers of quizzes
	private ArrayList<String> projectList;
	private ArrayList<String> numberList;
	// arrayList for alphabetical display of quiz-name-buttons
	private ArrayList<String> sortedList;
	// variable to keep track of total number of quizzes
	private int quizNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_project);
		
		// find the Tablelayout 
		buttonTableLayout = (TableLayout)findViewById(R.id.buttonTableLayout);
		
		projectList=new ArrayList<String>();
		numberList=new ArrayList<String>();
	    
	    addButtons();
	}
	
	// method called when the "Menu" button is clicked
	// takes the user back to the menu
	public void goToMenu(View view)
	{
		// create intent for ViewProjectActivity
		Intent intent = new Intent(this, MainActivity.class);
		// start the activity 
		startActivity(intent);
	}
	
	// method called when the "New Project" button is clicked
	// takes the user back to the menu
	public void goToNewProject(View view)
	{
		// create intent for ViewProjectActivity
		Intent intent = new Intent(this, NewProjectActivity.class);
		// start the activity 
		startActivity(intent);
	}
	
	// method that adds a button for a new quiz to the activity	
	public void addButtons()
	{	           
		// get quizNumber and update it in SharedPreferences
		SharedPreferences genPref= ViewProjectActivity.this.getSharedPreferences("totalNumber", Context.MODE_PRIVATE);
		
		String quizNumberString = genPref.getString("quizNumber", "0");
		quizNumber = Integer.parseInt(quizNumberString);
		
		// retrieve items from "allProjects" sharedPrefences and store in ArrayList
		SharedPreferences quizPref= ViewProjectActivity.this.getSharedPreferences("allProjects", Context.MODE_PRIVATE);
		// use the getAll method and store all pairs in a map
		Map<String,?> keys = quizPref.getAll();
		
		for(Map.Entry<String,?> entry : keys.entrySet())
		{
			projectList.add(entry.getKey());
			numberList.add(entry.getValue().toString());          
		}
		
		// sort the ArrayList of names alphabetically and store in another ArrayList
		// when the button is clicked, event handling will find correct quizNumber in original projectList ArrayList
		sortedList = projectList;
		//sort the list
		Collections.sort(sortedList);
		
		// Get a reference to the LayoutInflater service so we can "inflate" new Buttons
		LayoutInflater inflater = (LayoutInflater)getSystemService (Context.LAYOUT_INFLATER_SERVICE);
		  
		System.out.println(quizNumber);
	      
	    // use for loop and "allProjects" sharedPrefences to create buttons
	    for (int i = 0; i < quizNumber; i++) 
	    {
	    	// new table row to contain the new button 
	    	TableRow currentTableRow = new TableRow(this);
	    	currentTableRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
	    	  
	    	// inflate the new button
	    	Button newButton = (Button)inflater.inflate(R.layout.view_button, null);
	    	// set text on button (using the alphabetical ArrayList)
	    	newButton.setText(sortedList.get(i));
	    	// set text colour
	    	newButton.setTextColor(Color.WHITE);
	    	// set text size
	    	newButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, 30); 
			// set background for button
	    	//newButton.setBackgroundResource(R.drawable.button);
	    	//newButton.setBackgroundColor(Color.TRANSPARENT);
	        // attach listener to the Button
	        newButton.setOnClickListener(new quizButtonListener());
	    	// add button to table row
			currentTableRow.addView(newButton);
				 
		    // add table row to TableLayout
		    buttonTableLayout.addView(currentTableRow);
	    }
	}

	// private listener inner-class for quiz Buttons
	private class quizButtonListener implements OnClickListener 
	{
	 @Override
	 public void onClick(View v) 
	 {
	     // pass selected Button to goToQuiz()
	     goToQuiz((Button) v);
	     }
	};

	 private void goToQuiz(Button quizButton) 
	 {
		 // store the quiz name in a variable
		 String quizName = quizButton.getText().toString();
		 
		 // retrieve items from "allProjects" sharedPrefences and store in ArrayList
		 // (retrieving again because more quizzes might have been added)
		 SharedPreferences quizPref= ViewProjectActivity.this.getSharedPreferences("allProjects", Context.MODE_PRIVATE);
		 // use the getAll method and store all pairs in a map
		 Map<String,?> keys = quizPref.getAll();
			
		 for(Map.Entry<String,?> entry : keys.entrySet())
		 {
			 projectList.add(entry.getKey());
			 numberList.add(entry.getValue().toString());       
		 }
			
		 // iterate through arrayList of names to find index for chosen quiz
		
		 int index = 0;
		 for (int i = 0; i< projectList.size(); i++)
		 {
			 if (projectList.get(i).equalsIgnoreCase(quizName))
			 {
				 index = i;
			 }
		 }
		
		 // use index to get corresponding quiz number in arrayList of quiz numbers
		 int currentQuizNumber = Integer.parseInt(numberList.get(index));
			
		 // pass quiz number as an intent extra (used to retrieve data for the quiz
		 // which is in a SharedPreference named with quiz number)
		 Intent startQuizIntent = new Intent(this, StartQuizActivity.class);
		 startQuizIntent.putExtra("number", currentQuizNumber);
		 startQuizIntent.putExtra("name", quizName);
		 // start the activity 
		 startActivity(startQuizIntent);
	 }
	 
	 // method called when "Delete All" button is pressed
	 public void deleteAll(View view)
	 {
		 // delete all values in current SharedPreferences
		 SharedPreferences quizPref= ViewProjectActivity.this.getSharedPreferences("allProjects", Context.MODE_PRIVATE);
		 SharedPreferences.Editor prefEditor= quizPref.edit();
		 
		 prefEditor.clear();
		 prefEditor.commit();
		 
		 // find current total quiz number
		 SharedPreferences genPref= ViewProjectActivity.this.getSharedPreferences("totalNumber", Context.MODE_PRIVATE);
		 String quizNumberString = genPref.getString("quizNumber", "0");
		 quizNumber = Integer.parseInt(quizNumberString);
		 
		 // debug
		 Button newPButton = (Button)findViewById(R.id.newProjectButton2);
		 newPButton.setText(""+quizNumber);
		 
		 // delete all SharedPreferences for individual quizzes
		 for (int i = 1; i <= quizNumber; i++)
		 {
			 newPButton.setText(""+i);
			 SharedPreferences deletePref= ViewProjectActivity.this.getSharedPreferences("quiz"+i, Context.MODE_PRIVATE);
			 SharedPreferences.Editor deletePrefEditor= deletePref.edit();
			 
			 deletePrefEditor.clear();
			 deletePrefEditor.commit();
		 }
		 
		 // set total quiz number to 0
		 SharedPreferences.Editor genPrefEditor= genPref.edit();
		 genPrefEditor.putString("quizNumber", "0");
		 genPrefEditor.commit();
		 
		 // refresh to show results
		 Intent intent = getIntent();
		 finish();
		 startActivity(intent);		
	 }
}

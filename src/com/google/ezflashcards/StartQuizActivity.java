package com.google.ezflashcards;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class StartQuizActivity extends Activity {
	
	// instance variables to keep track of current quiz
	private int quizNumber=1;
	private String quizName;
	// textView to display quiz name
	private TextView quizNameTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_quiz);
		
		// get reference to text view for displaying quiz name
		quizNameTextView = (TextView) findViewById(R.id.quizNameTextView);
		
		// get intent and get the intent extra 
		Intent intent = getIntent();
	    if (intent.hasExtra("number")) 
	    {
	    	quizNumber = intent.getExtras().getInt("number");
	    	}
	    if (intent.hasExtra("name")) 
	    {
	    	quizName = intent.getExtras().getString("name");
	    	}
	    
		// set text view as name of quiz
		quizNameTextView.setText(quizName);
	}
	
	// method called if "Menu" button clicked
	public void goToMenu(View view)
	{
		// create intent for ViewProjectActivity
		Intent intent = new Intent(this, MainActivity.class);
		// start the activity 
		startActivity(intent);
	}
	
	// method called if "Start Quiz" button clicked
	public void startQuiz(View view)
	{
		// pass quiz number as an intent extra (used to retrieve data for the quiz
		// which is in a SharedPreference named with quiz number)
		Intent startQuizIntent = new Intent(this, QuizActivity.class);
		startQuizIntent.putExtra("number", quizNumber);
		startQuizIntent.putExtra("name", quizName);
		// start the activity 
		startActivity(startQuizIntent);
	}
	
	// method called if "view quiz" button clicked
	public void viewQuiz(View view)
	{	
		// pass quiz number as an intent extra (used to retrieve data for the quiz
		// which is in a SharedPreference named with quiz number)
		Intent startQuizIntent = new Intent(this, ViewQuizContentActivity.class);
		startQuizIntent.putExtra("number", quizNumber);
		// start the activity 
		startActivity(startQuizIntent);
	}
	
	// method called if "Delete Quiz" button clicked
	public void deleteQuiz(View view)
	{
		 // delete quiz in general SharedPreferences
		 SharedPreferences quizPref= StartQuizActivity.this.getSharedPreferences("allProjects", Context.MODE_PRIVATE);
		 SharedPreferences.Editor prefEditor= quizPref.edit();
		 
		 prefEditor.remove(quizName);
		 prefEditor.commit();
		 
		// clear shared preferences for quiz
		 SharedPreferences deletePref= StartQuizActivity.this.getSharedPreferences("quiz"+quizNumber, Context.MODE_PRIVATE);
		 SharedPreferences.Editor deletePrefEditor= deletePref.edit();
		 
		 deletePrefEditor.clear();
		 deletePrefEditor.commit();
		 
		 // add deleted quiz number to SharedPrefences dedicated to previously deleted quiz numbers
		 // (used during creation of new quizzes)
		 SharedPreferences allDeletedPref= StartQuizActivity.this.getSharedPreferences("deleted", Context.MODE_PRIVATE);
		 SharedPreferences.Editor deletedPrefEditor= allDeletedPref.edit();
		 
		 deletedPrefEditor.putInt(""+quizNumber, quizNumber);
		 deletedPrefEditor.commit();
		 
		 // minus one from general shared preferences total number
		 SharedPreferences genPref= StartQuizActivity.this.getSharedPreferences("totalNumber", Context.MODE_PRIVATE);
		 SharedPreferences.Editor genPrefEditor= genPref.edit();
		 
		 String quizNumberString = genPref.getString("quizNumber", "0");
		 int newQuizNumber = Integer.parseInt(quizNumberString);
		 newQuizNumber--;
		 // update in SharedPreferences
		 genPrefEditor.putString("quizNumber", ""+newQuizNumber);
		 genPrefEditor.commit();
		 
		 // go back to ViewProjectActivity after quiz is deleted
		 Intent intent = new Intent(this, ViewProjectActivity.class);
		 // start the activity 
		 startActivity(intent);
	}

}

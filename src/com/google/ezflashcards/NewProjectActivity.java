package com.google.ezflashcards;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class NewProjectActivity extends Activity {

	// references to UI components to retrieve data entered by user
	private EditText questionEditText;
	private EditText answerEditText;

	// variable to keep track of current quiz number
	private int quizNumber;
	
	// arrayLists to keep track of question and answers for current quiz
	// until the user pressed "Done".
	// Then, values in arrays are transferred to the SharePreferences object
	// for current quiz.
	private ArrayList<String> questions;
	private ArrayList<String> answers;
	// ArrayList to check for duplicate name
	private ArrayList<String> checkName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_project);
		
		questionEditText = (EditText) findViewById(R.id.questionEditText); 
		answerEditText = (EditText) findViewById(R.id.answerEditText); 
		
		questions = new ArrayList<String>();
		answers = new ArrayList<String>();
		checkName = new ArrayList<String>();
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
	
	// method called when "Next" button is clicked
	public void nextQuestion(View view)
	{	
		// store question and answer values in current editTexts in the arrayLists
		questions.add(questionEditText.getText().toString());
		answers.add(answerEditText.getText().toString());
		
		// empty EditText fields, prepare for next entries
		questionEditText.setText("");
		answerEditText.setText("");
		
	}
	
	// method called when "Done" button is clicked
	public void createQuiz(View view)
	{
		// store (last pair of) question and answer values the arrayLists
		questions.add(questionEditText.getText().toString());
		answers.add(answerEditText.getText().toString());
		
		// get quizNumber and update it in SharedPreferences
		SharedPreferences genPref= NewProjectActivity.this.getSharedPreferences("totalNumber", Context.MODE_PRIVATE);
		SharedPreferences.Editor prefEditor= genPref.edit();
		
		String quizNumberString = genPref.getString("quizNumber", "0");
		quizNumber = Integer.parseInt(quizNumberString);
		quizNumber++;
		// update in SharedPreferences
		prefEditor.putString("quizNumber", ""+quizNumber);
		prefEditor.commit();
		
		// variable to find out whether current quiz should be assigned an "empty" quiz number from before
		int actualQuizNumber = quizNumber;
		
		// access SharedPreferences containing numbers of deleted quizzes
		 SharedPreferences allDeletedPref= NewProjectActivity.this.getSharedPreferences("deleted", Context.MODE_PRIVATE);
		// check if there are any available "empty" quizNumbers (from previously deleted quizzes)
		for (int i=1; i < quizNumber; i++)
		{
			if (allDeletedPref.contains(""+i))
			{
				// set current available quiz number as quiz number for the new quiz
				actualQuizNumber = i;
				
				// delete current available quiz number from SharedPreferences for deleted numbers
				SharedPreferences.Editor deletedPrefEditor= allDeletedPref.edit();
				deletedPrefEditor.remove(""+i);
				deletedPrefEditor.commit();
				// end for loop
				i = quizNumber;
			}
		}
		
		// set new quiz number
		quizNumber = actualQuizNumber;
		
		// make a new SharedPreferences file for current quiz
		// distinguish using quizNumber
		SharedPreferences pref = NewProjectActivity.this.getSharedPreferences("quiz"+quizNumber, Context.MODE_PRIVATE);
		
		// transfer all values in current arrayList to current SharedPreferences file
		SharedPreferences.Editor editor = pref.edit();

		for(int i=0; i<questions.size(); i++) // questions and answers will have same size
		{// input question & answer pairs, one by one
			editor.putString(questions.get(i),answers.get(i));
		}
		editor.commit();
		
		// pop-up dialog to get name for current quiz
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Creating quiz...");
		alert.setMessage("Please enter a name for this quiz.");

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);

		// put an "OK" button which the user can click to indicate they have inputed the name
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
		{
		public void onClick(DialogInterface dialog, int whichButton) 
		{
			// get value of user's input to create a button in ViewProject with quiz name
			String quizName = input.getText().toString();
			
			// to avoid duplicate name, add (1) to end of name if name already exists
			// first put all names in an ArrayList
			SharedPreferences checkPref= NewProjectActivity.this.getSharedPreferences("allProjects", Context.MODE_PRIVATE);
			// use the getAll method and store all pairs in a map
			Map<String,?> keys = checkPref.getAll();
				
			for(Map.Entry<String,?> entry : keys.entrySet())
			{
				checkName.add(entry.getKey());     
			}
			// check for duplicate
			for (int i = 0; i <checkName.size(); i++)
			{
				if (quizName.equalsIgnoreCase(checkName.get(i)))
				{
					quizName = quizName+"(1)";
				}
			}
			
			// empty EditText fields
			questionEditText.setText("");
			answerEditText.setText("");
			
			// add pair of quiz name and quiz number to dedicated sharedPreferences
			// for future references
			SharedPreferences quizPref= NewProjectActivity.this.getSharedPreferences("allProjects", Context.MODE_PRIVATE);
			SharedPreferences.Editor prefEditor= quizPref.edit();
			
			prefEditor.putString(quizName, ""+quizNumber);
			prefEditor.commit();

			// create intent to go view projects after creating the quiz
			Intent createQuizIntent = new Intent(NewProjectActivity.this, ViewProjectActivity.class);
			// start the activity 
			startActivity(createQuizIntent);
		  }
		});
		// show the alert dialog
		alert.show();
	}
}

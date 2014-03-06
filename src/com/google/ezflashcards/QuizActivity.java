package com.google.ezflashcards;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.media.SoundPool;
import android.media.AudioManager;

@SuppressWarnings("unused")
public class QuizActivity extends Activity 
{
	// int to keep track of quiz number
	private int quizNumber;
	// int to keep track of quiz name
	private String quizName;
	// String to keep track of how many are correct
	private int numCorrect;
	// int to keep track of current question
	private int questionNumber;
	// int to represent total number of questions in quiz (for score)
	private int totalQuestions;
	// final instance variables to determine what the buttons should do
	private final int QUESTION_MODE = 0;
	private final int ANSWER_MODE = 1;
	// variable to keep track of what mode the quiz is in
	private int mode=1;
	// arrayListto get current quiz content
	private ArrayList<String> questions;
	private ArrayList<String> answers;
	// variables for playing sound effects
    private SoundPool soundPool;
    private int right_sound_id;
    private int wrong_sound_id;
    // variable to keep track of whether to play sound or not
    private Boolean soundOn = true;
    
	// textView for either question or answer
	private TextView quizTextView;
	// textView to display question number
	private TextView questionNumberTextView;
	// textView to show if it is a question or answer
	private TextView stateTextView;
	// reference to layout for changing background
	private RelativeLayout quizLayout;
	// reference to buttons
	private Button correctButton; // also "See answer" during question
	private Button incorrectButton; // also "Menu" during question

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quiz);
		
		// reset all values
		questionNumber = 0;
		numCorrect = 0;
		
		// ArrayLists to contain quiz content
		questions = new ArrayList<String>();
		answers =new ArrayList<String>();
		
		// find the references to components
		quizTextView = (TextView)findViewById(R.id.quizTextView);
		stateTextView = (TextView)findViewById(R.id.stateTextView);
		questionNumberTextView = (TextView)findViewById(R.id.questionNumberTextView);
		correctButton = (Button)findViewById(R.id.correctButton);
		incorrectButton = (Button)findViewById(R.id.incorrectButton);
		quizLayout = (RelativeLayout)findViewById(R.id.quizLayout);
		
		// get intent and get the intent extra (the current quiz number) 
		Intent intent = getIntent();
	    if (intent.hasExtra("number")) 
	    {
	    	quizNumber = intent.getExtras().getInt("number");
	    }
	    if (intent.hasExtra("name")) 
	    {
	    	quizName = intent.getExtras().getString("name");
	    }
	    
	    // get all questions and answers, store in ArrayLists
		SharedPreferences currentPref= QuizActivity.this.getSharedPreferences("quiz"+quizNumber, Context.MODE_PRIVATE);
		// transfer all values in current arrayList to current SharedPreferences file
		// use the getAll method and store all pairs in a map
		Map<String,?> keys = currentPref.getAll();
		
		for(Map.Entry<String,?> entry : keys.entrySet())
		{
			questions.add(entry.getKey());
			answers.add(entry.getValue().toString());      
			}
		// set total number of questions in quiz
		totalQuestions = questions.size();
		
		// allow volume buttons to set the game volume
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		  
		// create a SoundPool object, and use it to load the two sound effects
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		right_sound_id = soundPool.load(this, R.raw.right, 1);
		wrong_sound_id = soundPool.load(this, R.raw.wrong, 1);
		
		// call method to display first question
		incorrectButton(incorrectButton);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		super.onCreateOptionsMenu(menu);
		
	     // add "Font Size" menu option as the 1st item
	     menu.add(Menu.NONE, Menu.FIRST, Menu.NONE, R.string.font_size_menu);
	     // add "Sound" menu option as the 2nd item
	     menu.add(Menu.NONE, Menu.FIRST+1, Menu.NONE, R.string.sound_menu);
		return true;
	}
	
	 // method called when an option in Settings menu is selected
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) 
	 {
	     
		 // if user chose "Font Size" menu
	     if (item.getItemId() == Menu.FIRST) 
	     {
	         // create AlertDialog Builder and set its title to "Font Size" string resource
	         AlertDialog.Builder choicesBuilder = new AlertDialog.Builder(this);
	         choicesBuilder.setTitle(R.string.font_size_menu);
	  
	         // read array of the possible choices ("20", "30", "40") from string-array resource
	         final String[] fontOptions = getResources().getStringArray(R.array.font_array);
	          
	         // add array of possible choices to the Dialog
	         choicesBuilder.setItems(R.array.font_array,
	                 new DialogInterface.OnClickListener()
	                 {
	                     public void onClick(DialogInterface dialog, int item)
	                     {
	                         // get choice and set as size for quizTextView
	                         int font = Integer.parseInt(fontOptions[item]);  
	                         quizTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, font); 
	                     }
	                 }
	         );
	         // create and display the dialog
	         AlertDialog choicesDialog = choicesBuilder.create();
	         choicesDialog.show();
	         return true;
	     }
	     // Otherwise, if they selected "Sound"
	     else if (item.getItemId() == Menu.FIRST+1) 
	     {
	         //handle sound on/off
	         // create AlertDialog Builder and set its title to "Sound" string resource
	         AlertDialog.Builder choicesBuilder = new AlertDialog.Builder(this);
	         choicesBuilder.setTitle(R.string.sound_menu);
	  
	         // read array of the possible choices ("On" or "Off") from string-array resource
	         final String[] fontOptions = getResources().getStringArray(R.array.sound_array);
	          
	         // add array of possible choices to the Dialog
	         choicesBuilder.setItems(R.array.sound_array,
	                 new DialogInterface.OnClickListener()
	                 {
	                     public void onClick(DialogInterface dialog, int item)
	                     {
	                         // get user choice
	                         String choice = fontOptions[item];
	                         // set "soundOn" as true
	                         if (choice.equalsIgnoreCase("On"))
	                         {
	                        	 soundOn = true;
	                         }
	                         // set "soundOn" as false
	                         else
	                         {
	                        	 soundOn = false;
	                         }
	                     }
	                 }
	         );
	         // create and display the dialog
	         AlertDialog choicesDialog = choicesBuilder.create();
	         choicesDialog.show();
	         return true;           
	     }
	      
	     // If the user did not pick any menu items
	     return false;
	 }
	
	// method called when "Correct" or "See Answer" button is clicked
	public void correctButton(View view)
	{
		// if currently Question Mode, show answer
		if (mode == QUESTION_MODE)
		{				
			// change header textView to show it is an answer
			stateTextView.setText("Answer");
			
			// change background for answer
			quizLayout.setBackgroundResource(R.drawable.redline2);
			
			// update TextView with answer to current question
			String answer = answers.get(questionNumber);
			quizTextView.setText(answer);
			
			// add one to question count
			questionNumber++;
			
			// change buttons to "Correct" & "Incorrect"
			correctButton.setText("Correct");
			incorrectButton.setText("Incorrect");
			
			// change mode to answer
			mode = ANSWER_MODE;
		}
		
		// if currently in Answer mode, tally score
		// also show next question
		else if (mode == ANSWER_MODE)
		{
			// check sound mode is ON
			if (soundOn)
			{
				// play sound effect for right answer
		    	 soundPool.play(right_sound_id, 1.0f, 1.0f, 1, 0, 1.0f);
			}
			
			// add one to score (since it is the correct button)
			numCorrect++;
			
			// show next question, update textView
			nextQuestion();
			
			// change buttons to "See Answer" & "Menu"
			correctButton.setText("See Answer");
			incorrectButton.setText("Menu");
			
			// change mode to question
			mode = QUESTION_MODE;
		}
	}
	
	// method called when "Incorrect" or "Menu" button is clicked
	public void incorrectButton(View view)
	{
		// if currently Question Mode, go to Menu
		if (mode == QUESTION_MODE)
		{	
			// get intent to go back to Menu
			Intent intent = new Intent(this, MainActivity.class);
			// start the activity 
			startActivity(intent);
		}
		
		// if currently in Answer mode, show next question
		else if (mode == ANSWER_MODE)
		{	
			// if questionNumber != 0
			// check sound mode is ON
			if (soundOn)
			{
				// make sure it is not initial quiz screen
				if (questionNumber !=0)
				{
					// play sound effect for wrong answer
					soundPool.play(wrong_sound_id, 1.0f, 1.0f, 1, 0, 1.0f);
		    	 }
			}
			
			// show next question
			nextQuestion();
			
			// change buttons to "See Answer" & "Menu"
			correctButton.setText("See Answer");
			incorrectButton.setText("Menu");
			
			// change mode to question
			mode = QUESTION_MODE;
		}
	}
	
	// method shows next question when called
	private void nextQuestion()
	{
		// check if all questions are answered
		if (questionNumber > (totalQuestions-1))
		{
			// display results to user in popup dialog
			String results = "You answered " + numCorrect + " question(s) correctly out of "
					+ totalQuestions + " questions.";
			
            // Create an Alert Dialog Builder
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            String title = "Finished quiz " + quizName + "!";
            builder.setTitle(title);
            builder.setMessage(results);
            builder.setCancelable(false);
            
            // Add "Ok" Button
            builder.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() 
            		{
                        public void onClick(DialogInterface dialog, int id) 
                        {
                			// go back to StartQuizActivity
                			Intent intent = new Intent(QuizActivity.this, StartQuizActivity.class);
                			intent.putExtra("number", quizNumber);
                			intent.putExtra("name", quizName);
                			// start the activity 
                			startActivity(intent);
                        }
                    });
             
            // Create AlertDialog from the Builder
            AlertDialog resetDialog = builder.create();
            resetDialog.show();
		}
		// if not all questions are answered
		else
		{
			// change background for question
			quizLayout.setBackgroundResource(R.drawable.redline);
			
			// change header textView to show it is a question
			stateTextView.setText("Question");
			
			// set textView for current question number
			String currentQuestion = "Number " + (questionNumber+1) + " out of " +totalQuestions;
			questionNumberTextView.setText(currentQuestion);
			// set textView as current question
			String question = questions.get(questionNumber);
			quizTextView.setText(question);
		}
	}
}

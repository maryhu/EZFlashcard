package com.google.ezflashcards;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;

public class ViewQuizContentActivity extends Activity {

	// reference to TableLayout
    private TableLayout quizContentTableLayout;
    // variable to keep track of current quiz number
    private int quizNumber;
    // ArrayLists to contain questions and answers for quiz
    private ArrayList<String> questions;
    private ArrayList<String> answers;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_quiz_content);
		
		questions = new ArrayList<String>();
		answers = new ArrayList<String>();
		
		quizContentTableLayout = (TableLayout) findViewById(R.id.viewContentTableLayout);
		
		// get quizNumber reference via intent extra
		Intent intent = getIntent();
	    if (intent.hasExtra("number")) 
	    {
	    	quizNumber = intent.getExtras().getInt("number");
	    }
		
		// get SharedPreferences file for current quiz
		// distinguish using quizNumber
		SharedPreferences currentPref= ViewQuizContentActivity.this.getSharedPreferences("quiz"+quizNumber, Context.MODE_PRIVATE);

		// use the getAll method and store all pairs in a map
		Map<String,?> keys = currentPref.getAll();
		
		for(Map.Entry<String,?> entry : keys.entrySet())
		{
			questions.add(entry.getKey());
			answers.add(entry.getValue().toString());      
		}
		
		String myText ="";
		  
		for (int row = -1; row < questions.size(); row++) 
		{
			// Get a reference to the next tableRow
			TableRow currentTableRow = new TableRow(this);
			// make the table row with two columns
			currentTableRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));

		    // place textViews in currentTableRow
		    for (int col = 0; col < 3; col++)
		    {
		    	TextView text = new TextView(this);  
		    	// question column
		    	if (col==0)
		    	{
		    		// if row == -1, set headers (titles) for column
		    		if (row==-1)
		    		{
		    			myText = "Question";
		    		}
					// else, set question for column
		    		else
		    		{
		    			myText = questions.get(row);
		    		}
		    	}  
		    	// separator column
		    	else if (col==1)
		    	{
		    		myText = "     ";
		    	}
		    	// answer column
		    	else if (col == 2)
		    	{
		    		// if row == -1, set headers (titles) for column
		    		if (row==-1)
		    		{
		    			myText = "Answer";
		    		}
		    		// else, set question for column
		    		else
		    		{
		    			myText = answers.get(row);
		    		}
		    	}
		    	// set textView with appropriate String
		    	text.setText(myText);  
		    	  
		    	// set text size
		    	text.setTextSize(TypedValue.COMPLEX_UNIT_PX, 30);
		    	// set text colour
		    	text.setTextColor(Color.WHITE);
		    	  
		    	if (row==-1)
		    	{
		    		// set title for column to be bold to distinguish
	    			text.setTypeface(null,Typeface.BOLD);
	    			  
	    			// set colour for headings
	    			text.setTextColor(getResources().getColor(R.color.title_colour));
		    	}
		    	currentTableRow.addView(text); 
		    }
		    // add row to TableLayout
		    quizContentTableLayout.addView(currentTableRow);
		}
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

}

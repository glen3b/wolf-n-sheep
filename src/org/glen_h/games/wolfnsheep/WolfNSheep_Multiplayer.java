/* Copyright [2011-2012] [Glen Husman & contributors]

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package org.glen_h.games.wolfnsheep;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is the non-main, multiplayer, wolf 'n sheep game activity.
 * @author Glen Husman
 */
public class WolfNSheep_Multiplayer extends android.app.Activity {
	
	/**
	 * Gets gameplay data.
	 * @param data_get The data to get (SHEARED_WOOL or WOOL)
	 * @param num_player_data The player to get the data for
	 * @return The data. -1 if invalid (default in switch).
	 */
	int getData(Data data_get, Integer num_player_data) {
        switch (data_get) {
            case WOOL:
            	return wool[num_player_data];
            case SHEARED_WOOL:
            	return sheared_wool[num_player_data];
            case PLAYERS:
            	return num_players;
            default:
            	return -1;
        	}
        }
	
	@Override
	public Object onRetainNonConfigurationInstance() {
	    final int[] wool_saved = wool;
	    final int[] sheared_wool_saved = sheared_wool;
	    final int shear_visible = shear.getVisibility();
	    final int wolf_visible = wolf.getVisibility();
	    final CharSequence random_num_tosave = text.getText();
	    final int grow_visible = grow.getVisibility();
	    final int swap_visible = swap.getVisibility();
	    final int rolled_color = text.getCurrentTextColor();
	    final CharSequence log_text_content = logtext.getText();
	    Bundle data = new Bundle();
	    data.putIntArray("wool", wool_saved);
	    data.putCharSequence("lastmoves", log_text_content);
	    data.putIntArray("sheared_wool", sheared_wool_saved);
	    data.putCharSequence("randomnum", random_num_tosave);
	    data.putInt("shear_visible", shear_visible);
	    data.putInt("wolf_visible", wolf_visible);
	    data.putInt("grow_visible", grow_visible);
	    data.putInt("text_color", rolled_color);
	    data.putInt("swap_visible", swap_visible);
	    return data;
	}
	
	/**
	 * Sets gameplay data as specified by {@code data} and {@code data_set}.
	 * @param data_set The data to set to
	 * @param num_player_data The player's data to change
	 * @param data What data to set
	 */
	void setData(Data data_set, Integer num_player_data, Integer data) {
		switch (data_set) {
            case WOOL:
            	wool[num_player_data] = data;
            	break;
            case SHEARED_WOOL:
            	sheared_wool[num_player_data] = data;
            	break;
            case PLAYERS:
            	setData(Data.PLAYERS, data);
            	break;
            default:
            	break;
        	}
        }
        
	/**
	 * Sets non-player-specific gameplay data as specified by {@code data} and {@code data_set}.
	 * @param data_set The data to set to
	 * @param data What data to set
	 */
	void setData(Data data_set, Integer data){
		switch (data_set) {
        case PLAYERS:
        	num_players = data;
        	break;
        default:
        	break;
    	}
	}
	
	@Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
		android.view.MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    

	/**
	Warns the user that multiplayer is unstable.
	Make sure to delete/deprecate this method when multiplayer is finished.
	@author Glen Husman
	*/
	/*
	private void multiplayerUnstableToast(){
		Toast.makeText(getBaseContext(), "This is highly unstable and not ready for use!!", Toast.LENGTH_LONG).show();
	}
	*/
	
	private String about_dialog_text = "Wolf 'N Sheep 1.3 - http://code.google.com/p/wolf-n-sheep -" +
			" Wolf 'N Sheep release 1.3. An android game inspired by wild wool. Soon to have multiplayer support. " +
			"Icon is based off of http://en.wikipedia.org/wiki/File:Sheep_icon_05.svg, and under the public domain " +
			"(you can copy, modify, distribute and perform the work, even for commercial purposes, all without asking permission). " +
			"Please note that this applies ONLY to the application icon, not the code. The code is licensed under the apache license 2.0, available at " +
			"http://www.apache.org/licenses/LICENSE-2.0";
	
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        Integer item_id = item.getItemId();
    	switch (item_id) {
        /*
    	case R.id.multiplayer:
     	   Intent mp = new Intent(this, WolfNSheep_Multiplayer.class);
     	   startActivity(mp);
           multiplayerUnstableToast();
           return true;
           */
        case R.id.exit:
        	finish();
        	return true;
        case R.id.about:
        	AlertDialog about = LinkAlertDialog.create(this,"About",about_dialog_text,"OK");
        	about.show();
        	return true;
        }
        return false;
    }
	
	  private Integer random_number = 0;
	  private Button roll;

	  private int wool[] = new int[5];
	  private int sheared_wool[] = new int[5];
	  private int player_num;
	  private TextView logtext;
	  private int num_players = 4;
	  private String TAG = "WolfNSheep_Main";
	  private int total_wool;
      private final int max_wool = 5;
      private final int max_total_wool = 25;
      private Button shear;
      private Button wolf;
      private Button grow;
      private Button swap;
      // TODONE Fix dice (Also fix onClick functions for the dice)
	  private String[] messages = 
	        { "Roll the die!",
      		"Shear sheep or grow wool.",
      		"Swap sheep or grow wool.",
      		"Send wolf or shear sheep.",
      		"Send wolf or swap sheep.", 
      		"Grow wool.",
      		"Grow 2 wool."
      		}
      ;
      
	private TextView text;
	private TextView p1_wool_text;
	private TextView p2_wool_text;
	private TextView p3_wool_text;
	private TextView p4_wool_text;
		
	/** Called when the activity is first created.
	 * Initializes the TextViews from XML, the roll button, and the player buttons.
	 * @author Glen Husman & Matt Husmam */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // FIXED Computer Rolls
        // FIXED Die entries incorrect (see messages variable declaration)
        /*
         * OK, let's find out what to do for 1.3 and beyond, lets fix the TODOs,
         * add an about dialog (concerning legal issues such as the icon) DONE,
         * improve player logic, example randomize the order in which players are checked for swap or wolf "compatibility" DONE
         * add multiplayer (2.0 - 3.5 is goal version range)
         */
        // Note to self: Icon in based on icon public domain (we'll keep it in public domain), see http://en.wikipedia.org/wiki/File:Sheep_icon_05.svg (image based off of)
        final Bundle data_saved = (Bundle) getLastNonConfigurationInstance();
        for (player_num=1; player_num <= num_players; player_num++) {
        	wool[player_num] = 0;
        	sheared_wool[player_num] = 0;
        }
        // total_wool = player_wool + p2_wool + p3_wool + p4_wool;
        total_wool = 0;
        for (player_num=1; player_num <= num_players; player_num++) {
        	total_wool = total_wool + wool[player_num] + sheared_wool[player_num];
        }

        this.setContentView(R.layout.main);
        this.p1_wool_text = (TextView)this.findViewById(R.id.p1_wool);
        this.p2_wool_text = (TextView)this.findViewById(R.id.p2_wool);
        this.logtext = (TextView) findViewById(R.id.computer_action_log);
        this.p3_wool_text = (TextView)this.findViewById(R.id.p3_wool);
        this.p4_wool_text = (TextView)this.findViewById(R.id.p4_wool);
        this.shear = (Button)this.findViewById(R.id.shear);
        this.wolf = (Button)this.findViewById(R.id.wolf);
        this.grow = (Button)this.findViewById(R.id.grow);
        this.swap = (Button)this.findViewById(R.id.swap);
        this.roll = (Button)this.findViewById(R.id.roll);
        this.text = (TextView)this.findViewById(R.id.text);
        text.setTextSize(16);
        text.setTextColor(Color.GREEN);
        AlertDialog.Builder mp_alert = new AlertDialog.Builder(this);
        mp_alert.setMessage("Multiplayer or single-player?");
        mp_alert.setPositiveButton("Single player",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// This just continues single-player
					}
				});
        mp_alert.setNeutralButton("Multiplayer",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// TODO Launch multiplayer here
					}
				});
        AlertDialog mp_alert_showable = mp_alert.create();
        if(data_saved != null){
            final int[] wool_saved = data_saved.getIntArray("wool");
            final int[] sheared_wool_saved = data_saved.getIntArray("sheared_wool");
            final CharSequence log_saved = data_saved.getCharSequence("lastmoves");
    	    final int shear_visible = data_saved.getInt("shear_visible");
    	    final int wolf_visible = data_saved.getInt("wolf_visible");
    	    final int text_color_saved = data_saved.getInt("text_color");
    	    final int grow_visible = data_saved.getInt("grow_visible");
    	    final CharSequence random_num_saved = data_saved.getCharSequence("randomnum");
    	    final int swap_visible = data_saved.getInt("swap_visible");
            wool = wool_saved;
            shear.setVisibility(shear_visible);
            wolf.setVisibility(wolf_visible);
            grow.setVisibility(grow_visible);
            swap.setVisibility(swap_visible);
            text.setTextColor(text_color_saved);
            text.setText(random_num_saved);
            sheared_wool = sheared_wool_saved;
            logtext.setText(log_saved);
        }else{
        	mp_alert_showable.show();
        }
		updateTextOnly();
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		final AlertDialog.Builder alert2 = new AlertDialog.Builder(this);
		final AlertDialog.Builder alert3 = new AlertDialog.Builder(this);
		final AlertDialog.Builder alert4 = new AlertDialog.Builder(this);
		p1_wool_text.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	alert.setTitle("Player info");
        		alert.setMessage("Your wool: "+getData(Data.WOOL, 1)+"\n"+"Your sheared wool: "+getData(Data.SHEARED_WOOL, 1));
        		alert.setNeutralButton("OK",
        				new DialogInterface.OnClickListener() {
        					public void onClick(DialogInterface dialog, int whichButton) {
        					}
        				});
        		alert.show();
              }
            });
		p2_wool_text.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	alert2.setTitle("Player info");
            	alert2.setMessage("P2's wool: "+getData(Data.WOOL, 2)+"\n"+"P2's sheared wool: "+getData(Data.SHEARED_WOOL, 2));
            	alert2.setNeutralButton("OK",
        				new DialogInterface.OnClickListener() {
        					public void onClick(DialogInterface dialog, int whichButton) {
        					}
        				});
            	alert2.show();
              }
            });
		p3_wool_text.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	alert3.setTitle("Player info");
            	alert3.setMessage("P3's wool: "+getData(Data.WOOL, 3)+"\n"+"P3's sheared wool: "+getData(Data.SHEARED_WOOL, 3));
            	alert3.setNeutralButton("OK",
        				new DialogInterface.OnClickListener() {
        					public void onClick(DialogInterface dialog, int whichButton) {
        					}
        				});
            	alert3.show();
              }
            });
		p4_wool_text.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	alert4.setTitle("Player info");
            	alert4.setMessage("P4's wool: "+getData(Data.WOOL, 4)+"\n"+"P4's sheared wool: "+getData(Data.SHEARED_WOOL, 4));
            	alert4.setNeutralButton("OK",
        				new DialogInterface.OnClickListener() {
        					public void onClick(DialogInterface dialog, int whichButton) {
        					}
        				});
            	alert4.show();
              }
            });
        // text.setText(getResources().getString(R.string.message));
		init_app();
        OnClickListener roll_action = new OnClickListener() {
            public void onClick(View v) {
            	init_app();
                text.setTextColor(Color.YELLOW);
            	if(shear.getVisibility() == View.GONE && wolf.getVisibility() == View.GONE && grow.getVisibility() == View.GONE && swap.getVisibility() == View.GONE){
            	random_number = randomNumber(1, 6);
            	Log.i(TAG, "Player (P1) rolled number "+random_number.toString()+" on the die, also known as a '"+messages[random_number]+"'");
            	makeInvisible();
                text.setText(messages[random_number]);
                roll();
            	}
            	else{
            	    Toast.makeText(getBaseContext(), "No re-rolls!", Toast.LENGTH_LONG).show();  
            	}
            	checkIfGameOver();
              }
            };
        this.roll.setOnClickListener(roll_action);
        }
    private void init_app() {
    	final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Player selection");
		alert.setMessage("You have "+Integer.toString(wool[1])+" wool.\nWho would you like to swap sheep with?");
		alert.setPositiveButton("P2 ("+Integer.toString(wool[2])+" wool)", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
            	swap(2);
            	text.setText("You swapped with P2! Roll again!");
        		otherplayerrolls();
			}
		});

		alert.setNeutralButton("P3 ("+Integer.toString(wool[3])+" wool)",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
                    	swap(3);
                    	text.setText("You swapped with P3! Roll again!");
                		otherplayerrolls();
					}
				});
		alert.setNegativeButton("P4 ("+Integer.toString(wool[4])+" wool)",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
                    	swap(4);
                    	text.setText("You swapped with P4! Roll again!");
                		otherplayerrolls();
					}
				});
				
		this.swap.setOnClickListener(new OnClickListener() {	
			public void onClick(View v) {
				makeInvisible();
        		alert.show();
            }
        }
          );
        this.grow.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	wool[1] = wool[1] + 1;
            	if(wool[1] > max_wool){
                	wool[1] = max_wool;
                	Toast.makeText(getBaseContext(), "Cannot have more than "+Integer.toString(max_wool)+" wool on your sheep! Please roll again!", Toast.LENGTH_LONG).show();
                }
            	text.setText("You grew! Roll again!");
            	updateTextOnly();
            	makeInvisible();
        		otherplayerrolls();

            }
          });
        final AlertDialog.Builder wolf_alert = new AlertDialog.Builder(this);
        wolf_alert.setTitle("Player selection");
        wolf_alert.setMessage("You have "+Integer.toString(wool[1])+" wool.\nWho would you like to send the wolf to?");
        wolf_alert.setPositiveButton("P2 ("+Integer.toString(getData(Data.WOOL, 2))+" wool)", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				wool[2] = 0;
				text.setText("You wolfed P2! Roll again!");
				otherplayerrolls();
			}
		});

        wolf_alert.setNeutralButton("P3 ("+Integer.toString(getData(Data.WOOL, 3))+" wool)",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
                    	wool[3] = 0;
                    	text.setText("You wolfed P3! Roll again!");
                    	otherplayerrolls();
					}
				});
        wolf_alert.setNegativeButton("P4 ("+Integer.toString(getData(Data.WOOL, 4))+" wool)",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
                    	wool[4] = 0;
                    	text.setText("You wolfed P4! Roll again!");
                    	otherplayerrolls();
					}
				});
        this.wolf.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
            	makeInvisible();
        		wolf_alert.show();
        		
        	}
          });
		
	}

	/**
     * Checks whether the game is over, and, if so, returns {@code true} and performs the necessary game actions.
     * @author Glen Husman & Matt Husman
     * @return Whether the game is over or not
     */
	protected boolean checkIfGameOver() {
		boolean gameover;
		total_wool = wool[1] + wool[2] + wool[3] + wool[4] + sheared_wool[1] + sheared_wool[2] + sheared_wool[3] + sheared_wool[4];
		if(total_wool >= max_total_wool){
			// Game over!
			text.setText("Game over!");
			text.setTextColor(Color.GREEN);
			shearWool(1);
        	shearWool(2);
        	shearWool(3);
			shearWool(4);
			roll.setVisibility(View.VISIBLE);
			makeInvisible();
			roll.setText("Restart");
			roll.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
				    Intent intent = getIntent();
				    overridePendingTransition(0, 0);
				    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				    finish();
				    overridePendingTransition(0, 0);
				    startActivity(intent);
				}});
			final String tie_text = "Tie";
			Button share = (Button) findViewById(R.id.share);
			int winning_score = -1;
			String winner = "Nobody";
			TextView winner_text = (TextView)this.findViewById(R.id.winner);
			int winner_player_num = 0;
			// TODONE Use arrays everywhere, so this will work!!!
			for (player_num=1; player_num <= num_players; player_num++) {
				if ((wool[player_num]+sheared_wool[player_num]) == winning_score) {
					winner = tie_text;
					winner_player_num = 0;
				} else if ((wool[player_num]+sheared_wool[player_num]) > winning_score) {
					winner = "P"+Integer.toString(player_num);
					winner_player_num = player_num;
					winning_score = wool[player_num]+sheared_wool[player_num];
				}
			}
			winner_text.setVisibility(View.VISIBLE);
			if(winner == tie_text){
				winner_text.setText("Tie!!");
				Toast.makeText(getBaseContext(), "Game over! Tie!!", Toast.LENGTH_LONG).show();
			}
			else{
			winner_text.setText(winner+" wins!!");
			Toast.makeText(getBaseContext(), "Game over! Congratulations, "+winner+"!", Toast.LENGTH_LONG).show();
			}
			final int winner_final = winner_player_num;
			share.setVisibility(View.VISIBLE);
			share.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					String share_text;
					if(winner_final == 1){
						share_text = "I got a winning high score of "+Integer.toString(sheared_wool[1])+" wool on wolf 'n sheep. Think you can beat it?";
					}else{
						share_text = "I got a high score of "+Integer.toString(sheared_wool[1])+" wool on wolf 'n sheep. Think you can beat it?";
					}
					Intent sharingIntent = new Intent();
					sharingIntent.setAction(Intent.ACTION_SEND);
					sharingIntent.putExtra(Intent.EXTRA_TEXT, share_text);
					sharingIntent.setType("text/plain");
				    startActivity(Intent.createChooser(sharingIntent,"Share high score using"));
				}});
			updateTextOnly();
			gameover =  true;
		}		
		else{
			// Game-in-progress.
			gameover =  false;
		}
		return gameover;
	}
 
	/**
	 * Clear all visible buttons (wolf, grow, shear, and swap).
	 * Changes the visibility states of the action buttons to View.GONE.
	 */
	private void makeInvisible() {
		// Clear all visible buttons
		wolf.setVisibility(View.GONE);
		grow.setVisibility(View.GONE);
		shear.setVisibility(View.GONE);
		swap.setVisibility(View.GONE);
	}
	
	/**
	 * Do the actual roll. Uses the {@code random_number} integer and performs moves based on that.
	 * @author Glen Husman & Matt Husman
	 */
	protected void roll() {
		if(getData(Data.WOOL, 1) >= max_wool){
        	/** TODO Have an option to enable "special" features not in standard ruleset, like this
        	 * Commented because it is not standard rules, can easily make a preference for enabling this (and other modifications).
        	 * Uncommented because CPU does it
        	 */
    		shearWool(1);
        	Toast.makeText(getBaseContext(), "Auto-sheared a full sheep!", Toast.LENGTH_SHORT).show();
        	updateTextOnly();
			//Toast.makeText(getBaseContext(), "Cannot have more than "+Integer.toString(max_wool)+" wool on your sheep!", Toast.LENGTH_LONG).show();
        }
		if(random_number == 6){
    		wool[1] += 2;
    		if (wool[1] > max_wool) {
    			wool[1] = max_wool;
    		}
    		updateTextOnly();
    		text.setText("You grew 2! Roll again!");
    		otherplayerrolls();
    	}
    	else if(random_number == 5){
    		wool[1]++;
    		updateTextOnly();
    		text.setText("You grew! Roll again!");
    		otherplayerrolls();
    	}
    	else if(random_number == 4){
    		wolf.setVisibility(View.VISIBLE);
    		swap.setVisibility(View.VISIBLE);
    	}
    	else if(random_number == 3){
    		wolf.setVisibility(View.VISIBLE);
    		shear.setVisibility(View.VISIBLE);
    	}
    	else if(random_number == 2){
    		swap.setVisibility(View.VISIBLE);
    		grow.setVisibility(View.VISIBLE);
    	}
    	else if(random_number == 1){
    		shear.setVisibility(View.VISIBLE);
    		grow.setVisibility(View.VISIBLE);
    	}
        this.shear.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	shearWool(1);
            	makeInvisible();
        		otherplayerrolls();
            }
          });
        
        updateText();
		checkIfGameOver();
	}

	protected void otherplayerrolls() {
		int random_number_p2 = randomNumber(1, 6);
		String p2logtext = "Computer 2 (P2) rolled number "+Integer.toString(random_number_p2)+" on the die, also known as a '"+messages[random_number_p2]+"'";
		Log.i(TAG, p2logtext);
		int random_number_p3 = randomNumber(1, 6);
		String p3logtext = "Computer 3 (P3) rolled number "+Integer.toString(random_number_p3)+" on the die, also known as a '"+messages[random_number_p3]+"'";
		Log.i(TAG, p3logtext);
		int random_number_p4 = randomNumber(1, 6);
		String p4logtext = "Computer 4 (P4) rolled number "+Integer.toString(random_number_p4)+" on the die, also known as a '"+messages[random_number_p4]+"'";
		Log.i(TAG, p4logtext);
		String p2did = p_action(2, random_number_p2);
		String p3did = p_action(3, random_number_p3);
		String p4did = p_action(4, random_number_p4);
		logtext.setText(p2did+"\n"+p3did+"\n"+p4did);
		updateTextOnly();
	}
	
	/**
	 * Generate a random {@code int} between {@code Min} and {@code Max}.
	 * @param Min
	 * 	The minimum integer.
	 * @param Max
	 * 	The maximum integer.
	 * @author Glen Husman
	 */
	protected int randomNumber(int Min, int Max){
		  int randomNum = Min + (int)(Math.random() * ((Max - Min) + 1));
		  return randomNum;
	}
	
	private void updateTextOnly() {
		/** Now uses table format */
		p1_wool_text.setText(Integer.toString(getData(Data.WOOL, 1))+"/"+Integer.toString(getData(Data.SHEARED_WOOL, 1)));
	    p2_wool_text.setText(Integer.toString(getData(Data.WOOL, 2))+"/"+Integer.toString(getData(Data.SHEARED_WOOL, 2)));
	    p3_wool_text.setText(Integer.toString(getData(Data.WOOL, 3))+"/"+Integer.toString(getData(Data.SHEARED_WOOL, 3)));
	    p4_wool_text.setText(Integer.toString(getData(Data.WOOL, 4))+"/"+Integer.toString(getData(Data.SHEARED_WOOL, 4)));
		/*
		p1_wool_text.setText("Your wool: "+Integer.toString(getData(Data.WOOL, 1))+" Your sheared wool: "+Integer.toString(getData(Data.SHEARED_WOOL, 1)));
	    p2_wool_text.setText("P2 wool: "+Integer.toString(getData(Data.WOOL, 2))+" P2 sheared wool: "+Integer.toString(getData(Data.SHEARED_WOOL, 2)));
	    p3_wool_text.setText("P3 wool: "+Integer.toString(getData(Data.WOOL, 3))+" P3 sheared wool: "+Integer.toString(getData(Data.SHEARED_WOOL, 3)));
	    p4_wool_text.setText("P4 wool: "+Integer.toString(getData(Data.WOOL, 4))+" P4 sheared wool: "+Integer.toString(getData(Data.SHEARED_WOOL, 4)));
	    */
		total_wool = getData(Data.WOOL, 1) + getData(Data.WOOL, 2) + getData(Data.WOOL, 3) + getData(Data.WOOL, 4) + getData(Data.SHEARED_WOOL, 1) + getData(Data.SHEARED_WOOL, 2) + getData(Data.SHEARED_WOOL, 3) + getData(Data.SHEARED_WOOL, 4);
	}

	/**
	 * Perform a CPU roll
	 * @author Matt Husman & Glen Husman
	 * @param roll The players roll
	 * @param num_player The player to play for
	 */
	protected String p_action(Integer num_player, Integer roll) {
		// TODONE Finish attempt to make a generic other "player action" (means finishing logic)

		String returnvalue = null;
		// If sheep is full at beginning of turn, "auto-shear"
		// TODO Have an option to enable "special" features not in standard ruleset, like this
		
		// Commented because it is not standard rules, can easily make a preference for this.
		// Uncommented because P1 does it
		
		if (wool[num_player] == max_wool) {
			shearWool(num_player);
		}
		
		switch (roll) {
		case 6:
			// Grow 2 wool
			wool[num_player] += 2;
			returnvalue = "P"+Integer.toString(num_player)+" grew 2.";
			break;
		case 5:
			// Grow wool
			/**
			int player_wolf = 0;
			for(int players_checked = 1;players_checked <= 4;players_checked++){
					if(wool[players_checked] >= 4 && players_checked != num_player){
						player_wolf = players_checked;
					}
				  }
			// TODONE Eventually: if opponent has 4-5 wool, wolf him; for now, just grow - we could use a loop for this
			if(player_wolf > 0 && player_wolf < 5){
				wool[player_wolf] = 0;
				returnvalue = "P"+Integer.toString(num_player)+" wolfed P"+Integer.toString(player_wolf)+".";
				}
			else{
				wool[num_player]++;
				returnvalue = "P"+Integer.toString(num_player)+" grew.";
			}
			*/
			wool[num_player]++;
			returnvalue = "P"+Integer.toString(num_player)+" grew.";
			updateTextOnly();
			break;
		case 4:
			// Send wolf or swap sheep
			List<Integer> list = new LinkedList<Integer>();
			for (int addi = 1; addi <= 4; addi++) {
			    list.add(addi);
			}
			Collections.shuffle(list);
			Integer[] random = new Integer[]{0,0,0,0};
			random[0] = list.remove(0);
			random[1] = list.remove(0);
			random[2] = list.remove(0);
			random[3] = list.remove(0);
			int player_swap = 0;
			for(int checked = 0;checked <= 3;checked++){
				if(wool[random[checked]] >= (wool[num_player]+2) && random[checked] != num_player){
					player_swap = random[checked];
				}
			}
			int most_wool = -1;
			int who_most_wool = 0;
			List<Integer> list2 = new LinkedList<Integer>();
			for (int addi = 1; addi <= 4; addi++) {
			    list2.add(addi);
			}
			Collections.shuffle(list2);
			Integer[] random2 = new Integer[]{0,0,0,0};
			random2[0] = list2.remove(0);
			random2[1] = list2.remove(0);
			random2[2] = list2.remove(0);
			random2[3] = list2.remove(0);
			for (int checked = 0; checked <= 3; checked++) {
				if ((wool[random2[checked]]) > most_wool && random2[checked] != num_player) {
					who_most_wool = random2[checked];
					most_wool = wool[who_most_wool];
				}
			}
			if(player_swap != 0){
				final int player_swap_old_wool = wool[player_swap];
				final int num_player_old_wool = wool[num_player];
				wool[player_swap] = num_player_old_wool;
				wool[num_player] = player_swap_old_wool;
				returnvalue = "P"+Integer.toString(num_player)+" swapped with P"+Integer.toString(player_swap)+".";
			}else{
				wool[who_most_wool] = 0;
				returnvalue = "P"+Integer.toString(num_player)+" wolfed P"+Integer.toString(who_most_wool)+".";
			}
			break;
		case 3:
			// Wolf or shear
			// XXXX DAD: CHECK THIS CODE
			// TODONE Eventually: If I have 3+ wool, then shear; else if opponent has 2+ more than me, swap;
			// for now, just shear
			int player_wolf = 0;
			List<Integer> list3 = new LinkedList<Integer>();
			for (int addi = 1; addi <= 4; addi++) {
			    list3.add(addi);
			}
			Collections.shuffle(list3);
			Integer[] random3 = new Integer[]{0,0,0,0};
			random3[0] = list3.remove(0);
			random3[1] = list3.remove(0);
			random3[2] = list3.remove(0);
			random3[3] = list3.remove(0);
			for(int checked = 0;checked <= 3;checked++){
					if(wool[random3[checked]] >= 4 && random3[checked] != num_player){
						player_wolf = random3[checked];
					}
				  }
			// TODONE Eventually: if opponent has 4-5 wool, wolf him; for now, just grow - we could use a loop for this
			if(player_wolf > 0 && player_wolf < 5){
				wool[player_wolf] = 0;
				returnvalue = "P"+Integer.toString(num_player)+" wolfed P"+Integer.toString(player_wolf)+".";
				}
			else{
				shearWool(num_player);
				returnvalue = "P"+Integer.toString(num_player)+" sheared.";
			}
			break;
			// TODONE From hereon down, implement the new random order checking
		case 2:
			// Swap or grow
			// TODONE Eventually: if opponent has 2+ more than me, swap; for now, just grow
			int player_swap_alt = 0;
			List<Integer> list4 = new LinkedList<Integer>();
			for (int addi = 1; addi <= 4; addi++) {
				list4.add(addi);
			}
			Collections.shuffle(list4);
			Integer[] random4 = new Integer[]{0,0,0,0};
			random4[0] = list4.remove(0);
			random4[1] = list4.remove(0);
			random4[2] = list4.remove(0);
			random4[3] = list4.remove(0);
			for(int checked = 1;checked <= 3;checked++){
				if(wool[random4[checked]] >= (wool[random4[checked]]+2) && random4[checked] != num_player){
					player_swap_alt = random4[checked];
				}
			}
			if(player_swap_alt != 0){
				final int player_swap_old_wool = wool[player_swap_alt];
				final int num_player_old_wool = wool[num_player];
				wool[player_swap_alt] = num_player_old_wool;
				wool[num_player] = player_swap_old_wool;
				returnvalue = "P"+Integer.toString(num_player)+" swapped with P"+Integer.toString(player_swap_alt)+".";
			}else{
				wool[num_player]++;
				returnvalue = "P"+Integer.toString(num_player)+" grew.";
			}
			break;
		case 1:
			// Shear sheep or grow wool
			if(wool[num_player] >= 4){
				shearWool(num_player);
				returnvalue = "P"+Integer.toString(num_player)+" sheared.";
				}
			else{
				wool[num_player]++;
				returnvalue = "P"+Integer.toString(num_player)+" grew.";
			}
			break;
		}
		if (wool[num_player] > max_wool) {
			wool[num_player] = max_wool;
		}
		updateTextOnly();
		return returnvalue;
	}

	/**
	 * Shears P1's wool.
	 * @deprecated Use {@link shearWool(int)} instead.
	 * @author Glen Husman & Matt Husman
	 */
	protected void shearWool(){
        sheared_wool[1] = sheared_wool[1] + wool[1];
        wool[1] = 0;
    	updateTextOnly();
	}
	
	/**
	 * Shears the wool of a player.
	 * @author Glen Husman & Matt Husman
	 * @param num_player The player whose wool to shear
	 */
	protected void shearWool(int num_player){
		// TODONE Verify function works
		boolean autoshear = false;
		if(wool[1] >= 5){
			autoshear = true;
		}
		final int wool_old = wool[num_player];
		sheared_wool[num_player] += wool_old;
		wool[num_player] = 0;
		if(num_player == 1 && !autoshear){
			text.setText("You sheared! Roll again!");
		}
		updateTextOnly();
	}
	
	/**
	 * Update the TextView's text for all players wool.
	 */
	private void updateText(){
	updateTextOnly();
	checkIfGameOver();
	// Maybe use this code sometime
	
	/*
	switch (player) {
	case 2:
		p2_wool_text.setText("P2 wool: "+Integer.toString(wool[2])+" P2 sheared wool: "+Integer.toString(sheared_wool[2]));
		break;
	case 3:
		p3_wool_text.setText("P3 wool: "+Integer.toString(wool[3])+" P2 sheared wool: "+Integer.toString(sheared_wool[3]));
		break;
	case 4:
		p4_wool_text.setText("P4 wool: "+Integer.toString(wool[4])+" P2 sheared wool: "+Integer.toString(sheared_wool[4]));
		break;
	default:
	*/
	}
	
	/**
	 * Have P1 swap wool with {@code player}.
	 * @param player The player to swap wool with.
	 * @author Glen Husman & Matt Husman
	 */
	protected void swap(int player){
		// If we now have arrays working, we can do instead:
		final int temp_wool = wool[1];
		wool[1] = wool[player];
		wool[player] = temp_wool;
		// We might be able to make the "wool_text" into arrays eventually
		updateText();		
		}
	
	 public static class LinkAlertDialog {

		public static AlertDialog create(android.content.Context context, String title, String message_txt, String dismiss_text) {
		  final TextView message = new TextView(context);
		  // i.e.: R.string.dialog_message =>
		            // "Test this dialog following the link to dtmilano.blogspot.com"
		  final SpannableString s = 
		               new SpannableString(message_txt);
		  Linkify.addLinks(s, Linkify.WEB_URLS);
		  message.setText(s);
		  message.setMovementMethod(LinkMovementMethod.getInstance());

		  return new AlertDialog.Builder(context)
		   .setTitle(title)
		   .setCancelable(true)
		   .setIcon(android.R.drawable.ic_dialog_info)
		   .setPositiveButton(dismiss_text, null)
		   .setView(message)
		   .create();
		 }
		}

    }


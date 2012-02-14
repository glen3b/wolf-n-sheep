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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is the main, single-player, wolf 'n sheep game activity.
 * @author Glen Husman
 */
public class WolfNSheep_Main extends Activity {

	/**
	 * Gets gameplay data.
	 * @param data_get The data to get
	 * @param num_player_data The player to get the data for
	 * @return The data. -1 if invalid (default in switch).
	 */
	Integer getData(Data data_get, Integer num_player_data) {
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
	
	/**
	 * Gets gameplay data.
	 * @param data_get The data to get
	 * @param num_player_data The player to get the data for
	 * @return The data. -1 if invalid (default in switch).
	 */
	String getStringData(Data data_get, Integer num_player_data) {
        switch (data_get) {
            case LASTMOVE:
                StringBuilder p__did = new StringBuilder(players_did[num_player_data].replace("P"+num_player_data.toString()+" ", ""));
                p__did.setCharAt(0, Character.toUpperCase(p__did.charAt(0)));  
                return p__did.toString().replace(".", "");
            case LASTMOVE_FULL:
            	return players_did[num_player_data];
            case WOOL:
            	return getData(Data.WOOL, num_player_data).toString();
            case SHEARED_WOOL:
            	return getData(Data.SHEARED_WOOL, num_player_data).toString();
            case PLAYERS:
            	return getData(Data.PLAYERS, num_player_data).toString();
            default:
            	return null;
        	}
        }
	
	
	/**
	 * When the screen orientation changes, save all the vital data in a Bundle (when received, cast to Bundle from Object).
	 * @author Glen Husman
	 */
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
    public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    

	/**
	Warns the user that multiplayer is unstable.
	Make sure to delete/deprecate this method when multiplayer is finished.
	@author Glen Husman
	*/
	/*
	void multiplayerUnstableToast(){
		Toast.makeText(getBaseContext(), "This is highly unstable and not ready for use!!", Toast.LENGTH_LONG).show();
	}
	*/
	
	protected String version_name;
	protected String version_code;
	protected String about_dialog_text;
	
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int item_id = item.getItemId();
    	switch (item_id) {
    	case R.id.help:
    	   Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://code.google.com/p/wolf-n-sheep/wiki/GameHelp"));
    	   startActivity(browserIntent);
           return true;
    	case R.id.prefs_item:
      	   Intent prefs = new Intent(this, Extras.class);
      	   startActivity(prefs);
      	   return true;
        case R.id.exit:
        	finish();
        	return true;
        case R.id.about:
        	PackageManager manager = this.getPackageManager();
        	   PackageInfo info;
			try {
				info = manager.getPackageInfo(this.getPackageName(), 0);
			} catch (NameNotFoundException e) {
				info = null;
				Log.e(TAG, "Why was the package name not found?", e);
			}
			try{
			version_name = info.versionName;
			version_code = Integer.toString(info.versionCode);
			about_dialog_text = "Wolf 'N Sheep "+version_name+" - http://code.google.com/p/wolf-n-sheep -" +
					" Wolf 'N Sheep version "+version_name+". An android game inspired by wild wool. Soon to have multiplayer support. " +
					"Icon is based off of http://en.wikipedia.org/wiki/File:Sheep_icon_05.svg, and under the public domain " +
					"(you can copy, modify, distribute and perform the work, even for commercial purposes, all without asking permission). " +
					"Please note that this applies ONLY to the application icon, not the code. The code is licensed under the apache license 2.0, available at " +
					"http://www.apache.org/licenses/LICENSE-2.0";
			}catch (NullPointerException nullerror){
				Log.e(TAG, "Something was null here, probably info, while setting version_name, version_code, and about_dialog_text.", nullerror);
				version_name = "Unknown";
				version_code = "Unknown";
			}
        	AlertDialog about = LinkAlertDialog.create(this,"About",about_dialog_text,"OK");
        	about.show();
        	return true;
        }
        return false;
    }
	
	  private int random_number = 0;
	  private Button roll;

	  private int wool[] = new int[5];
	  private int sheared_wool[] = new int[5];
	  private int player_num;
	  private TextView logtext;
	  private int num_players = 4;
	  private static final String TAG = "WolfNSheep_Main";
	  private String[] players_did = {"","P1 is unknown, should refer to main text","None","None","None"};
	  private int total_wool;
      private static final int max_wool = 5;
      private static final int max_total_wool = 25;
      private Button shear;
      private Button wolf;
      private Button grow;
      private Button swap;
      // TODONE Fix dice (Also fix onClick functions for the dice)
	  public static final String[] messages = 
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
	private boolean autoshear_state;
	private boolean shearcosts_state;
	private boolean criticalalerts_state;
	protected PlayerMode mode;
		
	/** Called when the activity is first created.
	 * Initializes the TextViews from XML, the roll button, and the player buttons.
	 * @author Glen Husman & Matt Husmam */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // FIXED Computer Rolls
        // FIXED Die entries incorrect (see messages variable declaration)
        /*
         * OK, let's find out what to do for 1.5 and beyond, lets fix the TODOs,
         * add an about dialog (concerning legal issues such as the icon) DONE,
         * improve player logic DONE, example randomize the order in which players are checked for swap or wolf "compatibility" DONE
         * add multiplayer (2.0 - 3.5 is goal version range) WORKON
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
        // TODONE Implement auto-shear prefs checking here
        SharedPreferences settings = getSharedPreferences("extras", 0);
	    autoshear_state = settings.getBoolean("autoshear", true);
	    shearcosts_state = settings.getBoolean("shearcosts", false);
	    criticalalerts_state = settings.getBoolean("criticalalerts", true);
	    Log.d(TAG, "Auto-shear preference is "+Boolean.toString(autoshear_state));
	    Log.d(TAG, "Shear costs preference is "+Boolean.toString(shearcosts_state));
	    Log.d(TAG, "Critical alerts preference is "+Boolean.toString(criticalalerts_state));
        this.setContentView(R.layout.main);
        this.p1_wool_text = (TextView)this.findViewById(R.id.p1_wool);
        this.p2_wool_text = (TextView)this.findViewById(R.id.p2_wool);
        TextView p1_label = (TextView)this.findViewById(R.id.p1_label);
        TextView p2_label = (TextView)this.findViewById(R.id.p2_label);
        TextView p3_label = (TextView)this.findViewById(R.id.p3_label);
        TextView p4_label = (TextView)this.findViewById(R.id.p4_label);
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
						mode = PlayerMode.SINGLEPLAYER;
						init_app();
					}
				});
        mp_alert.setNeutralButton("Multiplayer",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// TODO Finish multiplayer!
						mode = PlayerMode.MULTIPLAYER;
			        	if(mode == PlayerMode.MULTIPLAYER){
			            	// TODO # of players selection dialog display
			            }
						// TODO Get this to do something (like select # of players)
						// init_app();
					}
				});
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
        }
        // TODO When ready, show dialog again
        /*
        else{
        	mp_alert.show();
        }
        */
		updateTextOnly();
		init_app();
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		final AlertDialog.Builder alert2 = new AlertDialog.Builder(this);
		final AlertDialog.Builder alert3 = new AlertDialog.Builder(this);
		final AlertDialog.Builder alert4 = new AlertDialog.Builder(this);
		OnClickListener p1_pinfo = new OnClickListener() {
            public void onClick(View v) {
            	alert.setTitle("Player info");
        		alert.setMessage("Your wool: "+getStringData(Data.WOOL, 1)+"\n"+"Your sheared wool: "+getStringData(Data.SHEARED_WOOL, 1));
        		alert.setNeutralButton("OK",
        				new DialogInterface.OnClickListener() {
        					public void onClick(DialogInterface dialog, int whichButton) {
        					}
        				});
        		alert.show();
              }
            };
        OnClickListener p2_pinfo = new OnClickListener() {
            public void onClick(View v) {
            	alert2.setTitle("Player info");
            	alert2.setMessage("P2's wool: "+getStringData(Data.WOOL, 2)+"\n"+"P2's sheared wool: "+getStringData(Data.SHEARED_WOOL, 2)+
            			"\n"+"P2's last move: "+getStringData(Data.LASTMOVE, 2));
            	alert2.setNeutralButton("OK",
        				new DialogInterface.OnClickListener() {
        					public void onClick(DialogInterface dialog, int whichButton) {
        					}
        				});
            	alert2.show();
              }
            };
        OnClickListener p3_pinfo = new OnClickListener() {
            public void onClick(View v) {
            	alert3.setTitle("Player info");
            	alert3.setMessage("P3's wool: "+getStringData(Data.WOOL, 3)+"\n"+"P3's sheared wool: "+getStringData(Data.SHEARED_WOOL, 3)+
            			"\n"+"P3's last move: "+getStringData(Data.LASTMOVE, 3));
            	alert3.setNeutralButton("OK",
        				new DialogInterface.OnClickListener() {
        					public void onClick(DialogInterface dialog, int whichButton) {
        					}
        				});
            	alert3.show();
              }
            };
        OnClickListener p4_pinfo = new OnClickListener() {
            public void onClick(View v) {
            	alert4.setTitle("Player info");
            	alert4.setMessage("P4's wool: "+getStringData(Data.WOOL, 4)+"\n"+"P4's sheared wool: "+getStringData(Data.SHEARED_WOOL, 4)+
            			"\n"+"P4's last move: "+getStringData(Data.LASTMOVE, 4));
            	alert4.setNeutralButton("OK",
        				new DialogInterface.OnClickListener() {
        					public void onClick(DialogInterface dialog, int whichButton) {
        					}
        				});
            	alert4.show();
              }
            };
		p1_label.setOnClickListener(p1_pinfo);
		p2_label.setOnClickListener(p2_pinfo);
		p3_label.setOnClickListener(p3_pinfo);
		p4_label.setOnClickListener(p4_pinfo);
        // text.setText(getResources().getString(R.string.message));
		init_app();
        OnClickListener roll_action = new OnClickListener() {
            public void onClick(View v) {
            	init_app();
                text.setTextColor(Color.YELLOW);
            	if(shear.getVisibility() == View.GONE && wolf.getVisibility() == View.GONE && grow.getVisibility() == View.GONE && swap.getVisibility() == View.GONE){
            	random_number = randomNumber(1, 6);
            	Log.i(TAG, "Player (P1) rolled number "+Integer.toString(random_number)+" on the die, also known as a '"+messages[random_number]+"'");
            	makeInvisible();
                text.setText(messages[random_number]);
                if(!checkIfGameOver()) roll();
            	}
            	else{
            	    Toast.makeText(getBaseContext(), "No re-rolls!", Toast.LENGTH_LONG).show();  
            	}
              }
            };
        this.roll.setOnClickListener(roll_action);
        }
    void init_app() {
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
        this.shear.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	shearWool(1);
            	updateTextOnly();
            	makeInvisible();
        		otherplayerrolls();

            }
          });
        final AlertDialog.Builder wolf_alert = new AlertDialog.Builder(this);
        wolf_alert.setTitle("Player selection");
        wolf_alert.setMessage("You have "+getStringData(Data.WOOL, 1)+" wool.\nWho would you like to send the wolf to?");
        wolf_alert.setPositiveButton("P2 ("+getStringData(Data.WOOL, 2)+" wool)", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				wool[2] = 0;
				text.setText("You wolfed P2! Roll again!");
				otherplayerrolls();
			}
		});

        wolf_alert.setNeutralButton("P3 ("+getStringData(Data.WOOL, 3)+" wool)",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
                    	wool[3] = 0;
                    	text.setText("You wolfed P3! Roll again!");
                    	otherplayerrolls();
					}
				});
        wolf_alert.setNegativeButton("P4 ("+getStringData(Data.WOOL, 4)+" wool)",
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

    int[] tie_between = new int[5];
    int ways_tie;
    
	/**
     * Checks whether the game is over, and, if so, returns {@code true} and performs the necessary game actions.
     * @author Glen Husman & Matt Husman
     * @return Whether the game is over or not
     */
	protected boolean checkIfGameOver() {
		boolean gameover;
		total_wool = wool[1] + wool[2] + wool[3] + wool[4] + sheared_wool[1] + sheared_wool[2] + sheared_wool[3] + sheared_wool[4];
		// FIXMED Developer cheat
		// total_wool = 25;
		// TODONE Remove developer cheat when done testing
		if(total_wool >= max_total_wool){
			// Game over!
			shearWoolGameover(1);
			shearWoolGameover(2);
			shearWoolGameover(3);
			shearWoolGameover(4);
			// FIXMED Developer cheat
			/**
			sheared_wool[1] = 0;
			sheared_wool[2] = 0;
			sheared_wool[3] = 0;
			sheared_wool[4] = 0;
			*/
			// TODONE Remove developer cheat when done testing
			roll.setVisibility(View.VISIBLE);
			text.setTextColor(Color.GREEN);
			makeInvisible();
			// FIXMED Tie-checking bug where if there is a 4+ way tie involving P1, P1 is not listed in tied players, or here!
			ways_tie = tie_between[1] + tie_between[2] + tie_between[3] + tie_between[4] /* + 1 */;
			roll.setText("Restart");
            final AlertDialog.Builder restarting_conf = new AlertDialog.Builder(this);
            restarting_conf.setTitle("New game?");
            restarting_conf.setMessage("Start a new game?");
            restarting_conf.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					Intent main = getIntent();
		            overridePendingTransition(0, 0);
		            main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		            overridePendingTransition(0, 0);
		            main.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		            finish();
		            overridePendingTransition(0, 0);
		            startActivity(main);
				}});
            restarting_conf.setNegativeButton("No", null);
			roll.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					restarting_conf.show();
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
					tie_between[player_num] = 1;
					winner_player_num = 0;
					if(wool[player_num]+sheared_wool[player_num] == wool[1]+sheared_wool[1] && player_num != 1) tie_between[1] = 1;
					if(wool[player_num]+sheared_wool[player_num] == wool[2]+sheared_wool[2] && player_num != 2) tie_between[2] = 1;
					if(wool[player_num]+sheared_wool[player_num] == wool[3]+sheared_wool[3] && player_num != 3) tie_between[3] = 1;
					if(wool[player_num]+sheared_wool[player_num] == wool[4]+sheared_wool[4] && player_num != 4) tie_between[4] = 1;
					/*
					try{
					if(wool[player_num]+sheared_wool[player_num] == wool[player_num - 1]+sheared_wool[player_num - 1] && (player_num - 1) > 0) tie_between[player_num - 1] = 1;
					if(wool[player_num]+sheared_wool[player_num] == wool[player_num - 2]+sheared_wool[player_num - 2] && (player_num - 2) > 0) tie_between[player_num - 2] = 1;
					if(wool[player_num]+sheared_wool[player_num] == wool[player_num - 3]+sheared_wool[player_num - 3] && (player_num - 3) > 0) tie_between[player_num - 3] = 1;
					}catch (ArrayIndexOutOfBoundsException array_error){
						Log.e(TAG, "We had an array error here (in the additional tie-checking verifiers [if statements])", array_error);
					}
					*/
				} else if ((wool[player_num]+sheared_wool[player_num]) > winning_score) {
					winner = "P"+Integer.toString(player_num);
					winner_player_num = player_num;
					winning_score = wool[player_num]+sheared_wool[player_num];
				}
			}
			Log.i(TAG,"Tied between array shows these tie statistics: "+tie_between[1]+", "+tie_between[2]+", "+tie_between[3]+", "+tie_between[4]);
			winner_text.setVisibility(View.VISIBLE);
			text.setText(((String) text.getText()).replace(" Roll again!", ""));
			String tiebetween = "";
			for (int loopi01=1; loopi01 <= 4; loopi01++) {
				if (tie_between[loopi01] >= 1 && loopi01 != 4) tiebetween = tiebetween.concat("P"+loopi01+" and ");
				else if (tie_between[loopi01] >= 1 && loopi01 == 4) tiebetween = tiebetween.concat("P"+loopi01);
			}
			if(winner == tie_text){
				winner_text.setText(ways_tie+"-way tie between "+tiebetween+"!!");
				winner_text.setText(((String) winner_text.getText()).replace(" and !!", "!!"));
				Toast.makeText(getBaseContext(), ("Game over! Congratulations, "+tiebetween+"!!").replace(" and !!", "!!"), Toast.LENGTH_LONG).show();
				/*
				winner_text.setText("Tie between P"+tie_between[0]+" and P"+tie_between[1]+"!!");
				Toast.makeText(getBaseContext(), "Game over! Congratulations, P"+tie_between[0]+" and P"+tie_between[1]+"!!", Toast.LENGTH_LONG).show();
				*/
			}
			else{
			winner_text.setText(winner+" wins!!");
			text.setText("Game over!");
			Toast.makeText(getBaseContext(), "Game over! Congratulations, "+winner+"!", Toast.LENGTH_LONG).show();
			}
			logtext.setText("Last moves:"+"\n"+players_did[2]+"\n"+players_did[3]+"\n"+players_did[4]);
			text.setText("Game over!");
			final int winner_final = winner_player_num;
			share.setVisibility(View.VISIBLE);
			share.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					String share_text;
					if(winner_final == 1){
						share_text = "I got a 1st place winning high score of "+Integer.toString(sheared_wool[1])+" wool on wolf 'n sheep. Think you can beat it?";
					}else if(ways_tie > 1 && tie_between[1] >= 1){
						share_text = "I tied with a 1st place high score of "+Integer.toString(sheared_wool[1])+" wool on wolf 'n sheep. Think you can beat it?";
					}
					else{
						share_text = "I got a high score of "+Integer.toString(sheared_wool[1])+" wool on wolf 'n sheep. Think you can beat it?";
					}
					Intent sharingIntent = new Intent();
					sharingIntent.setAction(Intent.ACTION_SEND);
					sharingIntent.putExtra(Intent.EXTRA_TEXT, share_text);
					sharingIntent.setType("text/plain");
				    startActivity(Intent.createChooser(sharingIntent,"Share high score using"));
				}});
			if(winner_final == 1){
				text.setText("Game over! You won!");
			}else if(tie_between[0] == 1 || tie_between[1] == 1){
				text.setText("Game over! You tied!");
			}
			gameover =  true;
		}		
		else{
			// Game-in-progress.
			gameover =  false;
		}
		updateTextOnly();
		return gameover;
	}
 
	/**
	 * Clear all visible buttons (wolf, grow, shear, and swap).
	 * Changes the visibility states of the action buttons to View.GONE.
	 */
	void makeInvisible() {
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
		if(getData(Data.WOOL, 1) >= max_wool && autoshear_state){
        	/** TODONE Have an option to enable "special" features not in standard ruleset, like this
        	 * Commented because it is not standard rules, can easily make a preference for enabling this (and other modifications).
        	 * Uncommented because CPU does it
        	 */
    		shearWool(1);
        	Toast.makeText(getBaseContext(), "Auto-sheared a full sheep!", Toast.LENGTH_SHORT).show();
        	updateTextOnly();
			//Toast.makeText(getBaseContext(), "Cannot have more than "+Integer.toString(max_wool)+" wool on your sheep!", Toast.LENGTH_LONG).show();
        }else if(getData(Data.WOOL, 1) >= max_wool && !autoshear_state){
        	Toast.makeText(getBaseContext(), "Cannot have more than "+Integer.toString(max_wool)+" wool on your sheep!", Toast.LENGTH_LONG).show();
        	wool[1] = max_wool;
        	updateTextOnly();
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
		players_did[2] = p_action(2, random_number_p2);
		players_did[3] = p_action(3, random_number_p3);
		players_did[4] = p_action(4, random_number_p4);
		int winning_score = -1;
		String winner;
		int winner_player_num = 0;
		// TODONE Use arrays everywhere, so this will work!!!
		for (player_num=1; player_num <= num_players; player_num++) {
			if ((wool[player_num]+sheared_wool[player_num]) == winning_score) {
				winner_player_num = 0;
			} else if ((wool[player_num]+sheared_wool[player_num]) > winning_score) {
				winner_player_num = player_num;
				winning_score = wool[player_num]+sheared_wool[player_num];
			}
		}
		if(winner_player_num == 0){
			winner = "Currently tied.";
		}else{
			winner = "P"+Integer.toString(winner_player_num)+" currently winning.";
		}
		logtext.setText(players_did[2]+"\n"+players_did[3]+"\n"+players_did[4]+"\n"+winner);
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
	
	void updateTextOnly() {
		/** Now uses table format */
		p1_wool_text.setText(getStringData(Data.WOOL, 1)+"/"+getStringData(Data.SHEARED_WOOL, 1));
	    p2_wool_text.setText(getStringData(Data.WOOL, 2)+"/"+getStringData(Data.SHEARED_WOOL, 2));
	    p3_wool_text.setText(getStringData(Data.WOOL, 3)+"/"+getStringData(Data.SHEARED_WOOL, 3));
	    p4_wool_text.setText(getStringData(Data.WOOL, 4)+"/"+getStringData(Data.SHEARED_WOOL, 4));
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
		// TODONE Have an option to enable "special" features not in standard ruleset, like this
		
		// Commented because it is not standard rules, can easily make a preference for this.
		// Uncommented because P1 does it
		
		if (wool[num_player] >= max_wool && autoshear_state) {
			shearWool(num_player);
		}else if(wool[num_player] >= max_wool && !autoshear_state){
			wool[num_player] = max_wool;
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
				returnvalue = "P"+num_player.toString()+" swapped with P"+Integer.toString(player_swap)+".";
				if(criticalalerts_state && player_swap == 1){
					LinkAlertDialog.create(this, "You got swapped!", "P"+num_player.toString()+" swapped with you!", "OK").show();
				}
			}else{
				wool[who_most_wool] = 0;
				returnvalue = "P"+Integer.toString(num_player)+" wolfed P"+Integer.toString(who_most_wool)+".";
				if(criticalalerts_state && who_most_wool == 1){
					LinkAlertDialog.create(this, "You got wolfed!", "P"+num_player.toString()+" wolfed you!", "OK").show();
				}
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
				if(criticalalerts_state && player_wolf == 1){
					LinkAlertDialog.create(this, "You got wolfed!", "P"+num_player.toString()+" wolfed you!", "OK").show();
				}
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
				if(criticalalerts_state && player_swap_alt == 1){
					LinkAlertDialog.create(this, "You got swapped!", "P"+num_player.toString()+" swapped with you!", "OK").show();
				}
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
	 * @deprecated Use {@link #shearWool(int)} instead.
	 * @author Glen Husman & Matt Husman
	 */
	protected void shearWool(){
        sheared_wool[1] = sheared_wool[1] + wool[1];
        wool[1] = 0;
    	updateTextOnly();
	}
	
	void shearWoolGameover(Integer num_player){
		CharSequence old_text = text.getText();
		shearWool(num_player);
		if(num_player == 1){
			text.setText(old_text);
		}
	}
	
	/**
	 * Shears the wool of a player.
	 * @author Glen Husman & Matt Husman
	 * @param num_player The player whose wool to shear
	 */
	protected void shearWool(int num_player){
		// TODONE Fix the shear costs (extras) bug!
		if(shearcosts_state && wool[num_player] > 0){
			wool[num_player]--;
		}
		if(num_player == 1){
			text.setText("You sheared! Roll again!");
		}
		final int wool_old = wool[num_player];
		sheared_wool[num_player] += wool_old;
		wool[num_player] = 0;
		updateTextOnly();
	}
	
	/**
	 * Update the TextView's text for all players wool.
	 */
	void updateText(){
	updateTextOnly();
	init_app();
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

    }


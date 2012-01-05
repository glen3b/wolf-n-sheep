package org.glen_h.games.wildwool;

import org.glen_h.libraries.Mathematics;
import org.glen_h.games.wildwool.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is the main, single-player, wild wool activity.
 * @author Glen Husman & Matt Husman
 */
public class WildWoolMain extends android.app.Activity {

	/**
	 * Possible data types in the game.
	 * @author Glen Husman
	 * @see getData(Data, Integer)
	 * @see setData(Data, Integer, Integer)
	 *
	 */
	protected enum Data {
	    WOOL, SHEARED_WOOL, MAX_WOOL, MAX_TOTAL_WOOL, PLAYERS
	}
	
	/**
	 * Gets gameplay data.
	 * @param data_get The data to get (SHEARED_WOOL or WOOL)
	 * @param num_player_data The player to get the data for
	 * @return The data
	 */
	protected int getData(Data data_get, Integer num_player_data) {
        switch (data_get) {
            case WOOL:
            	return wool[num_player_data];
                    
            case SHEARED_WOOL:
            	return sheared_wool[num_player_data];
            case MAX_WOOL:
            	return max_wool;
            case MAX_TOTAL_WOOL:
            	return max_total_wool;
            case PLAYERS:
            	return num_players;
            default:
            	return -1;
        	}
        }
	
	/**
	 * Sets gameplay data as specified by {@code data} and {@code data_set}.
	 * @param data_set The data to set to
	 * @param num_player_data The player's data to change
	 * @param data What data to set
	 */
	protected void setData(Data data_set, Integer num_player_data, Integer data) {
        // TODO: Finish method!
		switch (data_set) {
            case WOOL:
                    
            case SHEARED_WOOL:
            
            default:

        	}
        }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Catches an inflation error
        try{
        inflater.inflate(R.menu.main_menu, menu);
        }catch(android.view.InflateException error){
        	return false;
        }
        return true;
    }
    
	/**
	Warns the user that multiplayer is unstable.
	XXX Delete/deprecate this method when multiplayer finished.
	@author Glen Husman
	*/
	private void multiplayerUnstableToast(){
		Toast.makeText(getBaseContext(), "This is highly unstable and not ready for use!!", Toast.LENGTH_LONG).show();
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Integer item_id = item.getItemId();
    	switch (item_id) {
        case R.id.multiplayer:
     	   Intent mp = new Intent(this, WildWoolMultiplayer.class);
     	   startActivity(mp);
           multiplayerUnstableToast();
           return true;
        case R.id.exit:
        	finish();
        	return true;
        }
        return false;
    }
	
	  private int random_number = 0;
	  private Button roll;

	  private int wool[] = new int[5];
	  private int sheared_wool[] = new int[5];
	  private int player_num;
	  private int num_players = 4;
	  
	  private int total_wool;
      private final int max_wool = 5;
      private final int max_total_wool = 25;
      private Button shear;
      private Button wolf;
      private Button grow;
      private Button swap;
      // TODO Fix dice (Also fix onClick functions for the dice)
	  private String[] messages = 
	        { "Roll the die!",
      		"Send wolf or grow wool.",
      		"Swap sheep or grow wool.",
      		"Swap sheep or shear sheep.",
      		"Shear sheep or grow wool.", 
      		"Send wolf or grow wool.",
      		"Grow 2 wool."
      		}
      ;
      
	private TextView text;
	private TextView p1_wool_text;
	private TextView p2_wool_text;
	private TextView p3_wool_text;
	private TextView p4_wool_text;
	private TextView p2;
	private TextView p3;
	private TextView p4;
	
	/** Called when the activity is first created.
	 * Initializes the TextViews from XML, the roll button, and the player buttons.
	 * @author Glen Husman & Matt Husmam */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // FIXME Computer Rolls
        // FIXME Dice entries incorrect (see messages variable declaration)
        // Note to self: Icon in public domain, see http://en.wikipedia.org/wiki/File:Sheep_icon_05.svg
        
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
		updateTextOnly();
        text.setText(getResources().getString(R.string.message));
        OnClickListener roll_action = new OnClickListener() {
            public void onClick(View v) {
                text.setTextColor(Color.YELLOW);
            	if(shear.getVisibility() == View.GONE && wolf.getVisibility() == View.GONE && grow.getVisibility() == View.GONE && swap.getVisibility() == View.GONE){
            	random_number = Mathematics.randomNumber(1, 6);
            	makeInvisible();
        		roll();
                text.setText(messages[random_number]);
            	}
            	else{
            	    Toast.makeText(getBaseContext(), "No re-rolls!", Toast.LENGTH_LONG).show();  
            	}
            	checkIfGameOver();
              }
            };
        this.roll.setOnClickListener(roll_action);
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
			
			int winning_score = -1;
			String winner = "Nobody";
			TextView winner_text = (TextView)this.findViewById(R.id.winner);
			// TODONE Use arrays everywhere, so this will work!!!
			for (player_num=1; player_num <= num_players; player_num++) {
				if ((wool[player_num]+sheared_wool[player_num]) == winning_score) {
					winner = "Tie";
				} else if ((wool[player_num]+sheared_wool[player_num]) > winning_score) {
					winner = "P"+Integer.toString(player_num);
					winning_score = wool[player_num]+sheared_wool[player_num];
				}
			}
			p2.setVisibility(View.GONE);
			p3.setVisibility(View.GONE);
			p4.setVisibility(View.GONE);
			winner_text.setVisibility(View.VISIBLE);
			winner_text.setText(winner+" wins!!");
			Toast.makeText(getBaseContext(), "Game over! Congratulations, "+winner+"!", Toast.LENGTH_LONG).show();
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
        	shearWool(1);
        	/** TODO Have an option to enable "special" features not in standard ruleset, like this
        	 * Commented because it is not standard rules, can easily make a preference for enabling this (and other modifications).
        	 */
    		/*
    		if (wool[num_player] == max_wool) {
    			sheared_wool[num_player]+=wool[num_player];
    			wool[num_player] = 0;
    		}
    		*/
        	Toast.makeText(getBaseContext(), "Auto-sheared a full sheep!", Toast.LENGTH_SHORT).show();
			//Toast.makeText(getBaseContext(), "Cannot have more than "+Integer.toString(max_wool)+" wool on your sheep!", Toast.LENGTH_LONG).show();
        }
		if(random_number == 6){
    		wool[1] = wool[1] + 2;
    		if (wool[1] > max_wool) {
    			wool[1] = max_wool;
    		}
    	}
    	else if(random_number == 5){
    		wolf.setVisibility(View.VISIBLE);
    		grow.setVisibility(View.VISIBLE);
    	}
    	else if(random_number == 4){
    		shear.setVisibility(View.VISIBLE);
    		grow.setVisibility(View.VISIBLE);
    	}
    	else if(random_number == 3){
    		swap.setVisibility(View.VISIBLE);
    		shear.setVisibility(View.VISIBLE);
    	}
    	else if(random_number == 2){
    		swap.setVisibility(View.VISIBLE);
    		grow.setVisibility(View.VISIBLE);
    	}
    	else if(random_number == 1){
    		wolf.setVisibility(View.VISIBLE);
    		grow.setVisibility(View.VISIBLE);
    	}
        this.shear.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	shearWool(1);
            	makeInvisible();
        		otherplayerrolls();
            }
          });
        
        this.p2 = (TextView)this.findViewById(R.id.p2);
        this.p3 = (TextView)this.findViewById(R.id.p3);
        this.p4 = (TextView)this.findViewById(R.id.p4);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Player selection");
		alert.setMessage("Player to swap sheep with");
		alert.setPositiveButton("P2", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
            	swap(2);
            	p2.setVisibility(View.INVISIBLE);
            	p3.setVisibility(View.INVISIBLE);
            	p4.setVisibility(View.INVISIBLE);
        		otherplayerrolls();
			}
		});

		alert.setNeutralButton("P3",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
                    	swap(3);
                    	p2.setVisibility(View.INVISIBLE);
                    	p3.setVisibility(View.INVISIBLE);
                    	p4.setVisibility(View.INVISIBLE);
                		otherplayerrolls();
					}
				});
		alert.setNegativeButton("P4",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
                    	swap(4);
                    	p2.setVisibility(View.INVISIBLE);
                    	p3.setVisibility(View.INVISIBLE);
                    	p4.setVisibility(View.INVISIBLE);
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
            	p1_wool_text.setText("Your wool: "+Integer.toString(wool[2])+" Your sheared wool: "+Integer.toString(sheared_wool[1]));
            	makeInvisible();
        		otherplayerrolls();

            }
          });
        final AlertDialog.Builder wolf_alert = new AlertDialog.Builder(this);
        wolf_alert.setTitle("Player selection");
        wolf_alert.setMessage("Player to send wolf to");
        wolf_alert.setPositiveButton("P2", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				wool[2] = 0;
				otherplayerrolls();
			}
		});

        wolf_alert.setNeutralButton("P3",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
                    	wool[3] = 0;
                    	otherplayerrolls();
					}
				});
        wolf_alert.setNegativeButton("P4",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
                    	wool[4] = 0;
                    	otherplayerrolls();
					}
				});
        this.wolf.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
            	makeInvisible();
        		wolf_alert.show();
        		
        	}
          });
        updateText();
		checkIfGameOver();
	}

	protected void otherplayerrolls() {
		int random_number_p2 = Mathematics.randomNumber(1, 6);
		int random_number_p3 = Mathematics.randomNumber(1, 6);
		int random_number_p4 = Mathematics.randomNumber(1, 6);
		p_action(2, random_number_p2);
		p_action(3, random_number_p3);
		p_action(4, random_number_p4);
		updateTextOnly();
	}
	
	private void updateTextOnly() {
		p1_wool_text.setText("Your wool: "+Integer.toString(getData(Data.WOOL, 1))+" Your sheared wool: "+Integer.toString(getData(Data.SHEARED_WOOL, 1)));
	    p2_wool_text.setText("P2 wool: "+Integer.toString(getData(Data.WOOL, 2))+" P2 sheared wool: "+Integer.toString(getData(Data.SHEARED_WOOL, 2)));
	    p3_wool_text.setText("P3 wool: "+Integer.toString(getData(Data.WOOL, 3))+" P3 sheared wool: "+Integer.toString(getData(Data.SHEARED_WOOL, 3)));
	    p4_wool_text.setText("P4 wool: "+Integer.toString(getData(Data.WOOL, 4))+" P4 sheared wool: "+Integer.toString(getData(Data.SHEARED_WOOL, 4)));
		total_wool = getData(Data.WOOL, 1) + getData(Data.WOOL, 2) + getData(Data.WOOL, 3) + getData(Data.WOOL, 4) + getData(Data.SHEARED_WOOL, 1) + getData(Data.SHEARED_WOOL, 2) + getData(Data.SHEARED_WOOL, 3) + getData(Data.SHEARED_WOOL, 4);
	}

	/**
	 * Perform a CPU roll
	 * @author Matt Husman & Glen Husman
	 * @param roll The players roll
	 * @param num_player The player to play for
	 */
	protected void p_action(Integer num_player, Integer roll) {
		// TODO Finish attempt to make a generic other "player action"

		
		
		// If sheep is full at beginning of turn, "auto-shear"
		// TODO Have an option to enable "special" features not in standard ruleset, like this
		
		// Commented because it is not standard rules, can easily make a preference for this.
		/*
		if (wool[num_player] == max_wool) {
			sheared_wool[num_player]+=wool[num_player];
			wool[num_player] = 0;
		}
		*/
		
		switch (roll) {
		case 6:
			// Grow 2 wool
			wool[num_player] = wool[num_player]+2;
			break;
		case 5:
			// Send wolf or grow wool
			// TODO Eventually: if opponent has 4-5 wool, wolf him; for now, just grow
			wool[num_player]++;
			break;
		case 4:
			// Shear sheep or grow wool
			// If 0-1 wool, grow; else, shear
			if (wool[num_player] < 2) {
				wool[num_player]++;
			} else {
				shearWool(num_player);
			}
			break;
		case 3:
			// Swap or shear
			// TODO Eventually: If I have 3+ wool, then shear; else if opponent has 2+ more than me, swap;
			// for now, just shear
			sheared_wool[num_player]+=wool[num_player];
			wool[num_player]=0;
			break;
		case 2:
			// Swap or grow
			// TODO Eventually: if opponent has 2+ more than me, swap; for now, just grow
			wool[num_player]++;
			break;
		case 1:
			// Wolf or grow
			// TODO Eventually: if opponent has 4-5 wool, wolf him; for now, just grow
			wool[num_player]++;
			break;
		default:
		}
		if (wool[num_player] > max_wool) {
			wool[num_player] = max_wool;
		}
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
		// TODO Verify function works
		// XXX Verify that this throws ArrayIndexOutOfBoundsException (or something does when using this). I think it has to do with player_num.
		final int wool_old = wool[num_player];
		sheared_wool[num_player] += wool_old;
		wool[num_player] = 0;
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
    }


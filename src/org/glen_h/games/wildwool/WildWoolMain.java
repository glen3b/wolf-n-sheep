package org.glen_h.games.wildwool;

import org.glen_h.libraries.Mathematics;

import android.app.Activity;
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
public class WildWoolMain extends Activity {

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.multiplayer:
     	   Intent mp = new Intent(this, WildWoolMultiplayer.class);
           startActivity(mp);
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
	  
	  private int player_wool;
	  private int p2_wool;
	  private int p3_wool;
	  private int p4_wool;
	  private int player_wool_sheared = 0;
	  private int p2_wool_sheared = 0;
	  private int p3_wool_sheared = 0;
	  private int p4_wool_sheared = 0;
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
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // FIXME Computer Rolls
        // FIXME Dice entries incorrect (see messages variable declaration)
        // Note to self: Icon in public domain, see http://en.wikipedia.org/wiki/File:Sheep_icon_05.svg
        player_wool = 0;
        p2_wool = 0;
        p3_wool = 0;
        p4_wool = 0;
        
        for (player_num=1; player_num <= num_players; player_num++) {
        	wool[player_num] = 0;
        	sheared_wool[player_num] = 0;
        }
        
        total_wool = player_wool + p2_wool + p3_wool + p4_wool;
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
		 p1_wool_text.setText("Your wool: "+Integer.toString(player_wool)+" Your sheared wool: "+Integer.toString(player_wool_sheared));
	        p2_wool_text.setText("P2 wool: "+Integer.toString(p2_wool)+" P2 sheared wool: "+Integer.toString(p2_wool_sheared));
	        p3_wool_text.setText("P3 wool: "+Integer.toString(p3_wool)+" P3 sheared wool: "+Integer.toString(p3_wool_sheared));
	        p4_wool_text.setText("P4 wool: "+Integer.toString(p4_wool)+" P4 sheared wool: "+Integer.toString(p4_wool_sheared));
        text.setText(messages[random_number]);
        this.roll.setOnClickListener(new OnClickListener() {
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
        });
        }
    /**
     * Checks whether the game is over, and, if so, returns {@code true} and performs the necessary game actions.
     * @author Glen Husman & Matt Husman
     * @return gameover - Whether the game is over or not (boolean)
     */
	protected boolean checkIfGameOver() {
		boolean gameover;
		total_wool = player_wool + p2_wool + p3_wool + p4_wool + player_wool_sheared + p2_wool_sheared + p3_wool_sheared + p4_wool_sheared;
		if(total_wool >= max_total_wool){
			// Game over!
			text.setText("Game over!");
			text.setTextColor(Color.GREEN);
			shearWool();
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
			Toast.makeText(getBaseContext(), "Game over!", Toast.LENGTH_LONG).show();
			gameover =  true;
		}		
		else{
			// Game-in-progress.
			gameover =  false;
		}
		return gameover;
	}
 
	
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
		if(random_number == 6){
    		player_wool = player_wool + 2;
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
		if(player_wool > max_wool){
        	player_wool = max_wool;
        	Toast.makeText(getBaseContext(), "Cannot have more than "+Integer.toString(max_wool)+" wool on your sheep!", Toast.LENGTH_LONG).show();
        }
        this.shear.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	shearWool();
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
            	player_wool = player_wool + 1;
            	if(player_wool > max_wool){
                	player_wool = max_wool;
                	Toast.makeText(getBaseContext(), "Cannot have more than "+Integer.toString(max_wool)+"wool on your sheep! Please roll again!", Toast.LENGTH_LONG).show();
                }
            	p1_wool_text.setText("Your wool: "+Integer.toString(player_wool)+" Your sheared wool: "+Integer.toString(player_wool_sheared));
            	makeInvisible();
        		otherplayerrolls();

            }
          });
        final AlertDialog.Builder wolf_alert = new AlertDialog.Builder(this);
        wolf_alert.setTitle("Player selection");
        wolf_alert.setMessage("Player to send wolf to");
        wolf_alert.setPositiveButton("P2", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				p2_wool = 0;
				otherplayerrolls();
			}
		});

        wolf_alert.setNeutralButton("P3",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
                    	p3_wool = 0;
                    	otherplayerrolls();
					}
				});
        wolf_alert.setNegativeButton("P4",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
                    	p4_wool = 0;
                    	otherplayerrolls();
					}
				});
        this.wolf.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
            	makeInvisible();
        		wolf_alert.show();
        		
        	}
          });
        
		 p1_wool_text.setText("Your wool: "+Integer.toString(player_wool)+" Your sheared wool: "+Integer.toString(player_wool_sheared));
	        p2_wool_text.setText("P2 wool: "+Integer.toString(p2_wool)+" P2 sheared wool: "+Integer.toString(p2_wool_sheared));
	        p3_wool_text.setText("P3 wool: "+Integer.toString(p3_wool)+" P3 sheared wool: "+Integer.toString(p3_wool_sheared));
	        p4_wool_text.setText("P4 wool: "+Integer.toString(p4_wool)+" P4 sheared wool: "+Integer.toString(p4_wool_sheared));
		total_wool = player_wool + p2_wool + p3_wool + p4_wool + player_wool_sheared + p2_wool_sheared + p3_wool_sheared + p4_wool_sheared;
		checkIfGameOver();
	}

	protected void otherplayerrolls() {
		int random_number_p2 = Mathematics.randomNumber(1, 6);
		int random_number_p3 = Mathematics.randomNumber(1, 6);
		int random_number_p4 = Mathematics.randomNumber(1, 6);
		p2_action(random_number_p2);
		p3_action(random_number_p3);
		p4_action(random_number_p4);
		p1_wool_text.setText("Your wool: "+Integer.toString(player_wool)+" Your sheared wool: "+Integer.toString(player_wool_sheared));
	    p2_wool_text.setText("P2 wool: "+Integer.toString(p2_wool)+" P2 sheared wool: "+Integer.toString(p2_wool_sheared));
	    p3_wool_text.setText("P3 wool: "+Integer.toString(p3_wool)+" P3 sheared wool: "+Integer.toString(p3_wool_sheared));
	    p4_wool_text.setText("P4 wool: "+Integer.toString(p4_wool)+" P4 sheared wool: "+Integer.toString(p4_wool_sheared));
		total_wool = player_wool + p2_wool + p3_wool + p4_wool + player_wool_sheared + p2_wool_sheared + p3_wool_sheared + p4_wool_sheared;
	}
	
	// Implementing this later
	private void p_action(int roll, int num_player) {
		// TODO Attempt to make a generic other "player action"
		
		// If sheep is full at beginning of turn, "auto-shear"
		if (wool[num_player] == max_wool) {
			sheared_wool[num_player]+=wool[num_player];
			wool[num_player] = 0;
		}
		
		switch (roll) {
		case 6:
			// Grow 2 wool
			wool[num_player] = wool[num_player]+2;
			break;
		case 5:
			// Send wolf or grow wool
			// Eventually: if opponent has 4-5 wool, wolf him; for now, just grow
			wool[num_player]++;
			break;
		case 4:
			// Shear sheep or grow wool
			// If 0-1 wool, grow; else, shear
			if (wool[num_player] < 2) {
				wool[num_player]++;
			} else {
				sheared_wool[num_player]+=wool[num_player];
				wool[num_player]=0;
			}
			break;
		case 3:
			// Swap or shear
			// Eventually: If I have 3+ wool, then shear; else if opponent has 2+ more than me, swap;
			// for now, just shear
			sheared_wool[num_player]+=wool[num_player];
			wool[num_player]=0;
			break;
		case 2:
			// Swap or grow
			// Eventually: if opponent has 2+ more than me, swap; for now, just grow
			wool[num_player]++;
			break;
		case 1:
			// Wolf or grow
			// Eventually: if opponent has 4-5 wool, wolf him; for now, just grow
			wool[num_player]++;
			break;
		default:
		}
		if (wool[num_player] > max_wool) {
			wool[num_player] = max_wool;
		}
	}
	
	
	private void p4_action(int p4_roll) {
		// TODO P4 Roll
		if(p4_roll == 6){
			// Grow 2 wool
			p4_wool = p4_wool + 2;
    	}
    	else if(p4_roll == 5){
    		// Send wolf or grow wool
    		player_wool = 0;
    	}
    	else if(p4_roll == 4){
    		// Shear sheep or grow wool
    		final int p4_wool_old = p4_wool;
    		if(p4_wool != 0){
    		p4_wool_sheared = p4_wool_sheared + p4_wool_old;
        	p4_wool = 0;
    		}
    		else{
    		}
        	p4_wool_text.setText("P4 wool: "+Integer.toString(p4_wool)+" P4 sheared wool: "+Integer.toString(p4_wool_sheared));
    	}
    	else if(p4_roll == 3){
    		// Swap or shear
    		if(p4_wool < player_wool){
    			final int old_player_wool = player_wool;
        		final int new_player_wool = p4_wool;
        		p4_wool = old_player_wool;
        		player_wool = new_player_wool;
        		p1_wool_text.setText("Your wool: "+Integer.toString(player_wool)+" Your sheared wool: "+Integer.toString(player_wool_sheared));
                p4_wool_text.setText("P4 wool: "+Integer.toString(p4_wool)+" P4 sheared wool: "+Integer.toString(p4_wool_sheared));
    		}else{
    			final int p4_wool_old = p4_wool;
        		if(p4_wool != 0){
        		p4_wool_sheared = p4_wool_sheared + p4_wool_old;
            	p4_wool = 0;
        		}
        		else{
        		}
            	p4_wool_text.setText("P4 wool: "+Integer.toString(p4_wool)+" P4 sheared wool: "+Integer.toString(p4_wool_sheared));
    		}
    	}
    	else if(p4_roll == 2){
    		// Swap or grow
    		if(p2_wool > p4_wool){
    			
    		}
    		else if(p3_wool > p4_wool){
    			
    		}
    		else if(player_wool > p4_wool){
    			swap(4);
    		}
    		else{
    			p4_wool = p4_wool + 1;
    		}
    		if(p4_wool > max_wool){
    			p4_wool = 5;
            }
        	p4_wool_text.setText("P4 wool: "+Integer.toString(p4_wool)+" P4 sheared wool: "+Integer.toString(p4_wool_sheared));
    	}
    	else if(p4_roll == 1){
    		// Wolf or grow
    	}
		if(p4_wool > max_wool){
			p4_wool = 5;
        }
		
	}

	private void p3_action(int p3_roll) {
		// TODO P3 Roll
		if(p3_roll == 6){
			// Grow 2 wool
			p3_wool = p3_wool + 2;
    	}
    	else if(p3_roll == 5){
    		
    	}
    	else if(p3_roll == 4){
    		
    	}
    	else if(p3_roll == 3){
    		
    	}
    	else if(p3_roll == 2){
    		
    	}
    	else if(p3_roll == 1){
    		
    	}
		if(p3_wool > max_wool){
			p3_wool = 5;
        }
		
	}

	private void p2_action(int p2_roll) {
		// TODO P2 Roll
		if(p2_roll == 6){
			// Grow 2 wool
			p2_wool = p2_wool + 2;
    	}
    	else if(p2_roll == 5){
    		// 
    	}
    	else if(p2_roll == 4){
    		// 
    	}
    	else if(p2_roll == 3){
    		// 
    	}
    	else if(p2_roll == 2){
    		// 
    	}
    	else if(p2_roll == 1){
    		// 
    	}
		if(p2_wool > max_wool){
			p2_wool = 5;
        }
	}
	/**
	 * Shears P1's wool.
	 * @author Glen Husman & Matt Husman
	 */
	protected void shearWool(){
		final int player_wool_old = player_wool;
		if(player_wool <= 0){
		}
		else{
			player_wool_sheared = player_wool_sheared + player_wool_old;
	    	player_wool = 0;
		}
    	p1_wool_text.setText("Your wool: "+Integer.toString(player_wool)+" Your sheared wool: "+Integer.toString(player_wool_sheared));
	}
	
	/**
	 * Have P1 swap wool with {@code player}.
	 * @param player The player to swap wool with.
	 * @author Glen Husman & Matt Husman
	 */
	protected void swap(int player){
    	if(player == 2){
    		final int old_player_wool = player_wool;
    		final int new_player_wool = p2_wool;
    		p2_wool = old_player_wool;
    		player_wool = new_player_wool;
    		p1_wool_text.setText("Your wool: "+Integer.toString(player_wool)+" Your sheared wool: "+Integer.toString(player_wool_sheared));
            p2_wool_text.setText("P2 wool: "+Integer.toString(p2_wool)+" P2 sheared wool: "+Integer.toString(p2_wool_sheared));
    	}
    	else if(player == 3){
    		final int old_player_wool = player_wool;
    		final int new_player_wool = p3_wool;
    		p3_wool = old_player_wool;
    		player_wool = new_player_wool;
    		p1_wool_text.setText("Your wool: "+Integer.toString(player_wool)+" Your sheared wool: "+Integer.toString(player_wool_sheared));
    		p3_wool_text.setText("P3 wool: "+Integer.toString(p2_wool)+" P3 sheared wool: "+Integer.toString(p3_wool_sheared));
    	}
    	else if(player == 4){
    		final int old_player_wool = player_wool;
    		final int new_player_wool = p4_wool;
    		p4_wool = old_player_wool;
    		player_wool = new_player_wool;
    		p1_wool_text.setText("Your wool: "+Integer.toString(player_wool)+" Your sheared wool: "+Integer.toString(player_wool_sheared));
    		p4_wool_text.setText("P4 wool: "+Integer.toString(p2_wool)+" P4 sheared wool: "+Integer.toString(p4_wool_sheared));
    	}
    	else{
    		/* Invalid player number!
    		 TODO Catch this somehow.
    		 If something somehow ends up executing this, something went wrong:
    		 1. The code is incorrect somewhere (a bug).
    		 2. A runtime error somehow executed this.
    		 3. A bug executed this.
    		 */
    	}
    }

}

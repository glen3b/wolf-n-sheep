package org.glen_h.games.wildwool;

import org.glen_h.libraries.Mathematics;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is the multi-player wild wool activity.
 * For the moment, it is unfinished.
 * @author Glen Husman & Matt Husman
 */
public class WildWoolMultiplayer extends android.app.Activity {
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

	
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean returnvalue = false;
    	switch (item.getItemId()) {
        case R.id.scan:
            // Launch the DeviceListActivity to see devices and do scan
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            returnvalue =  true;
            break;
		case R.id.discoverable:
            // Ensure this device is discoverable by others
            ensureDiscoverable();
            returnvalue =  true;
            break;
        case R.id.singleplayer:
            Intent main = new Intent(this, WildWoolMain.class);
            overridePendingTransition(0, 0);
		    main.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		    overridePendingTransition(0, 0);
		    startActivity(main);
		    returnvalue =  true;
		    break;
        }
        return returnvalue;
    }
    
    public void ensureDiscoverable() {
        if(D) Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }
    
    // Layout Views
    private TextView mTitle;
    private EditText mOutEditText;
	
	// Debugging
    private final String TAG = "BluetoothChat";
    private final boolean D = true;

    // Message types sent from the BluetoothChatService Handler
    public final int MESSAGE_STATE_CHANGE = 1;
    public final int MESSAGE_READ = 2;
    public final int MESSAGE_WRITE = 3;
    public final int MESSAGE_DEVICE_NAME = 4;
    public final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public final String DEVICE_NAME = "device_name";
    public final String TOAST = "toast";
    
    // Intent request codes
    private final int REQUEST_CONNECT_DEVICE = 1;
    // private final int REQUEST_ENABLE_BT = 2;
    

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;
    
    // Wild wool
    private int random_number = 0;
	  private Button roll;
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
    
    
	   public final Handler mHandler = new Handler() {
		   // FIXME Exception is thrown here, crashes on menu option select or attempted exit
	        @Override
	        public void handleMessage(Message msg) {
	            switch (msg.what) {
	            case MESSAGE_STATE_CHANGE:
	                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
	                switch (msg.arg1) {
	                case BluetoothChatService.STATE_CONNECTED:
	                	Log.w(TAG, mtitle_error_log);
	                	mTitle.setText(R.string.title_connected_to);
	                    mTitle.append(mConnectedDeviceName);
	                    mConversationArrayAdapter.clear();
	                    break;
	                case BluetoothChatService.STATE_CONNECTING:
	                	Log.w(TAG, mtitle_error_log);
	                	mTitle.setText(R.string.title_connecting);
	                    break;
	                case BluetoothChatService.STATE_LISTEN:
	                case BluetoothChatService.STATE_NONE:
	                	Log.w(TAG, mtitle_error_log);
	                	mTitle.setText(R.string.title_not_connected);
	                    break;
	                }
	                break;
	            case MESSAGE_WRITE:
	                byte[] writeBuf = (byte[]) msg.obj;
	                // construct a string from the buffer
	                String writeMessage = new String(writeBuf);
	                mConversationArrayAdapter.add("Me:  " + writeMessage);
	                break;
	            case MESSAGE_READ:
	                byte[] readBuf = (byte[]) msg.obj;
	                // construct a string from the valid bytes in the buffer
	                String readMessage = new String(readBuf, 0, msg.arg1);
	                mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
	                break;
	            case MESSAGE_DEVICE_NAME:
	                // save the connected device's name
	                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
	                Toast.makeText(getApplicationContext(), "Connected to "
	                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
	                break;
	            case MESSAGE_TOAST:
	                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
	                               Toast.LENGTH_SHORT).show();
	                break;
	            }
	        }
	    };
	
	String mtitle_error_log;
	    
	public void initialize(){
        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
	}
	    
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
        mtitle_error_log = "mTitle is (probably) null, about to throw exception";
        player_wool = 0;
        p2_wool = 0;
        p3_wool = 0;
        p4_wool = 0;
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
		 p1_wool_text.setText("Your wool: "+Integer.toString(player_wool)+" Your sheared wool: "+Integer.toString(player_wool_sheared));
	        p2_wool_text.setText("P2 wool: "+Integer.toString(p2_wool)+" P2 sheared wool: "+Integer.toString(p2_wool_sheared));
	        p3_wool_text.setText("P3 wool: "+Integer.toString(p3_wool)+" P3 sheared wool: "+Integer.toString(p3_wool_sheared));
	        p4_wool_text.setText("P4 wool: "+Integer.toString(p4_wool)+" P4 sheared wool: "+Integer.toString(p4_wool_sheared));
        text.setText(messages[random_number]);
        this.roll.setOnClickListener(new OnClickListener() {
          public void onClick(View v) {
        	if(shear.getVisibility() == View.GONE && wolf.getVisibility() == View.GONE && grow.getVisibility() == View.GONE && swap.getVisibility() == View.GONE){
        	  random_number = Mathematics.randomNumber(1, 6);
        	wolf.setVisibility(View.GONE);
    		grow.setVisibility(View.GONE);
    		shear.setVisibility(View.GONE);
    		swap.setVisibility(View.GONE);
    		theStuff();
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
     * Sends a message.
     * @param message  A string of text to send.
     */
    public void sendMessage(String message) {
    	// Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(getBaseContext(), "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    	
    	// Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            // Not connected!
        	return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            
        	byte[] send = message.getBytes();
        	// byte[] send = { 6,1}
            //  ;;;
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);
        }
    }
    
    protected boolean checkIfGameOver() {
		if(total_wool > max_total_wool){
			// Game over!
			text.setText("Game over!");
			shearWool();
			roll.setVisibility(View.INVISIBLE);
			Toast.makeText(getBaseContext(), "Game over!", Toast.LENGTH_LONG).show();
			return true;
		}		
		else if(total_wool == max_total_wool){
			// Game over!
			text.setText("Game over!");
			shearWool();
			roll.setVisibility(View.INVISIBLE);
			Toast.makeText(getBaseContext(), "Game over!", Toast.LENGTH_LONG).show();
			return true;
			}
		else{
		// Game-in-progress.
		return false;
		}
	}

	protected void theStuff() {
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
        	player_wool = 5;
        }
        this.shear.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	shearWool();
            	wolf.setVisibility(View.GONE);
        		grow.setVisibility(View.GONE);
        		shear.setVisibility(View.GONE);
        		swap.setVisibility(View.GONE);
        		otherplayerrolls();
            }
          });
        
        /*
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		final EditText input = new EditText(this);
		alert.setView(input);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		alert.setTitle("Player to swap sheep with");
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				value = input.getText().toString();
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				});
				*/
        this.p2 = (TextView)this.findViewById(R.id.p2);
        this.p3 = (TextView)this.findViewById(R.id.p3);
        this.p4 = (TextView)this.findViewById(R.id.p4);
		this.swap.setOnClickListener(new OnClickListener() {	
            public void onClick(View v) {
            	p2.setVisibility(View.VISIBLE);
            	p3.setVisibility(View.VISIBLE);
            	p4.setVisibility(View.VISIBLE);
            	wolf.setVisibility(View.GONE);
        		grow.setVisibility(View.GONE);
        		shear.setVisibility(View.GONE);
        		swap.setVisibility(View.GONE);
        		p2.setOnClickListener(new OnClickListener() {	
                    public void onClick(View v) {
                    	swap(2);
                    	p2.setVisibility(View.INVISIBLE);
                    	p3.setVisibility(View.INVISIBLE);
                    	p4.setVisibility(View.INVISIBLE);
                		otherplayerrolls();
                    }
                }
                  );
        		p3.setOnClickListener(new OnClickListener() {	
                    public void onClick(View v) {
                    	swap(3);
                    	p2.setVisibility(View.INVISIBLE);
                    	p3.setVisibility(View.INVISIBLE);
                    	p4.setVisibility(View.INVISIBLE);
                		otherplayerrolls();

                    }
                }
                  );
        		p4.setOnClickListener(new OnClickListener() {	
                    public void onClick(View v) {
                    	swap(4);
                    	p2.setVisibility(View.INVISIBLE);
                    	p3.setVisibility(View.INVISIBLE);
                    	p4.setVisibility(View.INVISIBLE);
                		otherplayerrolls();

                    }
                }
                  );
            }
        }
          );
        this.grow.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	player_wool = player_wool + 1;
            	if(player_wool > max_wool){
                	player_wool = 5;
                	Toast.makeText(getBaseContext(), "Cannot have more than 5 wool on your sheep! Please roll again!", Toast.LENGTH_LONG).show();
                }
            	p1_wool_text.setText("Your wool: "+Integer.toString(player_wool)+" Your sheared wool: "+Integer.toString(player_wool_sheared));
            	wolf.setVisibility(View.GONE);
        		grow.setVisibility(View.GONE);
        		shear.setVisibility(View.GONE);
        		swap.setVisibility(View.GONE);
        		otherplayerrolls();

            }
          });
        
        this.wolf.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	p2.setVisibility(View.VISIBLE);
            	p3.setVisibility(View.VISIBLE);
            	p4.setVisibility(View.VISIBLE);
            	wolf.setVisibility(View.GONE);
        		grow.setVisibility(View.GONE);
        		shear.setVisibility(View.GONE);
        		swap.setVisibility(View.GONE);
        		p2.setOnClickListener(new OnClickListener() {	
                    public void onClick(View v) {
        				p2_wool = 0;
                    	p2.setVisibility(View.INVISIBLE);
                    	p3.setVisibility(View.INVISIBLE);
                    	p4.setVisibility(View.INVISIBLE);
                		otherplayerrolls();
                    	p1_wool_text.setText("Your wool: "+Integer.toString(player_wool)+" Your sheared wool: "+Integer.toString(player_wool_sheared));
            	        p2_wool_text.setText("P2 wool: "+Integer.toString(p2_wool)+" P2 sheared wool: "+Integer.toString(p2_wool_sheared));
            	        p3_wool_text.setText("P3 wool: "+Integer.toString(p3_wool)+" P3 sheared wool: "+Integer.toString(p3_wool_sheared));
            	        p4_wool_text.setText("P4 wool: "+Integer.toString(p4_wool)+" P4 sheared wool: "+Integer.toString(p4_wool_sheared));
                    }
                }
                  );
        		p3.setOnClickListener(new OnClickListener() {	
                    public void onClick(View v) {
        				p3_wool = 0;
                    	p2.setVisibility(View.INVISIBLE);
                    	p3.setVisibility(View.INVISIBLE);
                    	p4.setVisibility(View.INVISIBLE);
                		otherplayerrolls();
                    	p1_wool_text.setText("Your wool: "+Integer.toString(player_wool)+" Your sheared wool: "+Integer.toString(player_wool_sheared));
            	        p2_wool_text.setText("P2 wool: "+Integer.toString(p2_wool)+" P2 sheared wool: "+Integer.toString(p2_wool_sheared));
            	        p3_wool_text.setText("P3 wool: "+Integer.toString(p3_wool)+" P3 sheared wool: "+Integer.toString(p3_wool_sheared));
            	        p4_wool_text.setText("P4 wool: "+Integer.toString(p4_wool)+" P4 sheared wool: "+Integer.toString(p4_wool_sheared));
                    }
                }
                  );
        		p4.setOnClickListener(new OnClickListener() {	
                    public void onClick(View v) {
        				p4_wool = 0;
                    	p2.setVisibility(View.INVISIBLE);
                    	p3.setVisibility(View.INVISIBLE);
                    	p4.setVisibility(View.INVISIBLE);
                		otherplayerrolls();
                    	p1_wool_text.setText("Your wool: "+Integer.toString(player_wool)+" Your sheared wool: "+Integer.toString(player_wool_sheared));
            	        p2_wool_text.setText("P2 wool: "+Integer.toString(p2_wool)+" P2 sheared wool: "+Integer.toString(p2_wool_sheared));
            	        p3_wool_text.setText("P3 wool: "+Integer.toString(p3_wool)+" P3 sheared wool: "+Integer.toString(p3_wool_sheared));
            	        p4_wool_text.setText("P4 wool: "+Integer.toString(p4_wool)+" P4 sheared wool: "+Integer.toString(p4_wool_sheared));
                    }
                }
                  );
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
		String stats = 
				Integer.toString(player_wool)+","
				+Integer.toString(player_wool_sheared)+","
				+Integer.toString(p2_wool)+","
				+Integer.toString(p2_wool_sheared)+","
				+Integer.toString(p3_wool)+","
				+Integer.toString(p3_wool_sheared)+","
				+Integer.toString(p4_wool)+","
				+Integer.toString(p4_wool_sheared);
		sendMessage(stats);
		// int random_number_p3 = Mathematics.randomNumber(1, 6);
		// int random_number_p4 = Mathematics.randomNumber(1, 6);
		// TODO CPU Rolls (Copy over from 1P)
		// TODO Find out how many players there are, and only  do CPU rolls for others
		p1_wool_text.setText("Your wool: "+Integer.toString(player_wool)+" Your sheared wool: "+Integer.toString(player_wool_sheared));
	    p2_wool_text.setText("P2 wool: "+Integer.toString(p2_wool)+" P2 sheared wool: "+Integer.toString(p2_wool_sheared));
	    p3_wool_text.setText("P3 wool: "+Integer.toString(p3_wool)+" P3 sheared wool: "+Integer.toString(p3_wool_sheared));
	    p4_wool_text.setText("P4 wool: "+Integer.toString(p4_wool)+" P4 sheared wool: "+Integer.toString(p4_wool_sheared));
		total_wool = player_wool + p2_wool + p3_wool + p4_wool + player_wool_sheared + p2_wool_sheared + p3_wool_sheared + p4_wool_sheared;
	}

	protected void shearWool(){
		int player_wool_old = player_wool;
		if(player_wool != 0){
		player_wool_sheared = player_wool_sheared + player_wool_old;
    	player_wool = 0;
		}
		else{
		}
    	p1_wool_text.setText("Your wool: "+Integer.toString(player_wool)+" Your sheared wool: "+Integer.toString(player_wool_sheared));
	}
	
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
    }
}

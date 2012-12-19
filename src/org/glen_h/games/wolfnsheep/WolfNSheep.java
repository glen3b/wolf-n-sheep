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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is the main, single-player, wolf 'n sheep game activity.
 * @author Glen Husman
 */
public class WolfNSheep extends Activity {
	
	public static final boolean DEBUG = true;
	
	private ProgressDialog load;

	
	/**
	 * Returns the game's vital statistics as a string.
	 */
	@Override
	public String toString(){
		return getClass().getName()+":MODE,"+mode+";P1,"+wool[1]+","+sheared_wool[1]+";P2,"+wool[2]+","+sheared_wool[2]+";P3,"+wool[3]+","+sheared_wool[3]+";P4,"+wool[4]+","+sheared_wool[4];
	}
	
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
	    final String[] players_did_saved = players_did;
	    final int[] sheared_wool_saved = sheared_wool;
	    final int shear_visible = shear.getVisibility();
	    final int wolf_visible = wolf.getVisibility();
	    final CharSequence random_num_tosave = text.getText();
	    final int grow_visible = grow.getVisibility();
	    final int swap_visible = swap.getVisibility();
	    final String mp_password = mpPassword;
	    final String mp_user = mpUser;
	    final String mp_gameid = game_id;
	    final int player_mode = mode.ordinal();
	    final int player = mpPlayerNum;
	    final int rolled_color = text.getCurrentTextColor();
	    final CharSequence log_text_content = logtext.getText();
	    Bundle data = new Bundle();
	    data.putIntArray("wool", wool_saved);
	    data.putStringArray("players_did", players_did_saved);
	    data.putCharSequence("lastmoves", log_text_content);
	    data.putIntArray("sheared_wool", sheared_wool_saved);
	    data.putString("mpUser", mp_user);
	    data.putString("game_id", mp_gameid);
	    data.putString("mpPassword", mp_password);
	    data.putCharSequence("randomnum", random_num_tosave);
	    data.putInt("shear_visible", shear_visible);
	    data.putInt("mpPlayerNum", player);
	    data.putInt("wolf_visible", wolf_visible);
	    data.putInt("grow_visible", grow_visible);
	    data.putInt("text_color", rolled_color);
	    data.putInt("pmode", player_mode);
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
	int mpPlayerNum = 1;
	SharedPreferences settings;
	static String mpUser;
	static String mpPassword;
	protected static final String mpUrl = "http://glen3b.pagekite.me/ws-mp/";
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
      	   Intent prefs = new Intent(WolfNSheep.this, Extras.class);
      	   startActivity(prefs);
      	   return true;
        case R.id.exit:
        	finish();
        	return true;
        case R.id.about:
        	PackageManager manager = WolfNSheep.this.getPackageManager();
        	   PackageInfo info;
			try {
				info = manager.getPackageInfo(WolfNSheep.this.getPackageName(), 0);
			} catch (NameNotFoundException e) {
				info = null;
				if(DEBUG) Log.e(TAG, "Why was the package name not found?", e);
			}
			try{
			version_name = info.versionName;
			if(spanish){
				about_dialog_text = "Lobo 'N Ovejas "+version_name+" - http://code.google.com/p/wolf-n-sheep -" +
						" La versión del Lobo 'N Ovejas "+version_name+". Un juego para Android inspirado en lana natural. " +
						"Icono se basa fuera de http://en.wikipedia.org/wiki/File:Sheep_icon_05.svg, y bajo el dominio público " +
						"(puede copiar, modificar, distribuir y comunicar públicamente la obra, incluso para fines comerciales, todo ello sin pedir permiso). " +
						"Tenga en cuenta que esto sólo se aplica a la icono de la aplicación, no el código. El código está licenciado bajo la licencia Apache 2.0, disponible en " +
						"http://www.apache.org/licenses/LICENSE-2.0";
			}else{
			about_dialog_text = "Wolf 'N Sheep "+version_name+" - http://code.google.com/p/wolf-n-sheep -" +
					" Wolf 'N Sheep version "+version_name+". An android game inspired by wild wool. " +
					"Icon is based off of http://en.wikipedia.org/wiki/File:Sheep_icon_05.svg, and under the public domain " +
					"(you can copy, modify, distribute and perform the work, even for commercial purposes, all without asking permission). " +
					"Please note that this applies ONLY to the application icon, not the code. The code is licensed under the apache license 2.0, available at " +
					"http://www.apache.org/licenses/LICENSE-2.0";
			}
			}catch (NullPointerException nullerror){
				if(DEBUG) Log.w(TAG, "Something was null here, probably info, while setting version_name, version_code, and about_dialog_text.", nullerror);
				version_name = "Unknown";
				about_dialog_text = "We're sorry, an unexpected error occured.";
			}
			String about_title = "About";
			String ok = "OK";
			if(spanish){
				about_title = "sobre";
				ok = "Aceptar";
			}
        	AlertDialog about = LinkAlertDialog.create(WolfNSheep.this,about_title,about_dialog_text,ok);
        	about.show();
        	return true;
        }
        return false;
    }
	
	private void mpAuth(){
    	/*
		AlertDialog.Builder alert = new AlertDialog.Builder(WolfNSheep.this);
    	final SharedPreferences.Editor setedit = settings.edit();
    	alert.setTitle("Multiplayer Login: Username");
    	alert.setMessage("Please enter your multiplayer server username");

    	// Set an EditText view to get user input 
    	final EditText input = new EditText(WolfNSheep.this);
    	input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
    	alert.setView(input);

    	alert.setPositiveButton("Proceed to password", new DialogInterface.OnClickListener() {
    	public void onClick(DialogInterface dialog, int whichButton) {
    	mpUser = input.getText().toString();
    	setedit.putString("mpUser", mpUser);
    	setedit.commit();
    	AlertDialog.Builder alert = new AlertDialog.Builder(WolfNSheep.this);

    	alert.setTitle("Multiplayer Login: Password");
    	alert.setMessage("Please enter your multiplayer server password");

    	// Set an EditText view to get user input 
    	final EditText input = new EditText(WolfNSheep.this);
    	input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
    	alert.setView(input);

    	alert.setPositiveButton("Login", new DialogInterface.OnClickListener() {
    	public void onClick(DialogInterface dialog, int whichButton) {
    	mpPassword = SerializerClass.md5(input.getText().toString());
    	setedit.putString("mpPassword", mpPassword);
    	setedit.commit();
    	 }
    	});
    	 alert.show();
    	 }
    	});

    	alert.setNeutralButton("Register", new DialogInterface.OnClickListener() {
    	 public void onClick(DialogInterface dialog, int whichButton) {
    	     // Open webpage to register
    		 Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mpUrl+"registration.html"));
    		 startActivity(browserIntent);
    	}
    	});

    	 alert.show();
    	 */
		Intent login = new Intent(WolfNSheep.this, MultiplayerLogin.class);
		WolfNSheep.this.startActivity(login);
        finish();
    }
    
	/**
	 * Makes a URL from a string without the need for a try/catch.
	 * @see java.net.URL URL
	 * @author Glen Husman
	 * @return URL
	 */
	public static URL makeURL(String webaddress) {
		
		/*
		 * Makes a URL from a string
		 */
		
		URL website;
		try {
			website = new URL(webaddress);
		} catch (MalformedURLException e) {
			website = null;
			if(DEBUG) Log.e("URL", "Malformed URL Exception was thrown on string to URL conversion");
		}
	return website;
	}
	
	/**
	 * Downloads a text file and returns its contents as an array.
	 * @author Glen Husman
	 * @throws IOException 
	 */
	public static String[] downloadFile(URL website) throws IOException {
		BufferedReader in = null;
		try {
			in = new BufferedReader(
			              new InputStreamReader(
			              website.openStream()));
		} catch (IOException e) {
			in = null;
			e.printStackTrace();
			if(DEBUG) Log.w(TAG, "in is null!");
		}

		String input;
	    ArrayList<String> stringList = new ArrayList<String>();
	    
		try{
	      try {
			while ((input = in.readLine()) != null) {
			      stringList.add(input);
			  }
		} catch (IOException e) {
			stringList = new ArrayList<String>();
		}
		}catch (NullPointerException err){
			if(DEBUG) Log.e(TAG, "There has been an IO error, see previous log, throwing IOException.");
			throw new IOException();
		}
	      
	    String[] itemArray = new String[stringList.size()];
		String[] returnedArray = stringList.toArray(itemArray);
		return returnedArray;
		}
	
	String game_id;
	boolean game_id_valid;
	String language;
	boolean spanish = false;
	private AlertDialog.Builder aalert;
	private String gamestat;
	
	private void mpJoinGameNet(String game_id){
		WolfNSheep.this.game_id = game_id;
		String url = mpUrl+"join-game.php?username="+settings.getString("mpUser", null)+"&password="+settings.getString("mpPassword", null)+"&id="+game_id;
		// String pnum = downloadFile(makeURL(url))[0].replace("\n", "");
		load = new ProgressDialog(WolfNSheep.this);
        load.setTitle(WSMP_PROGRESS_TITLE);
        load.setMessage(WSMP_PROGRESS_MSG);
        load.setCancelable(false);
		load.show();
		(new JoinGame()).execute(makeURL(url));
		if(DEBUG) Log.i(TAG, "URL:"+url);
		/*
		 * try{
			mpPlayerNum = Integer.parseInt(pnum);
			if(DEBUG) Log.i(TAG, "mpPlayerNum is "+mpPlayerNum);
			game_id_valid = true;
		}catch(NumberFormatException err){
			if(pnum.contains("BAD_LOGIN")) LinkAlertDialog.create(WolfNSheep.this, "ERROR", "Your password was incorrect.", (spanish ? "Aceptar" : "OK")).show();
			else if(pnum.contains("BAD_ID")) LinkAlertDialog.create(WolfNSheep.this, "ERROR", "The game ID was not valid.", (spanish ? "Aceptar" : "OK")).show();
			else if(pnum.contains("BAD_GAME")) LinkAlertDialog.create(WolfNSheep.this, "ERROR", "The game is in use or doesn't exist.", (spanish ? "Aceptar" : "OK")).show();
			else LinkAlertDialog.create(WolfNSheep.this, "ERROR", "An error occurred.", (spanish ? "Aceptar" : "OK")).show();
			if(DEBUG) Log.w(TAG, "ERROR:"+pnum);
			game_id_valid = false;
		}
		if(game_id_valid){
			aalert = new AlertDialog.Builder(WolfNSheep.this);
			aalert.setCancelable(false);
	    	aalert.setTitle("Game Status");
	    	gamestat = downloadFile(makeURL(mpUrl+"game-state.php?id="+game_id))[0];
	    	String[] players_array_joined_game = downloadFile(makeURL(mpUrl+"joined.php?id="+game_id+"&username="+settings.getString("mpUser", null)+"&password="+settings.getString("mpPassword", null)));
	    	if(DEBUG) Log.d(TAG, "Game status:"+gamestat);
	    	String players_joined_game = "";
	    	for(String pjoined : players_array_joined_game){
	    		players_joined_game = players_joined_game + pjoined.replace("JOINED 1 ", "Player 1: ").replace("JOINED 2 ", "Player 2: ").replace("JOINED 3 ", "Player 3: ").replace("JOINED 4 ", "Player 4: ")+"\n";
	    	}
	    	StringBuilder pjg = new StringBuilder(players_joined_game);
	    	pjg.replace(players_joined_game.lastIndexOf("\n"), players_joined_game.lastIndexOf("\n") + 1, "" );
	    	players_joined_game = pjg.toString();
	    	String gamestat_user = gamestat.replace("STATUS ", "").replace("locked-game", "locked (players cannot join)").replace("open-game", "open (players can still join)");
	    	aalert.setMessage("You have joined game "+game_id+" as player "+mpPlayerNum+". This game is "+gamestat_user+".\nThe following players have joined the game:\n"+players_joined_game);
	    	final int orientation = getResources().getConfiguration().orientation;
	    	final TextView p1_label = (TextView)WolfNSheep.this.findViewById(R.id.p1_label);
	        final TextView p2_label = (TextView)WolfNSheep.this.findViewById(R.id.p2_label);
	        final TextView p3_label = (TextView)WolfNSheep.this.findViewById(R.id.p3_label);
	        final TextView p4_label = (TextView)WolfNSheep.this.findViewById(R.id.p4_label);
		    aalert.setPositiveButton((spanish ? "Aceptar" : "OK"), new DialogInterface.OnClickListener() {
		    protected void deactivatep1(){
		    	p1_label.setText("P1");
	    		p1_label.setTypeface(Typeface.DEFAULT);
	    		p1_wool_text.setTypeface(Typeface.DEFAULT);
		    }
		    	
		    public void onClick(DialogInterface dialog, int whichButton) {
		    	gamestat = downloadFile(makeURL(mpUrl+"game-state.php?id="+WolfNSheep.this.game_id))[0];
		    	if(!gamestat.contains("locked-game")){
		    		dialog.cancel();
		    		Toast.makeText(WolfNSheep.this.getBaseContext(), "Game not ready!", Toast.LENGTH_SHORT).show();
		    		aalert.show();
		    	}else{
		    	// Not a dead end anymore!
		    	String extratext = "";
				if(orientation != Configuration.ORIENTATION_LANDSCAPE){
		    		extratext = " (You)";
		    	}
		    	if(mpPlayerNum == 2){
		    		deactivatep1();
		    		p2_label.setText("P2"+extratext);
		    		p2_label.setTypeface(Typeface.DEFAULT_BOLD);
		    		p2_wool_text.setTypeface(Typeface.DEFAULT_BOLD);
		    	}else if(mpPlayerNum == 3){
		    		deactivatep1();
		    		p3_label.setText("P3"+extratext);
		    		p3_label.setTypeface(Typeface.DEFAULT_BOLD);
		    		p3_wool_text.setTypeface(Typeface.DEFAULT_BOLD);
		    	}else if(mpPlayerNum == 4){
		    		deactivatep1();
		    		p4_label.setText("P4"+extratext);
		    		p4_label.setTypeface(Typeface.DEFAULT_BOLD);
		    		p4_wool_text.setTypeface(Typeface.DEFAULT_BOLD);
		    	}
		    }
		    }
		    });
		    aalert.setNeutralButton("Update", new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int whichButton) {
			    	dialog.cancel();
			    	String gamestat = downloadFile(makeURL(mpUrl+"game-state.php?id="+WolfNSheep.this.game_id))[0];
			    	String[] players_array_joined_game = downloadFile(makeURL(mpUrl+"joined.php?id="+WolfNSheep.this.game_id+"&username="+settings.getString("mpUser", null)+"&password="+settings.getString("mpPassword", null)));
			    	if(DEBUG) Log.d(TAG, "Game status:"+gamestat);
			    	String players_joined_game = "";
			    	for(String pjoined : players_array_joined_game){
			    		players_joined_game = players_joined_game + pjoined.replace("JOINED 1 ", "Player 1: ").replace("JOINED 2 ", "Player 2: ").replace("JOINED 3 ", "Player 3: ").replace("JOINED 4 ", "Player 4: ")+"\n";
			    	}
			    	StringBuilder pjg = new StringBuilder(players_joined_game);
			    	pjg.replace(players_joined_game.lastIndexOf("\n"), players_joined_game.lastIndexOf("\n") + 1, "" );
			    	players_joined_game = pjg.toString();
			    	String gamestat_user = gamestat.replace("STATUS ", "").replace("locked-game", "locked (players cannot join)").replace("open-game", "open (players can still join)");
			    	aalert.setMessage("You have joined game "+WolfNSheep.this.game_id+" as player "+mpPlayerNum+". This game is "+gamestat_user+".\nThe following players have joined the game:\n"+players_joined_game);
			    	aalert.show();
			    }
			    });
	    	 aalert.show();
		}
		 */
	}
		
    void joinGame(){
    	if(settings.getString("mpUser", null) == null && settings.getString("mpPassword", null) == null){
    		mpAuth();
    	}else{
    	
    	AlertDialog.Builder alert = new AlertDialog.Builder(WolfNSheep.this);
    	alert.setCancelable(false);
    	alert.setTitle((spanish ? "Unirse a partida" : "Join Game"));
    	alert.setMessage((spanish ? "Por favor, introduzca el ID de juego" : "Please enter the game ID"));

    	// Set an EditText view to get user input 
    	final EditText input = new EditText(WolfNSheep.this);
    	input.setInputType(InputType.TYPE_CLASS_NUMBER);
    	alert.setView(input);

    	alert.setPositiveButton((spanish ? "Aceptar" : "OK"), new DialogInterface.OnClickListener() {
    	public void onClick(DialogInterface dialog, int whichButton) {
    		String game_id = input.getText().toString();
    		mpJoinGameNet(game_id);
    	}
    	});

    	 alert.show();
    	}
    }
    
    void createGame(){
    	if(settings.getString("mpUser", null) == null && settings.getString("mpPassword", null) == null){
    		mpAuth();
    	}else{
    	load.show();
    	(new CreateGame()).execute(makeURL(mpUrl+"game-start.php?username="+settings.getString("mpUser", null)+"&password="+settings.getString("mpPassword", null)));
    	}
    }
    
    private class CreateGame extends AsyncTask<URL, Void, String> {
        @Override
    	protected String doInBackground(URL... urls) {
        	// downloadFile(makeURL(mpUrl+"game-start.php?username="+settings.getString("mpUser", null)+"&password="+settings.getString("mpPassword", null)))[0].replace("\n", "")
        	String res;
        	try{
        		res = downloadFile(urls[0])[0].replace("\n", "");
        	}catch(IOException e){
        		res = "BAD_LOGIN";
        		if(DEBUG) Log.w(TAG, "Error getting game ID, set ID to BAD_LOGIN", e);
        	}
        	return res;
        }

        @Override
        protected void onPostExecute(String result) {
        	load.cancel();
        	AlertDialog.Builder alert = new AlertDialog.Builder(WolfNSheep.this);
        	alert.setCancelable(false);
        	final String id = result;
        	alert.setTitle((spanish ? "Hacer juego" : "Make Game"));
        	if(!id.contains("BAD_LOGIN")){
        	alert.setMessage((spanish ? "Su número de identificación del juego es "+id+". Dile a tus amigos a unirse a este juego." : "Your game ID is "+id+". Tell your friends to join this game."));

        	alert.setPositiveButton((spanish ? "Aceptar" : "OK"), new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int whichButton) {
        		mpJoinGameNet(id);
        	}
        	});
        	}else{
        		alert.setMessage((spanish ? "Se produjo un error (intentar comprobar su nombre de usuario y la conexión con el servidor)." : "An error occurred (try checking your login and connection to server)."));

            	alert.setPositiveButton((spanish ? "Aceptar" : "OK"), new DialogInterface.OnClickListener() {
            	public void onClick(DialogInterface dialog, int whichButton) {
            	}
            	});
        	}
        	alert.show();
        }
    }
    
    private class UpdateGameDialog extends AsyncTask<Void, Void, String[][]> {
        @Override
    	protected String[][] doInBackground(Void... vv) {
        	// downloadFile(makeURL(mpUrl+"game-start.php?username="+settings.getString("mpUser", null)+"&password="+settings.getString("mpPassword", null)))[0].replace("\n", "")
        	String[][] ret;
        	try{
        	ret = new String[][]{downloadFile(makeURL(mpUrl+"game-state.php?id="+WolfNSheep.this.game_id)), downloadFile(makeURL(mpUrl+"joined.php?id="+WolfNSheep.this.game_id+"&username="+settings.getString("mpUser", null)+"&password="+settings.getString("mpPassword", null)))};
        	}catch(IOException e){
        		ret = null;
        		if(DEBUG) Log.w(TAG, "IO error in game dialog update.", e);
        	}
        	return ret;
        }

        @Override
        protected void onPostExecute(String[][] gamestats) {
        	load.cancel();
        	if(gamestats != null){
        	WolfNSheep.this.gamestat = gamestats[0][0];
        	String[] players_array_joined_game = gamestats[1];
        	if(DEBUG) Log.d(TAG, "Game status:"+gamestat);
	    	String players_joined_game = "";
	    	for(String pjoined : players_array_joined_game){
	    		players_joined_game = players_joined_game + pjoined.replace("JOINED 1 ", (spanish ? "Jugador 1: " : "Player 1: ")).replace("JOINED 2 ", (spanish ? "Jugador 2: " : "Player 2: ")).replace("JOINED 3 ", (spanish ? "Jugador 3: " : "Player 3: ")).replace("JOINED 4 ", (spanish ? "Jugador 4: " : "Player 4: "))+"\n";
	    	}
	    	StringBuilder pjg = new StringBuilder(players_joined_game);
	    	pjg.replace(players_joined_game.lastIndexOf("\n"), players_joined_game.lastIndexOf("\n") + 1, "" );
	    	players_joined_game = pjg.toString();
	    	String gamestat_user = gamestat.replace("STATUS ", "").replace("locked-game", "locked (players cannot join)").replace("open-game", "open (players can still join)");
	    	aalert.setMessage("You have joined game "+WolfNSheep.this.game_id+" as player "+mpPlayerNum+". This game is "+gamestat_user+".\nThe following players have joined the game:\n"+players_joined_game);
	    	aalert.show();
        	}else{
        		aalert.setMessage("An IO error in communication with the server occurred. Please try again later.");
        		aalert.show();
        	}
        }
    }
    
    protected void deactivatep1(){
    	final TextView p1_label = (TextView)WolfNSheep.this.findViewById(R.id.p1_label);
    	p1_label.setText("P1");
		p1_label.setTypeface(Typeface.DEFAULT);
		p1_wool_text.setTypeface(Typeface.DEFAULT);
    }
    
    private class JoinGameOK extends AsyncTask<Void, Void, String>{

		@Override
		protected String doInBackground(Void... params) {
			try {
				gamestat = downloadFile(makeURL(mpUrl+"game-state.php?id="+WolfNSheep.this.game_id))[0];
			} catch (IOException e) {
				gamestat = null;
				if(DEBUG) Log.w(TAG, "Error getting gamestat", e);
			}
			return gamestat;
    }
		
		@Override
        protected void onPostExecute(String gamestat) {
			load.cancel();
			final int orientation = getResources().getConfiguration().orientation;
	        final TextView p2_label = (TextView)WolfNSheep.this.findViewById(R.id.p2_label);
	        final TextView p3_label = (TextView)WolfNSheep.this.findViewById(R.id.p3_label);
	        final TextView p4_label = (TextView)WolfNSheep.this.findViewById(R.id.p4_label);
	    	if(gamestat != null){
	        if(!gamestat.contains("locked-game")){
	    		Toast.makeText(WolfNSheep.this.getBaseContext(), "Game not ready!", Toast.LENGTH_SHORT).show();
	    		aalert.show();
	    	}else{
	    	String extratext = "";
			if(orientation != Configuration.ORIENTATION_LANDSCAPE){
	    		extratext = " (You)";
	    	}
	    	if(mpPlayerNum == 2){
	    		deactivatep1();
	    		p2_label.setText("P2"+extratext);
	    		p2_label.setTypeface(Typeface.DEFAULT_BOLD);
	    		p2_wool_text.setTypeface(Typeface.DEFAULT_BOLD);
	    	}else if(mpPlayerNum == 3){
	    		deactivatep1();
	    		p3_label.setText("P3"+extratext);
	    		p3_label.setTypeface(Typeface.DEFAULT_BOLD);
	    		p3_wool_text.setTypeface(Typeface.DEFAULT_BOLD);
	    	}else if(mpPlayerNum == 4){
	    		deactivatep1();
	    		p4_label.setText("P4"+extratext);
	    		p4_label.setTypeface(Typeface.DEFAULT_BOLD);
	    		p4_wool_text.setTypeface(Typeface.DEFAULT_BOLD);
	    	}
		}}else{
			LinkAlertDialog.create(WolfNSheep.this, "ERROR", "An error occurred.", (spanish ? "Aceptar" : "OK")).show();
		}
        }
    }
    private class JoinGame extends AsyncTask<URL, Void, String[]> {
        
    	private String[] pvjs22;
    	
    	@Override
    	protected String[] doInBackground(URL... urls) {
        	// downloadFile(makeURL(mpUrl+"game-start.php?username="+settings.getString("mpUser", null)+"&password="+settings.getString("mpPassword", null)))[0].replace("\n", "")
    		String[] ret = null;
    		try{
    		ret = new String[]{downloadFile(urls[0])[0].replace("\n", ""), downloadFile(makeURL(mpUrl+"game-state.php?id="+game_id))[0]};
        	pvjs22 = downloadFile(makeURL(mpUrl+"joined.php?id="+game_id+"&username="+settings.getString("mpUser", null)+"&password="+settings.getString("mpPassword", null)));
    		}catch (IOException e){
    			if(DEBUG) Log.w(TAG, "We have an IO error.", e);
    		}
        	return ret;
        }

        @Override
        protected void onPostExecute(String[] res) {
        	load.cancel();
        	if(res != null){
        	String pnum = res[0];
        	try{
    			mpPlayerNum = Integer.parseInt(pnum);
    			if(DEBUG) Log.i(TAG, "mpPlayerNum is "+mpPlayerNum);
    			game_id_valid = true;
    		}catch(NumberFormatException err){
    			if(pnum.contains("BAD_LOGIN")) LinkAlertDialog.create(WolfNSheep.this, "ERROR", "Your password was incorrect.", (spanish ? "Aceptar" : "OK")).show();
    			else if(pnum.contains("BAD_ID")) LinkAlertDialog.create(WolfNSheep.this, "ERROR", "The game ID was not valid.", (spanish ? "Aceptar" : "OK")).show();
    			else if(pnum.contains("BAD_GAME")) LinkAlertDialog.create(WolfNSheep.this, "ERROR", "The game is in use or doesn't exist.", (spanish ? "Aceptar" : "OK")).show();
    			else LinkAlertDialog.create(WolfNSheep.this, "ERROR", "An error occurred.", (spanish ? "Aceptar" : "OK")).show();
    			if(DEBUG) Log.w(TAG, "ERROR ON:"+pnum);
    			game_id_valid = false;
    		}
        	
        	if(game_id_valid){
    			aalert = new AlertDialog.Builder(WolfNSheep.this);
    			aalert.setCancelable(false);
    	    	aalert.setTitle("Game Status");
    	    	gamestat = res[1];
    	    	String[] players_array_joined_game = pvjs22;
    	    	if(DEBUG) Log.d(TAG, "Game status:"+gamestat);
    	    	String players_joined_game = "";
    	    	for(String pjoined : players_array_joined_game){
    	    		players_joined_game = players_joined_game + pjoined.replace("JOINED 1 ", "Player 1: ").replace("JOINED 2 ", "Player 2: ").replace("JOINED 3 ", "Player 3: ").replace("JOINED 4 ", "Player 4: ")+"\n";
    	    	}
    	    	StringBuilder pjg = new StringBuilder(players_joined_game);
    	    	pjg.replace(players_joined_game.lastIndexOf("\n"), players_joined_game.lastIndexOf("\n") + 1, "" );
    	    	players_joined_game = pjg.toString();
    	    	String gamestat_user = gamestat.replace("STATUS ", "").replace("locked-game", "locked (players cannot join)").replace("open-game", "open (players can still join)");
    	    	aalert.setMessage("You have joined game "+game_id+" as player "+mpPlayerNum+". This game is "+gamestat_user+".\nThe following players have joined the game:\n"+players_joined_game);
    		    aalert.setPositiveButton((spanish ? "Aceptar" : "OK"), new DialogInterface.OnClickListener() {	
    		    public void onClick(DialogInterface dialog, int whichButton) {
    		    	dialog.cancel();
    		    	load = new ProgressDialog(WolfNSheep.this);
    		        load.setTitle(WSMP_PROGRESS_TITLE);
    		        load.setMessage(WSMP_PROGRESS_MSG);
    		        load.setCancelable(false);
    		        load.show();
    		        (new JoinGameOK()).execute();
    		    	
    		    
    		    }
    		    });
    		    aalert.setNeutralButton("Update", new DialogInterface.OnClickListener() {
    			    public void onClick(DialogInterface dialog, int whichButton) {
    			    	load.cancel();
    			    	(new UpdateGameDialog()).execute();
    			    	load = new ProgressDialog(WolfNSheep.this);
    			        load.setTitle(WSMP_PROGRESS_TITLE);
    			        load.setMessage(WSMP_PROGRESS_MSG);
    			        load.setCancelable(false);
    			    	load.show();
    			    }
    			    });
    	    	 aalert.show();
    		}
        }else{
        	LinkAlertDialog.create(WolfNSheep.this, "ERROR", "An error occurred during joining a multiplayer game.", (spanish ? "Aceptar" : "OK")).show();
        }
        }
    }
    
    /**
	 * Post data to a URL.
	 * @author Glen Husman
	 * @param url The URL to post data to.
	 * @param ids The array of IDs of POST variables.
	 * @param values The array of values of POST variables.
	 * @return The HTTP status code of the request.
	 */
	protected static int postData(String url, String[] ids, String[] values) {
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(url);

	    try {
	        if(ids.length-1 == values.length-1){
	    	// Add your data
	        final int len = ids.length-1;
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(len);
	        
	        for(int ii = 0; ii <= len; ii++){
	        nameValuePairs.add(new BasicNameValuePair(ids[ii], values[ii]));
	        }
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse result = httpclient.execute(httppost);
	        return result.getStatusLine().getStatusCode();
	        }
			return -1;
	    } catch (ClientProtocolException e) {
	    	return -1;
	    } catch (IOException e) {
	    	return -1;
	    }
	} 
    /*
    void createdGame(String id, String user, String password){
    	
    }
    */
    
	  private int random_number = 0;
	  private Button roll;

	  private int wool[] = new int[5];
	  private int sheared_wool[] = new int[5];
	  private int player_num;
	  private TextView logtext;
	  private int num_players = 4;
	  protected static final String TAG = "WolfNSheep";
	  private String[] players_did = {"","P1 is unknown, should refer to main text","None","None","None"};
	  private int total_wool;
	  protected static final int max_wool = 5;
	  protected static final int max_total_wool = 40;
      private Button shear;
      private Button wolf;
      private Button grow;
      private Button swap;
      // TODONE Fix dice (Also fix onClick functions for the dice)
	  public static String[] messages = 
	        { "Roll the die!",
      		"Shear sheep or grow wool.",
      		"Swap sheep or grow wool.",
      		"Send wolf or shear sheep.",
      		"Send wolf or swap sheep.", 
      		"Grow wool.",
      		"Grow 2 wool."
      		}
      ;
      
	  
	static final String WSMP_PROGRESS_TITLE = "Loading";
	static final String WSMP_PROGRESS_MSG = "Wolf 'N Sheep Multiplayer is loading...";
	private TextView text;
	private TextView p1_wool_text;
	private TextView p2_wool_text;
	private TextView p3_wool_text;
	private TextView p4_wool_text;
	private boolean autoshear_state;
	private boolean shearcosts_state;
	boolean ss_busy = false;
	private boolean criticalalerts_state;
	protected PlayerMode mode = null;
	
	private class MPScoreSubmit extends AsyncTask<String[], Void, Integer>{

		@Override
		protected Integer doInBackground(String[]... params) {
			ss_busy = true;
			String url = params[0][0];
			int status = postData(url, params[1], params[2]);
			return status;
		}
		
		@Override
		protected void onPostExecute(Integer status){
			load.cancel();
			ss_busy = false;
			if(DEBUG) Log.i(TAG, "Just POSTed data");
			if(status >= 400 || status == -1){
				LinkAlertDialog.create(WolfNSheep.this, "ERROR", "An error occurred during multiplayer.", (spanish ? "Aceptar" : "OK")).show();
			}
		}
		
	}
	
	private class MPGameover extends AsyncTask<Void, Void, Integer>{

		@Override
		protected Integer doInBackground(Void... params) {
			ss_busy = true;
			String[] ids = new String[]{"username", "password", "id"};
			String[] values = new String[]{settings.getString("mpUser", null), settings.getString("mpPassword", null), game_id};
			int status = postData(mpUrl+"gameover.php", ids, values);
			return status;
		}
		
		@Override
		protected void onPostExecute(Integer status){
			load.cancel();
			ss_busy = false;
			if(status >= 400 || status == -1){
				LinkAlertDialog.create(WolfNSheep.this, "ERROR", "An error occurred during multiplayer gameover.", (spanish ? "Aceptar" : "OK")).show();
			}
		}
		
	}
	
	
	private class MPRoll extends AsyncTask<Void, Void, Void>{
		
		private String turn;
		private boolean opengame;
		private String[] scores;
		private boolean error = false;
		
		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				turn = downloadFile(makeURL(mpUrl+"get-scores.php?turn=OK&id="+game_id))[0];
				opengame = downloadFile(makeURL(mpUrl+"game-state.php?id="+game_id))[0].contains("open-game");
				scores = downloadFile(makeURL(mpUrl+"get-scores.php?id="+game_id));
			} catch (IOException e) {
				if (DEBUG) Log.e(TAG, "IO error in MPRoll", e);
				error = true;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void na){
			load.cancel();
			if(opengame){
    			Toast.makeText(getBaseContext(), "Game not ready, players still joining!", Toast.LENGTH_SHORT).show();
    		}else if(error){
    			LinkAlertDialog.create(WolfNSheep.this, "ERROR", "A multiplayer error occurred.", (spanish ? "Aceptar" : "OK")).show();
    		}else{
    		if(DEBUG) Log.i(TAG, turn);
    		if(turn.contains(Integer.toString(mpPlayerNum)) && !ss_busy){
    		int len = scores.length;

            for (int i = 0; i < len; ++i) {
            	String parse = scores[i].replace("SWOOL1 ", "").replace("SWOOL2 ", "").replace("SWOOL3 ", "").replace("SWOOL4 ", "").replace("WOOL1 ", "").replace("WOOL2 ", "").replace("WOOL3 ", "").replace("WOOL4 ", "").replace("\n", "");
            	parse = parse.replaceAll("S", "");
            	// String parse = scores[i].split(" ")[1];
            	if(DEBUG) Log.i(TAG, "Going to try to parse int: "+parse);
            	try{
            		if((i+1) <= 4) wool[(i+1)] = Integer.parseInt(parse);
            	}catch(NumberFormatException e){
            		if(DEBUG) Log.w(TAG, "Error parsing score!", e);
            		wool[(i+1)] = 0;
            	}
            	try{
            		if((i+1) > 4) sheared_wool[i-3] = Integer.parseInt(parse);
            	}catch(NumberFormatException e){
            		if(DEBUG) Log.w(TAG, "Error parsing score!", e);
            		sheared_wool[i-3] = 0;
            	}
            }
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
    		updateText();
            text.setTextColor(Color.YELLOW);
        	if(shear.getVisibility() == View.GONE && wolf.getVisibility() == View.GONE && grow.getVisibility() == View.GONE && swap.getVisibility() == View.GONE){
        		random_number = randomNumber(1, 6);
            	if(DEBUG) Log.i(TAG, "Player (P1) rolled number "+Integer.toString(random_number)+" on the die, also known as a '"+messages[random_number]+"'");
            	makeInvisible();
            	text.setText(messages[random_number]);
            	roll();
        	}
    		}else{
    			Toast.makeText(getBaseContext(), "Not your turn!", Toast.LENGTH_SHORT).show();
    		}
    	}
		}
		
	}
	
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
        Log.w(TAG, "We are up and running. DEBUG is "+DEBUG+", if false, you shall not see any more log messages.");
        language = WolfNSheep.this.getResources().getConfiguration().locale.getLanguage();
        spanish = language.contains("es");
        // TODONE Implement auto-shear prefs checking here
        settings = getSharedPreferences("extras", 0);
	    autoshear_state = settings.getBoolean("autoshear", true);
	    shearcosts_state = settings.getBoolean("shearcosts", false);
	    criticalalerts_state = settings.getBoolean("criticalalerts", true);
	    if(DEBUG) Log.d(TAG, "Auto-shear preference is "+Boolean.toString(autoshear_state));
	    if(DEBUG) Log.d(TAG, "Shear costs preference is "+Boolean.toString(shearcosts_state));
	    if(DEBUG) Log.d(TAG, "Critical alerts preference is "+Boolean.toString(criticalalerts_state));
        WolfNSheep.this.setContentView(R.layout.main);
        WolfNSheep.this.p1_wool_text = (TextView)WolfNSheep.this.findViewById(R.id.p1_wool);
        WolfNSheep.this.p2_wool_text = (TextView)WolfNSheep.this.findViewById(R.id.p2_wool);
        TextView p1_label = (TextView)WolfNSheep.this.findViewById(R.id.p1_label);
        TextView p2_label = (TextView)WolfNSheep.this.findViewById(R.id.p2_label);
        TextView p3_label = (TextView)WolfNSheep.this.findViewById(R.id.p3_label);
        TextView p4_label = (TextView)WolfNSheep.this.findViewById(R.id.p4_label);
        load = new ProgressDialog(WolfNSheep.this);
        load.setTitle(WSMP_PROGRESS_TITLE);
        load.setMessage(WSMP_PROGRESS_MSG);
        load.setCancelable(false);
        WolfNSheep.this.logtext = (TextView) findViewById(R.id.computer_action_log);
        WolfNSheep.this.p3_wool_text = (TextView)WolfNSheep.this.findViewById(R.id.p3_wool);
        WolfNSheep.this.p4_wool_text = (TextView)WolfNSheep.this.findViewById(R.id.p4_wool);
        WolfNSheep.this.shear = (Button)WolfNSheep.this.findViewById(R.id.shear);
        WolfNSheep.this.wolf = (Button)WolfNSheep.this.findViewById(R.id.wolf);
        WolfNSheep.this.grow = (Button)WolfNSheep.this.findViewById(R.id.grow);
        WolfNSheep.this.swap = (Button)WolfNSheep.this.findViewById(R.id.swap);
        WolfNSheep.this.roll = (Button)WolfNSheep.this.findViewById(R.id.roll);
        WolfNSheep.this.text = (TextView)WolfNSheep.this.findViewById(R.id.text);
        text.setTextSize(16);
        text.setTextColor(Color.GREEN);
        String mp_or_sp = "Multiplayer or single-player?";
        String sp = "Single player";
        String mp = "Multiplayer";
        if(spanish){
        	messages = new String[]{ "Lanza el dado!",
              		"Esquilar ovejas o hacer crecer la lana.",
              		"Cambie las ovejas de lana o crecer.",
              		"Enviar lobo o esquilar ovejas.",
              		"Enviar lobo o una oveja de intercambio.", 
              		"Crecer lana.",
              		"Crecer 2 de lana."
              		};
        	mp_or_sp = "Múltiple o de un solo jugador?";
        	mp = "Multijugador";
        	sp = "de un solo jugador";
        }
        AlertDialog.Builder mp_alert = new AlertDialog.Builder(WolfNSheep.this);
        mp_alert.setMessage(mp_or_sp);
        mp_alert.setCancelable(false);
        mp_alert.setPositiveButton(sp,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// This just continues single-player
						mode = PlayerMode.SINGLEPLAYER;
						init_app();
					}
				});
        mp_alert.setNeutralButton(mp,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// TODONE Re-implement server-side multiplayer!
						// For the moment, there must be 4 players
						mode = PlayerMode.MULTIPLAYER_4P;
			        	/*
						if(mode == PlayerMode.MULTIPLAYER){
			            	// TODONE # of players selection dialog display
			        		AlertDialog.Builder mp_alert = new AlertDialog.Builder(WolfNSheep.this);
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
			        						// TODONE Finish multiplayer!
			        						mode = PlayerMode.MULTIPLAYER;
			        			        	if(mode == PlayerMode.MULTIPLAYER){
			        			            	// STILL_TODO # of players selection dialog display
			        			            }
			        						// STILL_TODO Get this to do something (like select # of players)
			        						// init_app();
			        					}
			        				});
			            }
			            */
						String jom = "Join game or make game?";
						String m = "Make game";
						String j = "Join game";
						String login = "Login to server";
						if(spanish){
				        	jom = "Unirse al juego o hacer juego?";
				        	m = "Hacer juego";
				        	j = "Unirse al juego";
				        	login = "Inicie sesión con el servidor";
				        }
						AlertDialog.Builder join_or_make = new AlertDialog.Builder(WolfNSheep.this);
						join_or_make.setCancelable(false);
						join_or_make.setTitle(jom);
						join_or_make.setPositiveButton(m,
		        				new DialogInterface.OnClickListener() {
		        					public void onClick(DialogInterface dialog, int whichButton) {
		        						createGame();
		        					}
		        				});
						join_or_make.setNeutralButton(j,
		        				new DialogInterface.OnClickListener() {
		        					public void onClick(DialogInterface dialog, int whichButton) {
		        						joinGame();
		        					}
		        				});
						join_or_make.setNegativeButton(login,
		        				new DialogInterface.OnClickListener() {
		        					public void onClick(DialogInterface dialog, int whichButton) {
		        						mpAuth();
		        					}
		        				});
						join_or_make.show();
						// For the moment, there must be 4 players
						// TODONE Get this to do something (like select # of players)
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
    	    final String[] players_did_saved = data_saved.getStringArray("players_did");
            wool = wool_saved;
            players_did = players_did_saved;
            shear.setVisibility(shear_visible);
            wolf.setVisibility(wolf_visible);
            grow.setVisibility(grow_visible);
            swap.setVisibility(swap_visible);
            text.setTextColor(text_color_saved);
            text.setText(random_num_saved);
            sheared_wool = sheared_wool_saved;
            mode = PlayerMode.values()[data_saved.getInt("pmode")];
            mpPlayerNum = data_saved.getInt("mpPlayerNum");
            mpUser = data_saved.getString("mpUser");
            mpPassword = data_saved.getString("mpPassword");
            game_id = data_saved.getString("game_id");
            logtext.setText(log_saved);
            if(mode != PlayerMode.SINGLEPLAYER){
            	String extratext = "";
				if(getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE){
		    		extratext = " (You)";
		    		if(spanish) extratext = " (Usted)";
		    	}
		    	if(mpPlayerNum == 2){
		    		deactivatep1();
		    		p2_label.setText("P2"+extratext);
		    		p2_label.setTypeface(Typeface.DEFAULT_BOLD);
		    		p2_wool_text.setTypeface(Typeface.DEFAULT_BOLD);
		    	}else if(mpPlayerNum == 3){
		    		deactivatep1();
		    		p3_label.setText("P3"+extratext);
		    		p3_label.setTypeface(Typeface.DEFAULT_BOLD);
		    		p3_wool_text.setTypeface(Typeface.DEFAULT_BOLD);
		    	}else if(mpPlayerNum == 4){
		    		deactivatep1();
		    		p4_label.setText("P4"+extratext);
		    		p4_label.setTypeface(Typeface.DEFAULT_BOLD);
		    		p4_wool_text.setTypeface(Typeface.DEFAULT_BOLD);
		    	}
            }
        }else{
        	mp_alert.show();
        }
		updateText();
		final AlertDialog.Builder alert = new AlertDialog.Builder(WolfNSheep.this);
		final AlertDialog.Builder alert2 = new AlertDialog.Builder(WolfNSheep.this);
		final AlertDialog.Builder alert3 = new AlertDialog.Builder(WolfNSheep.this);
		final AlertDialog.Builder alert4 = new AlertDialog.Builder(WolfNSheep.this);
		OnClickListener p1_pinfo = new OnClickListener() {
            public void onClick(View v) {
            	String pinfot = (spanish ? "información de jugadores" : "Player info");
            	String pinfo = "P1's wool: "+getStringData(Data.WOOL, 1)+"\n"+"P1's sheared wool: "+getStringData(Data.SHEARED_WOOL, 1);
            	if(spanish){
            		pinfo = "Lana de P1: "+getStringData(Data.WOOL, 1)+"\n"+"Lana esquilada de P1: "+getStringData(Data.SHEARED_WOOL, 1);
            	}
            	alert.setTitle(pinfot);
        		alert.setMessage(pinfo);
        		alert.setNeutralButton((spanish ? "Aceptar" : "OK"),
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
            	alert2.setNeutralButton((spanish ? "Aceptar" : "OK"),
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
            	alert3.setNeutralButton((spanish ? "Aceptar" : "OK"),
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
            	alert4.setNeutralButton((spanish ? "Aceptar" : "OK"),
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
            	if(mode == PlayerMode.SINGLEPLAYER){
            	init_app();
                text.setTextColor(Color.YELLOW);
            	if(!checkIfGameOver() && shear.getVisibility() == View.GONE && wolf.getVisibility() == View.GONE && grow.getVisibility() == View.GONE && swap.getVisibility() == View.GONE){
            		random_number = randomNumber(1, 6);
                	if(DEBUG) Log.i(TAG, "Player (P1) rolled number "+Integer.toString(random_number)+" on the die, also known as a '"+messages[random_number]+"'");
                	makeInvisible();
                	text.setText(messages[random_number]);
                	roll();
            	}
            	else if(!checkIfGameOver()) Toast.makeText(getBaseContext(), "No re-rolls!", Toast.LENGTH_LONG).show();
            	}else{
            		load = new ProgressDialog(WolfNSheep.this);
                    load.setTitle(WSMP_PROGRESS_TITLE);
                    load.setMessage(WSMP_PROGRESS_MSG);
                    load.setCancelable(false);
                    load.show();
                    (new MPRoll()).execute();
            	}
              }
            };
        WolfNSheep.this.roll.setOnClickListener(roll_action);
        }
    void init_app() {
    	final AlertDialog.Builder alert = new AlertDialog.Builder(WolfNSheep.this);
		alert.setTitle("Player selection");
		alert.setMessage("You have "+Integer.toString(wool[mpPlayerNum])+" wool.\nWho would you like to swap sheep with?");
		alert.setCancelable(false);
		if(mpPlayerNum == 1){
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
		}else if(mpPlayerNum == 2){
			alert.setPositiveButton("P1 ("+Integer.toString(wool[1])+" wool)", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
	            	swap(1);
	            	text.setText("You swapped with P1! Roll again!");
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
		}else if(mpPlayerNum == 3){
			alert.setPositiveButton("P1 ("+Integer.toString(wool[1])+" wool)", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
	            	swap(1);
	            	text.setText("You swapped with P1! Roll again!");
	        		otherplayerrolls();
				}
			});

			alert.setNeutralButton("P2 ("+Integer.toString(wool[2])+" wool)",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
	                    	swap(2);
	                    	text.setText("You swapped with P2! Roll again!");
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
		}else if(mpPlayerNum == 4){
			alert.setPositiveButton("P1 ("+Integer.toString(wool[1])+" wool)",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
                	swap(1);
                	text.setText("You swapped with P1! Roll again!");
            		otherplayerrolls();
				}
			});

			alert.setNeutralButton("P2 ("+Integer.toString(wool[2])+" wool)", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
	            	swap(2);
	            	text.setText("You swapped with P2! Roll again!");
	        		otherplayerrolls();
				}
			});
			alert.setNegativeButton("P3 ("+Integer.toString(wool[3])+" wool)",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
                	swap(3);
                	text.setText("You swapped with P3! Roll again!");
            		otherplayerrolls();
				}
			});
		}
				
		WolfNSheep.this.swap.setOnClickListener(new OnClickListener() {	
			public void onClick(View v) {
				makeInvisible();
        		alert.show();
            }
        }
          );
        WolfNSheep.this.grow.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	wool[mpPlayerNum] = wool[mpPlayerNum] + 1;
            	if(wool[mpPlayerNum] > max_wool){
                	wool[mpPlayerNum] = max_wool;
                	Toast.makeText(getBaseContext(), "Cannot have more than "+Integer.toString(max_wool)+" wool on your sheep! Please roll again!", Toast.LENGTH_LONG).show();
                }
            	text.setText("You grew! Roll again!");
            	updateTextOnly();
            	makeInvisible();
        		otherplayerrolls();

            }
          });
        WolfNSheep.this.shear.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	shearWool(mpPlayerNum);
            	updateTextOnly();
            	makeInvisible();
        		otherplayerrolls();

            }
          });
        final AlertDialog.Builder wolf_alert = new AlertDialog.Builder(WolfNSheep.this);
        wolf_alert.setTitle("Player selection");
        wolf_alert.setMessage("You have "+getStringData(Data.WOOL, mpPlayerNum)+" wool.\nWho would you like to send the wolf to?");
        wolf_alert.setCancelable(false);
        if(mpPlayerNum == 1){
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
        }else if(mpPlayerNum == 2){
            wolf_alert.setPositiveButton("P1 ("+getStringData(Data.WOOL, 1)+" wool)", new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton) {
    				wool[1] = 0;
    				text.setText("You wolfed P1! Roll again!");
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
            }else if(mpPlayerNum == 3){
                wolf_alert.setPositiveButton("P1 ("+getStringData(Data.WOOL, 1)+" wool)", new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int whichButton) {
        				wool[1] = 0;
        				text.setText("You wolfed P1! Roll again!");
        				otherplayerrolls();
        			}
        		});

                wolf_alert.setNeutralButton("P2 ("+getStringData(Data.WOOL, 2)+" wool)", new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int whichButton) {
        				wool[2] = 0;
        				text.setText("You wolfed P2! Roll again!");
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
                }else if(mpPlayerNum == 4){
                	wolf_alert.setPositiveButton("P1 ("+getStringData(Data.WOOL, 1)+" wool)", new DialogInterface.OnClickListener() {
            			public void onClick(DialogInterface dialog, int whichButton) {
            				wool[1] = 0;
            				text.setText("You wolfed P1! Roll again!");
            				otherplayerrolls();
            			}
            		});

                    wolf_alert.setNeutralButton("P2 ("+getStringData(Data.WOOL, 2)+" wool)", new DialogInterface.OnClickListener() {
            			public void onClick(DialogInterface dialog, int whichButton) {
            				wool[2] = 0;
            				text.setText("You wolfed P2! Roll again!");
            				otherplayerrolls();
            			}
            		});
                    wolf_alert.setNegativeButton("P3 ("+getStringData(Data.WOOL, 3)+" wool)",
    				new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog, int whichButton) {
                        	wool[3] = 0;
                        	text.setText("You wolfed P3! Roll again!");
                        	otherplayerrolls();
    					}
    				});
                }
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
			/*
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
            final AlertDialog.Builder restarting_conf = new AlertDialog.Builder(WolfNSheep.this);
            restarting_conf.setTitle("New game?");
            restarting_conf.setMessage("Start a new game?");
            restarting_conf.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					Intent main = getIntent();
		            main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		            finish();
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
			TextView winner_text = (TextView)WolfNSheep.this.findViewById(R.id.winner);
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
						if(DEBUG) Log.e(TAG, "We had an array error here (in the additional tie-checking verifiers [if statements])", array_error);
					}
					*/
				} else if ((wool[player_num]+sheared_wool[player_num]) > winning_score) {
					winner = "P"+Integer.toString(player_num);
					winner_player_num = player_num;
					winning_score = wool[player_num]+sheared_wool[player_num];
				}
			}
			if(DEBUG) Log.i(TAG,"Tied between array shows these tie statistics: "+tie_between[1]+", "+tie_between[2]+", "+tie_between[3]+", "+tie_between[4]);
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
					if(winner_final == mpPlayerNum){
						share_text = "I got a 1st place winning high score of "+Integer.toString(sheared_wool[mpPlayerNum])+" wool on wolf 'n sheep. Think you can beat it?";
					}else if(ways_tie > 1 && tie_between[mpPlayerNum] >= 1){
						share_text = "I tied with a 1st place high score of "+Integer.toString(sheared_wool[mpPlayerNum])+" wool on wolf 'n sheep. Think you can beat it?";
					}
					else{
						share_text = "I got a high score of "+Integer.toString(sheared_wool[mpPlayerNum])+" wool on wolf 'n sheep. Think you can beat it?";
					}
					Intent sharingIntent = new Intent();
					sharingIntent.setAction(Intent.ACTION_SEND);
					sharingIntent.putExtra(Intent.EXTRA_TEXT, share_text);
					sharingIntent.setType("text/plain");
				    startActivity(Intent.createChooser(sharingIntent,"Share high score using"));
				}});
			if(winner_final == mpPlayerNum){
				text.setText("Game over! You won!");
			}else if(/*tie_between[0] == 1 || tie_between[1] == 1)*/tie_between[mpPlayerNum] == 1){
				text.setText("Game over! You tied!");
			}
			gameover =  true;
			if(mode != PlayerMode.SINGLEPLAYER){
				load = new ProgressDialog(WolfNSheep.this);
		        load.setTitle(WSMP_PROGRESS_TITLE);
		        load.setMessage(WSMP_PROGRESS_MSG);
		        load.setCancelable(false);
		        load.show();
				new MPGameover().execute();
			}
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
		if(getData(Data.WOOL, mpPlayerNum) >= max_wool && autoshear_state){
        	/* TODONE Have an option to enable "special" features not in standard ruleset, like this
        	 * Commented because it is not standard rules, can easily make a preference for enabling this (and other modifications).
        	 * Uncommented because CPU does it
        	 */
    		shearWool(mpPlayerNum);
        	Toast.makeText(getBaseContext(), "Auto-sheared a full sheep!", Toast.LENGTH_SHORT).show();
        	updateTextOnly();
			//Toast.makeText(getBaseContext(), "Cannot have more than "+Integer.toString(max_wool)+" wool on your sheep!", Toast.LENGTH_LONG).show();
        }else if(getData(Data.WOOL, mpPlayerNum) >= max_wool && !autoshear_state){
        	Toast.makeText(getBaseContext(), "Cannot have more than "+Integer.toString(max_wool)+" wool on your sheep!", Toast.LENGTH_LONG).show();
        	wool[mpPlayerNum] = max_wool;
        	updateTextOnly();
        }
		if(random_number == 6){
    		wool[mpPlayerNum] += 2;
    		if (wool[mpPlayerNum] > max_wool) {
    			wool[mpPlayerNum] = max_wool;
    		}
    		updateTextOnly();
    		text.setText("You grew 2! Roll again!");
    		otherplayerrolls();
    	}
    	else if(random_number == 5){
    		wool[mpPlayerNum]++;
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
        WolfNSheep.this.shear.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	shearWool(mpPlayerNum);
            	makeInvisible();
        		otherplayerrolls();
            }
          });
        
        updateText();
	}

	protected void otherplayerrolls() {
		if(mode == PlayerMode.MULTIPLAYER || mode == PlayerMode.MULTIPLAYER_2P || mode == PlayerMode.MULTIPLAYER_3P || mode == PlayerMode.MULTIPLAYER_4P){
			// TODONE Do something special for multiplayer
			String[] ids = new String[]{"player", "username", "password", "id", "p1wool", "p1shearedwool", "p2wool", "p2shearedwool", "p3wool", "p3shearedwool", "p4wool", "p4shearedwool"};
			String[] values = new String[]{Integer.toString(mpPlayerNum), settings.getString("mpUser", null), settings.getString("mpPassword", null), game_id, Integer.toString(wool[1]), Integer.toString(sheared_wool[1]), Integer.toString(wool[2]), Integer.toString(sheared_wool[2]), Integer.toString(wool[3]), Integer.toString(sheared_wool[3]), Integer.toString(wool[4]), Integer.toString(sheared_wool[4])};
			load = new ProgressDialog(WolfNSheep.this);
	        load.setTitle(WSMP_PROGRESS_TITLE);
	        load.setMessage(WSMP_PROGRESS_MSG);
	        load.setCancelable(false);
	        load.show();
			(new MPScoreSubmit()).execute(new String[]{mpUrl+"game.php"}, ids, values);
			}else{
			// This is singleplayer
		int random_number_p2 = randomNumber(1, 6);
		String p2logtext = "Computer 2 (P2) rolled number "+Integer.toString(random_number_p2)+" on the die, also known as a '"+messages[random_number_p2]+"'";
		if(DEBUG) Log.i(TAG, p2logtext);
		int random_number_p3 = randomNumber(1, 6);
		String p3logtext = "Computer 3 (P3) rolled number "+Integer.toString(random_number_p3)+" on the die, also known as a '"+messages[random_number_p3]+"'";
		if(DEBUG) Log.i(TAG, p3logtext);
		int random_number_p4 = randomNumber(1, 6);
		String p4logtext = "Computer 4 (P4) rolled number "+Integer.toString(random_number_p4)+" on the die, also known as a '"+messages[random_number_p4]+"'";
		if(DEBUG) Log.i(TAG, p4logtext);
		players_did[2] = p_action(2, random_number_p2);
		players_did[3] = p_action(3, random_number_p3);
		players_did[4] = p_action(4, random_number_p4);
		}
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
		// Now uses table format
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
			/*
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
			list.add(1);
			list.add(2);
			list.add(3);
			list.add(4);
			Collections.shuffle(list);
			Integer[] random = new Integer[]{0,0,0,0};
			random[0] = list.remove(0);
			random[1] = list.remove(0);
			random[2] = list.remove(0);
			random[3] = list.remove(0);
			int player_swap = 0;
			for(int rchecked : random){
				if(wool[rchecked] >= (wool[num_player]+2) && rchecked != num_player){
					player_swap = rchecked;
				}
			}
			int most_wool = -1;
			int who_most_wool = 0;
			List<Integer> list2 = new LinkedList<Integer>();
			list2.add(1);
			list2.add(2);
			list2.add(3);
			list2.add(4);
			Collections.shuffle(list2);
			Integer[] random2 = new Integer[]{0,0,0,0};
			random2[0] = list2.remove(0);
			random2[1] = list2.remove(0);
			random2[2] = list2.remove(0);
			random2[3] = list2.remove(0);
			for(int rchecked2 : random2) {
				if ((wool[rchecked2]) > most_wool && rchecked2 != num_player) {
					who_most_wool = rchecked2;
					most_wool = wool[who_most_wool];
				}
			}
			if(player_swap != 0){
				final int player_swap_old_wool = wool[player_swap];
				final int num_player_old_wool = wool[num_player];
				wool[player_swap] = num_player_old_wool;
				wool[num_player] = player_swap_old_wool;
				returnvalue = "P"+num_player.toString()+" swapped with P"+Integer.toString(player_swap)+".";
				if(criticalalerts_state && player_swap == mpPlayerNum){
					LinkAlertDialog.create(WolfNSheep.this, "You got swapped!", "P"+num_player.toString()+" swapped with you!", (spanish ? "Aceptar" : "OK")).show();
				}
			}else{
				wool[who_most_wool] = 0;
				returnvalue = "P"+Integer.toString(num_player)+" wolfed P"+Integer.toString(who_most_wool)+".";
				if(criticalalerts_state && who_most_wool == mpPlayerNum){
					LinkAlertDialog.create(WolfNSheep.this, "You got wolfed!", "P"+num_player.toString()+" wolfed you!", (spanish ? "Aceptar" : "OK")).show();
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
			list3.add(1);
			list3.add(2);
			list3.add(3);
			list3.add(4);
			Collections.shuffle(list3);
			Integer[] random3 = new Integer[]{0,0,0,0};
			random3[0] = list3.remove(0);
			random3[1] = list3.remove(0);
			random3[2] = list3.remove(0);
			random3[3] = list3.remove(0);
			for(int rchecked3 : random3){
					if(wool[rchecked3] >= 4 && rchecked3 != num_player){
						player_wolf = rchecked3;
					}
				  }
			// TODONE Eventually: if opponent has 4-5 wool, wolf him; for now, just grow - we could use a loop for this
			if(player_wolf > 0 && player_wolf < 5){
				wool[player_wolf] = 0;
				returnvalue = "P"+Integer.toString(num_player)+" wolfed P"+Integer.toString(player_wolf)+".";
				if(criticalalerts_state && player_wolf == mpPlayerNum){
					LinkAlertDialog.create(WolfNSheep.this, "You got wolfed!", "P"+num_player.toString()+" wolfed you!", (spanish ? "Aceptar" : "OK")).show();
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
			list4.add(1);
			list4.add(2);
			list4.add(3);
			list4.add(4);
			Collections.shuffle(list4);
			Integer[] random4 = new Integer[]{0,0,0,0};
			random4[0] = list4.remove(0);
			random4[1] = list4.remove(0);
			random4[2] = list4.remove(0);
			random4[3] = list4.remove(0);
			for(int rchecked4 : random4){
				if(wool[rchecked4] >= (wool[rchecked4]+2) && rchecked4 != num_player){
					player_swap_alt = rchecked4;
				}
			}
			if(player_swap_alt != 0){
				final int player_swap_old_wool = wool[player_swap_alt];
				final int num_player_old_wool = wool[num_player];
				wool[player_swap_alt] = num_player_old_wool;
				wool[num_player] = player_swap_old_wool;
				returnvalue = "P"+Integer.toString(num_player)+" swapped with P"+Integer.toString(player_swap_alt)+".";
				if(criticalalerts_state && player_swap_alt == mpPlayerNum){
					LinkAlertDialog.create(WolfNSheep.this, "You got swapped!", "P"+num_player.toString()+" swapped with you!", (spanish ? "Aceptar" : "OK")).show();
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
	 * Shears mpPlayerNum's wool.
	 * @deprecated Use {@link #shearWool(int)} instead.
	 * @param go Whether to shear as if it is game over
	 * @author Glen Husman & Matt Husman
	 */
	protected void shearWool(boolean go){
		if(!go){
			shearWool(1);
		}else{
			shearWoolGameover(1);
		}
    	updateTextOnly();
	}
	
	void shearWoolGameover(int num_player){
		CharSequence old_text = text.getText();
		shearWool(num_player);
		if(num_player == mpPlayerNum){
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
		if(num_player == mpPlayerNum){
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
		final int temp_wool = wool[mpPlayerNum];
		wool[mpPlayerNum] = wool[player];
		wool[player] = temp_wool;
		// We might be able to make the "wool_text" into arrays eventually
		updateText();		
		}

    }


package org.glen_h.games.wolfnsheep;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Extras extends ListActivity {

	protected static final String PREFS_NAME = "extras";
    String current_state_shearcosts;
    String current_state_autoshear;
    String current_state_criticalalerts;
	AlertDialog.Builder autoshear;
	AlertDialog.Builder shearcosts;
	SharedPreferences settings;
	AlertDialog.Builder criticalalerts;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		final String[] items = new String[]{"Auto-shear","Shearing costs","Critical alerts"};
		setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, items));
		  ListView lv = getListView();
		  lv.setTextFilterEnabled(true);
		  settings = getSharedPreferences(PREFS_NAME, 0);
	      boolean autoshear_state = settings.getBoolean("autoshear", true);
	      boolean shearcosts_state = settings.getBoolean("shearcosts", false);
	      boolean criticalalerts_state = settings.getBoolean("criticalalerts", true);
	      final SharedPreferences.Editor editor = settings.edit();
		  autoshear = new AlertDialog.Builder(this);
		  criticalalerts = new AlertDialog.Builder(this);
		  final AlertDialog.Builder restart = new AlertDialog.Builder(this);
		  restart.setTitle("Restart Required");
		  restart.setMessage("To apply changes, please restart wolf 'n sheep.");
		  restart.setNegativeButton("Restart Later", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {}
			});
		  restart.setPositiveButton("Restart Now", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					  Intent main = new Intent(getBaseContext(), WolfNSheep.class);
					  main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			          startActivity(main);
				}
			});
		  shearcosts = new AlertDialog.Builder(this);
		  if(autoshear_state){
			  current_state_autoshear = "Enabled";
		  }else{
			  current_state_autoshear = "Disabled";
		  }
		  if(shearcosts_state){
			  current_state_shearcosts = "Enabled";
		  }else{
			  current_state_shearcosts = "Disabled";
		  }
		  if(criticalalerts_state){
			  current_state_criticalalerts = "Enabled";
		  }else{
			  current_state_criticalalerts = "Disabled";
		  }
		  autoshear.setTitle(items[0]);
		  autoshear.setMessage("Automatically shear when you have 5 wool at the beginning of your turn\nCurrrent Preference: "+current_state_autoshear);
		  autoshear.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
	            	// Enable extra
					final boolean old_autoshear = settings.getBoolean("autoshear", true);
					editor.putBoolean("autoshear", true);
					editor.commit();
					current_state_autoshear = "Enabled";
					autoshear.setMessage("Automatically shear when you have 5 wool at the beginning of your turn\nCurrrent Preference: "+current_state_autoshear);
					if(old_autoshear != true){
					restart.show();
					}
				}
			});
		  autoshear.setNegativeButton("Disable",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							// Disable extra
							final boolean old_autoshear = settings.getBoolean("autoshear", true);
							editor.putBoolean("autoshear", false);
							editor.commit();
							current_state_autoshear = "Disabled";
							autoshear.setMessage("Automatically shear when you have 5 wool at the beginning of your turn\nCurrrent Preference: "+current_state_autoshear);
							if(old_autoshear != false){
								restart.show();
							}
						}
					});
		  criticalalerts.setTitle(items[2]);
		  criticalalerts.setMessage("Alert you when something critical happens to your sheep\nCurrrent Preference: "+current_state_criticalalerts);
		  // TODO Put buttons here
		  criticalalerts.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
	            	// Enable extra
					final boolean old_criticalalerts = settings.getBoolean("criticalalerts", true);
					editor.putBoolean("criticalalerts", true);
					editor.commit();
					current_state_criticalalerts = "Enabled";
					criticalalerts.setMessage("Alert you when something critical happens to your sheep\nCurrrent Preference: "+current_state_criticalalerts);
					if(old_criticalalerts != true){
					restart.show();
					}
				}
			});
		  criticalalerts.setNegativeButton("Disable",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							// Disable extra
							final boolean old_criticalalerts = settings.getBoolean("criticalalerts", true);
							editor.putBoolean("criticalalerts", false);
							editor.commit();
							current_state_criticalalerts = "Disabled";
							criticalalerts.setMessage("Alert you when something critical happens to your sheep\nCurrrent Preference: "+current_state_criticalalerts);
							if(old_criticalalerts != false){
								restart.show();
							}
						}
					});
		  
		  shearcosts.setTitle(items[1]);
		  shearcosts.setMessage("Add a cost of 1 wool to shear\nCurrrent Preference: "+current_state_shearcosts);
		  shearcosts.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
	            	// Enable extra
					final boolean old_shearcosts = settings.getBoolean("shearcosts", false);
					editor.putBoolean("shearcosts", true);
					editor.commit();
					current_state_shearcosts = "Enabled";
					shearcosts.setMessage("Add a cost of 1 wool to shear\nCurrrent Preference: "+current_state_shearcosts);
					if(old_shearcosts != true){
					restart.show();
					}
				}
			});
		  shearcosts.setNegativeButton("Disable",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							// Disable extra
							final boolean old_shearcosts = settings.getBoolean("shearcosts", false);
							editor.putBoolean("shearcosts", false);
							editor.commit();
							current_state_shearcosts = "Disabled";
							shearcosts.setMessage("Add a cost of 1 wool to shear\nCurrrent Preference: "+current_state_shearcosts);
							if(old_shearcosts != false){
								restart.show();
							}
						}
					});
		  lv.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view,
		        int position, long id) {
		      // When clicked, show a toast with the TextView text
		      TextView tv = (TextView) view;
		      if(tv.getText() == items[0]){
		    	  autoshear.show();
		  	  }else if(tv.getText() == items[1]){
		  		shearcosts.show();
		  	  }
		  	else if(tv.getText() == items[2]){
		  		criticalalerts.show();
		  	  }
		    }
		  });

	}
}

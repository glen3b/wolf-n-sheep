package org.glen_h.games.wolfnsheep;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
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
	AlertDialog.Builder autoshear;
	AlertDialog.Builder shearcosts;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		final String[] items = new String[]{"Auto-shear","Shearing costs"};
		setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, items));
		  ListView lv = getListView();
		  lv.setTextFilterEnabled(true);
		  SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	      boolean autoshear_state = settings.getBoolean("autoshear", true);
	      boolean shearcosts_state = settings.getBoolean("shearcosts", false);
	      final SharedPreferences.Editor editor = settings.edit();
		  autoshear = new AlertDialog.Builder(this);
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
		  autoshear.setTitle("Auto-shear");
		  autoshear.setMessage("Automatically shear when you have 5 wool at the beginning of your turn\nCurrrent Preference: "+current_state_autoshear);
		  autoshear.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
	            	// Enable extra
					editor.putBoolean("autoshear", true);
					editor.commit();
					current_state_autoshear = "Enabled";
					autoshear.setMessage("Automatically shear when you have 5 wool at the beginning of your turn\nCurrrent Preference: "+current_state_autoshear);
				}
			});
		  autoshear.setNegativeButton("Disable",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							// Disable extra
							editor.putBoolean("autoshear", false);
							editor.commit();
							current_state_autoshear = "Disabled";
							autoshear.setMessage("Automatically shear when you have 5 wool at the beginning of your turn\nCurrrent Preference: "+current_state_autoshear);
						}
					});
		  
		  shearcosts.setTitle("Shearing costs");
		  shearcosts.setMessage("Add a cost of 1 wool to shear\nCurrrent Preference: "+current_state_shearcosts);
		  shearcosts.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
	            	// Enable extra
					editor.putBoolean("shearcosts", true);
					editor.commit();
					current_state_shearcosts = "Enabled";
					shearcosts.setMessage("Add a cost of 1 wool to shear\nCurrrent Preference: "+current_state_shearcosts);
				}
			});
		  shearcosts.setNegativeButton("Disable",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							// Disable extra
							editor.putBoolean("shearcosts", false);
							editor.commit();
							current_state_shearcosts = "Disabled";
							shearcosts.setMessage("Add a cost of 1 wool to shear\nCurrrent Preference: "+current_state_shearcosts);
						}
					});
		  lv.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view,
		        int position, long id) {
		      // When clicked, show a toast with the TextView text
		      if(((TextView) view).getText() == items[0]){
		    	  autoshear.show();
		  	  }else if(((TextView) view).getText() == items[1]){
		  		shearcosts.show();
		  	  }
		    }
		  });

	}
}

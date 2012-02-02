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

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		final String[] items = new String[]{"Auto-shear"};
		setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, items));
		  ListView lv = getListView();
		  lv.setTextFilterEnabled(true);
		  SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	      // boolean autoshear_state = settings.getBoolean("autoshear", true);
	      final SharedPreferences.Editor editor = settings.edit();
		  final AlertDialog.Builder autoshear = new AlertDialog.Builder(this);
		  autoshear.setTitle("Auto-shear");
		  autoshear.setMessage("Automatically shear when you have 5 wool at the beginning of your turn");
		  autoshear.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
	            	// Enable extra
					editor.putBoolean("autoshear", true);
					editor.commit();
				}
			});
		  autoshear.setNegativeButton("Disable",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							// Disable extra
							editor.putBoolean("autoshear", false);
							editor.commit();
						}
					});
		  lv.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view,
		        int position, long id) {
		      // When clicked, show a toast with the TextView text
		      if(((TextView) view).getText() == items[0]){
		    	  autoshear.show();
		  	  }
		    }
		  });

	}
}

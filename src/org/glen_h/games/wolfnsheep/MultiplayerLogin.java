package org.glen_h.games.wolfnsheep;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MultiplayerLogin extends Activity {

	private ProgressDialog pd;
	private EditText user;
	private EditText password;
	
	private class Register extends AsyncTask<String[], Void, Integer>{

		
		
		@Override
		protected Integer doInBackground(String[]... url) {
			return WolfNSheep.postData(url[0][0], url[1], url[2]);
    }
		
		@Override
        protected void onPostExecute(Integer stat) {
			pd.cancel();
			if(stat >= 400){
				Toast.makeText(MultiplayerLogin.this.getBaseContext(), "Error!", Toast.LENGTH_SHORT);
				}else{
				Toast.makeText(MultiplayerLogin.this.getBaseContext(), "Registered "+user.getText().toString()+"!", Toast.LENGTH_SHORT);
				WolfNSheep.mpUser = user.getText().toString();
				WolfNSheep.mpPassword = SerializerClass.md5(password.getText().toString());
				SharedPreferences settings = getSharedPreferences("extras", 0);
				SharedPreferences.Editor pwdedit = settings.edit();
				pwdedit.putString("mpUser", WolfNSheep.mpUser);
				pwdedit.putString("mpPassword", WolfNSheep.mpPassword);
				pwdedit.commit();
				Intent intent = new Intent(MultiplayerLogin.this, WolfNSheep.class);
		        startActivity(intent);
				MultiplayerLogin.this.finish();
				}
		}
        }
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.auth);
		user = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		Button register = (Button) findViewById(R.id.register);
		Button login = (Button) findViewById(R.id.login);
		register.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				/*
				Intent register = new Intent(MultiplayerLogin.this, Register.class);
				MultiplayerLogin.this.startActivity(register);
				MultiplayerLogin.this.finish();
				*/
				String[] url = {WolfNSheep.mpUrl+"register.php"};
				pd = new ProgressDialog(MultiplayerLogin.this);
				pd.setTitle("Registering");
				pd.setMessage("Registering Wolf 'N Sheep Multiplayer account for "+user.getText().toString()+".");
				pd.setCancelable(false);
				pd.show();
				(new Register()).execute(url, new String[]{"username", "password"}, new String[]{user.getText().toString(), password.getText().toString()});
				}
		});
		login.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				WolfNSheep.mpUser = user.getText().toString();
				WolfNSheep.mpPassword = SerializerClass.md5(password.getText().toString());
				SharedPreferences settings = getSharedPreferences("extras", 0);
				SharedPreferences.Editor pwdedit = settings.edit();
				pwdedit.putString("mpUser", WolfNSheep.mpUser);
				pwdedit.putString("mpPassword", WolfNSheep.mpPassword);
				pwdedit.commit();
				Intent intent = new Intent(MultiplayerLogin.this, WolfNSheep.class);
		        startActivity(intent);
				MultiplayerLogin.this.finish();
			}
		});
	}
}

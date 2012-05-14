package org.glen_h.games.wolfnsheep;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MultiplayerLogin extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.auth);
		final EditText user = (EditText) findViewById(R.id.username);
		final EditText password = (EditText) findViewById(R.id.password);
		Button register = (Button) findViewById(R.id.register);
		Button login = (Button) findViewById(R.id.login);
		register.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				/*
				Intent register = new Intent(MultiplayerLogin.this, Register.class);
				MultiplayerLogin.this.startActivity(register);
				MultiplayerLogin.this.finish();
				*/
				int stat = WolfNSheep.postData(WolfNSheep.mpUrl+"register.php", new String[]{"username", "password"}, new String[]{user.getText().toString(), password.getText().toString()});
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

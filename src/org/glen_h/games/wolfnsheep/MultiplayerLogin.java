package org.glen_h.games.wolfnsheep;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

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
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(WolfNSheep.mpUrl+"registration.html"));
				startActivity(browserIntent);
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

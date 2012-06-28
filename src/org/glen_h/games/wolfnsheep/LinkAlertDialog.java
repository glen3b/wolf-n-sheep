package org.glen_h.games.wolfnsheep;

import android.app.AlertDialog;
import android.content.Context;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

public class LinkAlertDialog{

		public static AlertDialog create(Context context, String title, String message_txt, String dismiss_text) {
		  final TextView message = new TextView(context);
		  // i.e.: R.string.dialog_message =>
		            // "Test this dialog following the link to dtmilano.blogspot.com"
		  final SpannableString s = 
		               new SpannableString(message_txt);
		  Linkify.addLinks(s, Linkify.WEB_URLS);
		  message.setText(s);
		  message.setMovementMethod(LinkMovementMethod.getInstance());

		  return new AlertDialog.Builder(context)
		   .setTitle(title)
		   .setCancelable(true)
		   // .setIcon(android.R.drawable.ic_dialog_info)
		   .setPositiveButton(dismiss_text, null)
		   .setView(message)
		   .create();
		   /*
			final TextView message = new TextView(context);
	         
	        final Spanned s =
	                       Html.fromHtml(message_txt.replaceAll("(.http://[^<>[:space:]]+[[:alnum:]/])", "<a href=\"$1\">$1</a>"));
	        message.setText(s);
	        message.setMovementMethod(LinkMovementMethod.getInstance());
	 
	        return new AlertDialog.Builder(context)
	           .setTitle(title)
	           .setCancelable(true)
	           // .setIcon(android.R.drawable.ic_dialog_info)
	           .setPositiveButton(dismiss_text, null)
	           .setView(message)
	           .create();
	           */
		 }
}

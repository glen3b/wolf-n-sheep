/* Copyright 2011 Glen Husman - Teacher Wishlist Viewer
  Client application for reading a wishlist file from the web.

	This file is part of Teacher Wishlist Viewer.

    Teacher Wishlist Viewer is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Teacher Wishlist Viewer is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Teacher Wishlist Viewer.  If not, see <http://www.gnu.org/licenses/>
  */

/*
 * Work on:
 * Variable parameters {URLGet(website);}
 * IMPORTANT: Calling function from other files
 */

package org.glen_h.libraries;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.util.Log;

/**
 * Functions to download text files and return their contents in certain formats.
 * @author Glen Husman
 * @see java.net.URL URL
 * @see java.io.BufferedReader BufferedReader
 * @see java.io.InputStreamReader InputStreamReader
 */
public class URLGet extends Activity {
	
	/**
	 * Make a URL from a string.
	 * @author Glen Husman
	 * @see java.net.URL URL
	 */
	public static URL MakeURL(String webaddress){
		URL website = Internet.MakeURL(webaddress);
		return website;
	}
	
	/**
	 * Downloads a text file and returns its contents as an array.
	 * @author Glen Husman
	 */
	public static String[] ArrayReturn(URL website) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(
			              new InputStreamReader(
			              website.openStream()));
		} catch (IOException e) {
			in = null;
		}

	      String input;
	      ArrayList<String> stringList = new ArrayList<String>();
	      	                
	      try {
			while ((input = in.readLine()) != null) {
			      stringList.add(input);
			  }
		} catch (IOException e) {
			stringList = new ArrayList<String>();
			}
	      
	    String[] itemArray = new String[stringList.size()];
		String[] returnedArray = stringList.toArray(itemArray);
		return returnedArray;
		}
	
	/**
	 * Downloads a single-line text file from the internet and returns its contents as a string.
	 * @see java.lang.String String
	 * @author Glen Husman
	 */
	public static String StringReturn(URL website) {
		BufferedReader version_in = null;
		try {
			version_in = new BufferedReader(
			              new InputStreamReader(
			              website.openStream()));
		} catch (IOException e) {
			version_in = null;
		}
		
	
        
		String string = null;
		try {
			string = version_in.readLine();
			     
		} catch (IOException e) {
			string = null;
		}
		
	return string;
	
	}
	
	/**
	 * Parses an {@code int} from a {@code String} value downloaded. If the string is not a valid integer, it will return 0.
	 * @see java.lang.String String
	 * @author Glen Husman
	 */
	public static int IntegerReturn(URL website) {
	String string = StringReturn(website);
	int integer;
	try{
	integer = Integer.parseInt( string );
	} catch(NumberFormatException e){
		integer = 0;
	}
	return integer;
	
	}

	/**
	 * Parses a boolean string downloaded from the internet and returns a boolean. If the value is not true, false, yes, or no (in any case), it will return {@code false}.
	 * @see java.lang.Boolean Boolean
	 * @see java.lang.String String
	 * @author Glen Husman
	 */
	public static boolean BooleanReturn(URL website) {
	String string = StringReturn(website);
	boolean Boolean = false;
	boolean isTrue = "true".equalsIgnoreCase(string);
	boolean isFalse = "false".equalsIgnoreCase(string);
	boolean isYes = "yes".equalsIgnoreCase(string);
	boolean isNo = "no".equalsIgnoreCase(string);
	
	if(isYes == true){
		Boolean = true;
	}
	else if(isYes == false){
	}
	else if(isTrue == true){
		Boolean = true;
	}
	else if(isTrue == false){
	}
	else if(isFalse == true){
		Boolean = false;
	}
	else if(isFalse == false){
	}
	else if(isNo == true){
		Boolean = false;
	}
	else if(isNo == false){
	}
	else{
		Log.e("Could not parse string to a boolean. Returning false.", "ERROR PARSING BOOLEAN");
	}
	
	return Boolean;
	/*
	if(string == "true"){
		Boolean = true;
	}
	else if(string == "True"){
		Boolean = true;
	}
	else if(string == "TRUE"){
		Boolean = true;
	}
	else if(string == "yes"){
		Boolean = true;
	}
	else if(string == "Yes"){
		Boolean = true;
	}
	else if(string == "YES"){
		Boolean = true;
	}
	
	if(string == "false"){
		Boolean = false;
	}
	else if(string == "False"){
		Boolean = false;
	}
	else if(string == "FALSE"){
		Boolean = false;
	}
	else if(string == "no"){
		Boolean = false;
	}
	else if(string == "No"){
		Boolean = false;
	}
	else if(string == "NO"){
		Boolean = false;
	}
	else{
		Log.e("Could not parse string to a boolean. Returning false.", "ERROR PARSING BOOLEAN");
	}
	*/
		}

	/**
	 * Parses a boolean string downloaded from the internet and returns a boolean. If the value is not either {@code TRUE}, {@code true}, {@code FALSE}, or {@code false}, it will throw a {@link org.glen_h.libraries.BooleanParseException BooleanParseException}.
	 * @see java.lang.Boolean Boolean
	 * @see java.lang.String String
	 * @throws BooleanParseException
	 * @author Glen Husman
	 */
	public static boolean BooleanReturnStrict(URL website) throws BooleanParseException {
		String string = StringReturn(website);
		// This MUST be a boolean value downloaded!
		boolean Boolean;
		if(string == "true"){
			Boolean = true;
		}
		else if(string == "TRUE"){
			Boolean = true;
		}
		
		if(string == "false"){
			Boolean = false;
		}
		else if(string == "FALSE"){
			Boolean = false;
		}
		else{
			throw new BooleanParseException();
			}
		return Boolean;
			}


	
	}

package org.glen_h.libraries;

/**
 * Basic math and number operations.
 * @author Glen Husman
 */
public class Mathematics {

	/**
	 * Uses {@code randomNumber} to generate a random {@code int} between {@code Min} and {@code Max}.
	 * @param Min
	 * 	The minimum integer.
	 * @param Max
	 * 	The maximum integer.
	 * @author Glen Husman
	 * @deprecated Use {@code randomNumber} instead.
	 */
	public static int RandomNumber(Integer Min, Integer Max){
		return randomNumber(Min,Max);
	}
	
	/**
	 * Generate a random {@code int} between {@code Min} and {@code Max}.
	 * @param Min
	 * 	The minimum integer.
	 * @param Max
	 * 	The maximum integer.
	 * @author Glen Husman
	 */
	public static int randomNumber(int Min, int Max){
		  int randomNum = Min + (int)(Math.random() * ((Max - Min) + 1));
		  return randomNum;
	}
	
	/**
	 * Convert an {@link java.lang.Integer Integer} to a primitive {@code int}.
	 * @param num
	 * 	The {@link java.lang.Integer Integer} to convert to an {@code int}.
	 * @author Glen Husman
	 * @see java.lang.Integer Integer
	 */
	public static int integerToInt(Integer num){
		int number = num;
		return number;
	}
	
	/**
	 * Convert a primitive {@code int} to an {@link java.lang.Integer Integer}.
	 * @param num
	 * 	The {@code int} to convert to an {@link java.lang.Integer Integer}.
	 * @author Glen Husman
	 * @see java.lang.Integer Integer
	 */
	public static int intToInteger(int num){
		Integer number = num;
		return number;
	}
	
	/**
	 * Add two integers (as strings) and return a string.
	 * @param addend1
	 * 	Addend 1.
	 * @param addend2
	 * 	Addend 2.
	 * @author Glen Husman
	 * @throws NumberFormatException
	 * Thrown when the strings are not valid integers.
	 * @see java.lang.Integer Integer
	 * @see java.lang.String String
	 */
	public static String addStrings(String addend1, String addend2) throws NumberFormatException{
		  Integer addend_1;
		  Integer addend_2;
		  try{
		  addend_1 = Integer.parseInt(addend1);
		  addend_2 = Integer.parseInt(addend2);
		  }catch(NumberFormatException e){
			  throw new NumberFormatException("Could not parse parameters");
		  }
		  Integer sum = addend_1 + addend_2;
		  String sum_string = Integer.toString(sum);
		  return sum_string;
	}
}

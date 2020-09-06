package model;

import java.util.stream.Stream;

/**
 * 
 * @author Zoe Lubanza
 *
 */

public class HexConvertion 
{
	private StringBuilder asciiText = new StringBuilder();
	private StringBuilder hexText = new StringBuilder();
	
	/**
	 * converts a Hex string into ASCII
	 * @param hexString
	 */
	public void hexToAscii(String hexString) 
	{
	    for (int i = 0; i < hexString.length(); i = i + 2) 
	    {
	    	String str = hexString.substring(i, i + 2);	//Split the hex string into two character group
	        int n = Integer.valueOf(str, 16);			//Convert the each character group into integer using valueOf method
	        asciiText.append((char)n); 					//Cast the integer value to char
	    }
	}
	
	/**
	 * converts a ASCII string into hexadecimal
	 * @param asciiString
	 */
	public void asciiToHex(String asciiString)
	{
	    Stream.of(asciiString)
	    .flatMapToInt(line -> line.chars())				//convert each character into its decimal value
	    .forEach(ch -> {
	    	String s = String.format("%02X", ch);		//convert the decimal value to its Hexadecimal in 2byte format
	    	hexText.append(s);							//append the Hex value of the selected character
	    });
	}

	/**
	 * 
	 * @return the converted hex text
	 */
	public StringBuilder getAsciiText()
	{
		return asciiText;
	}

	/**
	 * 
	 * @return the converted ASCII text
	 */
	public StringBuilder getHexText()
	{
		return hexText;
	}
}

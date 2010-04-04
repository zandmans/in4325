package nl.tudelft.in4325.index;

public class Soundex
{

	public Soundex()
	{
		
	}
	
	
	public void convertIndex()
	{
		
	}
	
	
	public String convertToken(String WordString)
	{
		String tmpStr = "";
		String wordStr = "";
		Character curChar;
		Character lastChar;
		String firstLetter = "";
		Integer soundExLen = 10;
		Integer WSLen = 0;
		Integer lengthOption = 4;
		
		if(lengthOption != null)
			soundExLen = lengthOption;
		
		if(soundExLen > 10)
			soundExLen = 10;
		
		if(soundExLen < 4)
			soundExLen = 4;
		
		if(WordString == "")
			return WordString;
		
		WordString = WordString.toUpperCase();
		
		wordStr = WordString;
		
		wordStr = wordStr.replaceAll("[^A-Z]", ""); // replace non-chars with space
		wordStr = wordStr.replaceAll("^(\\s)*", ""); // remove leading space
		wordStr = wordStr.replaceAll("(\\s)*$", ""); // remove trailing space
		
		firstLetter = wordStr.substring(0,1);
		
		if(firstLetter == "H" || firstLetter == "W")
		{
			tmpStr = wordStr.substring(1);
			wordStr = "-";
			wordStr += tmpStr;
		}
		
		wordStr = wordStr.replaceAll("[HW]", ".");
		wordStr = wordStr.replaceAll("[AEIOUYHW]", "0");
		wordStr = wordStr.replaceAll("[BPFV]", "1");
		wordStr = wordStr.replaceAll("[CSGJKQXZ]", "2");
		wordStr = wordStr.replaceAll("[DT]", "3");
		wordStr = wordStr.replaceAll("[L]", "4");
		wordStr = wordStr.replaceAll("[MN]", "5");
		wordStr = wordStr.replaceAll("[R]", "6");
		wordStr = wordStr.replaceAll("[.]", "");
		
		WSLen = wordStr.length();
		lastChar = '-';
		tmpStr = "";
		
		for(int i=0; i<WSLen; i++)
		{
			curChar = wordStr.charAt(i);
			if(curChar == lastChar)
				tmpStr += " ";
			else
			{
				tmpStr += curChar;
				lastChar = curChar;
			}
		}
		wordStr = tmpStr;
		
		wordStr = wordStr.substring(1);
		wordStr = wordStr.replaceAll("(\\s)", "");
		wordStr = wordStr.replaceAll("[0]", "");
		wordStr += "0000000000";
		
		wordStr = firstLetter + wordStr;
		
		wordStr = wordStr.substring(0, soundExLen);
		
		return wordStr;
	}
	
}

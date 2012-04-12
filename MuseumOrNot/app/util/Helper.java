package util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;

import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;

public class Helper {

	public static String allowedCharsContent = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ01234567890 ,.:;_-~?|\\{}=+@#!'\"\n$%^&*()[]<>";
	public static String allowedCharsUsername = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ01234567890_-";
	public static String allowedCharsProjectname = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ01234567890_-";
	
	
	public static int MIN_PASSWORD_LENGTH = 6;
	public static int MIN_PASSWORD_NON_ALPHABET = 2;
	public static int MIN_PASSWORD_ALPHABET = 3;
	
	public static String cleanProjectName(String project_name)
	{
		if (project_name==null) return null;
		return cleanString(project_name.trim().toLowerCase(), allowedCharsProjectname);
	}

	public static String cleanFullPath(String path) {
		return path.trim();
	}
	
	public static String cleanEmail(String email) {
		if (email==null) return null;
		return email.trim().toLowerCase();
	}
	
	public static List<String> splitFilenameSearchWords(String search_string)
	{
		String[] words = search_string.toLowerCase().trim().split("\\b");
		// todo: diacritische tekens en andere dingen eruit?
		ArrayList<String> terms = new ArrayList<String>();
		for (String s:words) terms.add(s);
		return terms;
		
	}
	
	public static List<String> splitSearchWords(String search_string)
	{
		String[] words = search_string.toLowerCase().trim().split("\\b");
		List<String> list = new ArrayList();
		for (String s:words) list.add(s);
		return list;
	}
	
	public static long parse(String value, long default_value)
	{
		try
		{
			return Long.parseLong(value);
			
		} catch (Exception e) { return default_value; }
	}

	public static String cleanString(String s, String allowedChars) {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<s.length(); i++)
		{
			char ch= s.charAt(i);
			if (allowedChars.indexOf(ch)>-1) buf.append(ch);
		}
		return buf.toString();
	}
	
	// encryption stuff
	



	// encryption methods
	public static byte[] getHash(String password, byte[] salt) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		digest.reset();	
		digest.update(salt);	
		byte[] input = digest.digest(password.getBytes("UTF-8"));	
		digest.reset();	
		input = digest.digest(input);
		return input;
	}

	static String passwordChars = "abcdefghijklmnopqrstuvwxyz";
	
	public static String generateNewPassword(int passwordLength) {
		StringBuffer buf = new StringBuffer();
		int templateLength = passwordChars.length();
		for (int i=0; i<passwordLength; i++)
		{
			char ch = passwordChars.charAt((int)Math.floor(Math.random()*templateLength));
			buf.append(ch);
		}
		return buf.toString();
	}

	public static String cleanUserName(String user_name) {
		if (user_name==null) return null;
		return cleanString(user_name.trim().toLowerCase(), allowedCharsUsername);
	}

	public static List<String> splitSearchWords(String commaSeparatedValues, String wordBoundaryValues, String emailField) {
		List<String> words = new ArrayList();
		
		if (commaSeparatedValues!=null) addStrings(words, commaSeparatedValues.toLowerCase().trim().split(","));
		if (wordBoundaryValues!=null) addStrings(words, commaSeparatedValues.toLowerCase().trim().split("\\b"));
		if (emailField!=null)
		{
			emailField = cleanEmail(emailField);
			words.add(emailField);
			addStrings(words, emailField.split("\\b"));
		}
		
		if (words.contains("")) words.remove(""); // no empty ones
		
		
		return words;
	}
	
	public static void addStrings(List<String> list, String[] array)
	{
		if (array!=null && list!=null) for (String s:array) list.add(s);
	}
	

	public static String md5hex(String value) {
		return hex(DigestUtils.md5(value));
        
    }
	
	public static String hex(byte[] array) {
		
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
        sb.append(Integer.toHexString((array[i]
            & 0xFF) | 0x100).substring(1,3));        
        }
        return sb.toString();
    }

	public static String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	/**
	 * returns NULL if password is complex enough, or returns error message if there's something wrong.
	 * @param new_password
	 * @return
	 */
  public static String validatePasswordComplexity(String new_password) {
    if (new_password==null || new_password.trim().length() < MIN_PASSWORD_LENGTH) return "Your password is too short. Please use at least " + MIN_PASSWORD_LENGTH + " characters.";
  
    new_password = new_password.trim();
    if (new_password.contains(" ")) return "Your password cannot contain a space character. Sorry.";
    
    int alphabet = 0;
    int non_alphabet = 0;
    for (int i=0; i<new_password.length(); i++)
    {
      char ch = new_password.charAt(i);
      if (ALPHABET.indexOf(ch)>-1) alphabet++; else non_alphabet++;
    }
    
    if (alphabet<MIN_PASSWORD_ALPHABET) return "You need at least " + MIN_PASSWORD_ALPHABET + " alphabet characters in your password.";
    if (non_alphabet<MIN_PASSWORD_NON_ALPHABET) return "You need at least " + MIN_PASSWORD_NON_ALPHABET + " non-alphabet characters in your password.";
    
    return null; // OK.
  }

  public static boolean validateEmailAddress(String email) {
    if (email==null) return false;
    email = cleanEmail(email);
    if (!email.contains("@")) return false;
    if (!email.contains(".")) return false;
    if (email.length() < 5) return false;
    
    // TODO: can be better
    return true;
  }
  
  /**
   * handy date methods for Stats & StatsService
   */
  
 // helper methods
  
  public static int createDayCode(Date d)
  {
    return (d.getYear()+1900)*10000 + (d.getMonth()+1)* 100 + (d.getDate());
  }
  
  public static Date getDateFromDayCode(int day)
  {
    
    int y = day / 10000;
    int m = (day - (y * 10000)) / 100;
    int d = day - (y * 10000) - (m * 100);
    
    Date res = new Date(y-1900, m-1, d);
    //Logger.info("converting day " + day + " to date: " + res + ", " + y +":"+ m +":" + d);
    return res;
  }
  
  public static int createDayCode()
  {
    return createDayCode(new Date());
  }


}

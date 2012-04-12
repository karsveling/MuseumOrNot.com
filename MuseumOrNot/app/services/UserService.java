package services;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import play.modules.siena.QueryWrapper;
import play.mvc.Http.Cookie;
import play.mvc.Http.Response;
import play.mvc.Scope.Session;
import play.Logger;
import siena.Model;
import siena.Query;
import util.Helper;

import models.User;

public class UserService {

	public static User getById(long user_id) {
		return User.findById(user_id);
	}

	public static boolean isUserNameAvailable(String user_name) {
		return User.all().filter("username =", Helper.cleanUserName(user_name)).get() == null;
	}
	
	public static User login(String email, String password) {
		User user = User.all().filter("email =", Helper.cleanEmail(email)).get();
		if (user==null) return null;
		
		if (!user.checkPassword(password)) return null;
		
		// login successful. register login-date
		user.login_date = new Date();
		user.save();
		
		return user;
		
	}
	
	public static User processGoogleLogin(com.google.appengine.api.users.User googleUser) 
	{
		User user = UserService.getByEmail(googleUser.getEmail());
		if (user==null)
		{
			// autocreate user record
			
			// first we need to generate a new, unique userName.
			String uniqueUserName = googleUser.getNickname();
			if (uniqueUserName.contains("@"))
			{
				// we don't want that...
				uniqueUserName = uniqueUserName.substring(0, uniqueUserName.indexOf("@"));
			}
			
			uniqueUserName = UserService.generateUniqueUsername(uniqueUserName);
			
			user = new User(googleUser, uniqueUserName);
		}
		
		user.login_date = new Date();
		user.save();
		
		return user;
	}	

	

	public static User authenticateByAccessToken(String access_token) {
		if (access_token==null) return null;
		
		// access tokens always contain the user_id, like this: 87612312|ashgfakhvfakweyakewf. We're gonna parse that, so that we can do a really fast authentication check
		String[] splitted = access_token.split("\\|");
		
		long user_id = Helper.parse(splitted[0], -1);
		if (user_id<0 || splitted.length!=2) return null;
		
		User user = User.findById(user_id);
		if (user==null) return null;
		
		if (!access_token.equals(user.access_token)) return null;
		
		return null;
	}


	public static String generateUniqueUsername(String wanted_name) {
		wanted_name = Helper.cleanUserName(wanted_name);
		
		if (isUserNameAvailable(wanted_name))
		{
			return wanted_name;
		}
		
		// generate a new ID and try if that's still available.
		int maxTries = 20;
		double numberScope = 999;
		while (maxTries>0)
		{
			String newName = wanted_name + Math.floor(( Math.random()* numberScope ));
			if (isUserNameAvailable(newName))
			{
				// yeah! let's try to create it.
				return newName;
			}
			maxTries--;
			numberScope = numberScope * 5;
		}
		
		Logger.error("Could not find available username for user with wanted_name " + wanted_name);
		return null;
		
	}

	public static User getByUsername(String username) {
		User foundUser = User.all().filter("username =", Helper.cleanUserName(username)).get();
		//TODO: check if user is enabled / validated, otherwise return null
		return foundUser;
	}
	
	public static User getByEmail(String email) {
		User foundUser = User.all().filter("email =", Helper.cleanEmail(email)).get();
		//TODO: check if user is enabled / validated, otherwise return null
		return foundUser;
	}

	public static List<User> listUsers(String filter, int offset, int max) {
		QueryWrapper q = User.all();
		if (filter!=null && !filter.equals("")) 
		{
			String[] words = filter.split(" ");
			for (String word:words) if (!word.trim().equals("")) q = q.filter("search_words =", word.trim());
			Logger.info("filtering users on " + q);
		}
		
		if (offset<0) offset=0;
		if (max>200) max=200;
		if (max<1) max = 20;
		
		q = q.limit(max).offset(offset);
		
		return q.fetch();
	}
	

	public static boolean isEmailAvailable(String email) {
		User user = User.all().filter("email =", Helper.cleanEmail(email)).get();
		return (user==null);
	}

  public static void setLoginState(User user, Session session,
      Response response, String loginType) {
    
    session.put("user_id", user.id);
    
    Cookie c = new Cookie();
    c.name = "access_token";
    c.maxAge = 24 * 3600 * 365; // 1 yr
    
    c.value = user.access_token;
    response.cookies.put("access_token", c);
    
    session.put("login_type", loginType);
  }



}

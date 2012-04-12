package controllers;

import play.*;
import play.mvc.*;
import play.mvc.Http.Cookie;
import services.*;
import util.Helper;

import java.util.*;
import java.io.PrintWriter;
import java.net.URLDecoder;

import models.*;

public class AuthenticatedBaseController extends Controller {

	static boolean requireValidUser = true; // set to false (static!) if you don't require logged in users
	
	@Before
	public static void makeSureTestDataIsFilled()
	{
		
	}
	
	@Before
	public static void validateUserAndOptionalProjectname(String projectname) {
		User user = null;
		Cookie cookie = request.cookies.get("access_token");

		long user_id = Helper.parse(session.get("user_id"), -1);
		if (user_id < 1)
			Account.signin(null, null);

		user = UserService.getById(user_id);
		if (user == null && cookie != null) {
			String access_token = URLDecoder.decode(cookie.value);

			// we have an access token. Nice. Let's try to authenticate that.
			user = UserService.authenticateByAccessToken(access_token);
		}

		// still nothing?
		if (requireValidUser)
		{
			if (user == null) Account.signin(null, null);
			renderArgs.put("user", user);
		}
		
		Logger.info("User " + user.id + " (" + user.email + "): " + request.path + "?" + request.querystring);
		
			
	}

	public static User getCurrentUser() {
		return (User) renderArgs.get("user");
	}

	
}
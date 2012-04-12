package controllers;

import play.*;
import play.mvc.*;
import play.mvc.Http.Cookie;
import services.*;
import util.Helper;

import java.util.*;
import java.io.PrintWriter;
import java.net.URLEncoder;

import com.google.appengine.api.users.UserServiceFactory;

import models.*;

public class Account extends Controller {

	public static String LOGINTYPE_EMAILPW = "emailpw";
	public static String LOGINTYPE_GOOGLEID = "googleid";
	
	
	public static void index() {
	}

	public static void signin(String email, String password) {
		com.google.appengine.api.users.UserService us = UserServiceFactory.getUserService();
		String googleLoginUrl = us.createLoginURL("/account/signinwithgoogle");
		
		User user = null;
		
		if (email != null && password != null)
		{
			// try to login, the normal way (username+pw given)
			user = UserService.login(email, password);

			if (user == null) {
				flash.error("Invalid username or password");
				render("account/signin.html", email, password, googleLoginUrl);
			}
		}
		
		if (user!=null)
		{
			// we're logged in!
			// user found.
		  UserService.setLoginState(user, session, response, LOGINTYPE_EMAILPW);
		  
			// need redirect to somewhere?
			String target = request.params.get("target"); 
			if (target != null) {
				redirect(target);
			}

			redirect("/");
		}

		render(email, password, googleLoginUrl);
	}

	public static void signinWithGoogle()
	{
		com.google.appengine.api.users.UserService us = UserServiceFactory.getUserService();
		com.google.appengine.api.users.User googleUser = us.getCurrentUser();
		
		User user = null;
		
		String redirectTo = "/";
		
		// two options to login, with Google ID or with email/password
		if (googleUser!=null) {
			 // nice! already logged in via Google ID.
			 Logger.info("Signed in:" + googleUser.getNickname() +", " + googleUser.getEmail() +"," + googleUser.getAuthDomain() + ","+ googleUser.getFederatedIdentity()+"," + googleUser.getUserId());
			 
			 // make sure this user exists in our system too.
			 user = UserService.processGoogleLogin(googleUser);
			 
			 
			 
			if (user.access_token==null)
			{
				// no access token yet! auto-generate
				user.setPassword(Helper.generateNewPassword(32)); // generate dummy password.
			}
			 
			UserService.setLoginState(user, session, response, LOGINTYPE_GOOGLEID);
			
		}
		
		redirect(redirectTo);
	}
	
	public static void signout() {
		session.remove("user_id");
		response.cookies.remove("access_token");
		
		
		redirect("/");
	}
	
	public static void signup() {
		
	}
	

	
	public static void forgotPassword(String email, String species) {
	  flash.clear();
		if (email!=null) {
		  
		  if (species==null || !"Human".equals(species))
		  {
		    flash.error("Oh hmmm. We only support Human interaction right now. We'll get in touch.");
		    render(email);
		  }
		  
		  User user = UserService.getByEmail(email);
		  
		  if (user!=null)
		  {
		    // user found.
		    user.resetPassword();
		    user.save();
		    
		    //EmailService.sendResetPasswordMail(user, request.host);

		    flash.success("We sent you an email with the password reset instructions.");
		    signin(null,null);
		  }
		}
		
		render(email);
	}
	
	public static void resetPassword(long user_id, String reset_code, String new_password, String new_password_again) {
	  if (user_id<1 || reset_code==null) redirect("/"); // hackattack. forget it.
	  
	  User user = UserService.getById(user_id);
	  if (user==null || user.reset_password_date==null || !user.checkResetCode(reset_code))
	  {
	    // invalid code or expired.
	    flash.error("The reset code has expired or not found.");
	    render(user, user_id, reset_code);
	  }
	  
	  // code is Ok. New password entered?
	  if (new_password==null || new_password.trim().length()==0 || new_password_again==null) {
	    // Ok. Just show the screen.
	  }
	  else
	  {
	    if (!new_password.equals(new_password_again))
	    {
	      // two times same password?
	      flash.error("Please enter your new password twice. Passwords do not match now.");
	    }
	    else
	    {
  	    String error = Helper.validatePasswordComplexity(new_password);
  	    if (error!=null) {
  	      flash.error(error);
  	    }
  	    else
  	    {
  	      user.setPassword(new_password);
  	      user.save();
  	      
  	      UserService.setLoginState(user, session, response, LOGINTYPE_EMAILPW);
  	      
  	      flash.success("Your password is changed.");
  	      Logger.info("Setting new password for user " + user_id + ", via resetPassword link");
  	      redirect(user.getMainUrl());
  	    }
	    }
	  }
	  
	  render(user, user_id, reset_code);
	}

}
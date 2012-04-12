package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import com.google.appengine.api.users.UserServiceFactory;

import models.*;

public class Application extends AuthenticatedBaseController {

    public static void index() {
      
      com.google.appengine.api.users.UserService us = UserServiceFactory.getUserService();
      String googleLoginUrl = us.createLoginURL("/account/signinwithgoogle");
      
      User currentUser = getCurrentUser();
      
        render(googleLoginUrl);
    }

}
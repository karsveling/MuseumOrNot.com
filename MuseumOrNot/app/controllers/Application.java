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
    
    public static void play()
    {
      User user = getCurrentUser();
      
      int offset = (int) Math.floor((Math.random() * 10000.0));
      
      boolean sw =  Math.random() >= 0.5;
      
      ArtObject artobject1 = ArtObject.getRandomObject(sw);
      ArtObject artobject2 = ArtObject.getRandomObject(sw);
      
      Logger.info("Done:" + artobject1.id + ":"+ artobject1.image_url + ":" + artobject2.id + ":" + artobject2.image_url);
      
      // get two random objects from database
      render(artobject1, artobject2);
    }

}
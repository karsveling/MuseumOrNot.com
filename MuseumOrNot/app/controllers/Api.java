package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Api extends AuthenticatedBaseController {

    public static void addArtObject(String title, String description, String url, String image_url, int width, int height, boolean in_a_museum, String institution) {
        
      ArtObject obj = new ArtObject(title, description, url, image_url, width, height, in_a_museum, institution);
      
      obj.save();
      renderHtml("Ok.");
    }
    
    public static void vote(long object_id)
    {
      User user = getCurrentUser();
      
      if (user==null) redirect("/");
      
      ArtObject obj = ArtObject.findById(object_id);
      
      if (obj==null)
      {
        // not found!
        notFound();
      }
      
      String message = "";
      boolean correct = false;
          
      if (obj.in_a_museum)
      {
        // correct!
        user.processCorrect();
        correct=true;
        
        message = "Oh yeah! You're so right man...";
      }
      else
      {
        user.processWrong();
        correct=false;
        message = "Ohnoes! It's not! (yet)";
      }
      
      renderJSON("{\"result\":" + correct+", \"message\":\"" + message + "\", \"score\":\""+user.score+"\"}");
        
      
      
      
    }

}
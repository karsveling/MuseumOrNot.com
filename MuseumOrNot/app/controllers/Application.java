package controllers;

import play.*;
import play.mvc.*;
import siena.Query;

import java.util.*;

import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskQueuePb.TaskQueueService;
import com.google.appengine.api.users.UserServiceFactory;

import models.*;

public class Application extends AuthenticatedBaseController {

    
    public static void index() {
      
      com.google.appengine.api.users.UserService us = UserServiceFactory.getUserService();
      String googleLoginUrl = us.createLoginURL("/account/signinwithgoogle");
      
      User currentUser = getCurrentUser();
      
      if (currentUser!=null) redirect("/play");
      
        render(googleLoginUrl);
    }
    
    public static void play()
    {
      if (getCurrentUser()==null)
      {
        redirect("/");
      }
      
      makeSureTestDataIsThere();
      makeSureTotalCountsAreDone();
       
      User user = getCurrentUser();
      
      boolean sw =  Math.random() >= 0.5;
      
      ArtObject artobject1 = ArtObject.getRandomObject(sw);
      ArtObject artobject2 = ArtObject.getRandomObject(!sw);
   
      Logger.info("Done:" + artobject1.id + ":"+ artobject1.image_url + ":" + artobject2.id + ":" + artobject2.image_url);
      
      // get two random objects from database
      render(artobject1, artobject2);
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
      String explanation = "";
      String level_upgrade = null;
      boolean correct = false;
          
      if (obj.in_a_museum)
      {
        // correct!
        user.processCorrect();
        correct=true;
        message = "Oh yeah!<br/>Thats right!";
        explanation = "This is <u>"+obj.title+"</u> from <u>"+obj.institution+"</u>";
        
        level_upgrade = user.newLevelShouldBe();
      }
      else
      {
        user.processWrong();
        correct=false;
        message = "Oh, it's not<br/>in a museum (yet)";
        explanation = "This is <u>"+obj.title+"</u> from <u>"+obj.institution+"</u>";
        
        level_upgrade = user.newLevelShouldBe();
      }
      
      Logger.info("New level of user should be:" + level_upgrade);
     
      String old_level = user.reputation_label;
      user.reputation_label = level_upgrade;
     
      user.save();
      
      String result = correct?"right":"wrong";
     
      render(result, message, obj, explanation, level_upgrade, old_level);
      
    }
    private static void makeSureTestDataIsThere()
    {
      ArtObject obj = ArtObject.findById(1L);
      if (obj==null)
      {
        // hardcoded with id 1
        obj = new ArtObject("Test obj 1", "descr 1", null, "http://images.powerhousemuseum.com/images/zoomify/thumbs/255831.jpg", 160, 160, true, "Powerhouse");
        obj.id=1L;
        obj.save();
        
        obj = new ArtObject("Test obj 2", "descr 2", null, "http://images.powerhousemuseum.com/images/zoomify/TLF_mediums/127093.jpg", 260, 260, true, "Powerhouse");
        obj.id=2L;
        obj.save();
        
        obj = new ArtObject("Test obj 3", "descr 3", null, "http://images.powerhousemuseum.com/images/zoomify/TLF_mediums/127093.jpg", 260, 260, false, "Powerhouse");
        obj.id=3L;
        obj.save();
        
        
      }
    }
   
    
    private static void makeSureTotalCountsAreDone()
    {
      long museum = Settings.getLongValue(ArtObject.TOTAL_MUSEUM_OBJECTS);
      if (museum<0)
      {
        ArtObject.countAllObjects(); // update static counter settings (slow, I know, but ok for now), will run only once, reset by clearing field in db
      }
    }

}
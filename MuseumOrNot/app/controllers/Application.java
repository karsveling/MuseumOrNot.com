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
      
        render(googleLoginUrl);
    }
    
    public static void play()
    {
      makeSureTestDataIsThere();
      makeSureTotalCountsAreDone();
       
      User user = getCurrentUser();
      
      boolean sw =  Math.random() >= 0.5;
      
      ArtObject artobject1 = ArtObject.getRandomObject(sw);
      ArtObject artobject2 = ArtObject.getRandomObject(sw);
      
      Logger.info("Done:" + artobject1.id + ":"+ artobject1.image_url + ":" + artobject2.id + ":" + artobject2.image_url);
      
      // get two random objects from database
      render(artobject1, artobject2);
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
        
        
        
      }
    }
    
    public static void touchAllObjects(int start)
    {
      int batchSize = 100;
      
      Query<ArtObject>q = ArtObject.all(ArtObject.class).offset(start).limit(batchSize);
      
      Iterator<ArtObject> iter = q.iter().iterator();
      
      if (!iter.hasNext())
      {
        Logger.info("ready. offset was " + start);
        renderHtml("ready!");
      }
      
      while (iter.hasNext())
      {
        ArtObject obj = iter.next();
        if (obj.random_key==0) obj.save();
      }
      
      renderHtml("ready with this batch. offset was " + start);
      
      TaskOptions task = TaskOptions.Builder.withUrl("/application/touchallobjects").param("start", ""+start + batchSize);
      
      com.google.appengine.api.taskqueue.QueueFactory.getDefaultQueue().add(task);
      
      
      
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
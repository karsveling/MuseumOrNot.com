package controllers;

import play.*;
import play.mvc.*;
import siena.Query;

import java.util.*;

import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskQueuePb.TaskQueueService;
import com.google.appengine.api.users.UserServiceFactory;

import models.*;

public class Tasks extends Controller {

 
    
    public static void touchAllObjects(int start)
    {
      Logger.info("touching... " + start);
      
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
      
      start += batchSize;
      
      TaskOptions task = TaskOptions.Builder.withUrl("/tasks/touchAllObjects").param("start",start+"");
      
      com.google.appengine.api.taskqueue.QueueFactory.getDefaultQueue().add(task);
      
      renderHtml("ready with this batch. offset was " + start);
      
      
      
    }
    

}
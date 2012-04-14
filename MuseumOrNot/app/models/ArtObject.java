package models;

import java.util.Iterator;
import java.util.Random;

import play.modules.siena.EnhancedModel;
import play.mvc.Before;
import siena.Id;
import siena.Query;
import siena.core.lifecycle.PreInsert;
import siena.core.lifecycle.PreSave;
import siena.core.lifecycle.PreUpdate;

public class ArtObject extends EnhancedModel
{

  public static String TOTAL_MUSEUM_OBJECTS = "total_museum_objects";
  public static String TOTAL_NON_MUSEUM_OBJECTS = "total_non_museum_objects";
  
  
  @Id
  public Long id;
  
  public String title;
  public String description;
  public String url;
  
  public int width;
  public int height;
  
  public double random_key;

  public boolean portrait;
  
  public boolean in_a_museum;
  
  public String image_url;
  
  public String institution;
  
  public int num_votes;
  public int museum_votes;
  public int non_museum_votes;
  
  public int right_votes;
  public int wrong_votes;
  
  public ArtObject(String title, String description, String url, String image_url, int width, int height, boolean in_a_museum, String institution)
  {
    this.title = title;
    this.description = description;
    this.url = url;
    this.image_url = image_url;
    this.institution = institution;
    this.in_a_museum = in_a_museum;
    this.width = width;
    this.height = height;
    this.random_key = Math.random();
  }

  @PreInsert
  @PreSave
  @PreUpdate
  public void makeSureRandomFieldIsFilled()
  {
    if (this.random_key==0) this.random_key = Math.random();
    
    this.portrait = width<=height;
    
  }
  
  public static ArtObject getRandomObject(boolean in_a_museum) {
  //  double max = Settings.getLongValue(in_a_museum?ArtObject.TOTAL_MUSEUM_OBJECTS:ArtObject.TOTAL_NON_MUSEUM_OBJECTS);
    
    
    ArtObject obj = null;
    
    while (obj==null)
    {
      double randomSplit = Math.random();
      //in_a_museum = true; // temp hack to get objects
      // TODO get better random object
      
      Query<ArtObject> results = ArtObject.all(ArtObject.class).filter("in_a_museum =", in_a_museum).filter("random_key >",randomSplit).limit(1);
      
      obj = results.get();
      
      // retry for the very rare case we found a too high random number :)
    }
    return obj;
  }
  
  public static void countAllObjects()
  {
    Query<ArtObject> q = ArtObject.all(ArtObject.class);
    
    long museum = 0, non_museum=0;
    Iterator<ArtObject> iter = q.iter().iterator();
    while (iter.hasNext())
    {
      ArtObject obj = iter.next();
      if (obj.in_a_museum) museum++; else non_museum++;
    }
    
    Settings.put(TOTAL_MUSEUM_OBJECTS, museum);
    Settings.put(TOTAL_NON_MUSEUM_OBJECTS, non_museum);
    
  }

  public void registerVote(boolean in_a_museum) {
    this.num_votes = this.num_votes + 1;
    if (in_a_museum) this.museum_votes = this.museum_votes + 1;
      else this.non_museum_votes = this.non_museum_votes + 1; 
    
    if ((in_a_museum && this.in_a_museum) || (!in_a_museum && !this.in_a_museum)) this.right_votes = this.right_votes + 1;
      else this.wrong_votes = this.wrong_votes + 1;
    
    save();
  }

}
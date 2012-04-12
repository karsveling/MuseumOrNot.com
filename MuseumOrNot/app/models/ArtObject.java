package models;

import java.util.Random

import play.modules.siena.EnhancedModel;
import siena.Id;

public class ArtObject extends EnhancedModel
{

  @Id
  public Long id;
  
  public String title;
  public String description;
  public String url;
  
  public int width;
  public int height;
  
  public float random_key;

  boolean portrait;
  
  boolean in_a_museum;
  
  public String image_url;
  
  public String institution;
  
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
    this.portrait = width<=height;
    this.random_key = Math.random()
  }

}

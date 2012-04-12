package models;

import play.modules.siena.EnhancedModel;
import siena.Id;

public class Institution 
{

  @Id
  public Long id;
  public String name;
  public String city;
  public String url;
  
  
  public Institution()
  {
  }

}
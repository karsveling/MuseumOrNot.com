package services;

import models.ArtObject;

public class ArtObjectService {

  public static ArtObject getRandom(boolean in_a_museum)
  {
    return ArtObject.all().order("random_key").filter("random_key >=", Math.random()).filter("in_a_museum =", in_a_museum).get();
  }

}

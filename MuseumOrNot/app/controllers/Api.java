package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Api extends Controller {

    public static void addArtObject(String title, String description, String url, String image_url, int width, int height, boolean in_a_museum, String institution) {
        
      ArtObject obj = new ArtObject(title, description, url, image_url, width, height, in_a_museum, institution);
      
      obj.save();
      renderHtml("Ok.");
    }

}
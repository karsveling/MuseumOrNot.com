package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Api extends Controller {

    public static void addArtObject(String title, String description, String url, String image_url, int width, int height, String institution) {
        
      ArtObject obj = new ArtObject(title, description, url, image_url, width, height, true, institution);
      
      obj.save();
      renderHtml("Ok.");
    }

}
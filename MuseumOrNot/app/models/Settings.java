package models;

import play.modules.siena.EnhancedModel;
import siena.Generator;
import siena.Id;

public class Settings extends EnhancedModel
{

  @Id(Generator.NONE)
  public String name;
  public String value;
  
  
  public Settings(String name, String value)
  {
    this.name=name;
    this.value=value;
  }
  
  public long getAsLong(long defaultValue)
  {
    try
    {
      return Long.parseLong(value);
    }
    catch (Exception e)
    {
      return defaultValue;
    }
  }
  
  public static long getLongValue(String name)
  {
    Settings s = Settings.findById(name);
    if (s==null) return -1;
    return s.getAsLong(-1);
  }
  
  public static void put(String name, long value)
  {
    Settings s = Settings.findById(name);
    if (s==null) s = new Settings(name, value+"");
    else s.value = value+"";
    s.save();
  }

}
package com.android.support.jhf.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * 
 * @author gyx
 * 
 * 功能：配置文件管理
 *
 */
public class ConfigManager
{
  private volatile static ConfigManager instance;
  
  public static final String CONFIG_NAME = "config";
  
  private Context context;
  
  private SharedPreferences.Editor editor;
  
  private SharedPreferences preferences;
  
  public static ConfigManager getInstance(Context context) {

    if (instance == null) {
      synchronized (ConfigManager.class) {
        if (instance == null) {
          instance = new ConfigManager(context);
        }
      }

    }
    return instance;
  }
  
  private ConfigManager(Context context) {
    
    this.context = context;
    
    openEditor();
  }
  
  // 创建或修改配置文件
  public void openEditor() {
    int mode = Activity.MODE_PRIVATE;
    preferences = context.getSharedPreferences(CONFIG_NAME, mode);
    editor = preferences.edit();
  }
  
  public void putBoolean(String name, boolean value) {
    
    editor.putBoolean(name, value);
    editor.commit();
  }
  
  public void putFloat(String name, float value) {
    
    editor.putFloat(name, value);
    editor.commit();
  }
  
  public void putInt(String name, int value) {
    
    editor.putInt(name, value);
    editor.commit();
  }
  
  public void putLong(String name, long value) {
    
    editor.putLong(name, value);
    editor.commit();
  }
  
  public void putString(String name, String value) {
    
    editor.putString(name, value);
    editor.commit();
  }

  public boolean loadBoolean(String key) {
    return preferences.getBoolean(key, false);
  }
  
  public float loadFloat(String key) {
    return preferences.getFloat(key, 0);
  }
  
  public int loadInt(String key) {
    return preferences.getInt(key, 0);
  }
  
  public int loadInt(String key, int defValue){
	  return preferences.getInt(key, defValue);
  }
  
  public long loadLong(String key) {
    return preferences.getLong(key, 0);
  }
  
  public String loadString(String key) {
    return preferences.getString(key, "");
  }
  
  /**
   * 删除对应的item
   * @param key
   */
  public void remove(String key){
	  editor.remove(key);
	  editor.commit();
  }
  
}

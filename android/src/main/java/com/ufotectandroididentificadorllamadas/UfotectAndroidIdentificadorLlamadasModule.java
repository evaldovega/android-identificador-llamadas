package com.ufotectandroididentificadorllamadas;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.role.RoleManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.common.references.SharedReference;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;

@ReactModule(name = UfotectAndroidIdentificadorLlamadasModule.NAME)
public class UfotectAndroidIdentificadorLlamadasModule extends ReactContextBaseJavaModule implements ActivityEventListener {
  public static final String NAME = "UfotectAndroidIdentificadorLlamadas";
  private Promise permiso;
  private SharedPreferences preferences;
  private SharedPreferences.Editor editor;
  String channel_id="UFOTECH";

  public UfotectAndroidIdentificadorLlamadasModule(ReactApplicationContext reactContext) {
    super(reactContext);
    preferences = reactContext.getSharedPreferences("UFOCALL", Context.MODE_PRIVATE);
    editor = preferences.edit();
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

  private void createNotificationChannel(){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationManager notificationManager = getReactApplicationContext().getSystemService(NotificationManager.class);
      notificationManager.deleteNotificationChannel(channel_id);

      NotificationChannel channel =  new NotificationChannel(channel_id, channel_id, NotificationManager.IMPORTANCE_HIGH);
      AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build();

      //channel.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+"://com.ufotectandroididentificadorllamadas/"+R.raw.truli), Notification.AUDIO_ATTRIBUTES_DEFAULT);
      channel.setDescription("Envia  notificaciónes para notificar al usuario de llamadas entrantes");

      notificationManager.createNotificationChannel(channel);
      Log.d("UFO:","Channel created");
    }
  }

  @ReactMethod
  public  void init(Promise promise){
    createNotificationChannel();
    promise.resolve(true);
  }

  @ReactMethod
  public void getMyNumber(Promise promise){
    String my_number = preferences.getString("my_number","");
    promise.resolve(my_number);
  }

  @ReactMethod
  public void setMyNumber(String my_number,Promise promise){
    editor.putString("my_number",my_number);
    editor.apply();
    promise.resolve("Número almacenado correctamente");
  }

  @ReactMethod
  public void getBackendUrl(Promise promise){
    String url = preferences.getString("backend_url","");
    promise.resolve(url);
  }

  @ReactMethod
  public void setBackendUrl(String url,Promise promise){
    editor.putString("backend_url",url);
    editor.apply();
    promise.resolve("Backend URL almacenada correctamente");
  }

  @ReactMethod
  public void getBackendAuth(Promise promise){
    String auth = preferences.getString("backend_auth","");
    promise.resolve(auth);
  }

  @ReactMethod
  public void setBackendAuth(String auth,Promise promise){
    editor.putString("backend_auth",auth);
    editor.apply();
    promise.resolve("Backend Auth almacenada correctamente");
  }

  @ReactMethod
  public void setPreference(String key,String value,Promise promise){
    editor.putString(key,value);
    editor.apply();
    promise.resolve("Preference almacenada correctamente");
  }

  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  public void multiply(double a, double b, Promise promise) {
    promise.resolve(a * b);
  }

  @ReactMethod
  public void getDefaultCallScreening(Promise promise){
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
      RoleManager manager = getReactApplicationContext().getSystemService(RoleManager.class);
      boolean isRoledHeld = manager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING);
      WritableMap result= Arguments.createMap();
      result.putBoolean("is_roled",isRoledHeld);
      promise.resolve(result);
      return;
    }
    promise.reject("Su dispositivo no es compatible para configurar un detector de llamadas");
  }

  @ReactMethod
  public void defaultCallScreening(Promise promise){
    try{

      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        permiso=promise;
        Intent intent = null;
        RoleManager roleManager = (RoleManager) getReactApplicationContext().getSystemService(RoleManager.class);
        intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING);
        getCurrentActivity().startActivityForResult(intent,1);
      }else{
        permiso.reject("Su dispositivo no soporta esta funcionalidad");
        permiso=null;
      }

    }catch (Exception e){
      Log.e("UFO:CALL Screening",e.getMessage());
      permiso.reject(e.getMessage());
      permiso=null;
    }
  }

  @Override
  public void onActivityResult(Activity activity, int i, int i1, @Nullable Intent intent) {
    if(i==1){
      Log.d("UFO:","onActivityResult");
      if(i1 == Activity.RESULT_OK){
        if(permiso!=null) {
          Log.d("UFO:","concedido");
          permiso.resolve("ok");
        }
      }else{
        Log.d("UFO:","no concedido");
        permiso.reject("E_REJECT_TO_ROLE_MANAGER");
      }
    }

    permiso=null;
  }

  @Override
  public void onNewIntent(Intent intent) {

  }
}

package com.ufotectandroididentificadorllamadas;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.telecom.Call;
import android.telecom.CallScreeningService;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@TargetApi(Build.VERSION_CODES.N)
public class Servicio extends CallScreeningService {
  private RequestQueue mRequestQueue;
  String number="";
  String channel_id="UFOTECH";
  static int importance = NotificationManager.IMPORTANCE_MAX;
  static Llamada llamada;
  SharedPreferences preferences;

  private void detect(String number_agent){
    String backend_url=preferences.getString("backend_url","");
    String my_number = preferences.getString("my_number","");

    mRequestQueue = Volley.newRequestQueue(getApplicationContext());
    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, backend_url, null, new Response.Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {
        Log.i("UFO: Call screening","Response :" + response.toString());
        try {
          boolean valid = response.getBoolean("valid");
          if(valid) {
            String bussines = response.getString("bussines");
            String subject = response.getString("subject");

            llamada.agenteDetectado(number_agent,bussines,subject);

           //notifyCall(getId(number_agent), bussines, subject);
          }else{
            clearNotification(getId(number_agent));
            Log.e("UFO:","Number "+number_agent+" not valid");
          }
        }catch (JSONException e) {
          e.printStackTrace();
          clearNotification(getId(number_agent));
          Log.e("UFO:",e.getMessage());
        }
      }
    }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        Log.d("UFO: Call screeing",error.toString());
        clearNotification(getId(number_agent));
      }
    }){
      @Override
      public Map<String,String> getHeaders(){
        HashMap<String, String> headers = new HashMap<String, String>();
        String backend_auth=preferences.getString("backend_auth","");
        if(backend_auth.equals("")==false){
          headers.put("Authorization","Bearer "+backend_auth);
        }
        return headers;
      }
      @Override
      public Map<String,String> getParams(){
        Map<String,String> params=new HashMap<String,String>();
        params.put("number_user",my_number);
        params.put("number_agent",number_agent);
        return  params;
      }
    };

    mRequestQueue.add(request);

  }

  private void powerOn(){
    KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
    KeyguardManager.KeyguardLock keyguard = km.newKeyguardLock("UFOCALL");
    keyguard.disableKeyguard();

    PowerManager power = (PowerManager) getApplicationContext().getSystemService(getApplicationContext().POWER_SERVICE);
    boolean isScreenOn = Build.VERSION.SDK_INT >= 20 ? power.isInteractive() : power.isScreenOn();
    if (!isScreenOn) {
      PowerManager.WakeLock wl = power.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "myApp:notificationLock");
      wl.acquire(5000);
      wl.release();
    }
  }

  private void createNotificationChannel(){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationManager notificationManager = getSystemService(NotificationManager.class);
      notificationManager.deleteNotificationChannel(channel_id);

      AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build();

      NotificationChannel channel = new NotificationChannel(channel_id, channel_id, NotificationManager.IMPORTANCE_HIGH);
      channel.enableLights(true);
      channel.enableVibration(true);
      channel.setDescription("Envia notificaciónes para notificar al usuario de llamadas entrantes");
      channel.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+"://com.ufotectandroididentificadorllamadas/"+R.raw.truli),audioAttributes);

      notificationManager.createNotificationChannel(channel);
      Log.d("UFO:","Channel created");
    }
  }

  private void notifyCall(int id,String bussines,String subject){
    Log.d("UFO:","Notify call");
    Intent fullScreenIntent = new Intent(Intent.ACTION_MAIN);
    fullScreenIntent.setComponent(new ComponentName(getApplicationContext().getPackageName(), getApplicationContext().getPackageName()+".MainActivity"));
    PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
      fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channel_id)
      .setSmallIcon(R.drawable.icon)
      .setCategory(NotificationCompat.CATEGORY_CALL)
      .setContentTitle(bussines)
      .setContentText("Te llamaré pronto.")
      .setVibrate(new long[]{0, 500, 1000})
      .setStyle(new NotificationCompat.BigTextStyle().bigText(subject).setBigContentTitle("mas información"))
      .setOngoing(true)
      .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
      .setPriority(NotificationCompat.PRIORITY_MAX)
      .setAutoCancel(true)
      .setFullScreenIntent(fullScreenPendingIntent, true);

    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
    notificationManager.notify(id, builder.build());
  }

  private int getId(String number){
    return  Integer.parseInt(number.substring(number.length()-4,number.length()));
  }

  private void clearNotification(int id) {
    Log.d("UFO:","Clear notification");
    NotificationManager notificationManager = (NotificationManager) getApplicationContext()
      .getSystemService(Context.NOTIFICATION_SERVICE);
      notificationManager.cancel(id);
  }

  @Override
  public void onScreenCall(@NonNull Call.Details details) {
    preferences = getApplicationContext().getSharedPreferences("UFOCALL", Context.MODE_PRIVATE);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      if(details.getCallDirection()==Call.Details.DIRECTION_INCOMING){
        Log.d("UFO:","Incoming call "+getPackageName());
        llamada = Llamada.getInstance(getApplicationContext());

        int resId = getApplicationContext().getResources().getIdentifier("sound", "raw", getPackageName());
        final MediaPlayer mp=MediaPlayer.create(getApplicationContext(),resId);
        mp.start();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
          @Override
          public void onCompletion(MediaPlayer mediaPlayer) {
            mp.start();
          }
        });

        number=details.getHandle().toString().replace("tel:","");

        try{
          powerOn();
          llamada.setAgente(number);
          llamada.notificarLlamada();
          detect(number);
          CallResponse.Builder response = new CallResponse.Builder();

          final Handler handler = new Handler();
          handler.postDelayed(new Runnable() {
            @Override
            public void run() {
             mp.release();
            }
          }, 7000);

          final Handler handler2 = new Handler();
          handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
              respondToCall(details,response.setSilenceCall(true).build());
            }
          }, 4000);





        }catch (Exception e){
          Log.e("UFO:",e.getMessage());
          mp.release();
        }
      }
    }else{
      Log.d("UFO:","Android version bad");
      CallResponse.Builder response = new CallResponse.Builder();
      respondToCall(details,response.build());
    }
  }
}

package com.ufotectandroididentificadorllamadas;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class Util {
  Context ctx;

  public Util(Context ctx){
    this.ctx=ctx;
  }

  public String myNumber() {
    TelephonyManager manager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
    if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
      return "";
    }
    String m1 = manager.getLine1Number();
    Log.d("UFO","M2 "+m1);
    if(m1!=null && m1.equals("")){
      return  m1;
    }

    String m2 = "";
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
      m2 = Integer.toString(manager.getSubscriptionId());
    }
    Log.d("UFO","M2 "+m2);

    return m2;
  }
}

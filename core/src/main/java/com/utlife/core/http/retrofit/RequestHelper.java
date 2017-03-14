package com.utlife.core.http.retrofit;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gzsll on 2014/9/23 0023.
 */
public class RequestHelper {

  private Context mContext;

  public RequestHelper(Context mContext) {
    this.mContext = mContext;
  }

  private Map<String,String> extraMap = new HashMap<>();
  public void addExtraMap(Map<String,String> extraMap){
    extraMap.putAll(extraMap);
  }

  public Map<String, String> getHttpRequestMap() {
    HashMap<String, String> map = new HashMap<String, String>();
    map.put("client", getDeviceId());
    if(extraMap != null && extraMap.size() > 0){
      map.putAll(extraMap);
    }
    return map;
  }

  public String getAndroidId() {
    return Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
  }

  public String getDeviceId() {
    String deviceId;
    TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
    if (tm.getDeviceId() == null) {
      deviceId = getAndroidId();
    } else {
      deviceId = tm.getDeviceId();
    }
    return deviceId;
  }

}

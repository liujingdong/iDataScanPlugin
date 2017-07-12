package iData.scan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.scan.IDataScanActivity;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by CrazyDong on 2017/7/10.
 * 针对工厂机iData扫描的插件
 */
public class IDataScanPlugin extends CordovaPlugin {
  private CallbackContext scanCallbackContext;

  @Override
  public boolean execute(String action, JSONArray args, final CallbackContext callbackContext)
    throws JSONException {
    if(action.equals("singleScan")){
      scanCallbackContext = callbackContext;
      Context appCtx = cordova.getActivity().getApplicationContext();
      Intent scanIntent = new Intent(appCtx,IDataScanActivity.class);
      cordova.startActivityForResult(this,scanIntent,200);
      return true;
    }
    return false;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    switch (resultCode){
      case Activity.RESULT_OK:
        String scanResult = intent.getStringExtra("scanResult");
        scanCallbackContext.success(scanResult);
        break;
    }
  }
}

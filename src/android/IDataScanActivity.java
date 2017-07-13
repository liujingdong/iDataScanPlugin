package org.apache.cordova.scan;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.chinaZhongWang.community.R;

/**
 * Created by CrazyDong on 2017/7/10.
 */
public class IDataScanActivity extends Activity{
  private int i;
  private ScannerInterface scanner;
  private IntentFilter intentFilter;
  private BroadcastReceiver scanReceiver;
  private boolean isContinue = false;	//连续扫描的标志
  private static final String RES_ACTION = "android.intent.action.SCANRESULT";
  private String scanResult;//扫描后的结果

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    setContentView(R.layout.idatascan);
    initScanner();

    super.onCreate(savedInstanceState);
  }

  /*初始化*/
  private void initScanner(){
    i = 0;
    scanner = new ScannerInterface(this);
    scanner.open();	//打开扫描头上电   scanner.close();//打开扫描头下电
    scanner.enablePlayBeep(true);//是否允许蜂鸣反馈
    scanner.enableFailurePlayBeep(false);//扫描失败蜂鸣反馈
    scanner.enablePlayVibrate(true);//是否允许震动反馈
    scanner.enableAddKeyValue(1);/**附加无、回车、Teble、换行*/
    scanner.timeOutSet(2);//设置扫描延时2秒
    scanner.intervalSet(1000); //设置连续扫描间隔时间
    scanner.lightSet(false);//关闭右上角扫描指示灯
    scanner.enablePower(true);//省电模式
    scanner.setMaxMultireadCount(5);//设置一次最多解码5个
    //		scanner.addPrefix("AAA");//添加前缀
    //		scanner.addSuffix("BBB");//添加后缀
    //		scanner.interceptTrimleft(2); //截取条码左边字符
    //		scanner.interceptTrimright(3);//截取条码右边字符
    //		scanner.filterCharacter("R");//过滤特定字符
    scanner.SetErrorBroadCast(true);//扫描错误换行
    //scanner.resultScan();//恢复iScan默认设置

    //		scanner.lockScanKey();
    //锁定设备的扫描按键,通过iScan定义扫描键扫描，用户也可以自定义按键。
    scanner.unlockScanKey();
    //释放扫描按键的锁定，释放后iScan无法控制扫描按键，用户可自定义按键扫描。

    /**设置扫描结果的输出模式，参数为0和1：
     * 0为模拟输出（在光标停留的地方输出扫描结果）；
     * 1为广播输出（由应用程序编写广播接收者来获得扫描结果，并在指定的控件上显示扫描结果）
     * 这里采用接收扫描结果广播并在TextView中显示*/
    scanner.setOutputMode(1);

    //扫描结果的意图过滤器的动作一定要使用"android.intent.action.SCANRESULT"
    intentFilter = new IntentFilter(RES_ACTION);
    //注册广播接受者
    scanReceiver =  new ScannerResultReceiver();
    registerReceiver(scanReceiver,intentFilter);
    scanner.scan_start();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    finishScanner();
    scanner.lockScanKey();//锁定iScan扫描按键
  }

  /*结束扫描*/
  private void finishScanner(){
    scanner.scan_stop();
    //		scanner.close();	//关闭iscan  非正常关闭会造成iScan异常退出
    unregisterReceiver(scanReceiver);
    scanner.continceScan(false);
  }

  /*客户自定义按键处理方式
  * 指定只能按键值为139的物理按键(中间黄色按键)按下来触发扫描
  * */
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if(keyCode == 139 && event.getRepeatCount() == 0){
      scanner.scan_start();
    }
    return super.onKeyDown(keyCode, event);
  }

  @Override
  public boolean onKeyUp(int keyCode, KeyEvent event) {
    if(keyCode == 139){
      scanner.scan_stop();
    }else if (keyCode == 140){
      scanner.scan_stop();
      isContinue = !isContinue;
      if(isContinue){
        scanner.continceScan(true);
      }else{
        scanner.continceScan(false);
      }
    }
    return super.onKeyUp(keyCode, event);
  }

  /*扫描结果的广播接受者*/
  private class ScannerResultReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
      if(intent.getAction().equals(RES_ACTION)){
        //获取扫描结果
         scanResult = intent.getStringExtra("value");
         putValue(scanResult);



//        int barocodelen=intent.getIntExtra("length",0);
//        int type =intent.getIntExtra("type", 0);
//        String myType =  String.format("%c", type);
//        tvScanResult.append("Length："+barocodelen+"  Type:"+myType+"  第"+i+"个CodeBar："+scanResult);
//				tvScanResult.append("Length："+barocodelen+"  第"+i+"个CodeBar："+scanResult);
        i++;
      }
    }
  }

   public void putValue(String scanResult){
//     Intent resultIntent = new Intent();
//     Bundle bundle = new Bundle();
//     bundle.putSerializable("scanResult",scanResult);
//     resultIntent.putExtras(bundle);
//     setResult(Activity.RESULT_OK,resultIntent);
//     finish();

     Intent resultIntent = new Intent();
     resultIntent.putExtra("scanResult",scanResult);
     setResult(Activity.RESULT_OK,resultIntent);
     finish();
   }


}

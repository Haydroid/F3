package com.act.f6test;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import static android.widget.Toast.*;

/**
 * Created by Administrator on 2017/2/21 0021.
 */
public class wifiCDialog extends Dialog {

    OnCloseListener mCloseListener = null;
    private File sdCardDir;
    private File internalDir;
    private File saveFile;
    private FileOutputStream outStream;
    private FileInputStream  inStream;
    private String AbsolutePath;
    private TextView ip_text;
    public  boolean bConnect = false;
    Context mContext;

    public void setCloseListener(OnCloseListener listener) {
        mCloseListener = listener;
    }

    public interface OnCloseListener {
        void onClose(boolean isOk);
    }

    public wifiCDialog(Context context) {
        super(context);
        mContext = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wificonn);
        setTitle("      wifi Connect");
        setCancelable(false);
        ip_text = (TextView) findViewById(R.id.ipaddr);
        //得到SD卡路径，需要在AndroidManifest.xml中配置
        sdCardDir = Environment.getExternalStorageDirectory();
        AbsolutePath = (String) sdCardDir.getAbsolutePath();
        saveFile = new File(sdCardDir,"Wifi_IP.txt");
        Log.i("IP","ip开始 : "+AbsolutePath);
        ReadFile();
        final Button OkButton = (Button) findViewById(R.id.btnConnect);
        OkButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView txt1,text2;
                dismiss();
                txt1 = (TextView)findViewById(R.id.ipaddr);
                text2 = (TextView)findViewById(R.id.post) ;
                try {
                    //bConnect = Application.cr.connect(txt1.getText().toString(),Integer.valueOf(text2.getText().toString()),1);
                    if(bConnect)  ;//Application.showInfo("WIFI连接成功!!", "WIFI", mContext);
                    else Application.showInfo("WIFI连接失败", "wifi", mContext);
                    WriteFiles();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mCloseListener != null)
                    mCloseListener.onClose(true);
            }
        });

        final Button CancelButton = (Button) findViewById(R.id.btnCancel);
        CancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //dismiss();
                cancel();
                if (mCloseListener != null)
                    mCloseListener.onClose(false);
            }
        });
    }

    public void ReadFile()
    {
        int len;
        //创建一个字节数组输出流
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            //初始化 流对象
            try{
                inStream = new FileInputStream(saveFile);
            }catch (FileNotFoundException e){
                //Toast.makeText(this, "实例化inStream！", Toast.LENGTH_SHORT).show();
                return ;
            }
            try{
                //Log.v("Read", "读数据");
                while((
                        len=inStream.read(buffer)
                )!=-1){
                    ostream.write(buffer, 0, len);
                }
               // Log.v("Read", "读成功");
            }catch (IOException e){

            }
            try{
                //关闭输入流和输出流
                inStream.close();
                ostream.close();
                //Log.v("Read", "读成功！");
            }catch (IOException e){
                //Toast.makeText(this, "读 异常！", Toast.LENGTH_SHORT).show();
            }
            String str = new String(ostream.toByteArray());
            Log.v("Disp", "文本显示！");
            ip_text.setText(str);

        }else{
            //Toast.makeText(this, "内存卡不存在！", Toast.LENGTH_SHORT).show();
            return ;
        }

        return;
    }


    private void WriteFiles()
    {

        TextView txt_IP;
        txt_IP = (TextView)findViewById(R.id.ipaddr);

        Log.i("IP",txt_IP.getText().toString());
        //监测SD卡是否挂载
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            //创建流对象
            try{
                outStream = new FileOutputStream(saveFile,false);
            }catch (FileNotFoundException e){
                //Toast.makeText(this, "Start SD writing operation", Toast.LENGTH_SHORT).show();
                return ;
            }
            //写入文件
            try {
                outStream.write(txt_IP.getText().toString().getBytes());
            }catch(FileNotFoundException e) {
                // TODO Auto-generated catch block
                Log.i("IP","FileNotFoundException 异常");
            } catch (IOException e){
                // TODO Auto-generated catch block
                Log.i("IP","IOException 异常");
            } catch (NullPointerException e){
                Log.i("IP","NullPointerException 异常");
            }
            finally{
                try{
                    //关闭输出流
                    outStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    Log.i("IP","关闭 IO 异常");
                }
                Log.i("IP","写入完毕");
            }

        }else {
            Log.i("IP","SD 没有挂载或不存在");
            return ;
        }
    }
}

package com.act.f6test;

import android.content.res.Resources;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    FragmentTabHost mTabHost = null;
    int iditem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Application.mainView = this;
        Application.CTX = this;
        initTabHost();
        setStatus(false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        iditem = item.getItemId();
        int lRet;
        String strMsg = new String();
        switch (iditem)
        {
            case R.id.connect:
                ConnDialog dlg = new ConnDialog(this);
                dlg.setCloseListener(new ConnDialog.OnCloseListener() {
                    @Override
                    public void onClose(boolean isOk) {
                        if (isOk)
                            setStatus(true);
                    }
                });
                dlg.show();
                break;
            case R.id.disconnect:
                try {
                    if (Application.testThread != null) {
                        if (Application.testThread.isAlive())
                        {
                            Application.showError("正在测试中。请停止测试，然后再断开连接。", MainActivity.this);
                            return super.onOptionsItemSelected(item);
                        }
                    }
                    Application.cr.disconnect();
                    strMsg = "连接断开成功!!";
                    setStatus(false);
                    Toast.makeText(getApplication(), strMsg,
                            Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    Application.showError(e.getMessage(), MainActivity.this);
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initTabHost() {
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        Resources resources = getResources();
        addTab(mTabHost, resources.getString(R.string.base), BaseFragment.class);
        addTab(mTabHost, resources.getString(R.string.cpu), CpuFragment.class);
       /*addTab(mTabHost, resources.getString(R.string.mag), MagFragment.class);
        addTab(mTabHost, resources.getString(R.string.test), TestFragment.class);*/
    }

    private void addTab(FragmentTabHost tabHost, String tag, Class<?> clss) {
        FragmentTabHost.TabSpec tabSpec = tabHost.newTabSpec(tag);
        tabSpec.setIndicator(tag);
        tabHost.addTab(tabSpec, clss, null);
    }

    private void setStatus(boolean isConnected) {
        String title = getString(R.string.app_name);
        String status = "";
        if (isConnected) {
            if(iditem  == R.id.connect) status = "当前连接方式：串口";
            title += "[" + status + "]";
        }
        setTitle(title);
    }
    @Override
    public void finish()
    {
        if (Application.testThread != null) {
            if (Application.testThread.isAlive()) {
                Application.testThread.interrupt();
                Application.testThread = null;
            }
        }

        try {
            Application.cr.disconnect();
        }catch(Exception ex) {

        }

        super.finish();
    }
}

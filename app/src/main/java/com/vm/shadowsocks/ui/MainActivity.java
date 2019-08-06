package com.vm.shadowsocks.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.graphics.Color;
import android.widget.LinearLayout;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.vm.shadowsocks.R;
import com.vm.shadowsocks.core.AppInfo;
import com.vm.shadowsocks.core.AppProxyManager;
import com.vm.shadowsocks.core.LocalVpnService;
import com.vm.shadowsocks.core.ProxyConfig;

import java.util.Calendar;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import at.markushi.ui.CircleButton;
import cn.forward.androids.views.BitmapScrollPicker;
import cn.forward.androids.views.ScrollPickerView;

public class MainActivity extends Activity implements
        View.OnClickListener,
        OnCheckedChangeListener,
        LocalVpnService.onStatusChangedListener {

    private static String GL_HISTORY_LOGS;

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String CONFIG_URL_KEY = "CONFIG_URL_KEY";

    private static final int START_VPN_SERVICE_REQUEST_CODE = 1985;

    private TextView textViewProxyUrl, textViewProxyApp;
    private Calendar mCalendar;
    private BitmapScrollPicker mPickerHorizontal;
    private boolean StartVpnChecked = false;
    private Animation operatingAnim;
    private ImageView infoOperatingIV;
    private Vector<String> proxy_vec = new Vector<String>();
    private Vector<String> country_vec = new Vector<String>();
    private String selectCountry = "America";

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.176.17:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.176.217:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.176.228:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.176.48:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.176.8:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.178.100:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.178.119:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.178.123:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.178.135:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.178.148:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.178.154:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.184.183:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.184.194:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.184.196:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.184.216:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.184.222:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.184.224:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.184.241:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.188.13:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.188.170:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.188.18:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.188.181:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.188.191:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.188.195:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.188.200:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.188.210:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.188.247:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.188.36:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.188.85:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.191.199:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.191.243:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.191.69:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.191.70:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.64.107:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.64.154:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.64.170:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.64.242:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.64.27:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.64.59:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.64.85:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.65.249:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.66.174:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.66.189:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.66.212:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.66.39:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.66.74:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.67.27:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.68.207:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.68.24:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.72.5:4431");
        proxy_vec.add("ss://aes-128-cfb:Xf4aGbTaf1@104.248.72.63:4431");
        ProxyConfig.Instance.globalMode = true;
        mPickerHorizontal = (BitmapScrollPicker) findViewById(R.id.picker_03_horizontal);
        final CopyOnWriteArrayList<Bitmap> bitmaps = new CopyOnWriteArrayList<Bitmap>();
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.aodaliya));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.baxi));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.deguo));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.faguo));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.hanguo));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.helan));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.jianada));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.meiguo));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.putaoya));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.riben));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.xianggang));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.xinxilan));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.yindu));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.yindunixiya));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.yinggelan));
        country_vec.add("Australia");
        country_vec.add("Brazil");
        country_vec.add("Germany");
        country_vec.add("France");
        country_vec.add("Korea");
        country_vec.add("Holland");
        country_vec.add("Canada");
        country_vec.add("America");
        country_vec.add("Portugal");
        country_vec.add("Japan");
        country_vec.add("Hong Kong");
        country_vec.add("New Zealand");
        country_vec.add("India");
        country_vec.add("Indonesia");
        country_vec.add("England");

        mPickerHorizontal.setData(bitmaps);
        mPickerHorizontal.setOnSelectedListener(new ScrollPickerView.OnSelectedListener() {
            @Override
            public void onSelected(ScrollPickerView scrollPickerView, int position) {
                TextView countr_text = (TextView) findViewById(R.id.countryName);
                selectCountry = country_vec.get(position);
                countr_text.setText(selectCountry);
                if (!LocalVpnService.IsRunning) {
                    TextView country_text2 = (TextView) findViewById(R.id.countryName2);
                    country_text2.setText(selectCountry);
                }
            }
        });
        findViewById(R.id.ProxyUrlLayout).setOnClickListener(this);
        findViewById(R.id.AppSelectLayout).setOnClickListener(this);

        textViewProxyUrl = (TextView) findViewById(R.id.textViewProxyUrl);
        String ProxyUrl = readProxyUrl();
        if (TextUtils.isEmpty(ProxyUrl)) {
            textViewProxyUrl.setText(R.string.config_not_set_value);
        } else {
            textViewProxyUrl.setText(ProxyUrl);
        }
        mCalendar = Calendar.getInstance();
        LocalVpnService.addOnStatusChangedListener(this);

        //Pre-App Proxy
        if (AppProxyManager.isLollipopOrAbove){
            new AppProxyManager(this);
            textViewProxyApp = (TextView) findViewById(R.id.textViewAppSelectDetail);
        } else {
            ((ViewGroup) findViewById(R.id.AppSelectLayout).getParent()).removeView(findViewById(R.id.AppSelectLayout));
            ((ViewGroup) findViewById(R.id.textViewAppSelectLine).getParent()).removeView(findViewById(R.id.textViewAppSelectLine));
        }

        infoOperatingIV = (ImageView)findViewById(R.id.infoOperating);
        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.tip);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
    }

    String readProxyUrl() {
        int rand_num = (int)(Math.random()) * proxy_vec.size();
        return proxy_vec.get(rand_num);// editText.getText().toString().trim();
    }

    void setProxyUrl(String ProxyUrl) {
        SharedPreferences preferences = getSharedPreferences("shadowsocksProxyUrl", MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString(CONFIG_URL_KEY, ProxyUrl);
        editor.apply();
    }

    String getVersionName() {
        PackageManager packageManager = getPackageManager();
        if (packageManager == null) {
            Log.e(TAG, "null package manager is impossible");
            return null;
        }

        try {
            return packageManager.getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "package not found is impossible", e);
            return null;
        }
    }

    boolean isValidUrl(String url) {
        try {
            if (url == null || url.isEmpty())
                return false;

            if (url.startsWith("ss://")) {//file path
                return true;
            } else { //url
                Uri uri = Uri.parse(url);
                if (!"http".equals(uri.getScheme()) && !"https".equals(uri.getScheme()))
                    return false;
                if (uri.getHost() == null)
                    return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onClick(View v) {

    }

    private void scanForProxyUrl() {
        new IntentIntegrator(this)
                .setPrompt(getString(R.string.config_url_scan_hint))
                .initiateScan(IntentIntegrator.QR_CODE_TYPES);
    }

    private void showProxyUrlInputDialog() {
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        editText.setHint(getString(R.string.config_url_hint));
        editText.setText(readProxyUrl());

        new AlertDialog.Builder(this)
                .setTitle(R.string.config_url)
                .setView(editText)
                .setPositiveButton(R.string.btn_ok, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText() == null) {
                            return;
                        }
                        int rand_num = (int)(Math.random()) * proxy_vec.size();
                        String ProxyUrl = proxy_vec.get(rand_num);// editText.getText().toString().trim();
                        Toast.makeText(MainActivity.this, ProxyUrl, Toast.LENGTH_SHORT).show();

                        if (isValidUrl(ProxyUrl)) {
                            setProxyUrl(ProxyUrl);
                            textViewProxyUrl.setText(ProxyUrl);
                        } else {
                            Toast.makeText(MainActivity.this, ProxyUrl, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onLogReceived(String logString) {
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        logString = String.format("[%1$02d:%2$02d:%3$02d] %4$s\n",
                mCalendar.get(Calendar.HOUR_OF_DAY),
                mCalendar.get(Calendar.MINUTE),
                mCalendar.get(Calendar.SECOND),
                logString);

        System.out.println(logString);
    }

    @Override
    public void onStatusChanged(String status, Boolean isRunning) {
        CircleButton c_btn = (CircleButton)findViewById(R.id.start_vpn);
        c_btn.setEnabled(true);
        TextView country_text2 = (TextView) findViewById(R.id.countryName2);
        country_text2.setText(selectCountry);
        if (isRunning) {
            infoOperatingIV.clearAnimation();
            c_btn.setColor(Color.parseColor("#009800"));
            ImageView conn_fail_circle = (ImageView)findViewById(R.id.infoOperating);
            conn_fail_circle.setVisibility(View.GONE);
            ImageView conn_fail_image = (ImageView)findViewById(R.id.notConnectImage);
            conn_fail_image.setVisibility(View.GONE);
            ImageView conn_succ_circle = (ImageView)findViewById(R.id.infoOperating2);
            conn_succ_circle.setVisibility(View.VISIBLE);
            ImageView conn_succ_image = (ImageView)findViewById(R.id.connectedImage);
            conn_succ_image.setVisibility(View.VISIBLE);
            Toast.makeText(this, "LegoVPN Connected!", Toast.LENGTH_SHORT).show();
        } else {
            c_btn.setColor(Color.parseColor("#989898"));
            ImageView conn_fail_circle = (ImageView)findViewById(R.id.infoOperating);
            conn_fail_circle.setVisibility(View.VISIBLE);
            ImageView conn_succ_circle = (ImageView)findViewById(R.id.infoOperating2);
            conn_succ_circle.setVisibility(View.GONE);
            ImageView conn_fail_image = (ImageView)findViewById(R.id.notConnectImage);
            conn_fail_image.setVisibility(View.VISIBLE);
            ImageView conn_succ_image = (ImageView)findViewById(R.id.connectedImage);
            conn_succ_image.setVisibility(View.GONE);
            Toast.makeText(this, "LegoVPN Disconnect!", Toast.LENGTH_SHORT).show();
        }
    }

    public void startVpn(View view) {
        CircleButton c_btn = (CircleButton)findViewById(R.id.start_vpn);
        c_btn.setEnabled(false);
        if (LocalVpnService.IsRunning != true) {
            Intent intent = LocalVpnService.prepare(this);
            if (intent == null) {
                startVPNService();
            } else {
                startActivityForResult(intent, START_VPN_SERVICE_REQUEST_CODE);
            }
            if (operatingAnim != null) {
                infoOperatingIV.startAnimation(operatingAnim);
            }
            LocalVpnService.IsRunning = true;
        } else {
            LocalVpnService.IsRunning = false;
        }
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    private void startVPNService() {
        int rand_num = (int)(Math.random()) * proxy_vec.size();
        String ProxyUrl = proxy_vec.get(rand_num);// editText.getText().toString().trim();

        if (!isValidUrl(ProxyUrl)) {
            Toast.makeText(this, "invalid proxy config.", Toast.LENGTH_SHORT).show();
            return;
        }

        LocalVpnService.ProxyUrl = ProxyUrl;
        startService(new Intent(this, LocalVpnService.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_about:
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.app_name) + getVersionName())
                        .setMessage(R.string.about_info)
                        .setPositiveButton(R.string.btn_ok, null)
                        .setNegativeButton(R.string.btn_more, new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                                // startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("")));
                            }
                        })
                        .show();

                return true;
            case R.id.menu_item_exit:
                if (!LocalVpnService.IsRunning) {
                    finish();
                    return true;
                }

                new AlertDialog.Builder(this)
                        .setTitle(R.string.menu_item_exit)
                        .setMessage(R.string.exit_confirm_info)
                        .setPositiveButton(R.string.btn_ok, new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LocalVpnService.IsRunning = false;
                                LocalVpnService.Instance.disconnectVPN();
                                stopService(new Intent(MainActivity.this, LocalVpnService.class));
                                System.runFinalization();
                                System.exit(0);
                            }
                        })
                        .setNegativeButton(R.string.btn_cancel, null)
                        .show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppProxyManager.isLollipopOrAbove) {
            if (AppProxyManager.Instance.proxyAppInfo.size() != 0) {
                String tmpString = "";
                for (AppInfo app : AppProxyManager.Instance.proxyAppInfo) {
                    tmpString += app.getAppLabel() + ", ";
                }
                textViewProxyApp.setText(tmpString);
            }
        }
    }

    @Override
    protected void onDestroy() {
        LocalVpnService.removeOnStatusChangedListener(this);
        super.onDestroy();
    }

}

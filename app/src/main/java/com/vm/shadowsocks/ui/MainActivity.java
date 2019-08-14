package com.vm.shadowsocks.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceControl;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ListView;
import android.widget.BaseAdapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.graphics.Color;
import android.app.ListActivity;
import android.os.Environment;
import android.widget.LinearLayout;
import android.content.res.AssetManager;
import android.net.VpnService;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.os.Handler;
import android.os.Message;
import android.app.AlertDialog;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.vm.shadowsocks.R;
import com.vm.shadowsocks.core.AppInfo;
import com.vm.shadowsocks.core.AppProxyManager;
import com.vm.shadowsocks.core.LocalVpnService;
import com.vm.shadowsocks.core.ProxyConfig;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.HashMap;
import java.net.NetworkInterface;
import java.net.InetAddress;
import java.net.Inet4Address;
import java.util.Enumeration;
import java.net.SocketException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import at.markushi.ui.CircleButton;
import cn.forward.androids.views.BitmapScrollPicker;
import cn.forward.androids.views.ScrollPickerView;

import org.json.JSONObject;
import org.json.JSONArray;

public class MainActivity extends ListActivity implements
        View.OnClickListener,
        OnCheckedChangeListener,
        LocalVpnService.onStatusChangedListener {
    static {
        System.loadLibrary("native-lib");
    }
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String CONFIG_URL_KEY = "CONFIG_URL_KEY";
    private static final int START_VPN_SERVICE_REQUEST_CODE = 1985;
    private Calendar mCalendar;
    private BitmapScrollPicker mPickerHorizontal;
    private boolean StartVpnChecked = false;
    private Animation operatingAnim;
    private ImageView infoOperatingIV;
    private Vector<String> proxy_vec = new Vector<String>();
    private Vector<String> country_vec = new Vector<String>();
    private String selectCountry = "America";
    private VpnService vpn_service = new VpnService();
    private CheckTransaction check_tx = new CheckTransaction();
    private static final int COMPLETED = 0;
    private HashMap<Integer, String> block_hashmap = new HashMap<Integer, String>();
    ArrayList<String> listItems=new ArrayList<String>();
    ArrayAdapter<String> adapter;
    int list_counter = 0;
    private String int_tx_hash;

    private static JSONObject getBaseRequest() {
        try {
            return new JSONObject()
                    .put("apiVersion", 2)
                    .put("apiVersionMinor", 0);
        } catch (org.json.JSONException e) {
            return null;
        }
    }

    private static JSONObject getTokenizationSpecification() {
        try {
            JSONObject tokenizationSpecification = new JSONObject();
            tokenizationSpecification.put("type", "DIRECT");
            tokenizationSpecification.put(
                    "parameters",
                    new JSONObject()
                            .put("protocolVersion", "ECv2")
                            .put("publicKey", "BOdoXP1aiNp.....kh3JUhiSZKHYF2Y="));
            return tokenizationSpecification;
        } catch (org.json.JSONException e) {
            return null;
        }
    }
    private static JSONArray getAllowedCardNetworks() {
            return new JSONArray()
                .put("AMEX")
                .put("DISCOVER")
                .put("INTERAC")
                .put("JCB")
                .put("MASTERCARD")
                .put("VISA");


    }

    private static JSONArray getAllowedCardAuthMethods() {
        return new JSONArray()
                .put("PAN_ONLY")
                .put("CRYPTOGRAM_3DS");
    }

    private static JSONObject getBaseCardPaymentMethod() {
        try {
            JSONObject cardPaymentMethod = new JSONObject();
            cardPaymentMethod.put("type", "CARD");
            cardPaymentMethod.put(
                    "parameters",
                    new JSONObject()
                            .put("allowedAuthMethods", getAllowedCardAuthMethods())
                            .put("allowedCardNetworks", getAllowedCardNetworks()));

            return cardPaymentMethod;
        } catch (org.json.JSONException e) {
            return null;
        }
    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == COMPLETED) {
                String res = (String)msg.obj;
                String[] split = res.split("\t");
                if (split.length == 5) {
                    if (!split[4].equals(int_tx_hash)) {
                        String list_item = split[0] + ", now balance: " + split[1];
                        listItems.add(list_item);
                        adapter.notifyDataSetChanged();
                    }

                    TextView balance = (TextView)findViewById(R.id.account_balance);
                    balance.setText("LEGO：" + split[1]);
                    String block_item = "\n\n\n    Transaction Hash: \n    " + split[4] + "\n\n    Block Height：" + split[2] + "\n\n    Block Hash：\n    " + split[3] + "\n\n\n";
                    block_hashmap.put(list_counter, block_item);
                    ++list_counter;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter=new ArrayAdapter<String>(this,
                R.layout.array_adapter,
                listItems);
        setListAdapter(adapter);
        ListView lv = getListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView showText = new TextView(MainActivity.this);
                showText.setText(block_hashmap.get(position));
                showText.setTextIsSelectable(true);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(showText).setCancelable(true).show();
                /*
                AlertDialog alertDialog1 = new AlertDialog.Builder(MainActivity.this)
                        .setMessage(block_hashmap.get(position))
                        .create();
                alertDialog1.show();
                */
            }
        });

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

        String ProxyUrl = readProxyUrl();

        mCalendar = Calendar.getInstance();
        LocalVpnService.addOnStatusChangedListener(this);

        //Pre-App Proxy
        if (AppProxyManager.isLollipopOrAbove){
            new AppProxyManager(this);
        }

        infoOperatingIV = (ImageView)findViewById(R.id.infoOperating);
        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.tip);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        String local_ip = getIpAddressString();
        int local_port = 7981;
        String res = initP2PNetwork(local_ip, local_port, "id_1:134.209.43.75:8991");
        if (res.equals("create account address error!")) {
            Log.e(TAG,"init p2p network failed!" + res + ", " + local_ip + ":" + local_port);
        }
        TextView acc_view = (TextView)findViewById(R.id.account_address);
        acc_view.setText(res);
        int p2p_socket = getP2PSocket();
        if (!vpn_service.protect(p2p_socket)) {
            Log.e(TAG,"protect vpn socket failed");
            return;
        }
        createAccount();

        Thread t1 = new Thread(check_tx,"check tx");
        t1.start();
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
    }

    @Override
    protected void onDestroy() {
        LocalVpnService.removeOnStatusChangedListener(this);
        super.onDestroy();
    }

    public static String getIpAddressString() {
        try {
            for (Enumeration<NetworkInterface> enNetI = NetworkInterface
                    .getNetworkInterfaces(); enNetI.hasMoreElements(); ) {
                NetworkInterface netI = enNetI.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = netI
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "0.0.0.0";
    }

    public class CheckTransaction extends ListActivity implements Runnable {
        public List<String> gid_list = new ArrayList<String>();
        private int create_tx_period = 10000;
        public void run() {
            int_tx_hash = transaction(SHA("to", "SHA-256"), 10);
            AddTxGid(int_tx_hash);
            while (true) {
                synchronized (this) {
                    Iterator<String> iterator = gid_list.iterator();
                    while (iterator.hasNext()) {
                        String tx_gid = iterator.next();
                        String res = getTransaction(tx_gid);
                        Log.e(TAG, "check tx runing." + tx_gid + ":" + res);
                        if (!res.equals("NO")) {
                            iterator.remove();
                            Message message = new Message();
                            message.what = COMPLETED;
                            message.obj = res.toString();
                            handler.sendMessage(message);
                        }
                    }
                }

                create_tx_period += 500;
                if (create_tx_period >= 10000) {
                    if (LocalVpnService.IsRunning) {
                        String tx_gix1 = transaction("9650e70834d97b0d4de7bdb8a959045a2c9a5704219c896e3c35d2c88e279bb8", 10);
                        AddTxGid(tx_gix1);
                        Log.e(TAG,"start new tx: " + tx_gix1);
                    }
                    create_tx_period = 0;
                }

                try {
                    Thread.sleep(500);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void AddTxGid(String tx_gid) {
            synchronized (this) {
                gid_list.add(tx_gid);
            }
        }
    }

    private String SHA(final String strText, final String strType)
    {
        // 返回值
        String strResult = null;

        // 是否是有效字符串
        if (strText != null && strText.length() > 0) {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance(strType);
                messageDigest.update(strText.getBytes());
                byte byteBuffer[] = messageDigest.digest();

                StringBuffer strHexString = new StringBuffer();
                for (int i = 0; i < byteBuffer.length; i++) {
                    String hex = Integer.toHexString(0xff & byteBuffer[i]);
                    if (hex.length() == 1) {
                        strHexString.append('0');
                    }
                    strHexString.append(hex);
                }
                strResult = strHexString.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return strResult;
    }

    public native String initP2PNetwork(String ip, int port, String bootstarp);
    public native int getP2PSocket();
    public native String createAccount();
    public native String getTransaction(String tx_gid);
    public native String transaction(String to, int amount);
}

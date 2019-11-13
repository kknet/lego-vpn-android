package com.vm.shadowsocks.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.graphics.Color;
import android.app.ListActivity;
import android.net.VpnService;
import android.widget.AdapterView;
import android.os.Handler;
import android.os.Message;

//import com.google.android.gms.common.api.Status;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.android.gms.wallet.AutoResolveHelper;
//import com.google.android.gms.wallet.IsReadyToPayRequest;
//import com.google.android.gms.wallet.ItemInfo;
//import com.google.android.gms.wallet.PaymentData;
//import com.google.android.gms.wallet.PaymentDataRequest;
//import com.google.android.gms.wallet.PaymentsClient;
//import com.google.android.gms.wallet.PaymentsUtil;
//import com.google.zxing.integration.android.IntentIntegrator;
//import com.google.zxing.integration.android.IntentResult;
import com.vm.shadowsocks.R;
import com.vm.shadowsocks.core.AppProxyManager;
import com.vm.shadowsocks.core.LocalVpnService;
import com.vm.shadowsocks.core.ProxyConfig;
import java.util.Calendar;
import java.util.HashSet;
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

import at.grabner.circleprogress.CircleProgressView;
import at.markushi.ui.CircleButton;

import org.json.JSONObject;
import org.json.JSONArray;

import android.widget.Spinner;

import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import me.shaohui.bottomdialog.BottomDialog;
import androidx.fragment.app.FragmentActivity;
import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import android.app.Dialog;
import androidx.core.content.ContextCompat;
import java.lang.String;
import java.io.File;
import android.widget.Switch;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.RenderPriority;
import android.view.KeyEvent;

import com.suke.widget.SwitchButton;

public class MainActivity extends FragmentActivity  implements
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
    private boolean StartVpnChecked = false;
    private Animation operatingAnim;
    private Vector<String> country_vec = new Vector<String>();
    private String selectCountry = "America";
    private VpnService vpn_service = new VpnService();
    private CheckTransaction check_tx = new CheckTransaction();
    private static final int COMPLETED = 0;
    private static final int GOT_VPN_SERVICE = 1;
    private static final int GOT_TANSACTIONS = 2;
    private static final int GOT_BALANCE = 3;
    private static final int GOT_VPN_ROUTE = 5;
    public static boolean init_p2p_network_succ = false;

    private HashMap<Integer, String> block_hashmap = new HashMap<Integer, String>();
    ArrayList<String> listItems=new ArrayList<String>();
    int list_counter = 0;

    public ArrayList<String> vpn_country_list = new ArrayList<String>();

    private HashMap<String, Vector<String>> country_vpn_map = new HashMap<String, Vector<String>>();
    private HashMap<String, Vector<String>> country_route_map = new HashMap<String, Vector<String>>();
    private HashMap<String, String> country_to_short = new HashMap<String, String>();
    static public HashMap<String, String> default_routing_map = new HashMap<String, String>();

    private CircleProgressView mCircleView;

    private Spinner mSpinner;
    private String[] spinnerTitles;
    private String[] spinnerPopulation;
    private int[] spinnerImages;
    private boolean isUserInteracting;
    private String now_choosed_country = "US";
    private String local_country = "CN";
    private BottomDialog bottom_dialog;
    private BottomDialog upgrade_dialog;
    private BottomDialog webview_dialog;

    private String private_key;
    private String account_address;

    private String transactions_res = new String("");
    private long account_balance = -1;
    private String local_ip = "0.0.0.0";
    public static String choosed_vpn_seckey = "";
    public static String choosed_vpn_url = "";
    private String relay_country = local_country;
    public static boolean use_smart_route = true;
    private final int kDefaultVpnServerPort = 9033;
    private String version_download_url = "";

    private com.suke.widget.SwitchButton switchButton;
    private WebView wv_produce;

    private Button web_view_open_btn;
    private TextView tilte_text_view;

    private long goback_prev_timestamp = 0;
    private String bootstrap = "id_1:120.77.2.117:9001,id:47.105.87.61:9001,id:110.34.181.120:9001,id:98.126.31.159:9001";
    //"id:122.112.234.133:9001",
    private final int kLocalPort = 7891;

    private boolean isExit;

    private final String kCurrentVersion = "2.0.2";
    private int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void InitItemLayout() {
        WindowManager wm1 = this.getWindowManager();
        DisplayMetrics mec = new DisplayMetrics();
        wm1.getDefaultDisplay().getMetrics(mec);
        if (mec.heightPixels > 1900) {
            mCircleView.setY(160);
            CircleButton c_btn = (CircleButton)findViewById(R.id.start_vpn);
            c_btn.setY(160);
        }
    }

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
                    TextView balance = (TextView)findViewById(R.id.balance_lego);
                    balance.setText(split[1] + " Tenon");
                    TextView balance_d = (TextView)findViewById(R.id.balance_dollar);
                    balance_d.setText(String.format("%.2f", Integer.parseInt(split[1]) * 0.002) + "$");
                    String block_item = "\n\n\n    Transaction Hash: \n    " + split[4] + "\n\n    Block Height：" + split[2] + "\n\n    Block Hash：\n    " + split[3] + "\n\n\n";
                    block_hashmap.put(list_counter, block_item);
                    ++list_counter;
                }
            }

            if (msg.what == GOT_VPN_SERVICE) {
                String res = (String)msg.obj;
                String[] c_split = res.split("\t");
                String country = c_split[0];
                String[] split = c_split[1].split(",");
                if (!country_vpn_map.containsKey(country)) {
                    country_vpn_map.put(country, new Vector<String>());
                }

                Vector<String> url_vec = country_vpn_map.get(country);
                for (int i = 0; i < split.length; ++i) {
                    url_vec.add(split[i]);
                    if (url_vec.size() > 16) {
                        url_vec.remove(0);
                    }
                }
            }

            if (msg.what == GOT_VPN_ROUTE) {
                String res = (String)msg.obj;
                String[] c_split = res.split("\t");
                String country = c_split[0];
                String[] split = c_split[1].split(",");
                if (!country_route_map.containsKey(country)) {
                    country_route_map.put(country, new Vector<String>());
                }

                Vector<String> url_vec = country_route_map.get(country);
                for (int i = 0; i < split.length; ++i) {
                    url_vec.add(split[i]);
                    if (url_vec.size() > 16) {
                        url_vec.remove(0);
                    }
                }
            }

            if (msg.what == GOT_TANSACTIONS) {
                String res = (String)msg.obj;
                if (res.isEmpty()) {
                    return;
                }
                transactions_res = res;
            }

            if (msg.what == GOT_BALANCE) {
                long res = (long)msg.obj;
                account_balance = res;
                TextView balance = (TextView)findViewById(R.id.balance_lego);
                balance.setText(account_balance + " Tenon");
                TextView balance_d = (TextView)findViewById(R.id.balance_dollar);
                balance_d.setText(String.format("%.2f", account_balance * 0.002) + "$");
            }
        }
    };

    public void closeWebview(View view) {
        web_view_open_btn.setText(this.getString(R.string.navigation_string));
        tilte_text_view.setText("TenonVPN");
        webview_dialog.dismiss();
    }
    public void hideDialog(View view) {
        bottom_dialog.dismiss();
    }
    public void hideUpgrade(View view) {
        upgrade_dialog.dismiss();
    }
    public void switchSmartRoute(View view) {
        use_smart_route = switchButton.isChecked();
        LocalVpnService.IsRunning = false;
        P2pLibManager.getInstance().use_smart_route = switchButton.isChecked();
    }

    public void homePage(View view) {
        wv_produce.loadUrl("file:///android_asset/index.html");
        wv_produce.clearHistory();
    }

    public void useBrower(View view) {
        String url = wv_produce.getUrl();
        if (url.isEmpty() || url.startsWith("file")) {
            Toast.makeText(this, getString(R.string.select_a_website_string), Toast.LENGTH_SHORT).show();
            return;
        }

        Uri uri = Uri.parse(url);
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setData(uri);
        startActivity(intent);
    }

    void initWebView(final View view) {
        wv_produce=(WebView) view.findViewById(R.id.wv_produce1);
        wv_produce.loadUrl("file:///android_asset/index.html");
        WebSettings webSettings = wv_produce.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wv_produce.getSettings().setSupportZoom(true);
        wv_produce.getSettings().setAllowFileAccess(true);
        wv_produce.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        wv_produce.getSettings().setJavaScriptEnabled(true);
        wv_produce.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        wv_produce.getSettings().setLoadWithOverviewMode(true);
        wv_produce.getSettings().setDefaultTextEncodingName("utf-8");
    }

    private void initView(final View view) {
        transactions_res = getTransactions();
        String[] lines = transactions_res.split(";");
        String[][] DATA_TO_SHOW = new String[lines.length][4];
        for (int i = 0; i < lines.length; ++i) {
            String[] items = lines[i].split(",");
            if (items.length != 4) {
                continue;
            }

            DATA_TO_SHOW[i][0] = items[0];
            DATA_TO_SHOW[i][1] = items[1];
            DATA_TO_SHOW[i][2] = items[2].substring(0, 5).toUpperCase() + "..." + items[2].substring(items[2].length() - 5).toUpperCase();
            DATA_TO_SHOW[i][3] = items[3];
        }
        EditText prikey_text=(EditText)view.findViewById(R.id.dlg_private_key);
        prikey_text.setText(private_key.toUpperCase());
        EditText acc_text=(EditText)view.findViewById(R.id.dlg_account_address);
        acc_text.setText(account_address.toUpperCase());

        TextView balance = (TextView)findViewById(R.id.balance_lego);
        TextView dlg_balance = (TextView)view.findViewById(R.id.dlg_balance_lego);
        dlg_balance.setText(balance.getText());

        TextView balance_d = (TextView)findViewById(R.id.balance_dollar);
        TextView dlg_balance_d = (TextView)view.findViewById(R.id.dlg_balance_dollar);
        dlg_balance_d.setText(balance_d.getText());

        String[] data_header ={"datetime", "type", "account", "amount"};
        final SimpleTableHeaderAdapter simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(MainActivity.this, data_header);
        simpleTableHeaderAdapter.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.white));
        simpleTableHeaderAdapter.setTextSize(14);
        TableView<String[]> tableView = (TableView<String[]>) view.findViewById(R.id.tableView);

        final int rowColorEven = ContextCompat.getColor(MainActivity.this, R.color.env);
        final int rowColorOdd = ContextCompat.getColor(MainActivity.this, R.color.odd);
        tableView.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(rowColorEven, rowColorOdd));

        final TableColumnWeightModel tableColumnWeightModel = new TableColumnWeightModel(4);
        tableColumnWeightModel.setColumnWeight(0, 35);
        tableColumnWeightModel.setColumnWeight(1, 15);
        tableColumnWeightModel.setColumnWeight(2, 30);
        tableColumnWeightModel.setColumnWeight(3, 20);
        tableView.setColumnModel(tableColumnWeightModel);

        tableView.setHeaderAdapter(simpleTableHeaderAdapter);
        if (!transactions_res.isEmpty()) {
            SimpleTableDataAdapter dataAdapter = new SimpleTableDataAdapter(MainActivity.this, DATA_TO_SHOW);
            dataAdapter.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.dark_gray));
            dataAdapter.setTextSize(12);
            tableView.setDataAdapter(dataAdapter);
        }
    }

    public void showWebview(View view) {
        if (LocalVpnService.IsRunning != true) {
            Toast.makeText(this, getString(R.string.connect_first_string), Toast.LENGTH_SHORT).show();
            return;
        }
        webview_dialog.show();
        web_view_open_btn.setText("");
        tilte_text_view.setText("");
    }

    public void hideWebview(View view) {
        web_view_open_btn.setText(getString(R.string.navigation_string));
        tilte_text_view.setText("TenonVPN");
        webview_dialog.dismiss();
    }

    public void webPrev(View view) {
        if (wv_produce.canGoBack()) {
            wv_produce.goBack();
        }
    }

    public void webNext(View view) {
        if (wv_produce.canGoForward()) {
            wv_produce.goForward();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isExit) {
                LocalVpnService.IsRunning = false;
                LocalVpnService.removeOnStatusChangedListener(this);
                p2pDestroy();
                this.finish();
            } else {
                Toast.makeText(this, getString(R.string.exit_check), Toast.LENGTH_SHORT).show();
                isExit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit= false;
                    }
                }, 2000);
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void showDialog(View view) {
        bottom_dialog.show();
    }

    public void checkVer(View view) {
        version_download_url = "";
        String ver = checkVersion();
        if (ver.isEmpty()) {
            Toast.makeText(this, "Already the latest version.", Toast.LENGTH_SHORT).show();
        } else {
            String[] downs = ver.split(",");
            for (int i = 0; i < downs.length; ++i) {
                String[] item = downs[i].split(";");
                if (item.length < 3) {
                    continue;
                }

                if (item[0].equals("android")) {
                    if (item[1].compareTo(kCurrentVersion) <= 0) {
                        Toast.makeText(this, "Already the latest version.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    version_download_url = item[2];
                    break;
                }
            }

            if (version_download_url.isEmpty()) {
                Toast.makeText(this, "Already the latest version...", Toast.LENGTH_SHORT).show();
                return;
            }
            upgrade_dialog.show();
        }
    }

    public void upgradeNow(View view)
    {
        Uri uri = Uri.parse(version_download_url);
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setData(uri);
        startActivity(intent);
        upgrade_dialog.dismiss();
    }

    private void InitSpinner() {
        mSpinner = (Spinner) findViewById(R.id.spinner);
        country_vec.add("America");
        country_vec.add("Brazil");
        country_vec.add("Germany");
        country_vec.add("France");
        country_vec.add("Korea");
        country_vec.add("Netherlands");
        country_vec.add("Canada");
        country_vec.add("Australia");
        country_vec.add("Portugal");
        country_vec.add("Japan");
        country_vec.add("Hong Kong");
        country_vec.add("New Zealand");
        country_vec.add("India");
        country_vec.add("Indonesia");
        country_vec.add("England");
        country_vec.add("China");

        country_to_short.put("Australia", "AU");
        country_to_short.put("Singapore", "SG");
        country_to_short.put("Brazil", "BR");
        country_to_short.put("Germany", "DE");
        country_to_short.put("France", "FR");
        country_to_short.put("Korea", "KR");
        country_to_short.put("Netherlands", "NL");
        country_to_short.put("Canada", "CA");
        country_to_short.put("America", "US");
        country_to_short.put("Portugal", "PT");
        country_to_short.put("Japan", "JP");
        country_to_short.put("Hong Kong", "HK");
        country_to_short.put("New Zealand", "NZ");
        country_to_short.put("India", "IN");
        country_to_short.put("Indonesia", "ID");
        country_to_short.put("England", "GB");
        country_to_short.put("China", "CN");

        for (String value: country_to_short.values()) {
            vpn_country_list.add(value);
        }
        spinnerTitles = new String[]{"America", "Singapore", "Brazil","Germany", "Netherlands","France","Korea", "Japan", "Canada","Australia","Hong Kong", "India", "England", "China"};
        spinnerImages = new int[]{
                R.drawable.us
                , R.drawable.sg
                , R.drawable.br
                , R.drawable.de
                , R.drawable.nl
                , R.drawable.fr
                , R.drawable.kr
                , R.drawable.jp
                , R.drawable.ca
                , R.drawable.au
                , R.drawable.hk
                , R.drawable.in
                , R.drawable.gb
                , R.drawable.cn
        };

        CustomAdapter mCustomAdapter = new CustomAdapter(MainActivity.this, spinnerTitles, spinnerImages);
        mSpinner.setAdapter(mCustomAdapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                now_choosed_country = country_to_short.get(spinnerTitles[i]);
                P2pLibManager.getInstance().choosed_country = country_to_short.get(spinnerTitles[i]);
                LocalVpnService.IsRunning = false;
                InitItemLayout();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public String GetUserPrivateKey() {
        SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        String private_key=sharedPreferences.getString("private_key","");
        return private_key;
    }

    public void SaveUserPrivateKey(String pri_key) {
        SharedPreferences sharedPreferences= getSharedPreferences("data",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("private_key", pri_key);
        editor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        P2pLibManager.getInstance().Init();
        InitSpinner();
        ProxyConfig.Instance.globalMode = true;
        mCircleView = (CircleProgressView) findViewById(R.id.circleView);
        mCircleView.setValue(100);
        mCircleView.setBarColor(getResources().getColor(R.color.disconnect_succ_out));
        mCalendar = Calendar.getInstance();
        LocalVpnService.addOnStatusChangedListener(this);

        web_view_open_btn = findViewById(R.id.web_open_buton);
        tilte_text_view = findViewById(R.id.title_text);
        switchButton = (com.suke.widget.SwitchButton)
                findViewById(R.id.switch_smart_route);
        switchButton.setChecked(true);
        switchButton.setShadowEffect(true);//disable shadow effect
        switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                //TODO do your job
                switchSmartRoute(view);
            }
        });

        //Pre-App Proxy
        if (AppProxyManager.isLollipopOrAbove){
            new AppProxyManager(this);
        }
        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.tip);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        local_ip = getIpAddressString();
        String data_path = this.getFilesDir().getPath();
        Log.e(TAG, "get file path:" + data_path);
        String pri_key = GetUserPrivateKey();
        Log.e("TAG", "get private key: " + pri_key);
        String res = "";
        int try_times = 0;
        for (try_times = 0; try_times < 3; ++try_times) {
            res = initP2PNetwork(local_ip, kLocalPort,
                    bootstrap,
                    data_path,
                    pri_key);
            if (res.equals("create account address error!")) {
                try {
                    Thread.sleep(1000);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            break;
        }

        if (try_times == 5) {
            Toast.makeText(this, getString(R.string.init_failed) , Toast.LENGTH_SHORT).show();
            try {
                Thread.sleep(1000);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
            this.finish();
            return;
        }
        String[] res_split = res.split(",");
        if (res_split.length < 4) {
            Log.e(TAG,"init p2p network failed!" + res + ", " + local_ip + ":" + kLocalPort);
            return;
        }
        Log.d(TAG, "onCreate: start check tx thread. 22222 ");

        local_country = res_split[0];
        account_address = res_split[1];
        private_key = res_split[2];
        if (pri_key.isEmpty()) {
            SaveUserPrivateKey(private_key);
            Log.e("TAG", "save private key: " + private_key);
        }

        String[] default_routing = res_split[3].split(";");
        for (int i = 0; i < default_routing.length; ++i) {
            String[] tmp_item = default_routing[i].split(":");
            if (tmp_item.length < 2) {
                continue;
            }

            if (tmp_item[0].length() != 2 && tmp_item[1].length() != 2) {
                continue;
            }

            default_routing_map.put(tmp_item[0], tmp_item[1]);
        }

        P2pLibManager.getInstance().local_country = local_country;
        Log.e(TAG, "get local country: " + local_country);
        bottom_dialog = BottomDialog.create(getSupportFragmentManager())
                .setViewListener(new BottomDialog.ViewListener() {
                    @Override
                    public void bindView(View v) {
                        initView(v);
                    }
                })
                .setLayoutRes(R.layout.dialog_layout)
                .setDimAmount(0.1f)
                .setCancelOutside(false)
                .setTag("BottomDialog");
        upgrade_dialog = BottomDialog.create(getSupportFragmentManager())
                .setViewListener(new BottomDialog.ViewListener() {
                    @Override
                    public void bindView(View v) {
                    }
                })
                .setLayoutRes(R.layout.upgrade)
                .setDimAmount(0.1f)
                .setCancelOutside(false)
                .setTag("UpgradeDialog");

        webview_dialog = BottomDialog.create(getSupportFragmentManager());

        webview_dialog.setViewListener(new BottomDialog.ViewListener() {
                    @Override
                    public void bindView(View v) {
                        initWebView(v);

                        webview_dialog.getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
                        webview_dialog.getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                                Log.e("Key down", "what's the mater.");
                                if (i == KeyEvent.KEYCODE_BACK) {
                                    long now_timestamp = System.currentTimeMillis();
                                    if (now_timestamp - goback_prev_timestamp <= 200) {
                                        return true;
                                    }

                                    goback_prev_timestamp = now_timestamp;
                                    if (wv_produce.canGoBack()) {
                                        wv_produce.goBack();
                                        return true;
                                    } else {
                                        web_view_open_btn.setText(getString(R.string.navigation_string));
                                        tilte_text_view.setText("TenonVPN");
                                        webview_dialog.dismiss();
                                        return false;
                                    }
                                }
                                //webview_dialog.getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

                                return true;
                            }
                        });
                    }
                })
                .setLayoutRes(R.layout.link_web)
                .setDimAmount(0.1f)
                .setCancelOutside(false)
                .setTag("WebviewDialog");
        int p2p_socket = getP2PSocket();
        if (!vpn_service.protect(p2p_socket)) {
            Log.e(TAG,"protect vpn socket failed");
        }
        createAccount();
        Thread t1 = new Thread(check_tx,"check tx");
        t1.start();
        Log.d(TAG, "onCreate: start check tx thread.");
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
        if (isRunning) {
            c_btn.setColor(getResources().getColor(R.color.connect_succ_in));
            //possiblyShowGooglePayButton();
            mCircleView.stopSpinning();
            mCircleView.setSpinSpeed(10);
            c_btn.setImageDrawable(getResources().getDrawable(R.drawable.connected));
            mCircleView.setValueAnimated(100);
            mCircleView.setBarColor(getResources().getColor(R.color.connect_succ_out));
        } else {
            c_btn.setColor(getResources().getColor(R.color.disconnect_succ_in));
            mCircleView.stopSpinning();
            mCircleView.setBarColor(getResources().getColor(R.color.disconnect_succ_out));
            c_btn.setImageDrawable(getResources().getDrawable(R.drawable.connect));
            mCircleView.setValue(100);
            mCircleView.setSpinSpeed(2);
        }
    }

    public void startVpn(View view) {
        if (LocalVpnService.IsRunning != true) {
            Intent intent = LocalVpnService.prepare(this);
            if (intent == null) {
                startVPNService();
            } else {
                startActivityForResult(intent, START_VPN_SERVICE_REQUEST_CODE);
                startVPNService();
            }
        } else {
            LocalVpnService.IsRunning = false;
        }
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    private String ChooseRouteProxyUrl(String dest_country) {
        String route_proxy_url = new String();
        if (country_route_map.containsKey(dest_country)) {
            Vector<String> vpn_url_vec = country_route_map.get(dest_country);
            int rand_num = (int)(Math.random() * vpn_url_vec.size());
            String[] item_split = vpn_url_vec.get(rand_num).split(":");
            if (item_split.length >= 6) {
                route_proxy_url = "ss://aes-128-cfb:passwd@" + item_split[0] + ":" + item_split[2];
                relay_country = dest_country;
            }
        }

        if (route_proxy_url.isEmpty()) {
            if (country_route_map.containsKey(now_choosed_country)) {
                Vector<String> vpn_url_vec = country_route_map.get(now_choosed_country);
                int rand_num = (int)(Math.random() * vpn_url_vec.size());
                String[] item_split = vpn_url_vec.get(rand_num).split(":");
                if (item_split.length >= 6) {
                    route_proxy_url = "ss://aes-128-cfb:passwd@" + item_split[0] + ":" + item_split[2];
                    relay_country = now_choosed_country;
                }
            }
        }

        if (route_proxy_url.isEmpty()) {
            for (String key : country_route_map.keySet()) {
                Vector<String> vpn_url_vec = country_route_map.get(key);
                if (vpn_url_vec.size() > 0) {
                    int rand_num = (int)(Math.random() * vpn_url_vec.size());
                    String[] item_split = vpn_url_vec.get(rand_num).split(":");
                    if (item_split.length >= 6) {
                        route_proxy_url = "ss://aes-128-cfb:passwd@" + item_split[0] + ":" + item_split[2];
                        relay_country = key;
                        break;
                    }
                }
            }
        }
        return route_proxy_url;
    }

    private String ChooseVpnProxyUrl() {
        String vpn_proxy_url = new String();
        if (country_vpn_map.containsKey(now_choosed_country)) {
            Vector<String> vpn_url_vec = country_vpn_map.get(now_choosed_country);
            Vector<String> new_url_vec = new Vector<String>();
            for (String item: vpn_url_vec) {
                String[] item_split = item.split(":");
                if (item_split.length >= 6) {
                    if (Integer.parseInt(item_split[1]) != kDefaultVpnServerPort) {
                        new_url_vec.add(item);
                    }
                }
            }

            String item_string = "";
            if (!new_url_vec.isEmpty()) {
                int rand_num = (int)(Math.random() * new_url_vec.size());
                item_string = new_url_vec.get(rand_num);
                Log.e(TAG, "tt get vpn nodes: " + item_string);

            }

            if (item_string.isEmpty()) {
                int rand_num = (int)(Math.random() * vpn_url_vec.size());
                item_string = vpn_url_vec.get(rand_num);
            }

            if (!item_string.isEmpty()) {
                vpn_proxy_url = item_string;
            }
        }

        if (vpn_proxy_url.isEmpty()) {
            for (String key : country_vpn_map.keySet()) {
                if (key == local_country) {
                    continue;
                }

                Vector<String> vpn_url_vec = country_vpn_map.get(key);
                if (vpn_url_vec.size() > 0) {
                    Vector<String> new_url_vec = new Vector<String>();
                    for (String item: vpn_url_vec) {
                        String[] item_split = item.split(":");
                        if (item_split.length >= 6) {
                            if (Integer.parseInt(item_split[2]) != kDefaultVpnServerPort) {
                                new_url_vec.add(item);
                            }
                        }
                    }

                    String item_string = "";
                    if (!new_url_vec.isEmpty()) {
                        int rand_num = (int)(Math.random() * new_url_vec.size());
                        item_string = new_url_vec.get(rand_num);
                    }

                    if (item_string.isEmpty()) {
                        int rand_num = (int)(Math.random() * vpn_url_vec.size());
                        item_string = vpn_url_vec.get(rand_num);
                    }

                    if (!item_string.isEmpty()) {
                        vpn_proxy_url = item_string;
                    }
                }
            }
        }
        return vpn_proxy_url;
    }

    private void startVPNService() {
        String route_proxy_url = "";
        if (use_smart_route) {
            route_proxy_url = ChooseRouteProxyUrl(local_country);
            if (!isValidUrl(route_proxy_url)) {
                Toast.makeText(this, "Waiting Decentralized Routing...", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String vpn_proxy_url = ChooseVpnProxyUrl();
        Log.e(TAG, "vpn proxy url: " + vpn_proxy_url);
        if (vpn_proxy_url.isEmpty()) {
            Toast.makeText(this, "Waiting Decentralized Vpn Server...", Toast.LENGTH_SHORT).show();
            return;
        }
        String[] item_split = vpn_proxy_url.split(":");
        if (item_split.length < 7) {
            Toast.makeText(this, "Waiting Decentralized Vpn Server...", Toast.LENGTH_SHORT).show();
            return;
        }
        String direct_vpn_proxy_url = "ss://aes-128-cfb:passwd@" + item_split[0] + ":" + item_split[1];
        //String login_gid = vpnLogin(item_split[6]);
        //Log.e(TAG, "join account address:" + item_split[6]);


        boolean res = P2pLibManager.getInstance().GetVpnNode();
        if(!res) {
            Toast.makeText(this, "Waiting Decentralized Vpn Server...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (use_smart_route) {
            //Toast.makeText(this, " to " + direct_vpn_proxy_url +  " use " + route_proxy_url, Toast.LENGTH_SHORT).show();
            choosed_vpn_url = vpn_proxy_url;
            LocalVpnService.ProxyUrl = route_proxy_url;
            Log.e(TAG, " to " + vpn_proxy_url +  " use " + route_proxy_url);
        } else {
            //Toast.makeText(this, "local " + local_country + " direct to " + now_choosed_country + ", " + direct_vpn_proxy_url, Toast.LENGTH_SHORT).show();
            choosed_vpn_url = vpn_proxy_url;
            LocalVpnService.ProxyUrl = direct_vpn_proxy_url;
        }
        startService(new Intent(this, LocalVpnService.class));
        CircleButton c_btn = (CircleButton)findViewById(R.id.start_vpn);
        c_btn.setEnabled(false);
        LocalVpnService.IsRunning = true;
        mCircleView.spin();
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
        LocalVpnService.IsRunning = false;
        LocalVpnService.removeOnStatusChangedListener(this);
        p2pDestroy();
        super.onDestroy();
    }

    public static String getIpAddressString() {
        return "0.0.0.0";
    }

    public class CheckTransaction extends ListActivity implements Runnable {
        public List<String> gid_list = new ArrayList<String>();
        private ArrayList<String> not_get_country_list = new ArrayList<String>();
        private int check_vip_times = 0;
        private int bandwidth_used = 0;
        public void run() {
            for (String value: country_to_short.values()) {
                not_get_country_list.add(value);
            }

            while (true) {
                for (int i = 0; i < not_get_country_list.size(); ++i) {
                    String vpn_url = getVpnNodes(not_get_country_list.get(i));
                    if (!vpn_url.isEmpty()) {
                        vpn_url = not_get_country_list.get(i) + "\t" + vpn_url;
                        Message message = new Message();
                        message.what = GOT_VPN_SERVICE;
                        message.obj = vpn_url;
                        handler.sendMessage(message);
                    }

                    String route_url = getRouteNodes(not_get_country_list.get(i));
                    if (!route_url.isEmpty()) {
                        route_url = not_get_country_list.get(i) + "\t" + route_url;
                        Message message = new Message();
                        message.what = GOT_VPN_ROUTE;
                        message.obj = route_url;
                        handler.sendMessage(message);
                    }
                }

                {
                    long now_balance = getBalance();
                    P2pLibManager.getInstance().SetBalance(now_balance);
                    if (now_balance != -1) {
                        Message message = new Message();
                        message.what = GOT_BALANCE;
                        message.obj = now_balance;
                        handler.sendMessage(message);
                    }
                }

                vpn_service.protect(getP2PSocket());
                if (check_vip_times < 10) {
                    long tm = Long.parseLong(P2pLibManager.checkVip());
                    if (P2pLibManager.getInstance().payfor_timestamp == 0 || tm != Long.MAX_VALUE) {
                        P2pLibManager.getInstance().payfor_timestamp = tm;
                    }
                    check_vip_times++;
                } else {
                    P2pLibManager.getInstance().PayforVpn();
                }

                try {
                    Thread.sleep(2000);
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

    public native String initP2PNetwork(String ip, int port, String bootstarp, String file_path, String pri_key);
    public native int getP2PSocket();
    public native String createAccount();
    public native String getVpnNodes(String country);
    public native String getRouteNodes(String country);
    public native String getTransactions();
    public native long getBalance();
    public native String checkVersion();
    public native void p2pDestroy();

}

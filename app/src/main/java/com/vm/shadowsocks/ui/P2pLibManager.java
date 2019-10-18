package com.vm.shadowsocks.ui;

import android.os.Message;
import android.util.Log;

import com.vm.shadowsocks.tunnel.shadowsocks.CryptFactory;
import com.vm.shadowsocks.tunnel.shadowsocks.ICrypt;
import com.vm.shadowsocks.tunnel.shadowsocks.ShadowsocksConfig;

import java.util.Vector;

public final class P2pLibManager {
    public String local_country = "";
    public String choosed_country = "US";
    public boolean use_smart_route = true;

    public ICrypt encryptor;
    public String choosed_vpn_ip;
    public int choosed_vpn_port;
    public String choosed_method = "aes-128-cfb";
    public String public_key;


    private String countries[] = {"US", "SG", "BR","DE","FR","KR", "JP", "CA","AU","HK", "IN", "GB","CN"};
    private String def_vpn_coutry[] = {"US", "IN", "GB"};
    private String def_route_coutry[] = {"US", "IN", "GB"};

    private P2pLibManager() {
    }

    public static P2pLibManager getInstance() {
        return StaticSingletonHolder.instance;
    }

    private static class StaticSingletonHolder {
        private static final P2pLibManager instance = new P2pLibManager();
    }

    public String GetRemoteServer() {
        if (use_smart_route) {
            return GetRouteNode();
        } else {
            return choosed_vpn_ip + ":" + choosed_vpn_port;
        }
    }

    public boolean GetVpnNode() {
        String res = GetOneVpnNode(choosed_country);
        if (res.isEmpty()) {
            for (String country : def_vpn_coutry) {
                res = GetOneVpnNode(choosed_country);
                if (!res.isEmpty()) {
                    break;
                }
            }
        }

        if (res.isEmpty()) {
            return false;
        }

        String info_split[] = res.split(":");
        if (info_split.length < 7) {
            return false;
        }

        choosed_vpn_ip = info_split[0];
        choosed_vpn_port = Integer.parseInt(info_split[1]);
        encryptor = CryptFactory.get(choosed_method, info_split[3]);
        Log.e("test encryptor", "encryptor is not null: " + encryptor + ", seckey is: " + info_split[3]);
        public_key = getPublicKey();
        return true;
    }

    public String GetRouteNode() {
        String res = GetOneRouteNode(local_country);
        if (!res.isEmpty()) {
            return res;
        }

        for (String country: def_route_coutry) {
            res = GetOneRouteNode(choosed_country);
            if (!res.isEmpty()) {
                return res;
            }
        }
        return "";
    }

    public String GetOneVpnNode(String country) {
        String vpn_url = getVpnNodes(country);
        if (vpn_url.isEmpty()) {
            return "";
        }

        String[] split = vpn_url.split(",");
        int rand_num = (int)(Math.random() * split.length);
        String[] item_split = split[rand_num].split(":");
        if (item_split.length >= 6) {
            return split[rand_num];
        }

        return "";
    }

    public String GetOneRouteNode(String country) {
        String route_url = getRouteNodes(country);
        if (route_url.isEmpty()) {
            return "";
        }

        String[] split = route_url.split(",");
        int rand_num = (int)(Math.random() * split.length);
        String[] item_split = split[rand_num].split(":");
        if (item_split.length >= 6) {
            return item_split[0] + ":" + item_split[2];
        }
        return "";
    }

    public native String getVpnNodes(String country);
    public native String getRouteNodes(String country);
    public static native String getPublicKey();
}

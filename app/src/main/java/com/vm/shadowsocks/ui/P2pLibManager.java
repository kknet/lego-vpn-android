package com.vm.shadowsocks.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashSet;
import java.util.Vector;

public final class P2pLibManager {
    public String local_country = "";
    public String choosed_country = "US";
    public boolean use_smart_route = true;
    public String choosed_vpn_ip;
    public int choosed_vpn_port;
    public String choosed_method = "aes-128-cfb";
    public String platfrom = "and";
    public String private_key;
    public String public_key;
    public String account_id;
    public String seckey;
    public HashSet<String> payfor_vpn_accounts = new HashSet<String>();
    public Vector<String> payfor_vpn_accounts_arr = new Vector<String>();
    public int vip_level = 0;
    public int free_used_bandwidth = 0;
    public long payfor_timestamp = 0;
    public String now_status = "ok";
    public long vip_left_days = -1;

    private String countries[] = {"US", "SG", "BR","DE","FR","KR", "JP", "CA","AU","HK", "IN", "GB","CN"};
    private String def_vpn_coutry[] = {"US", "IN", "GB"};
    private String def_route_coutry[] = {"US", "IN", "GB"};
    public long min_payfor_vpn_tenon = 2000;
    public long now_balance = -1;
    private String payfor_gid = "";

    public void Init() {
        InitPayforAccounts();
    }
    public void InitResetPrivateKey() {
        vip_level = 0;
        free_used_bandwidth = 0;
        payfor_timestamp = 0;
        now_status = "ok";
        vip_left_days = -1;
        now_balance = -1;
        payfor_gid = "";
        GetVpnNode();
    }

    public void SetBalance(long balance) {
        now_balance = balance;
    }

    public void PayforVpn() {
        long day_msec = 3600 * 1000 * 24;
        long days_timestamp = payfor_timestamp / day_msec;
        long cur_timestamp = System.currentTimeMillis();
        long days_cur = cur_timestamp / day_msec;
        if (payfor_timestamp != Long.MAX_VALUE && days_timestamp + 30 >= days_cur) {
            payfor_gid = "";
            vip_level = 1;
            vip_left_days = (days_timestamp + 30 - days_cur) + (now_balance / min_payfor_vpn_tenon) * 30;
            return;
        } else {
            if (payfor_gid.isEmpty() && payfor_timestamp != 0) {
                if (now_balance >= min_payfor_vpn_tenon) {
                    PayforVipTrans();
                }
            }
        }

        if (!payfor_gid.isEmpty()) {
            payfor_timestamp = Long.parseLong(P2pLibManager.checkVip());
            Log.e("payfor vpn check", "" + payfor_timestamp);
        }
    }

    private void PayforVipTrans() {
        int rand_num = (int)(Math.random() * payfor_vpn_accounts_arr.size());
        String acc = payfor_vpn_accounts_arr.get(rand_num);
        if (acc.isEmpty()) {
            return;
        }
        payfor_gid = payforVpn(acc, min_payfor_vpn_tenon, payfor_gid);
        Log.e("payfor vpn and get gid", payfor_gid);
    }

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
                res = GetOneVpnNode(country);
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
        seckey = info_split[3];
        public_key = getPublicKey();
        return true;
    }

    public String GetRouteNode() {
        String routing_country = local_country;
        if (MainActivity.default_routing_map.containsKey(local_country)) {
            routing_country = MainActivity.default_routing_map.get(local_country);
        }

        String res = GetOneRouteNode(routing_country);
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

    public boolean ResetPrivateKey(String prikey) {
        String res = resetPrivateKey(prikey);
        String[] item_split = res.split(",");
        if (item_split.length != 2) {
            return false;
        }

        private_key = prikey;
        public_key = item_split[0];
        account_id = item_split[1];
        return true;
    }

    private void InitPayforAccounts() {
        payfor_vpn_accounts.add("dc161d9ab9cd5a031d6c5de29c26247b6fde6eb36ed3963c446c1a993a088262");
        payfor_vpn_accounts.add("5595b040cdd20984a3ad3805e07bad73d7bf2c31e4dc4b0a34bc781f53c3dff7");
        payfor_vpn_accounts.add("25530e0f5a561f759a8eb8c2aeba957303a8bb53a54da913ca25e6aa00d4c365");
        payfor_vpn_accounts.add("9eb2f3bd5a78a1e7275142d2eaef31e90eae47908de356781c98771ef1a90cd2");
        payfor_vpn_accounts.add("c110df93b305ce23057590229b5dd2f966620acd50ad155d213b4c9db83c1f36");
        payfor_vpn_accounts.add("f64e0d4feebb5283e79a1dfee640a276420a08ce6a8fbef5572e616e24c2cf18");
        payfor_vpn_accounts.add("7ff017f63dc70770fcfe7b336c979c7fc6164e9653f32879e55fcead90ddf13f");
        payfor_vpn_accounts.add("6dce73798afdbaac6b94b79014b15dcc6806cb693cf403098d8819ac362fa237");
        payfor_vpn_accounts.add("b5be6f0090e4f5d40458258ed9adf843324c0327145c48b55091f33673d2d5a4");

        for (String acc : payfor_vpn_accounts) {
            payfor_vpn_accounts_arr.add(acc);
        }
    }

    public native String getVpnNodes(String country);
    public native String getRouteNodes(String country);
    public static native String getPublicKey();
    public static native String payforVpn(String to, long tenon, String gid);
    public static native String checkVip();
    public static native String checkFreeBandwidth();
    public static native String resetPrivateKey(String prikey);
}

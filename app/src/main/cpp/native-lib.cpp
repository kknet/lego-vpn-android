#include <jni.h>
#include <string>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>
#include <android/log.h>
#include <vpn_client.h>

#define LOG_TAG  "NativeLog"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

#include "vpn_client.h"

#ifdef __cplusplus
extern "C"{
#endif


JNIEXPORT jstring JNICALL Java_com_vm_shadowsocks_ui_MainActivity_initP2PNetwork(
        JNIEnv *env,
        jobject,
        jstring ip,
        jint port,
        jstring bootstrap,
        jstring file_path) {
    jboolean iscopy;
    const char *in_ip = env->GetStringUTFChars(ip, &iscopy);
    const char *in_bootstrap = env->GetStringUTFChars(bootstrap, &iscopy);
    //const char *in_path = "data/data/com.vm.legovpn";
    const char *in_path = env->GetStringUTFChars(file_path, &iscopy);

    std::string conf_file = std::string(in_path) + "/lego.conf";
    std::string log_file = std::string(in_path) + "/lego.log";
    std::string conf_log_file = std::string(in_path) + "/log4cpp.properties";
    std::string res = lego::client::VpnClient::Instance()->Init(
            in_ip,
            port,
            in_bootstrap,
            conf_file,
            log_file,
            conf_log_file);
    if (res == "ERROR") {
        res = "create account address error!";
    }
    return env->NewStringUTF(res.c_str());
}

JNIEXPORT jstring JNICALL Java_com_vm_shadowsocks_ui_MainActivity_getRouteNodes(
        JNIEnv *env,
        jobject,
        jstring country) {
    std::vector<lego::client::VpnServerNodePtr> nodes;
    jboolean iscopy;
    const char *in_country = env->GetStringUTFChars(country, &iscopy);
    lego::client::VpnClient::Instance()->GetVpnServerNodes(in_country, 16, true, nodes);
    std::string vpn_svr = "";
    for (uint32_t i = 0; i < nodes.size(); ++i) {
        vpn_svr += nodes[i]->ip + ":";
        vpn_svr += std::to_string(nodes[i]->svr_port) + ":";
        vpn_svr += std::to_string(nodes[i]->route_port) + ":";
        vpn_svr += nodes[i]->seckey + ":";
        vpn_svr += nodes[i]->pubkey + ":";
        vpn_svr += nodes[i]->dht_key + ":";
        vpn_svr += nodes[i]->acccount_id + ":";
        if (i != nodes.size() - 1) {
            vpn_svr += ",";
        }
    }
    return env->NewStringUTF(vpn_svr.c_str());
}

JNIEXPORT jstring JNICALL Java_com_vm_shadowsocks_ui_MainActivity_getVpnNodes(
        JNIEnv *env,
        jobject,
        jstring country) {
    std::vector<lego::client::VpnServerNodePtr> nodes;
    jboolean iscopy;
    const char *in_country = env->GetStringUTFChars(country, &iscopy);
    lego::client::VpnClient::Instance()->GetVpnServerNodes(in_country, 16, false, nodes);
    std::string vpn_svr = "";
    for (uint32_t i = 0; i < nodes.size(); ++i) {
        vpn_svr += nodes[i]->ip + ":";
        vpn_svr += std::to_string(nodes[i]->svr_port) + ":";
        vpn_svr += std::to_string(nodes[i]->route_port) + ":";
        vpn_svr += nodes[i]->seckey + ":";
        vpn_svr += nodes[i]->pubkey + ":";
        vpn_svr += nodes[i]->dht_key + ":";
        vpn_svr += nodes[i]->acccount_id + ":";
        if (i != nodes.size() - 1) {
            vpn_svr += ",";
        }
    }
    return env->NewStringUTF(vpn_svr.c_str());
}


JNIEXPORT jstring JNICALL Java_com_vm_shadowsocks_ui_P2pLibManager_getRouteNodes(
        JNIEnv *env,
        jobject,
        jstring country) {
    std::vector<lego::client::VpnServerNodePtr> nodes;
    jboolean iscopy;
    const char *in_country = env->GetStringUTFChars(country, &iscopy);
    lego::client::VpnClient::Instance()->GetVpnServerNodes(in_country, 16, true, nodes);
    std::string vpn_svr = "";
    for (uint32_t i = 0; i < nodes.size(); ++i) {
        vpn_svr += nodes[i]->ip + ":";
        vpn_svr += std::to_string(nodes[i]->svr_port) + ":";
        vpn_svr += std::to_string(nodes[i]->route_port) + ":";
        vpn_svr += nodes[i]->seckey + ":";
        vpn_svr += nodes[i]->pubkey + ":";
        vpn_svr += nodes[i]->dht_key + ":";
        vpn_svr += nodes[i]->acccount_id + ":";
        if (i != nodes.size() - 1) {
            vpn_svr += ",";
        }
    }
    return env->NewStringUTF(vpn_svr.c_str());
}

JNIEXPORT jstring JNICALL Java_com_vm_shadowsocks_ui_P2pLibManager_getVpnNodes(
        JNIEnv *env,
        jobject,
        jstring country) {
    std::vector<lego::client::VpnServerNodePtr> nodes;
    jboolean iscopy;
    const char *in_country = env->GetStringUTFChars(country, &iscopy);
    lego::client::VpnClient::Instance()->GetVpnServerNodes(in_country, 16, false, nodes);
    std::string vpn_svr = "";
    for (uint32_t i = 0; i < nodes.size(); ++i) {
        vpn_svr += nodes[i]->ip + ":";
        vpn_svr += std::to_string(nodes[i]->svr_port) + ":";
        vpn_svr += std::to_string(nodes[i]->route_port) + ":";
        vpn_svr += nodes[i]->seckey + ":";
        vpn_svr += nodes[i]->pubkey + ":";
        vpn_svr += nodes[i]->dht_key + ":";
        vpn_svr += nodes[i]->acccount_id + ":";
        if (i != nodes.size() - 1) {
            vpn_svr += ",";
        }
    }
    return env->NewStringUTF(vpn_svr.c_str());
}

JNIEXPORT jstring JNICALL Java_com_vm_shadowsocks_ui_P2pLibManager_getPublicKey(
        JNIEnv *env,
        jobject) {
    std::string res = lego::client::VpnClient::Instance()->GetPublicKey();
    return env->NewStringUTF(res.c_str());
}

JNIEXPORT jint JNICALL Java_com_vm_shadowsocks_ui_MainActivity_getP2PSocket(
        JNIEnv *env,
        jobject /* this */) {
    return lego::client::VpnClient::Instance()->GetSocket();
}

JNIEXPORT jboolean JNICALL Java_com_vm_shadowsocks_ui_MainActivity_isFirstTimeInstall(
        JNIEnv *env,
        jobject /* this */) {
    return lego::client::VpnClient::Instance()->IsFirstInstall();
}

JNIEXPORT jstring JNICALL Java_com_vm_shadowsocks_ui_MainActivity_getTransactions(
        JNIEnv *env,
        jobject /* this */) {
    std::string res = lego::client::VpnClient::Instance()->Transactions(0, 64);
    return env->NewStringUTF(res.c_str());
}

JNIEXPORT jlong JNICALL Java_com_vm_shadowsocks_ui_MainActivity_getBalance(
        JNIEnv *env,
        jobject /* this */) {
    return lego::client::VpnClient::Instance()->GetBalance();
}

JNIEXPORT void JNICALL Java_com_vm_shadowsocks_ui_MainActivity_setFirstTimeInstall(
        JNIEnv *env,
        jobject /* this */) {
    lego::client::VpnClient::Instance()->SetFirstInstall();
}

JNIEXPORT void JNICALL Java_com_vm_shadowsocks_ui_MainActivity_vpnNodeHeartbeat(
        JNIEnv *env,
        jobject,
        jstring dht_key) {
    jboolean iscopy;
    const char *tmp_dht_key = env->GetStringUTFChars(dht_key, &iscopy);
    lego::client::VpnClient::Instance()->VpnHeartbeat(tmp_dht_key);
}

JNIEXPORT jstring JNICALL Java_com_vm_shadowsocks_ui_MainActivity_createAccount(
        JNIEnv *env,
        jobject /* this */) {
    std::string tx_gid;
    lego::client::VpnClient::Instance()->Transaction("", 0, tx_gid);
    return env->NewStringUTF("");
}

JNIEXPORT jstring JNICALL Java_com_vm_shadowsocks_ui_MainActivity_transaction(
        JNIEnv *env,
        jobject,
        jstring to,
        jint amount) {
    jboolean iscopy;
    const char *to_str = env->GetStringUTFChars(to, &iscopy);
    std::string tx_gid;
    lego::client::VpnClient::Instance()->Transaction(to_str, amount, tx_gid);
    return env->NewStringUTF(tx_gid.c_str());
}

JNIEXPORT jstring JNICALL Java_com_vm_shadowsocks_ui_MainActivity_getTransaction(
        JNIEnv *env,
        jobject,
        jstring tx_gid) {
    jboolean iscopy;
    const char *tx_gid_str = env->GetStringUTFChars(tx_gid, &iscopy);
    std::string tx_info_str = lego::client::VpnClient::Instance()->GetTransactionInfo(tx_gid_str);
    if (tx_info_str.empty()) {
        return env->NewStringUTF("NO");
    }
    time_t now = time(0);
    tm *ltm = localtime(&now);
    std::string res_str = std::to_string(1900 + ltm->tm_year) + "-" +
                std::to_string(1 + ltm->tm_mon) + "-" +
                std::to_string(ltm->tm_mday) + " " +
                std::to_string(ltm->tm_hour) + ":" +
                std::to_string(ltm->tm_min) + ":" +
                std::to_string(ltm->tm_sec) + " " + tx_info_str;
    return env->NewStringUTF(res_str.c_str());
}

JNIEXPORT jint JNICALL Java_com_vm_shadowsocks_ui_MainActivity_resetTransport(
        JNIEnv *env,
        jobject,
        jstring local_ip,
        jint local_port) {
    jboolean iscopy;
    const char *tmp_ip = env->GetStringUTFChars(local_ip, &iscopy);
    return lego::client::VpnClient::Instance()->ResetTransport(tmp_ip, local_port);
}

JNIEXPORT jstring JNICALL Java_com_vm_shadowsocks_ui_MainActivity_getPublicKey(
        JNIEnv *env,
        jobject) {
    std::string res = lego::client::VpnClient::Instance()->GetPublicKey();
    return env->NewStringUTF(res.c_str());
}

JNIEXPORT jstring JNICALL Java_com_vm_shadowsocks_ui_MainActivity_vpnLogin(
        JNIEnv *env,
        jobject,
        jstring server_id) {
    jboolean iscopy;
    const char *svr_acc = env->GetStringUTFChars(server_id, &iscopy);
    std::vector<std::string> route_vec;
    std::string gid;
    lego::client::VpnClient::Instance()->VpnLogin(svr_acc, route_vec, gid);
    return env->NewStringUTF(gid.c_str());
}

JNIEXPORT jstring JNICALL Java_com_vm_shadowsocks_ui_MainActivity_checkVersion(
        JNIEnv *env,
        jobject) {
    std::string res = lego::client::VpnClient::Instance()->CheckVersion();
    return env->NewStringUTF(res.c_str());
}

#ifdef __cplusplus
}
#endif
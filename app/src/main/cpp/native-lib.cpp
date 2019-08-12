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
        jstring bootstrap) {
    jboolean iscopy;
    const char *in_ip = env->GetStringUTFChars(ip, &iscopy);
    const char *in_bootstrap = env->GetStringUTFChars(bootstrap, &iscopy);
    std::string res = lego::client::VpnClient::Instance()->Init(in_ip, port, in_bootstrap);
    if (res == "ERROR") {
        res = "create account address error!";
    }
    return env->NewStringUTF(res.c_str());
}

JNIEXPORT jint JNICALL Java_com_vm_shadowsocks_ui_MainActivity_getP2PSocket(
        JNIEnv *env,
        jobject /* this */) {
    return lego::client::VpnClient::Instance()->GetSocket();
}

JNIEXPORT jstring JNICALL Java_com_vm_shadowsocks_ui_MainActivity_createAccount(
        JNIEnv *env,
        jobject /* this */) {
    std::string tx_gid;
    lego::client::VpnClient::Instance()->Transaction("", 0, tx_gid);
    return env->NewStringUTF(tx_gid.c_str());
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
        LOGD("get tx info error[%s]", tx_gid_str);
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
    LOGD("get tx info succ[%s]", tx_gid_str);
    return env->NewStringUTF(res_str.c_str());
}

#ifdef __cplusplus
}
#endif
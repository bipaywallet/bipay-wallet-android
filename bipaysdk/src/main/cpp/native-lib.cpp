#include <jni.h>
#include <string>
#include <include/bipaydll.h>
#include <malloc.h>
#include <string.h>


char *jstringToChar(JNIEnv *env, jstring jstr) {
    char *rtn = NULL;
    jclass clsstring = env->FindClass("java/lang/String");
    jstring strencode = env->NewStringUTF("UTF8");
    jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray barr = (jbyteArray) env->CallObjectMethod(jstr, mid, strencode);
    jsize alen = env->GetArrayLength(barr);
    jbyte *ba = env->GetByteArrayElements(barr, JNI_FALSE);
    if (alen > 0) {
        rtn = (char *) malloc(alen + 1);
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    env->ReleaseByteArrayElements(barr, ba, 0);
    return rtn;
}

extern "C"
JNIEXPORT jstring
JNICALL
Java_com_spark_bipaysdk_jni_JNIUtil_getMnemonic(
        JNIEnv *env,
        jobject obj,
        jint la) {

    auto key = GetMnemonic(la);

    if (key == NULL) {
        return NULL;
    } else {
        return env->NewStringUTF(key);
    }
}

extern "C"
JNIEXPORT jstring
JNICALL
Java_com_spark_bipaysdk_jni_JNIUtil_getSeedWithMnemonic(
        JNIEnv *env,
        jobject obj,
        jstring str) {

    char *pStr = jstringToChar(env, str);
    auto key = GetSeed(pStr);

    if (key == NULL) {
        return NULL;
    } else {
        return env->NewStringUTF(key);
    }
}

extern "C"
JNIEXPORT jstring
JNICALL
Java_com_spark_bipaysdk_jni_JNIUtil_getMasterKey(
        JNIEnv *env,
        jobject obj,
        jstring str) {

    char *pStr = jstringToChar(env, str);
    auto key = GetMasterKey(pStr);

    if (key == NULL) {
        return NULL;
    } else {
        return env->NewStringUTF(key);
    }
}

extern "C"
JNIEXPORT jstring
JNICALL
Java_com_spark_bipaysdk_jni_JNIUtil_getSignaturePrivKey(
        JNIEnv *env,
        jobject obj,
        jstring str,
        jint coinType,
        jint prefix) {

    char *pStr = jstringToChar(env, str);
    auto key = GetExportedPrivKey(pStr, coinType, prefix);

    if (key == NULL) {
        return NULL;
    } else {
        return env->NewStringUTF(key);
    }
}

extern "C"
JNIEXPORT jstring
JNICALL
Java_com_spark_bipaysdk_jni_JNIUtil_getExPrivKey(
        JNIEnv *env,
        jobject obj,
        jstring str,
        jint coinType) {

    char *pStr = jstringToChar(env, str);
    auto key = GetCoinMasterKey(pStr, coinType);

    if (key == NULL) {
        return NULL;
    } else {
        return env->NewStringUTF(key);
    }
}

extern "C"
JNIEXPORT jstring
JNICALL
Java_com_spark_bipaysdk_jni_JNIUtil_getCoinAddress(
        JNIEnv *env,
        jobject obj,
        jstring str,
        jint coinType,
        jint prefix) {

    char *pStr = jstringToChar(env, str);
    auto key = GetAddressUsePrivkey(pStr, coinType, prefix);

    if (key == NULL) {
        return NULL;
    } else {
        return env->NewStringUTF(key);
    }
}

extern "C"
JNIEXPORT jstring
JNICALL
Java_com_spark_bipaysdk_jni_JNIUtil_getSubPrivKey(
        JNIEnv *env,
        jobject obj,
        jstring str,
        jlong index) {

    char *pStr = jstringToChar(env, str);
    auto key = GetSubPrivKey(pStr, index);

    if (key == NULL) {
        return NULL;
    } else {
        return env->NewStringUTF(key);
    }
}

extern "C"
JNIEXPORT jstring
JNICALL
Java_com_spark_bipaysdk_jni_JNIUtil_getSupportedCoins(
        JNIEnv *env,
        jobject obj) {

    auto key = GetSupportedCoins();

    if (key == NULL) {
        return NULL;
    } else {
        return env->NewStringUTF(key);
    }
}

extern "C"
JNIEXPORT jstring
JNICALL
Java_com_spark_bipaysdk_jni_JNIUtil_createNewTransaction(
        JNIEnv *env,
        jobject obj,
        jstring str,
        jint coinType) {

    char *pStr = jstringToChar(env, str);
    auto key = NewTransaction(pStr, coinType);

    if (key == NULL) {
        return NULL;
    } else {
        return env->NewStringUTF(key);
    }
}

extern "C"
JNIEXPORT jstring
JNICALL
Java_com_spark_bipaysdk_jni_JNIUtil_signatureForTransfer(
        JNIEnv *env,
        jobject obj,
        jstring str,
        jstring privkeys,
        jint coinType) {

    char *pStr = jstringToChar(env, str);
    char *privkeysStr = jstringToChar(env, privkeys);
    auto key = Signature(pStr, privkeysStr, coinType);

    if (key == NULL) {
        return NULL;
    } else {
        return env->NewStringUTF(key);
    }
}

extern "C"
JNIEXPORT jstring
JNICALL
Java_com_spark_bipaysdk_jni_JNIUtil_SignSignatureForTransfer(
        JNIEnv *env,
        jobject obj,
        jstring str,
        jstring privkeys,
        jint coinType,
        jstring reserved) {

    char *pStr = jstringToChar(env, str);
    char *privkeysStr = jstringToChar(env, privkeys);
    char *pReserved = jstringToChar(env, reserved);
    auto key = SignSignature(pStr, privkeysStr, coinType, pReserved);

    if (key == NULL) {
        return NULL;
    } else {
        return env->NewStringUTF(key);
    }
}

extern "C"
JNIEXPORT void
JNICALL
Java_com_spark_bipaysdk_jni_JNIUtil_freeAlloc(
        JNIEnv *env,
        jobject obj,
        jstring str) {

    char *pStr = jstringToChar(env, str);
    FreeAlloc(pStr);
}

extern "C"
JNIEXPORT jboolean
JNICALL
Java_com_spark_bipaysdk_jni_JNIUtil_verifyCoinAddress(
        JNIEnv *env,
        jobject obj,
        jstring str,
        jint coinType) {

    char *pStr = jstringToChar(env, str);
    return (jboolean) VerifyAddress(pStr, coinType);
}
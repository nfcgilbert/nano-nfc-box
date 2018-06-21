#include "malloc.h"
#include "jni.h"
#include "monocypher.h"

extern "C" JNIEXPORT jbyteArray JNICALL Java_com_example_newestnanoapp_MainActivity_sign(JNIEnv *env, jobject thiz, jbyteArray messageJava, jbyteArray privateKeyJava, jbyteArray publicKeyJava)
{
    int LEN = 64;

    int msgLen = 0;
    unsigned char* signature = NULL;
    unsigned char* message = NULL;
    int priKeyLen = 0;
    unsigned char* privateKey = NULL;
    int pubKeyLen = 0;
    unsigned char* publicKey = NULL;

    signature = (unsigned char*)malloc(LEN);
    msgLen = env->GetArrayLength(messageJava);
    message = (unsigned char*)malloc(msgLen);
    env->GetByteArrayRegion(messageJava, 0, msgLen , reinterpret_cast<jbyte*>(message));

    priKeyLen = env->GetArrayLength(privateKeyJava);
    privateKey = (unsigned char*)malloc(priKeyLen);

    env->GetByteArrayRegion(privateKeyJava ,0, priKeyLen , reinterpret_cast<jbyte*>(privateKey));

    pubKeyLen = env->GetArrayLength(publicKeyJava);
    publicKey = (unsigned char*)malloc(pubKeyLen);
    env->GetByteArrayRegion (publicKeyJava, 0, pubKeyLen , reinterpret_cast<jbyte*>(publicKey));

    //ed25519_sign(signature,message, msgLen , publicKey , privateKey );
    crypto_sign(signature, privateKey, publicKey , message, msgLen );

    /* (u8        signature[64],
                 const u8  secret_key[32],
                 const u8  public_key[32],
                 const u8 *message, size_t message_size) */


    free(message);
    free(privateKey);
    free(publicKey);

    jbyteArray array = env->NewByteArray (LEN);
    env->SetByteArrayRegion (array, 0, LEN, reinterpret_cast<jbyte*>(signature));

    free(signature);

    return array;

}
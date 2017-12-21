/**
 * Project : XTremWeb
 * File    : XWTracerImpl.c
 *
 * Initial revision : July 2001
 * By               : Anna Lawer
 *
 * New revision : January 2002
 * By           : Oleg Lodygensky
 * e-mail       : lodygens /at\ .in2p3.fr
 */

#include <jni.h>
#include "XWTracerImpl.h"
#include "XWTracerlib.h"
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <string.h>

static nodeState state;
static nodeConfig config;

char *outputDir = NULL;

#define  RETURN_IF_NO_DIR    if (!outputDir) return

/*
 * Class:     xtremweb_archdep_XWTracerImpl
 * Method:    setOutputDir
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_xtremweb_archdep_XWTracerImpl_setOutputDir 
    (JNIEnv * env, jobject obj, jstring dirname)
{
  const char* dirname_tmp;

  if (!env || !dirname)
    return;

  dirname_tmp = (*env)->GetStringUTFChars(env, dirname, 0); 
  if(outputDir != (char*)NULL && strlen(dirname_tmp) > strlen(outputDir)) {
    free(outputDir);
    outputDir = NULL;
  }
  if(outputDir == (char*) NULL) 
    outputDir = malloc(strlen(dirname_tmp) * sizeof(char));
  strcpy(outputDir, dirname_tmp);

  (*env)->ReleaseStringUTFChars(env, dirname, dirname_tmp);
}


/*
 * Class:     xtremweb_archdep_XWTracerImpl
 * Method:    checkNodeState
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_xtremweb_archdep_XWTracerImpl_checkNodeState
  (JNIEnv *env, jobject obj, jint effacer)
{
  RETURN_IF_NO_DIR;

  processCpustate (&state);
  processLoadstate(&state);

  processMemstate (&state);

  writeState(&state, effacer);
  keyBordStat(effacer);
}


JNIEXPORT void JNICALL
Java_xtremweb_archdep_XWTracerImpl_collectNodeConfig (JNIEnv *env,
							jobject obj,
							jint effacer)
{
  RETURN_IF_NO_DIR;
    
  collectCpuinfo(&config);
  collectMeminfo(&config);
  writeConfig(&config, effacer);
  keyBordStat(effacer);
}


/*
 * Class:     xtremweb_archdep_XWTracerImpl
 * Method:    checkNetwork
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_xtremweb_archdep_XWTracerImpl_checkNetwork
  (JNIEnv *env, jobject obj, jint effacer)
{
  RETURN_IF_NO_DIR;
}


JNIEXPORT void JNICALL
Java_xtremweb_worker_ThreadActivityMonitor_fermera(JNIEnv *env,
						   jobject obj)
{
  RETURN_IF_NO_DIR;
}

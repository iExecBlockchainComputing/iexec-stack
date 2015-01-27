#include "XWUtilImpl.h"
#include <stdio.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <signal.h>


JNIEXPORT jboolean JNICALL 
Java_xtremweb_archdep_XWUtilImpl_isRunning(JNIEnv *env, jobject obj, jint pid)
{
  if (kill (pid, SIGCONT) == 0)
    return JNI_TRUE;
  return JNI_FALSE;
}


JNIEXPORT jint JNICALL 
Java_xtremweb_archdep_XWUtilImpl_getPid(JNIEnv *env, jobject obj)
{
  return (jint)getpid();
}

JNIEXPORT jint JNICALL Java_xtremweb_archdep_XWUtilImpl_getGid
  (JNIEnv *e, jobject o) {
  return (jint)getgid();
}

JNIEXPORT jint JNICALL Java_xtremweb_archdep_XWUtilImpl_getUid
  (JNIEnv *e, jobject o) {
  return (jint)getuid();
}


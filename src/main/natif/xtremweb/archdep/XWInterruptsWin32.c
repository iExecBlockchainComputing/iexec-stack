/**
 *  XWUtilWin32.c
 *  Win32 implementation of XWUtil.
 *  
 *  Created by heriard on Tue Apr 16 2002.
 *
 */

#include <windows.h>
#include "XWInterruptsImpl.h"
//#define TRACKER_DLL
#include "win_idle_tracker.h"

static int init = 0;
/*
 * Class:     xtremweb_archdep_XWInterruptsWin32
 * Method:    initialize
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_xtremweb_archdep_XWInterruptsWin32_initialize(JNIEnv * jenv, jobject jobj) {
	init = (int)IdleTrackerInit();
	return JNI_TRUE;
}

/*
 * Class:     xtremweb_archdep_XWInterruptsImpl
 * Method:    readKey
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_xtremweb_archdep_XWInterruptsWin32_readKey(JNIEnv *jenv, jobject jobj) {
	if (init == 0) {
		init = (int)IdleTrackerInit();
	}
	if (init != 0)
	  return IdleTrackerGetKbdIRQ();
  return -1;
}


/*
 * Class:     xtremweb_archdep_XWInterruptsImpl
 * Method:    readMouse
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_xtremweb_archdep_XWInterruptsWin32_readMouse(JNIEnv *jenv, jobject jobj) {
	if (init == 0) {
		init = (int)IdleTrackerInit();
	}
	if (init != 0)
	  return IdleTrackerGetMouseIRQ();
  return -1;
}


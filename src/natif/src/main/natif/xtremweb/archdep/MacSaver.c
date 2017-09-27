/**
 *  File : MacSaver.c
 *  Purpose : MacOSX activator implementation based on screen saver
 *  Author : Oleg Lodygensky
 *  Date : August, 12th 2004
 */


#include <jni.h>
#include "MacSaver.h"
#include <mach/mach.h>
#include <stdio.h>
#include <string.h>
#include <sys/sysctl.h>
#include <CoreServices/CoreServices.h>

/* 
 * This code is inspired from Apple Developper Connection documentation
 * http://developer.apple.com/documentation/mac/Devices/Devices-274.html
 *
 * This forces dimming control (i.e. this sets dimming control)
 */
jint IsSaverRunning()
{ 
    int srunning = 0;

    // force dimming control    
    if (IsDimmingControlDisabled ())
        DimmingControl(TRUE);

    unsigned char dim = GetDimmingTimeout ();
    return (jint)dim;
}

JNIEXPORT jint JNICALL Java_xtremweb_archdep_MacSaver_running
  (JNIEnv * env, jobject obj)
{
    return IsSaverRunning();
}




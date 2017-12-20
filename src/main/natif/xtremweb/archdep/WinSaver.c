/* 
 * WinSaverActivator.c -- native implementation of xtremweb.worker.WinSaverActivator.c
 * 
 * Created Thu May 30 2002 by Samuel Heriard
 */

#include <windows.h>
#include "WinSaver.h"
#include <stdio.h>

#ifndef SPI_GETSCREENSAVERRUNNING
#define SPI_GETSCREENSAVERRUNNING 114
#endif

#ifndef SPI_GETPOWEROFFACTIVE
#define SPI_GETPOWEROFFACTIVE 84
#endif

#ifndef SPI_GETLOWPOWERACTIVE
#define SPI_GETLOWPOWERACTIVE 83
#endif

#ifndef SPI_GETLOWPOWERTIMEOUT
#define SPI_GETLOWPOWERTIMEOUT 79
#endif

#ifndef SPI_GETPOWEROFFTIMEOUT
#define SPI_GETPOWEROFFTIMEOUT 80
#endif

#ifndef SPI_SETPOWEROFFACTIVE
#define SPI_SETPOWEROFFACTIVE 86
#endif

#ifndef SPI_SETLOWPOWERACTIVE
#define SPI_SETLOWPOWERACTIVE 85
#endif
 
/**
 * This code is taken from Lucian Wischik's guide on screensavers
 * http://www.wischik.com/scr/howtoscr.html
 *
 * This retreives whether the screen saver is running
 * @return true if the screen saver is running
 */
BOOL IsScreenSaverRunning()
{ 
    BOOL srunning = FALSE;
    BOOL res = SystemParametersInfo(SPI_GETSCREENSAVERRUNNING,0,&srunning,0);
    if (!res) {
       /* That works fine under '95, '98 and NT5. But not older versions of NT.
        * Hence we need some magic.
        */
        HDESK hDesk=OpenDesktop(TEXT("screen-saver"), 0, FALSE, MAXIMUM_ALLOWED);
        if (hDesk!=NULL) {
            CloseDesktop(hDesk); 
            srunning = TRUE;
        } else
            srunning = (GetLastError() == ERROR_ACCESS_DENIED);
    }
    return srunning;
}


/**
 * This retreives whether the screen saver is running
 * @return true if the screen saver is running
 * @see #IsScreeSaverRunning()
 */
JNIEXPORT jboolean JNICALL Java_xtremweb_archdep_WinSaver_screenSaverRunning
  (JNIEnv * env, jobject obj)
{
    return IsScreenSaverRunning();
}



/**
 * This retreives whether the low power is enabled
 * @return true if the low power is enabled
 */
BOOL IsLowPowerActive()
{ 
    BOOL srunning = FALSE;
    SystemParametersInfo(SPI_GETLOWPOWERACTIVE,0,&srunning,0);
    return srunning;
}

/**
 * This retreives whether the low power is enabled
 * @return true if the low power is enabled
 * @see #IsLowPowerActive()
 */
JNIEXPORT jboolean JNICALL Java_xtremweb_archdep_lowPowerActive
  (JNIEnv *env, jclass c)
{
    return IsLowPowerActive();
}

/**
 * This retreives whether the low power time out
 * @return the low power time out in seconds
 */
jint LowPowerTimeOut()
{ 
    int timeout = 1000;
    SystemParametersInfo(SPI_GETLOWPOWERTIMEOUT,0,&timeout,0);
    return (jint)timeout;
}

/**
 * This retreives the low power time out
 * @return the low power time out in seconds
 * @see #LowPowerTimeOut()
 */
JNIEXPORT jint JNICALL Java_xtremweb_archdep_lowPowerTimeOut
  (JNIEnv *env, jclass c)
{
    return LowPowerTimeOut();
}

/**
 * This retreives the power off is enabled
 * @return true if the low power is enabled
 */
BOOL IsLowPowerOff()
{ 
    BOOL srunning = FALSE;
    SystemParametersInfo(SPI_GETPOWEROFFACTIVE,0,&srunning,0);
    return srunning;
}

/**
 * This retreives whether the power off is enabled
 * @return true if the power off is enabled
 * @see #IsPowerOff()
 */
JNIEXPORT jboolean JNICALL Java_xtremweb_archdep_powerOffActive
  (JNIEnv *env, jclass c)
{
    return IsLowPowerOff();
}

/**
 * This retreives the power off time out
 * @return the power off time out in seconds
 */
jint PowerOffTimeOut()
{ 
    int timeout = 1000;
    SystemParametersInfo(SPI_GETPOWEROFFTIMEOUT,0,&timeout,0);
    return (jint)timeout;
}

/**
 * This retreives the power off time out
 * @return the low power time out in seconds
 * @see #PowerOffTimeOut()
 */
JNIEXPORT jint JNICALL Java_xtremweb_archdep_powerOffTimeOut
  (JNIEnv *env, jclass c)
{
    return PowerOffTimeOut();
}

/**
 * This sets/unsets the low power
 * @param b is an integer set to 1 to set low power, 0 to to unsets low power
 */
void LowPower(int b)
{ 
    SystemParametersInfo(SPI_SETLOWPOWERACTIVE,b,NULL,0);
}

/**
 * This unsets the low power
 */
JNIEXPORT void JNICALL Java_xtremweb_archdep_disableLowPower
(JNIEnv *env, jclass c) {
	LowPower(0);
}

/**
 * This sets the low power
 */
JNIEXPORT void JNICALL Java_xtremweb_archdep_enableLowPower
(JNIEnv *env, jclass c) {
	LowPower(1);
}

/**
 * This sets/unsets the power off
 * @param b is an integer set to 1 to set low power, 0 to to unsets power off
 */
void PowerOff(int b)
{ 
    SystemParametersInfo(SPI_SETPOWEROFFACTIVE,b,NULL,0);
}

/**
 * This unsets the power off
 */
JNIEXPORT void JNICALL Java_xtremweb_archdep_disablePowerOff
(JNIEnv *env, jclass c) {
	PowerOff(0);
}

/**
 * This sets the power off
 */
JNIEXPORT void JNICALL Java_xtremweb_archdep_enablePowerOff
(JNIEnv *env, jclass c) {
	PowerOff(1);
}

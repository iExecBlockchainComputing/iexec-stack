#ifndef __TRACKER_DLL_H__
#define __TRACKER_DLL_H__

#define VC_EXTRALEAN		// Exclude rarely-used stuff from Windows headers

/**
 * This has been extracted from boinc
 * http://boinc.berkeley.edu/
 *
 * IdleTracker - a DLL that tracks the user's idle input time
 *               system-wide.
 *
 * Usage
 * =====
 * - call IdleTrackerInit() when you want to start monitoring.
 * - call IdleTrackerTerm() when you want to stop monitoring.
 * - to get the time past since last user input, do the following:
 *    GetTickCount() - IdleTrackerGetLastTickCount()
 *
 * Author: Sidney Chong
 * Date: 25/5/2000
 * Version: 1.0
 **/


#include <jni.h>

#ifdef TRACKER_DLL
  #ifdef TRACKER_EXPORTS
    #define TRACKER_API __declspec(dllexport)
  #else
    #define TRACKER_API __declspec(dllimport)
  #endif
#else
  #define TRACKER_API
#endif

#ifdef __cplusplus
extern "C" {
#endif

TRACKER_API BOOL IdleTrackerInit();
TRACKER_API jint IdleTrackerGetKbdIRQ();
TRACKER_API jint IdleTrackerGetMouseIRQ();
TRACKER_API void IdleTrackerTerm();
TRACKER_API DWORD IdleTrackerGetLastTickCount();


#ifdef __cplusplus
}
#endif

#endif

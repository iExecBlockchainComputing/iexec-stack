
/**
 * This has been extracted from boinc
 * http://boinc.berkeley.edu/
 */

/**
 * IdleTracker - a DLL that tracks the user's idle input time
 *               system-wide.
 *
 * <u>Usage</u>
 * <ul> 
 * <li> call IdleTrackerInit() when you want to start monitoring.
 * <li> call IdleTrackerTerm() when you want to stop monitoring.
 * <li> to get the time past since last user input, do the following:
 *      GetTickCount() - IdleTrackerGetLastTickCount()
 * </ul>
 *
 * @author: Sidney Chong
 * @date: 25/5/2000
 * @version: 1.0
 **/

#include <windows.h>
//#include "XWInterruptsImpl.h"
#define TRACKER_DLL
#define TRACKER_EXPORTS
#include "win_idle_tracker.h"
//#include <crtdbg.h>
#include <iostream>
#include <tchar.h>


/**
 * The following global data is SHARED among all instances of the DLL
 * (processes); i.e., these are system-wide globals.
 **/ 
#pragma data_seg(".IdleTrac")	// you must define as SHARED in .def
HHOOK 	g_hHkKeyboard = NULL;	// handle to the keyboard hook
HHOOK 	g_hHkMouse = NULL;	// handle to the mouse hook
static DWORD	s_dwLastTick = 0;	// tick time of last input event
static LONG	s_mouseLocX = -1;	// x-location of mouse position
static LONG	s_mouseLocY = -1;	// y-location of mouse position

/**
 * Pseudo keyboard IRQ counter
 */
static jint    s_kbdIRQ = 0;
/**
 * Pseudo mouse IRQ counter
 */
static jint    s_mouseIRQ = 0;

#pragma data_seg()
#pragma comment(linker, "/section:.IdleTrac,rws")

//HINSTANCE g_hInstance = NULL;   // global instance handle
extern HINSTANCE g_instance;


/**
 * Get keyboard IRQ pseudo counter
 **/
TRACKER_API jint IdleTrackerGetKbdIRQ()
{
  return s_kbdIRQ;
}


/**
 * Get mouse IRQ pseudo counter
 **/
TRACKER_API jint IdleTrackerGetMouseIRQ()
{
  return s_mouseIRQ;
}


/**
 * Get tick count of last keyboard or mouse event
 **/
TRACKER_API DWORD IdleTrackerGetLastTickCount()
{
	return s_dwLastTick;
}

/**
 * Keyboard hook: record tick count
 **/
LRESULT CALLBACK KeyboardTracker(int code, WPARAM wParam, LPARAM lParam)
{
  if (code==HC_ACTION) {
    s_kbdIRQ++;
    s_dwLastTick = GetTickCount();
  }
  return ::CallNextHookEx(g_hHkKeyboard, code, wParam, lParam);
}

/**
 * Mouse hook: record tick count
 **/
LRESULT CALLBACK MouseTracker(int code, WPARAM wParam, LPARAM lParam)
{
  if (code==HC_ACTION) {
    MOUSEHOOKSTRUCT* pStruct = (MOUSEHOOKSTRUCT*)lParam;
    //we will assume that any mouse msg with the same locations as spurious
    if (pStruct->pt.x != s_mouseLocX || pStruct->pt.y != s_mouseLocY)
      {
				s_mouseLocX = pStruct->pt.x;
				s_mouseLocY = pStruct->pt.y;
				s_dwLastTick = GetTickCount();
				s_mouseIRQ++;
      }
  }
  return ::CallNextHookEx(g_hHkMouse, code, wParam, lParam);
}

/**
 * Initialize DLL: install kbd/mouse hooks.
 **/
TRACKER_API BOOL IdleTrackerInit()
{
  if (g_hHkKeyboard == NULL) {
    g_hHkKeyboard = SetWindowsHookEx(WH_KEYBOARD, KeyboardTracker, g_instance, 0);
  }
  if (g_hHkMouse == NULL) {
    g_hHkMouse = SetWindowsHookEx(WH_MOUSE, MouseTracker, g_instance, 0);
  }
  
  //_ASSERT(g_hHkKeyboard);
  //_ASSERT(g_hHkMouse);

  s_dwLastTick = GetTickCount(); // init count

  if (!g_hHkKeyboard || !g_hHkMouse)
    return FALSE;
  else
    return TRUE;
}

/**
 * Terminate DLL: remove hooks.
 **/
TRACKER_API void IdleTrackerTerm()
{
  BOOL bResult;
  if (g_hHkKeyboard)
    {
      bResult = UnhookWindowsHookEx(g_hHkKeyboard);
      //_ASSERT(bResult);
      g_hHkKeyboard = NULL;
    }
  if (g_hHkMouse)
    {
      bResult = UnhookWindowsHookEx(g_hHkMouse);
      //_ASSERT(bResult);
      g_hHkMouse = NULL;
    }
}


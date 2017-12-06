/*
 * Copyrights     : CNRS
 * Author         : Oleg Lodygensky
 * Acknowledgment : XtremWeb-HEP is based on XtremWeb 1.8.0 by inria : http://www.xtremweb.net/
 * Web            : http://www.xtremweb-hep.org
 * 
 *      This file is part of XtremWeb-HEP.
 *
 *    XtremWeb-HEP is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    XtremWeb-HEP is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with XtremWeb-HEP.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
/*
 *  XWUtilWin32.c
 *  Win32 implementation of XWUtil.
 *  
 *  Created by heriard on Tue Apr 16 2002.
 *
 * Modified: Oct 21st, 2014
 * GlobalMemoryStatusEx() replaces GlobalMemoryStatus()
 */

#include <windows.h>
#include "XWUtilImpl.h"
#include <sys/types.h>
#include <sys/stat.h>


/*
 * Class:     xtremweb_archdep_XWUtilImpl
 * Method:    getPid
 * Signature: ()I
 */
JNIEXPORT jint JNICALL 
Java_xtremweb_archdep_XWUtilImpl_getPid(JNIEnv *env, jobject obj)
{
  return (jint)GetCurrentProcessId();
}


/*
 * Class:     xtremweb_archdep_XWUtilImpl
 * Method:    isRunning
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL 
Java_xtremweb_archdep_XWUtilImpl_isRunning(JNIEnv *env, jobject obj, jint pid)
{
  HANDLE phandle = OpenProcess(PROCESS_QUERY_INFORMATION, 0, (DWORD)pid);
  jboolean ret = (phandle != NULL);
  if (ret) 
    CloseHandle(phandle);
  return ret;
}


/*
 * Class:     xtremweb_archdep_XWUtilImpl
 * Method:    getSpeedProc
 * Signature: ()I
 */
JNIEXPORT jint JNICALL 
Java_xtremweb_archdep_XWUtilImpl_getSpeedProc (JNIEnv *env , jobject o)
{
  HKEY hKey;             // handle to registry key
  DWORD dwBuffer;        // bytes to allocate for buffers
  DWORD dwBufferSize;    // size of dwBuffer

  RegOpenKeyEx (HKEY_LOCAL_MACHINE,
                "HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\0",
                0,
                KEY_READ,
                &hKey);

  dwBufferSize = sizeof(dwBuffer);

  RegQueryValueEx (hKey,
                   "~MHz",
                   NULL,
                   NULL,
                   (LPBYTE) &dwBuffer,
                   &dwBufferSize );

  RegCloseKey (hKey);

  return (jint) dwBuffer;
}


/*
 * Class:     xtremweb_archdep_XWUtilImpl
 * Method:    getProcModel
 * Signature: ()I
 */
JNIEXPORT jstring JNICALL 
Java_xtremweb_archdep_XWUtilImpl_getProcModel (JNIEnv *env , jobject o)
{
  const char* unknown = "UNKNOWN"; // all Windows version
  const char* intel = "INTEL"; // all Windows version
  const char* mips = "MIPS"; // NT 3.51 only
  const char* alpha = "ALPHA"; // NT 4.0 and earlier
  const char* ppc = "PPC"; // NT 4.0 and earlier
  const char* ia64 = "IA64"; // Windows 64 bits only
  const char* ia32OnWin64 = "IA32WIN64"; // Windows 64 bits only
  const char* amd64 = "AMD64"; // Windows 64 bits only

  SYSTEM_INFO SystemInfo;
  GetSystemInfo(&SystemInfo);

  switch (SystemInfo.wProcessorArchitecture) {
  case PROCESSOR_ARCHITECTURE_UNKNOWN :
    return (*env)->NewStringUTF(env, unknown);
  case PROCESSOR_ARCHITECTURE_INTEL :
    return (*env)->NewStringUTF(env, intel);
  case PROCESSOR_ARCHITECTURE_MIPS :
    return (*env)->NewStringUTF(env, mips);
  case PROCESSOR_ARCHITECTURE_ALPHA :
    return (*env)->NewStringUTF(env, alpha);
  case PROCESSOR_ARCHITECTURE_PPC :
    return (*env)->NewStringUTF(env, ppc);
  case PROCESSOR_ARCHITECTURE_IA64 :
    return (*env)->NewStringUTF(env, ia64);
#ifdef PROCESSOR_ARCHITECTURE_IA32_ON_WIN64
  case PROCESSOR_ARCHITECTURE_IA32_ON_WIN64 :
    return (*env)->NewStringUTF(env, ia32OnWin64);
#endif
#ifdef PROCESSOR_ARCHITECTURE_AMD64
  case PROCESSOR_ARCHITECTURE_AMD64 :
    return (*env)->NewStringUTF(env, amd64);
#endif
  }

  return (*env)->NewStringUTF(env, unknown);
}


/*
 * Class:     xtremweb_archdep_XWUtilImpl
 * Method:    getTotalMem
 * Signature: ()I
 */
JNIEXPORT jlong JNICALL
Java_xtremweb_archdep_XWUtilImpl_getTotalMem (JNIEnv *env , jobject o)
{
  MEMORYSTATUSEX statex;
  statex.dwLength = sizeof (statex);
  GlobalMemoryStatusEx (&statex);
  return (jlong) (statex.ullTotalPhys/1024);
}


/*
 * Class:     xtremweb_archdep_XWUtilImpl
 * Method:    getTotalSwap
 * Signature: ()I
 */
JNIEXPORT jlong JNICALL
Java_xtremweb_archdep_XWUtilImpl_getTotalSwap (JNIEnv *env , jobject o)
{
  MEMORYSTATUSEX statex;
  statex.dwLength = sizeof (statex);
  GlobalMemoryStatusEx (&statex);
  return (jlong) (statex.ullTotalVirtual/1024);
}

/*
 * Class:     xtremweb_archdep_XWUtilImpl
 * Method:    raz
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_xtremweb_archdep_XWUtilImpl_raz
(JNIEnv *env, jobject obj)
{
}



JNIEXPORT jint JNICALL Java_xtremweb_archdep_XWUtilImpl_getGid
(JNIEnv *e, jobject o) {

  // getgid() is not implemented in win32
  // there should be something to do with LsaGetLogonSessionData
  //  return (jint)getgid();
  return -1;
}

JNIEXPORT jint JNICALL Java_xtremweb_archdep_XWUtilImpl_getUid
(JNIEnv *e, jobject o) {

  // getuid() is not implemented in win32
  // there should be something to do with LsaGetLogonSessionData
  //  return (jint)getuid();
  return -1;
}


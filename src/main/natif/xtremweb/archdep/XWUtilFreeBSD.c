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
 *  XWUtilFreeBSD.c
 *  FreeBSD implementation of XWUtil 
 *
 *  Quick and dirty,  I don't know anything about freebsd :(
 *  Created by fedak based on the MacOSX from heriard on March 2004.
 *
 */

#include "XWUtilImpl.h"
#include <stdio.h>
#include <string.h>
#include <signal.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/sysctl.h>
#include <unistd.h>



static int hw_cpu_freq[2] = { CTL_HW, HW_CPU_FREQ };
static int hw_physmem[2]  = { CTL_HW, HW_PHYSMEM  };
static int hw_swapusage[2]  = { CTL_VM, VM_SWAPUSAGE  };


static long long_sysctl (int * mib, unsigned int miblen)
{
    int err;
    long ret;
    size_t sz =sizeof(ret);
    err = sysctl (mib, miblen, &ret, &sz, NULL, 0);
    if (err < 0) perror("sysctl");
    return (err < 0 ? err : ret);
}

static int int_sysctl (int * mib, unsigned int miblen)
{
    int ret, err;
    size_t sz =sizeof(ret);
    err = sysctl (mib, miblen, &ret, &sz, NULL, 0);
    if (err < 0) perror("sysctl");
    return (err < 0 ? err : ret);
}


/**
 * in Mhz
 */
JNIEXPORT jint JNICALL Java_xtremweb_archdep_XWUtilImpl_getSpeedProc
(JNIEnv * env, jobject obj)
{
  unsigned int ret = (unsigned int) int_sysctl (hw_cpu_freq, 2);
  ret = ret / 1000000;
  return ret;
}


/**
 * in Kb
 */
JNIEXPORT jlong JNICALL Java_xtremweb_archdep_XWUtilImpl_getTotalMem
(JNIEnv * env, jobject obj)
{
  unsigned long ret = (unsigned int)long_sysctl (hw_physmem, 2);
  ret = ret / 1024;
  return ret;
}
/**
 * in Kb
 */
JNIEXPORT jlong JNICALL Java_xtremweb_archdep_XWUtilImpl_getTotalSwap
(JNIEnv * env, jobject obj) {
  unsigned long ret = (unsigned int)long_sysctl (hw_swapusage, 2);
  ret = ret / 1024;
  return ret;
}

/*
 * look at hostinfo source for more infos:
 * http://www.opensource.apple.com
 *                   /cgi-bin/registered/cvs/src/live/system_cmds/hostinfo.tproj/hostinfo.c
 */
JNIEXPORT jstring JNICALL Java_xtremweb_archdep_XWUtilImpl_getProcModel
(JNIEnv * env, jobject obj) {
    return (*env)->NewStringUTF(env, "n/a");    
}


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


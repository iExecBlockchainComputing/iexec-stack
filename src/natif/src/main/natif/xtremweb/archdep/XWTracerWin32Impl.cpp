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


#include "XWTracerWin32lib.h"
#include "XWTracerImpl.h"
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
/*
#include <jni.h>
#include <stdio.h>
#include <string.h>
*/

/*
static nodeState state;
static nodeConfig config;
*/


/* 
 * static variables
 */

#define PERF_OBJECTS "2 4 234 238 546"
static char* logName = "xwtraces.log";
static char* perfQuery = PERF_OBJECTS;
static Perf hostPerf;
static int initialized = 0;


const char *outputDir = NULL;
#define  RETURN_IF_NO_DIR    if (!outputDir) return

/*
 * Class:     xtremweb_archdep_XWTracerImpl
 * Method:    setOutputDir
 * Signature: (Ljava/lang/String;)V
 */
// ****************************************************************************
JNIEXPORT void JNICALL
Java_xtremweb_archdep_XWTracerImpl_setOutputDir (JNIEnv * env,
						 jobject obj,
						 jstring dirname)
{
  outputDir = env->GetStringUTFChars(dirname, 0);
}


// ****************************************************************************
static void initPerf ()
{
  hostPerf.CpuPerf.lastTime.QuadPart = 0;
  hostPerf.CpuPerf.lastValue = 0;
  hostPerf.CpuInterruptsPerf.lastTime.QuadPart = 0;
  hostPerf.CpuInterruptsPerf.lastValue = 0;
  hostPerf.DiskPerf.lastTime.QuadPart = 0;
  hostPerf.DiskPerf.lastValue = 0;
  hostPerf.IPPerf.lastTime.QuadPart = 0;
  hostPerf.IPPerf.lastValue = 0;
  hostPerf.PageFaultsPerf.lastTime.QuadPart = 0;
  hostPerf.PageFaultsPerf.lastValue = 0;
  hostPerf.UPSincePerf.lastTime.QuadPart = 0;
  hostPerf.UPSincePerf.lastValue = 0;
  hostPerf.hostName = NULL;
  hostPerf.perfQuery = (char*)perfQuery;
  hostPerf.PerfData = NULL;
  hostPerf.hthread = NULL;
  hostPerf.hmutexlog = NULL;
  hostPerf.hmutexperf = NULL;
  hostPerf.lastError = 0L;

  hostPerf.logFile = fopen (logName, "w+");

  fprintf (hostPerf.logFile, "%s", "Time;CPU%;Interrupts/sec;");
  fprintf (hostPerf.logFile, "%s", "Memory Kb available;Memory Page faults/sec;");
  fprintf (hostPerf.logFile, "%s", "Nb processes;Nb threads;Sys uptime;");
  fprintf (hostPerf.logFile, "%s", "IP datagrams/sec;Physical disks transferts/sec\n");
}


JNIEXPORT void JNICALL
Java_xtremweb_archdep_XWTracerImpl_checkNodeState (JNIEnv *jenv,
						     jobject thisObj,
						     jint effacer)
{
  if (initialized == 0) {
    initialized = 1;
    initPerf ();
  }

  time_t ltime;
  LONG status;

  DWORD dwvalue;

  status = Tracer_Reload (hostPerf);
  //
  // save new error only to reduce log file size...
  //
  if (hostPerf.lastError != status)
  {
    hostPerf.lastError = status;
  }

  if (status != ERROR_SUCCESS)
  {
    return;
  }

//**
//************************** UNIX style time & date *****************
//**
  time (&ltime);
  fprintf( hostPerf.logFile, "%lu;", ltime);


// **
// ************************* Processor ******************************
// **
  dwvalue = Tracer_GetProcessorTime (hostPerf);
  fprintf (hostPerf.logFile, "%lu;", dwvalue);


// **
// ************************* Interrupts per second ******************
// **
  dwvalue = Tracer_GetProcessorInterruptsPerSec (hostPerf);
  fprintf (hostPerf.logFile, "%lu;", dwvalue);


// **
// ************************* Memory *********************************
// **
  dwvalue = Tracer_GetMemoryKBAvailable (hostPerf);
  fprintf (hostPerf.logFile, "%lu;", dwvalue);

  dwvalue = Tracer_GetMemoryPageFaultsPerSec (hostPerf);
  fprintf (hostPerf.logFile, "%lu;", dwvalue);

// **
// ************************* System *********************************
// **
  dwvalue = Tracer_GetSystemProcesses (hostPerf);
  fprintf (hostPerf.logFile, "%lu;", dwvalue);

  dwvalue = Tracer_GetSystemThreads (hostPerf);
  fprintf (hostPerf.logFile, "%lu;", dwvalue);

  dwvalue = Tracer_GetSystemUpTime (hostPerf);
  fprintf (hostPerf.logFile, "%lu;", dwvalue);


// **
// *************************  IP  ***********************************
// **
  dwvalue = Tracer_GetIPDatagramsPerSec (hostPerf);
  fprintf (hostPerf.logFile, "%lu;", dwvalue);

// **
// ************************* PhysicalDisk ***************************
// **
  dwvalue = Tracer_GetPhysicalDiskTransfertsPerSec (hostPerf);
  fprintf (hostPerf.logFile, "%lu\n", dwvalue);

  fflush (hostPerf.logFile);

} // Java_xtremweb_archdep_XWTracerImpl_checkNodeState ()


JNIEXPORT void JNICALL
Java_xtremweb_archdep_XWTracerImpl_collectNodeConfig (JNIEnv *jenv,
							jobject thisObj,
							jint effacer)
{
/*
  collectCpuinfo(&config);
  collectMeminfo(&config);
  writeConfig(&config, effacer);
  keyBordStat(effacer);
*/
}


JNIEXPORT void JNICALL
Java_xtremweb_archdep_XWTracerImpl_checkNetwork(JNIEnv *env,
						jobject thisObj,
						jint effacer)
{
}

JNIEXPORT void JNICALL
Java_xtremweb_archdep_XWTracerImpl_fermera(JNIEnv *env,
					   jobject thisObj)
{
/*
  close(s);
*/
}

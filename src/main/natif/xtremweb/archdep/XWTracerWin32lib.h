#ifndef __TRACER_DLL_H__
#define __TRACER_DLL_H__

#define VC_EXTRALEAN		// Exclude rarely-used stuff from Windows headers

#include <windows.h>
#include <stdio.h>
#include <stdlib.h>
#include <malloc.h>
#include <string.h>
#include <winperf.h>
#include <time.h>



#ifdef TRACER_DLL
  #ifdef TRACER_EXPORTS
    #define TRACER_API __declspec(dllexport)
  #else
    #define TRACER_API __declspec(dllimport)
  #endif
#else
  #define TRACER_API 
#endif

/*
#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif
*/

/*
// This class is exported from the tracer.dll
class TRACER_API CTracer {
public:
	CTracer(void);
	// TODO: add your methods here.
};

extern TRACER_API int nTracer;

TRACER_API int fnTracer(void);
*/


#define PERF_OBJ_SYSTEM		  2 // System
/*
			Counter 10: File Read Operations/sec
			Counter 12: File Write Operations/sec
			Counter 14: File Control Operations/sec
			Counter 16: File Read Bytes/sec
			Counter 18: File Write Bytes/sec
			Counter 20: File Control Bytes/sec
			Counter 146: Context Switches/sec
			Counter 150: System Calls/sec
			Counter 406: File Data Operations/sec
			Counter 674: System Up Time
			Counter 44: Processor Queue Length
			Counter 248: Processes
			Counter 250: Threads
			Counter 686: Alignment Fixups/sec
			Counter 688: Exception Dispatches/sec
			Counter 690: Floating Emulations/sec
			Counter 1350: % Registry Quota In Use
			Counter 1350: % Registry Quota In Use
*/

#define PERF_OBJ_MEMORY				  4 // Memory
/*
			Counter 28: Page Faults/sec
			Counter 36: Cache Faults/sec
			Counter 678: Free System Page Table Entries
			Counter 818: Cache Bytes
			Counter 1380: Available KBytes
*/

#define PERF_OBJ_CACHE					 86 // Cache

#define PERF_OBJ_PROCESS				230 // Process
/*
		Instance _Total: 
			Counter 6: % Processor Time
			Counter 142: % User Time
			Counter 144: % Privileged Time
			Counter 172: Virtual Bytes Peak
			Counter 174: Virtual Bytes
			Counter 28: Page Faults/sec
			Counter 178: Working Set Peak
			Counter 180: Working Set
			Counter 182: Page File Bytes Peak
			Counter 184: Page File Bytes
			Counter 186: Private Bytes
			Counter 680: Thread Count
			Counter 682: Priority Base
			Counter 684: Elapsed Time
			Counter 784: ID Process
			Counter 1410: Creating Process ID
			Counter 56: Pool Paged Bytes
			Counter 58: Pool Nonpaged Bytes
			Counter 952: Handle Count
			Counter 1412: IO Read Operations/sec
			Counter 1414: IO Write Operations/sec
			Counter 1416: IO Data Operations/sec
			Counter 1418: IO Other Operations/sec
			Counter 1420: IO Read Bytes/sec
			Counter 1422: IO Write Bytes/sec
			Counter 1424: IO Data Bytes/sec
			Counter 1426: IO Other Bytes/sec
*/

#define PERF_OBJ_THREAD						232 // Thread
/*
		Instance _Total: 
			Counter 6: % Processor Time
			Counter 142: % User Time
			Counter 144: % Privileged Time
			Counter 146: Context Switches/sec
			Counter 684: Elapsed Time
			Counter 694: Priority Current
			Counter 682: Priority Base
			Counter 706: Start Address
			Counter 46: Thread State
			Counter 336: Thread Wait Reason
			Counter 784: ID Process
			Counter 804: ID Thread
*/

#define PERF_OBJ_PHYSICAL_DISKS							234 // Physical disks
/*
		Instance _Total: 
			Counter 198: Current Disk Queue Length
			Counter 200: % Disk Time
			Counter 200: % Disk Time
			Counter 1400: Avg. Disk Queue Length
			Counter 202: % Disk Read Time
			Counter 202: % Disk Read Time
			Counter 1402: Avg. Disk Read Queue Length
			Counter 204: % Disk Write Time
			Counter 204: % Disk Write Time
			Counter 1404: Avg. Disk Write Queue Length
			Counter 206: Avg. Disk sec/Transfer
			Counter 206: Avg. Disk sec/Transfer
			Counter 208: Avg. Disk sec/Read
			Counter 208: Avg. Disk sec/Read
			Counter 210: Avg. Disk sec/Write
			Counter 210: Avg. Disk sec/Write
			Counter 212: Disk Transfers/sec
			Counter 214: Disk Reads/sec
			Counter 216: Disk Writes/sec
			Counter 218: Disk Bytes/sec
			Counter 220: Disk Read Bytes/sec
			Counter 222: Disk Write Bytes/sec
			Counter 224: Avg. Disk Bytes/Transfer
			Counter 224: Avg. Disk Bytes/Transfer
			Counter 226: Avg. Disk Bytes/Read
			Counter 226: Avg. Disk Bytes/Read
			Counter 228: Avg. Disk Bytes/Write
			Counter 228: Avg. Disk Bytes/Write
			Counter 1482: % Idle Time
			Counter 1482: % Idle Time
			Counter 1484: Split IO/Sec
*/

#define PERF_OBJ_PROCESSOR				  238 // Processor
/*
		Instance _Total: 
			Counter 6: % Processor Time
			Counter 142: % User Time
			Counter 144: % Privileged Time
			Counter 148: Interrupts/sec
			Counter 696: % DPC Time
			Counter 698: % Interrupt Time
			Counter 1334: DPCs Queued/sec
			Counter 1336: DPC Rate
			Counter 1338: DPC Bypasses/sec
			Counter 1340: APC Bypasses/sec
*/

#define PERF_OBJ_NETWORK_INTERFACE	510 // Network Interfarce
/*
		Instance ORINOCO PC Card: 
			Counter 388: Bytes Total/sec
*/

#define PERF_OBJ_IP							546 // IP
/*
			Counter 438: Datagrams/sec
			Counter 446: Datagrams Received/sec
			Counter 552: Datagrams Received Header Errors
			Counter 554: Datagrams Received Address Errors
			Counter 556: Datagrams Forwarded/sec
			Counter 558: Datagrams Received Unknown Protocol
			Counter 560: Datagrams Received Discarded
			Counter 562: Datagrams Received Delivered/sec
			Counter 442: Datagrams Sent/sec
			Counter 566: Datagrams Outbound Discarded
			Counter 568: Datagrams Outbound No Route
			Counter 570: Fragments Received/sec
			Counter 572: Fragments Re-assembled/sec
			Counter 574: Fragment Re-assembly Failures
			Counter 576: Fragmented Datagrams/sec
			Counter 578: Fragmentation Failures
			Counter 580: Fragments Created/sec
*/

#define PERF_OBJ_ICMP						582 // ICMP

#define PERF_OBJ_TCP						638 // TCP

#define PERF_OBJ_UDP						658 // UDP

#define PERF_OBJ_PAGING_FILE		700 // Paging file
/*
		Instance _Total: 
			Counter 702: % Usage
			Counter 704: % Usage Peak
*/

#define PERF_OBJ_RAS_PORTS		870 // RAS port (E/S : LPT, COM & VPN Cards)

#define PERF_OBJ_RAS_TOTAL		906 // RAS Total





#define TOTALBYTES    8192
#define BYTEINCREMENT 1024

#define LOCALHOST "localhost"


typedef PERF_DATA_BLOCK             PERF_DATA,      *PPERF_DATA;
typedef PERF_OBJECT_TYPE            PERF_OBJECT,    *PPERF_OBJECT;
typedef PERF_INSTANCE_DEFINITION    PERF_INSTANCE,  *PPERF_INSTANCE;
typedef PERF_COUNTER_DEFINITION     PERF_COUNTER,   *PPERF_COUNTER;



typedef struct _PerfCounter
{

	LARGE_INTEGER	lastTime;
	DWORD						lastValue;

} PerfCounter;


typedef struct _Perf
{
	char* hostName;
	char* perfQuery;
	HANDLE hthread;
	HANDLE hmutexperf;
	HANDLE hmutexlog;
	LONG lastError;

	FILE* logFile;

	PPERF_DATA_BLOCK PerfData;

	PerfCounter CpuInterruptsPerf;
	PerfCounter CpuPerf;
	PerfCounter DiskPerf;
	PerfCounter UPSincePerf;
	PerfCounter IPPerf;
	PerfCounter PageFaultsPerf;

} Perf;



#ifdef __cplusplus
extern "C" {
#endif

TRACER_API void __stdcall Tracer_Init ();
TRACER_API LONG Tracer_Reload (Perf&);
TRACER_API void Tracer_Exit ();


TRACER_API BOOL Tracer_GetIpHelp (char*, char*);
TRACER_API void Tracer_GetCpuNb (DWORD*);



TRACER_API DWORD Tracer_GetMemoryKBAvailable (Perf&);
TRACER_API DWORD Tracer_GetMemoryPageFaultsPerSec (Perf&);
TRACER_API DWORD Tracer_GetSystemProcesses (Perf&);
TRACER_API DWORD Tracer_GetSystemThreads (Perf&);
TRACER_API DWORD Tracer_GetSystemUpTime (Perf&);
TRACER_API DWORD Tracer_GetIPDatagramsPerSec (Perf&);
TRACER_API DWORD Tracer_GetProcessorTime (Perf&);
TRACER_API DWORD Tracer_GetProcessorInterruptsPerSec (Perf&);
TRACER_API DWORD Tracer_GetPhysicalDiskTransfertsPerSec (Perf&);

#ifdef __cplusplus
}
#endif

#endif

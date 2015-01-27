// tracer.cpp : Defines the entry point for the DLL application.
//


#include "XWTracerWin32lib.h"

#define _WINSOCK2API_
//#include <NspAPI.h>
#ifndef CROSS
  #include <IPTypes.h>
  #include <BaseTsd.h>
#endif

#include <iphlpapi.h>



/*
 * Displaying Object, Instance, and Counter Names
 * The following example displays the index and name of each object,
 * along with the indices and names of its counters. 
 *
 * The object and counter names are stored in the registry, by index.
 * This example creates a function, GetNameStrings, to load the indices
 * and names of each object and counter from the registry into an array,
 * so that they can be easily accessed.
 * GetNameStrings uses the following standard registry functions 
 * to access the data:
 *                      - RegOpenKey,
 *                      - RegCloseKey,
 *                      - RegQueryInfoKey,
 *                      - RegQueryValueEx. 
 *
 * This example creates the following functions for navigating 
 * the performance data:
 *                      - FirstObject,
 *                      - FirstInstance,
 *                      - FirstCounter,
 *                      - NextCounter,
 *                      - NextInstance,
 *                      - NextCounter. 
 *
 * These functions navigate the performance data by using the offsets 
 * stored in the performance structures. 
 */

/*****************************************************************
 *                                                               *
 *                       Static variables.                       *
 *                                                               *
 *****************************************************************/



/****************** counter names ********************************/
static char**	lpNamesArray = NULL;

/****************** mutex for concurrent access ******************/
static HANDLE hMutex = NULL;

/****************** hook handle ******************/
static HHOOK hookh;


/*****************************************************************
 *                                                               *
 *                       Static functions.                       *
 *                                                               *
 *****************************************************************/


static LARGE_INTEGER     Tracer_GetTime100nsSec (Perf&);
static PPERF_OBJECT_TYPE Tracer_FirstObject (PPERF_DATA_BLOCK);
static PPERF_OBJECT_TYPE Tracer_NextObject (PPERF_OBJECT_TYPE);
static PPERF_OBJECT_TYPE Tracer_GetObjectByName(Perf&, LPSTR);

static PPERF_INSTANCE_DEFINITION Tracer_FirstInstance (PPERF_OBJECT_TYPE);
static PPERF_INSTANCE_DEFINITION Tracer_NextInstance (PPERF_INSTANCE_DEFINITION);
static PPERF_INSTANCE_DEFINITION Tracer_GetInstanceByName (PPERF_OBJECT_TYPE, LPSTR);
static char*                     Tracer_GetInstanceName (PPERF_INSTANCE_DEFINITION);

static PPERF_COUNTER_DEFINITION Tracer_FirstCounter (PPERF_OBJECT_TYPE);
static PPERF_COUNTER_DEFINITION Tracer_NextCounter (PPERF_COUNTER_DEFINITION);
static PPERF_COUNTER_DEFINITION Tracer_GetCounterByName (PPERF_OBJECT_TYPE, LPSTR, LPSTR);

static PVOID Tracer_GetInstanceCounterData (PERF_OBJECT_TYPE *,
					    PERF_INSTANCE_DEFINITION *,
					    PERF_COUNTER_DEFINITION *);
static PVOID Tracer_GetCounterData (PERF_OBJECT_TYPE *pObjectDef,
				    PERF_COUNTER_DEFINITION *pCounterDef);
static void GetNameStrings ();
static LONG LoadPerfObjects (Perf&);



/*****************************************************************
 *                                                               *
 *                   Init/deinit lib functions.                  *
 *                                                               *
 *****************************************************************/

// ****************************************************************************
TRACER_API void __stdcall Tracer_Init ()
{
  hMutex = CreateMutex (NULL,                 // no security attributes
			FALSE,                // initially not owned
			"XWTracerMutex");			// name of mutex

  GetNameStrings ();
}


// ****************************************************************************
TRACER_API void Tracer_Exit ()
{
  RegCloseKey (HKEY_PERFORMANCE_DATA);
  CloseHandle (hMutex);
}



/*****************************************************************
 *                                                               *
 *          Functions to get workstation informations.           *
 *                                                               *
 *****************************************************************/

// ****************************************************************************
TRACER_API BOOL Tracer_GetIpHelp (char* card, char* macAddr)
{
/*
  PIP_ADAPTER_INFO	pAdapterInfo, pAdapt;
  DWORD   		AdapterInfoSize, Err;

  if (!card || !macAddr)
    return FALSE;

  // raz strings
  //
  *card = 0;
  *macAddr = 0;


  AdapterInfoSize = 0;
  if ((Err = GetAdaptersInfo(NULL, &AdapterInfoSize)) != 0) {

    if (Err != ERROR_BUFFER_OVERFLOW)
      return FALSE;
  }

  // Allocate memory from sizing information
  if ((pAdapterInfo = (PIP_ADAPTER_INFO) GlobalAlloc(GPTR, AdapterInfoSize)) == NULL)
    return FALSE;

  // Get actual adapter information
  if ((Err = GetAdaptersInfo(pAdapterInfo, &AdapterInfoSize)) != 0)
    return FALSE;

  pAdapt = pAdapterInfo;


  // There may be several different cards.
  // Let assume one of them is determinant enough...

//	while (pAdapt)
  {
    if (pAdapt->Description)
      strcpy (card, pAdapt->Description);
    
    for (UINT i=0; i<pAdapt->AddressLength; i++) {

      if (!macAddr[0])
	sprintf (macAddr, "%02X", (int)pAdapt->Address[i]);
      else
	sprintf (macAddr, "%s-%02X", macAddr, (int)pAdapt->Address[i]);
      
//			if (i == (pAdapt->AddressLength - 1))
//				logFile << (int)pAdapt->Address[i] << "\n";
//			else
//				logFile << (int)pAdapt->Address[i] << "-";

    }        

    pAdapt = pAdapt->Next;
  }

  GlobalFree (pAdapterInfo);

  return TRUE;
*/
  return FALSE;
}


// ****************************************************************************
TRACER_API void Tracer_GetCpuNb (DWORD *cpuNb)
{
  SYSTEM_INFO SystemInfo;

  GetSystemInfo(&SystemInfo);
  *cpuNb = SystemInfo.dwNumberOfProcessors;
}


/*****************************************************************
 *                                                               *
 * Functions used to navigate through the performance data.      *
 *                                                               *
 *****************************************************************/

// ****************************************************************************
static LARGE_INTEGER Tracer_GetTime100nsSec (Perf& perfStruct)
{
  return perfStruct.PerfData->PerfTime100nSec;
}


// ****************************************************************************
static PPERF_OBJECT_TYPE Tracer_FirstObject (PPERF_DATA_BLOCK PerfData)
{
  return ((PPERF_OBJECT_TYPE)((PBYTE)PerfData + PerfData->HeaderLength));
}


// ****************************************************************************
static PPERF_OBJECT_TYPE Tracer_NextObject (PPERF_OBJECT_TYPE PerfObj)
{
  return ((PPERF_OBJECT_TYPE)((PBYTE)PerfObj + PerfObj->TotalByteLength));
}


// ****************************************************************************
static PPERF_OBJECT_TYPE Tracer_GetObjectByName (Perf& perfStruct, char* objName)
{
  PPERF_OBJECT_TYPE PerfObj;

  if (!objName)
    return NULL;

  PerfObj = Tracer_FirstObject (perfStruct.PerfData);
  if (!PerfObj)
    return NULL;

  
  // Process all objects.
  for (DWORD i = 0; i < perfStruct.PerfData->NumObjectTypes; i++) {

    if (!strcmp (objName, lpNamesArray[PerfObj->ObjectNameTitleIndex]))
      return PerfObj;
    
    PerfObj = Tracer_NextObject (PerfObj);
  }
  return NULL;

} // Tracer_GetObjectByName ()


// ****************************************************************************
static PPERF_INSTANCE_DEFINITION Tracer_FirstInstance (PPERF_OBJECT_TYPE PerfObj)
{
  return ((PPERF_INSTANCE_DEFINITION)((PBYTE)PerfObj + PerfObj->DefinitionLength));
}


// ****************************************************************************
static PPERF_INSTANCE_DEFINITION Tracer_NextInstance (PPERF_INSTANCE_DEFINITION PerfInst)
{
  PPERF_COUNTER_BLOCK PerfCntrBlk;

  PerfCntrBlk = (PPERF_COUNTER_BLOCK)((PBYTE)PerfInst + PerfInst->ByteLength);

  return ((PPERF_INSTANCE_DEFINITION)((PBYTE)PerfCntrBlk + PerfCntrBlk->ByteLength));
}


// ****************************************************************************
static PPERF_INSTANCE_DEFINITION Tracer_GetInstanceByName (PPERF_OBJECT_TYPE PerfObj,
							   LPSTR instName)
{
  PPERF_INSTANCE_DEFINITION PerfInst;
  
  if (!PerfObj || !instName)
    return NULL;

  if (PerfObj->NumInstances <= 0)
    return NULL;

  PerfInst = Tracer_FirstInstance (PerfObj);

  for (DWORD i = 0; i < (DWORD)PerfObj->NumInstances; i++) {

    if (!strcmp (instName, Tracer_GetInstanceName (PerfInst)))
      return PerfInst;
    
    PerfInst = Tracer_NextInstance (PerfInst);
  }

  return NULL;

} // Tracer_GetInstanceByName ()


// ****************************************************************************
static char* Tracer_GetInstanceName (PPERF_INSTANCE_DEFINITION pInst)
{
  if (pInst)
    return (char *)((PBYTE)pInst + pInst->NameOffset);
  else
    return NULL;
}


// ****************************************************************************
static PPERF_COUNTER_DEFINITION Tracer_FirstCounter (PPERF_OBJECT_TYPE PerfObj)
{
  return ((PPERF_COUNTER_DEFINITION) ((PBYTE)PerfObj + PerfObj->HeaderLength));
}


// ****************************************************************************
static PPERF_COUNTER_DEFINITION Tracer_NextCounter (PPERF_COUNTER_DEFINITION PerfCntr)
{
  return ((PPERF_COUNTER_DEFINITION)((PBYTE)PerfCntr + PerfCntr->ByteLength));
}


// ****************************************************************************
static PPERF_COUNTER_DEFINITION Tracer_GetCounterByName (PPERF_OBJECT_TYPE PerfObj,
							 LPSTR instName,
							 LPSTR counterName)
{
  PPERF_INSTANCE_DEFINITION PerfInst;
  PPERF_COUNTER_DEFINITION PerfCntr, CurCntr;
  PPERF_COUNTER_BLOCK PtrToCntr;

  if (!PerfObj || !counterName)
    return NULL;

  PerfCntr = Tracer_FirstCounter (PerfObj);

  if (PerfObj->NumInstances > 0) {

    // Get the first instance.
    PerfInst = Tracer_FirstInstance (PerfObj);

    // Retrieve all instances.
    for (DWORD k = 0; k < (DWORD)PerfObj->NumInstances; k++) {

      if (!strcmp ((LPSTR)instName,
		   (const char*)((PBYTE)PerfInst + PerfInst->NameOffset))) {

	CurCntr = PerfCntr;
	
	// Retrieve all counters.

	for (DWORD j = 0; j < PerfObj->NumCounters; j++) {

	  if (!strcmp (counterName, lpNamesArray[CurCntr->CounterNameTitleIndex]))
	    return CurCntr;
	  
	  CurCntr = Tracer_NextCounter( CurCntr );
	}
      }

      PerfInst = Tracer_NextInstance( PerfInst );
    }
  }
  else {
    PtrToCntr = (PPERF_COUNTER_BLOCK) ((PBYTE)PerfObj + PerfObj->DefinitionLength);

    // Retrieve all counters.

    for (DWORD j = 0; j < PerfObj->NumCounters; j++) {

      if (!strcmp (counterName, lpNamesArray[PerfCntr->CounterNameTitleIndex]))
	return PerfCntr;

      PerfCntr = Tracer_NextCounter (PerfCntr);
    }
  }

  return NULL;

} // Tracer_GetCounterByName ()


// ****************************************************************************
TRACER_API LONG Tracer_Reload (Perf& perfStruct)
{
  return LoadPerfObjects (perfStruct);
}


// ****************************************************************************
// returns : 0 on local host
//           1 on remote host
//           -1 on error
static LONG LoadPerfObjects (Perf& perfStruct)
{
  DWORD BufferSize = TOTALBYTES;
  static char* globalQuery = "Global";
  HKEY hKey = HKEY_PERFORMANCE_DATA;
  bool ret  = 0;
  LONG status = ERROR_SUCCESS;

  // Allocate the buffer for the performance data.

  if (!perfStruct.perfQuery || !*perfStruct.perfQuery)
    perfStruct.perfQuery = globalQuery;

  if (perfStruct.PerfData != NULL)
    free (perfStruct.PerfData);

  perfStruct.PerfData = NULL;
  perfStruct.PerfData = (PPERF_DATA_BLOCK) malloc (BufferSize);

  if ((perfStruct.hostName) && (strcmp (perfStruct.hostName, LOCALHOST) != 0)) {

    status = RegConnectRegistry ((LPTSTR)perfStruct.hostName,
				 HKEY_PERFORMANCE_DATA,
				 &hKey);

    if (status != ERROR_SUCCESS)
      return status;
  }
  else
    hKey = HKEY_PERFORMANCE_DATA;
  
  while (RegQueryValueEx (hKey,
			  perfStruct.perfQuery,
			  NULL,
			  NULL,
			  (LPBYTE) perfStruct.PerfData,
			  &BufferSize ) == ERROR_MORE_DATA ) {

    // Get a buffer that is big enough.
    BufferSize += BYTEINCREMENT;
    perfStruct.PerfData = (PPERF_DATA_BLOCK) realloc (perfStruct.PerfData, BufferSize);
  }

  return ret;

} // LoadPerfObjects ()


/*****************************************************************
 *                                                               *
 * Load the counter and object names from the registry to the    *
 * global variable lpNamesArray.                                 *
 *                                                               *
 *****************************************************************/

static void GetNameStrings( )
{
  LPSTR lpNameStrings = NULL;
  HKEY hKeyPerflib;      // handle to registry key
  HKEY hKeyPerflib009;   // handle to registry key
  DWORD dwMaxValueLen;   // maximum size of key values
  DWORD dwBuffer;        // bytes to allocate for buffers
  DWORD dwBufferSize;    // size of dwBuffer
  LPSTR lpCurrentString; // pointer for enumerating data strings
  DWORD dwCounter;       // current counter index

  // Get the number of Counter items.

  WaitForSingleObject (hMutex, INFINITE);

  try {
    RegCloseKey (HKEY_PERFORMANCE_DATA);

    if (lpNamesArray) {

      free (lpNamesArray);
      lpNamesArray = NULL;
    }

    RegOpenKeyEx( HKEY_LOCAL_MACHINE,
		  "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Perflib",
		  0,
		  KEY_READ,
		  &hKeyPerflib);

    dwBufferSize = sizeof(dwBuffer);
    
    RegQueryValueEx( hKeyPerflib,
		     "Last Counter",
		     NULL,
		     NULL,
		     (LPBYTE) &dwBuffer,
		     &dwBufferSize );

    RegCloseKey( hKeyPerflib );

    // Allocate memory for the names array.
    lpNamesArray = (char**)malloc( (dwBuffer+1) * sizeof(LPSTR) );

    // Open key containing counter and object names.
    
    RegOpenKeyEx( HKEY_LOCAL_MACHINE,
		  "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Perflib\\009",
		  0,
		  KEY_READ,
		  &hKeyPerflib009);
    
    // Get the size of the largest value in the key (Counter or Help).
    
    RegQueryInfoKey( hKeyPerflib009,
		     NULL,
		     NULL,
		     NULL,
		     NULL,
		     NULL,
		     NULL,
		     NULL,
		     NULL,
		     &dwMaxValueLen,
		     NULL,
		     NULL);

    // Allocate memory for the counter and object names.

    dwBuffer = dwMaxValueLen + 1;

    lpNameStrings = (char*)malloc( dwBuffer * sizeof(CHAR) );

    // Read Counter value.

    RegQueryValueEx( hKeyPerflib009,
		     "Counter",
		     NULL,
		     NULL,
		     (unsigned char*)lpNameStrings,
		     &dwBuffer );

    // Load names into an array, by index.

    for (lpCurrentString = lpNameStrings;
	 *lpCurrentString;
	 lpCurrentString += (lstrlen(lpCurrentString)+1)) {

      dwCounter = atol( lpCurrentString );

      lpCurrentString += (lstrlen(lpCurrentString)+1);

      lpNamesArray[dwCounter] = (LPSTR) lpCurrentString;
    }
  }
  catch (...) {
  }

  ReleaseMutex (hMutex);

} // GetNameStrings()


/***************************************************************************\
* Tracer_GetCounterData()
*
* Entry: pointer to object definition and number of counter, must be
*	 an object with no instances
* Exit:  returns a pointer to the data
*
\***************************************************************************/

static PVOID Tracer_GetCounterData (PERF_OBJECT_TYPE *pObjectDef,
				    PERF_COUNTER_DEFINITION *pCounterDef)
{
  PERF_COUNTER_BLOCK *pCtrBlock;
  
  pCtrBlock = (PERF_COUNTER_BLOCK *)((PCHAR)pObjectDef + 
				     pObjectDef->DefinitionLength);
  
  return (PVOID)((PCHAR)pCtrBlock + pCounterDef->CounterOffset);
}


/***************************************************************************\
* Tracer_GetInstanceCounterData()
*
* Entry: pointer to object definition and number of counter, and a pointer
*        to the instance for which the data is to be retrieved
* Exit:  returns a pointer to the data
*
\***************************************************************************/

static PVOID Tracer_GetInstanceCounterData (PERF_OBJECT_TYPE *pObjectDef,
					    PERF_INSTANCE_DEFINITION *pInstanceDef,
					    PERF_COUNTER_DEFINITION *pCounterDef)
{
  PERF_COUNTER_BLOCK *pCtrBlock;
  
  pCtrBlock = (PERF_COUNTER_BLOCK *)((PCHAR)pInstanceDef +
				     pInstanceDef->ByteLength);
  
  return (PVOID)((PCHAR)pCtrBlock + pCounterDef->CounterOffset);
}


// ****************************************************************************
TRACER_API DWORD Tracer_GetMemoryKBAvailable (Perf& perfStruct)
{
  PPERF_OBJECT_TYPE PerfObj = NULL;
  PPERF_COUNTER_DEFINITION PerfCounter = NULL;
  LPSTR counterName;
  DWORD *dwvalue = NULL;
  
  WaitForSingleObject (perfStruct.hmutexperf, INFINITE);

  try {

    PerfObj = Tracer_GetObjectByName (perfStruct, "Memory");

    if (PerfObj != NULL) {
      counterName = "Available KBytes";
      PerfCounter = Tracer_GetCounterByName (PerfObj,
					     "",
					     counterName);
      
      if (PerfCounter) {
	dwvalue = (DWORD*) Tracer_GetCounterData (PerfObj, PerfCounter);
	ReleaseMutex (perfStruct.hmutexperf);
	return *dwvalue;
      }
    }
  }
  catch (...) {
  }

  ReleaseMutex (perfStruct.hmutexperf);
  return (DWORD)-1;
}


// ****************************************************************************
TRACER_API DWORD Tracer_GetMemoryPageFaultsPerSec (Perf& perfStruct)
{
  PPERF_OBJECT_TYPE PerfObj = NULL;
  PPERF_COUNTER_DEFINITION PerfCounter = NULL;
  LPSTR counterName;

  DWORD *dwvalue = NULL;

  DWORD						deltaC;

  WaitForSingleObject (perfStruct.hmutexperf, INFINITE);

  try {

    PerfObj = Tracer_GetObjectByName (perfStruct, "Memory");
    if (PerfObj != NULL) {

      counterName = "Page Faults/sec";
      PerfCounter = Tracer_GetCounterByName (PerfObj,
					     "",
					     counterName);

      if (PerfCounter) {
	
	dwvalue = (DWORD*) Tracer_GetCounterData (PerfObj, PerfCounter);
	deltaC = *dwvalue - perfStruct.PageFaultsPerf.lastValue;
	perfStruct.PageFaultsPerf.lastValue = *dwvalue;
	ReleaseMutex (perfStruct.hmutexperf);
	return deltaC;
      }
    }
  }
  catch (...) {
  }

  ReleaseMutex (perfStruct.hmutexperf);
  return (DWORD)-1;
}


// ****************************************************************************
TRACER_API DWORD Tracer_GetSystemProcesses (Perf& perfStruct)
{
  PPERF_OBJECT_TYPE PerfObj = NULL;
  PPERF_COUNTER_DEFINITION PerfCounter = NULL;

  LPSTR counterName;

  DWORD *dwvalue = NULL;

  WaitForSingleObject (perfStruct.hmutexperf, INFINITE);

  try {
    
    PerfObj = Tracer_GetObjectByName (perfStruct, "System");

    if (PerfObj != NULL) {
      
      counterName = "Processes";
      PerfCounter = Tracer_GetCounterByName (PerfObj,
					     "",
					     counterName);
      
      if (PerfCounter) {
	
	dwvalue = (DWORD*) Tracer_GetCounterData (PerfObj, PerfCounter);
	ReleaseMutex (perfStruct.hmutexperf);
	return *dwvalue;
      }
    }
  }
  catch (...) {
  }

  ReleaseMutex (perfStruct.hmutexperf);
  return (DWORD)-1;
}


// ****************************************************************************
TRACER_API DWORD Tracer_GetSystemThreads (Perf& perfStruct)
{
  PPERF_OBJECT_TYPE PerfObj = NULL;
  PPERF_COUNTER_DEFINITION PerfCounter = NULL;

  LPSTR counterName;

  DWORD *dwvalue = NULL;


  WaitForSingleObject (perfStruct.hmutexperf, INFINITE);

  try {
  
    PerfObj = Tracer_GetObjectByName (perfStruct, "System");

    if (PerfObj != NULL) {

      counterName = "Threads";
      PerfCounter = Tracer_GetCounterByName (PerfObj,
					     "",
					     counterName);

      if (PerfCounter) {

	dwvalue = (DWORD*) Tracer_GetCounterData (PerfObj, PerfCounter);
	ReleaseMutex (perfStruct.hmutexperf);
	return *dwvalue;
      }
    }
  }
  catch (...) {
  }

  ReleaseMutex (perfStruct.hmutexperf);
  return (DWORD)-1;
}


// ****************************************************************************
TRACER_API DWORD Tracer_GetSystemUpTime (Perf& perfStruct)
{
  PPERF_OBJECT_TYPE PerfObj = NULL;
  PPERF_COUNTER_DEFINITION PerfCounter = NULL;

  LPSTR counterName;
  LARGE_INTEGER		curTime = Tracer_GetTime100nsSec (perfStruct);
  LARGE_INTEGER		*dwvalue = NULL;


  WaitForSingleObject (perfStruct.hmutexperf, INFINITE);

  try {

    PerfObj = Tracer_GetObjectByName (perfStruct, "System");

    if (PerfObj != NULL) {

      counterName = "System Up Time";
      PerfCounter = Tracer_GetCounterByName (PerfObj,
					     "",
					     counterName);

      if (PerfCounter) {

	dwvalue = (LARGE_INTEGER*) Tracer_GetCounterData (PerfObj, PerfCounter);
	dwvalue->QuadPart = curTime.QuadPart - dwvalue->QuadPart;
	dwvalue->QuadPart /= (DWORD)1E+7;
	ReleaseMutex (perfStruct.hmutexperf);
	return (DWORD)(dwvalue->QuadPart);
      }
    }
  }
  catch (...) {
  }

  ReleaseMutex (perfStruct.hmutexperf);
  return (DWORD)-1;
}


// ****************************************************************************
TRACER_API DWORD Tracer_GetIPDatagramsPerSec (Perf& perfStruct)
{
  PPERF_OBJECT_TYPE PerfObj = NULL;
  PPERF_COUNTER_DEFINITION PerfCounter = NULL;
  LPSTR counterName;
  DWORD *dwvalue = NULL;
  DWORD deltaC;

  WaitForSingleObject (perfStruct.hmutexperf, INFINITE);

  try {
    PerfObj = Tracer_GetObjectByName (perfStruct, "IP");

    if (PerfObj != NULL) {

      counterName = "Datagrams/sec";
      PerfCounter = Tracer_GetCounterByName (PerfObj,
					     "",
					     counterName);

      if (PerfCounter) {
	dwvalue = (DWORD*) Tracer_GetCounterData (PerfObj, PerfCounter);
	deltaC = *dwvalue - perfStruct.IPPerf.lastValue;
	perfStruct.IPPerf.lastValue = *dwvalue;
	
	ReleaseMutex (perfStruct.hmutexperf);
	return deltaC;
      }
    }
  }
  catch (...) {
  }

  ReleaseMutex (perfStruct.hmutexperf);
  return (DWORD)-1;
}


// ****************************************************************************
TRACER_API DWORD Tracer_GetProcessorTime (Perf& perfStruct)
{
  PPERF_OBJECT_TYPE PerfObj = NULL;
  PPERF_COUNTER_DEFINITION PerfCounter = NULL;
  PPERF_INSTANCE_DEFINITION PerfInst = NULL;
  LPSTR counterName;
  char  InstanceName[20];
  DWORD *dwvalue = NULL;

  LARGE_INTEGER	curTime = Tracer_GetTime100nsSec (perfStruct);
  DWORD deltaT;
  DWORD deltaC;
  DWORD count;
  FLOAT fdeltaT;
  FLOAT fdeltaC;
  FLOAT fraction;
  FLOAT fcount;
  FLOAT freq = 1.0f;      // don't ask why

  WaitForSingleObject (perfStruct.hmutexperf, INFINITE);

  try {
    PerfObj = Tracer_GetObjectByName (perfStruct, "Processor");

    if (PerfObj != NULL) {
      // ********************************
      deltaT = curTime.LowPart - perfStruct.CpuPerf.lastTime.LowPart;
      fdeltaT = (FLOAT)deltaT;
      
      strcpy (InstanceName, "_Total");
      counterName  = "% Processor Time";

      PerfInst = Tracer_GetInstanceByName (PerfObj,
					   InstanceName);
      if (PerfInst) {
	PerfCounter = Tracer_GetCounterByName (PerfObj,
					       InstanceName,
					       counterName);

	if (PerfCounter) {
	  dwvalue = (DWORD*) Tracer_GetInstanceCounterData (PerfObj,
							    PerfInst,
							    PerfCounter);
	  deltaC = *dwvalue - perfStruct.CpuPerf.lastValue;
	  fdeltaC = (FLOAT)deltaC;
	  
	  fraction = fdeltaC / freq;
	  fcount = fraction / fdeltaT;
	  fcount *= 100.0f;
	  fcount = 100.0f - fcount;
	  if (fcount > 100.0f)
	    fcount = 100.0f;
	  if (fcount < 0.0f)
	    fcount = 0.0f;

	  perfStruct.CpuPerf.lastValue = *dwvalue;
	  perfStruct.CpuPerf.lastTime = curTime;

	  count = (DWORD)fcount;
	  ReleaseMutex (perfStruct.hmutexperf);
	  return count;
	}
      }
    }
  }
  catch (...) {
  }

  ReleaseMutex (perfStruct.hmutexperf);
  return (DWORD)-1;
}


// ****************************************************************************
TRACER_API DWORD Tracer_GetProcessorInterruptsPerSec (Perf& perfStruct)
{
  PPERF_OBJECT_TYPE PerfObj = NULL;
  PPERF_COUNTER_DEFINITION PerfCounter = NULL;
  PPERF_INSTANCE_DEFINITION PerfInst = NULL;
  
  LPSTR counterName;
  char  InstanceName[20];
  
  DWORD *dwvalue = NULL;
  
  LARGE_INTEGER		curTime = Tracer_GetTime100nsSec (perfStruct);
  DWORD						deltaT;
  DWORD						deltaC;
  FLOAT						fdeltaT;
  
  WaitForSingleObject (perfStruct.hmutexperf, INFINITE);
  try {
    PerfObj = Tracer_GetObjectByName (perfStruct, "Processor");
    if (PerfObj != NULL) {
      // ********************************
      deltaT = curTime.LowPart - perfStruct.CpuInterruptsPerf.lastTime.LowPart;
      fdeltaT = (FLOAT)deltaT;
      
      strcpy (InstanceName, "_Total");
      counterName  = "Interrupts/sec";
      
      PerfInst = Tracer_GetInstanceByName (PerfObj,
					   InstanceName);
      if (PerfInst) {
	PerfCounter = Tracer_GetCounterByName (PerfObj,
					       InstanceName,
					       counterName);
	
	perfStruct.CpuInterruptsPerf.lastTime = curTime;
	
	if (PerfCounter) {
	  dwvalue = (DWORD*) Tracer_GetInstanceCounterData (PerfObj,
							    PerfInst,
							    PerfCounter);
	  deltaC = *dwvalue - perfStruct.CpuInterruptsPerf.lastValue;
	  
	  perfStruct.CpuInterruptsPerf.lastValue = *dwvalue;
	  perfStruct.CpuInterruptsPerf.lastTime  = curTime;
	  
	  ReleaseMutex (perfStruct.hmutexperf);
	  return deltaC;
	}
      }
    }
  }
  catch (...) {
  }

  ReleaseMutex (perfStruct.hmutexperf);
  return (DWORD)-1;
}


// ****************************************************************************
TRACER_API DWORD Tracer_GetPhysicalDiskTransfertsPerSec (Perf& perfStruct)
{
  PPERF_OBJECT_TYPE PerfObj = NULL;
  PPERF_COUNTER_DEFINITION PerfCounter = NULL;
  PPERF_INSTANCE_DEFINITION PerfInst = NULL;
  
  LPSTR counterName;
  char  InstanceName[20];
  
  DWORD *dwvalue = NULL;
  
  LARGE_INTEGER curTime = Tracer_GetTime100nsSec (perfStruct);
  DWORD deltaC;
  
  WaitForSingleObject (perfStruct.hmutexperf, INFINITE);
  try {
    PerfObj = Tracer_GetObjectByName (perfStruct, "PhysicalDisk");
    if (PerfObj != NULL) {
      // ********************************
      strcpy (InstanceName, "_Total");
      counterName  = "Disk Transfers/sec";

      PerfInst = Tracer_GetInstanceByName (PerfObj,
					   InstanceName);
      if (PerfInst) {
	PerfCounter = Tracer_GetCounterByName (PerfObj,
					       InstanceName,
					       counterName);
	
	if (PerfCounter) {
	  dwvalue = (DWORD*) Tracer_GetInstanceCounterData (PerfObj,
							    PerfInst,
							    PerfCounter);
	  deltaC = *dwvalue - perfStruct.DiskPerf.lastValue;

	  perfStruct.DiskPerf.lastValue = *dwvalue;
	  perfStruct.DiskPerf.lastTime  = curTime;

	  ReleaseMutex (perfStruct.hmutexperf);
	  return deltaC;
	}
      }
    }
  }
  catch (...) {
  }

  ReleaseMutex (perfStruct.hmutexperf);
  return (DWORD)-1;
}


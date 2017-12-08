// dllmain.c

#include <windows.h>

HINSTANCE g_instance = NULL;

BOOL WINAPI DllMain (HINSTANCE hinstDLL,  // handle to DLL module
		     DWORD fdwReason,     // reason for calling function
		     LPVOID lpvReserved   // reserved
		     )
{
  switch( fdwReason )
    {
    case DLL_THREAD_ATTACH:
    case DLL_THREAD_DETACH:
    case DLL_PROCESS_DETACH:

    case DLL_PROCESS_ATTACH:
      g_instance = hinstDLL;
      break;
    }
  return TRUE;
}


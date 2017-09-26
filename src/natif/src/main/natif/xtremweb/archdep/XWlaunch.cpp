// ToDo: Changer le declenchement en mode auto: virer la ref. au screensaver
// et monitorer directement les events clavier (et la charge CPU ?)
// Affichage de XW stopped pas toujours correct...
// long au demarrage
// essayer de virer le passage par java pour Quit



#define STRICT
#define WIN32_LEAN_AND_MEAN
#include <windows.h>
#include <stdlib.h>
#include <stdio.h>
#include <shellapi.h>

#include <jni.h>

#include "XWlaunchRes.h"
#include "XWlaunch.h"


// #ifdef XWSERVER

// #define MAIN_CLASS "xtremweb/dispatcher/Dispatcher"
// #define HC_CLASSPATH "../lib;xtremweb.jar;jcert.jar;jsse.jar,mm.mysql-2.0.2-bin.jar"
// #define WNDCLASSNAME "XWServerWndClass"
// #define WNDNAME "Xtremweb server"
// #define CONFIG_FILE "./xtremweb.server.conf"

// #endif

#ifdef XWWORKER

#define MAIN_CLASS "xtremweb/worker/Worker"
#define HC_CLASSPATH "xtremweb.jar"
#define WNDCLASSNAME "XWWorkerWndClass"
#define WNDNAME "XWHEP worker"
#define CONFIG_FILE "../conf/xtremweb.worker.conf"
#define KEYSTORE "xwhepworker.keys"

#endif

#ifdef XWCLIENT

#define MAIN_CLASS "xtremweb/client/Client"
#define HC_CLASSPATH "xtremweb.jar"
#define WNDCLASSNAME "XWClientWndClass"
#define WNDNAME "XWHEP client"
#define CONFIG_FILE "xtremweb.client.conf"
#define KEYSTORE "xwhepclient.keys"
#endif


#ifndef MAIN_CLASS
#error "Compile what? (use -DXWWORKER, or -DXWCLIENT, or -DXWSERVER)"
#endif


#define MAIN_METHOD "main"
#define MAIN_METHOD_SIGNATURE "([Ljava/lang/String;)V"
//#define JARFILE "XWDisp.jar;mm.mysql-2.0.2-bin.jar"
//
// local variables
//
static HINSTANCE hInstance;
static HWND hScrWindow;
static HMENU popupMenu = NULL;

static NOTIFYICONDATA XWDispNid;
static HICON XWDispIcon;

static char XWstate[200];
  

typedef int (CALLBACK *FCTX)(JavaVM **pvm, JNIEnv **env, void *args);
static FCTX ifn = NULL;


// ****************************************************************************
static void xwExit(int code) {

  //  Shell_NotifyIcon (NIM_DELETE,&XWDispNid);
  DestroyWindow (hScrWindow);

  exit (code);
}


// ------------------------------------------------
// Handler
//
// Console Control Handler
//
// Ignore all attempts to shutdown the application.
//
// Arguments:
//
// dwCtrlType - control event type
//
// Return:
//
// TRUE (ignore event)
// ------------------------------------------------
BOOL Handler (DWORD dwCtrlType) {
  return TRUE;
}


// ****************************************************************************
static LRESULT CALLBACK WindowProc(HWND hwnd,UINT msg,WPARAM wParam,LPARAM lParam) {

  switch (msg) {
  case WM_CREATE:
    return 0;

  case WM_CLOSE:
    DestroyWindow(hwnd);
    break;
 
  case WM_DESTROY:
    PostQuitMessage(0);
    break;

  case XW_ICONNOTIFY: 
    //click icon in status area
    if (lParam==WM_RBUTTONDOWN) {

      if (popupMenu != NULL)
	return 0;

      popupMenu = CreatePopupMenu ();
      if (popupMenu == NULL)
	return 0;

      MENUITEMINFO menuItem;
      POINT pos;

      menuItem.cbSize = sizeof (MENUITEMINFO);
      menuItem.fMask = MIIM_DATA | MIIM_TYPE | MIIM_ID | MIIM_STATE;
      menuItem.fType = MFT_STRING;
      menuItem.fState = MFS_DEFAULT;
      menuItem.wID = IDQUIT;
      menuItem.hSubMenu = NULL;
      menuItem.hbmpChecked = NULL;
      menuItem.hbmpUnchecked = NULL;
      menuItem.dwItemData = 0;
      menuItem.dwTypeData = "Quit";
      menuItem.cch = 4;

      InsertMenuItem (popupMenu, 0, TRUE, &menuItem);

      GetCursorPos (&pos);

      int cmd;
      cmd = (int)TrackPopupMenuEx(popupMenu,
				  TPM_RETURNCMD,
				  pos.x,
				  pos.y,
				  hScrWindow,
				  NULL);
      DestroyMenu (popupMenu);
      popupMenu = NULL;

      switch (cmd) {
      case 0:
	/*
	  char errStr[30];
	  sprintf (errStr, "cant track popup : %li", (long)GetLastError());
	  MessageBox(0, errStr, WNDNAME, MB_OK | MB_ICONWARNING);
	  break;
	*/
      case IDQUIT:
	xwExit (0);
      }
    }

    break;

  default:
    return DefWindowProc(hwnd,msg,wParam,lParam);

  } // switch (msg)

  return 0;

} //WindowProc()


// ****************************************************************************
void NidCreate() {

  XWDispIcon=(HICON)LoadImage(hInstance,MAKEINTRESOURCE(IDI_XWD),IMAGE_ICON,16,16,0);

  XWDispNid.cbSize=sizeof(NOTIFYICONDATA);
  XWDispNid.hWnd=hScrWindow;
  XWDispNid.uID=1234;
  XWDispNid.uFlags=NIF_MESSAGE|NIF_ICON|NIF_TIP;
  XWDispNid.uCallbackMessage=XW_ICONNOTIFY;
  XWDispNid.hIcon=XWDispIcon;
  lstrcpy(XWDispNid.szTip, WNDNAME);

} //NidCreate()



// ****************************************************************************
#define MAX_KEY_LENGTH 255
#define MAX_VALUE_NAME 16383

TCHAR* findLastSubKey(HKEY hKey)  {

  static TCHAR    achKey[MAX_KEY_LENGTH];   // buffer for subkey name
  DWORD    cbName;                   // size of name string 
  TCHAR    achClass[MAX_PATH] = TEXT("");  // buffer for class name 
  DWORD    cchClassName = MAX_PATH;  // size of class string 
  DWORD    cSubKeys=0;               // number of subkeys 
  DWORD    cbMaxSubKey;              // longest subkey size 
  DWORD    cchMaxClass;              // longest class string 
  DWORD    cValues;              // number of values for key 
  DWORD    cchMaxValue;          // longest value name 
  DWORD    cbMaxValueData;       // longest value data 
  DWORD    cbSecurityDescriptor; // size of security descriptor 
  DWORD    dwType = REG_SZ;
  FILETIME ftLastWriteTime;      // last write time 
 
  DWORD i, retCode; 
 
  TCHAR  achValue[MAX_VALUE_NAME]; 
  DWORD cchValue = MAX_VALUE_NAME; 
 
  // Get the class name and the value count. 
  retCode = RegQueryInfoKey(
			    hKey,                    // key handle 
			    achClass,                // buffer for class name 
			    &cchClassName,           // size of class string 
			    NULL,                    // reserved 
			    &cSubKeys,               // number of subkeys 
			    &cbMaxSubKey,            // longest subkey size 
			    &cchMaxClass,            // longest class string 
			    &cValues,                // number of values for this key 
			    &cchMaxValue,            // longest value name 
			    &cbMaxValueData,         // longest value data 
			    &cbSecurityDescriptor,   // security descriptor 
			    &ftLastWriteTime);       // last write time 
 
  // Enumerate the subkeys, until RegEnumKeyEx fails.
    
  if (cSubKeys) {

    for (i=0; i<cSubKeys; i++)  { 
      cbName = MAX_KEY_LENGTH;
      retCode = RegEnumKeyEx(hKey, i,
			     achKey, 
			     &cbName, 
			     NULL, 
			     NULL, 
			     NULL, 
			     &ftLastWriteTime); 
    }
  } 
 
  // Enumerate the key values. 

  if (cValues)  {

    for (i=0, retCode=ERROR_SUCCESS; i<cValues; i++) {

      cchValue = MAX_VALUE_NAME; 
      achValue[0] = '\0'; 
      retCode = RegEnumValue(hKey, i, 
			     achValue, 
			     &cchValue, 
			     NULL, 
			     &dwType,
			     NULL,
			     NULL); 
    }
  }
  return achKey;
}

// ***********************************************************************
HINSTANCE LoadJVM () {

  HINSTANCE hinstance;
  DWORD dwCount = 128, dwType;
  static BYTE DLLdata [128];
  HKEY hKey;
  static char* keyNameLabel = "SOFTWARE\\JavaSoft\\Java Runtime Environment";
  static char keyNametxt[512];
  TCHAR keyName[512] = "SOFTWARE\\JavaSoft\\Java Runtime Environment";
  keyName[ strlen(keyNameLabel) ] = '\0';

  if( RegOpenKeyEx(HKEY_LOCAL_MACHINE,
		   keyName,
		   0,
		   KEY_READ,
		   &hKey) != ERROR_SUCCESS) {

    char errMsg[1024];
    strncpy(errMsg, "Can not find ", 1024);
    strncat(errMsg, keyName, 1024);
    MessageBox(0, errMsg, "XWHEP error", MB_OK | MB_ICONWARNING);
    return NULL;
  }

  TCHAR* foundKey = findLastSubKey(hKey);
  if(foundKey == NULL) {
    char errMsg[1024];
    strncpy(errMsg, "Can not find any subkey in ", 1024);
    strncat(errMsg, keyName, 1024);
    MessageBox(0, errMsg, "XWHEP error", MB_OK | MB_ICONWARNING);
    return NULL;
  }

  //	printf("strlen(%s) = %d\n ", foundKey, strlen(foundKey));

  strncpy(keyNametxt, keyNameLabel, 512);
  //	printf("strlen(%s) = %d\n ", keyNametxt, strlen(keyNametxt));

  strncat(keyNametxt, "\\", 512);
  strncat(keyNametxt, foundKey, 512);
  //	printf("keyNametx = %s\n ", keyNametxt);

  if( RegOpenKeyEx(HKEY_LOCAL_MACHINE,
		   keyNametxt,
		   0,
		   KEY_READ,
		   &hKey) != ERROR_SUCCESS) {

    char errMsg[1024];
    strncpy(errMsg, "Can not find any subkey in ", 1024);
    strncat(errMsg, keyNametxt, 1024);
    MessageBox(0, errMsg, "XWHEP error", MB_OK | MB_ICONWARNING);
    return NULL;
  }

  if (RegQueryValueEx (hKey,
		       "RuntimeLib",
		       NULL,
		       &dwType,
		       DLLdata,
		       &dwCount) != ERROR_SUCCESS) {

    char errMsg[1024];
    strncpy(errMsg, "Can not find subkey \"RuntimeLib\" in ", 1024);
    strncat(errMsg, keyNametxt, 1024);
    MessageBox(0, errMsg, "XWHEP error", MB_OK | MB_ICONWARNING);
    return NULL;
  }
  //	if(dwType == REG_SZ)
  //		printf("%s\\RuntimeLib = %s\n", keyNametxt, DLLdata);
  //	else
  //		printf("%s\\RuntimeLib , type = %d\n", keyNametxt, dwType);

  RegCloseKey (hKey);

  // Once we have the path and name of JVM 1.2/2.0 DLL, we can attempt to
  // load this DLL.

  if ((hinstance = LoadLibrary ((LPCTSTR) DLLdata)) != NULL) {

    // Since the DLL has now been successfully loaded, we need to obtain
    // the entry point address of the JNI_CreateJavaVM function.

    //		printf("IFN = %x\n", ifn);

    ifn = (FCTX)GetProcAddress(hinstance, "JNI_CreateJavaVM");

    //		printf("IFN = %x\n", ifn);

    if (ifn == NULL) {
      FreeLibrary (hinstance);
      return NULL;
    }
  }

  return hinstance;
}


// ****************************************************************************
DWORD WINAPI RunJVM(LPVOID) {

  int i;
  jint ret;
  JNIEnv *env;
  JavaVM *jvm;
  jclass clazz;
  jstring jstr0;
  jstring jstr1;
  jmethodID mid;
  HINSTANCE hinstance;
  JavaVMOption options[2];
  JavaVMInitArgs vm_args;
  jobjectArray str_array;
  char buffer0 [512];//, *psz;
  char buffer1 [512];//, *psz;
  char buffer2 [512];//, *psz;
  //	InvocationFunctions ifn;

  // Prevent application from shutting down due to Ctrl-Break or Ctrl-C
  // keypresses, window close button clicks, user logoff, or system shutdown.

  SetConsoleCtrlHandler ((PHANDLER_ROUTINE) Handler, TRUE);

  // Attempt to load the JVM.

  if ((hinstance = LoadJVM ()) == NULL) {
    return 1;
  }

  if (ifn == NULL) {
    return 1;
  }

  // Initialize JVM arguments.

  vm_args.version = JNI_VERSION_1_4;
  vm_args.nOptions = 1;
  //	if ((psz = getenv ("CLASSPATH")) == 0) psz = ".";

  wsprintf (buffer0, "-Djava.class.path=%s", HC_CLASSPATH);
  options[0].optionString = buffer0;

  wsprintf (buffer1, "-Djavax.net.ssl.trustStore=%s", KEYSTORE);
  options[1].optionString = buffer1;

  vm_args.options = options;

  // Attempt to create an instance of the loaded JVM.


  //	if ((ret = ifn.JNI_CreateJavaVM (&jvm, &env, &vm_args)) < 0) {
  if ((ret = ifn(&jvm, &env, &vm_args)) < 0) {
    char errMsg[1024];
    sprintf(errMsg, "Can't create JVM.  Error: %ld", ret);
    MessageBox(0, errMsg, "XWHEP error", MB_OK | MB_ICONWARNING);
    goto main2;
  }

  str_array = env->NewObjectArray(2,
				  env->FindClass("java/lang/String"),
				  env->NewStringUTF (""));

  if (str_array == NULL) {
    MessageBox(0, "Insufficient memory", "XWHEP error", MB_OK | MB_ICONWARNING);
    goto main1;
  }

  // For each command-line argument ...


  if ((jstr0 = env->NewStringUTF("--xwgui")) == 0) {
    MessageBox(0, "Can not allocate arguments", "XWHEP error", MB_OK | MB_ICONWARNING);
    goto main1;
  }

  //  wsprintf (buffer2, "--xwconfig=%s", CONFIG_FILE);
  if ((jstr1 = env->NewStringUTF("--xwconfig=xtremweb.client.conf")) == 0) {
    MessageBox(0, "Can not allocate arguments", "XWHEP error", MB_OK | MB_ICONWARNING);
    goto main1;
  }

  env->SetObjectArrayElement(str_array, 0, jstr0);
  env->SetObjectArrayElement(str_array, 1, jstr1);


  if ((clazz = env->FindClass (MAIN_CLASS)) == 0) {
    MessageBox(0, "Can not locate java class", "XWHEP error", MB_OK | MB_ICONWARNING);
    goto main1;
  }

  // Attempt to locate the te class's main method.

  if ((mid = env->GetStaticMethodID(clazz, "main",
				    "([Ljava/lang/String;)V")) == 0) {
    MessageBox(0, "Can not locate main method", "XWHEP error", MB_OK | MB_ICONWARNING);
    goto main1;
  }

  // Launch the main method.

  env->CallStaticVoidMethod(clazz, mid, str_array);

  // Check for and display any exception that may have been thrown.

  if (env->ExceptionOccurred()) {
    env->ExceptionDescribe();
    goto main1;
  }

  // Attempt to detach the main thread from the JVM, so that this thread
  // will appear to have exited when the Java application's main method
  // terminates.  All Java monitors held by this thread are released.  All
  // Java threads waiting for this thread to die are notified.

  if (jvm->DetachCurrentThread() != 0)
    MessageBox(0, "Could not detach the main thread from the JVM", "XWHEP error", MB_OK | MB_ICONWARNING);

 main1:

  // Destroy the JVM instance.

  jvm->DestroyJavaVM();

 main2:

  // Release the loaded JVM.

  FreeLibrary (hinstance);
  xwExit (0);

} // RunJVM()


// ****************************************************************************
int WINAPI WinMain(HINSTANCE h,HINSTANCE,LPSTR,int) {

  DWORD ThreadID;
  HANDLE hThread;
  WNDCLASS wc;

  hInstance=h; 

  wc.style         = 0;
  wc.lpfnWndProc   = WindowProc;
  wc.cbClsExtra    = 0;
  wc.cbWndExtra    = 0;
  wc.hInstance     = hInstance;
  wc.hIcon         = NULL;
  wc.hCursor       = NULL;
  wc.hbrBackground = NULL;
  wc.lpszMenuName  = NULL;
  wc.lpszClassName = WNDCLASSNAME;

  //are we alone ?
  if (FindWindow (WNDCLASSNAME, WNDNAME) != NULL) {
    MessageBox (0, "Already started", WNDNAME, MB_OK | MB_ICONWARNING);
    xwExit (1);
  }


  //create main window
  RegisterClass(&wc);
  lstrcpy(XWstate,"Initializing");
 
  hScrWindow = CreateWindow(WNDCLASSNAME,
			    WNDNAME,
			    WS_SYSMENU,
			    0,
			    0,
			    0,
			    0,
			    NULL,
			    NULL,
			    hInstance,
			    NULL);

  if (hScrWindow==NULL) {
    MessageBox (0, "Can't create window", WNDNAME, MB_OK | MB_ICONWARNING);
    xwExit (1);
  }


  //Boot JVM in a new Thread
  hThread=CreateThread(NULL,0,(LPTHREAD_START_ROUTINE)RunJVM,NULL,0,&ThreadID);
  if (hThread==NULL) {
    MessageBox (0, "Can't create JVM thread", WNDNAME, MB_OK | MB_ICONWARNING);
    xwExit (1);
  }

  //load resources
  //  NidCreate();
  //Shell_NotifyIcon(NIM_ADD,&XWDispNid);


  //msg loop...

  MSG msg;
  while (GetMessage(&msg,NULL,0,0)) {
    TranslateMessage(&msg);
    DispatchMessage(&msg);
  }

  return 0;

} //WinMain ()

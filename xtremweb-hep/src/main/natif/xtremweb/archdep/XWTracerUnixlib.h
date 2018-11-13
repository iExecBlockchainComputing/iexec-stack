#ifndef __XWTRACERLIB_H__
#define __XWTRACERLIB_H__
/**
 * Project : XTremWeb
 * File    : XWTracer.h
 *
 * Initial revision : July 2001
 * By               : Anna Lawer
 *
 * New revision : January 2002
 * By           : Oleg Lodygensky
 * e-mail       : lodygens /at\ .in2p3.fr
 */

#define DEFAULT_SAFE_TIMEOUT 750
#define BUFFER_SIZE 4096




typedef struct {
  short cpuNum;
  short cpuSpeed;
  long  memTotal;
  long  swapTotal;
  long  boottime;
  char  kernel[16];
  long time;
  
} nodeConfig;

typedef struct {
  long cpuUser;
  long cpuNice;
  long cpuSystem;
  long cpuIdle;
  long cpuAidle;
  short loadOne;
  short loadFive;
  short loadFifteen;
  short procRun;
  short procTotal;
  long  memFree;
  long  memShared;
  long  memBuffers;
  long  memCached;
  long  swapFree;
  long time;
} nodeState;


char * skipWhitespace (char *p);
char * skipToken (char *p);
int safeRead (int desc, void *ptr, int len, int timeout);
void processCpustate (nodeState* state); 
void processMemstate (nodeState* state); 
void processLoadstate (nodeState* now); 
void collectMeminfo (nodeConfig* config); 
void collectCpuinfo (nodeConfig* config); 
int slurpfile (char * filename ); 
void imprimer();
void keyBordStat (int);
void writeConfig(nodeConfig* config, int effacer);
void writeState(nodeState* state, int effacer);

#endif

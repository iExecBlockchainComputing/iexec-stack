/**
 * Project : XTremWeb
 * File    : XWTracerlib.c
 *
 * Initial revision : July 2001
 * By               : Anna Lawer
 *
 * New revision : January 2002
 * By           : Oleg Lodygensky
 * e-mail       : lodygens /at\ .in2p3.fr
 */

#include <stdio.h>
#include "XWTracerlib.h"
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/time.h>
#include <fcntl.h>
#include <sys/poll.h>
#include <string.h>
#include <errno.h>
#include <unistd.h>
#include <stdlib.h>
#include <time.h>
#include <math.h>      
#include <errno.h>  
#include <ctype.h>
#include <netinet/in.h>

/************************************************************
 * Static variables
 ************************************************************/

const char *valid_tokens[] = {
  "cpu_num",    "cpu_speed", "cpu_user",   "cpu_nice",    "cpu_system",
  "cpu_idle",  "cpu_aidle",  "load_one",    "load_five",   "load_fifteen",
  "proc_run", "proc_total",  "rexec_up",    "ganglia_up",
  "mem_total",  "mem_free",  "mem_shared", "mem_buffers", "mem_cached",
  "swap_total", "swap_free", "clock",    "kernel",  "uptime", "health"
};


/*static int       co = 0;*/
static int       conf_changed = 1;
static nodeState newState;
static char      buffer[BUFFER_SIZE];


/************************************************************
 * External variables
 ************************************************************/
extern char *outputDir;


/************************************************************
 * Technical stuff
 ************************************************************/

/***************************************************************/
static char *getFileName (char *fname)
{
  static char fileName[256];

  if (outputDir == NULL) {
    //    fprintf (stderr, "outputDir not set!\n");
    exit (-1);
  }

  strcpy (fileName, outputDir);
  if (fileName [strlen (fileName) - 1 ] != '/')
    strcat (fileName, "/");

  strcat (fileName, fname);

  return fileName;
}


/***************************************************************/
char * skipWhitespace (char *p)
{
  while (isspace(*p))
    p++;

  return p;
}
 

/***************************************************************/
char * skipToken (char *p)
{
  while (isspace(*p))
    p++;

  while (*p && !isspace(*p))
    p++;

  return p;
}                             

 
/* The safe_read() and safe_write() functions were taken from the
 * C Compatible Compiler Preprocessor (CCCP)
 * Copyright (C) 1986, 87, 89, 92-98, 1999 Free Software Foundation, Inc.
 * Written by Paul Rubin, June 1986
 * Adapted to ANSI C, Richard Stallman, Jan 1987
 * and modified heavily by Matt Massie, Jan 2001
 */ 

/* Read LEN bytes at PTR from descriptor DESC, for file FILENAME,
   retrying if necessary.  If MAX_READ_LEN is defined, read at most
   that bytes at a time.  Return a negative value if an error occurs,
   otherwise return the actual number of bytes read,
   which must be LEN unless end-of-file was reached.  */


/***************************************************************/
int safeRead (int desc, void *ptr, int len, int timeout)
{
 
  int left, rcount, nchars;
  struct pollfd p_fd = {desc, POLLIN, 0};
 
  if ( timeout <= 0 )
    timeout = DEFAULT_SAFE_TIMEOUT;

  left = len;

  while (left > 0) {
    rcount = left;
#ifdef MAX_READ_LEN
    if (rcount > MAX_READ_LEN)
      rcount = MAX_READ_LEN;
#endif

    if ( poll ( &p_fd, 1, timeout ) )
      nchars = read (desc, ptr, rcount);
    else
      break;

    if (nchars < 0)
      {
#ifdef EINTR
	if (errno == EINTR){
	
	  continue;
	}
#endif
	return nchars;
      }
    if (nchars == 0)
      break;
    ptr += nchars;
    left -= nchars;
  }
  return len - left;
}


/* Cautiously read a file */
/***************************************************************/
int slurpfile (char * filename ) 
{
  int  fd, length;

  if ( ( fd = open(filename, O_RDONLY) ) == -1 ){
    perror("slurpfile:open");
    return 0; 
  }
   
  if ( ( length = safeRead( fd, buffer, BUFFER_SIZE-1, 0)) == -1 ){ 
    return 0;
  }
  if(close(fd) != 0)
    perror("warning slurpfile-close failed: ");
  buffer[length]='\0';
   
  return length;
}


/***********************************************************
 * Reading dynamic state : CPU and memory, from /proc
 * This section is linux dependent 
 **********************************************************/
/***************************************************************/
void processCpustate (nodeState* state) 
{
  char *p;
  struct timeval get_time;
  static double cpu_user, cpu_nice, cpu_system, cpu_idle;
  double d;
  double user_jiffies, nice_jiffies, system_jiffies,
    idle_jiffies, relative_jiffies, absolute_jiffies;

  if (! slurpfile ( "/proc/stat" ))
    return;

  /*  printf ("je suis dans stat/Activitylib.c, routine processCpustate\n");*/
  p = skipToken ( buffer );
  d  = strtod( p, &p);
  user_jiffies = d;
  cpu_user     = d;
  
  d            = strtod( p, &p );
  nice_jiffies = d;
  cpu_nice     = d;
  
  d              = strtod( p, &p);
  system_jiffies = d;
  cpu_system     = d;
  
  d            = strtod( p, (char**)NULL);
  idle_jiffies = d;
  cpu_idle     = d;
  
  relative_jiffies = user_jiffies   + nice_jiffies + system_jiffies + idle_jiffies;
  
  absolute_jiffies = newState.cpuUser + newState.cpuNice + newState.cpuSystem + newState.cpuIdle;
  //VARFOR AR DET * 1000 JAG TYCKER ATT DET BORDE VARA * 100 FOR ATT
  //FA PROCENT, NU FAR VI PROMILLE!!!!!!
  //  why this result  *1000 (per mil), it should be *100 (per cent)
  newState.cpuUser = (long)user_jiffies;
  // forrut var fomeln (nice_jiffies / relative_jiffies)*100
  // the formula was (nice_jiffies / relative_jiffies)*100
  newState.cpuNice     = (long)nice_jiffies;
  newState.cpuSystem   = (long)system_jiffies;
  newState.cpuIdle     = (long)idle_jiffies;
  newState.cpuAidle    = (newState.cpuIdle/absolute_jiffies)*1000;   

  if(gettimeofday(&get_time, 0)== 0)
    {
      newState.time = get_time.tv_sec;
    }
  else
    {
      newState.time = 0;
    }
}


/***************************************************************/
void processMemstate (nodeState* state) 
{
  char *p;
  /*  long memFree;
  long memShared;
  long memBuffers;
  long memCached;
  long swapFree;
  */
  if ( ! slurpfile( "/proc/meminfo" ) )
    return;
  
  p = (char *)strstr( buffer, "MemFree:" );
  p = (char *)skipToken(p);
  p = (char *)skipWhitespace(p);

  newState.memFree =  strtod( p, (char **)NULL);
 
  p = (char *)strstr( buffer, "MemShared:" );
  p = (char *)skipToken(p);
  p = (char *)skipWhitespace(p);
  
  newState.memShared = strtod( p, (char **)NULL);
 
  p = (char *)strstr( buffer, "Buffers:" );
  p = (char *)skipToken(p);
  p = (char *)skipWhitespace(p);
  
  newState.memBuffers =  strtod( p, (char **)NULL);
 
  p = (char *)strstr( buffer, "Cached:" );
  p = (char *)skipToken(p);
  p = (char *)skipWhitespace(p);
  
  newState.memCached = strtod( p, (char **)NULL);

  p = (char *)strstr( buffer, "SwapFree:" );
  p = (char *)skipToken(p);
  p = (char *)skipWhitespace(p);
  
  newState.swapFree = strtod( p, (char **)NULL );
}


/***************************************************************/
void processLoadstate (nodeState* now) 
{
  char *p, *endptr;
  /*  short loadOne;
  short loadFive;
  short loadFifteen;
  short procRun;
  short procTotal;
  */
  if ( ! slurpfile ( "/proc/loadavg" ))
    return;
  p = buffer;
 
  newState.loadOne = ( strtod(p, (char **)NULL) * 100.00 );
  p = skipToken(p);
  p = skipWhitespace(p);

  newState.loadFive = ( strtod(p, (char **)NULL) * 100.00 );

  p = skipToken(p);
  p = skipToken(p);
  p = skipToken(p);
  p = skipWhitespace(p);
  newState.loadFifteen =( strtod(p, (char **)NULL) * 100.00 );
 

  p = skipToken(p);
  p = skipWhitespace(p);
  
  newState.procRun     = (short)( strtod(p, &endptr ));
  
  endptr++;  /* Skip the slash */
  newState.procTotal = (int)( strtod(endptr, &p) );
  
 
}


/***********************************************************
 * Reading static state : CPU and memory, from /proc
 * Redundant with info gathered at connecting time by the client ?
 * This section is linux dependent 
 **********************************************************/
/***************************************************************/
void collectMeminfo (nodeConfig* config) 
{
  char *p;

  long memTotal;
  long swapTotal;
  struct timeval get_time;

  if ( ! slurpfile ( "/proc/meminfo" ))
    return;
  
  p = strstr( buffer, "MemTotal:" );
  p = skipToken(p);
  p = skipWhitespace(p);
  
  memTotal  = strtol( p, (char **)NULL, 10 );
 
  if((config->memTotal -memTotal) != 0 ){
    config->memTotal = memTotal;
    conf_changed = 1;
  }
  p = (char *)strstr( buffer, "SwapTotal:" );
  p = (char *)skipToken(p);
  p = (char *)skipWhitespace(p);
  
  swapTotal = strtol( p, (char **)NULL, 10 );  
  
  if((config->swapTotal -swapTotal) != 0){
    config->swapTotal = swapTotal;
    conf_changed = 1;
    
    if(gettimeofday(&get_time, 0)== 0){
      config->time = get_time.tv_sec;
    }
    else{
      config->time = 0;
	
    }
  }
  
}


/***************************************************************/
void collectCpuinfo (nodeConfig* config) 
{
  int len;
  char *p; 
  short cpuNum = 0;
  short cpuSpeed = 0;
  char kernel[16];
  long boottime;

  if ( ! slurpfile ( "/proc/cpuinfo" )) 
    return;
  
  /* Count the number of processors */
  for ( p = buffer ; *p != '\0' ; p++ ){
    if (! strncmp( p, "processor", 9) ){
      cpuNum++;
    } 
  }
  if((cpuNum-config->cpuNum) != 0){
    config->cpuNum = cpuNum;
    conf_changed = 1;  
    
    p = buffer;
    p = (char *)strstr( p, "cpu MHz" );
    p = (char *)strchr( p, ':' );
    p++;
    p = (char *)skipWhitespace(p);

    cpuSpeed = strtol( p, (char **)NULL , 10 );
  
    if((cpuSpeed-config->cpuSpeed) != 0){
      config->cpuSpeed = cpuSpeed;
      conf_changed = 1;
    
    }

    if (! slurpfile ( "/proc/stat" )){
      return;
    }
  
    p = strstr ( buffer, "btime" );
    p = skipToken ( p );
    p = skipWhitespace ( p );
  
    boottime = strtol( p, (char **)NULL, 10 );

    if((config->boottime -boottime) > 1 ||(config->boottime -boottime)<-1 ){
      config->boottime = boottime;  
      conf_changed = 1;
    
    }

    if ( ! (len = slurpfile ("/proc/sys/kernel/osrelease" ))){
      //      fprintf (stderr, "error in slurpfile in collect_node_config");
      return;
    }
    /* Get rid of pesky \n */
    buffer [len-1] = '\0';
    strncpy ( kernel, buffer, 16 );
    if(strcmp(config->kernel, kernel)< 0){
      strncpy ( config->kernel, kernel, 16 );
      conf_changed = 1;
    }
  }
}


static char   consolebuffer[BUFFER_SIZE];
static char   consolebufferold[BUFFER_SIZE];
static char   consolebufferoldmouse[BUFFER_SIZE];

static char *oldkey = "oldinit";
static char *oldmouse ="oldinit";

/***************************************************************/
static void WriteInterrupts(short Mouse, short Keybord, int effacer)
{
  int fd;
  int append = 0;
  short sVal;
  long lVal;
  struct timeval get_time;
  char *fName = getFileName ("console");

  if(effacer == 0)
    append = O_TRUNC;
  else
    append = O_APPEND;

  if ((fd = open(fName,
		 O_WRONLY|append|O_CREAT|O_APPEND,
		 S_IRUSR|S_IWUSR)) == -1) {
    //    fprintf (stderr, "can't open console");
    exit (-1);
  }

  sVal = htons (Keybord);
  write(fd, &sVal, sizeof(short));
  sVal = htons (Keybord);
  write(fd, &sVal, sizeof(short));

  lVal = 0;

  if(gettimeofday(&get_time, 0)== 0)
    lVal = htonl (get_time.tv_sec);

  write(fd, &lVal, sizeof(long));

  //  if(close(fd) != 0)
    //    fprintf (stderr, "keybordStat");
}


/***************************************************************/
void keyBordStat(int effacer){

  /*  int counter = 0;
      int ch;*/
  short Keybord = 0;
  short Mouse = 0;
  char *newkey;
  char *delim = "\n";
  char *newmouse;
  /*  int size;*/

  /*
    printf ("Dans keyBordStat\n");
  */

    if (! slurpfile ( "/proc/interrupts" ))
        printf("slurpfile failed in client keyBordStat");
    else{
   
    if((newkey = strstr(consolebuffer, "1:")) != NULL){
      
      newkey = strtok(newkey, delim);
      
      if(strstr(oldkey, newkey) == NULL){
	 
	//save 1 to signal that the keyboard has been used
	Keybord = 1;
      }
      else{
	 
	//save 0 to signal that the keyboard has not been used
	Keybord = 0;
      }
	  
      if(strcpy(consolebufferold, consolebuffer)== NULL)
	printf("warning keybord monitor doesn't work");
	  
      else{
	 
	if(
	   (oldkey = strstr(consolebufferold, "1:")) == NULL ||
	   (oldkey = strtok(oldkey, delim)) == NULL)
	  printf("warning keyboard monitor doesn't work\n");
	    
      }
    }
  }

  if (! slurpfile ( "/proc/interrupts" ))
    printf("slurpfile failed in client keyBordStat");
  else{
   
    if((newmouse = strstr(consolebuffer, "12:")) != NULL){ 
      newmouse = strtok(newmouse, delim);
      if(strstr(oldmouse, newmouse) == NULL){
	
	//save 1 to signal that the mouse has been used
	Mouse = 1;
      }
      else{ 
	
	//save 0 to signal that the mouse has not been used
	Mouse = 0;
      }
      
      if(strcpy(consolebufferoldmouse, consolebuffer)== NULL)
	printf("warning mouse monitor doesn't work");
      
      else{
	if(
	   (oldmouse = strstr(consolebufferoldmouse, "12:")) == NULL ||
	   (oldmouse = strtok(oldmouse, delim)) == NULL)
	  printf("warning mouse monitor doesn't work");
	
      }
    }
  }
  WriteInterrupts(Mouse, Keybord, effacer);
}


/************************************************************
 * Buffering stat info in temporary files
 * (see documentation)
 ***********************************************************/

/***********************************************************
 * Dynamic state
 * revoir :
 * 1/ les masks par puissances de 2
 * 2/ la structure de controle
 ************************************************************/
void writeState(nodeState* state, int effacer)
{
  char* c;
  int fd;
  int fd2;
  char k[1];
  unsigned short mask =0;
  int test;
  int append;
  short sVal;
  long  lVal;
  static int sizeOfLong  = sizeof (long);
  static int sizeOfShort = sizeof (short);
  char *fName;

  k[0] = '\n';

  //  c = "jag har ej oppnat filen";
  c = "I did not open the file";

  if(effacer == 0)
    append = O_TRUNC;
  else
    append = O_APPEND;


  fName = getFileName ("sta");
  fd  = open(fName,  append | O_WRONLY|O_CREAT,S_IRUSR|S_IWUSR);
  fName = getFileName ("mask");
  fd2 = open(fName, append | O_WRONLY|O_CREAT,S_IRUSR|S_IWUSR);

  if ((fd == -1) || (fd2 == -1)) {
    perror("can't open sta or mask");
    exit (-1);
  }

  if(effacer == 1){
    /*    printf("ok\n");*/
    if(state->cpuUser-newState.cpuUser != 0){
      mask = pow(2,0);
      lVal = htonl (newState.cpuUser);
      write(fd, &lVal, sizeOfLong);
      /* printf("user %ld\n", newState.cpuUser); */
      state->cpuUser=newState.cpuUser;
    }
    if(state->cpuNice- newState.cpuNice != 0){
      mask = pow(2,1)+mask;
      lVal = htonl (newState.cpuNice);
      write(fd, &lVal, sizeOfLong);
      // printf("nice %ld\n",newState.cpuNice);
      state->cpuNice= newState.cpuNice;
    }
    if(state->cpuSystem-newState.cpuSystem != 0){
      mask = pow(2,2)+mask;
      lVal = htonl (newState.cpuSystem);
      write(fd, &lVal, sizeOfLong);
      // printf("system %ld\n",newState.cpuSystem);
      state->cpuSystem=newState.cpuSystem;
    }
    if(state->cpuIdle- newState.cpuIdle != 0){
      mask = pow(2,3)+mask;
      // printf("idle %ld\n",newState.cpuIdle);
      lVal = htonl (newState.cpuIdle);
      write(fd,  &lVal, sizeOfLong);
      state->cpuIdle= newState.cpuIdle;
    }
    if(state->cpuAidle- newState.cpuAidle != 0){
      mask = pow(2,4)+mask;
      // printf("Aidle %ld\n",newState.cpuAidle);
      lVal = htonl (newState.cpuAidle);
      write(fd,  &lVal, sizeOfLong);
      state->cpuAidle= newState.cpuAidle;
    }
    if(state->loadOne- newState.loadOne != 0){
      mask = pow(2,5)+mask;
      // printf("loadOne %hd\n",newState.loadOne);
      sVal = htons (newState.loadOne);
      write(fd,  &sVal, sizeOfShort);
      state->loadOne= newState.loadOne;
    }
    if(state->loadFive- newState.loadFive != 0){
      mask = pow(2,6)+mask;
      sVal = htons (newState.loadFive);
      write(fd, &sVal, sizeOfShort);
      state->loadFive= newState.loadFive;
    }
    if(state->loadFifteen- newState.loadFifteen != 0){
      mask = pow(2,7)+mask;
      //printf("loadFive %hd\n",newState.loadFive);
      sVal = htons (newState.loadFifteen);
      write(fd,  &sVal, sizeOfShort);
      state->loadFifteen= newState.loadFifteen;
    }
    if(state->procRun- newState.procRun != 0){
      mask = pow(2,8)+mask;
      // printf("procRun %hd\n",newState.procRun);
      sVal = htons (newState.procRun);
      write(fd,  &sVal, sizeOfShort);
      state->procRun= newState.procRun;
    }
    if(state->procTotal- newState.procTotal != 0){
      mask = pow(2,9)+mask;
      //printf("procTotal %hd\n",newState.procTotal);
      sVal = htons (newState.procTotal);
      write(fd,  &sVal, sizeOfShort);
      state->procTotal= newState.procTotal;
    }
    if(state->memFree- newState.memFree != 0){
      mask = pow(2,10)+mask;
      // printf("memFree %hd\n",newState.memFree);
      lVal = htonl (newState.memFree);
      write(fd,  &lVal, sizeOfLong);
      state->memFree= newState.memFree;
    }
    if(state->memShared- newState.memShared != 0){
      mask = pow(2,11)+mask;
      // printf("memShared   %hd\n",newState.memShared);
      lVal = htonl (newState.memShared);
      write(fd,  &lVal, sizeOfLong);
      state->memShared= newState.memShared;
    }
    if(state->memBuffers- newState.memBuffers != 0){
      mask = pow(2,12)+mask;
      // printf("memBuffers %hd\n",newState.memBuffers);
      lVal = htonl (newState.memBuffers);
      write(fd,  &lVal, sizeOfLong);
      state->memBuffers= newState.memBuffers;
    }
    if(state->memCached- newState.memCached != 0){
      mask = pow(2,13)+mask;
      // printf("memCached %hd\n",newState.memCached);
      lVal = htonl (newState.memCached);
      write(fd,  &lVal, sizeOfLong);
      state->memCached= newState.memCached;
    }
    if(state->swapFree- newState.swapFree != 0){
      mask = pow(2,14)+mask;
      // printf("swapFree %hd\n",newState.swapFree);
      lVal = htonl (newState.swapFree);
      write(fd,  &lVal, sizeOfLong);
      state->swapFree= newState.swapFree;
    }
    mask = pow(2,15) + mask;
    test = (int)pow(2, 15);
    // printf("KKKKKKKKKKKKKKKKKKKKK%i\n", mask);
    lVal = htonl (newState.time);
    write(fd, &lVal, sizeOfLong);
    state->time = newState.time;
    // printf("time %hd\n",newState.time);
    
    //printf ("mask = %02X\n", mask);
    sVal = htons (mask);
    write(fd2, &sVal, sizeOfShort);
    //    printf("HAR JAG RAKNAT FEL TRO?? %hd \n", mask);
    //    printf("is count wrong?? %hd \n", mask);
  }

  else {

    mask = pow(2,0);
    lVal = htonl (newState.cpuUser);
    write(fd, &lVal, sizeOfLong);

    state->cpuUser=newState.cpuUser; 
    mask = pow(2,1)+mask;
    lVal = htonl (newState.cpuNice);
    write(fd, &lVal, sizeOfLong);

    state->cpuNice= newState.cpuNice;
    mask = pow(2,2)+mask;
    lVal = htonl (newState.cpuSystem);
    write(fd, &lVal, sizeOfLong);

    state->cpuSystem=newState.cpuSystem;
    mask = pow(2,3)+mask;
    lVal = htonl (newState.cpuIdle);
    write(fd,  &lVal, sizeOfLong);

    state->cpuIdle= newState.cpuIdle;
    mask = pow(2,4)+mask;
    lVal = htonl (newState.cpuAidle);
    write(fd, &lVal, sizeOfLong);

    state->cpuAidle= newState.cpuAidle;
    mask = pow(2,5)+mask;
    sVal = htons (newState.loadOne);
    write(fd,  &sVal, sizeOfShort);

    state->loadOne= newState.loadOne;
    mask = pow(2,6)+mask;
    sVal = htons (newState.loadFive);
    write(fd, &sVal, sizeOfShort);

    state->loadFive= newState.loadFive;
    mask = pow(2,7)+mask;
    sVal = htons (newState.loadFifteen);
    write(fd, &sVal, sizeOfShort);

    state->loadFifteen= newState.loadFifteen;
    mask = pow(2,8)+mask;
    sVal = htons (newState.procRun);
    write(fd, &sVal, sizeOfShort);

    state->procRun= newState.procRun;
    mask = pow(2,9)+mask;
    sVal = htons (newState.procTotal);
    write(fd, &sVal, sizeOfShort);

    state->procTotal= newState.procTotal;
    mask = pow(2,10)+mask;
    lVal = htonl (newState.memFree);
    write(fd, &lVal, sizeOfLong);

    state->memFree= newState.memFree;
    mask = pow(2,11)+mask;
    lVal = htonl (newState.memShared);
    write(fd, &lVal, sizeOfLong);

    state->memShared= newState.memShared;
    mask = pow(2,12)+mask;
    lVal = htonl (newState.memBuffers);
    write(fd, &lVal, sizeOfLong);

    state->memBuffers= newState.memBuffers;
    mask = pow(2,13)+mask;   
    lVal = htonl (newState.memCached);
    write(fd, &lVal, sizeOfLong);

    state->memCached= newState.memCached;
    mask = pow(2,14)+mask;
    lVal = htonl (newState.swapFree);
    write(fd, &lVal, sizeOfLong);

    state->swapFree= newState.swapFree;
    mask = pow(2,15) + mask;
    test = (int)pow(2, 15);
    lVal = htonl (newState.time);
    write(fd, &lVal, sizeOfLong);

    state->time = newState.time;

    //printf ("mask = %02X\n", mask);
    sVal = htons (mask);
    write(fd2, &sVal, sizeOfShort);
  }

  if(close(fd2) != 0)
    perror("Warning writeMask close failure; ");

  if(close(fd) != 0)
    perror("Warning writeStat close failure; ");
}


/*************************************************************
 * Static state
 **************************************************************/

void writeConfig(nodeConfig* config, int effacer)
{
  /*  char* c;*/
  int fd;
  /*  long k;*/
  int append;
  long lVal;
  short sVal;
  static int sizeOfLong  = sizeof (long);
  static int sizeOfShort = sizeof (short);
  char *fName;

  if(conf_changed != 0  || effacer != 0){
    
    if(effacer == 0)
      append = O_TRUNC;
    else
      append = O_APPEND;

    fName = getFileName ("config");
    fd = open(fName,
	      append | O_WRONLY|O_CREAT|O_APPEND,S_IRUSR|S_IWUSR);
    if (fd == -1) {
      perror("can't open config");
      return;
    }


    sVal = htons (config->cpuNum);
    write(fd, &sVal, sizeOfShort);

    sVal = htons (config->cpuSpeed);
    write(fd, &sVal, sizeOfShort);

    lVal = htonl (config->memTotal);
    write(fd, &lVal, sizeOfLong);

    lVal = htonl (config->swapTotal);
    write(fd, &lVal, sizeOfLong);

    lVal = htonl (config->boottime);
    write(fd, &lVal, sizeOfLong);

    write(fd, &config->kernel, 16);

    sVal = htons (config->time);
    write(fd, &lVal, sizeOfLong);

    if(close(fd) != 0)
      perror("Warning writeConfig close failure; ");

    conf_changed = 0;
  }
}

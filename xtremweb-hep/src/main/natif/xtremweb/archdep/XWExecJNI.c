#include "XWExecJNI.h"
#include <errno.h>
#include <fcntl.h>
#include <jni.h>
#include <unistd.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/wait.h>

extern int errno;

JNIEXPORT jboolean JNICALL Java_xtremweb_archdep_XWExecJNI_jni_1kill (JNIEnv * env, 
						     jobject obj , 
						     jint pid,
						     jboolean isSuspended)
{
  int ret_pid ;
  int status;
  pid_t gid;

  gid = getpgid(pid);

  if(isSuspended)
    killpg(gid, SIGCONT);
  killpg(gid, SIGTERM);
  ret_pid = waitpid(pid, &status, 0);
  while(!waitpid(-1* gid, &status, WNOHANG));
  return (ret_pid == pid) ? JNI_TRUE : JNI_FALSE;
}


JNIEXPORT jint JNICALL Java_xtremweb_archdep_XWExecJNI_jni_1waitFor (JNIEnv * env, 
						       jobject obj, 
						       jint pid)
{
  int status = 0;
  int ret_pid = 0;

  ret_pid = waitpid(pid, &status, WUNTRACED);
  while (!WIFEXITED(status)) {
    ret_pid = waitpid(pid, &status, 0);
  }
  return (WIFEXITED(status)) ? WEXITSTATUS(status) : -1;
}

JNIEXPORT jboolean JNICALL Java_xtremweb_archdep_XWExecJNI_jni_1suspend  (JNIEnv * env, 
							 jobject obj, 
							 jint pid) 
{
  int ret, gid;
  gid = getpgid(pid);
  ret = killpg(gid, SIGSTOP);
  return (ret==0) ? JNI_TRUE : JNI_FALSE;
}


JNIEXPORT jboolean JNICALL Java_xtremweb_archdep_XWExecJNI_jni_1activate (JNIEnv * env, 
							 jobject obj, 
							 jint pid) 
{
  int ret, gid;
  gid = getpgid(pid);
  ret = killpg(gid, SIGCONT);
  return (ret==0) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jint JNICALL Java_xtremweb_archdep_XWExecJNI_jni_1exec  (JNIEnv * env, 
						  jobject obj, 
						  jobjectArray array,
						  jstring stdin, jstring stdout,
						  jstring stderr, jstring workingDir) 
{
    int argc, i, size_buff;
    int pid, father_pid;
    jstring jstr ;
    char ** argv;
    int fd, status;
    char* in;
    char* out;
    char* err;
    char* buff;
    char* cwd;
    jboolean isCopy;
    father_pid = getpid();
 
    cwd = NULL;
    if(workingDir) {
      buff = (char*) (*env)->GetStringUTFChars(env, workingDir, &isCopy);
      if(isCopy == JNI_TRUE) {
	size_buff = strlen(buff);
	if(size_buff) {
	  cwd = malloc(sizeof(char) * size_buff);
	  strcpy(cwd, buff);
	}
	(*env)->ReleaseStringUTFChars(env, workingDir, buff);
      }
    }

    in = NULL;
    if(stdin) {
      buff = (char*) (*env)->GetStringUTFChars(env, stdin, &isCopy);
      if(isCopy == JNI_TRUE) {
	size_buff = strlen(buff);
	if(size_buff) {
	  in = malloc(sizeof(char) * size_buff);
	  strncpy(in, buff, sizeof(char) * size_buff);
	}
	(*env)->ReleaseStringUTFChars(env, stdin, buff);
      }
    }
    

    out = NULL;
    if(stdout) {
      buff = (char*) (*env)->GetStringUTFChars(env, stdout, &isCopy);
      if(isCopy == JNI_TRUE) {
	size_buff = strlen(buff);
	if(size_buff) {
	  out = malloc(sizeof(char) * size_buff);
	  strcpy(out, buff);
	}
	(*env)->ReleaseStringUTFChars(env, stdout, buff);
      }
    }

    err = NULL;
    if(stderr) {
      buff = (char*) (*env)->GetStringUTFChars(env, stderr, &isCopy);
      if(isCopy == JNI_TRUE) {
	size_buff = strlen(buff);
	if(size_buff) {
	  err = malloc(sizeof(char) * (1+size_buff));
	  strcpy(err, buff);
	}
	(*env)->ReleaseStringUTFChars(env, stderr, buff);
      }
    }
    
    argc = (*env)->GetArrayLength(env, array);
    argv = (char**) malloc(sizeof(char*) * argc);

    for(i=0 ; i < argc ; i++) {
      jstr = (*env)->GetObjectArrayElement(env, array, i);
      buff = (char*) (*env)->GetStringUTFChars(env, jstr, &isCopy);
      if(isCopy == JNI_TRUE) {
	size_buff = strlen(buff);
	argv[i] = malloc (size_buff * sizeof(char));
	strcpy(argv[i], buff);
	argv[i][size_buff] = '\0';
	(*env)->ReleaseStringUTFChars(env, jstr, buff);
      }
      //      (*env)->ReleaseObjectArrayElement(env, array, jstr, i);
    }
    argv[i] = NULL;

    if((pid = fork()) == 0) {
      

      //Create a new session of proceses
      pid = setsid();
      if(cwd != NULL) {
	chdir(cwd);
      }

      //Redirect stdin to the specified file or /dev/null (if the file is not defined)
      if(in == NULL)
	fd = open("/dev/null", O_RDONLY);
      else {
	fd = open(in, O_RDONLY);      
      }
      if(fd != -1) {
	dup2(fd, 0);
      }
      
      //Redirect stdout to the specified file 
      if (out != NULL) {
	fd = open(out, O_WRONLY|O_TRUNC|O_CREAT, 0666);
	if(fd != -1) {
	  dup2(fd, 1);
	}
      }
      
      //Redirect stderr to the specified file
      if(err != NULL) {
	fd = open(err, O_WRONLY|O_TRUNC|O_CREAT, 0666);
	if(fd != -1) {
	  dup2(fd, 2);
	}
      }
      kill(father_pid, SIGCHLD);

      printf("XtremWeb JNI is going to exec %s\n", argv[0]);

      execvp(argv[0], &argv[0]);
      exit(200);
    }

    wait(&status);

    return pid;
}

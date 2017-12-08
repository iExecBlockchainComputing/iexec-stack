/**
 * Date    : Mar 25th, 2005
 * Project : RPCXW / RPCXW-C
 * File    : portmapper.c
 *
 * This aims to retreive a sun RPC server port, providing its (prog num, version num)
 *
 * @author <a href="mailto:lodygens_a_lal.in2p3.fr">Oleg Lodygensky</a>
 * @version
 */

#include <stdio.h>
#include <rpc/rpc.h>
#include <unistd.h>
#include "PortMapper.h"


static int getport (int prog, int version, int protocol) {

	struct sockaddr_in addr;
	/*
	u_short port;
	*/

	addr.sin_port = 0;
  addr.sin_addr.s_addr = INADDR_ANY;
  addr.sin_family = AF_INET;

	return pmap_getport (&addr, prog, version, protocol);
}


JNIEXPORT jint JNICALL Java_xtremweb_archdep_PortMapper_getudpport
  (JNIEnv *env, jobject obj, jint prog, jint version) {
	return (jint)getport (prog, version, IPPROTO_UDP);
}



JNIEXPORT jint JNICALL Java_xtremweb_archdep_PortMapper_gettcpport
  (JNIEnv *env, jobject obj, jint prog, jint version) {
	return (jint)getport (prog, version, IPPROTO_TCP);
}

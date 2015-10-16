#
# Copyrights     : CNRS
# Author         : Oleg Lodygensky
# Acknowledgment : XtremWeb-HEP is based on XtremWeb 1.8.0 by inria : http://www.xtremweb.net/
# Web            : http://www.xtremweb-hep.org
# 
#      This file is part of XtremWeb-HEP.
#
#    XtremWeb-HEP is free software: you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    XtremWeb-HEP is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License
#    along with XtremWeb-HEP.  If not, see <http://www.gnu.org/licenses/>.
#

#
# Modified: Oct 21st, 2014
# -1- compiler/linker command line option "-mno-cygwin" not supported any more by cygwin/gcc
# -2- XWNotify is not implemented for long
#

SHELL=/bin/sh


# XtremWeb -- jni classes
#
# GNU Makefile to build jni libs in 'src/natif'


COPY = cp
MKDIR = mkdir
FS = /
CC = $(CROSS)gcc
DLLWRAP = $(CROSS)dllwrap
WINDRES = $(CROSS)windres
#LD = $(CROSS)ld

ifeq ("$(TARGET_JAVA_HOME)", "")
  TARGET_JAVA_HOME := $(JAVA_HOME)
endif

ifeq ("$(TARGET)", "Linux")
  ALL = XWUtil.jni XwTracer.jni PortMapper.jni
  REASON="LINUX"
  CPPFLAGS_MD = -I'$(TARGET_JAVA_HOME)$(FS)include$(FS)linux'
endif

ifeq ("$(TARGET)", "SunOS")
  ALL = XWUtil.jni XwTracer.jni
  CPPFLAGS_MD = -I'$(TARGET_JAVA_HOME)$(FS)include$(FS)solaris'
  LDFLAGS_MD = -L'$(TARGET_JAVA_HOME)$(FS)jre$(FS)lib$(FS)sparc'
  REASON="LINUX"
endif

ifeq ("$(TARGET)", "FreeBSD")
  ALL = XWUtil.jni
  CPPFLAGS_MD = -I'$(TARGET_JAVA_HOME)$(FS)include$(FS)freebsd'
  REASON="LINUX"
endif

ifeq ("$(TARGET)","Mac OS X")
  ifeq ("$(CROSS)", "")
    CC=cc
  endif
  ifeq ("$(JAVA_HOME)", "")
    if test -d /Library/Java/Home/ ; then  JAVA_HOME = "/Library/Java/Home" ; fi
    if test -d /System/Library/FrameWorks/JavaVM.framework/Home ; then JAVA_HOME = "/System/Library/FrameWorks/JavaVM.framework/Home" ; fi
    TARGET_JAVA_HOME = "$(JAVA_HOME)"
  endif
#  ifeq ("$(JAVA_HOME)", "")
#	@echo "Can't determine JAVA_HOME"
#	exit 1
#  endif
  JAVA_HEADERS_10_8 = /Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX10.8.sdk/System/Library/Frameworks/JavaVM.framework/Headers/
  JAVA_HEADERS_10_9 = /Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX10.9.sdk/System/Library/Frameworks/JavaVM.framework/Headers/
  JAVA_HEADERS_10_10 = /System/Library/Frameworks/JavaVM.framework/Versions/Current/Headers/
  JAVA_HEADERS_10_11 = ${JAVA_HOME}/include
  JAVA_HEADERS = $(JAVA_HEADERS_10_10)
  if ( test -d $(JAVA_HOME)/../Headers ) ; then  JAVA_HEADERS = $(JAVA_HOME)/../Headers ; fi
  if ( test -d $(JAVA_HEADERS_10_8)    ) ; then JAVA_HEADERS = $(JAVA_HEADERS_10_8) ; fi
  if ( test -d $(JAVA_HEADERS_10_9)    ) ; then JAVA_HEADERS = $(JAVA_HEADERS_10_9) ; fi
  if ( test -d $(JAVA_HEADERS_10_10)   ) ; then JAVA_HEADERS = $(JAVA_HEADERS_10_10) ; fi
  if ( test -d $(JAVA_HEADERS_10_11)   ) ; then JAVA_HEADERS = $(JAVA_HEADERS_10_11) ; fi
#  ifeq ("$(JAVA_HEADERS)", "")
#	@echo "Can't determine JAVA_HEADERS"
#	exit 1
#  endif
  ALL = XWUtil.jni
  CPPFLAGS_MD = -no-cpp-precomp -I'$(TARGET_JAVA_HOME)$(FS)include$(FS)darwin'
  
  LDFLAGS_MD  = -framework CoreServices -framework JavaVM
  REASON="MACOS"
endif

ifneq (,$(findstring Windows,$(TARGET)))
#  ALL =  XWNotify.jni  XWUtil.jni XwTracer.jni XWInterrupts.jni XWWorker.exe XWClient.exe
  ALL =  XWUtil.jni XwTracer.jni XWInterrupts.jni XWWorker.exe XWClient.exe
#  CPPFLAGS_MD = -mno-cygwin -I'$(TARGET_JAVA_HOME)$(FS)include$(FS)win32'
  CPPFLAGS_MD = -I'$(TARGET_JAVA_HOME)$(FS)include$(FS)win32'
# uncomment this to debug problems when resolving symbols 
# LDFLAGS_MD = --no-demangle -lkernel32
#  LDFLAGS_MD = -mno-cygwin -lkernel32
  LDFLAGS_MD = -lkernel32
#  DLLFLAGS = -mno-cygwin --driver-name g++ --add-stdcall-alias --enable-stdcall-fixup
  DLLFLAGS = --driver-name g++ --add-stdcall-alias --enable-stdcall-fixup
  REASON="WIN32"
endif

ifeq ("$(ALL)", "")
  REASON="All not set"
  ALL=usage
endif

#
# CPU type
#
ifeq ("$(XWARCH)", "ix86")
	CPUTYPE = -m32
endif
ifeq ("$(XWARCH)", "x86_64")
	CPUTYPE = -m64
endif
ifeq ("$(XWARCH)", "amd64")
	CPUTYPE = -fPIC
endif

# compilation flags
CPPFLAGS = $(CPPFLAGS_MD) $(CPUTYPE) -I'$(TARGET_JAVA_HOME)$(FS)include'
CFLAGS = $(CFLAGS_MD) $(CPUTYPE) -g -Wall $(CPPFLAGS)
LDFLAGS = $(LDFLAGS_MD) $(CPUTYPE)  -L'$(TARGET_JAVA_HOME)$(FS)lib'


BUILD    = ..$(FS)..$(FS)build
CLASSDIR = $(BUILD)$(FS)classes
INSTALLDIR  = $(BUILD)$(FS)dist$(FS)lib
JNIDIR = $(CLASSDIR)$(FS)jni
OBJDIR = $(CLASSDIR)$(FS)obj$(FS)$(XWSUFFIX)
_DUMMY := $(shell $(MKDIR) $(OBJDIR))

usage:
	@echo "Some parameters are missing"
	@echo "OS         =     $(OS)     # build operating system"
	@echo "TARGET     =     $(TARGET) # target operating system" 
	@echo "CC         =     $(CC)	  # C compiler"
	@echo "CROSS	  =     $(CROSS)  # cross prefix"
	@echo "TARGET_JAVA_HOME	 =     $(TARGET_JAVA_HOME)"
	@echo "CLASSDIR   =     $(CLASSDIR) # where to find xtremweb .class"
	@echo "INSTALLDIR =     $(INSTALLDIR)"
	@echo "REASON     =     $(REASON)"
	@echo "ALL        =     $(ALL)"

install: $(ALL:%=$(JNIDIR)$(FS)%.$(XWVERSION).$(XWSUFFIX)) 

$(OBJDIR):
	$(MKDIR) $(OBJDIR)

$(JNIDIR)$(FS)%.$(XWVERSION).$(XWSUFFIX): $(OBJDIR)$(FS)%
	$(COPY) $< $@


#### Object files
$(OBJDIR)$(FS)PortMapper.o: PortMapper.c  # XWUtilImpl.h
$(OBJDIR)$(FS)XWUtilUnix.o: XWUtilUnix.c  # XWUtilImpl.h
$(OBJDIR)$(FS)XWUtilMacOSX.o: XWUtilMacOSX.c # XWUtilImpl.h
$(OBJDIR)$(FS)MacSaver.o: MacSaver.c # MacSaver.h
$(OBJDIR)$(FS)XWTracerUnixImpl.o:  XWTracerUnixImpl.c # XWTracerImpl.h
$(OBJDIR)$(FS)XWUtilWin32.o: XWUtilWin32.c # XWUtilImpl.h
$(OBJDIR)$(FS)XWTracerWin32lib.o: XWTracerWin32lib.cpp XWTracerWin32lib.h
$(OBJDIR)$(FS)XWTracerWin32Impl.o: XWTracerWin32Impl.cpp XWTracerWin32lib.h # XWTracerImpl.h
$(OBJDIR)$(FS)XWInterruptsWin32.o: XWInterruptsWin32.c # XWInterruptsImpl.h
$(OBJDIR)$(FS)XWWorker.o: XWlaunch.cpp XWlaunch.h XWlaunchRes.h
	$(CC) -c $(CFLAGS) -DXWWORKER -o $@ $<
$(OBJDIR)$(FS)XWClient.o: XWlaunch.cpp XWlaunch.h XWlaunchRes.h
	$(CC) -c $(CFLAGS) -DXWCLIENT -o $@ $<
$(OBJDIR)$(FS)XWServer.o: XWlaunch.cpp XWlaunch.h XWlaunchRes.h
	$(CC) -c $(CFLAGS) -DXWSERVER -o $@ $<
$(OBJDIR)$(FS)XWlaunch.o: XWlaunch.cpp XWlaunch.h XWlaunchRes.h
$(OBJDIR)$(FS)WinSaver.o: WinSaver.c # WinSaver.h
$(OBJDIR)$(FS)win_idle_tracker.o: win_idle_tracker.cpp # WinSaver.h
#$(OBJDIR)$(FS)XWNotifyWin32.o: XWNotifyWin32.cpp #XWNotifyImpl.h

#### Mac OS X Libraries
ifeq ("$(TARGET)","Mac OS X")
$(OBJDIR)$(FS)%.jni:
	$(CC) -bundle $(CFLAGS) $(LDFLAGS) -o $@ $^
#$(OBJDIR)$(FS)XWUtil.jni: $(OBJDIR)$(FS)XWUtilFreeBSD.o $(OBJDIR)$(FS)MacSaver.o
$(OBJDIR)$(FS)XWUtil.jni: $(OBJDIR)$(FS)XWUtilFreeBSD.o
#$(OBJDIR)$(FS)XWUtil.jni: $(OBJDIR)$(FS)XWUtilMacOSX.o
endif

#### Linux Libraries
ifeq ("$(TARGET)", "Linux")
$(OBJDIR)$(FS)%.jni:
	$(CC) -shared $(LDFLAGS) -o $@ $^ 
$(OBJDIR)$(FS)XWUtil.jni: $(OBJDIR)$(FS)XWUtilUnix.o
$(OBJDIR)$(FS)PortMapper.jni: $(OBJDIR)$(FS)PortMapper.o
$(OBJDIR)$(FS)XwTracer.jni: $(OBJDIR)$(FS)XWTracerUnixlib.o $(OBJDIR)$(FS)XWTracerUnixImpl.o 
endif

#### FreeBSD Libraries
ifeq ("$(TARGET)", "FreeBSD")
$(OBJDIR)$(FS)%.jni:
	$(CC) -shared $(CFLAGS) $(LDFLAGS) -o $@ $^
$(OBJDIR)$(FS)XWUtil.jni: $(OBJDIR)$(FS)XWUtilFreeBSD.o $(OBJDIR)$(FS)XWUtilUnix.o
endif

#### SunOS (Solaris) Libraries
ifeq ("$(TARGET)", "SunOS")
$(OBJDIR)$(FS)%.jni:
	ar -r $@ $^ 
#	$(CC) $(LDFLAGS) -o $@ $^ 
$(OBJDIR)$(FS)XWUtil.jni: $(OBJDIR)$(FS)XWUtilUnix.o
$(OBJDIR)$(FS)XwTracer.jni: $(OBJDIR)$(FS)XWTracerUnixlib.o $(OBJDIR)$(FS)XWTracerUnixImpl.o 
endif


#### Win32 Libraries
ifneq (,$(findstring Windows,$(TARGET)))
$(OBJDIR)$(FS)%.jni: $(OBJDIR)$(FS)dllmain.o
	$(DLLWRAP) $(DLLFLAGS) -o $@ $^
$(OBJDIR)$(FS)XWUtil.jni: $(OBJDIR)$(FS)XWUtilWin32.o
$(OBJDIR)$(FS)XWInterrupts.jni: $(OBJDIR)$(FS)win_idle_tracker.o $(OBJDIR)$(FS)XWInterruptsWin32.o $(OBJDIR)$(FS)WinSaver.o
$(OBJDIR)$(FS)XwTracer.jni: $(OBJDIR)$(FS)XWTracerWin32Impl.o $(OBJDIR)$(FS)XWTracerWin32lib.o
#$(OBJDIR)$(FS)XWNotify.jni: $(OBJDIR)$(FS)XWNotifyWin32.o $(OBJDIR)$(FS)XWNotifyHandler.o $(OBJDIR)$(FS)XWNotifyImages.o $(OBJDIR)$(FS)XWNotifyThread.o
$(OBJDIR)$(FS)XWServer.exe: $(OBJDIR)$(FS)XWServer.coff $(OBJDIR)$(FS)XWServer.o

$(OBJDIR)$(FS)XWWorker.exe: $(OBJDIR)$(FS)XWWorker.coff $(OBJDIR)$(FS)XWWorker.o $(OBJDIR)$(FS)libjvm.a
	$(CC) -L. $(LDFLAGS) -ljvm -o $@ $^

$(OBJDIR)$(FS)XWClient.exe: $(OBJDIR)$(FS)XWClient.coff $(OBJDIR)$(FS)XWClient.o $(OBJDIR)$(FS)libjvm.a
	$(CC) -L. $(LDFLAGS) -ljvm -o $@ $^

$(OBJDIR)$(FS)XWlaunch.exe: $(OBJDIR)$(FS)XWlaunch.coff $(OBJDIR)$(FS)XWlaunch.o
$(OBJDIR)$(FS)XWWorker.coff: XWlaunch.rc
	$(WINDRES) -DXWWORKER -i $< -o $@
$(OBJDIR)$(FS)XWClient.coff: XWlaunch.rc
	$(WINDRES) -DXWCLIENT -i $< -o $@
$(OBJDIR)$(FS)XWServer.coff: XWlaunch.rc
	$(WINDRES) -DXWSERVER -i $< -o $@
$(OBJDIR)$(FS)XWlaunch.coff: XWlaunch.rc
	$(WINDRES) -DXWSERVER -i $< -o $@
$(OBJDIR)$(FS)dllmain.o: dllmain.c
$(OBJDIR)$(FS)libjvm.a: jvm.def
	 $(CROSS)dlltool --kill-at --input-def jvm.def --dll jvm.dll --output-lib $@
endif

#### clean
clean:
	rm -f XWInterruptsImpl.h PortMapper.h XWUtilImpl.h XWTracerImpl.h

#### generic rules
$(OBJDIR)$(FS)%.o: %.c
	$(CC) -c $(CFLAGS) -o $@ $<

$(OBJDIR)$(FS)%.o: %.cpp
	$(CC) -c $(CFLAGS) -o $@ $<

$(OBJDIR)$(FS)%.exe: %.coff $(OBJDIR)$(FS)libjvm.a
	$(CC) -L. $(LDFLAGS) -o $@ $^ -ljvm



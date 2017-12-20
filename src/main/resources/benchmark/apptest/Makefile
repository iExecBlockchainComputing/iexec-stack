OSNAME=$(shell uname -s)
CPUNAME=$(shell uname -m)


APPLICATION=apptest
SOURCE=$(APPLICATION).c

MACOS=macosx
LINUX=linux
WIN32=win32

INTEL=ix86
PPC=ppc
AMD64=amd64


OSTYPE=unknown
CPUTYPE=$(CPUNAME)


GCC=gcc
GCCFLAGS=


ifeq ("$(OSNAME)", "Darwin")
  OSTYPE=$(MACOS)
  GCCFLAGS= -force_cpusubtype_ALL -arch i386 -arch ppc
endif
ifeq ("$(OSNAME)", "Linux")
  OSTYPE=$(LINUX)
endif
ifeq ("$(CPUNAME)", "i686")
  CPUTYPE=$(INTEL)
endif
ifeq ("$(CPUNAME)", "i386")
  CPUTYPE=$(INTEL)
endif

ALL=$(APPLICATION)-$(OSTYPE)-$(CPUTYPE)
MACOSXPPC=$(APPLICATION)-$(OSTYPE)-$(PPC)
MACOSXINTEL=$(APPLICATION)-$(OSTYPE)-$(INTEL)

all: $(ALL)

clean:
	@echo $(OSNAME)
	rm -f $(ALL)
ifeq ("$(OSNAME)", "Darwin")
	rm -f $(MACOSXINTEL) $(MACOSXPPC)
endif

$(ALL): $(SOURCE)
	gcc $(GCCFLAGS) $< -o $@
ifeq ("$(OSNAME)", "Darwin")
ifeq ("$(CPUNAME)", "PPC")
	cp $(ALL) $(MACOSXINTEL)
else
	cp $(ALL) $(MACOSXPPC)
endif
endif

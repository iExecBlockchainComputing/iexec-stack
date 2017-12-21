
    Linux XtremWeb dispatcher
    -------------------------


 Introduction
 ------------

This package contains the XtremWeb dispatcher linux version.



 Manifest
 --------

This directory contains :
	- ./README.txt                 : this file
	- bin/dispatcher               : a script to manually run the dispatcher
	- bin/wrapper                  : the dispatcher wrapper
	- bin/wrapper.log              : the dispatcher log file (if any)
	- conf/wrapper.conf            : the wrapper    configuration file (DON'T EDIT)
	- conf/config.defaults         : the dispatcher configuration file (MUST BE EDITED SEE BELOW 'Usage')
	- lib/libwrapper.so            : the wrapper library file
	- lib/wrapper.jar              : the wrapper jar file
	- lib/xwdisp.jar    etc.       : the dispatcher jar files


 Usage
 -----

*****
        Before anything else, you must :
		- install MySQL !
		  Please refer to http://www.mysql.org
		- edit conf/config.defaults !
		  Please refer to http://www.lri.fr/~fedak/XtremWeb/doc/xtremweb.html
*****
*****
	To start the XtremWeb dispatcher, please run bin/dispatcher
*****

	-----	-----	-----	-----


The XtremWeb Team.


Directory     : build/
Content       : build scripts
Purpose       : these files aim to compile, install, remove packages
Author        : Oleg Lodygensky
Last Modified : 21 octobre 2010

Files         : Makefile, natif.mk, build.xml, build.conf, install.xml,

Web site      : http://www.xtremweb-hep.org


Introduction :
------------

The XWHEP middleware is provided under GPL license.
Please read License file.

The middleware is provided in two packages : the source and the binary ones.
We can't propose out of the box installers since (1) the server needs mysql configuration
(mysql server, login and password) and (2) SSL keys must be generated before deployment.

Source package aims to generate the binary one as described below.
Binary package aims to generate installers as described below.



==============
Source package
==============

The source package aims to generate the binary package.


Preparing the process : 
---------------------

Before any action, "build.conf" must be edited


Preparing the binary package :
----------------------------

Make options :
            $> make clean        ; clean all but DB
            $> make              ; build packages
       (1)  $> make removeDB     ; remove DB only
            $> make uninstall    ; remove all but DB
            $> make uninstallAll ; remove all including DB
            $> make doc          ; install Java Docs



How to make and install the binary package : 
            $> make clean
            $> make

How to uninstall all (including DB) : 
       (1)  $> make uninstallAll


  (1) removeDB does not remove the database itself, but tables only



==============
Binary package
==============

The binary package aims to generate installers.

Using the binary package :
------------------------

As soon as you have either download the binary package or generated it from source package
you must execute the <you bin package path>/bin/xwconfigure script and follow the instructions.
You are also invited to read the INSTALL file.

================
That's all folks
================


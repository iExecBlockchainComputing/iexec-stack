#!/usr/bin/env python
# -*- coding: utf-8 -*-

#inspired from
#http://matplotlib.sourceforge.net/mpl_examples/pylab_examples/histogram_demo_extended.py
#http://matplotlib.sourceforge.net/examples/pylab_examples/histogram_demo_extended.html

#add the following code in a place that gets read before any other pylab/matplotlib import:
import matplotlib
# Force matplotlib to not use any Xwindows backend.
matplotlib.use('Agg')

import numpy as np
import pylab as P

import os

#def genhist()
simupath= os.getcwd()
#print simupath
#os.system("touch oo")
cmd = "/usr/bin/awk '{print $1}' "+simupath+"/beam1.dat > "+simupath+"/beam1h.dat"
os.system(cmd)
lines = [float(line.strip()) for line in open(simupath+'/beam1h.dat')]
n, bins, patches = P.hist(lines, 35, normed=1, histtype='bar', rwidth=0.6)
P.savefig(simupath+'/beam1.png')
cmd = "/bin/rm "+simupath+"/beam1h.dat"
os.system(cmd)

cmd = "/usr/bin/awk '{print $1}' "+simupath+"/beam2.dat > "+simupath+"/beam2h.dat"
os.system(cmd)
lines = [float(line.strip()) for line in open(simupath+'/beam2h.dat')]
n, bins, patches = P.hist(lines, 35, normed=1, histtype='bar', rwidth=0.6)
P.savefig(simupath+'/beam2.png')
cmd = "/bin/rm "+simupath+"/beam2h.dat"
os.system(cmd)




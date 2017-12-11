/*
 *
 * Copyrights     : CNRS
 * Author         : Oleg Lodygensky
 * Acknowledgment : XtremWeb-HEP is based on XtremWeb 1.8.0 by inria : http://www.xtremweb.net/
 * Web            : http://www.xtremweb-hep.org
 * Since          : 8.1.0
 * 
 *      This file is part of XtremWeb-HEP.
 *
 *    XtremWeb-HEP is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    XtremWeb-HEP is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with XtremWeb-HEP.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *
 */



<!-- ********************************************************************** -->
<!-- ********************************************************************** -->
<!-- ********************************************************************** -->
<!-- ********************************************************************** -->
<!-- These are the Web worker scripts to retrieve works from server -->
<!-- These are the Web worker scripts to retrieve works from server -->
<!-- These are the Web worker scripts to retrieve works from server -->
<!-- These are the Web worker scripts to retrieve works from server -->
<!-- These are the Web worker scripts to retrieve works from server -->
<!-- ********************************************************************** -->
<!-- ********************************************************************** -->
<!-- ********************************************************************** -->
<!-- ********************************************************************** -->




/**
 * This is the connection
 */
var xmlHttpConnectionGet;

/**
 * This is the Web Worker entry point
 */
self.onmessage = function(e) {
	xmlHttpConnectionGet=getXmlHttpObject();
	if (xmlHttpConnectionGet!==null)
	{
		var url = e.data;
//		postMessage({'cmd' : 'log', 'content' : "webWorkerGetScript URL = " + url});
		xmlHttpConnectionGet.onreadystatechange=getStateChanged;
	    xmlHttpConnectionGet.open("POST",url,true);
	    xmlHttpConnectionGet.send(null);
	}
}

/**
 * This creates a new XMLHTTPRequest object
 */
function getXmlHttpObject()
{
    var ret=null;
    try
    {
	// Firefox, Opera 8.0+, Safari
		ret=new XMLHttpRequest();
    }
    catch (e)
    {
	// Internet Explorer
	try
	{
	    ret=new ActiveXObject("Msxml2.XMLHTTP");
	}
	catch (e)
	{
	    ret=new ActiveXObject("Microsoft.XMLHTTP");
	}
    }
    return ret;
}

/**
 * This handles xmlHttp events
 * On xmlHttp received answer, this post the JSon message: {'cmd' : 'answer', 'content' : xmlHttpConnectionGet.responseText}
 */
function getStateChanged()
{ 
    if (xmlHttpConnectionGet.readyState!=4)
    {
        return;
    }

    try {
    	postMessage({'cmd' : 'answer', 'content' : xmlHttpConnectionGet.responseText})
	}
	catch(err) {
	    postMessage({'cmd' : 'error', 'content' : "Connection error " + msg});
	}
}


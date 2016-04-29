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

/**********************************************************************
 **********************************************************************
 **********************************************************************
 These are the main scripts
 These are the main scripts
 These are the main scripts
 These are the main scripts
 **********************************************************************
 **********************************************************************
 **********************************************************************
 **********************************************************************/

/**
 * This cancels background color
 * @param thediv is the div to reset background color to
 */
function resetBgColor(thediv)
{
  if (thediv) {
     thediv.style.backgroundColor="";
  }
}

/**
 * This sets background color from green to red according to idx
 * @param thediv is the div to set background color to
 * @param idx is the div index
 */
function setBgColor(thediv, idx)
{
  var hslColor = (1 - (0.125 * idx)) * 120;
  console.log("hslColor = " + hslColor);
  if (thediv) {
     thediv.style.backgroundColor="hsl(" + hslColor + ", 100%, 50%)";
  }
  else {
    console.log("setBgColor() : thediv is not set");
  }
}

/**
 * This retrieves a cookie
 * @param c_name is the 
 * @see http://www.w3schools.com/js/js_cookies.asp
 */
function getCookie(c_name)
{
  var c_value = document.cookie;
  console.log("getCookie("+c_name+") = " + c_value);
  var c_start = c_value.indexOf(" " + c_name + "=");
  if (c_start == -1)
  {
    c_start = c_value.indexOf(c_name + "=");
  }
  if (c_start == -1)
  {
    c_value = null;
  }
  else
  {
    c_start = c_value.indexOf("=", c_start) + 1;
    var c_end = c_value.indexOf(";", c_start);
    if (c_end == -1)
    {
      c_end = c_value.length;
    }
    c_value = unescape(c_value.substring(c_start,c_end));
  }
  return c_value;
}

/**
 * This sets a cookie
 * @param c_name is the
 * @param value is the cookie value
 * @param exdays is the expiration date 
 * @see http://www.w3schools.com/js/js_cookies.asp
 */
function setCookie(c_name,value,exdays)
{
  var exdate=new Date();
  exdate.setDate(exdate.getDate() + exdays);
  var c_value=escape(value) + ((exdays==null) ? "" : "; expires="+exdate.toUTCString());
  document.cookie=c_name + "=" + c_value;
}


/**
 * These variables aim to colorize informations
 */
var colors = new Array("#b0b0b0", "#e0e0e0");
var colorpending   = "#F8F8F8";
var colorrunning   = "#0099ff";
var colorcompleted = "#00cc66";
var colorerror     = "#CC3366";

/**
 * This is the regular expression to check access rights
 */
var ARREGEXP = /7[0-7]{2}/g;

/**
 * This is the XML root element opening tag
 */
var XMLROOTOPEN="<xwhep version='@XWVERSION@'>";
/**
 * This is the XML root element closing tag
 */
var XMLROOTCLOSE="</xwhep>";

var XMLDESC="XMLDESC";

/**
 * These are connections to server
 */
var xmlHttpConnectionGetCurrentUser;
var xmlHttpConnectionGetApps;
var xmlHttpConnectionGetDatas;
var xmlHttpConnectionGetBots;
var xmlHttpConnectionGetWorks;
/**
 * This is used to register an object
 */
var xmlHttpConnectionSend;
/**
 * This is used to upload data content
 */
var xmlHttpConnectionUpload;
/**
 * These are used to retrieve objects given their UID from server
 * These are hashtables having UID as key and xmlHttpConnection as value
 */
var hashtableGetApp    = new Object();
var hashtableGetData   = new Object();
var hashtableGetBot    = new Object();
var hashtableGetWork   = new Object();
var hashtableGetDetail = new Object();
var hashtableDelete    = new Object();

/**
 * This is the URL to upload data
 * @see uploadData()
 */
var uploadUrl;
/**
 * This is the file reader to read file from local disk
 * @see uploadData()
 */
var fileReader;
/**
 * This is the file to upload
 * @see uploadData()
 */
var uploadFile;

/**
 * These are button bars ID 
 */
var emptyBarID    = "emptyBar";
var overviewBarID = "overviewBar";
var objectsBarID  = "objectsBar";

/**
 * These are form ID 
 */
var overviewFormID  = "overviewForm";
var listAppFormID   = "listAppForm";
var listDataFormID  = "listDataForm";
var listWorkFormID  = "listWorkForm";
var listBotFormID   = "listBotForm";
var sendFormID      = "sendForm";
var uploadFormID    = "uploadForm";
var submitFormID    = "submitForm";
var selectObjFormID = "selectObjectForm";

/**
 * This is the XML tag of the element returned by the server on error
 */
var XMLRPCRESULTTAG="xmlrpcresult";
/**
 * This is the XMLRPCRESULT return code
 */
var XMLRPCRESULTRETURNCODE="RETURNCODE";
/**
 * This is the XMLRPCRESULT return code
 */
var XMLRPCRESULTMESSAGE="MESSAGE";

/**
 * These are HTML button ID 
 */
var submitButtonID  ="submitButton";
var dirinName   = "dirin";
var stdinName   = "stdin";
var JobTabTitle = "jobTabTitle";
/**
 * These are tab ID 
 */
var overviewTabID   = "tabOverview";
var appsTabID   = "tabApps";
var datasTabID  = "tabDatas";
var worksTabID  = "tabJobs";
var botsTabID   = "tabBots";
var helpTabID   = "tabHelp";
var apiTabID    = "tabAPI";

var overviewAppContent = new Array();

/**
 * These are HTML element ID 
 */
var overviewListID  = "overviewList";
var appListID       = "appList";
var dataListID      = "dataList";
var botListID       = "botList";
var helpListID      = "helpList";
var apiListID       = "apiList";
var detailListID    = "detailList";
var worksListsID    = "jobsLists";
var overviewCurrentUserID = "overviewCurrentUser";
var overviewCurrentUserIntroID = "overviewCurrentUserIntro";
var overviewAppsID  = "overviewApps";
var overviewHostsID = "overviewHosts";
var pendingListID   = "pendingWorks";
var runningListID   = "runningWorks";
var completedListID = "completedWorks";
var errorListID     = "errorWorks";
var theOverviewID   = "theOverview";
var theAppsID       = "theApplications";
var theDatasID      = "theDatas";
var theWorksID      = "theJobs";
var theBotsID       = "theBots";
var theSubmitID     = "theSubmit";
var theHelpID       = "theHelp";
var theAPIID        = "theApi";
/**
 * These are HTML element name in "sendForm" form 
 */
var nameInputName     = "nameInput"; // data name, app name
var arInputName       = "accessrightsInput";
var menuTypeInputName = "menutypeInput";
var menuOsInputName   = "menuosInput";
var menuCpuInputName  = "menucpuInput";
var uriName           = "uriInput";

/**
 * These are HTML element name in "uploadForm" form 
 */
var uidInputName   = "DATAUID";
var sizeInputName = "DATASIZE";
var md5InputName  = "DATAMD5SUM";
var fileInputName = "DATAFILE";
/**
 * These are HTML element name in "submitForm" form 
 */
var appuidSubmitInputName   = "submit_appuid";
var labelSubmitInputName    = "submit_label";
var botuidSubmitInputName   = "submit_botuid";
var cmdlineSubmitInputName  = "submit_cmdline";
var envvarsSubmitInputName  = "submit_envvars";
var dirinuriSubmitInputName = "submit_dirinuri";
var stdinuriSubmitInputName = "submit_stdinuri";
var listenportSubmitInputName = "submit_listenport";
var smartsocketclientSubmitInputName = "submit_smartsocketclient";

/**
 * These are HTML element ID header
 */
var appnameheader    = "appname_";
var appuidheader     = "appuid_";
var datanameheader   = "dataname_";
var datauidheader    = "datauid_";
var botuidheader     = "botuid_";
var botjobsuidheader = "groupworksuid_";
var pendingheader    = "pending_";
var runningheader    = "running_";
var completedheader  = "completed_";
var errorheader      = "error_";
var datauriheader    = "datauri_";
var resultHeader     = "ResultOf_";

/**
 * This is the access rights tooltip
 */
var arTooltip = "<p>Access rights (<i>AR</i>) allows or denies access; it must be written as octal value.</p>" +
				"<p>e.g.:<ul>"+
				"<li>public AR = 755 (<b>this is the default</b>, where 7 gives full access to the owner;" +
				" 5 gives read and execute access to the group, if any;" +
				" and 5 gives read and execute access to others.</li>" + 
				"<li>group AR = 750, where 7 gives full access to the owner;" +
				" 5 gives read and execute access to the group, if any;" +
				" and 0 gives no access to others.</li>" + 
				"<li>private AR = 700, where 7 gives full access to the owner;" +
				" 0 gives no access to the group, if any;" +
				" and 0 gives no access to others.</li></ul><p>"; 


/**
 * This contains the web worker pool with 8 workers
 */
var webWorkerPool = new WorkerPool(8);

/**
 * Web Worker Pool
 * size is the max number of arguments
 * https://gist.github.com/605541#file-js_web_workerpool-js
*/
function WorkerPool(size) {
    var workers = 0,
        jobs    = [];

    // url: the url of the worker script
    // msg: the initial message to pass to the worker
    // cb : the callback to recieve messages from postMessage.
    //      return true from cb to dismiss the worker and advance the queue.
    // ctx: the context for cb.apply
    this.queueJob = function(url, msg, cb, ctx) {
        var job = {
            "url": url,
            "msg": msg,
            "cb" : cb,
            "ctx": ctx
        };
        jobs.push(job);
        if (workers < size) nextJob();
    };
    
    function nextJob() {
	      console.log("workers = " + workers);
	      console.log("jobs.l  = " + jobs.length);
        if (jobs.length) {
            (function() {
                var job    = jobs.shift(),
                worker = new Worker(job.url);
				var mydivid= "worker" + workers;
				var mydiv = document.getElementById(mydivid);
                setBgColor(mydiv, workers);
                workers++;
                worker.addEventListener('message', function(e) {
                    if (job.cb.call(job.ctx, worker, e)) {
                    	console.log("terminating worker = " + workers);
						worker.terminate();
						var mydivid= "worker" + workers;
						var mydiv = document.getElementById(mydivid);
						resetBgColor(mydiv);
                        workers--;
                        nextJob();
                    };
                }, false);
		      console.log("starting worker = " + workers);
                worker.postMessage(job.msg);
            })();
        }
    }
}




function getDocHeight() {
    var D = document;
    return Math.max(
        Math.max(D.body.scrollHeight, D.documentElement.scrollHeight),
        Math.max(D.body.offsetHeight, D.documentElement.offsetHeight),
        Math.max(D.body.clientHeight, D.documentElement.clientHeight)
    );
}

/**
 * This ensures we open one popup at a time
 */
var popupOpened = false;

/**
 * This calls myPop(buttonTitle, callback, false)
 * @see myPop(buttonTitle, callback, false)
 */
function myPop(buttonTitle, callback) { 
	myPop(buttonTitle, callback, false)
}

/**
 * This opens a DIV as popup window with a "Close" button.
 * This may add another button to the pop up, defined by the provided title and callback
 * This does nothing, if a pop up is already opened; except if force == true
 * @param buttonTitle is the title of the button to add to the popup
 * @param callback is a method to be called by clicking this button
 * @param force forces to open a new popup even if a popup is already opened
 */
function myPop(buttonTitle, callback, force) { 

/*
    if ((popupOpened == true) && (force == false)) {
		return;
	}
	popupOpened = true;
*/
    this.square = null;
    this.overdiv = null;

    this.popOut = function(msgtxt) {
 
	    if (popupOpened == true) {
			return;
		}
		popupOpened = true;
 
        //filter:alpha(opacity=25);-moz-opacity:.25;opacity:.25;
        this.overdiv = document.createElement("div");
        this.overdiv.className = "overdiv";
		this.overdiv.style.height = getDocHeight();
        this.square = document.createElement("div");
        this.square.className = "square";
        this.square.Code = this;
        var msg = document.createElement("div");
        msg.className = "msg";
        msg.innerHTML = msgtxt;
        this.square.appendChild(msg);
        
        var closebtn = document.createElement("button");
        closebtn.onclick = function() {
            this.parentNode.Code.popIn();
        }

        closebtn.innerHTML = "Close";
        closebtn.className = "firstdetail";
        this.square.appendChild(closebtn);

		if((buttonTitle != undefined) && (callback != undefined)) {
	        var customButton = document.createElement("button");
    	    customButton.onclick = function() {
				callback();
	            this.parentNode.Code.popIn();
        	}
	        customButton.innerHTML = buttonTitle;
	        customButton.className = "lastdetail";
    	    this.square.appendChild(customButton);
    	}

        document.body.appendChild(this.overdiv);
        document.body.appendChild(this.square);
    }
    this.popIn = function() {
        if (this.square != null) {
            document.body.removeChild(this.square);
            this.square = null;
        }
        if (this.overdiv != null) {
	        document.body.removeChild(this.overdiv);
    	    this.overdiv = null;
        }
		popupOpened = false;
    }
}

/**
 * This creates a warn modal box content
 * @return a String containing the warn modal box content
 * @see warning(theMsg)
 */
function warningContent(theMsg) {
	var theBody = "<div class=\"modalContent\" style=\"display:block\">" +
		"<div class=\"modalpane\"><div class=\"modaltitrewarning\">" +
		  "<span ><p>WARNING</p><p>" + theMsg + "</p></span></div>" +
	    "<div id=\"" + detailListID + "\" style=\"display:block\"></div>" +
		"<div class=\"modaltrailer\">&nbsp;</div></div></div>";

	return theBody;
}

/**
 * This displays a warn modal box
 * @see warningContent(theMsg)
 */
function warning(theMsg) {
	var theBody = warningContent(theMsg);
	var pop = new myPop();
    pop.popOut(theBody);
	pop = null;
	theBody = null;
}

/**
 * This displays an error on connection error
 */
function connectionError() {
	error("Connection error");
}

/**
 * This displays an error modal box
 */
function error(theMsg) {
	var theBody = "<div class=\"modalContent\" style=\"display:block\">" +
		"<div class=\"modalpane\">" +
	    "<div class=\"modaltitreerror\">" +
		  "<span ><p>ERROR</p><p>" + theMsg + "</p></span></div>" +
	    "<div id=\"" + detailListID + "\" style=\"display:block\"></div>" +
		"<div class=\"modaltrailer\">&nbsp;</div>" +
		"</div></div>";

	var pop = new myPop();
    pop.popOut(theBody);
	pop = null;
	theBody = null;
}

/**
 * This creates a string containing a tooltip to be shown on mouse over
 * @param title is the tooltip title
 * @param msg is the tooltip message
 */
function toolTip(title, msg) {
	return "<a href=\"#\" class=\"tooltip\"><b>&nbsp;" + title + "&nbsp;</b></a><div>" + msg + "</div>";
}

/**
 * This checks if there is an RPC error
 * If there is an RPC error, this displays an error msgBox
 * @return true on RPC error, false otherwise 
 */
function rpcError(xmldoc) {
	if(xmldoc == null) {
		return false;
	}
    var rpcErr = xmldoc.getElementsByTagName(XMLRPCRESULTTAG)[0];
    if(rpcErr != null) {
	    var msg = rpcErr.getAttribute(XMLRPCRESULTMESSAGE);
	    var rc  = rpcErr.getAttribute(XMLRPCRESULTRETURNCODE);
	    error("RPC error<br />&quot;" + msg + "&quot;<br />(" + rc + ")");
        return true;
	}
	return false;
}

/**
 * This parses a string to an XML object
 * @param xmlStr is a string representation of an XML object
 * @return an XML object
 */
function stringToXml(xmlStr) {
	var doc = null;

    if (window.ActiveXObject) {
      doc = new ActiveXObject('Microsoft.XMLDOM');
      doc.async='false';
      doc.loadXML(xmlStr);
    }
    else {
      var parser = new DOMParser();
      doc = parser.parseFromString(xmlStr, 'text/xml');
    }
    return doc;
}

/**
 * This shows/hides an element
 * @param theID is the ID of the element to show/hide
 */
 function showHide(theID) {
	var ele = document.getElementById(theID);
	if(ele.style.display == "block") {
    		ele.style.display = "none";
  	}
	else {
		ele.style.display = "block";
	}
}

/**
 * This shows an element
 * @param theID is the ID of the element to show
 */
 function show(theID) {
	var ele = document.getElementById(theID);
	if(ele != null)
		ele.style.display = "block";
}


/**
 * This checks all buttons in the current tab
 * @see listAppFormID
 * @see listDataFormID
 * @see listWorkFormID
 * @see listBotFormID
 */
function checkAll() {
	if(document.getElementById(appsTabID).getAttribute("class") == "current") {
		checkAllInForm(listAppFormID);
		return;
	}
	if(document.getElementById(datasTabID).getAttribute("class") == "current") {
		checkAllInForm(listDataFormID);
		return;
	}
	if(document.getElementById(worksTabID).getAttribute("class") == "current") {
		checkAllInForm(listWorkFormID);
		return;
	}
	if(document.getElementById(botsTabID).getAttribute("class") == "current") {
		checkAllInForm(listBotFormID);
		return;
	}
}

/**
 * This checks all buttons in the given element
 * @param theID is the ID of the element containing the checkboxes
 */
function checkAllInForm(theID) {
	var elem = document.getElementById(theID);
	for (var i = 0; i < elem.length; i++) {
		try {
			if(elem[i].type == "checkbox")
				elem[i].checked = true;
		}
		catch(err) {
		}
	}
}

/**
 * This unchecks all buttons in the current tab
 * @see listAppFormID
 * @see listDataFormID
 * @see listWorkFormID
 * @see listBotFormID
 */
function uncheckAll() {
	if(document.getElementById(appsTabID).getAttribute("class") == "current") {
		uncheckAllInForm(listAppFormID);
		return;
	}
	if(document.getElementById(datasTabID).getAttribute("class") == "current") {
		uncheckAllInForm(listDataFormID);
		return;
	}
	if(document.getElementById(worksTabID).getAttribute("class") == "current") {
		uncheckAllInForm(listWorkFormID);
		return;
	}
	if(document.getElementById(botsTabID).getAttribute("class") == "current") {
		uncheckAllInForm(listBotFormID);
		return;
	}
}

/**
 * This unchecks all buttons in the given element
 * @param theID is the ID of the element containing the checkboxes
 */
function uncheckAllInForm(theID) {
	var elem = document.getElementById(theID);
	for (var i = 0; i < elem.length; i++) {
		try {
			if(elem[i].type == "checkbox")
				elem[i].checked = false;
		}
		catch(err) {
		}
	}
}

/**
 * This hides an element
 * @param theID is the ID of the element to hide
 */
 function hide(theID) {
	var ele = document.getElementById(theID);
	if(ele != null) {
		ele.style.display = "none";
	}
}

/**
 * This shows the given tab and hides all others
 * @param theTabID is the ID of the tab to show
 */
function showTab(theID, theTabID) {

	uncheckAll();

	//
	// hide buttons bars
	//
	var ele = document.getElementById(objectsBarID);
    ele.style.display = "none";
	ele = document.getElementById(overviewBarID);
    ele.style.display = "none";
	ele = document.getElementById(emptyBarID);
    ele.style.display = "none";

	//
	// hide all tabs
	//
	ele = document.getElementById(theOverviewID);
    ele.style.display = "none";
	ele = document.getElementById(overviewTabID).setAttribute("class", "");

	ele = document.getElementById(theAppsID);
    ele.style.display = "none";
	ele = document.getElementById(appsTabID).setAttribute("class", "");

	ele = document.getElementById(theDatasID);
    ele.style.display = "none";
	ele = document.getElementById(datasTabID).setAttribute("class", "");

	ele = document.getElementById(theWorksID);
    ele.style.display = "none";
	ele = document.getElementById(worksTabID).setAttribute("class", "");

	ele = document.getElementById(theBotsID);
    ele.style.display = "none";
	ele = document.getElementById(botsTabID).setAttribute("class", "");

	ele = document.getElementById(theAPIID);
    ele.style.display = "none";
	ele = document.getElementById(apiTabID).setAttribute("class", "");

	ele = document.getElementById(theHelpID);
    ele.style.display = "none";
	ele = document.getElementById(helpTabID).setAttribute("class", "");

	ele = document.getElementById(theID);
    ele.style.display = "block";
	ele = document.getElementById(theTabID).setAttribute("class", "current");

	if(theID == theOverviewID) {
		ele = document.getElementById(overviewBarID);
    	ele.style.display = "block";
	}
	else {
		if((theID != theHelpID) && (theID != theAPIID)) {
			ele = document.getElementById(objectsBarID);
    		ele.style.display = "block";
		}
		else {
			ele = document.getElementById(emptyBarID);
    		ele.style.display = "block";
		}
	}
}


/**
 * This retrieves the form of the current tab
 * @return the form of the current tab
 */
function getCurrentForm() {

	var formElem = null;
	if(document.getElementById(overviewTabID).getAttribute("class") == "current") {
		formElem = document.getElementById(overviewFormID);
	}
	if(document.getElementById(appsTabID).getAttribute("class") == "current") {
		formElem = document.getElementById(listAppFormID);
	}
	if(document.getElementById(datasTabID).getAttribute("class") == "current") {
		formElem = document.getElementById(listDataFormID);
	}
	if(document.getElementById(worksTabID).getAttribute("class") == "current") {
		formElem = document.getElementById(listWorkFormID);
	}
	if(document.getElementById(botsTabID).getAttribute("class") == "current") {
		formElem = document.getElementById(listBotFormID);
	}
	return formElem;
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
 * This retrieves objects from server
 */
function refresh() {
	if(document.getElementById(overviewTabID).getAttribute("class") == "current") {
		getCurrentUser();
		getApps();
		return;
	}
	if(document.getElementById(appsTabID).getAttribute("class") == "current") {
		getApps();
		return;
	}
	if(document.getElementById(datasTabID).getAttribute("class") == "current") {
		getDatas();
		return;
	}
	if(document.getElementById(worksTabID).getAttribute("class") == "current") {
		getWorks();
		return;
	}
	if(document.getElementById(botsTabID).getAttribute("class") == "current") {
		getBots();
		return;
	}
}

/**
 * This retrieves the current user using USERUID cookie value
 * @see xtremweb.dispatcher.HTTPHandler#usagePage(User)
 */
function getCurrentUser()
{ 
    xmlHttpConnectionGetCurrentUser=getXmlHttpObject();
    if (xmlHttpConnectionGetCurrentUser==null)
    {
		return;
    }
	var uid = getCookie("USERUID");
	console.log("getCurrentUser#USERUID = " + uid);
	if (uid == null || uid == "")
	{
		return;
	}

    var url="/get/" + uid;
    xmlHttpConnectionGetCurrentUser.onreadystatechange=getCurrentUserStateChanged;
    xmlHttpConnectionGetCurrentUser.open("POST",url,true);
    xmlHttpConnectionGetCurrentUser.send(null);
}

/**
 * This handles getCurrentUser events
 * This displays the application informations in its DIV
 *
 * @see getCurrentUser()
 */
function getCurrentUserStateChanged()
{
	var current = xmlHttpConnectionGetCurrentUser;
   	if (current.readyState!=4) {
        return;
   	}

	var uid = getCookie("USERUID");
	console.log("getCurrentUserStateChanged#USERUID = " + uid);
	if (uid == null || uid == "")
	{
		return;
	}

    var xmlDoc = null;
    try {
    	if (xmlDoc=current.responseXML == null) {
    		if (current.status == 401) {
	    		document.documentElement.innerHTML=current.responseText;
	   			return;
    		}
    	}
    	xmlDoc=current.responseXML.documentElement;
	}
	catch(err) {
		connectionError();
		return;
	}

	var divID = overviewCurrentUserIntroID;

    if(rpcError(xmlDoc) == true) {
		return;
	}

	try {
       	document.getElementById(divID).innerHTML =
       	      "<span class=\"overviewleft\">Login</span><span class=\"overviewright\" id=\"overviewUserLogin\">" + xmlDoc.getElementsByTagName("login").item(0).firstChild.nodeValue + "</span>"
              + "<span class=\"overviewleft\">Email</span><span class=\"overviewright\" id=\"overviewUserEmail\">" + xmlDoc.getElementsByTagName("email").item(0).firstChild.nodeValue + "</span>" 
              + "<span class=\"overviewleft\">Rights</span><span class=\"overviewright\" id=\"overviewUserRights\">" + xmlDoc.getElementsByTagName("rights").item(0).firstChild.nodeValue + "</span>"; 
		try {
       		document.getElementById(divID).innerHTML +=
            	"<span class=\"overviewleft\">First Name</span><span class=\"overviewright\" id=\"overviewUserFName\">" + xmlDoc.getElementsByTagName("fname").item(0).firstChild.nodeValue + "</span>";
         } catch(err) {
         } 
         try {
       		document.getElementById(divID).innerHTML +=
             	"<span class=\"overviewleft\">Last Name</span><span class=\"overviewright\" id=\"overviewUserLName\">" + xmlDoc.getElementsByTagName("lname").item(0).firstChild.nodeValue + "</span>";
         } catch(err) {
         } 

   		document.getElementById(divID).innerHTML +=
              "<span class=\"overviewleft\">&nbsp;</span><span class=\"overviewright\">&nbsp;</span>";

         try {
       		document.getElementById(divID).innerHTML +=
              "<span class=\"overviewleft\">Pending jobs</span><span class=\"overviewright\" id=\"overviewUserPending\">" + xmlDoc.getElementsByTagName("pendingjobs").item(0).firstChild.nodeValue + "</span>"; 
         } catch(err) {
         } 
         try {
       		document.getElementById(divID).innerHTML +=
              "<span class=\"overviewleft\">Running jobs</span><span class=\"overviewright\" id=\"overviewUserRunning\">" + xmlDoc.getElementsByTagName("runningjobs").item(0).firstChild.nodeValue + "</span>"; 
         } catch(err) {
         } 
         try {
       		document.getElementById(divID).innerHTML +=
              "<span class=\"overviewleft\">Error jobs</span><span class=\"overviewright\" id=\"overviewUserError\">" + xmlDoc.getElementsByTagName("errorjobs").item(0).firstChild.nodeValue + "</span>"; 
         } catch(err) {
         } 
         try {
       		document.getElementById(divID).innerHTML +=
              "<span class=\"overviewleft\">Completed jobs</span><span class=\"overviewright\" id=\"overviewUserJobs\">" + xmlDoc.getElementsByTagName("nbjobs").item(0).firstChild.nodeValue + "</span>"; 
         } catch(err) {
         } 

       	var pendings   = parseInt(xmlDoc.getElementsByTagName("pendingjobs").item(0).firstChild.nodeValue);
       	var runnings   = parseInt(xmlDoc.getElementsByTagName("runningjobs").item(0).firstChild.nodeValue);
       	var completeds = parseInt(xmlDoc.getElementsByTagName("nbjobs").item(0).firstChild.nodeValue);
       	var errors     = parseInt(xmlDoc.getElementsByTagName("errorjobs").item(0).firstChild.nodeValue);

	var propertyNames = ["pending", "running", "completed", "errors"];
		addData("graph", {"id":new Date().getTime(), "pending":pendings, "running":runnings, "completed":completeds, "errors":errors});
	}
	catch(err){
		console.log("getCurrentUserStateChanged err " + err);
   	}
}

/**
 * This retrieves registered applications uid
 * This cancels all detail
 */
function getApps()
{
    xmlHttpConnectionGetApps=getXmlHttpObject();
    if (xmlHttpConnectionGetApps==null)
    {
		return;
    }

	appDataTreemapLength = 0;
	appDataTreemap = new Array();
	appDataTreemap.push(new Array('Name', 'Parent', 'Completed', 'Pending'));
	appDataTreemap.push(new Array('Applications', null, 0, 0));

    var url="/getapps";
    xmlHttpConnectionGetApps.onreadystatechange=getAppsStateChanged;
    xmlHttpConnectionGetApps.open("POST",url,true);
    xmlHttpConnectionGetApps.send(null);
}

/**
 * This handles getApps events
 * For each retrieved application uid, this sets 
 * a new DIV with ID="the application uid".
 * This finally calls getApp("the application uid") 
 * which will fill the application DIV.
 *
 * getAppStateChange() will finally fill the DIV
 *
 * @see getApp(uid)
 */
function getAppsStateChanged()
{ 
	var current = xmlHttpConnectionGetApps;
    if (current.readyState!=4)
    {
        return;
    }

    var xmlDoc = null;
    try {
    	if (xmlDoc=current.responseXML == null) {
    		if (current.status == 401) {
	    		document.documentElement.innerHTML=current.responseText;
	   			return;
    		}
    	}
    	xmlDoc=current.responseXML.documentElement;
	}
	catch(err) {
		connectionError();
		return;
	}

	var xmlTagName = "XMLVALUE";
	

    if(rpcError(xmlDoc) == true) {
        return;
	}

	document.getElementById(appListID).innerHTML = 
		  "<div class=\"tupletitre\">"+
		    "<span class=\"selectbutton\">Select</span>" +
		    "<span class=\"firstvalue\">UID</span>" +
		    "<span class=\"value\">Name</span>" +
		    "<span class=\"value\">Type</span>" +
		"</div>";

	appDataTreemapLength = xmlDoc.getElementsByTagName(xmlTagName).length;

	for (var i = 0; i < xmlDoc.getElementsByTagName(xmlTagName).length; i++) {
    	try {
			// this is the UID of the application
			var uid = xmlDoc.getElementsByTagName(xmlTagName)[i].getAttribute("value");

			var color = colors[(i % 2)];
				
			// this creates a new DIV with the ID=uid
        	document.getElementById(appListID).innerHTML += 
        		"<div class=\"tuple\" style=\"background-color:" + color +
        		"\" id=\"" + uid + "\"></div>";

        	getApp(uid);
    	}
    	catch(err){
    	}
    }

	// now, the user can get its jobs
	hide("worksRefreshInfo");
    show("worksRefresh");	
}

/**
 * This retrieves registered application from XWHEP server, given its uid
 * @param uid the UID of the app to retrieve
 */
function getApp(uid)
{ 
    hashtableGetApp[uid]=getXmlHttpObject();
    if (hashtableGetApp[uid]==null)
    {
		return;
    }

    var url="/get/" + uid;
    hashtableGetApp[uid].onreadystatechange=getAppStateChanged;
    hashtableGetApp[uid].open("POST",url,true);
    hashtableGetApp[uid].send(null);
}

/**
 * This handles getApp events
 * This displays the application informations in its DIV
 *
 * @see getAppsStateChanged()
 */
function getAppStateChanged()
{
	for (var uid in hashtableGetApp) {

	    if (hashtableGetApp.hasOwnProperty(uid) == false) {
		    console.log("hashtableGetApp.hasOwnProperty(" + uid + ") = false");
	    	continue;
    	}
    	
	    var current = hashtableGetApp[uid];
	     
    	if (current.readyState!=4) {
	        continue;
    	}

	    var xmlDoc = null;
	    try {
	    	if (xmlDoc=current.responseXML == null) {
	    		if (current.status == 401) {
		    		document.documentElement.innerHTML=current.responseText;
		   			return;
	    		}
	    	}
	    	xmlDoc=current.responseXML.documentElement;
		}
		catch(err) {
			connectionError();
			return;
		}
    	// get returns an app XML object
		var xmlTagName = "app";

    	delete hashtableGetApp[uid];
		--appDataTreemapLength;

	    if(rpcError(xmlDoc) == true) {
	        continue;
		}

   		try {
		    var name = xmlDoc.getElementsByTagName("name").item(0).firstChild.nodeValue;
	    	var uid = xmlDoc.getElementsByTagName("uid").item(0).firstChild.nodeValue;
		    var type = xmlDoc.getElementsByTagName("type").item(0).firstChild.nodeValue;
		    var nbJobs = 0;
		    try {
		    	nbJobs = xmlDoc.getElementsByTagName("nbjobs").item(0).firstChild.nodeValue;
		    }
		    catch(err) {
		    }
		    var runningJobs = 0;
		    try {
		    	runningJobs = xmlDoc.getElementsByTagName("runningjobs").item(0).firstChild.nodeValue;
		    }
		    catch(err) {
		    }
		    var pendingJobs = 0;
		    try {
			    pendingJobs = xmlDoc.getElementsByTagName("pendingjobs").item(0).firstChild.nodeValue;
		    }
		    catch(err) {
		    }

			appDataTreemap.push(new Array(name, 'Applications', parseInt(nbJobs), parseInt(pendingJobs)));

		    console.log("App " + name);

        	document.getElementById(uid).innerHTML = "<span class=\"selectbutton\"><input type=\"checkbox\" name=\"" + uid + "\" /></span>" +
        	    "<span class=\"firstvalue\" id=\"" + appuidheader + uid + "\">" + uid + "</span>" +
        		"<span class=\"value\" id=\"" + appnameheader + uid + "\">" + name + "</span>" +
        		"<span class=\"lastvalue\">" + type + "</span>";

			if (overviewAppContent.length == 0) {
				overviewAppContent.push(new Array('AppName', 'Parent', 'Completed', 'Running'));
				overviewAppContent.push(new Array('Global' ,  null   ,  0         ,  0));
			}
			var appContent = new Array(name, 'Global', nbJobs, runningJobs + pendingJobs);
			overviewAppContent.push(appContent);
   		}
   		catch(err){
   		console.log(err);
   		}
    }    	    	

}


/**
 * This retrieves registered data uid
 * This cancels all detail
 */
function getDatas()
{ 
    xmlHttpConnectionGetDatas=getXmlHttpObject();
    if (xmlHttpConnectionGetDatas==null)
    {
		return;
    }

    var url="/getdatas";
    xmlHttpConnectionGetDatas.onreadystatechange=getDatasStateChanged;
    xmlHttpConnectionGetDatas.open("POST",url,true);
    xmlHttpConnectionGetDatas.send(null);
}

/**
 * This handles getDatas events
 * For each retrieved data uid, this sets 
 * a new DIV with ID="the data uid".
 * This finally calls getData("the data uid") 
 * which will fill the data DIV.
 * @see getData(uid)
 */
function getDatasStateChanged()
{
	var current = xmlHttpConnectionGetDatas;
    if (current.readyState!=4)
    {
        return;
    }

    var xmlDoc = null;
    try {
    	if (xmlDoc=current.responseXML == null) {
    		if (current.status == 401) {
	    		document.documentElement.innerHTML=current.responseText;
	   			return;
    		}
    	}
    	xmlDoc=current.responseXML.documentElement;
	}
	catch(err) {
		connectionError();
		return;
	}
    // getdatas returns an XMLVECTOR containing some XMLVALUE
	var xmlTagName = "XMLVALUE";


    if(rpcError(xmlDoc) == true) {
        return;
	}

	document.getElementById(dataListID).innerHTML = 
		  "<div class=\"tupletitre\">"+
		    "<span class=\"selectbutton\">Select</span>" +
		    "<span class=\"firstvalue\">UID</span>" +
		    "<span class=\"value\">Name</span>" +
		    "<span class=\"value\">Status</span>" +
			"</div>";

	for (var i = 0; i < xmlDoc.getElementsByTagName(xmlTagName).length; i++) {
    	try {
			// this is the UID of the data
			var uid = xmlDoc.getElementsByTagName(xmlTagName)[i].getAttribute("value");

			var color = colors[i % 2];
				
			// this creates a new DIV with the ID=uid
        	document.getElementById(dataListID).innerHTML += 
        		"<div class=\"tuple\" style=\"background-color:" + color +
        		"\" id=\"" + uid + "\"></div>";

        	getData(uid);
    	}
    	catch(err){
    	}
    }    	
}

/**
 * This retrieves registered data from XWHEP server, given its uid
 * @param uid the UID of the data to retrieve
 */
function getData(uid)
{ 
    hashtableGetData[uid]=getXmlHttpObject();
    if (hashtableGetData[uid]==null)
    {
		return;
    }

    var url="/get/" + uid;
    hashtableGetData[uid].onreadystatechange=getDataStateChanged;
    hashtableGetData[uid].open("POST",url,true);
    hashtableGetData[uid].send(null);
}

/**
 * This handles getData events
 * This displays the data informations in its DIV
 *
 * @see getDatasStateChanged()
 */
function getDataStateChanged()
{
	for (var uid in hashtableGetData) {

	    if (hashtableGetData.hasOwnProperty(uid) == false) {
		    console.log("hashtableGetData.hasOwnProperty(" + uid + ") = false");
	    	continue;
    	}
    	
	    var current = hashtableGetData[uid];
	     
    	if (current.readyState!=4) {
	        continue;
    	}

	    var xmlDoc = null;
	    try {
	    	if (xmlDoc=current.responseXML == null) {
	    		if (current.status == 401) {
		    		document.documentElement.innerHTML=current.responseText;
		   			return;
	    		}
	    	}
	    	xmlDoc=current.responseXML.documentElement;
		}
		catch(err) {
			connectionError();
			return;
		}
    	// get returns an data XML object
		var xmlTagName = "data";
	

    	delete hashtableGetData[uid];

	    if(rpcError(xmlDoc) == true) {
	        return;
		}

   		try {
		    var name      = xmlDoc.getElementsByTagName("name").item(0).firstChild.nodeValue;
	    	var uid       = xmlDoc.getElementsByTagName("uid").item(0).firstChild.nodeValue;
	    	var uri       = xmlDoc.getElementsByTagName("uri").item(0).firstChild.nodeValue;
		    var status    = xmlDoc.getElementsByTagName("status").item(0).firstChild.nodeValue;
			var datauriid = datauriheader + uid;

		    console.log("Data " + name);
 		    var dataref="<span class=\"value\" id=\"" + datanameheader + uid + "\" \>" + name + "</span>";
			if(status == "AVAILABLE") {
        		dataref = "<span class=\"value\"><a href=\"/downloaddata/" + uid +"\" download=\"" + name + "\" id=\"" + datanameheader + uid + "\">" + name + "</a></span>";
			}
        	document.getElementById(uid).innerHTML = "<span class=\"selectbutton\"><input type=\"checkbox\" name=\"" + uid + "\" /></span>" +
        		"<span class=\"firstvalue\" id=\"" + datauidheader + uid + "\">" + uid + "</span>" +
        		dataref +
        		"<span class=\"value\">" + status + "</span>" +
   				"<span class=\"value\" style=\"display:none\" id=\"" + datauriid + "\">" + uri + "</span>";
   		}
   		catch(err){
   		}
    }    	    	
}

/**
 * This retrieves registered group of jobs uid
 * This cancels all detail
 */
function getBots()
{ 
    xmlHttpConnectionGetBots=getXmlHttpObject();
    if (xmlHttpConnectionGetBots==null)
    {
		return;
    }

    var url="/getgroups";
    xmlHttpConnectionGetBots.onreadystatechange=getBotsStateChanged;
    xmlHttpConnectionGetBots.open("POST",url,true);
    xmlHttpConnectionGetBots.send(null);
}

/**
 * This handles getBots events
 * For each retrieved group of jobs uid, this sets 
 * a new DIV with ID="the bot uid".
 * This finally calls getBot("the bot uid") 
 * which will fill the bot DIV.
 * @see getBot(uid)
 */
function getBotsStateChanged()
{ 
	var current = xmlHttpConnectionGetBots;
    if (current.readyState!=4)
    {
        return;
    }

    var xmlDoc = null;
    try {
    	if (xmlDoc=current.responseXML == null) {
    		if (current.status == 401) {
	    		document.documentElement.innerHTML=current.responseText;
	   			return;
    		}
    	}
    	xmlDoc=current.responseXML.documentElement;
	}
	catch(err) {
		connectionError();
		return;
	}
    // getdatas returns an XMLVECTOR containing some XMLVALUE
	var xmlTagName = "XMLVALUE";


    if(rpcError(xmlDoc) == true) {
        return;
	}

	document.getElementById(botListID).innerHTML = 
		  "<div class=\"tupletitre\">"+
		    "<span class=\"selectbutton\">Select</span>" +
		    "<span class=\"firstvalue\">UID</span>" +
		    "<span class=\"value\">Name</span>" +
//		    "<span class=\"value\">Session</span>" +
		"</div>";
	for (var i = 0; i < xmlDoc.getElementsByTagName(xmlTagName).length; i++) {
    	try {
			// this is the UID of the data
			var uid = xmlDoc.getElementsByTagName(xmlTagName)[i].getAttribute("value");

			var color = colors[i % 2];
				
			// this creates a new DIV with the ID=uid
        	document.getElementById(botListID).innerHTML += 
        		"<div class=\"tuple\" style=\"background-color:" + color +
        		"\" id=\"" + uid + "\"></div>";

        	getBot(uid);
    	}
    	catch(err){
    	}
    }    	
}

/**
 * This retrieves registered grouop of jobs from XWHEP server, given its uid
 * @param uid the UID of the bot to retrieve
 */
function getBot(uid)
{ 
    hashtableGetBot[uid]=getXmlHttpObject();
    if (hashtableGetBot[uid]==null)
    {
		return;
    }

    var url="/get/" + uid;
    hashtableGetBot[uid].onreadystatechange=getBotStateChanged;
    hashtableGetBot[uid].open("POST",url,true);
    hashtableGetBot[uid].send(null);
}

/**
 * This handles getBot events
 * This displays informations in its DIV
 * @see getBotsStateChanged()
 */
function getBotStateChanged()
{
	for (var uid in hashtableGetBot) {

	    if (hashtableGetBot.hasOwnProperty(uid) == false) {
		    console.log("hashtableGetBot.hasOwnProperty(" + uid + ") = false");
	    	continue;
    	}
    	
	    var current = hashtableGetBot[uid];
	     
    	if (current.readyState!=4) {
	        continue;
    	}

	    var xmlDoc = null;
	    try {
	    	if (xmlDoc=current.responseXML == null) {
	    		if (current.status == 401) {
		    		document.documentElement.innerHTML=current.responseText;
		   			return;
	    		}
	    	}
	    	xmlDoc=current.responseXML.documentElement;
		}
		catch(err) {
			connectionError();
			return;
		}
    	// get returns a group XML object
		var xmlTagName = "group";
	

    	delete hashtableGetBot[uid];

	    if(rpcError(xmlDoc) == true) {
	        return;
		}

   		try {
		    var name = xmlDoc.getElementsByTagName("name").item(0).firstChild.nodeValue;
	    	var uid  = xmlDoc.getElementsByTagName("uid").item(0).firstChild.nodeValue;

		    console.log("Data " + name);
			var divid = botjobsuidheader + uid;
        	document.getElementById(uid).innerHTML = 
        		"<span class=\"selectbutton\">" +
        		"<input type=\"checkbox\" name=\"" + uid + "\" />" +
        		"</span>" +
        		"<span class=\"firstvalue\" id=\"" + botuidheader + uid + "\">" + uid + "</span>" +
        		"<span class=\"value\">" + name + "</span>" +
//        		"<span class=\"lastvalue\">" + sessionuid + "</span>" +
	            "<span><a style=\"float:right;margin-right:15px;color:black\" href=\"javascript:showHide('" + divid + "');\" >show/hide</a></span>" +
				"<div id=\"" + divid + "\" class=\"groupworks\" style=\"display:none\"></div>";
   		}
   		catch(err){
   		}
    }    	    	
}

/**
 * This retrieves registered work uid
 * This cancels all detail
 */
function getWorks()
{ 
    xmlHttpConnectionGetWorks=getXmlHttpObject();
    if (xmlHttpConnectionGetWorks==null)
    {
		return;
    }

    var url = "/getworks";
	
    xmlHttpConnectionGetWorks.onreadystatechange=getWorksStateChanged;
    xmlHttpConnectionGetWorks.open("POST",url,true);
    xmlHttpConnectionGetWorks.send(null);
}

/**
 * This handles getWorks events
 * For each retrieved work uid, this sets 
 * a new DIV with ID="the work uid".
 * This finally calls getWork("the work uid") 
 * which will fill the work DIV.
 *
 * getWorkStateChange() will finally fill the DIV
 *
 * @see getWork(uid)
 */
function getWorksStateChanged()
{ 
	var current = xmlHttpConnectionGetWorks;
    if (current.readyState!=4)
    {
        return;
    }

    var xmlDoc = null;
    try {
    	if (xmlDoc=current.responseXML == null) {
    		if (current.status == 401) {
	    		document.documentElement.innerHTML=current.responseText;
	   			return;
    		}
    	}
    	xmlDoc=current.responseXML.documentElement;
	}
	catch(err) {
		connectionError();
		return;
	}
    // getworks returns an XMLVECTOR containing some XMLVALUE
	var xmlTagName = "XMLVALUE";


    if(rpcError(xmlDoc) == true) {
        return;
	}

	document.getElementById(pendingListID).innerHTML = 
		  "<div class=\"tupletitre\">"+
		    "<span class=\"selectbutton\">Select</span>" +
		    "<span class=\"firstvalue\">UID</span>" +
		    "<span class=\"value\">Application</span>" +
		    "<span class=\"value\">Status</span>" +
		    "<span class=\"value\">Label</span>" +
		    "<span class=\"value\">Error Msg</span>" +
		"</div>";
	document.getElementById(runningListID).innerHTML = 
		  "<div class=\"tupletitre\">"+
		    "<span class=\"selectbutton\">Select</span>" +
		    "<span class=\"firstvalue\">UID</span>" +
		    "<span class=\"value\">Application</span>" +
		    "<span class=\"value\">Status</span>" +
		    "<span class=\"value\">Label</span>" +
		    "<span class=\"value\">Error Msg</span>" +
		"</div>";
	document.getElementById(completedListID).innerHTML = 
		  "<div class=\"tupletitre\">"+
		    "<span class=\"selectbutton\">Select</span>" +
		    "<span class=\"firstvalue\">UID</span>" +
		    "<span class=\"value\">Application</span>" +
		    "<span class=\"value\">Status</span>" +
		    "<span class=\"value\">Label</span>" +
		    "<span class=\"value\">Error Msg</span>" +
		"</div>";
	document.getElementById(errorListID).innerHTML = 
		  "<div class=\"tupletitre\">"+
		    "<span class=\"selectbutton\">Select</span>" +
		    "<span class=\"firstvalue\">UID</span>" +
		    "<span class=\"value\">Application</span>" +
		    "<span class=\"value\">Status</span>" +
		    "<span class=\"value\">Label</span>" +
		    "<span class=\"value\">Error Msg</span>" +
		"</div>";

	for (var i = 0; i < xmlDoc.getElementsByTagName(xmlTagName).length; i++) {
    	try {
			// this is the UID of the work
			var uid = xmlDoc.getElementsByTagName(xmlTagName)[i].getAttribute("value");

			var color = colors[i % 2];
				
			// we don't know this work status yet
			// just in case, we create a new DIV in each list
			var pendingid   = pendingheader + uid;
			var runningid   = runningheader + uid;
			var completedid = completedheader + uid;
			var errorid     = errorheader + uid;
        	document.getElementById(pendingListID).innerHTML += 
        		"<div class=\"tuple\" style=\"background-color:" + color +
        		"\" id=\"" + pendingid + "\"></div>";
        	hide(pendingid);			
        	document.getElementById(runningListID).innerHTML += 
        		"<div class=\"tuple\" style=\"background-color:" + color +
        		"\" id=\"" + runningid + "\"></div>";
        	hide(runningid);			
        	document.getElementById(completedListID).innerHTML += 
        		"<div class=\"tuple\" style=\"background-color:" + color +
        		"\" id=\"" + completedid + "\"></div>";
        	hide(completedid);			
        	document.getElementById(errorListID).innerHTML += 
        		"<div class=\"tuple\" style=\"background-color:" + color +
        		"\" id=\"" + errorid + "\"></div>";
        	hide(errorid);

        	getWork(uid);
    	}
    	catch(err){
    	}
    }    	
}

/**
 * This retrieves registered work from XWHEP server, given its uid
 * @param uid the UID of the work to retrieve
 */
function getWork(uid)
{
    var url="/get/" + uid;

	var useWebWorker = 1;
	
	var elem = document.getElementById("useWebWorker");
	try {
		useWebWorker = elem.checked;
	}
	catch(err) {
		console.log("getWork("+uid+") error " + err);
	}
 
	if((useWebWorker == 1) && (typeof(Worker)!=="undefined"))
	{
/*
    	window.URL = window.URL || window.webkiURL;
    	var getURL = document.URL + url;
    	console.log("getURL = " + getURL);
		var blob = new Blob([document.querySelector('#webWorkerGetScript').textContent]);
		webWorkerPool.queueJob(window.URL.createObjectURL(blob),
						getURL,
						webWorkerGetWorkOnMessage,
                		this);
*/
		webWorkerPool.queueJob(new Worker('http://www.xtremweb-hep.org/lal/xwserver_webworker.js'),
						getURL,
						webWorkerGetWorkOnMessage,
                		this);
	  	return;
	}
	else
	{
 	   hashtableGetWork[uid]=getXmlHttpObject();
    	if (hashtableGetWork[uid]==null)
    	{
			return;
    	}

   		hashtableGetWork[uid].onreadystatechange=getWorkStateChanged;
	    hashtableGetWork[uid].open("POST",url,true);
    	hashtableGetWork[uid].send(null);
	}
}

/**
 * This is called by the WorkerPool
 * This must return true to force worker termination !
 * @param worker is the calling worker from the WorkerPool
 * @param event is a JSon message from the worker
 * @return true
 */
function webWorkerGetWorkOnMessage(worker,event) {
	var data = event.data;
	console.log("Web Worker data cmd : " + data.cmd);
	switch (data.cmd) {
	case 'log' :
		console.log(data.content);
		break;
	case 'error' :
		console.log(data.content);
		break;
	case 'answer' :
		var xmlDoc = stringToXml(data.content);
		displayWork(xmlDoc);
		break;
	}
	return true;
}

/**
 * This handles getWork events
 * This displays the work informations in its DIV
 *
 * @see getWorksStateChanged()
 */
function getWorkStateChanged()
{
	for (var uid in hashtableGetWork) {

	    if (hashtableGetWork.hasOwnProperty(uid) == false) {
		    console.log("hashtableGetWork.hasOwnProperty(" + uid + ") = false");
	    	continue;
    	}
    	
	    var current = hashtableGetWork[uid];
	     
    	if (current.readyState!=4) {
	        continue;
    	}

	    var xmlDoc = null;
	    try {
	    	if (xmlDoc=current.responseXML == null) {
	    		if (current.status == 401) {
		    		document.documentElement.innerHTML=current.responseText;
		   			return;
	    		}
	    	}
	    	xmlDoc=current.responseXML.documentElement;
		}
		catch(err) {
			connectionError();
			return;
		}

    	delete hashtableGetWork[uid];
    	displayWork(xmlDoc);
	}
}

function displayWork(xmlDoc)
{
	// get returns an work XML object
	var xmlTagName = "work";

    if(rpcError(xmlDoc) == true) {
        return;
	}

	try {
    	var uid  = xmlDoc.getElementsByTagName("uid").item(0).firstChild.nodeValue;
		var pendingid   = pendingheader   + uid;
		var runningid   = runningheader   + uid;
		var completedid = completedheader + uid;
		var datauriid   = datauriheader   + uid;
		var errorid     = errorheader     + uid;
    	var appuid      = xmlDoc.getElementsByTagName("appuid").item(0).firstChild.nodeValue;
    	var appnameid = appnameheader + appuid;
    	var appname = null;
    	try {
    		appname = document.getElementById(appnameid).innerHTML;
		} catch(err) {
		}
    	var status = xmlDoc.getElementsByTagName("status").item(0).firstChild.nodeValue;
	    var label = null;
	    try {
		    label = xmlDoc.getElementsByTagName("label").item(0).firstChild.nodeValue
		} catch(err) {
		}
	    var error_msg = null;
		try {
		    error_msg = xmlDoc.getElementsByTagName("errormsg").item(0).firstChild.nodeValue;
		} catch(err) {
		}

	    var groupuid = null;
	    try {
		    groupuid = xmlDoc.getElementsByTagName("groupuid").item(0).firstChild.nodeValue;
		} catch(err) {
		}

		var groupdivid = null;
		if((groupuid != null) && (groupuid != ""))
			groupdivid = botjobsuidheader + groupuid;
		var groupdiv = null;
		if(groupdivid != null)
			document.getElementById(groupdivid);
		console.log("groupuid = " + groupuid + " groupdivid = " + groupdivid + " " + groupdiv);

	    var resulturi = null;
		var resultuid = null;
	    try {
		    resulturi = xmlDoc.getElementsByTagName("resulturi").item(0).firstChild.nodeValue;
		} catch(err) {
		}
		    
		if((resulturi != null) && (resulturi.length > 0)) {
			var lastindex = resulturi.lastIndexOf("/");
			resultuid = resulturi.substring(lastindex + 1, resulturi.length);
		}
		var resultname = resultHeader + uid;
    	var datanameid = datanameheader + resultuid;
    	var datauriid  = datauriheader + resultuid;
	    if(document.getElementById(datanameid) != null)
		    resultname = document.getElementById(datanameid).innerHTML;

	    console.log("work " + uid + " " + groupuid + " " + appname + " " + status + " " + error_msg + " " + resultuid);

		var id = pendingid;
		var workbkgcolor = colorpending;
		
		switch(status) {
			case "RUNNING":
				id = runningid;
				workbkgcolor = colorrunning;
    		break;
			case "COMPLETED":
				id = completedid;
				workbkgcolor = colorcompleted;
    		break;
			case "ERROR":
				id = errorid;
				workbkgcolor = colorerror;
    		break;
   		}

   		var theBody =
   			"<span class=\"firstvalue\">" + uid + "</span>" +
			"<span class=\"value\" >" + (appname != null ? appname : appuid) + "</span>";
		if(resultuid != null)
			theBody += "<span class=\"value\"><a href=\"/downloaddata/" + resultuid +"\" download=\"" + resultname+ "\">" + status + "</a></span>";
		else				
			theBody += "<span class=\"value\">" + status + "</span>";
		theBody += "<span class=\"value\">" + label + "</span>" +
			"<span class=\"value\">" + error_msg + "</span>" +
			"<span class=\"lastvalue\" style=\"display:none\" id=\"" + datauriid + "\">" + resulturi + "</span>";

   		document.getElementById(id).innerHTML = "<span class=\"selectbutton\"><input type=\"checkbox\" name=\"" + uid + "\" /></span>" + theBody;
		show(id);
		if(groupdiv != null) {
			groupdiv.innerHTML += "<div class=\"tuple\" style=\"background-color:" + workbkgcolor + "\">" + theBody + "</div>";
			show(groupdivid);
   		}
	}
	catch(err){
		console.log("displayWork error " + err);
	}
}

/**
 * This details all checked object
 */
function details() {

	var theBody = "<div class=\"modalContent\">" +
    "<div class=\"modalpane\">" +
      "<div class=\"modaltitre\"><span >Details</span></div>" +
      "<div id=\"" + detailListID + "\" style=\"display:block\">" +
      "</div><div class=\"modaltrailer\">&nbsp;</div>" +
    "</div></div>";

	var formElem = getCurrentForm();

	var count = 0;
	for (var i = 0; i < formElem.length; i++) {
		try {
			if(formElem[i].checked == false) {
				continue;
			}

			if(count > 0) {
				theBody = warningContent("You have selected more than one row.<br />" +
						"This displays one row only");
			  	break;
		   	}

			count++;
			var uid = formElem[i].name;
			getDetail(uid);
		}
		catch(err) {
		}
	}

	console.log("count = " + count);
	if(count < 1) {
		error("You must select a row");
		return;
	}
	var pop = new myPop();
    pop.popOut(theBody);
	pop = null;
}

/**
 * This retrieves an object from XWHEP server, given its uid
 * @param uid the UID of the object to retrieve
 */
function getDetail(uid)
{ 
	console.log("getDetail(" + uid + ")");
    hashtableGetDetail[uid]=getXmlHttpObject();
    if (hashtableGetDetail[uid]==null)
    {
		return;
    }

    var url="/get/" + uid;
    hashtableGetDetail[uid].onreadystatechange=getDetailStateChanged;
    hashtableGetDetail[uid].open("POST",url,true);
    hashtableGetDetail[uid].send(null);
}

/**
 * This handles getDetail events
 * This displays the object informations in its DIV
 */
function getDetailStateChanged()
{
	for (var uid in hashtableGetDetail) {

	    if (hashtableGetDetail.hasOwnProperty(uid) == false) {
		    console.log("hashtableGetDetail.hasOwnProperty(" + uid + ") = false");
	    	continue;
    	}
    	
	    var current = hashtableGetDetail[uid];
	     
    	if (current.readyState!=4) {
	        continue;
    	}

    	delete hashtableGetDetail[uid];

	    var xmlDoc = null;
	    try {
	    	if (xmlDoc=current.responseXML == null) {
	    		if (current.status == 401) {
		    		document.documentElement.innerHTML=current.responseText;
		   			return;
	    		}
	    	}
	    	xmlDoc=current.responseXML.documentElement;
		}
		catch(err) {
			connectionError();
			return;
		}
	

	    if(rpcError(xmlDoc) == true) {
	        return;
    	}

   		try {
			var theBody = "";
			var colorcounter = 0;
			var color = colors[colorcounter++ % 2];
			
			if(document.getElementById(appsTabID).getAttribute("class") == "current") {

				var xmlTagName = "app";

			    var name = xmlDoc.getElementsByTagName("name").item(0).firstChild.nodeValue;
		    	var uid = xmlDoc.getElementsByTagName("uid").item(0).firstChild.nodeValue;
			    var accessrights = xmlDoc.getElementsByTagName("accessrights").item(0).firstChild.nodeValue;
			    var type = xmlDoc.getElementsByTagName("type").item(0).firstChild.nodeValue;

        		theBody = "<div class=\"tupledetail\" style=\"background-color:" + color + "\"><span class=\"firstdetail\">UID</span>" +
        			"<span class=\"lastdetail\">" + uid + "</span></div>";
				color = colors[colorcounter++ % 2];
        		theBody += "<div class=\"tupledetail\" style=\"background-color:" + color + "\"><span class=\"firstdetail\">Name</span>" + 
	        		"<span class=\"lastdetail\">" + name + "</span></div>";
				color = colors[colorcounter++ % 2];
        		theBody += "<div class=\"tupledetail\" style=\"background-color:" + color + "\"><span class=\"firstdetail\">Access Rights</span>" + 
	        		"<span class=\"lastdetail\">" + accessrights + "</span></div>";
				color = colors[colorcounter++ % 2];
        		theBody += "<div class=\"tupledetail\" style=\"background-color:" + color + "\"><span class=\"firstdetail\">Type</span>" + 
	        		"<span class=\"lastdetail\">" + type + "</span></div>";

				try {
			    	var thedata = xmlDoc.getElementsByTagName("linux_ix86uri").item(0).firstChild.nodeValue;
			    	if((thedata != null) && (thedata != "")) {
						color = colors[colorcounter++ % 2];
	        			theBody += "<div class=\"tupledetail\" style=\"background-color:" + color + "\"><span class=\"firstdetail\">Linux ix86</span>" + 
		        			"<span class=\"lastdetail\">Yes</span></div>";
			   		}
		   		}
		   		catch(err) {
		   		}
				try {
			    	var thedata = xmlDoc.getElementsByTagName("linux_amd64uri").item(0).firstChild.nodeValue;
			    	if((thedata != null) && (thedata != "")) {
						color = colors[colorcounter++ % 2];
		        		theBody += "<div class=\"tupledetail\" style=\"background-color:" + color + "\"><span class=\"firstdetail\">Linux amd64</span>" + 
			        		"<span class=\"lastdetail\">Yes</span></div>";
			   		}
		   		}
		   		catch(err) {
		   		}
				try {
					var thedata = xmlDoc.getElementsByTagName("linux_x86_64uri").item(0).firstChild.nodeValue;
			    	if((thedata != null) && (thedata != "")) {
						color = colors[colorcounter++ % 2];
		        		theBody += "<div class=\"tupledetail\" style=\"background-color:" + color + "\"><span class=\"firstdetail\">Linux x86_64</span>" + 
			        		"<span class=\"lastdetail\">Yes</span></div>";
			   		}
		   		}
		   		catch(err) {
		   		}
				try {
					var thedata = xmlDoc.getElementsByTagName("linux_ia64uri").item(0).firstChild.nodeValue;
			    	if((thedata != null) && (thedata != "")) {
						color = colors[colorcounter++ % 2];
		        		theBody += "<div class=\"tupledetail\" style=\"background-color:" + color + "\"><span class=\"firstdetail\">Linux ia64</span>" + 
			        		"<span class=\"lastdetail\">Yes</span></div>";
			   		}
		   		}
		   		catch(err) {
		   		}
				try {
					var thedata = xmlDoc.getElementsByTagName("linux_ppcuri").item(0).firstChild.nodeValue;
			    	if((thedata != null) && (thedata != "")) {
						color = colors[colorcounter++ % 2];
	    	    		theBody += "<div class=\"tupledetail\" style=\"background-color:" + color + "\"><span class=\"firstdetail\">Linux PPC</span>" + 
			        		"<span class=\"lastdetail\">Yes</span></div>";
			   		}
		   		}
		   		catch(err) {
		   		}
				try {
					var thedata = xmlDoc.getElementsByTagName("macos_ix86uri").item(0).firstChild.nodeValue;
			    	if((thedata != null) && (thedata != "")) {
						color = colors[colorcounter++ % 2];
		        		theBody += "<div class=\"tupledetail\" style=\"background-color:" + color + "\"><span class=\"firstdetail\">Mac OS ix86</span>" + 
			        		"<span class=\"lastdetail\">Yes</span></div>";
			   		}
		   		}
		   		catch(err) {
		   		}
				try {
					var thedata = xmlDoc.getElementsByTagName("macos_x86_64uri").item(0).firstChild.nodeValue;
			    	if((thedata != null) && (thedata != "")) {
						color = colors[colorcounter++ % 2];
	    	    		theBody += "<div class=\"tupledetail\" style=\"background-color:" + color + "\"><span class=\"firstdetail\">Mac OS x86_64</span>" + 
			        		"<span class=\"lastdetail\">Yes</span></div>";
			   		}
		   		}
		   		catch(err) {
		   		}
				try {
					var thedata = xmlDoc.getElementsByTagName("macos_ppcuri").item(0).firstChild.nodeValue;
			    	if((thedata != null) && (thedata != "")) {
						color = colors[colorcounter++ % 2];
	    	    		theBody += "<div class=\"tupledetail\" style=\"background-color:" + color + "\"><span class=\"firstdetail\">Mac OS PPC</span>" + 
			        		"<span class=\"lastdetail\">Yes</span></div>";
			   		}
		   		}
		   		catch(err) {
		   		}
				try {
					var thedata = xmlDoc.getElementsByTagName("win32_ix86uri").item(0).firstChild.nodeValue;
			    	if((thedata != null) && (thedata != "")) {
						color = colors[colorcounter++ % 2];
	    	    		theBody += "<div class=\"tupledetail\" style=\"background-color:" + color + "\"><span class=\"firstdetail\">Win32 ix86</span>" + 
			        		"<span class=\"lastdetail\">Yes</span></div>";
			   		}
		   		}
		   		catch(err) {
		   		}
				try {
					var thedata = xmlDoc.getElementsByTagName("win32_amd64uri").item(0).firstChild.nodeValue;
			    	if((thedata != null) && (thedata != "")) {
						color = colors[colorcounter++ % 2];
	    	    		theBody += "<div class=\"tupledetail\" style=\"background-color:" + color + "\"><span class=\"firstdetail\">Win32 amd64</span>" + 
			        		"<span class=\"lastdetail\">Yes</span></div>";
			   		}
		   		}
		   		catch(err) {
		   		}
				try {
					var thedata = xmlDoc.getElementsByTagName("win32_x86_64uri").item(0).firstChild.nodeValue;
			    	if((thedata != null) && (thedata != "")) {
						color = colors[colorcounter++ % 2];
	    	    		theBody += "<div class=\"tupledetail\" style=\"background-color:" + color + "\"><span class=\"firstdetail\">Win32 x86_64</span>" + 
			        		"<span class=\"lastdetail\">Yes</span></div>";
			   		}
		   		}
		   		catch(err) {
		   		}
				try {
					var thedata = xmlDoc.getElementsByTagName("javauri").item(0).firstChild.nodeValue;
			    	if((thedata != null) && (thedata != "")) {
						color = colors[colorcounter++ % 2];
	    	    		theBody += "<div class=\"tupledetail\" style=\"background-color:" + color + "\"><span class=\"firstdetail\">Java</span>" + 
			        		"<span class=\"lastdetail\">Yes</span></div>";
			   		}
		   		}
		   		catch(err) {
		   		}
	   		}

			if(document.getElementById(datasTabID).getAttribute("class") == "current") {

				var xmlTagName = "data";

			    var name   = xmlDoc.getElementsByTagName("name").item(0).firstChild.nodeValue;
		    	var uid    = xmlDoc.getElementsByTagName("uid").item(0).firstChild.nodeValue;
		    	var type   = xmlDoc.getElementsByTagName("type").item(0).firstChild.nodeValue;
		    	var os     = xmlDoc.getElementsByTagName("os").item(0).firstChild.nodeValue;
		    	var cpu    = xmlDoc.getElementsByTagName("cpu").item(0).firstChild.nodeValue;
		    	var status = xmlDoc.getElementsByTagName("status").item(0).firstChild.nodeValue;
		    	var links  = xmlDoc.getElementsByTagName("links").item(0).firstChild.nodeValue;
		    	var accessrights = xmlDoc.getElementsByTagName("accessrights").item(0).firstChild.nodeValue;
		    	var accessdate   = xmlDoc.getElementsByTagName("mtime").item(0).firstChild.nodeValue;

				color = colors[colorcounter++ % 2];
        		theBody = "<div class=\"tupledetail\" style=\"background-color:" + color + "\"><span class=\"firstdetail\">UID</span>" +
        			"<span class=\"lastdetail\">" + uid + "</span></div>";
				color = colors[colorcounter++ % 2];
        		theBody += "<div class=\"tupledetail\" style=\"background-color:" + color + "\"><span class=\"firstdetail\">Name</span>" + 
	        		"<span class=\"lastdetail\">" + name + "</span></div>";
				color = colors[colorcounter++ % 2];
        		theBody += "<div class=\"tupledetail\" style=\"background-color:" + color + "\"><span class=\"firstdetail\">Status</span>" + 
	        		"<span class=\"lastdetail\">" + status + "</span></div>";
				color = colors[colorcounter++ % 2];
        		theBody += "<div class=\"tupledetail\" style=\"background-color:" + color + "\"><span class=\"firstdetail\">Access Rights</span>" + 
	        		"<span class=\"lastdetail\">" + accessrights + "</span></div>";
				color = colors[colorcounter++ % 2];
        		theBody += "<div class=\"tupledetail\" style=\"background-color:" + color + "\"><span class=\"firstdetail\">Last access</span>" + 
	        		"<span class=\"lastdetail\">" + accessdate + "</span></div>";
				color = colors[colorcounter++ % 2];
        		theBody += "<div class=\"tupledetail\" style=\"background-color:" + color + "\"><span class=\"firstdetail\">Links</span>" + 
	        		"<span class=\"lastdetail\">" + links + "</span></div>";
        		if((type != null) && (type != "")) {
					color = colors[colorcounter++ % 2];
        			theBody += "<div class=\"tupledetail\" style=\"background-color:" + color + "\"><span class=\"firstdetail\">Type</span>" + 
	        			"<span class=\"lastdetail\">" + type + "</span></div>";
		   		}
        		if((os != null) && (os != "")) {
					color = colors[colorcounter++ % 2];
        			theBody += "<div class=\"tupledetail\" style=\"background-color:" + color + "\"><span class=\"firstdetail\">OS</span>" + 
	        			"<span class=\"lastdetail\">" + os + "</span></div>";
		   		}
        		if((cpu != null) && (cpu != "")) {
					color = colors[colorcounter++ % 2];
        			theBody += "<div class=\"tupledetail\" style=\"background-color:" + color + "\"><span class=\"firstdetail\">CPU</span>" + 
	        			"<span class=\"lastdetail\">" + cpu + "</span></div>";
		   		}
	   		}

			if(document.getElementById(worksTabID).getAttribute("class") == "current") {

				var xmlTagName = "work";

		    	var uid    = xmlDoc.getElementsByTagName("uid").item(0).firstChild.nodeValue;
		    	var appuid = xmlDoc.getElementsByTagName("appuid").item(0).firstChild.nodeValue;
		    	var appnameid = appnameheader + appuid;
		    	var appname = document.getElementById(appnameid).innerHTML;

				color = colors[colorcounter++ % 2];
        		theBody = "<div class=\"tupledetail\" style=\"background-color:" + color + "\"><span class=\"firstdetail\">UID</span>" +
        			"<span class=\"lastdetail\">" + uid + "</span></div>";
				color = colors[colorcounter++ % 2];
        		theBody += "<div class=\"tupledetail\" style=\"background-color:" + color + "\"><span class=\"firstdetail\">Access Rights</span>" + 
	        		"<span class=\"lastdetail\">" + 
	        		xmlDoc.getElementsByTagName("accessrights").item(0).firstChild.nodeValue +
	        		 "</span></div>";
				color = colors[colorcounter++ % 2];
        		theBody += "<div class=\"tupledetail\" style=\"background-color:" + color + "\"><span class=\"firstdetail\">Application</span>" + 
	        		"<span class=\"lastdetail\">" + appname + "</span></div>";
				color = colors[colorcounter++ % 2];
        		theBody += "<div class=\"tupledetail\" style=\"background-color:" + color + "\"><span class=\"firstdetail\">Status</span>" + 
	        		"<span class=\"lastdetail\">" + 
	        		xmlDoc.getElementsByTagName("status").item(0).firstChild.nodeValue +
	        		 "</span></div>";

				try {
					var thedata = xmlDoc.getElementsByTagName("sessionuid").item(0).firstChild.nodeValue;
					if(thedata != null) {
						color = colors[colorcounter++ % 2];
		        		theBody += "<div class=\"tupledetail\" style=\"background-color:" +
		        			 		color + "\"><span class=\"firstdetail\">Session</span>" + 
			        		"<span class=\"lastdetail\">" + thedata + "</span></div>";
			   		}
		   		}
		   		catch(err) {
		   		}
				try {
					var thedata = xmlDoc.getElementsByTagName("groupuid").item(0).firstChild.nodeValue;
					if(thedata != null) {
						color = colors[colorcounter++ % 2];
	        			theBody += "<div class=\"tupledetail\" style=\"background-color:" +
	        				 		color + "\"><span class=\"firstdetail\">Group of jobs</span>" + 
		    	    		"<span class=\"lastdetail\">" + thedata + "</span></div>";
			   		}
		   		}
		   		catch(err) {
		   		}
				try {
					var thedata = xmlDoc.getElementsByTagName("listenport").item(0).firstChild.nodeValue;
					if(thedata != null) {
						color = colors[colorcounter++ % 2];
		        		theBody += "<div class=\"tupledetail\" style=\"background-color:" +
		        			 		color + "\"><span class=\"firstdetail\">Listen port</span>" + 
			        		"<span class=\"lastdetail\">" + thedata + "</span></div>";
			   		}
		   		}
		   		catch(err) {
		   		}
				try {
					var thedata = xmlDoc.getElementsByTagName("smartsocketaddr").item(0).firstChild.nodeValue;
					if(thedata != null) {
						color = colors[colorcounter++ % 2];
		        		theBody += "<div class=\"tupledetail\" style=\"background-color:" +
		        			 		color + "\"><span class=\"firstdetail\">Smartsocket server address</span>" + 
			        		"<span class=\"lastdetail\">" + thedata + "</span></div>";
			   		}
		   		}
		   		catch(err) {
		   		}
				try {
					var thedata = xmlDoc.getElementsByTagName("smartsocketclient").item(0).firstChild.nodeValue;
					if(thedata != null) {
						color = colors[colorcounter++ % 2];
		        		theBody += "<div class=\"tupledetail\" style=\"background-color:" +
		        			 		color + "\"><span class=\"firstdetail\">Smartsocket client address</span>" + 
			        		"<span class=\"lastdetail\">" + thedata + "</span></div>";
			   		}
		   		}
		   		catch(err) {
		   		}
				try {
					var thedata = xmlDoc.getElementsByTagName("label").item(0).firstChild.nodeValue;
					if(thedata != null) {
						color = colors[colorcounter++ % 2];
		        		theBody += "<div class=\"tupledetail\" style=\"background-color:" +
		        			 		color + "\"><span class=\"firstdetail\">Label</span>" + 
			        		"<span class=\"lastdetail\">" + thedata + "</span></div>";
			   		}
		   		}
		   		catch(err) {
		   		}
				try {
					var thedata = xmlDoc.getElementsByTagName("errormsg").item(0).firstChild.nodeValue;
					if(thedata != null) {
						color = colors[colorcounter++ % 2];
		        		theBody += "<div class=\"tupledetail\" style=\"background-color:" +
		        			 		color + "\"><span class=\"firstdetail\">Error msg</span>" + 
			        		"<span class=\"lastdetail\">" + thedata + "</span></div>";
			   		}
		   		}
		   		catch(err) {
		   		}
				try {
					var thedata = xmlDoc.getElementsByTagName("arrivaldate").item(0).firstChild.nodeValue;
					if(thedata != null) {
						color = colors[colorcounter++ % 2];
		        		theBody += "<div class=\"tupledetail\" style=\"background-color:" +
		        			 		color + "\"><span class=\"firstdetail\">Arrival date</span>" + 
			        		"<span class=\"lastdetail\">" + thedata + "</span></div>";
			   		}
		   		}
		   		catch(err) {
		   		}
				try {
					var thedata = xmlDoc.getElementsByTagName("readydate").item(0).firstChild.nodeValue;
					if(thedata != null) {
						color = colors[colorcounter++ % 2];
		        		theBody += "<div class=\"tupledetail\" style=\"background-color:" +
		        					color + "\"><span class=\"firstdetail\">Ready date</span>" + 
			        		"<span class=\"lastdetail\">" + thedata + "</span></div>";
			   		}
		   		}
		   		catch(err) {
		   		}
				try {
					var thedata = xmlDoc.getElementsByTagName("datareadydate").item(0).firstChild.nodeValue;
					if(thedata != null) {
						color = colors[colorcounter++ % 2];
		        		theBody += "<div class=\"tupledetail\" style=\"background-color:" +
		        					color + "\"><span class=\"firstdetail\">Data ready date</span>" + 
			        		"<span class=\"lastdetail\">" + thedata + "</span></div>";
			   		}
		   		}
		   		catch(err) {
		   		}
				try {
					var thedata = xmlDoc.getElementsByTagName("compstartdate").item(0).firstChild.nodeValue;
					if(thedata != null) {
						color = colors[colorcounter++ % 2];
		        		theBody += "<div class=\"tupledetail\" style=\"background-color:" +
		        					color + "\"><span class=\"firstdetail\">Computation start date</span>" + 
			        		"<span class=\"lastdetail\">" +  thedata + "</span></div>";
			   		}
		   		}
		   		catch(err) {
		   		}
				try {
					var thedata = xmlDoc.getElementsByTagName("compenddate").item(0).firstChild.nodeValue;
					if(thedata != null) {
						color = colors[colorcounter++ % 2];
		        		theBody += "<div class=\"tupledetail\" style=\"background-color:" +
		        					color + "\"><span class=\"firstdetail\">Computation end date</span>" + 
			        		"<span class=\"lastdetail\">" + thedata + "</span></div>";
			   		}
		   		}
		   		catch(err) {
		   		}
				try {
					var thedata = xmlDoc.getElementsByTagName("completeddate").item(0).firstChild.nodeValue;
					if(thedata != null) {
						color = colors[colorcounter++ % 2];
		        		theBody += "<div class=\"tupledetail\" style=\"background-color:" +
		        					color + "\"><span class=\"firstdetail\">Completion date</span>" + 
			        		"<span class=\"lastdetail\">" +  thedata + "</span></div>";
			   		}
		   		}
		   		catch(err) {
		   		}
				try {
					var thedata = xmlDoc.getElementsByTagName("resultdate").item(0).firstChild.nodeValue;
					if(thedata != null) {
						color = colors[colorcounter++ % 2];
		        		theBody += "<div class=\"tupledetail\" style=\"background-color:" +
		        					color + "\"><span class=\"firstdetail\">Result date</span>" + 
			        		"<span class=\"lastdetail\">" +  thedata + "</span></div>";
			   		}
		   		}
		   		catch(err) {
		   		}
	   		}

			if(document.getElementById(botsTabID).getAttribute("class") == "current") {

				var xmlTagName = "group";

			    var name = xmlDoc.getElementsByTagName("name").item(0).firstChild.nodeValue;
		    	var uid = xmlDoc.getElementsByTagName("uid").item(0).firstChild.nodeValue;

				color = colors[colorcounter++ % 2];
        		theBody = "<div class=\"tupledetail\" style=\"background-color:" + color + "\"><span class=\"firstdetail\">UID</span>" +
        			"<span class=\"lastdetail\">" + uid + "</span></div>";
				color = colors[colorcounter++ % 2];
        		theBody += "<div class=\"tupledetail\" style=\"background-color:" + color + "\"><span class=\"firstdetail\">Access Rights</span>" + 
	        		"<span class=\"lastdetail\">" + 
	        		xmlDoc.getElementsByTagName("accessrights").item(0).firstChild.nodeValue  + 
	        		 "</span></div>";
				color = colors[colorcounter++ % 2];
        		theBody += "<div class=\"tupledetail\" style=\"background-color:" + color + "\"><span class=\"firstdetail\">Name</span>" + 
	        		"<span class=\"lastdetail\">" + name + "</span></div>";
				color = colors[colorcounter++ % 2];

				try {
					var thedata = xmlDoc.getElementsByTagName("sessionuid").item(0).firstChild.nodeValue;
					if(thedata != null) {
						color = colors[colorcounter++ % 2];
		        		theBody += "<div class=\"tupledetail\" style=\"background-color:" +
		        			 		color + "\"><span class=\"firstdetail\">Session</span>" + 
			        		"<span class=\"lastdetail\">" + thedata + "</span></div>";
			   		}
		   		}
		   		catch(err) {
		   		}
	   		}

        	document.getElementById(detailListID).innerHTML = theBody;
   		}
   		catch(err){
   		}
    }    	    	
}

/**
 * This an object
 * @see sendApp()
 */
function send() {

	if(document.getElementById(appsTabID).getAttribute("class") == "current") {
		sendApp();
		return;
	}
	if(document.getElementById(datasTabID).getAttribute("class") == "current") {
		sendData();
		return;
	}
	if(document.getElementById(worksTabID).getAttribute("class") == "current") {
		sendWork();
		return;
	}
	if(document.getElementById(botsTabID).getAttribute("class") == "current") {
		sendBot();
		return;
	}
}

/**
 * This handles sendDataToServer events
 * @see sendDataToServer()
 */
function sendStateChanged()
{
    var current = xmlHttpConnectionSend;
     
	if (current.readyState!=4) {
        return;
	}

    var xmlDoc = null;
    try {
    	if (xmlDoc=current.responseXML == null) {
    		if (current.status == 401) {
	    		document.documentElement.innerHTML=current.responseText;
	   			return;
    		}
    	}
    	xmlDoc=current.responseXML.documentElement;
	}
	catch(err) {
		connectionError();
		return;
	}

    if(rpcError(xmlDoc) == true) {
    	return;
	}

	alert("Please use the \"Refresh\" button");
}

/**
 * This an object
 */
function sendApp() {

	if(document.getElementById(appsTabID).getAttribute("class") != "current") {
		return;
	}
	alert("not implemented yet");
}

/**
 * This sends a new data
 */
function sendData() {

	if(document.getElementById(datasTabID).getAttribute("class") != "current") {
		return;
	}

	var theBody = "<div class=\"modalContent\">" +
    "<div class=\"modalpane\">" +
      "<div class=\"modaltitre\">" +
        "<span >Send new data</span>" +
      "</div>" +
      "<div style=\"display:block\">" +
	   "<form id=\"" + sendFormID + "\" >" +
		"<div class=\"tupledetail\" style=\"background-color:" + colors[0] + "\">" +
			"<span class=\"firstdetail\">Name</span>" +
			"<span class=\"lastdetail\"><input type=\"text\" name=\"" + nameInputName + "\" /></span>" +
		"</div>" +
		"<div class=\"tupledetail\" style=\"background-color:" + colors[1] + "\">" +
			"<span class=\"firstdetail\">AccessRights</span>" +
			"<span class=\"lastdetail\"><input type=\"text\" name=\"" + arInputName + "\" /></span>" +
			"<span>" + toolTip("?", arTooltip) + "</span>" + 
		"</div>" +
		"<div class=\"tupledetail\" style=\"background-color:" + colors[0] + "\">" +
			"<span class=\"firstdetail\">Type</span>" +
			"<span class=\"lastdetail\"><select name=\"" + menuTypeInputName + "\">"+
				"<option value=\"\" />Any" +
				"<option value=\"BINARY\" />Binary" +
				"<option value=\"ISO\" />ISO" +
				"<option value=\"VDI\" />VDI" +
				"<option value=\"VMDK\" />VMDK" +
				"<option value=\"JAVA\" />Java" +
				"<option value=\"TEXT\" />Text" +
				"<option value=\"ZIP\" />Zip" +
				"<option value=\"URIPASSTHROUGH\" />URI pass through" +
				"<option value=\"SH\" />Shell script" +
				"<option value=\"BAT\" />Cmd script" +
				"<option value=\"X509\" />X509" +
			"</select></span>" +
			"<span>" + toolTip("?", "Type is optionnal") + "</span>" + 
		"</div>" +
		"<div class=\"tupledetail\" style=\"background-color:" + colors[1] + "\">" +
			"<span class=\"firstdetail\">OS</span>" +
			"<span class=\"lastdetail\"><select name=\"" + menuOsInputName + "\">"+
				"<option value=\"\" />Any" +
				"<option value=\"LINUX\" />Linux" +
				"<option value=\"WIN32\" />Win32" +
				"<option value=\"MACOSX\" />Mac OS X" +
				"<option value=\"JAVA\" />Java" +
			"</select></span>" +
			"<span>" + toolTip("?", "OS is optionnal") + "</span>" + 
		"</div>" +
		"<div class=\"tupledetail\" style=\"background-color:" + colors[0] + "\">" +
			"<span class=\"firstdetail\">CPU</span>" +
			"<span class=\"lastdetail\"><select name=\"" + menuCpuInputName + "\">"+
				"<option value=\"\" />Any" +
				"<option value=\"IX86\" />IX86" +
				"<option value=\"X86_64\" />X86_64" +
				"<option value=\"AMD64\" />AMD64" +
				"<option value=\"PPC\" />PPC" +
			"</select></span>" +
			"<span>" + toolTip("?", "CPU is optionnal") + "</span>" + 
		"</div>" +
	   "</form>" +
      "</div>" +
      "<div class=\"modaltrailer\">&nbsp;</div>" +
    "</div></div>";

	var pop = new myPop("Send", sendDataToServer);
    pop.popOut(theBody);
	pop = null;
}

/**
 * This is called by the send button in the send popup
 * @see sendData
 */
function sendDataToServer() {

    xmlHttpConnectionSend=getXmlHttpObject();
    if (xmlHttpConnectionSend==null) {
		return;
    }

	var elem = document.getElementById(sendFormID);

	var name = elem[nameInputName].value;
	if(name == "") {
		error("name must be set");
		return;
	}
	var xmlDescription = XMLROOTOPEN + "<data><name>" + name + "</name>";

	var type = elem[menuTypeInputName].value;
	if(type != "") {
		xmlDescription += "<type>" + type + "</type>";
	}
	var cpu = elem[menuCpuInputName].value;
	if(cpu != "") {
		xmlDescription += "<cpu>" + cpu + "</cpu>";
	}
	var os = elem[menuOsInputName].value;
	if(os != "") {
		xmlDescription += "<os>" + os + "</os>";
	}
	var accessrights = elem[arInputName].value;
	console.log("accessrights = " + accessrights);
	if(accessrights != "") {
		if(accessrights.match(ARREGEXP) == null) {
			error("access rights syntax error.<br />Must match : " + ARREGEXP);
			return;
		}
		xmlDescription += "<accessrights>0x" + accessrights + "</accessrights>";
	}

	xmlDescription += "</data>" + XMLROOTCLOSE;

    var url="/send/?XMLDESC=" + xmlDescription;
    xmlHttpConnectionSend.onreadystatechange=sendStateChanged;
    xmlHttpConnectionSend.open("POST",url,true);
    xmlHttpConnectionSend.send(null);
    return;
}

/**
 * This upload data content for the selected data
 */
function upload() {

	var formElem = getCurrentForm();
	var datauid = null;

	var count = 0;
	for (var i = 0; i < formElem.length; i++) {
		try {
			if(formElem[i].checked == false) {
				continue;
			}

			if(count > 0) {
				error("You can't select more than one row");
			  	return;
		   	}

			count++;
			datauid = formElem[i].name;
		}
		catch(err) {
		}
	}

	if(count < 1) {
		error("You must select one row");
	  	return;
   	}

	var datanameid = datanameheader + datauid;
	console.log("datanameid = " + datanameid);
	var dataname   = document.getElementById(datanameid).innerHTML;

	var theBody =
	 "<div class=\"modalContent\">" +
	    "<div class=\"modalpane\">" +
	      "<div class=\"modaltitre\">" +
	        "<span >Upload data content</span>" +
	      "</div>" +
	      "<div style=\"display:block\">" +
		   "<form id=\"" + uploadFormID + "\" action=\"UPLOADDATA\" enctype=\"multipart/form-data\" method=\"post\">" + 
		    "<div class=\"tupledetail\"><span class=\"firstdetail\">Data name:</span>" + 
		    "<span class=\"lastdetail\">" + dataname + "<input type=\"hidden\" name=\"" +
		    uidInputName + "\" value=\"" + datauid + "\"></span></div>" +
		    "<div class=\"tupledetail\"><span class=\"firstdetail\">md5sum:</span>" +
		    "<span class=\"lastdetail\"><input type=\"text\" name=\"" + md5InputName + "\" size=\"32\"></span></div>" +
		    "<div class=\"tupledetail\"><span class=\"firstdetail\">Select a file:</span>" +
		    "<span class=\"lastdetail\"><input type=\"file\" name=\"" + fileInputName + "\" size=\"32\"></span></div>" +
		   "</form>" +
	      "</div>" +
	      "<div class=\"modaltrailer\">&nbsp;</div>" +
	    "</div></div>";

	var pop = new myPop("Upload", uploadData);
    pop.popOut(theBody);
	pop = null;
}

/**
 * This reads from local file the data contant to upload to XWHEP server
 * The end of the reading process will call uploadDataToServer()
 * @see uploadDataToServer()
 */
function uploadData() {

	console.log("uploadData()");

	if (window.File && window.FileReader && window.FileList && window.Blob) {
	  // Great success! All the File APIs are supported.
	} else {
	  alert('The File APIs are not fully supported in this browser.');
	  return;
	}

	var elem = document.getElementById(uploadFormID);

	var uid = elem[uidInputName].value;
	if(uid == "") {
		error("uid must be set");
		return;
	}
	var md5 = elem[md5InputName].value;
	if(md5 == "") {
		error("md5 must be set");
		return;
	}
    uploadFile = elem[fileInputName].files[0];
	if(uploadFile == null) {
		error("file must be set");
		return;
	}

	var size = uploadFile.size;

	console.log(uid + " " + size + " " + md5 + " " + escape(uploadFile.name) + " (" + uploadFile.type + ") " + uploadFile.size);

    uploadUrl="/uploaddata/" + uid + "?" + sizeInputName + "=" + size + "&" + md5InputName + "=" + md5 + "&" + fileInputName + "=" + escape(uploadFile.name);

	fileReader = new FileReader();
	fileReader.onload  = uploadDataToServer;
	fileReader.onerror = uploadError;
	fileReader.onabort = uploadError;
	fileReader.readAsBinaryString(uploadFile);
}

/**
 * This is the FileReader callback called on I/O error
 */
function uploadError() {
	error("Can't read file \"" + uploadFile.name + "\" to upload : " + fileReader.error);
	uploadUrl  = "";
	fileReader = null;
	uploadFile = null;
}

/**
 * This is the FileReader callback called when the file to upload has been read
 * This finally uploads the data to server 
 */
function uploadDataToServer() {

	if((uploadUrl == null) || (uploadUrl == "")) {
		return;
    }
		
	console.log("uploadDataToServer()");

    xmlHttpConnectionUpload=getXmlHttpObject();
    if (xmlHttpConnectionUpload==null) {
		return;
    }
    xmlHttpConnectionUpload.onreadystatechange=uploadDataStateChanged;
	console.log("uploadUrl = " + uploadUrl);
    xmlHttpConnectionUpload.open("POST",uploadUrl);

	//
	// from http://mike.kaply.com/2010/05/20/post-multipart-form-xhr/
	//
	var boundary = '---------------------------';
	boundary += Math.floor(Math.random()*32768);
	boundary += Math.floor(Math.random()*32768);
	boundary += Math.floor(Math.random()*32768);
	xmlHttpConnectionUpload.setRequestHeader("Content-Type", 'multipart/form-data; boundary=' + boundary);
	var body = '';
	body += 'Content-Type: multipart/form-data; boundary=' + boundary;
	body += '\r\n\r\n--' + boundary + '\r\n' + 'Content-Disposition: form-data; name="';
	body += fileInputName + '"; filename="'+uploadFile.name+'" \r\n';
	body += "Content-Type: "+uploadFile.type;
	body += '\r\n\r\n';


	body += fileReader.result;


	body += '\r\n'
	body += '--' + boundary + '\r\n' + 'Content-Disposition: form-data; name="submitBtn"\r\n\r\nUpload\r\n';
	body += '--' + boundary + '--';
	xmlHttpConnectionUpload.setRequestHeader('Content-length', body.length);

	xmlHttpConnectionUpload.overrideMimeType('text/plain; charset=x-user-defined-binary');
	xmlHttpConnectionUpload.sendAsBinary(body);

	uploadUrl  = "";
	fileReader = null;
	uploadFile = null;
}

/**
 * This handles uploadData events
 */
function uploadDataStateChanged()
{
    var current = xmlHttpConnectionUpload;
     
	if (current.readyState!=4) {
        return;
	}

    var xmlDoc = null;
    try {
    	if(current.responseXML != null)
    		xmlDoc=current.responseXML.documentElement;
    	else {
			var parser = new DOMParser();
			xmlDoc = parser.parseFromString(current.responseText,'text/xml');
		}

	    if(rpcError(xmlDoc) == true) {
	    	return;
		}
	
		alert("Please use the \"Refresh\" button");
	}
	catch(err) {
		console.log(err.toString());
		connectionError();
		return;
	}
}
	

/**
 * This a new work
 */
function sendWork() {

	if(document.getElementById(worksTabID).getAttribute("class") != "current") {
		return;
	}
	alert("Please use the \"Submit\" button in the \"Applications\" pane");
}

/**
 * This sends a new BoT
 */
function sendBot() {

	if(document.getElementById(botsTabID).getAttribute("class") != "current") {
		return;
	}
	
	var theBody = "<div class=\"modalContent\">" +
    "<div class=\"modalpane\">" +
      "<div class=\"modaltitre\">" +
        "<span >Send new Group of jobs</span>" +
      "</div>" +
      "<div style=\"display:block\">" +
	   "<form id=\"" + sendFormID + "\" >" +
		"<div class=\"tupledetail\" style=\"background-color:" + colors[0] + "\">" +
			"<span class=\"firstdetail\">Name</span>" +
			"<span class=\"lastdetail\"><input type=\"text\" name=\"" + nameInputName + "\" /></span>" +
		"</div>" +
		"<div class=\"tupledetail\" style=\"background-color:" + colors[1] + "\">" +
			"<span class=\"firstdetail\">AccessRights</span>" +
			"<span class=\"lastdetail\"><input type=\"text\" name=\"" + arInputName + "\" /></span>" +
			"<span>" + toolTip("?", arTooltip) + "</span>" + 
		"</div>" +
	   "</form>" +
      "</div>" +
      "<div class=\"modaltrailer\">&nbsp;</div>" +
    "</div></div>";

	var pop = new myPop("Send", sendBotToServer);
    pop.popOut(theBody);
	pop = null;
}

/**
 * This is called by the submit button in the send BoT popup 
 * @see sendBot
 */
function sendBotToServer() {

    xmlHttpConnectionSend=getXmlHttpObject();
    if (xmlHttpConnectionSend==null) {
		return;
    }

	var elem = document.getElementById(sendFormID);

	var name = elem[nameInputName].value;
	if(name == "") {
		error("name must be set");
		return;
	}
	var xmlDescription = XMLROOTOPEN + "<group><name>" + name + "</name>";

	var accessrights = elem[arInputName].value;
	console.log("accessrights = " + accessrights);
	if(accessrights != "") {
		if(accessrights.match(ARREGEXP) == null) {
			error("access rights must match " + ARREGEXP);
			return;
		}
		xmlDescription += "<accessrights>0x" + accessrights + "</accessrights>";
	}

	xmlDescription += "</group>" + XMLROOTCLOSE;

    var url="/send/?XMLDESC=" + xmlDescription;
    xmlHttpConnectionSend.onreadystatechange=sendStateChanged;
    xmlHttpConnectionSend.open("POST",url,true);
    xmlHttpConnectionSend.send(null);
    return;
}

/**
 * This contains the selection output
 * @see selectObject()
 */
var selectObjectOut     = null;
/**
 * This contains the ID of the list to get the selection from
 * @see selectObject()
 */
var selectObjectListID  = null;
/**
 * This select an object from the given list
 * @param formid is the where the outputid is located
 * @param outputid is the ID of the HTML element to write the result
 * @param listid is the object list where to select an object
 */
function selectObject(formid, outputid, listid) {

	console.log("SelectObject(" + formid + ", " + outputid + ", " + listid + ")");

	var elem = document.getElementById(formid);

	selectObjectOut = elem[outputid];
	if (selectObjectOut == null) {
		error("out is null");
		return;
	}
	
	selectObjectListID = listid;
	var list = document.getElementById(selectObjectListID);
	if((selectObjectListID == null) || (list == null)) {
		error("List is null");
		return;
	}

	var theBody = "<div class=\"selection\"><form id =\"" + selectObjFormID + "\">" + list.innerHTML +"</form></div>";
    var pop = new myPop("Select", selectObjectCallback, true);
    pop.popOut(theBody);
}

/**
 * This retrieves the selected object
 * @see selectObject()
 */
function selectObjectCallback() {
	if (selectObjectOut == null) {
		error("out is null");
		return;
	}
	if (selectObjectListID == null) {
		error("List is null");
		return;
	}

	var list = document.getElementById(selectObjFormID);
	console.log("selectObjectCallback() " + list.length);

	if (list == null) {
		error("List is null");
		return;
	}

	for (var i = 0; i < list.length; i++) {
		try {
		console.log("selectObjectCallback() " + i + " " + list[i].checked);

			if(list[i].checked == true) {
				var uid = list[i].name;
				console.log("uid = " + uid);
				var datauriid = datauriheader + uid;
				console.log("datauriid = " + datauriid);
				if(selectObjectListID == listBotFormID) {
					console.log("selectObjectCallback bot");
					selectObjectOut.value = uid;
					return;
				}
				if(selectObjectListID == listDataFormID) {
					console.log("selectObjectCallback data");
					selectObjectOut.value = document.getElementById(datauriid).innerHTML;
					return;
				}
			}
		}
		catch(err) {
			console.log(err);
		}
	}
}

/**
 * This submits a new jobs
 */
function submit() {

	var theBody =
	 "<div class=\"modalContent\">" +
	    "<div class=\"modalpane\">" +
	      "<div class=\"modaltitre\">" +
	        "<span >Submit job</span>" +
	      "</div>" +
	      "<div style=\"display:block\">" +
		   "<form id=\"" + submitFormID + "\">";

	var formElem = getCurrentForm();
	var appuid = null;

	var uidListLength = 0;
	for (var i = 0; i < formElem.length; i++) {
		if(formElem[i].checked == true) {
			uidListLength++;
			appuid = formElem[i].name;
		}
	}

	if((uidListLength == 0) || (document.getElementById(appsTabID).getAttribute("class") != "current")) {
		error("You must select an application");
	  	return;
	}

	if(uidListLength > 1) {
		error("You can't select more than one application");
	  	return;
   	}


	var appnameid = appnameheader + appuid;
	var appname = document.getElementById(appnameid).innerHTML;
	var appuidid = appuidheader + appuid;
	var appuid = document.getElementById(appuidid).innerHTML;
	console.log("appname = " + appname + " appuid = " + appuid);

	var colorcounter = 0;
	var color = colors[colorcounter++ % 2];
	
	theBody += "<div class=\"tupledetail\" style=\"background-color:" + color + "\"><span class=\"firstdetail\">Application</span>" +
    			"<span class=\"lastdetail\">" + appname + "<input type=\"hidden\" name=\"" + appuidSubmitInputName + "\" value=\"" + appuid + "\" /></span></div>";

	color = colors[colorcounter++ % 2];
	theBody += "<div class=\"tupledetail\" style=\"background-color:" + color + "\">" +
				"<span class=\"firstdetail\">Label</span>" +
    			"<span class=\"lastdetail\"><input name=\"" + labelSubmitInputName + "\" type=\"text\" width=\"40\"></span>" +
    			"<span style=\"padding-right:65px;\">&nbsp;</span>" +
    			"<span>" + toolTip("?", "Label is optional ") + "</span></div>";
	color = colors[colorcounter++ % 2];
	theBody += "<div class=\"tupledetail\" style=\"background-color:" + color + "\">" +
				"<span class=\"firstdetail\">Group of jobs</span>" +
    			"<span class=\"lastdetail\"><input name=\"" + botuidSubmitInputName + "\" type=\"text\" width=\"40\"></span>" +
    			"<span style=\"padding-right:20px;\"><button type=\"button\" onclick=\"selectObject(submitFormID, 'submit_botuid', listBotFormID);\">Select</button></span>" +
    			"<span>" + toolTip("?", "Group of jobs is optional. You can eighter enter an UID by hand or click \"Select\" to select a group of jobs from the repository") + "</span></div>";
	color = colors[colorcounter++ % 2];
	theBody += "<div class=\"tupledetail\" style=\"background-color:" + color + "\">" +
				"<span class=\"firstdetail\">Command line</span>" +
    			"<span class=\"lastdetail\"><input name=\"" + cmdlineSubmitInputName + "\" type=\"text\" width=\"40\"></span>" +
    			"<span style=\"padding-right:65px;\">&nbsp;</span>" +
    			"<span>" + toolTip("?", "Command line is optional ") + "</span></div>";
	color = colors[colorcounter++ % 2];
	theBody += "<div class=\"tupledetail\" style=\"background-color:" + color + "\">" +
				"<span class=\"firstdetail\">Listen port</span>" +
    			"<span class=\"lastdetail\"><input name=\"" + listenportSubmitInputName + "\" type=\"text\" width=\"40\"></span>" +
    			"<span style=\"padding-right:65px;\">&nbsp;</span>" +
    			"<span>" + toolTip("?", "Listen port is optional ") + "</span></div>";
	color = colors[colorcounter++ % 2];
	theBody += "<div class=\"tupledetail\" style=\"background-color:" + color + "\">" +
				"<span class=\"firstdetail\">Smartsocket client</span>" +
    			"<span class=\"lastdetail\"><input name=\"" + smartsocketclientSubmitInputName + "\" type=\"text\" width=\"40\"></span>" +
    			"<span style=\"padding-right:65px;\">&nbsp;</span>" +
    			"<span>" + toolTip("?", "Smartsocket client is optional ") + "</span></div>";
	color = colors[colorcounter++ % 2];
	theBody += "<div class=\"tupledetail\" style=\"background-color:" + color + "\">" +
				"<span class=\"firstdetail\">Dirin</span>" +
    			"<span class=\"lastdetail\"><input name=\"" + dirinuriSubmitInputName + "\" type=\"text\" width=\"40\"></span>" +
    			"<span style=\"padding-right:20px;\"><button type=\"button\" onclick=\"selectObject(submitFormID, 'submit_dirinuri', listDataFormID);\">Select</button></span>" +
    			"<span>" + toolTip("?", "Dirin is optional. You can eighter enter an URI by hand or click \"Select\" to select a data from the repository") + "</span></div>";
	color = colors[colorcounter++ % 2];
	theBody += "<div class=\"tupledetail\" style=\"background-color:" + color + "\">" +
				"<span class=\"firstdetail\">Stdin</span>" +
    			"<span class=\"lastdetail\"><input name=\"" + stdinuriSubmitInputName + "\" type=\"text\" width=\"40\"></span>" +
    			"<span style=\"padding-right:20px;\"><button type=\"button\" onclick=\"selectObject(submitFormID, 'submit_stdinuri', listDataFormID);\">Select</button></span>" +
    			"<span>" + toolTip("?", "Stdin is optional. You can eighter enter an URI by hand or click \"Select\" to select a data from the repository") + "</span></div>";
	color = colors[colorcounter++ % 2];
	theBody += "<div class=\"tupledetail\" style=\"background-color:" + color + "\">" +
				"<span class=\"firstdetail\">Env variable</span>" +
    			"<span class=\"lastdetail\"><input name=\"" + envvarsSubmitInputName + "\" type=\"text\" width=\"40\"></span>" +
    			"<span style=\"padding-right:65px;\">&nbsp;</span>" +
    			"<span>" + toolTip("?", "Environment variables is optional") + "</span></div>";

	theBody += "</form></div>" +
      "<div class=\"modaltrailer\">&nbsp;</div>" +
    "</div></div>";
	
    var pop = new myPop("Submit", submitToServer);
    pop.popOut(theBody);
}
/**
 * This is called by the submit button in the submit popup
 * @see submit()
 */
function submitToServer() {

    xmlHttpConnectionSend=getXmlHttpObject();
    if (xmlHttpConnectionSend==null) {
		return;
    }

	var elem = document.getElementById(submitFormID);

	var appuid = elem[appuidSubmitInputName].value;
	if(appuid == "") {
		error("appuid must be set");
		return;
	}
	var xmlDescription = XMLROOTOPEN + "<work><appuid>" + appuid + "</appuid>";

	var v = elem[labelSubmitInputName].value;
	if(v != "") {
		xmlDescription += "<label>" + v + "</label>";
	}
	v = elem[botuidSubmitInputName].value;
	if(v != "") {
		xmlDescription += "<groupuid>" + v + "</groupuid>";
	}
	v = elem[cmdlineSubmitInputName].value;
	if(v != "") {
		xmlDescription += "<cmdline>" + v + "</cmdline>";
	}
	v = elem[listenportSubmitInputName].value;
	if(v != "") {
		xmlDescription += "<listenport>" + v + "</listenport>";
	}
	v = elem[smartsocketclientSubmitInputName].value;
	if(v != "") {
		xmlDescription += "<smartsocketclient>" + v + "</smartsocketclient>";
	}
	v = elem[dirinuriSubmitInputName].value;
	if(v != "") {
		xmlDescription += "<dirinuri>" + v + "</dirinuri>";
	}
	v = elem[stdinuriSubmitInputName].value;
	if(v != "") {
		xmlDescription += "<stdinuri>" + v + "</stdinuri>";
	}
	v = elem[envvarsSubmitInputName].value;
	if(v != "") {
		xmlDescription += "<envvars>" + v + "</envvars>";
	}

	xmlDescription += "</work>" + XMLROOTCLOSE;

    var url="/send/?XMLDESC=" + xmlDescription;
    xmlHttpConnectionSend.onreadystatechange=sendStateChanged;
    xmlHttpConnectionSend.open("POST",url,true);
    xmlHttpConnectionSend.send(null);
    return;
}

/**
 * This removes work from user interface
 */
function undisplay(uid) {

	var pendingid   = pendingheader + uid;
	var runningid   = runningheader + uid;
	var completedid = completedheader + uid;
	var errorid     = errorheader + uid;

    //
    // let try to hide the object from its uid
    //
	try {
		console.log("undisplay(" + uid + ") " + uid);
		document.getElementById(uid).style.visibility = "hidden";
		return;
	}
	catch(err) {
	}
    //
    // if it is a job, let hide the job in all possible job lists
    //
	try {
		console.log("undisplay(" + uid + ") " + pendingid);
		document.getElementById(pendingid).style.visibility = "hidden";
	}
	catch(err) {
	}
	try {
		console.log("undisplay(" + uid + ") " + runningid);
		document.getElementById(runningid).style.visibility = "hidden";
	}
	catch(err) {
	}
	try {
		console.log("undisplay(" + uid + ") " + completedid);
		document.getElementById(completedid).style.visibility = "hidden";
	}
	catch(err) {
	}
	try {
		console.log("undisplay(" + uid + ") " + errorid);
		document.getElementById(errorid).style.visibility = "hidden";
	}
	catch(err) {
	}
}
/**
 * This deletes checked objects
 * @see undisplay(uid)
 */
function deleteFromServer() {

	var formElem = getCurrentForm();

	var uidListLength = 0;
	for (var i = 0; i < formElem.length; i++) {

		if(formElem[i].checked == false) {
			continue;
		}

		var uid = formElem[i].name;

	    hashtableDelete[uid]=getXmlHttpObject();
	    if (hashtableDelete[uid]==null) {
			console.log("delete() can't create xmlHttpObject");
			continue;
	    }

		console.log("delete() : " + uid);
	    var url="/remove/" + uid;
    	hashtableDelete[uid].onreadystatechange = deleteFromServerStateChanged;
	    hashtableDelete[uid].open("POST",url,true);
    	hashtableDelete[uid].send(null);
	}
}

/**
 * This handles deleteFromServer events
 * @see deleteFromServer()
 */
function deleteFromServerStateChanged()
{
	for (var uid in hashtableDelete) {

	    if (hashtableDelete.hasOwnProperty(uid) == false) {
		    console.log("hashtableDelete.hasOwnProperty(" + uid + ") = false");
	    	continue;
    	}
    	
	    var current = hashtableDelete[uid];
	     
    	if (current.readyState!=4) {
	        continue;
    	}

	    var xmlDoc = null;
	    try {
	    	if (xmlDoc=current.responseXML == null) {
	    		if (current.status == 401) {
		    		document.documentElement.innerHTML=current.responseText;
		   			return;
	    		}
	    	}
	    	xmlDoc=current.responseXML.documentElement;
		}
		catch(err) {
			connectionError();
			return;
		}

    	delete hashtableDelete[uid];

	    if(rpcError(xmlDoc) == true) {
	        continue;
		}

		undisplay(uid);
    }    	    	
}

/*
 * OSes chart
 */
google.charts.load("current", {packages:["corechart"]});
google.charts.setOnLoadCallback(drawOSesChart);
function drawOSesChart() {
  var osesData = google.visualization.arrayToDataTable([
      ['OSes', 'OS types'],
      ['Windows',     11],
      ['Mac OS',      2],
      ['Linux',  2]
  ]);

  var osesChartOptions = {
      pieHole: 0.4,
      legend: 'none'
  };

  var osesChart = new google.visualization.PieChart(document.getElementById('osesChart'));
  osesChart.draw(osesData, osesChartOptions);
}
                
/*
 * CPUs chart
 */
google.charts.setOnLoadCallback(drawCPUsChart);
function drawCPUsChart() {
	var cpusData = google.visualization.arrayToDataTable([
    					['CPUs', 'CPU types'],
						['x86_64',     110],
						['ix86',      78],
						['PPC',  10]
	]);
 
    var cpuChartOptions = {
    	pieHole: 0.4,
        legend: 'none'
    };

    var cpusChart = new google.visualization.PieChart(document.getElementById('cpusChart'));
    cpusChart.draw(cpusData, cpuChartOptions);
}

/*********************
 * End of scripts    *
 *********************/

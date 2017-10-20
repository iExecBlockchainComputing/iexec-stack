const https = require('https');
const http = require('http');
const { parseString } = require('xml2js');
const fs = require('fs');
const uuidV4 = require('uuid/v4');
const { URL } = require('url');
const request = require('request');
const json2xml = require('json2xml');
const FormData = require('form-data');
const md5File = require('md5-file');
const unzip = require('unzip');

/*
 * This is the delay between between two get status calls
 * This is in milliseconds
 */
const WAITSTATUSDELAY = 10000;

/**
 * API PATH
 */
const PATH_GETAPPS = '/getapps';
const PATH_GET = '/get';
const PATH_SENDWORK = '/sendwork';
const PATH_REMOVE = '/remove';
const PATH_DOWNLOADDATA = '/downloaddata';
const PATH_SENDDATA = '/senddata';
const PATH_SENDAPP = '/sendapp';
const PATH_UPLOADDATA = '/uploaddata';

// /!\   keep the leading slash
const PATH_ETHAUTH = '/ethauth/';

/**
 * This is the XML tag of the element returned by the server on error
 */
const XMLRPCRESULTTAG = 'xmlrpcresult';
/**
 * This is the XMLRPCRESULT return code
 */
const XMLRPCRESULTMESSAGE = 'MESSAGE';

/**
 * This are cookies names
 */
const STATENAME="state";
const ETHAUTHNAME="ethauthtoken";


/**
 * This contains work parameters write access.
 * Key is the parameter name
 * Value describes the write access
 */
const workAvailableParameters = {
  uid: false,
  owneruid: false,
  accessrights: true,
  errormsg: true,
  mtime: false,
  userproxy: true,
  sessionuid: true,
  groupuid: true,
  sgid: true,
  expectedhostuid: true,
  isservice: false,
  label: true,
  appuid: true,
  returncode: false,
  server: false,
  listenport: true,
  smartsocketaddr: true,
  smartsocketclient: true,
  envvars: true,
  cmdline: true,
  stdinuri: true,
  dirinuri: true,
  resulturi: false,
  arrivaldate: false,
  completeddate: false,
  readydate: false,
  datareadydate: false,
  compstartdate: false,
  compenddate: false,
  sendtoclient: false,
  local: false,
  active: true,
  replications: true,
  totalr: true,
  sizer: true,
  replicateduid: true,
  datadrivenuri: true,
  maxretry: true,
  retry: false,
  maxwallclocktime: true,
  diskspace: true,
  minmemory: true,
  mincpuspeed: true,
  status: false,
  minfreemassstorage: true,
};

/**
 * This contains application parameters write access.
 * Key is the parameter name
 * Value describes the write access
 */
const appAvailableParameters = {
  uid: false,
  owneruid: false,
  accessrights: true,
  errormsg: true,
  mtime: false,
  name: true,
  isservice: true,
  type: true,
  minfreemassstorage: true,
  avgexectime: false,
  minmemory: true,
  mincpuspeed: true,
  launchscriptshuri: true,
  launchscriptcmduri: true,
  unloadscriptshuri: true,
  unloadscriptcmduri: true,
  nbjobs: false,
  pendingjobs: false,
  runningjobs: false,
  errorjobs: false,
  webpage: true,
  neededpackages: true,
  envvars: true,
  defaultstdinuri: true,
  basedirinuri: true,
  defaultdirinuri: true,
  ldlinux_ix86uri: true,
  ldlinux_x86_64uri: true,
  ldlinux_amd64uri: true,
  ldlinux_ia64uri: true,
  ldlinux_ppcuri: true,
  ldmacos_ix86uri: true,
  ldmacos_x86_64uri: true,
  ldmacos_ppcuri: true,
  ldwin32_ix86uri: true,
  ldwin32_amd64uri: true,
  ldwin32_x86_64uri: true,
  linux_ix86uri: true,
  linux_amd64uri: true,
  linux_x86_64uri: true,
  linux_ia64uri: true,
  linux_ppcuri: true,
  macos_ix86uri: true,
  macos_x86_64uri: true,
  macos_ppcuri: true,
  win32_ix86uri: true,
  win32_amd64uri: true,
  win32_x86_64uri: true,
  javauri: true,
};

/**
 * This contains data parameters write access.
 * Key is the parameter name
 * Value describes the write access
 */
const dataAvailableParameters = {
  uid: false,
  owneruid: false,
  accessrights: true,
  errormsg:true,
  mtime: false,
  name: true,
  links: false,
  insertiondate:false,
  osversion: true,
  status: true,
  type: true,
  cpu: true,
  os: true,
  size: true,
  md5: true,
  uri: false,
  sendtoclient: false,
  workuid: true,
  package: true,
  replicated: false,
};
/**
 * This contains known CPU names
 * Key is the cpu name
 * Value is true
 */
const knownCPUs = {
  IX86: true,
  X86_64: true,
  IA64: true,
  PPC: true,
  SPARC: true,
  ALPHA: true,
  AMD64: true,
  ARM: true,
}
/**
 * This contains known Operating System (OS) names
 * Key is the OS name
 */
const knownOSes = {
  LINUX: true,
  WIN32: true,
  MACOSX: true,
  SOLARIS: true,
  JAVA: true,
}
/**
 * This retrieves a value from a cookie
 * @param cookie is the cookie
 * @param name is the value name 
 * @see http://www.w3schools.com/js/js_cookies.asp
 */
function getCookie(cookie, name)
{
  var c_value = cookie.toString();
  var c_start = c_value.toString().indexOf(` ${name}=`);
  
  if (c_start == -1)
  {
  	c_start = c_value.toString().indexOf(`${name}=`);
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
 * @param name is the cookie name
 * @param value is the cookie value
 * @param exdays is the expiration date
 * @return a new cookie value 
 * @see http://www.w3schools.com/js/js_cookies.asp
 */
function setCookie(name,value,exdays)
{
  var exdate=new Date();
  exdate.setDate(exdate.getDate() + exdays);
  var c_value=escape(value) + ((exdays==null) ? "" : "; expires="+exdate.toUTCString());
  return name + "=" + c_value;
}


/**
 * This retrieves the binary field name, given OS and CPU
 * @param _os is the OS name
 * @param _cpu is the CPU name
 */
function getApplicationBinaryFieldName(_os, _cpu) {

  if ((_os === undefined) || (_cpu === undefined)) {
	throw new Error('OS or CPU undefined');
  }

  os = _os.toUpperCase();
  cpu = _cpu.toUpperCase();

  if (os === "JAVA") {
	return "javauri";
  }

  switch (os) {
  case "LINUX":
	switch (cpu) {
	case "IX86":
	  return "linux_ix86uri";
	case "PPC":
	  return "linux_ppcuri";
	case "AMD64":
	  return "linux_amd64uri";
	case "X86_64":
	  return "linux_x86_64uri";
	case "IA64":
	  return "linux_ia64uri";
	default:
	  break;
	}
	break;
  case "WIN32":
    switch (cpu) {
    case "IX86":
      return "win32_ix86uri";
    case "AMD64":
      return "win32_amd64uri";
    case "X86_64":
      return "win32_x86_64uri";
    default:
      break;
    }
    break;
  case "MACOSX":
	switch (cpu) {
	case "IX86":
	  return "macos_ix86uri";
	case "X86_64":
	  return "macos_x86_64uri";
	case "PPC":
	  return "macos_ppcuri";
	default:
	  break;
	}
	break;
  }
  return undefined;
}

const createXWHEPClient = ({
  login, password, hostname, port,
}) => {
  const CREDENTIALS = `?XWLOGIN=${encodeURIComponent(login)}&XWPASSWD=${encodeURIComponent(password)}`;

  /**
   * This contains all known application names
   */
  const hashtableAppNames = {};

  /**
   * This throws "Connection error"
   */
  function connectionError() {
    throw new Error('Connection error');
  }

  /**
   * This checks if there is an remote call error
   * @param xmldoc contains the server answer
   * @exception is throw if xmldoc represents an error
   */
  function rpcError(xmldoc) {
    const rpcErr = xmldoc.getElementsByTagName(XMLRPCRESULTTAG)[0];
    if (rpcErr != null) {
      const msg = rpcErr.getAttribute(XMLRPCRESULTMESSAGE);
      throw msg;
    }
  }

  /**
   * This sends the work to server
   * This is a private method not implemented in the smart contract
   * @param xmlWork is an XML description of the work
   * @return a new Promise
   * @resolve undefined
   */
  function sendWork(cookies, xmlWork) {
    return new Promise((resolve, reject) => {

  	  var state = "";
      if(cookies !== undefined) {
    	console.log(`sendWork(${cookies}) : cookies = ${cookies}`);
    	state = getCookie(cookies, STATENAME);
      }

	  console.log(`sendWork(${cookies}) ; ${STATENAME} = ${state}`);

	  var creds = CREDENTIALS;
	  if (state !== "") {
		  creds = `?${STATENAME}=${state}`;
	  }
      const sendWorkPath = `${PATH_SENDWORK}?XMLDESC=${xmlWork}`;
      const options = {
        hostname,
        port,
        path: `${PATH_SENDWORK + creds}&XMLDESC=${xmlWork}`,
        method: 'GET',
        rejectUnauthorized: false,
      };
//      console.log(`${options.hostname}:${options.port}${sendWorkPath}`);

      const req = https.request(options, (res) => {
        res.on('data', (d) => {
        });

        res.on('end', () => {
          resolve();
          return;
        });
      });

      req.on('error', (e) => {
        reject(e);
        return;
      });
      req.end();
    });
  }

  /**
   * This sends the application to server
   * This is a private method not implemented in the smart contract
   * @param xmlApp is an XML description of the application
   * @return a new Promise
   * @resolve undefined
   */
  function sendApp(cookies, xmlApp) {
    return new Promise((resolve, reject) => {
      var state = "";
      if((cookies !== undefined) && (cookies[0] !== undefined)) {
    	var cookie = cookies[0];
    	console.log(`sendWork(${cookies}) : cookie = ${cookie}`);
    	state = getCookie(cookies, STATENAME);
      }

      console.log(`sendWork(${cookies}) ; ${STATENAME} = ${state}`);

      var creds = CREDENTIALS;
      if (state !== "") {
    	creds = `?${STATENAME}=${state}`;
      }
      const options = {
        hostname,
        port,
        path: `${PATH_SENDAPP + creds}&XMLDESC=${xmlApp}`,
        method: 'GET',
        rejectUnauthorized: false,
      };
      console.log(`${options.hostname}:${options.port}${options.path}`);

      const req = https.request(options, (res) => {
        res.on('data', (d) => {
        });

        res.on('end', () => {
          resolve();
          return;
        });
      });

      req.on('error', (e) => {
    	  reject(e);
    	  return;
      });
      req.end();
    });
  }

  /**
   * Ref: https://github.com/form-data/form-data
   * This sends the data to server
   * This is a private method not implemented in the smart contract
   * @param dataUid is the uid of the data
   * @param dataPath is the path of the data in local fs
   * @return a new Promise
   * @resolve undefined
   */
  function uploadData(cookies, dataUid, dataPath) {
    return new Promise((resolve, reject) => {
      var state = "";
      if((cookies !== undefined) && (cookies[0] !== undefined)) {
    	var cookie = cookies[0];
    	console.log(`sendWork(${cookies}) : cookie = ${cookie}`);
    	state = getCookie(cookies, STATENAME);
      }

      console.log(`sendWork(${cookies}) ; ${STATENAME} = ${state}`);

      var creds = CREDENTIALS;
      if (state !== "") {
    	creds = `?${STATENAME}=${state}`;
      }
      const uploadDataPath = `${PATH_UPLOADDATA}/${dataUid}`;
      const options = {
        hostname,
        port : port,
        path : `${PATH_UPLOADDATA}/${dataUid}${creds}`,
        method : 'POST',
        protocol : 'https:',
        rejectUnauthorized: false
      };
      console.log(`uploadData(${dataUid}) : ${options.hostname}:${options.port}${options.path}`);

      const stats = fs.statSync(dataPath);
//      console.log(stats);
      const dataSize = stats['size'];

      const dataMD5 = md5File.sync(dataPath);

//      console.log('uploadData DATAUID ', dataUid);
//      console.log('uploadData DATAMD5SUM ', dataMD5);
//      console.log('uploadData DATASIZE ', dataSize);
//      console.log('uploadData DATAFILE ', dataPath);

      const dataForm = new FormData();
      dataForm.append('DATAUID', dataUid);
      dataForm.append('DATAMD5SUM', dataMD5);
      dataForm.append('DATASIZE', dataSize);
      dataForm.append('DATAFILE', fs.createReadStream(dataPath));
      dataForm.submit(options, function(err, res) {
    	if (err) {
           reject('uploadData error ' + err);
           return;
        }
      });
      resolve();
      return;
    });
  }

  /**
   * This sends the data to server
   * This is a private method not implemented in the smart contract
   * @param xmlData is an XML description of the data
   * @return a new Promise
   * @resolve undefined
   */
 function sendData(cookies, xmlData) {
   return new Promise((resolve, reject) => {
	 var state = "";
	 if((cookies !== undefined) && (cookies[0] !== undefined)) {
	   var cookie = cookies[0];
	   console.log(`sendWork(${cookies}) : cookie = ${cookie}`);
	   state = getCookie(cookies, STATENAME);
	 }

	 console.log(`sendWork(${cookies}) ; ${STATENAME} = ${state}`);

	 var creds = CREDENTIALS;
	 if (state !== "") {
	   creds = `?${STATENAME}=${state}`;
	 }
      const sendDataPath = `${PATH_SENDDATA}?XMLDESC=${xmlData}`;
      const options = {
        hostname: hostname,
        port: port,
        path: `${PATH_SENDDATA}${creds}&XMLDESC=${xmlData}`,
        method: 'GET',
        protocol : 'https:',
        rejectUnauthorized: false,
      };
      console.log(`sendData() : ${options.hostname}:${options.port}${sendDataPath}`);

      const req = https.request(options, (res) => {

    	res.on('data', (d) => {
        });

        res.on('end', () => {
          resolve();
          return;
        });
      });

      req.on('error', (e) => {
        reject(e);
        return;
      });
      req.end();
    });
  }

  /**
   * This retrieves an object from server
   * This is a public method implemented in the smart contract
   * @param uid is the uid of the object to retrieve
   * @return a Promise
   * @resolve a String containing the XML representation of the retrieved object
   */
  const get = (cookies, uid) => (
    new Promise((resolve, reject) => {
      let getResponse = '';

      var state = "";
      if((cookies !== undefined) && (cookies[0] !== undefined)) {
    	var cookie = cookies[0];
    	console.log(`sendWork(${cookies}) : cookie = ${cookie}`);
    	state = getCookie(cookies, STATENAME);
      }

	  var creds = CREDENTIALS;
	  if (state !== "") {
		  creds = `?${STATENAME}=${state}`;
	  }

      const getPath = `${PATH_GET}/${uid}`;
      const options = {
        hostname,
        port,
        path: getPath + creds,
        method: 'GET',
        rejectUnauthorized: false,
      };
	  console.log(`get(${cookies}, ${uid}) ; ${options}`);

      const req = https.request(options, (res) => {
        res.on('data', (d) => {
          const strd = String.fromCharCode.apply(null, new Uint16Array(d));
          getResponse += strd;
        });

        res.on('end', () => {
          console.log(`get() : ${getResponse}`);
          resolve(getResponse);
          return;
        });
      });

      req.on('error', (e) => {
        reject(e);
        return;
      });
      req.end();
    })
  );
  /**
   * This retrieves an application
   * This is a private method not implemented in the smart contract
   * @param appUid is the uid of the application to retrieve
   * @return a new Promise
   * @resolve a String containing the XML representation of the retrieved object
   * @see get(uid)
   */
  function getApp(cookies, appUid) {
    return new Promise((resolve, reject) => {
      console.log(`getApp(${cookies}, ${appUid}`);
      get(cookies, appUid).then((getResponse) => {
        let jsonObject;
        parseString(getResponse, (err, result) => {
          jsonObject = JSON.parse(JSON.stringify(result));
        });

        if (jsonObject.xwhep.app === undefined) {
          reject(`getApp() : Not an application : ${appUid}`);
          return;
        }

        const appName = jsonObject.xwhep.app[0].name;
        console.log(`getApp(${cookies}, ${appName}`);

        if (!(appName in hashtableAppNames)) {
          console.log(`getApp(${cookies}, inserting ${appName}`);
          hashtableAppNames[appName] = appUid;
        }

        resolve(getResponse);
  	    return;
      }).catch((e) => {
        reject(`getApp() : Application not found (${appUid}) : ${e}`);
        return;
      });
    });
  }


  /**
   * This retrieves registered applications uid
   * This is a private method not implemented in the smart contract
   * @param cookies is an array
   * @return a new Promise
   * @resolve undefined
   * @see getApp(appUid)
   * @see auth(jwtoken)
   */
  function getApps(cookies) {
    return new Promise((resolve, reject) => {

  	  var state = "";

//	  console.log(`getApps(${cookies})`);

	  if((cookies !== undefined) && (cookies[0] !== undefined)) {
    	var cookie = cookies[0];
    	console.log(`getApps(${cookies}) : cookie = ${cookie}`);
    	state = getCookie(cookies, STATENAME);
      }

//	  console.log(`getApps(${cookies}) ; ${STATENAME} = ${state}`);

	  var creds = CREDENTIALS;
	  if (state !== "") {
		  creds = `?${STATENAME}=${state}`;
	  }
	  var getAppsResponse = '';
      const options = {
        hostname,
        port,
        path: `${PATH_GETAPPS + creds}`,
        method: 'GET',
        rejectUnauthorized: false,
      };
//      console.log('getApps() options', options);
      const req = https.request(options, (res) => {
//        console.log('statusCode:', res.statusCode);
//        console.log('headers:', res.headers);
        res.on('data', (d) => {
          const strd = String.fromCharCode.apply(null, new Uint16Array(d));
          getAppsResponse += strd;
        });
        res.on('end', () => {
//          console.log('getAppsResponse', getAppsResponse)
          parseString(getAppsResponse, (err, result) => {
            if ((result === null) || (result === '') || (result === undefined)) {
              reject('getApps() : connection Error');
              return;
            }
            const jsonData = JSON.parse(JSON.stringify(result));
            if (jsonData === null) {
              reject('getApps() : connection Error');
              return;
            }
            const appsCount = jsonData.xwhep.XMLVector[0].XMLVALUE.length;
            const appuids = [];
            for (let i = 0; i < appsCount; i += 1) {
              const appuid = JSON.stringify(jsonData.xwhep.XMLVector[0].XMLVALUE[i].$.value).replace(/"/g, '');
              appuids[i] = appuid;
            }
            const apppUidPromises = appuids.map(function (x) {
            	return new Promise((resolve, reject) => {
            		getApp(cookies, x).then((strxml) => {
            		  resolve();
            		}).catch((e) => {reject(`getApp(${x}) ${e}`);}); 
            	});
            });
            Promise.all(apppUidPromises).then((xmlStr) => {
              resolve(xmlStr);
              return;
            }).catch((e) => {
              console.log('getApps error ', e);
              reject(`getApps() : ${e}`)
        	  return;
            });
          });
        });
      });

      req.on('error', (e) => {
        console.log('onError', e);
        reject(`getApps() : ${e}`);
        return;
      });
      req.end();
    });
  }

  /**
   * This registers a new deployable application
   * This is a public method implemented in the smart contract
   * It is the caller responsibility to ensure appName does not already exist
   * @param appName is the application name;
   *        application name is set as "appName_creator" and this is unic
   *        If one given creator calls this method twice or more,
   *        this does not insert a new application, but updates application
   *        which name is "appName_creator"
   * @param os  is the binary operating system; must be in knownOSes
   * @param cpu is the binary CPU type; must be in knownCPUs
   * @param binaryUrl is the URI where to find the binary;
   *        binary is uploaded to XWHEP server, if its a "file://"
   * @return a new Promise
   * @resolve the new app uid
   * @exception is thrown if application is not found
   * @see knownCPUs
   * @see knownOSes
   */
  function registerApp(cookies, user, provider, creator, appName, _os, _cpu, binaryUrl) {
	return new Promise((resolve, reject) => {
	  if ((_os === undefined) || (_cpu === undefined) || (binaryUrl === undefined)) {
        reject('registerApp() : OS or CPU undefined');
        return;
	  }

	  os = _os.toUpperCase();
	  cpu = _cpu.toUpperCase();

	  if (!(cpu in knownCPUs)) {
        reject(`registerApp() : unknown CPU "${cpu}"`);
        return;
	  }
	  if (!(os in knownOSes)) {
        reject(`registerApp() : unknown OS "${os}"`);
        return;
	  }

	  console.log(`registerApp (${appName}, ${os}, ${cpu}, ${binaryUrl})`);

      const appUid = uuidV4();
      console.log(`registerApp appUid = ${appUid}`);

      const appDescription = `<app><uid>${appUid}</uid><name>${appName}</name><type>DEPLOYABLE</type><accessrights>0x755</accessrights></app>`;
      sendApp(cookies, appDescription).then(() => {
    	setApplicationBinary(cookies, appUid, os, cpu, binaryUrl).then(() => {
    	  resolve(appUid);
    	  return;
    	}).catch((err) => {
    	  reject(`registerApp() setApplicationBinary error : ${err}`);
    	  return;
    	});
      }).catch((err) => {
        reject(`registerApp() sendApp error : ${err}`);
        return;
      });
    });
  }
  /**
   * This registers a new data as stdin for the provided work
   * This is a private method not implemented in the smart contract
   * @param workUid is the work identifier
   * @param stdinContent is a string containing the stdin
   * @return a new Promise
   * @resolve undefined
   * @exception is thrown on error
   */
  function setApplicationBinary(cookies, appUid, _os, _cpu, binaryUrl) {

	return new Promise((resolve, reject) => {
      if ((_os === undefined) || (_cpu === undefined) || (binaryUrl === undefined)) {
        reject(`setApplicationBinary() : OS or CPU undefined`);
        return;
      }

      os = _os.toUpperCase();
      cpu = _cpu.toUpperCase();

      if (!(cpu in knownCPUs)) {
        reject(`setApplicationBinary() : unknown CPU "${cpu}"`);
        return;
      }
      if (!(os in knownOSes)) {
        reject(`setApplicationBinary() : unknown OS "${os}"`);
        return;
      }

      let binaryURI;
      binaryURI = new URL(binaryUrl);

      console.log(`setApplicationBinary (${appUid}, ${os}, ${cpu}, ${binaryURI}) : ${binaryURI.protocol}`);
      console.log(`setApplicationBinary binaryURI.protocol : ${binaryURI.protocol}`);

	  if(binaryURI.protocol == "file:") {
		const dataFile = binaryURI.pathname;
		const stats = fs.statSync(dataFile);
		const dataSize = stats['size'];
		console.log(`setApplicationBinary ${dataSize}`);
		if(dataSize < 55) {
			reject(`setApplicationBinary() : binaryfile.size < 55 ???`);
			return;
		}
        const dataUid = uuidV4();
        const dataDescription = `<data><uid>${dataUid}</uid><accessrights>0x755</accessrights><type>BINARY</type><name>fileName</name><cpu>${cpu}</cpu><os>${os}</os><status>UNAVAILABLE</status></data>`;

        sendData(cookies, dataDescription).then(() => {

    	  console.log(`setApplicationBinary() dataFile ${dataFile}`);

    	  uploadData(cookies, dataUid, dataFile).then(() => {
    		get(cookies, dataUid).then((getResponse) => {
    		  let jsonObject;
    		  parseString(getResponse, (err, result) => {
    			jsonObject = JSON.parse(JSON.stringify(result));
    		  });

    		  if (jsonObject.xwhep.data === undefined) {
    			reject(`setApplicationBinary() : can't retrieve data: ${dataUid}`);
    			return;
              }

    		  binaryURI = new URL(jsonObject.xwhep.data[0]['uri']);

    		  const appBinaryFieldName = getApplicationBinaryFieldName(os,cpu);
     		  console.log(`setApplicationBinary  setApplicationParam(${appUid}, ${appBinaryFieldName}, ${binaryURI.href})`);

     		  setApplicationParam(cookies, appUid, appBinaryFieldName, binaryURI.href).then(() => {
     			console.log(`setApplicationBinary(${appUid}) ${appUid}#${appBinaryFieldName} = ${binaryURI}`);
     			resolve();
     			return;
     	      }).catch((err) => {
     	    	reject(`setApplicationBinary() setApplicationParam error : ${err}`);
     	    	return;
     	      });
      	    }).catch((err) => {
      	     reject(`setApplicationBinary() get data error : ${err}`);
      	     return;
      	    });
      	  }).catch((err) => {
      		reject(`setApplicationBinary() uploadData error : ${err}`);
 	    	return;
      	  });
        }).catch((err) => {
          reject(`setApplicationBinary() sendData error : ${err}`);
          return;
        });

	  } else {

        const appBinaryFieldName = getApplicationBinaryFieldName(os,cpu);
        console.log(`setApplicationBinary  setApplicationParam(${appUid}, ${appBinaryFieldName}, ${binaryURI.href})`);
        setApplicationParam(cookies, appUid, appBinaryFieldName, binaryURI.href).then(() => {
		  console.log(`setApplicationBinary(${appUid}) ${appUid}#${appBinaryFieldName} = ${binaryURI}`);
		  resolve();
          return;
        }).catch((err) => {
          reject(`setApplicationBinary() setApplicationParam error : ${err}`);
          return;
        });
	  }
    })
  }

  /**
   * This registers a new UNAVAILABLE work for the provided application.
   * Since the status is set to UNAVAILABLE, this new work is not candidate for scheduling.
   * This lets a chance to sets some parameters.
   * To make this work candidate for scheduling, setPending() must be called
   * This is a public method implemented in the smart contract
   * @param appName is the application name
   * @return a new Promise
   * @resolve the new work uid
   * @exception is thrown if application is not found
   * @see #setPending(uid)
   */
  async function register(cookies, user, provider, creator, appName, submitTxHash) {

	if (!(appName in hashtableAppNames)) {
	  await getApps().then(() => {
		if (!(appName in hashtableAppNames)) {
		  return Promise.reject(new Error(`register() : application not found ${appName}`));
		}
	  });
	}

	return new Promise((resolve, reject) => {
      const workUid = uuidV4();

      const appUid = hashtableAppNames[appName];

      const workDescription = `<work><uid>${workUid}</uid><accessrights>0x755</accessrights><appuid>${appUid}</appuid><sgid>${submitTxHash}</sgid><status>UNAVAILABLE</status></work>`;
      sendWork(cookies, workDescription).then(() => {
        sendWork(cookies, workDescription).then(() => { // a 2nd time to force status to UNAVAILABLE
          resolve(workUid);
          return;
        }).catch((err) => {
          reject(`register() sendWork 2 error : ${err}`);
          return;
        });
      }).catch((err) => {
        reject(`register() sendWork 1 error : ${err}`);
        return;
      });
    })
  }

  /**
   * This sets a parameter for the provided application
   * This is a public method implemented in the smart contract
   * @param uid is the application unique identifier
   * @param paramName contains the name of the application parameter to modify
   * @param paramValue contains the value of the application parameter
   * @return a new Promise
   * @resolve undefined
   * @exception is thrown if application is not found
   * @exception is thrown if paramName does not represent a valid application parameter
   * @exception is thrown if parameter is read only (e.g. status, return code, etc.)
   */
  function setApplicationParam(cookies, uid, paramName, paramValue) {

	console.log('setApplicationParam uid', uid);
	console.log('setApplicationParam paramName', paramName);
	console.log('setApplicationParam paramValue', paramValue);

	if (!(paramName in appAvailableParameters)) {
	  return Promise.reject(new Error(`setApplicationParam() : invalid app parameter ${paramName}`));
	}
	if (appAvailableParameters[paramName] === false) {
	  return Promise.reject(new Error(`setApplicationParam() : read only app parameter ${paramName}`));
	}

	return new Promise((resolve, reject) => {
      get(cookies, uid).then((getResponse) => {
        let jsonObject;
        parseString(getResponse, (err, result) => {
          jsonObject = JSON.parse(JSON.stringify(result));
        });

        if (jsonObject.xwhep.app === undefined) {
          reject(`setApplicationParam : Not an application : ${uid}`);
          return;
        }

        jsonObject.xwhep.app[0][paramName] = paramValue;

        sendApp(cookies, json2xml(jsonObject, false)).then(() => {
          resolve();
          return;
        }).catch((err) => {
          reject(`setApplicationParam() error : ${err}`);
          return;
        });
      }).catch((e) => {
        reject(`setApplicationParam(): Work not found (${uid}) : ${e}`);
        return;
      });
    });
  }

  /**
   * This sets a parameter for the provided work.
   * This is a public method implemented in the smart contract
   * @param uid is the work unique identifier
   * @param paramName contains the name of the work parameter to modify
   * @param paramValue contains the value of the work parameter
   * @return a new Promise
   * @resolve undefined
   * @exception is thrown if work is not found
   * @exception is thrown if work status is not UNAVAILABLE
   * @exception is thrown if paramName does not represent a valid work parameter
   * @exception is thrown if parameter is read only (e.g. status, return code, etc.)
   */

  function setWorkParam(cookies, uid, paramName, paramValue) {

	if (!(paramName in workAvailableParameters)) {
	  return Promise.reject(new Error(`setWorkParam() : Invalid parameter ${paramName}`));
	}

	if (workAvailableParameters[paramName] === false) {
	  return Promise.reject(new Error(`setWorkParam() : read only parameter ${paramName}`));
	}

	return new Promise((resolve, reject) => {
      get(cookies, uid).then((getResponse) => {
        let jsonObject;
        parseString(getResponse, (err, result) => {
          jsonObject = JSON.parse(JSON.stringify(result));
        });

        if (jsonObject.xwhep.work === undefined) {
          reject(`setWorkParam(): Not a work : ${uid}`);
          return;
        }
        if (jsonObject.xwhep.work[0].status.toString() !== 'UNAVAILABLE') {
          console.log("setWorkParam(",uid,",",paramName,",", paramValue,") invalid Status");
          reject(`setWorkParam(): Invalid status : ${jsonObject.xwhep.work[0].status}`);
          return;
        }

        jsonObject.xwhep.work[0][paramName] = paramValue;
        console.log("setWorkParam(",uid,",",paramName,",", paramValue,")");
        sendWork(cookies, json2xml(jsonObject, false)).then(() => {
          resolve();
          return;
        }).catch((err) => {
          reject(`setWorkParam() sendWork error : ${err}`);
          return;
        });
      }).catch((e) => {
        reject(`setWorkParam(): Work not found (${uid}) : ${e}`);
        return;
      });
    });
  }

  /**
   * This retrieves a parameter for the provided work.
   * This is a public method implemented in the smart contract
   * @param uid is the work unique identifier
   * @param paramName contains the name of the work parameter to modify
   * @return a new Promise
   * @resolve a String containing the parameter value
   * @exception is thrown if work is not found
   * @exception is thrown if paramName does not represent a valid work parameter
   */
  function getWorkParam(cookies, uid, paramName) {
    return new Promise((resolve, reject) => {
      get(cookies, uid).then((getResponse) => {

        let jsonObject;
        parseString(getResponse, (err, result) => {
          jsonObject = JSON.parse(JSON.stringify(result));
        });

        if (jsonObject.xwhep.work === undefined) {
          reject(`getWorkParam(): Not a work : ${uid}`);
          return;
        }

        const paramValue = jsonObject.xwhep.work[0][paramName];
        if (paramValue === undefined) {
          reject(`getWorkParam() : Invalid work parameter : ${paramName}`);
          return;
        }

        resolve(paramValue);
  	    return;
      }).catch((e) => {
        reject(`getWorkParam(): Work not found (${uid}) : ${e}`);
        return;
      });
    });
  }

  /**
   * This retrieves the status for the provided work.
   * This is a public method implemented in the smart contract
   * @param uid is the work unique identifier
   * @return a new Promise
   * @resolve a String containing the parameter value
   * @exception is thrown if work is not found
   * @exception is thrown if paramName does not represent a valid work parameter
   * @exception is thrown if parameter is read only
   * @see #getWorkParam(uid, paramName)
   */
  function getWorkStatus(cookies, uid) {
    return getWorkParam(cookies, uid, 'status');
  }

  /**
   * This sets the status of the provided work to PENDING
   * We don't call setWorkParam() since STATUS is supposed to be read only
   * This is a public method implemented in the smart contract
   * @param uid is the work unique identifier
   * @return a new Promise
   * @resolve undefined
   * @exception is thrown if work is not found
   * @exception is thrown if work status is not UNAVAILABLE
   * @exception is thrown if paramName does not represent a valid work parameter
   * @exception is thrown if parameter is read only (e.g. status, return code, etc.)
   */
  function setPending(cookies, uid) {
    return new Promise((resolve, reject) => {
      get(cookies, uid).then((getResponse) => {
        let jsonObject;
        parseString(getResponse, (err, result) => {
          jsonObject = JSON.parse(JSON.stringify(result));
        });

        if (jsonObject.xwhep.work === undefined) {
          reject(`setPending(): Not a work : ${uid}`);
          return;
        }

        if (jsonObject.xwhep.work[0].status.toString() !== 'UNAVAILABLE') {
          reject(`setPending(${uid}): Invalid status : ${jsonObject.xwhep.work[0].status}`);
          return;
        }

        jsonObject.xwhep.work[0].status = 'PENDING';
        console.log(`setPending(${uid}) send : ${JSON.stringify(jsonObject)}`);

        sendWork(cookies, json2xml(jsonObject, false)).then(() => {
          resolve();
          return;
        }).catch((err) => {
          reject(`setPending() error : ${err}`);
          return;
        });
      }).catch((e) => {
        reject(`setPending(): Work not found (${uid}) : ${e}`);
        return;
      });
    });
  }

  /**
   * This writes a new file
   * This is a private method not implemented in the smart contract
   * @param dataUid is the data identifier
   * @param fileContent is written into new file
   * @return a new Promise
   * @resolve file name
   * @exception is thrown on error
   */
  const writeFile = (dataUid, fileContent) => (
    new Promise((resolve, reject) => {
      const wFile = `/tmp/${dataUid}`;
	  fs.writeFile(wFile, fileContent, (err) => {
        if (err) {
          fs.unlink(wFile);
          reject(`writeFile() writeFile error : ${err}`);
          return;
  	    }
        resolve(wFile)
  	    return;
	  })
    })
  )
  /**
   * This registers a new data as stdin for the provided work
   * This is a private method not implemented in the smart contract
   * @param workUid is the work identifier
   * @param stdinContent is a string containing the stdin
   * @return a new Promise
   * @resolve undefined
   * @exception is thrown on error
   */
  const setStdinUri= (cookies, workUid, stdinContent) => (
	new Promise((resolve, reject) => {
		console.log(`setStdinUri(${cookies}, ${workUid}, ${stdinContent})`);
      if ((stdinContent === "") || (stdinContent === undefined)) {
    	  resolve();
    	  return;
      }

      console.log(`setStdinUri(${workUid})`);
      const dataUid = uuidV4();
      const dataDescription = `<data><uid>${dataUid}</uid><accessrights>0x755</accessrights><name>stdin.txt</name><status>UNAVAILABLE</status></data>`;
      sendData(cookies, dataDescription).then(() => {
    	writeFile(dataUid, stdinContent.concat("                                                            ")).then((dataFile) =>{
      	  uploadData(cookies, dataUid, dataFile).then(() => {
            get(cookies, dataUid).then((getResponse) => {
              let jsonObject;
        	  parseString(getResponse, (err, result) => {
          		jsonObject = JSON.parse(JSON.stringify(result));
              });
              if (jsonObject.xwhep.data === undefined) {
                reject(`setStdinUri() : can't retrieve data: ${dataUid}`);
                return;
              }
              const stdinUri = jsonObject.xwhep.data[0]['uri'];
              console.log(`setStdinUri(${workUid}) : ${stdinUri}`);
          	  setWorkParam(cookies, workUid, 'stdinuri', stdinUri).then(() => {
          	    console.log(`setStdinUri(${workUid}) ${workUid}#stdinuri = ${stdinUri}`);
       	        fs.unlink(dataFile);
       	        resolve();
       	        return;
          	  }).catch((err) => {
                fs.unlink(dataFile);
          	    reject(`setStdinUri() setWorkParam error : ${err}`);
          	    return;
          	  });
      	    }).catch((err) => {
        	  fs.unlink(dataFile);
        	  reject(`setStdinUri() get data error : ${err}`);
        	  return;
            });
      	  }).catch((err) => {
      		fs.unlink(dataFile);
      		reject(`setStdinUri() uploadData error : ${err}`);
      	    return;
          });
        }).catch((err) => {
          console.log(`setStdinUri() writeFile error : ${err}`);
      	  reject(`setStdinUri() writeFile error : ${err}`);
      	  return;
        });
      }).catch((err) => {
        console.log(`setStdinUri sendData error : ${err}`);
  		reject(`setStdinUri() sendData error : ${err}`);
  	    return;
      });
	})
  )

  /**
   * This registers a new PENDING work for the provided application.
   * Since the status is set to PENDING, this new work candidate for scheduling.
   * This is a public method implemented in the smart contract
   * @param appName is the application name
   * @param cmdLineParam is the command line parameter. This may be ""
   * @return a new Promise
   * @resolve the new work uid
   * @exception is thrown if application is not found
   */
  const submit = (cookies, user, provider, creator,appName, cmdLineParam, stdinContent,submitTxHash) => (
    new Promise((resolve, reject) => {
      console.log(`submit(${appName})`);
      register(cookies, user, provider, creator,appName,submitTxHash).then((workUid) => {
    	console.log(`submit(${appName}) : ${workUid}`);
        setWorkParam(cookies, workUid, 'cmdline', cmdLineParam).then(() => {
          setStdinUri(cookies, workUid, stdinContent).then(() => {
            setPending(cookies, workUid).then(() => {
              resolve(workUid);
              return;
            }).catch((msg) => {
              reject("submit() setPending error : ", msg);
              return;
            });
          }).catch((msg) => {
            reject("submit() setStdinUri error : ", msg);
      	    return;
          });
        }).catch((msg) => {
          reject("submit() setWorkParam error : ", msg);
          return;
        });
      }).catch((msg) => {
        reject("submit() register error : ", msg);
  	    return;
      });
    })
  )

  /**
   * This downloads a data
   * This is a private method not implemented in the smart contract
   * @param uri is the data uri
   * @param downloadedPath denotes a file in local fs
   * @return a new Promise
   * @resolve downloadedPath
   * @exception is thrown if work is not found
   * @exception is thrown if work result is not set
   */
  function download(cookies, uri, downloadedPath) {
	return new Promise((resolve, reject) => {
	  var state = "";
	  if((cookies !== undefined) && (cookies[0] !== undefined)) {
		var cookie = cookies[0];
		console.log(`sendWork(${cookies}) : cookie = ${cookie}`);
		state = getCookie(cookies, STATENAME);
	  }

	  console.log(`sendWork(${cookies}) ; ${STATENAME} = ${state}`);

	  var creds = CREDENTIALS;
	  if (state !== "") {
		creds = `?${STATENAME}=${state}`;
	  }

	  const uid = uri.substring(uri.lastIndexOf('/') + 1);

      const downloadPath = `${PATH_DOWNLOADDATA}/${uid}`;

      const options = {
        hostname,
        port,
        path: downloadPath + creds,
        method: 'GET',
        rejectUnauthorized: false,
      };

      process.env.NODE_TLS_REJECT_UNAUTHORIZED = '0';

//      console.log(`https://${options.hostname}:${options.port}${options.path}`);

      const outputStream = fs.createWriteStream(downloadedPath);
      outputStream.on('error', (e) => {
        reject(`download() : pipe error ${e}`);
  	    return;
      }).on('data', (d) => {
//        console.log(d);
      }).on('finish', () => {
        resolve(downloadedPath);
        return;
      });

      request.get(`https://${options.hostname}:${options.port}${options.path}`)
        .on('response', () => {
        })
        .on('error', (response) => {
          console.error(`download() : request error ${response}`);
          reject(`download() : request error ${response}`);
          return;
        })
        .pipe(outputStream);
    });
  }

  /**
   * This downloads the provided url
   * This is for testing only
   * @param url is the url to downaload
   * @param downloadedPath is the path to store the download
   * @return a new PRomise
   * @resolve downloadedPath
   */
  function downloadURL(url, downloadedPath) {
    return new Promise((resolve, reject) => {
      process.env.NODE_TLS_REJECT_UNAUTHORIZED = '0';

//      console.log(`${url}:${downloadedPath}`);

      const outputStream = fs.createWriteStream(downloadedPath);
      outputStream.on('error', (e) => {
        reject(`download() : pipe error ${e}`);
  	    return;
      }).on('data', (d) => {
//        console.log(d);
      }).on('finish', () => {
        resolve(downloadedPath);
        return;
      });

      request.get(url)
        .on('response', () => {
        })
        .on('error', (response) => {
          console.error(`downloadURL() : request error ${response}`);
          reject(`download() : request error ${response}`);
    	    return;
        })
        .pipe(outputStream);
    });
  }

  /**
   * This retrieves the result of the work
   * This is a private method not implemented in the smart contract
   * @param uid is the work unique identifier
   * @return a new Promise
   * @resolve a string containing xml representation of the result metadata or undefined, if not set
   * @exception is thrown if work is not found
   * @exception is thrown if work status is not COMPLETED
   */
  function getResult(cookies, uid) {
    return new Promise((resolve, reject) => {
      get(cookies, uid).then((getResponse) => {
        let jsonObject;
        parseString(getResponse, (err, result) => {
          jsonObject = JSON.parse(JSON.stringify(result));
        });
        if (jsonObject.xwhep.work === undefined) {
          reject(`getResult(): Not a work : ${uid}`);
          return;
        }

        if (jsonObject.xwhep.work[0].status.toString() !== 'COMPLETED') {
          reject(`getRestult(): Invalid status : ${jsonObject.xwhep.work[0].status}`);
          return;
        }

        if (jsonObject.xwhep.work[0].resulturi === undefined) {
          resolve(undefined);
          return;
        }
        const uri = jsonObject.xwhep.work[0].resulturi.toString();
        const resultUid = uri.substring(uri.lastIndexOf('/') + 1);

        resolve(get(resultUid));
      }).catch((err) => {
        reject(`getResult() error : ${err}`);
  	    return;
      });
    });
  }

  /**
   * This downloads the result of the work
   * This is a public method not implemented in the smart contract
   * @param uid is the work uid
   * @return a new Promise
   * @resolve a string containing the path of the downloaded result
   * @exception is thrown if work is not found
   * @exception is thrown if work result is not set
   */
  function downloadResult(cookies, uid) {
    return new Promise((resolve, reject) => {
      getResult(cookies, uid).then((getResponse) => {
        let jsonObject;
        parseString(getResponse, (err, result) => {
          jsonObject = JSON.parse(JSON.stringify(result));
        });
        if (jsonObject.xwhep.data === undefined) {
          reject(`downloadResult(): Not a data : ${uid}`);
          return;
        }

        if (jsonObject.xwhep.data[0].status.toString() !== 'AVAILABLE') {
          reject(`downloadResult(): Invalid status : ${jsonObject.xwhep.data[0].status}`);
          return;
        }

        let resultPath = `result.${uid}`;
        const dataName = jsonObject.xwhep.data[0].name;
        if (dataName !== undefined) {
          resultPath += `.${dataName.toString().toLowerCase()}`;
        }
        const dataType = jsonObject.xwhep.data[0].type;
        if (dataType !== undefined) {
          resultPath += `.${dataType.toString().toLowerCase()}`;
        } else {
          resultPath += '.txt';
        }
        const dataUri = jsonObject.xwhep.data[0].uri;
        if (dataUri === undefined) {
          reject(`downloadResult(): data uri not found : ${uid}`);
          return;
        }
        console.log(`downloadResult() calling download(${dataUri}, ${resultPath})`);
        download(cookies, dataUri.toString(), resultPath).then((downloadedPath) => {
          console.log(`downloadResult() : ${downloadedPath}`);
          resolve(downloadedPath);
          return;
        }).catch((msg) => {
          console.error(msg);
        });
      }).catch((msg) => {
        console.error(msg);
        reject(msg);
        return;
      });
    });
  }

  /**
   * This retrieves the result path
   * This is a public method not implemented in the smart contract
   * @param uid is the work unique identifier
   * @return a new Promise
   * @resolve the work result path on local file system; undefined if work has no result
   * @exception is thrown on local fs error, or if there is no result file for the provided work
   */
  function getResultPath(uid) {
    return new Promise((resolve, reject) => {
      fs.readdir('.', (ferr, files) => { // '/' denotes the root folder
        if (ferr) {
          reject(ferr);
          return;
        }

        files.forEach((file) => {
          if (file.indexOf(uid) !== -1) {
            resolve(file);
            return;
          }
        });
        reject(`getResultPath() : file not found ${uid}`);
  	    return;
      });
    });
  }

  /**
   * This removes the work
   * This is a public method implemented in the smart contract
   * @param uid is the uid of the object to retrieve
   * @return a Promise
   * @resolve undefined
   */
  // eslint-disable-next-line
  function remove(cookies, uid) {
    return new Promise((resolve, reject) => {
  	  var state = "";
	  if((cookies !== undefined) && (cookies[0] !== undefined)) {
		var cookie = cookies[0];
		console.log(`sendWork(${cookies}) : cookie = ${cookie}`);
		state = getCookie(cookies, STATENAME);
	  }

	  console.log(`sendWork(${cookies}) ; ${STATENAME} = ${state}`);

	  var creds = CREDENTIALS;
	  if (state !== "") {
		creds = `?${STATENAME}=${state}`;
	  }

	  let getResponse = '';

      const getPath = `${PATH_REMOVE}/${uid}`;
      const options = {
        hostname,
        port,
        path: getPath + creds,
        method: 'GET',
        rejectUnauthorized: false,
      };
//      console.log(`${options.hostname}:${options.port}${getPath}`);

      const req = https.request(options, (res) => {
        res.on('data', (d) => {
          const strd = String.fromCharCode.apply(null, new Uint16Array(d));
          getResponse += strd;
        });

        res.on('end', () => {
//          console.log(getResponse);
          resolve();
          return;
        });
      });

      req.on('error', (e) => {
        reject(e);
  	    return;
      });
      req.end();
    });
  }

  /**
   * This waits the work completion
   * This is a public method not implemented in the smart contract
   * @param uid is the work unique identifier
   * @return a new Promise
   * @resolve undefined
   * @exception is thrown if work is not found
   * @exception is thrown if work status is ERROR
   */
  function waitCompleted(cookies, uid) {
    return new Promise((resolve, reject) => {
      const theInterval = setInterval(() => {
        getWorkStatus(cookies, uid).then((newStatus) => {
          console.log(`waitCompleted ${newStatus}`);

          if (newStatus.toString() === 'ERROR') {
            clearInterval(theInterval);
            reject(`waitCompleted() : work ERROR (${uid})`);
            return;
          }
          if (newStatus.toString() === 'COMPLETED') {
            console.log("waitCompleted !");
            clearInterval(theInterval);
            console.log("waitCompleted. interval clear !");
            resolve();
            console.log("waitCompleted. reseolved!");
            return;
          }
          console.log(`waitCompleted sleeping ${WAITSTATUSDELAY}ms : ${uid} (${newStatus})`);
        }).catch((e) => {
          clearInterval(theInterval);
          reject(`waitCompleted() : ${e}`);
          return;
        });
      }, WAITSTATUSDELAY);
    });
  }

  /**
   * This submits a work for the provided application and waits for its completion
   * This is a public method implemented in the smart contract
   * @param appName is the application name
   * @param cmdLineParam is the command line parameter. This may be ""
   * @return a new Promise
   * @resolve the workuid and result path
   * @exception is thrown on submission error
   * @exception is thrown if work status is ERROR
   */
  function submitAndWait(cookies, user, provider, creator, appName, cmdLineParam,stdinContent,submitTxHash) {
    return new Promise((resolve, reject) => {
      let workuid;
      submit(cookies, user, provider, creator, appName, cmdLineParam,stdinContent,submitTxHash).then((uid) => {
        workuid = uid;
        console.log('submitAndWait() submission done');
        waitCompleted(cookies, uid).then(() => {
          console.log(`submitAndWait() COMPLETED ${workuid}`);
          downloadResult(cookies, workuid).then(() => {
            console.log(`submitAndWait() downloaded ${workuid}`);
            getResultPath(cookies, workuid).then((resultPath) => {
              console.log(`submitAndWait() path ${resultPath}`);
              resolve([workuid,resultPath]);
              return;
            }).catch((msg) => {
              reject(`submitAndWait() 1 : ${msg}`);
              return;
            });
          }).catch((msg) => {
            reject(`submitAndWait() 2 : ${msg}`);
      	    return;
          });
        }).catch((e) => {
          reject(`submitAndWait() 3 : ${e}`);
          return;
        });
      }).catch((e) => {
        reject(`submitAndWait() 4 : ${e}`);
  	    return;
      });
    });
  }


  /**
   * This submits a work for the provided application and waits for its completion and then return stdout
   * This is a public method implemented in the smart contract
   * @param appName is the application name
   * @param cmdLineParam is the command line parameter. This may be ""
   * @return a new Promise
   * @resolve the workuid and result path
   * @exception is thrown on submission error
   * @exception is thrown if work status is ERROR
   */
  function submitAndWaitAndGetStdout(cookies, user, provider, creator, appName, cmdLineParam,stdinContent,submitTxHash) {
    return new Promise((resolve, reject) => {
      let workuid;
      let resultPath;
      submitAndWait(cookies, user, provider, creator, appName, cmdLineParam,stdinContent,submitTxHash).then((results) => {
        [workuid, resultPath] = results;
        console.log('submitAndWaitAndGetResult() submitAndWait done');
        console.log(`submitAndWaitAndGetResult() path ${resultPath}`);

          dumpFile(resultPath).then((textContent) => {
            resolve([workuid,textContent]);
            return;
          }).catch((msg) => {
            reject(`submitAndWaitAndGetResult() : ${msg}`);
            return;
          });
      }).catch((e) => {
        reject(`submitAndWaitAndGetResult() : ${e}`);
  	    return;
      });
    });
  }


  /**
   * This dumps a text file
   * This is a private method not implemented in the smart contract
   * @param path is the text file path
   * @return a new Promise
   * @resolve a String containing the text file content
   */
  function dumpFile(path) {
    return new Promise((resolve, reject) => {
      const readableStream = fs.createReadStream(path);
      let data = '';

      readableStream.on('error', (err) => {
        reject(err);
  	    return;
      });
      readableStream.on('data', (chunk) => {
        data += chunk;
      });

      readableStream.on('end', () => {
        resolve(data);
        return;
      });
    });
  }

/**
 * This the content of the work stdout file
 * This is a public method implemented in the smart contract
 * @param uid is the work uid
 * @return a new Promise
 * @resolve a String containing the text file content
 * @exception is thrown if work is not found
 * @exception is thrown if stdout file is not found
 */
  function getStdout(cookies, uid) {
    return new Promise((resolve, reject) => {
      downloadResult(cookies, uid).then(() => {
        console.log(`getStdout() downloaded ${uid}`);
        getResultPath(uid).then((resultPath) => {
          console.log(`getStdout() path ${resultPath}`);
          dumpFile(resultPath).then((textContent) => {
            resolve(textContent);
      	    return;
          }).catch((msg) => {
        	reject(`getStdout() : ${msg}`);
      	    return;
          })
        }).catch((msg) => {
          reject(`getStdout() : ${msg}`);
          return;
        });
      }).catch((msg) => {
    	reject(`getStdout() : ${msg}`);
    	return;
      })
    });
  }

/**
 * This authenticates to xwhep service
 * @param jwttoken is a signed encoded Json Web Token that must contain 2 fields:
 * -1- iss : the issuer
 * -2- blockchainaddr : the hash of the user public key
 * @return a new Promise
 * @resolve a cookies table to be used to access the server
 */
  async function auth(jwtoken) {
    return new Promise((resolve, reject) => {
      const keepAliveAgent = new http.Agent({ keepAlive: true });
      const options = {
        hostname,
        port,
        path: PATH_ETHAUTH,
        method: 'GET',
        rejectUnauthorized: false,
        headers: {
          Cookie: `${ETHAUTHNAME}=${jwtoken}`,
            'content-type': 'text/plain',
            'connection': 'keep-alive',
            'accept': '*/*' 
        },
      };

//      console.log('auth() options', options);
      const req = https.request(options, (res) => {
//    	console.log('statusCode:', res.statusCode);
//        console.log('response.headers :', res.headers);
//    	console.log('response.headers.location :', res.headers.location);
    	var location = new URL(res.headers.location);
//        console.log(`location.path : ${location}`);
//        console.log(`location.search: ${location.search}`);
        const state = location.search.substring(1);
//        console.log(`state: ${state}`);

//    	console.log('response.headers.set-cookie :', res.headers['set-cookie']);
        res.on('data', (d) => {});

        res.on('end', () => {
          resolve(res.headers['set-cookie']);
//          resolve(state);
          return;
        });
      });

      req.on('error', (e) => {
        console.log('error', e)
        reject(e);
        return;
      });
      req.end();
    })
  }

  return {
    connectionError,
    sendWork,
    sendApp,
    uploadData,
    sendData,
    get,
    getApp,
    getApps,
    registerApp,
    register,
    setApplicationParam,
    setWorkParam,
    getWorkParam,
    getWorkStatus,
    setPending,
    submit,
    download,
    downloadURL,
    getResult,
    downloadResult,
    getResultPath,
    remove,
    waitCompleted,
    submitAndWait,
    submitAndWaitAndGetStdout,
    dumpFile,
    getStdout,
    auth,
  };
};
module.exports = createXWHEPClient;

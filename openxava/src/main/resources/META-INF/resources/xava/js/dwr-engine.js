/*
 * Copyright 2005 Joe Walker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 



















if (typeof dwr == 'undefined') dwr = {};

(function() {
if (!dwr.engine) dwr.engine = {};





dwr.engine.setErrorHandler = function(handler) {
dwr.engine._errorHandler = handler;
};





dwr.engine.setWarningHandler = function(handler) {
dwr.engine._warningHandler = handler;
};









dwr.engine.setTextHtmlHandler = function(handler) {
dwr.engine._textHtmlHandler = handler;
};

dwr.engine.setPollStatusHandler = function(handler) {
dwr.engine._pollStatusHandler = handler;
};





dwr.engine.setTimeout = function(timeout) {
dwr.engine._timeout = timeout;
};




dwr.engine.setPollTimeout = function(timeout) {
dwr.engine._pollTimeout = timeout;
};





dwr.engine.setPreHook = function(handler) {
dwr.engine._preHook = handler;
};





dwr.engine.setPostHook = function(handler) {
dwr.engine._postHook = handler;
};





dwr.engine.setOverridePath = function(path) {
dwr.engine._overridePath = path;
};





dwr.engine.setOverrideContextPath = function(path) {
dwr.engine._overrideContextPath = path;
};





dwr.engine.setCookieAttributes = function(attributeString) {
dwr.engine._cookieAttributes = attributeString;
};





dwr.engine.setHeaders = function(headers) {
dwr.engine._headers = headers;
};





dwr.engine.setAttributes = function(attributes) {
dwr.engine._attributes = attributes;
};





dwr.engine.setOrdered = function(ordered) {
dwr.engine._ordered = ordered;
};







dwr.engine.setAsync = function(async) {
dwr.engine._async = async;
};





dwr.engine.setActiveReverseAjax = function(activeReverseAjax) {
if (activeReverseAjax) {

if (dwr.engine._activeReverseAjax) return;

if (!dwr.engine._retryIntervals || dwr.engine._retryIntervals.length === 0) { dwr.engine._retryIntervals = dwr.engine._defaultRetryIntervals; }
dwr.engine._activeReverseAjax = true;
if (dwr.engine._initializing) return;
dwr.engine._poll();
}
else {

if (dwr.engine._activeReverseAjax && dwr.engine._pollBatch) {
dwr.engine.transport.abort(dwr.engine._pollBatch);
}
dwr.engine._activeReverseAjax = false;
}



};





dwr.engine.setNotifyServerOnPageUnload = function(notify, asyncUnload) {
dwr.engine._asyncUnload = (asyncUnload !== undefined) ? asyncUnload : false;
dwr.engine._isNotifyServerOnPageUnload = notify;
};





dwr.engine.setNotifyServerOnPageLoad = function(notify) {
dwr.engine._isNotifyServerOnPageLoad = notify;
if (notify && !dwr.engine._initializing && !dwr.engine._isNotifyServerOnPageLoadSent) {
eval("dwr.engine._execute(dwr.engine._pathToDwrServlet, '__System', 'pageLoaded', [ function() { dwr.engine._ordered = false; }]);");
dwr.engine._isNotifyServerOnPageLoadSent = true;
}
};





dwr.engine.setMaxRetries = function(maxRetries) {
dwr.engine._maxRetries = maxRetries;
};





dwr.engine.setRetryIntervals = function(intervalsArray) {
dwr.engine._retryIntervals = intervalsArray;
};






dwr.engine.defaultErrorHandler = function(message, ex) {
dwr.engine._debug("Error: " + ex.name + ", " + ex.message, true);
if (!message) message = "A server error has occurred.";

if (message.indexOf("0x80040111") != -1) return;
if ("false" == "true") alert(message);
};






dwr.engine.defaultWarningHandler = function(message, ex) {
dwr.engine._debug(message);
};






dwr.engine.defaultPollStatusHandler = function(newStatus, ex) {
dwr.engine.util.logHandlerEx(function() {
if (newStatus === false && dwr.engine._errorHandler) dwr.engine._errorHandler(ex.message, ex);
});
};




dwr.engine.beginBatch = function() {
if (dwr.engine._batch) {
dwr.engine._handleError(null, { name:"dwr.engine.batchBegun", message:"Batch already begun" });
return;
}
dwr.engine._batch = dwr.engine.batch.create();
};





dwr.engine.endBatch = function(options) {
var batch = dwr.engine._batch;
if (batch == null) {
dwr.engine._handleError(null, { name:"dwr.engine.batchNotBegun", message:"No batch in progress" });
return;
}
dwr.engine._batch = null;
if (batch.map.callCount === 0) {
return;
}


if (options) {
dwr.engine.batch.merge(batch, options);
}



if (batch.async && (dwr.engine._ordered || dwr.engine._internalOrdered) && dwr.engine._batchesLength !== 0) {
dwr.engine._batchQueue[dwr.engine._batchQueue.length] = batch;
}
else {
return dwr.engine.transport.send(batch);
}
};






dwr.engine.openInDownload = function(data) {
var div = document.createElement("div");
document.body.appendChild(div);
div.innerHTML = "<iframe width='0' height='0' scrolling='no' frameborder='0' src='" + data + "'></iframe>";
};















dwr.version = {



major:parseInt("3", 10),




minor:parseInt("0", 10),




revision:parseInt("3", 10),




build:parseInt("-1", 10),





title:"dev",




label:"3.0.3-dev"
};






dwr.engine._initializing = true;


dwr.engine._allowGetButMakeForgeryEasier = "false";


dwr.engine._scriptTagProtection = "throw 'allowScriptTagRemoting is false.';";


dwr.engine._pathToDwrServlet = "/openxavatest/dwr";


dwr.engine._overridePath = "";
if (typeof pathToDwrServlet != "undefined") {
dwr.engine._overridePath = pathToDwrServlet;
}

dwr.engine._effectivePath = function() {
return dwr.engine._overridePath || dwr.engine._pathToDwrServlet;
};


dwr.engine._contextPath = "/openxavatest";


dwr.engine._overrideContextPath = "";

dwr.engine._effectiveContextPath = function() {
return dwr.engine._overrideContextPath || dwr.engine._contextPath;
};


dwr.engine._cookieAttributes = "";


dwr.engine._useStreamingPoll = "true";

dwr.engine._pollOnline = true;


dwr.engine._ModePlainCall = "/call/plaincall/";
dwr.engine._ModePlainPoll = "/call/plainpoll/";
dwr.engine._ModeHtmlCall = "/call/htmlcall/";
dwr.engine._ModeHtmlPoll = "/call/htmlpoll/";


dwr.engine._async = Boolean("true");


dwr.engine._pageId = null;


dwr.engine._dwrSessionId = null;


dwr.engine._scriptSessionId = "";


dwr.engine._preHook = null;


dwr.engine._postHook = null;


dwr.engine._batches = {};


dwr.engine._batchesLength = 0;


dwr.engine._batchQueue = [];


dwr.engine._ordered = false;


dwr.engine._internalOrdered = false;


dwr.engine._batch = null;


dwr.engine._timeout = 0;


dwr.engine._activeReverseAjax = false;


dwr.engine._nextReverseAjaxIndex = 0;


dwr.engine._reverseAjaxQueue = {};


dwr.engine._pollBatch = null;


dwr.engine._pollTimeout = 0;


dwr.engine._pollCometInterval = 200;


dwr.engine._retries = 1;
dwr.engine._maxRetries = -1;


dwr.engine._retryIntervals = [];





dwr.engine._defaultRetryIntervals = [ 1, 3, 3 ];

dwr.engine._textHtmlHandler = null;


dwr.engine._headers = null;


if (typeof attributes != "undefined") {
dwr.engine._attributes = attributes;
}
else {
dwr.engine._attributes = null;
}


dwr.engine._nextBatchId = 0;


dwr.engine._instanceId = -1;


dwr.engine._propnames = [ "async", "timeout", "errorHandler", "warningHandler", "textHtmlHandler" ];


dwr.engine._partialResponseNo = 0;
dwr.engine._partialResponseYes = 1;


dwr.engine._isNotifyServerOnPageUnload = false;


dwr.engine._isNotifyServerOnPageLoad = false;
dwr.engine._isNotifyServerOnPageLoadSent = false;


dwr.engine._asyncUnload = false;





dwr.engine._mappedClasses = {};


dwr.engine._errorHandler = dwr.engine.defaultErrorHandler;


dwr.engine._warningHandler = dwr.engine.defaultWarningHandler;

dwr.engine._pollStatusHandler = dwr.engine.defaultPollStatusHandler;


dwr.engine._postSeperator = "\n";
dwr.engine._defaultInterceptor = function(data) { return data; };
dwr.engine._urlRewriteHandler = dwr.engine._defaultInterceptor;
dwr.engine._contentRewriteHandler = dwr.engine._defaultInterceptor;
dwr.engine._replyRewriteHandler = dwr.engine._defaultInterceptor;


dwr.engine._excludeObjectAttributes = {
"$dwrClassName": true,
"$dwrByRef": true,
"$_dwrConversionRef": true
};

dwr.engine._ieConditions = {};


dwr.engine._beforeUnloading = false;


dwr.engine._queuedBatchException = null;



dwr.engine._beforeUnloader = function() {
dwr.engine._beforeUnloading = true;



setTimeout(function() {



setTimeout(function() {
dwr.engine._beforeUnloading = false;
}, 1000);
}, 1);
};


dwr.engine._unloading = false;


dwr.engine._unloader = function() {
dwr.engine._unloading = true;


dwr.engine._batchQueue.length = 0;


var batch;
for (var batchId in dwr.engine._batches) {
batch = dwr.engine._batches[batchId];
if (batch.transport && batch.transport.abort) {
batch.transport.abort(batch);
}
}


if (dwr.engine._isNotifyServerOnPageUnload) {
dwr.engine._debug("calling unloader for: " + dwr.engine._scriptSessionId);
batch = {
map:{
callCount:1,
'c0-scriptName':'__System',
'c0-methodName':'pageUnloaded',
'c0-id':0
},
paramCount:0, isPoll:false, async:dwr.engine._asyncUnload,
headers:{}, preHooks:[], postHooks:[],
timeout:dwr.engine._timeout,
errorHandler:null, globalErrorHandler:dwr.engine._errorHandler, warningHandler:null, textHtmlHandler:null, globalTextHtmlHandler:null,
handlers:[{ exceptionHandler:null, callback:null }]
};
dwr.engine.transport.send(batch);
dwr.engine._isNotifyServerOnPageUnload = false;
}
};

function ignoreIfUnloading(batch, f) {

if (dwr.engine._unloading) return;

if (dwr.engine._beforeUnloading && (batch == null || batch.async)) {
setTimeout(function() {
ignoreIfUnloading(batch, f);
}, 100);
}

else {
return f();
}
}

dwr.engine._initializer = {



preInit: function() {

dwr.engine._pageId = dwr.engine.util.tokenify(new Date().getTime()) + "-" + dwr.engine.util.tokenify(Math.random() * 1E16);


dwr.engine.transport.updateDwrSessionFromCookie();


dwr.engine.util.addEventListener(window, 'beforeunload', dwr.engine._beforeUnloader);
dwr.engine.util.addEventListener(window, 'unload', dwr.engine._unloader);


var g = dwr.engine._global;
if (!g.dwr) g.dwr = {};
if (!g.dwr._) g.dwr._ = [];
dwr.engine._instanceId = g.dwr._.length;
g.dwr._[dwr.engine._instanceId] = dwr;
},

loadDwrConfig: function() {
if (typeof dwrConfig != "undefined") {
for(p in dwrConfig) {
var methodName = "set" + p.charAt(0).toUpperCase() + p.substring(1);
var setter = dwr.engine[methodName];
if (setter) setter(dwrConfig[p]);
}
}
},

init: function() {
dwr.engine._initializer.preInit();
dwr.engine._initializer.loadDwrConfig();
dwr.engine._initializing = false;

if (dwr.engine._isNotifyServerOnPageLoad) {
eval("dwr.engine._execute(dwr.engine._pathToDwrServlet, '__System', 'pageLoaded', [ function() { dwr.engine._ordered = false; }]);");
dwr.engine._isNotifyServerOnPageLoadSent = true;
}
if (dwr.engine._activeReverseAjax) {
dwr.engine._poll();
}
}
};











dwr.engine._execute = function(overridePath, scriptName, methodName, args) {
var path = overridePath || dwr.engine._effectivePath();
dwr.engine._singleShot = false;
if (dwr.engine._batch == null) {
dwr.engine.beginBatch();
dwr.engine._singleShot = true;
}

var batch = dwr.engine._batch;


if (batch.path == null) {
batch.path = path;
}
else {
if (batch.path != path) {
dwr.engine._handleError(batch, { name:"dwr.engine.multipleServlets", message:"Can't batch requests to multiple DWR Servlets." });
return;
}
}

dwr.engine.batch.addCall(batch, scriptName, methodName, args);

if (dwr.engine._isHeartbeatBatch(batch)) {

batch.timeout = 750;
}


batch.map.callCount++;
if (dwr.engine._singleShot) {
return dwr.engine.endBatch();
}
};





dwr.engine._poll = function() {
if (!dwr.engine._activeReverseAjax) {
return;
}
dwr.engine._pollBatch = dwr.engine.batch.createPoll();
dwr.engine.transport.send(dwr.engine._pollBatch);
};


dwr.engine._executeScript = function(script) {
if (script == null) {
return null;
}
if (script === "") {
dwr.engine._debug("Warning: blank script", true);
return null;
}

// tmr (new Function("dwr", script))(dwr);
// tmr ini
if (script.includes('handleCallback("0"')) {
//if (true) {
	console.log("[executeScript] TRUE"); // tmr
	var functionCallIndex = script.indexOf(".handleCallback(");
	var jsonIndex = script.indexOf('{', functionCallIndex + 10);
	var paramsEndIndex = jsonIndex > 0?jsonIndex:9999;
	var paramsString = script.substring(functionCallIndex + 10, paramsEndIndex);
	var params = paramsString.match(/"([^"]*)"/g);
	var param1 = params[0].replaceAll('"', '');
	var param2 = params[1].replaceAll('"', '');
	console.log("[executeScript] param1=" + param1); // tmr
	console.log("[executeScript] param2=" + param2); // tmr
	var param3 = null;
	if (jsonIndex > 0) {
		var endIndex = script.indexOf('})();', jsonIndex);
		var json = script.substring(jsonIndex, endIndex - 4);
		json = json.replaceAll(':true', ':"true"').replaceAll(':false', ':"false"');
		json = json.replace(/([{,])(\s*)([a-zA-Z0-9_\-]+?)\s*:/g, '$1"$3":');
		// tmr var response = JSON.parse('{"120,false,false,false":{"confirmMessage":"","name":"Mode.list","takesLong":"false"},"66,true,false,false":{"confirmMessage":"","name":"SearchForCRUD.search","takesLong":"false"},"80,true,true,false":{"confirmMessage":"","name":"Print.generatePdf","takesLong":"false"},"39,true,true,false":{"confirmMessage":"","name":"Navigation.next","takesLong":"false"},"83,true,false,false":{"confirmMessage":"","name":"CRUD.save","takesLong":"false"},"68,true,false,false":{"confirmMessage":"Borra los registros seleccionados: ¿Estás seguro?","name":"CRUD.deleteSelected","takesLong":"false"},"36,true,true,false":{"confirmMessage":"","name":"Navigation.first","takesLong":"false"},"37,true,true,false":{"confirmMessage":"","name":"Navigation.previous","takesLong":"false"},"78,true,false,false":{"confirmMessage":"","name":"CRUD.new","takesLong":"false"},"88,true,true,false":{"confirmMessage":"","name":"Print.generateExcel","takesLong":"false"}}');
		console.log("[executeScript] json.9=" + json); // tmr
		param3 = JSON.parse(json);
	}
	else {
		param3 = params[2].replaceAll('"', '');
	}
	console.log("[executeScript] param3=" + param3); // tmr 
	
	dwr.engine.remote.handleCallback(param1,param2,param3); 
	 
	// tmr dwr.engine.remote.handleCallback("0","0",{"120,false,false,false":{confirmMessage:"",name:"Mode.list",takesLong:false},"66,true,false,false":{confirmMessage:"",name:"SearchForCRUD.search",takesLong:false},"80,true,true,false":{confirmMessage:"",name:"Print.generatePdf",takesLong:false},"39,true,true,false":{confirmMessage:"",name:"Navigation.next",takesLong:false},"83,true,false,false":{confirmMessage:"",name:"CRUD.save",takesLong:false},"68,true,false,false":{confirmMessage:"Borra los registros seleccionados: \u00BFEst\u00E1s seguro?",name:"CRUD.deleteSelected",takesLong:false},"36,true,true,false":{confirmMessage:"",name:"Navigation.first",takesLong:false},"37,true,true,false":{confirmMessage:"",name:"Navigation.previous",takesLong:false},"78,true,false,false":{confirmMessage:"",name:"CRUD.new",takesLong:false},"88,true,true,false":{confirmMessage:"",name:"Print.generateExcel",takesLong:false}});
}
else {
	console.log("[executeScript] NEVER"); // tmr
	
		var functionCallIndex = script.indexOf(".handleCallback(");
	var jsonIndex = script.indexOf('{', functionCallIndex + 10);
	var paramsEndIndex = jsonIndex > 0?jsonIndex:9999;
	var paramsString = script.substring(functionCallIndex + 10, paramsEndIndex);
	var params = paramsString.match(/"([^"]*)"/g);
	var param1 = params[0].replaceAll('"', '');
	var param2 = params[1].replaceAll('"', '');
	console.log("[executeScript] param1=" + param1); // tmr
	console.log("[executeScript] param2=" + param2); // tmr
	var param3 = null;
	if (jsonIndex > 0) {
		var endIndex = script.indexOf('})();', jsonIndex);
		var json = script.substring(jsonIndex, endIndex - 4);
		//json = json.replaceAll(':true', ':"true"').replaceAll(':false', ':"false"');
		json = json.replace(/([{,])(\s*)([a-zA-Z0-9_\-]+?)\s*:/g, '$1"$3":');
		// tmr var response = JSON.parse('{"120,false,false,false":{"confirmMessage":"","name":"Mode.list","takesLong":"false"},"66,true,false,false":{"confirmMessage":"","name":"SearchForCRUD.search","takesLong":"false"},"80,true,true,false":{"confirmMessage":"","name":"Print.generatePdf","takesLong":"false"},"39,true,true,false":{"confirmMessage":"","name":"Navigation.next","takesLong":"false"},"83,true,false,false":{"confirmMessage":"","name":"CRUD.save","takesLong":"false"},"68,true,false,false":{"confirmMessage":"Borra los registros seleccionados: ¿Estás seguro?","name":"CRUD.deleteSelected","takesLong":"false"},"36,true,true,false":{"confirmMessage":"","name":"Navigation.first","takesLong":"false"},"37,true,true,false":{"confirmMessage":"","name":"Navigation.previous","takesLong":"false"},"78,true,false,false":{"confirmMessage":"","name":"CRUD.new","takesLong":"false"},"88,true,true,false":{"confirmMessage":"","name":"Print.generateExcel","takesLong":"false"}}');
		console.log("[executeScript] json.9=" + json); // tmr
		param3 = JSON.parse(json);
		// tmr ini
		console.log("[executeScript] >>>> changedParts");
		var obj = param3.changedParts;
		for (let key in obj) {
		    console.log(key + ": " + obj[key]);
		    /*
		    var obj2 = obj[key]; 
			for (let key in obj2) {
		    	console.log(">> " + key + ": " + obj2[key]);
			}
			*/		    
		}
		console.log("[executeScript] <<<<");
		//param3.changedParts.ox_openxavatest_Invoice__core = "Hola"; 
		// tmr fin	
	}
	else {
		param3 = params[2].replaceAll('"', '');
	}
	console.log("[executeScript] param3=" + param3); // tmr 
	
	dwr.engine.remote.handleCallback(param1,param2,param3,param3);
	
	//dwr.engine.remote.handleCallback("1","0",{application:"openxavatest",changedParts:{"ox_openxavatest_Invoice__core":"Hola"},currentRow:-1,dataChanged:false,dialogLevel:0,dialogTitle:null,editorsWithError:null,editorsWithoutError:null,error:null,focusPropertyId:"listConfigurations",forwardInNewWindow:false,forwardURL:null,forwardURLs:null,hasPostJS:false,hideDialog:false,module:"Invoice",nextModule:null,propertiesUsedInCalculations:null,reload:false,resizeDialog:false,selectedRows:null,showDialog:false,strokeActions:{"120,false,false,false":{confirmMessage:"",name:"Mode.list",takesLong:false},"66,true,false,false":{confirmMessage:"",name:"SearchForCRUD.search",takesLong:false},"80,true,true,false":{confirmMessage:"",name:"Print.generatePdf",takesLong:false},"39,true,true,false":{confirmMessage:"",name:"Navigation.next",takesLong:false},"83,true,false,false":{confirmMessage:"",name:"CRUD.save",takesLong:false},"68,true,false,false":{confirmMessage:"Borra los registros seleccionados: \u00BFEst\u00E1s seguro?",name:"CRUD.deleteSelected",takesLong:false},"36,true,true,false":{confirmMessage:"",name:"Navigation.first",takesLong:false},"37,true,true,false":{confirmMessage:"",name:"Navigation.previous",takesLong:false},"78,true,false,false":{confirmMessage:"",name:"CRUD.new",takesLong:false},"88,true,true,false":{confirmMessage:"",name:"Print.generateExcel",takesLong:false}},urlParam:null,viewMember:"",viewSimple:false}, param3);
	//dwr.engine.remote.handleCallback("1","0",{application:"openxavatest",changedParts:{"ox_openxavatest_Invoice__core":"\n\n\n\n\n\n\n\n\n\n\n<form id=\"ox_openxavatest_Invoice__form\" name=\"ox_openxavatest_Invoice__form\"\n\tclass=\"xava_form\" method='POST'  \n\t>\n\t\n\t\n<INPUT type=\"hidden\" name=\"ox_openxavatest_Invoice__xava_action\" value=\"\"\/>\n<INPUT type=\"hidden\" name=\"ox_openxavatest_Invoice__xava_action_argv\" value=\"\"\/>\n<INPUT type=\"hidden\" name=\"ox_openxavatest_Invoice__xava_action_range\" value=\"\"\/>\n<INPUT type=\"hidden\" name=\"ox_openxavatest_Invoice__xava_action_already_processed\" value=\"\"\/>\n<INPUT type=\"hidden\" name=\"ox_openxavatest_Invoice__xava_action_application\" value=\"openxavatest\"\/>\n<INPUT type=\"hidden\" name=\"ox_openxavatest_Invoice__xava_action_module\" value=\"Invoice\"\/>\n<INPUT type=\"hidden\" name=\"ox_openxavatest_Invoice__xava_changed_property\"\/>\n<INPUT type=\"hidden\" id=\"ox_openxavatest_Invoice__xava_current_focus\" \n\tname=\"ox_openxavatest_Invoice__xava_current_focus\"\/>\n<INPUT type=\"hidden\" id=\"ox_openxavatest_Invoice__xava_previous_focus\" \n\tname=\"ox_openxavatest_Invoice__xava_previous_focus\"\/>\n<INPUT type=\"hidden\" name=\"ox_openxavatest_Invoice__xava_focus_forward\"\/> \n<INPUT type=\"hidden\" id=\"ox_openxavatest_Invoice__xava_focus_property_id\" \n\tname=\"ox_openxavatest_Invoice__xava_focus_property_id\" value=\"listConfigurations\"\/>\n\t\n\n\n<div class='ox-list-mode'>\n\n\t \n    <div id='ox_openxavatest_Invoice__button_bar' class=\"ox-button-bar-container\">     \n\t\t\n\n\n\n\n\n\n\n \n\n\n\n\n\n\n \n\n\n\n\n\n\n\n\n\t<div class=\"ox-button-bar\"> \n\t<div id=\"ox_openxavatest_Invoice__controllerElement\">\n\t<span>\n\t\n\t\t\t\n\n\n\n\n\n\n\n\n\n\n\n\n\n\t\t\t\n<span class=\"ox-button-bar-button\">\t\n<input name='ox_openxavatest_Invoice__action___CRUD___new' type='hidden'\/><a id='ox_openxavatest_Invoice__CRUD___new' title='Nuevo - control N - Crea una nueva entidad' class='xava_action_loses_changed_data ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='CRUD.new' data-in-new-window='false'>\n\t\t\n\t\t\n\t\t<i class=\"mdi mdi-library-plus\"><\/i>\n\t\t\t\t\t \t\t\t\t \t\t\t\n\t\t<span class=\"ox-action-label\">Nuevo<\/span> \n\t\t\t\t\n\t<\/a>\n\t\t\n<\/span>\n\t\t\n\t\t\t\n\t\t\t\n\n\n\n\n\n\n\n\n\n\n\n\n\n\t\t\t\n<span class=\"ox-button-bar-button\">\t\n<input name='ox_openxavatest_Invoice__action___CRUD___deleteSelected' type='hidden'\/><a id='ox_openxavatest_Invoice__CRUD___deleteSelected' title='Borrar - control D - Borra los registros seleccionados' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='Borra los registros seleccionados: \u00BFEst\u00E1s seguro?' data-takes-long='false' data-action='CRUD.deleteSelected' data-in-new-window='false'>\n\t\t\n\t\t\n\t\t<i class=\"mdi mdi-delete\"><\/i>\n\t\t\t\t\t \t\t\t\t \t\t\t\n\t\t<span class=\"ox-action-label\">Borrar<\/span> \n\t\t\t\t\n\t<\/a>\n\t\t\n<\/span>\n\t\t\n\t\t\t\n\t\t\t\n\n\n\n\n\n\n\n\n\n\n\n\n\n\t\t\t\n<span class=\"ox-button-bar-button\">\t\n<input name='ox_openxavatest_Invoice__action___Print___generatePdf' type='hidden'\/><a id='ox_openxavatest_Invoice__Print___generatePdf' title='Generar PDF - control alt P - Hace un listado en formato PDF de los datos visualizados en la lista' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='Print.generatePdf' data-in-new-window='true'>\n\t\t\n\t\t\n\t\t<i class=\"mdi mdi-file-pdf\"><\/i>\n\t\t\t\t\t \t\t\t\t \t\t\t\n\t\t<span class=\"ox-action-label\">Generar PDF<\/span> \n\t\t\t\t\n\t<\/a>\n\t\t\n<\/span>\n\t\t\n\t\t\t\n\t\t\t\n\n\n\n\n\n\n\n\n\n\n\n\n\n\t\t\t\n<span class=\"ox-button-bar-button\">\t\n<input name='ox_openxavatest_Invoice__action___Print___generateExcel' type='hidden'\/><a id='ox_openxavatest_Invoice__Print___generateExcel' title='Generar Excel - control alt X - Hace un listado en formato Excel de los datos visualizados en la lista' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='Print.generateExcel' data-in-new-window='true'>\n\t\t\n\t\t\n\t\t<i class=\"mdi mdi-file-excel\"><\/i>\n\t\t\t\t\t \t\t\t\t \t\t\t\n\t\t<span class=\"ox-action-label\">Generar Excel<\/span> \n\t\t\t\t\n\t<\/a>\n\t\t\n<\/span>\n\t\t\n\t\t\t\n\t\t\t\n\n\n\n\n\n\n\n\n\n\n\n\n\n\t\t\t\n<span class=\"ox-button-bar-button\">\t\n<input name='ox_openxavatest_Invoice__action___ExtendedPrint___myReports' type='hidden'\/><a id='ox_openxavatest_Invoice__ExtendedPrint___myReports' title='Mis informes' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='ExtendedPrint.myReports' data-in-new-window='false'>\n\t\t\n\t\t\n\t\t<i class=\"mdi mdi-library-books\"><\/i>\n\t\t\t\t\t \t\t\t\t \t\t\t\n\t\t<span class=\"ox-action-label\">Mis informes<\/span> \n\t\t\t\t\n\t<\/a>\n\t\t\n<\/span>\n\t\t\n\t\t\t\n\t\t\t\n\n\n\n\n\n\n\n\n\n\n\n\n\n\t\t\t\n<span class=\"ox-button-bar-button\">\t\n<input name='ox_openxavatest_Invoice__action___ImportData___importData' type='hidden'\/><a id='ox_openxavatest_Invoice__ImportData___importData' title='Importar datos' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='ImportData.importData' data-in-new-window='false'>\n\t\t\n\t\t\n\t\t<i class=\"mdi mdi-import\"><\/i>\n\t\t\t\t\t \t\t\t\t \t\t\t\n\t\t<span class=\"ox-action-label\">Importar datos<\/span> \n\t\t\t\t\n\t<\/a>\n\t\t\n<\/span>\n\t\t\n\t\t\t\n\t<\/span>\n\t<\/div>\n\n\t<div id=\"ox_openxavatest_Invoice__modes\">\n\t<span>\n\t<span class=\"ox-list-formats\">\n\t\n\t<input name='ox_openxavatest_Invoice__action___ListFormat___select' type='hidden'\/><a  title='Select' class='xava_action ox-selected-list-format' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='ListFormat.select' data-argv='editor=List' data-in-new-window='false'>\n\t\t<i class=\"mdi mdi-table-large\" title=\"Lista\"><\/i>\n\t<\/a>\t\t\t\n\t\n\t<input name='ox_openxavatest_Invoice__action___ListFormat___select' type='hidden'\/><a  title='Select' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='ListFormat.select' data-argv='editor=Charts' data-in-new-window='false'>\n\t\t<i class=\"mdi mdi-chart-line\" title=\"Gr\u00E1ficos\"><\/i>\n\t<\/a>\t\t\t\n\t\n\t<input name='ox_openxavatest_Invoice__action___ListFormat___select' type='hidden'\/><a  title='Select' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='ListFormat.select' data-argv='editor=Cards' data-in-new-window='false'>\n\t\t<i class=\"mdi mdi-view-module\" title=\"Cards\"><\/i>\n\t<\/a>\t\t\t\n\t\n\t<input name='ox_openxavatest_Invoice__action___ListFormat___select' type='hidden'\/><a  title='Select' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='ListFormat.select' data-argv='editor=Calendar' data-in-new-window='false'>\n\t\t<i class=\"mdi mdi-calendar\" title=\"Calendar\"><\/i>\n\t<\/a>\t\t\t\n\t\n\t<\/span>\n\t\t\n\t\t\n\t\t\t<span class=\"ox-unsubscribed\"> \n\t\t\t\t<input name='ox_openxavatest_Invoice__action___EmailNotifications___subscribe' type='hidden'\/>\n<a id='ox_openxavatest_Invoice__EmailNotifications___subscribe'\n title='Seguir' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='EmailNotifications.subscribe' data-in-new-window='false'><i class='mdi mdi-rss'><\/i><\/a>\n\t\t\t<\/span> \t\n\t\n\t\t<span class=\"ox-help\">  \n\t\t\t<a href=\"http:\/\/www.openxava.org\/OpenXavaDoc\/docs\/help_es.html\" target=\"_blank\">\n\t\t\t\t\n\t\t\t\t<i class=\"mdi mdi-help-circle\"><\/i>\n\t\t\t\t\n\t\t\t<\/a>\n\t\t<\/span>\n\t\n\t&nbsp;\n\t<\/span>\t\t\n\t<\/div>\t<!-- modes -->\n\t<\/div>\n\t\n \n\t<\/div>\n\t\n\t    \n    \n    <div class=\"ox-view\">\n        \n    \n    \t<div id='ox_openxavatest_Invoice__errors' class=\"ox-display-inline\"> \n    \t\t\n\n\n\n\n\n\n\n\n\n\n\t\t<\/div>\n    \n\t\t<div id='ox_openxavatest_Invoice__messages' class=\"ox-display-inline\"> \n\t\t\t\n\n\n\n\n\n\n\n\n\n\t\t<\/div>            \n\n    \t<div id='ox_openxavatest_Invoice__view' >\n\t\t\t\n\n\n\n\n\n \n\n\n\n\n\n\n\n\n\n\n<table width=\"100%\" class=>\n<tr><td class=ox-list-title>\n\n\n\n\n\n \r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n<select class=\"xava_list_configurations\" name='ox_openxavatest_Invoice__listConfigurations' title=\"Todos\">\r\n\t<option value=\"\">Todos<\/option>\r\n\t\r\n<\/select>\n\n\n\n\n\n\n<select class=\"xava_group_by\">\n\t<option value=\"\">Sin agrupar<\/option>\n\t\n\t<option value=\"year\" >Agrupado por a\u00F1o<\/option>\n\t\n\t<option value=\"number\" >Agrupado por n\u00FAmero<\/option>\n\t\n\t<option value=\"date\" >Agrupado por fecha<\/option>\n\t\n\t<option value=\"date[month]\" >Agrupado por mes de fecha<\/option>\n\t\t\t\n\t<option value=\"date[year]\" >Agrupado por a\u00F1o de fecha<\/option> \n\t\n\t<option value=\"paid\" >Agrupado por pagada<\/option>\n\t\n<\/select> \n\n<\/td><\/tr>\n<\/table>\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n<input type=\"hidden\" name=\"xava_list_filter_visible\"\/>\n\n\n<div class=\"ox_openxavatest_Invoice__list_scroll  ox-overflow-auto\">\n<table id=\"ox_openxavatest_Invoice__list\" class=\"xava_sortable_column ox-list\" >\n \n<tr class=\"ox-list-header\">\n<th class=\"ox-list-header ox-text-align-center\">\n<nobr>\n\t\n\t<a  id=\"ox_openxavatest_Invoice__customize_list\" class=\"xava_customize_list ox-image-link\" \n\t\ttitle=\"Personalizar lista\" data-id=\"list\">\n\t\t<i class=\"mdi mdi-settings\"><\/i>\n\t<\/a>\n\t\n\t \n\t<a id=\"ox_openxavatest_Invoice__show_filter_list\" class='xava_show_hide_filter ox-display-none'  \n\t\ttitle=\"Mostrar filtros\"\n\t\tdata-id=\"list\" data-tab-object=\"xava_tab\" data-visible=\"true\">\n\t\t<i id=\"ox_openxavatest_Invoice__filter_image_list\" class=\"mdi mdi-filter\"><\/i>\n\t<\/a>\n\t<a id=\"ox_openxavatest_Invoice__hide_filter_list\" class='xava_show_hide_filter' \n\t\ttitle=\"Ocultar filtros\"\n\t\tdata-id=\"list\" data-tab-object=\"xava_tab\" data-visible=\"false\">\n\t\t<i id=\"ox_openxavatest_Invoice__filter_image_list\" class=\"mdi mdi-filter-remove\"><\/i>  \n\t<\/a>\t\n\t\t\n\t\n\t<span class=\"ox_openxavatest_Invoice__customize_list ox-display-none\">\n\t<input name='ox_openxavatest_Invoice__action___List___addColumns' type='hidden'\/>\n<a id='ox_openxavatest_Invoice__List___addColumns'\n title='A\u00F1adir columnas a la lista' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.addColumns' data-in-new-window='false'><i class='mdi mdi-table-column-plus-after'><\/i><\/a>\n\t<\/span>\t\n\t\n<\/nobr> \n<\/th>\n<th class=\"ox-list-header\" width=\"5\">\n\t\n\t<input type=\"checkbox\" name=\"ox_openxavatest_Invoice__xava_selected_all\" value=\"selected_all\" \n\t\tdata-on-select-collection-element-action=\"\"\n\t\tdata-view-object=\"xava_view\"\n\t\tdata-prefix=\"\"\n\t\tdata-tab-object=\"xava_tab\"\/>\n\t\n<\/th>\n\n<th class=\"ox-list-header ox-padding-right-0 ox-vertical-align-middle \" data-property=\"year\">\n \n<div id=\"ox_openxavatest_Invoice__list_col0\" class=\"\" >\n\n<span class=\"ox_openxavatest_Invoice__customize_list ox-display-none\">\n<i class=\"xava_handle mdi mdi-cursor-move\"><\/i>\n<\/span>\n\n\n<span class=\"\">\n\n<input name='ox_openxavatest_Invoice__action___List___orderBy' type='hidden'\/><a  title='Ordenar por - Ordenar por esta columna' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.orderBy' data-argv='property=year' data-in-new-window='false'> <nobr>A\u00F1o<i class='mdi mdi-sort-numeric'><\/i><\/nobr><\/a>&nbsp;\n<\/span>\n\n\t<span class=\"ox_openxavatest_Invoice__customize_list ox-display-none ox-column-customize-controls-on-right\">\n\t<input name='ox_openxavatest_Invoice__action___List___changeColumnName' type='hidden'\/>\n<a  title='Cambiar nombre de columna' class='xava_action ox-image-link' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.changeColumnName' data-argv='property=year' data-in-new-window='false'><i class='mdi mdi-rename-box'><\/i><\/a>\n\t<a class=\"xava_remove_column\" title=\"Quitar columna\"\n\t\tdata-column=\"ox_openxavatest_Invoice__list_col0\" data-tab-object=\"xava_tab\">\t\n\t\t<i class=\"mdi mdi-close-circle\"><\/i>\n\t<\/a>\n\t<\/span>\n\n<\/div> \n\n<\/th>\n\n<th class=\"ox-list-header ox-padding-right-0 ox-vertical-align-middle \" data-property=\"number\">\n \n<div id=\"ox_openxavatest_Invoice__list_col1\" class=\"\" >\n\n<span class=\"ox_openxavatest_Invoice__customize_list ox-display-none\">\n<i class=\"xava_handle mdi mdi-cursor-move\"><\/i>\n<\/span>\n\n\n<span class=\"\">\n\n<input name='ox_openxavatest_Invoice__action___List___orderBy' type='hidden'\/><a  title='Ordenar por - Ordenar por esta columna' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.orderBy' data-argv='property=number' data-in-new-window='false'> <nobr>N\u00FAmero<i class='mdi mdi-sort-numeric'><\/i><\/nobr><\/a>&nbsp;\n<\/span>\n\n\t<span class=\"ox_openxavatest_Invoice__customize_list ox-display-none ox-column-customize-controls-on-right\">\n\t<input name='ox_openxavatest_Invoice__action___List___changeColumnName' type='hidden'\/>\n<a  title='Cambiar nombre de columna' class='xava_action ox-image-link' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.changeColumnName' data-argv='property=number' data-in-new-window='false'><i class='mdi mdi-rename-box'><\/i><\/a>\n\t<a class=\"xava_remove_column\" title=\"Quitar columna\"\n\t\tdata-column=\"ox_openxavatest_Invoice__list_col1\" data-tab-object=\"xava_tab\">\t\n\t\t<i class=\"mdi mdi-close-circle\"><\/i>\n\t<\/a>\n\t<\/span>\n\n<\/div> \n\n<\/th>\n\n<th class=\"ox-list-header ox-padding-right-0 ox-vertical-align-middle \" data-property=\"date\">\n \n<div id=\"ox_openxavatest_Invoice__list_col2\" class=\"\" >\n\n<span class=\"ox_openxavatest_Invoice__customize_list ox-display-none\">\n<i class=\"xava_handle mdi mdi-cursor-move\"><\/i>\n<\/span>\n\n\n<span class=\"\">\n\n<input name='ox_openxavatest_Invoice__action___List___orderBy' type='hidden'\/><a  title='Ordenar por - Ordenar por esta columna' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.orderBy' data-argv='property=date' data-in-new-window='false'> <nobr>Fecha<i class='mdi mdi-sort-alphabetical'><\/i><\/nobr><\/a>&nbsp;\n<\/span>\n\n\t<span class=\"ox_openxavatest_Invoice__customize_list ox-display-none ox-column-customize-controls-on-right\">\n\t<input name='ox_openxavatest_Invoice__action___List___changeColumnName' type='hidden'\/>\n<a  title='Cambiar nombre de columna' class='xava_action ox-image-link' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.changeColumnName' data-argv='property=date' data-in-new-window='false'><i class='mdi mdi-rename-box'><\/i><\/a>\n\t<a class=\"xava_remove_column\" title=\"Quitar columna\"\n\t\tdata-column=\"ox_openxavatest_Invoice__list_col2\" data-tab-object=\"xava_tab\">\t\n\t\t<i class=\"mdi mdi-close-circle\"><\/i>\n\t<\/a>\n\t<\/span>\n\n<\/div> \n\n<\/th>\n\n<th class=\"ox-list-header ox-padding-right-0 ox-vertical-align-middle \" data-property=\"amountsSum\">\n \n<div id=\"ox_openxavatest_Invoice__list_col3\" class=\"\" >\n\n<span class=\"ox_openxavatest_Invoice__customize_list ox-display-none\">\n<i class=\"xava_handle mdi mdi-cursor-move\"><\/i>\n<\/span>\n\n\nSuma importes&nbsp;\n\n\t<span class=\"ox_openxavatest_Invoice__customize_list ox-display-none ox-column-customize-controls-on-right\">\n\t<input name='ox_openxavatest_Invoice__action___List___changeColumnName' type='hidden'\/>\n<a  title='Cambiar nombre de columna' class='xava_action ox-image-link' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.changeColumnName' data-argv='property=amountsSum' data-in-new-window='false'><i class='mdi mdi-rename-box'><\/i><\/a>\n\t<a class=\"xava_remove_column\" title=\"Quitar columna\"\n\t\tdata-column=\"ox_openxavatest_Invoice__list_col3\" data-tab-object=\"xava_tab\">\t\n\t\t<i class=\"mdi mdi-close-circle\"><\/i>\n\t<\/a>\n\t<\/span>\n\n<\/div> \n\n<\/th>\n\n<th class=\"ox-list-header ox-padding-right-0 ox-vertical-align-middle \" data-property=\"vat\">\n \n<div id=\"ox_openxavatest_Invoice__list_col4\" class=\"\" >\n\n<span class=\"ox_openxavatest_Invoice__customize_list ox-display-none\">\n<i class=\"xava_handle mdi mdi-cursor-move\"><\/i>\n<\/span>\n\n\nI.V.A.&nbsp;\n\n\t<span class=\"ox_openxavatest_Invoice__customize_list ox-display-none ox-column-customize-controls-on-right\">\n\t<input name='ox_openxavatest_Invoice__action___List___changeColumnName' type='hidden'\/>\n<a  title='Cambiar nombre de columna' class='xava_action ox-image-link' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.changeColumnName' data-argv='property=vat' data-in-new-window='false'><i class='mdi mdi-rename-box'><\/i><\/a>\n\t<a class=\"xava_remove_column\" title=\"Quitar columna\"\n\t\tdata-column=\"ox_openxavatest_Invoice__list_col4\" data-tab-object=\"xava_tab\">\t\n\t\t<i class=\"mdi mdi-close-circle\"><\/i>\n\t<\/a>\n\t<\/span>\n\n<\/div> \n\n<\/th>\n\n<th class=\"ox-list-header ox-padding-right-0 ox-vertical-align-middle \" data-property=\"detailsCount\">\n \n<div id=\"ox_openxavatest_Invoice__list_col5\" class=\"\" >\n\n<span class=\"ox_openxavatest_Invoice__customize_list ox-display-none\">\n<i class=\"xava_handle mdi mdi-cursor-move\"><\/i>\n<\/span>\n\n\nCantidad l\u00EDneas&nbsp;\n\n\t<span class=\"ox_openxavatest_Invoice__customize_list ox-display-none ox-column-customize-controls-on-right\">\n\t<input name='ox_openxavatest_Invoice__action___List___changeColumnName' type='hidden'\/>\n<a  title='Cambiar nombre de columna' class='xava_action ox-image-link' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.changeColumnName' data-argv='property=detailsCount' data-in-new-window='false'><i class='mdi mdi-rename-box'><\/i><\/a>\n\t<a class=\"xava_remove_column\" title=\"Quitar columna\"\n\t\tdata-column=\"ox_openxavatest_Invoice__list_col5\" data-tab-object=\"xava_tab\">\t\n\t\t<i class=\"mdi mdi-close-circle\"><\/i>\n\t<\/a>\n\t<\/span>\n\n<\/div> \n\n<\/th>\n\n<th class=\"ox-list-header ox-padding-right-0 ox-vertical-align-middle \" data-property=\"paid\">\n \n<div id=\"ox_openxavatest_Invoice__list_col6\" class=\"\" >\n\n<span class=\"ox_openxavatest_Invoice__customize_list ox-display-none\">\n<i class=\"xava_handle mdi mdi-cursor-move\"><\/i>\n<\/span>\n\n\n<span class=\"\">\n\n<input name='ox_openxavatest_Invoice__action___List___orderBy' type='hidden'\/><a  title='Ordenar por - Ordenar por esta columna' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.orderBy' data-argv='property=paid' data-in-new-window='false'> <nobr>Pagada<i class='mdi mdi-sort-alphabetical'><\/i><\/nobr><\/a>&nbsp;\n<\/span>\n\n\t<span class=\"ox_openxavatest_Invoice__customize_list ox-display-none ox-column-customize-controls-on-right\">\n\t<input name='ox_openxavatest_Invoice__action___List___changeColumnName' type='hidden'\/>\n<a  title='Cambiar nombre de columna' class='xava_action ox-image-link' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.changeColumnName' data-argv='property=paid' data-in-new-window='false'><i class='mdi mdi-rename-box'><\/i><\/a>\n\t<a class=\"xava_remove_column\" title=\"Quitar columna\"\n\t\tdata-column=\"ox_openxavatest_Invoice__list_col6\" data-tab-object=\"xava_tab\">\t\n\t\t<i class=\"mdi mdi-close-circle\"><\/i>\n\t<\/a>\n\t<\/span>\n\n<\/div> \n\n<\/th>\n\n<th class=\"ox-list-header ox-padding-right-0 ox-vertical-align-middle \" data-property=\"importance\">\n \n<div id=\"ox_openxavatest_Invoice__list_col7\" class=\"\" >\n\n<span class=\"ox_openxavatest_Invoice__customize_list ox-display-none\">\n<i class=\"xava_handle mdi mdi-cursor-move\"><\/i>\n<\/span>\n\n\nImportancia&nbsp;\n\n\t<span class=\"ox_openxavatest_Invoice__customize_list ox-display-none ox-column-customize-controls-on-right\">\n\t<input name='ox_openxavatest_Invoice__action___List___changeColumnName' type='hidden'\/>\n<a  title='Cambiar nombre de columna' class='xava_action ox-image-link' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.changeColumnName' data-argv='property=importance' data-in-new-window='false'><i class='mdi mdi-rename-box'><\/i><\/a>\n\t<a class=\"xava_remove_column\" title=\"Quitar columna\"\n\t\tdata-column=\"ox_openxavatest_Invoice__list_col7\" data-tab-object=\"xava_tab\">\t\n\t\t<i class=\"mdi mdi-close-circle\"><\/i>\n\t<\/a>\n\t<\/span>\n\n<\/div> \n\n<\/th>\n\n<\/tr>\n\n<tr id=\"ox_openxavatest_Invoice__list_filter_list\" class=\"xava_filter ox-list-subheader \">\n<td class=\"ox-filter-cell ox-list-subheader\"> \n<input name='ox_openxavatest_Invoice__action___List___filter' type='hidden'\/>\n<a id='ox_openxavatest_Invoice__List___filter'\n title='Filtrar' class='xava_action ox-image-link' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.filter' data-in-new-window='false'><i class='mdi mdi-play'><\/i><\/a>\n<\/td> \n<td class=\"ox-list-subheader\" width=\"5\"> \n\t<a title='Borra valores del filtro' href=\"javascript:void(0)\">\n\t\t<i class=\"xava_clear_condition mdi mdi-eraser\" \n\t\t\tid=\"ox_openxavatest_Invoice__xava_clear_condition\" \n\t\t\tdata-prefix=\"\"><\/i>\t\t\n\t<\/a>\n<\/td> \n\n<td class=\"ox-list-subheader\" align=\"left\">\n<div class=\"ox_openxavatest_Invoice__list_col0 ox-list-subheader-no-resize\" >\n\n\n<span class=\"xava_comparator ox-display-none\"> \n\n\n\n\n\n\n\n\n\n \n\n\n\n\n\n<select id=\"ox_openxavatest_Invoice__conditionComparator___0\" name=\"ox_openxavatest_Invoice__conditionComparator___0\" class=editor\n\tdata-from=\"Desde\" \n\tdata-in-values=\"valor 1, valor 2, ...\"\n\tdata-collection-argv=\"\">\n\t\n\t\n\t<option value=\"eq_comparator\" >=<\/option>\n\t<option value=\"ne_comparator\" ><><\/option>\n\t<option value=\"ge_comparator\" >>=<\/option>\n\t<option value=\"le_comparator\" ><=<\/option>\t\n\t<option value=\"gt_comparator\" >><\/option>\n\t<option value=\"lt_comparator\" ><<\/option>\n\t\n\t\n\t<option value=\"in_comparator\" >en grupo<\/option>\n\t<option value=\"not_in_comparator\" >no en grupo<\/option>\n\t\t\n\t<option value=\"range_comparator\" >rango<\/option>\n\t\n\t\n<\/select>\t\n\t\n<br\/> \n<\/span>\n \n<nobr  >\n<input id=\"ox_openxavatest_Invoice__conditionValue___0\" name=\"ox_openxavatest_Invoice__conditionValue___0\" class=\"editor  ox-display-inline\" type=\"text\"\n\tmaxlength=\"100\" size=\"4\" value=\"\" placeholder=\"\"\n\t\/>\n\t\n<\/nobr>\n<br\/>\n \n<nobr  >\n<input id=\"ox_openxavatest_Invoice__conditionValueTo___0\" name=\"ox_openxavatest_Invoice__conditionValueTo___0\" class=\"editor ox-display-none\" type=\"text\" \n\tmaxlength=\"100\" size=\"4\" value=\"\" placeholder=\"Hasta\"\n\t\/>\n\t\n<\/nobr>\n\t\n<\/div>\n<\/td>\n\n<td class=\"ox-list-subheader\" align=\"left\">\n<div class=\"ox_openxavatest_Invoice__list_col1 ox-list-subheader-no-resize\" >\n\n\n<span class=\"xava_comparator ox-display-none\"> \n\n\n\n\n\n\n\n\n\n \n\n\n\n\n\n<select id=\"ox_openxavatest_Invoice__conditionComparator___1\" name=\"ox_openxavatest_Invoice__conditionComparator___1\" class=editor\n\tdata-from=\"Desde\" \n\tdata-in-values=\"valor 1, valor 2, ...\"\n\tdata-collection-argv=\"\">\n\t\n\t\n\t<option value=\"eq_comparator\" >=<\/option>\n\t<option value=\"ne_comparator\" ><><\/option>\n\t<option value=\"ge_comparator\" >>=<\/option>\n\t<option value=\"le_comparator\" ><=<\/option>\t\n\t<option value=\"gt_comparator\" >><\/option>\n\t<option value=\"lt_comparator\" ><<\/option>\n\t\n\t\n\t<option value=\"in_comparator\" >en grupo<\/option>\n\t<option value=\"not_in_comparator\" >no en grupo<\/option>\n\t\t\n\t<option value=\"range_comparator\" >rango<\/option>\n\t\n\t\n<\/select>\t\n\t\n<br\/> \n<\/span>\n \n<nobr  >\n<input id=\"ox_openxavatest_Invoice__conditionValue___1\" name=\"ox_openxavatest_Invoice__conditionValue___1\" class=\"editor  ox-display-inline\" type=\"text\"\n\tmaxlength=\"100\" size=\"6\" value=\"\" placeholder=\"\"\n\t\/>\n\t\n<\/nobr>\n<br\/>\n \n<nobr  >\n<input id=\"ox_openxavatest_Invoice__conditionValueTo___1\" name=\"ox_openxavatest_Invoice__conditionValueTo___1\" class=\"editor ox-display-none\" type=\"text\" \n\tmaxlength=\"100\" size=\"6\" value=\"\" placeholder=\"Hasta\"\n\t\/>\n\t\n<\/nobr>\n\t\n<\/div>\n<\/td>\n\n<td class=\"ox-list-subheader\" align=\"left\">\n<div class=\"ox_openxavatest_Invoice__list_col2 ox-list-subheader-no-resize\" >\n\n\n<span class=\"xava_comparator ox-display-none\"> \n\n\n\n\n\n\n\n\n\n \n\n\n\n\n\n<select id=\"ox_openxavatest_Invoice__conditionComparator___2\" name=\"ox_openxavatest_Invoice__conditionComparator___2\" class=editor\n\tdata-from=\"Desde\" \n\tdata-in-values=\"valor 1, valor 2, ...\"\n\tdata-collection-argv=\"\">\n\t\n\t\n\t<option value=\"eq_comparator\" >=<\/option>\n\t<option value=\"ne_comparator\" ><><\/option>\n\t<option value=\"ge_comparator\" >>=<\/option>\n\t<option value=\"le_comparator\" ><=<\/option>\t\n\t<option value=\"gt_comparator\" >><\/option>\n\t<option value=\"lt_comparator\" ><<\/option>\n\t\n\t\n\t<option value=\"empty_comparator\" >est\u00E1 vac\u00EDo<\/option>\n\t<option value=\"not_empty_comparator\" >no est\u00E1 vac\u00EDo<\/option>\n\t<option value=\"year_comparator\" >a\u00F1o =<\/option>\n\t<option value=\"month_comparator\" >mes =<\/option>\n\t<option value=\"year_month_comparator\" >a\u00F1o\/mes =<\/option>\n\t\n\t<option value=\"in_comparator\" >en grupo<\/option>\n\t<option value=\"not_in_comparator\" >no en grupo<\/option>\n\t\t\n\t<option value=\"range_comparator\" >rango<\/option>\n\t\n\t\n<\/select>\t\n\t\n<br\/> \n<\/span>\n \n<nobr class='xava_date ox-date-calendar' data-date-format='d\/m\/Y'>\n<input id=\"ox_openxavatest_Invoice__conditionValue___2\" name=\"ox_openxavatest_Invoice__conditionValue___2\" class=\"editor  ox-display-inline\" type=\"text\"\n\tmaxlength=\"100\" size=\"10\" value=\"\" placeholder=\"\"\n\tdata-input\/>\n\t\n\t\t<a href=\"javascript:void(0)\" data-toggle class=\"\" tabindex=\"999\"><i class=\"mdi mdi-calendar\"><\/i><\/a>\n\t\n<\/nobr>\n<br\/>\n \n<nobr class='xava_date ox-date-calendar' data-date-format='d\/m\/Y'>\n<input id=\"ox_openxavatest_Invoice__conditionValueTo___2\" name=\"ox_openxavatest_Invoice__conditionValueTo___2\" class=\"editor ox-display-none\" type=\"text\" \n\tmaxlength=\"100\" size=\"10\" value=\"\" placeholder=\"Hasta\"\n\tdata-input\/>\n\t\n\t\t<a href=\"javascript:void(0)\" data-toggle class=\"ox-display-none\" tabindex=\"999\"><i class=\"mdi mdi-calendar\"><\/i><\/a>\n\t\n<\/nobr>\n\t\n<\/div>\n<\/td>\n\n<td class=\"ox-list-subheader\">\n\t<div class=\"ox_openxavatest_Invoice__list_col3\"\/>\n<\/td>\n\n<td class=\"ox-list-subheader\">\n\t<div class=\"ox_openxavatest_Invoice__list_col4\"\/>\n<\/td>\n\n<td class=\"ox-list-subheader\">\n\t<div class=\"ox_openxavatest_Invoice__list_col5\"\/>\n<\/td>\n\n<td class=\"ox-list-subheader\" align=\"left\">\n<div class=\"ox_openxavatest_Invoice__list_col6 ox-list-subheader-no-resize\" >\n\n\n\n\n\n\n\n\n\n\n\n\n<div>\n\t\n\t<input id=\"ox_openxavatest_Invoice__conditionValue___3\" type=\"hidden\" name=\"ox_openxavatest_Invoice__conditionValue___3\" value=\"true\">\n\t<input id=\"ox_openxavatest_Invoice__conditionValueTo___3\" type=\"hidden\" name=\"ox_openxavatest_Invoice__conditionValueTo___3\" >\n\t\n<\/div>\n\n<select name=\"ox_openxavatest_Invoice__conditionComparator___3\" class=\"xava_combo_condition_value editor\"\n\tdata-collection-argv=\"\">\n\t<option value=\"\"><\/option>\n\t<option value=\"eq_comparator\" >S\u00ED<\/option>\n\t<option value=\"ne_comparator\" >No<\/option>\n<\/select>\t\n\t\n\n<\/div>\n<\/td>\n\n<td class=\"ox-list-subheader\">\n\t<div class=\"ox_openxavatest_Invoice__list_col7\"\/>\n<\/td>\n\n<\/tr>\n\n\n<tr id=\"ox_openxavatest_Invoice__0\" class=\"ox-list-pair\" >\n\t<td class=\"ox-list-pair ox-list-action-cell\">\n\t<nobr> \n\t\t\n\n<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/>\n<a  title='Editar - Editar registro' class='xava_action ox-image-link' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=0' data-in-new-window='false'><i class='mdi mdi-border-color'><\/i><\/a>\n\n\t\t\t<input name='ox_openxavatest_Invoice__action___CRUD___deleteRow' type='hidden'\/>\n<a  title='Borrar registro' class='xava_action ox-image-link' data-application='openxavatest' data-module='Invoice' data-confirm-message='Borrar registro de la fila 1: \u00BFEst\u00E1s seguro?' data-takes-long='false' data-action='CRUD.deleteRow' data-argv='row=0' data-in-new-window='false'><i class='mdi mdi-delete'><\/i><\/a>\n\n\t<\/nobr> \n\t<\/td>\n\t<td class=\"ox-list-pair\">\n\t<input class=\"xava_selected\" type=\"checkbox\" name=\"ox_openxavatest_Invoice__xava_selected\" \n\t\tvalue=\"selected:0\" \n\t\tdata-on-select-collection-element-action=\"\"\n\t\tdata-row-id=\"ox_openxavatest_Invoice__0\"\n\t\tdata-row=\"0\"\n\t\tdata-view-object=\"xava_view\"\n\t\tdata-tab-object=\"xava_tab\"\n\t\tdata-confirm-message=\"\"\n\t\tdata-takes-long=\"false\"\/>\n\t<\/td>\t\n\n\t<td class=\"ox-list-pair ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=0' data-in-new-window='false'>\n\t\t\t<div title=\"2002\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col0 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t2002\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-pair ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=0' data-in-new-window='false'>\n\t\t\t<div title=\"1\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col1 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t1\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-pair  ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=0' data-in-new-window='false'>\n\t\t\t<div title=\"01\/01\/2002\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col2 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t01\/01\/2002\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-pair ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=0' data-in-new-window='false'>\n\t\t\t<div title=\"2.500,00\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col3 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t2.500,00\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-pair ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=0' data-in-new-window='false'>\n\t\t\t<div title=\"400,00\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col4 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t400,00\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-pair ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=0' data-in-new-window='false'>\n\t\t\t<div title=\"2\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col5 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t2\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-pair  ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=0' data-in-new-window='false'>\n\t\t\t<div title=\"\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col6 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-pair  ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=0' data-in-new-window='false'>\n\t\t\t<div title=\"NORMAL\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col7 ox-width-100\" >\n\t\t\t\t\n\t\t\t\tNORMAL\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n<\/tr>\n\n<tr id=\"ox_openxavatest_Invoice__1\" class=\"ox-list-odd\" >\n\t<td class=\"ox-list-odd ox-list-action-cell\">\n\t<nobr> \n\t\t\n\n<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/>\n<a  title='Editar - Editar registro' class='xava_action ox-image-link' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=1' data-in-new-window='false'><i class='mdi mdi-border-color'><\/i><\/a>\n\n\t\t\t<input name='ox_openxavatest_Invoice__action___CRUD___deleteRow' type='hidden'\/>\n<a  title='Borrar registro' class='xava_action ox-image-link' data-application='openxavatest' data-module='Invoice' data-confirm-message='Borrar registro de la fila 2: \u00BFEst\u00E1s seguro?' data-takes-long='false' data-action='CRUD.deleteRow' data-argv='row=1' data-in-new-window='false'><i class='mdi mdi-delete'><\/i><\/a>\n\n\t<\/nobr> \n\t<\/td>\n\t<td class=\"ox-list-odd\">\n\t<input class=\"xava_selected\" type=\"checkbox\" name=\"ox_openxavatest_Invoice__xava_selected\" \n\t\tvalue=\"selected:1\" \n\t\tdata-on-select-collection-element-action=\"\"\n\t\tdata-row-id=\"ox_openxavatest_Invoice__1\"\n\t\tdata-row=\"1\"\n\t\tdata-view-object=\"xava_view\"\n\t\tdata-tab-object=\"xava_tab\"\n\t\tdata-confirm-message=\"\"\n\t\tdata-takes-long=\"false\"\/>\n\t<\/td>\t\n\n\t<td class=\"ox-list-odd ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=1' data-in-new-window='false'>\n\t\t\t<div title=\"2004\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col0 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t2004\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-odd ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=1' data-in-new-window='false'>\n\t\t\t<div title=\"2\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col1 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t2\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-odd  ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=1' data-in-new-window='false'>\n\t\t\t<div title=\"04\/01\/2004\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col2 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t04\/01\/2004\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-odd ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=1' data-in-new-window='false'>\n\t\t\t<div title=\"11,00\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col3 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t11,00\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-odd ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=1' data-in-new-window='false'>\n\t\t\t<div title=\"4,62\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col4 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t4,62\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-odd ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=1' data-in-new-window='false'>\n\t\t\t<div title=\"1\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col5 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t1\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-odd  ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=1' data-in-new-window='false'>\n\t\t\t<div title=\"Pagada\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col6 ox-width-100\" >\n\t\t\t\t\n\t\t\t\tPagada\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-odd  ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=1' data-in-new-window='false'>\n\t\t\t<div title=\"TRIVIAL\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col7 ox-width-100\" >\n\t\t\t\t\n\t\t\t\tTRIVIAL\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n<\/tr>\n\n<tr id=\"ox_openxavatest_Invoice__2\" class=\"ox-list-pair\" >\n\t<td class=\"ox-list-pair ox-list-action-cell\">\n\t<nobr> \n\t\t\n\n<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/>\n<a  title='Editar - Editar registro' class='xava_action ox-image-link' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=2' data-in-new-window='false'><i class='mdi mdi-border-color'><\/i><\/a>\n\n\t\t\t<input name='ox_openxavatest_Invoice__action___CRUD___deleteRow' type='hidden'\/>\n<a  title='Borrar registro' class='xava_action ox-image-link' data-application='openxavatest' data-module='Invoice' data-confirm-message='Borrar registro de la fila 3: \u00BFEst\u00E1s seguro?' data-takes-long='false' data-action='CRUD.deleteRow' data-argv='row=2' data-in-new-window='false'><i class='mdi mdi-delete'><\/i><\/a>\n\n\t<\/nobr> \n\t<\/td>\n\t<td class=\"ox-list-pair\">\n\t<input class=\"xava_selected\" type=\"checkbox\" name=\"ox_openxavatest_Invoice__xava_selected\" \n\t\tvalue=\"selected:2\" \n\t\tdata-on-select-collection-element-action=\"\"\n\t\tdata-row-id=\"ox_openxavatest_Invoice__2\"\n\t\tdata-row=\"2\"\n\t\tdata-view-object=\"xava_view\"\n\t\tdata-tab-object=\"xava_tab\"\n\t\tdata-confirm-message=\"\"\n\t\tdata-takes-long=\"false\"\/>\n\t<\/td>\t\n\n\t<td class=\"ox-list-pair ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=2' data-in-new-window='false'>\n\t\t\t<div title=\"2004\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col0 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t2004\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-pair ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=2' data-in-new-window='false'>\n\t\t\t<div title=\"9\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col1 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t9\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-pair  ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=2' data-in-new-window='false'>\n\t\t\t<div title=\"04\/01\/2004\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col2 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t04\/01\/2004\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-pair ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=2' data-in-new-window='false'>\n\t\t\t<div title=\"4.396,00\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col3 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t4.396,00\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-pair ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=2' data-in-new-window='false'>\n\t\t\t<div title=\"3.121,16\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col4 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t3.121,16\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-pair ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=2' data-in-new-window='false'>\n\t\t\t<div title=\"2\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col5 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t2\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-pair  ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=2' data-in-new-window='false'>\n\t\t\t<div title=\"\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col6 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-pair  ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=2' data-in-new-window='false'>\n\t\t\t<div title=\"SUCCULENT\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col7 ox-width-100\" >\n\t\t\t\t\n\t\t\t\tSUCCULENT\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n<\/tr>\n\n<tr id=\"ox_openxavatest_Invoice__3\" class=\"ox-list-odd\" >\n\t<td class=\"ox-list-odd ox-list-action-cell\">\n\t<nobr> \n\t\t\n\n<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/>\n<a  title='Editar - Editar registro' class='xava_action ox-image-link' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=3' data-in-new-window='false'><i class='mdi mdi-border-color'><\/i><\/a>\n\n\t\t\t<input name='ox_openxavatest_Invoice__action___CRUD___deleteRow' type='hidden'\/>\n<a  title='Borrar registro' class='xava_action ox-image-link' data-application='openxavatest' data-module='Invoice' data-confirm-message='Borrar registro de la fila 4: \u00BFEst\u00E1s seguro?' data-takes-long='false' data-action='CRUD.deleteRow' data-argv='row=3' data-in-new-window='false'><i class='mdi mdi-delete'><\/i><\/a>\n\n\t<\/nobr> \n\t<\/td>\n\t<td class=\"ox-list-odd\">\n\t<input class=\"xava_selected\" type=\"checkbox\" name=\"ox_openxavatest_Invoice__xava_selected\" \n\t\tvalue=\"selected:3\" \n\t\tdata-on-select-collection-element-action=\"\"\n\t\tdata-row-id=\"ox_openxavatest_Invoice__3\"\n\t\tdata-row=\"3\"\n\t\tdata-view-object=\"xava_view\"\n\t\tdata-tab-object=\"xava_tab\"\n\t\tdata-confirm-message=\"\"\n\t\tdata-takes-long=\"false\"\/>\n\t<\/td>\t\n\n\t<td class=\"ox-list-odd ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=3' data-in-new-window='false'>\n\t\t\t<div title=\"2004\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col0 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t2004\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-odd ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=3' data-in-new-window='false'>\n\t\t\t<div title=\"10\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col1 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t10\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-odd  ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=3' data-in-new-window='false'>\n\t\t\t<div title=\"04\/12\/2004\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col2 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t04\/12\/2004\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-odd ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=3' data-in-new-window='false'>\n\t\t\t<div title=\"1.189,00\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col3 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t1.189,00\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-odd ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=3' data-in-new-window='false'>\n\t\t\t<div title=\"214,02\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col4 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t214,02\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-odd ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=3' data-in-new-window='false'>\n\t\t\t<div title=\"3\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col5 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t3\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-odd  ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=3' data-in-new-window='false'>\n\t\t\t<div title=\"\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col6 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-odd  ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=3' data-in-new-window='false'>\n\t\t\t<div title=\"NORMAL\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col7 ox-width-100\" >\n\t\t\t\t\n\t\t\t\tNORMAL\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n<\/tr>\n\n<tr id=\"ox_openxavatest_Invoice__4\" class=\"ox-list-pair\" >\n\t<td class=\"ox-list-pair ox-list-action-cell\">\n\t<nobr> \n\t\t\n\n<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/>\n<a  title='Editar - Editar registro' class='xava_action ox-image-link' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=4' data-in-new-window='false'><i class='mdi mdi-border-color'><\/i><\/a>\n\n\t\t\t<input name='ox_openxavatest_Invoice__action___CRUD___deleteRow' type='hidden'\/>\n<a  title='Borrar registro' class='xava_action ox-image-link' data-application='openxavatest' data-module='Invoice' data-confirm-message='Borrar registro de la fila 5: \u00BFEst\u00E1s seguro?' data-takes-long='false' data-action='CRUD.deleteRow' data-argv='row=4' data-in-new-window='false'><i class='mdi mdi-delete'><\/i><\/a>\n\n\t<\/nobr> \n\t<\/td>\n\t<td class=\"ox-list-pair\">\n\t<input class=\"xava_selected\" type=\"checkbox\" name=\"ox_openxavatest_Invoice__xava_selected\" \n\t\tvalue=\"selected:4\" \n\t\tdata-on-select-collection-element-action=\"\"\n\t\tdata-row-id=\"ox_openxavatest_Invoice__4\"\n\t\tdata-row=\"4\"\n\t\tdata-view-object=\"xava_view\"\n\t\tdata-tab-object=\"xava_tab\"\n\t\tdata-confirm-message=\"\"\n\t\tdata-takes-long=\"false\"\/>\n\t<\/td>\t\n\n\t<td class=\"ox-list-pair ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=4' data-in-new-window='false'>\n\t\t\t<div title=\"2004\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col0 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t2004\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-pair ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=4' data-in-new-window='false'>\n\t\t\t<div title=\"11\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col1 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t11\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-pair  ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=4' data-in-new-window='false'>\n\t\t\t<div title=\"04\/11\/2006\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col2 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t04\/11\/2006\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-pair ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=4' data-in-new-window='false'>\n\t\t\t<div title=\"0,00\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col3 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t0,00\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-pair ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=4' data-in-new-window='false'>\n\t\t\t<div title=\"0,00\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col4 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t0,00\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-pair ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=4' data-in-new-window='false'>\n\t\t\t<div title=\"0\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col5 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t0\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-pair  ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=4' data-in-new-window='false'>\n\t\t\t<div title=\"\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col6 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-pair  ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=4' data-in-new-window='false'>\n\t\t\t<div title=\"TRIVIAL\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col7 ox-width-100\" >\n\t\t\t\t\n\t\t\t\tTRIVIAL\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n<\/tr>\n\n<tr id=\"ox_openxavatest_Invoice__5\" class=\"ox-list-odd\" >\n\t<td class=\"ox-list-odd ox-list-action-cell\">\n\t<nobr> \n\t\t\n\n<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/>\n<a  title='Editar - Editar registro' class='xava_action ox-image-link' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=5' data-in-new-window='false'><i class='mdi mdi-border-color'><\/i><\/a>\n\n\t\t\t<input name='ox_openxavatest_Invoice__action___CRUD___deleteRow' type='hidden'\/>\n<a  title='Borrar registro' class='xava_action ox-image-link' data-application='openxavatest' data-module='Invoice' data-confirm-message='Borrar registro de la fila 6: \u00BFEst\u00E1s seguro?' data-takes-long='false' data-action='CRUD.deleteRow' data-argv='row=5' data-in-new-window='false'><i class='mdi mdi-delete'><\/i><\/a>\n\n\t<\/nobr> \n\t<\/td>\n\t<td class=\"ox-list-odd\">\n\t<input class=\"xava_selected\" type=\"checkbox\" name=\"ox_openxavatest_Invoice__xava_selected\" \n\t\tvalue=\"selected:5\" \n\t\tdata-on-select-collection-element-action=\"\"\n\t\tdata-row-id=\"ox_openxavatest_Invoice__5\"\n\t\tdata-row=\"5\"\n\t\tdata-view-object=\"xava_view\"\n\t\tdata-tab-object=\"xava_tab\"\n\t\tdata-confirm-message=\"\"\n\t\tdata-takes-long=\"false\"\/>\n\t<\/td>\t\n\n\t<td class=\"ox-list-odd ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=5' data-in-new-window='false'>\n\t\t\t<div title=\"2004\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col0 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t2004\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-odd ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=5' data-in-new-window='false'>\n\t\t\t<div title=\"12\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col1 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t12\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-odd  ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=5' data-in-new-window='false'>\n\t\t\t<div title=\"05\/11\/2006\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col2 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t05\/11\/2006\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-odd ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=5' data-in-new-window='false'>\n\t\t\t<div title=\"110,00\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col3 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t110,00\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-odd ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=5' data-in-new-window='false'>\n\t\t\t<div title=\"14,30\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col4 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t14,30\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-odd ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=5' data-in-new-window='false'>\n\t\t\t<div title=\"2\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col5 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t2\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-odd  ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=5' data-in-new-window='false'>\n\t\t\t<div title=\"\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col6 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-odd  ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=5' data-in-new-window='false'>\n\t\t\t<div title=\"NORMAL\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col7 ox-width-100\" >\n\t\t\t\t\n\t\t\t\tNORMAL\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n<\/tr>\n\n<tr id=\"ox_openxavatest_Invoice__6\" class=\"ox-list-pair\" >\n\t<td class=\"ox-list-pair ox-list-action-cell\">\n\t<nobr> \n\t\t\n\n<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/>\n<a  title='Editar - Editar registro' class='xava_action ox-image-link' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=6' data-in-new-window='false'><i class='mdi mdi-border-color'><\/i><\/a>\n\n\t\t\t<input name='ox_openxavatest_Invoice__action___CRUD___deleteRow' type='hidden'\/>\n<a  title='Borrar registro' class='xava_action ox-image-link' data-application='openxavatest' data-module='Invoice' data-confirm-message='Borrar registro de la fila 7: \u00BFEst\u00E1s seguro?' data-takes-long='false' data-action='CRUD.deleteRow' data-argv='row=6' data-in-new-window='false'><i class='mdi mdi-delete'><\/i><\/a>\n\n\t<\/nobr> \n\t<\/td>\n\t<td class=\"ox-list-pair\">\n\t<input class=\"xava_selected\" type=\"checkbox\" name=\"ox_openxavatest_Invoice__xava_selected\" \n\t\tvalue=\"selected:6\" \n\t\tdata-on-select-collection-element-action=\"\"\n\t\tdata-row-id=\"ox_openxavatest_Invoice__6\"\n\t\tdata-row=\"6\"\n\t\tdata-view-object=\"xava_view\"\n\t\tdata-tab-object=\"xava_tab\"\n\t\tdata-confirm-message=\"\"\n\t\tdata-takes-long=\"false\"\/>\n\t<\/td>\t\n\n\t<td class=\"ox-list-pair ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=6' data-in-new-window='false'>\n\t\t\t<div title=\"2007\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col0 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t2007\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-pair ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=6' data-in-new-window='false'>\n\t\t\t<div title=\"14\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col1 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t14\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-pair  ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=6' data-in-new-window='false'>\n\t\t\t<div title=\"28\/05\/2007\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col2 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t28\/05\/2007\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-pair ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=6' data-in-new-window='false'>\n\t\t\t<div title=\"6.059,00\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col3 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t6.059,00\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-pair ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=6' data-in-new-window='false'>\n\t\t\t<div title=\"969,44\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col4 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t969,44\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-pair ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=6' data-in-new-window='false'>\n\t\t\t<div title=\"14\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col5 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t14\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-pair  ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=6' data-in-new-window='false'>\n\t\t\t<div title=\"\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col6 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-pair  ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=6' data-in-new-window='false'>\n\t\t\t<div title=\"SUCCULENT\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col7 ox-width-100\" >\n\t\t\t\t\n\t\t\t\tSUCCULENT\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n<\/tr>\n\n<tr id=\"ox_openxavatest_Invoice__7\" class=\"ox-list-odd\" >\n\t<td class=\"ox-list-odd ox-list-action-cell\">\n\t<nobr> \n\t\t\n\n<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/>\n<a  title='Editar - Editar registro' class='xava_action ox-image-link' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=7' data-in-new-window='false'><i class='mdi mdi-border-color'><\/i><\/a>\n\n\t\t\t<input name='ox_openxavatest_Invoice__action___CRUD___deleteRow' type='hidden'\/>\n<a  title='Borrar registro' class='xava_action ox-image-link' data-application='openxavatest' data-module='Invoice' data-confirm-message='Borrar registro de la fila 8: \u00BFEst\u00E1s seguro?' data-takes-long='false' data-action='CRUD.deleteRow' data-argv='row=7' data-in-new-window='false'><i class='mdi mdi-delete'><\/i><\/a>\n\n\t<\/nobr> \n\t<\/td>\n\t<td class=\"ox-list-odd\">\n\t<input class=\"xava_selected\" type=\"checkbox\" name=\"ox_openxavatest_Invoice__xava_selected\" \n\t\tvalue=\"selected:7\" \n\t\tdata-on-select-collection-element-action=\"\"\n\t\tdata-row-id=\"ox_openxavatest_Invoice__7\"\n\t\tdata-row=\"7\"\n\t\tdata-view-object=\"xava_view\"\n\t\tdata-tab-object=\"xava_tab\"\n\t\tdata-confirm-message=\"\"\n\t\tdata-takes-long=\"false\"\/>\n\t<\/td>\t\n\n\t<td class=\"ox-list-odd ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=7' data-in-new-window='false'>\n\t\t\t<div title=\"2009\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col0 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t2009\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-odd ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=7' data-in-new-window='false'>\n\t\t\t<div title=\"1\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col1 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t1\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-odd  ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=7' data-in-new-window='false'>\n\t\t\t<div title=\"23\/06\/2009\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col2 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t23\/06\/2009\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-odd ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=7' data-in-new-window='false'>\n\t\t\t<div title=\"0,00\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col3 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t0,00\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-odd ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=7' data-in-new-window='false'>\n\t\t\t<div title=\"0,00\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col4 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t0,00\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-odd ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=7' data-in-new-window='false'>\n\t\t\t<div title=\"0\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col5 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t0\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-odd  ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=7' data-in-new-window='false'>\n\t\t\t<div title=\"\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col6 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-odd  ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=7' data-in-new-window='false'>\n\t\t\t<div title=\"TRIVIAL\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col7 ox-width-100\" >\n\t\t\t\t\n\t\t\t\tTRIVIAL\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n<\/tr>\n\n<tr id=\"ox_openxavatest_Invoice__8\" class=\"ox-list-pair\" >\n\t<td class=\"ox-list-pair ox-list-action-cell\">\n\t<nobr> \n\t\t\n\n<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/>\n<a  title='Editar - Editar registro' class='xava_action ox-image-link' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=8' data-in-new-window='false'><i class='mdi mdi-border-color'><\/i><\/a>\n\n\t\t\t<input name='ox_openxavatest_Invoice__action___CRUD___deleteRow' type='hidden'\/>\n<a  title='Borrar registro' class='xava_action ox-image-link' data-application='openxavatest' data-module='Invoice' data-confirm-message='Borrar registro de la fila 9: \u00BFEst\u00E1s seguro?' data-takes-long='false' data-action='CRUD.deleteRow' data-argv='row=8' data-in-new-window='false'><i class='mdi mdi-delete'><\/i><\/a>\n\n\t<\/nobr> \n\t<\/td>\n\t<td class=\"ox-list-pair\">\n\t<input class=\"xava_selected\" type=\"checkbox\" name=\"ox_openxavatest_Invoice__xava_selected\" \n\t\tvalue=\"selected:8\" \n\t\tdata-on-select-collection-element-action=\"\"\n\t\tdata-row-id=\"ox_openxavatest_Invoice__8\"\n\t\tdata-row=\"8\"\n\t\tdata-view-object=\"xava_view\"\n\t\tdata-tab-object=\"xava_tab\"\n\t\tdata-confirm-message=\"\"\n\t\tdata-takes-long=\"false\"\/>\n\t<\/td>\t\n\n\t<td class=\"ox-list-pair ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=8' data-in-new-window='false'>\n\t\t\t<div title=\"2011\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col0 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t2011\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-pair ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=8' data-in-new-window='false'>\n\t\t\t<div title=\"1\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col1 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t1\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-pair  ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=8' data-in-new-window='false'>\n\t\t\t<div title=\"14\/03\/2011\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col2 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t14\/03\/2011\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-pair ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=8' data-in-new-window='false'>\n\t\t\t<div title=\"18.207,00\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col3 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t18.207,00\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-pair ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=8' data-in-new-window='false'>\n\t\t\t<div title=\"3.277,26\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col4 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t3.277,26\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-pair ox-text-align-right ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=8' data-in-new-window='false'>\n\t\t\t<div title=\"59\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col5 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t59\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-pair  ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=8' data-in-new-window='false'>\n\t\t\t<div title=\"\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col6 ox-width-100\" >\n\t\t\t\t\n\t\t\t\t\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n\t<td class=\"ox-list-pair  ox-list-data-cell\">\n\t\t<input name='ox_openxavatest_Invoice__action___List___viewDetail' type='hidden'\/><a  title='Editar - Editar registro' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.viewDetail' data-argv='row=8' data-in-new-window='false'>\n\t\t\t<div title=\"SUCCULENT\" class=\"ox_openxavatest_Invoice__tipable ox_openxavatest_Invoice__list_col7 ox-width-100\" >\n\t\t\t\t\n\t\t\t\tSUCCULENT\n\t\t\t\t\n\t\t\t<\/div>\n\t\t<\/a>\n\t<\/td>\n\n<\/tr>\n\n<tr class=\"ox-total-row\">\n<td\/>\n<td\/>\n\n\t<td class=\"ox-total-capable-cell\">\n\t\t<div class=\"ox_openxavatest_Invoice__list_col0\" >\n\t\t\t<input name='ox_openxavatest_Invoice__action___List___sumColumn' type='hidden'\/>\n<a  title='Suma' class='xava_action ox-image-link' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.sumColumn' data-argv='property=year' data-in-new-window='false'><i class='mdi mdi-sigma'><\/i><\/a>&nbsp;\n\t\t<\/div>\t\n\t<\/td>\n\t\n\t<td class=\"ox-total-capable-cell\">\n\t\t<div class=\"ox_openxavatest_Invoice__list_col1\" >\n\t\t\t<input name='ox_openxavatest_Invoice__action___List___sumColumn' type='hidden'\/>\n<a  title='Suma' class='xava_action ox-image-link' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.sumColumn' data-argv='property=number' data-in-new-window='false'><i class='mdi mdi-sigma'><\/i><\/a>&nbsp;\n\t\t<\/div>\t\n\t<\/td>\n\t\t \n\t<td\/>\n\t\t \n\t<td\/>\n\t\t \n\t<td\/>\n\t\t \n\t<td\/>\n\t\t \n\t<td\/>\n\t\t \n\t<td\/>\n\t\n<\/tr>\n\n\n\n \n<\/table>\n<\/div> \n\n\n<table width=\"100%\" class=\"ox-list-info\" cellspacing=0 cellpadding=0>\n<tr class='ox-list-info-detail'>\n<td class='ox-list-info-detail'>\n\n<span class='ox-first'><span class='ox-page-navigation-arrow-disable'>\n<i class=\"mdi mdi-menu-left\"><\/i>\n<\/span><\/span>\n\n<span class=\"ox-page-navigation-pages\">\n\n<span class=\"ox-page-navigation-selected\">1<\/span>\n\t\n\n<\/span>\n\n<span class='ox-last'>\n<span class='ox-page-navigation-arrow-disable'>\n<i class=\"mdi mdi-menu-right\"><\/i>\n<\/span>\n<\/span>\n\n\n&nbsp;\n<select id=\"ox_openxavatest_Invoice__list_rowCount\" class=\"editor xava_set_page_row_count\"\n\tdata-collection=''>\n\t\t\n\t<option value=\"5\" >5<\/option>\n\t\t\n\t<option value=\"10\" selected='selected'>10<\/option>\n\t\t\n\t<option value=\"12\" >12<\/option>\n\t\t\n\t<option value=\"15\" >15<\/option>\n\t\t\n\t<option value=\"20\" >20<\/option>\n\t\t\n\t<option value=\"50\" >50<\/option>\n\t\n<\/select>\n<span class=\"rows-per-page\">\t \nfilas por p\u00E1gina\n<\/span>\n\n<\/td>\n<td class='ox-list-info-detail'>\n \nHay 9 objetos en la lista\n\n \n(<input name='ox_openxavatest_Invoice__action___List___hideRows' type='hidden'\/><a id='ox_openxavatest_Invoice__List___hideRows' title='Ocultarlos' class='xava_action ' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='List.hideRows' data-in-new-window='false'>Ocultarlos<\/a>)\n\n<\/td>\n<\/tr>\n<\/table>\n\n\n\t\t\n\t\t<\/div>    \t\n\t\t\n\t\t\n\t\n\t<\/div>\n\n\t \n\t<div class=\"ox-core-bottom-buttons-separator\"><\/div>\n\t \n\t\n\n    <div id='ox_openxavatest_Invoice__bottom_buttons' class=\"ox-bottom-buttons\">\n\t\t\n\n\n\n\n\n\n\n\n\n\t\n\t\n\t\t<input name='ox_openxavatest_Invoice__action___Invoice___removeViewDeliveryInInvoice' type='hidden'\/>\n<input type='button' id='ox_openxavatest_Invoice__Invoice___removeViewDeliveryInInvoice' tabindex='1' title='Quitar ver albaranes' class='xava_action ' value='Quitar ver albaranes' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='Invoice.removeViewDeliveryInInvoice' data-in-new-window='false'\/>\n\n\t\t\n\t\t<input name='ox_openxavatest_Invoice__action___Invoice___addViewDeliveryInInvoice' type='hidden'\/>\n<input type='button' id='ox_openxavatest_Invoice__Invoice___addViewDeliveryInInvoice' tabindex='1' title='Poner ver albaranes' class='xava_action ' value='Poner ver albaranes' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='Invoice.addViewDeliveryInInvoice' data-in-new-window='false'\/>\n\n\t\t\n\t\t<input name='ox_openxavatest_Invoice__action___Invoice___changeTab' type='hidden'\/>\n<input type='button' id='ox_openxavatest_Invoice__Invoice___changeTab' tabindex='1' title='Cambiar tab' class='xava_action ' value='Cambiar tab' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='Invoice.changeTab' data-in-new-window='false'\/>\n\n\t\t\n\t\t<input name='ox_openxavatest_Invoice__action___Invoice___testChartTab' type='hidden'\/>\n<input type='button' id='ox_openxavatest_Invoice__Invoice___testChartTab' tabindex='1' title='Test chart tab' class='xava_action ' value='Test chart tab' data-application='openxavatest' data-module='Invoice' data-confirm-message='' data-takes-long='false' data-action='Invoice.testChartTab' data-in-new-window='false'\/>\n\n\t\t\n\t\n\t\n\t<button class=\"xava_action\" name=\"xava.DEFAULT_ACTION\" type=\"submit\"\n\t\tdata-application='openxavatest' \n\t\tdata-module='Invoice'\n\t\tdata-confirm-message=\"\" \n\t\tdata-takes-long=\"false\"  \n\t\tdata-action=\"List.filter\"\n\t\tdata-in-new-window=\"false\" \n\t><\/button>\n\t\t\n\n\n\t<\/div>\n    \n<\/div>\n \n<\/form>\n"},currentRow:-1,dataChanged:false,dialogLevel:0,dialogTitle:null,editorsWithError:null,editorsWithoutError:null,error:null,focusPropertyId:"listConfigurations",forwardInNewWindow:false,forwardURL:null,forwardURLs:null,hasPostJS:false,hideDialog:false,module:"Invoice",nextModule:null,propertiesUsedInCalculations:null,reload:false,resizeDialog:false,selectedRows:null,showDialog:false,strokeActions:{"120,false,false,false":{confirmMessage:"",name:"Mode.list",takesLong:false},"66,true,false,false":{confirmMessage:"",name:"SearchForCRUD.search",takesLong:false},"80,true,true,false":{confirmMessage:"",name:"Print.generatePdf",takesLong:false},"39,true,true,false":{confirmMessage:"",name:"Navigation.next",takesLong:false},"83,true,false,false":{confirmMessage:"",name:"CRUD.save",takesLong:false},"68,true,false,false":{confirmMessage:"Borra los registros seleccionados: \u00BFEst\u00E1s seguro?",name:"CRUD.deleteSelected",takesLong:false},"36,true,true,false":{confirmMessage:"",name:"Navigation.first",takesLong:false},"37,true,true,false":{confirmMessage:"",name:"Navigation.previous",takesLong:false},"78,true,false,false":{confirmMessage:"",name:"CRUD.new",takesLong:false},"88,true,true,false":{confirmMessage:"",name:"Print.generateExcel",takesLong:false}},urlParam:null,viewMember:"",viewSimple:false});
}
// tmr fin
};


dwr.engine._callPostHooks = function(batch) {
if (batch.postHooks) {
for (var i = 0; i < batch.postHooks.length; i++) {
batch.postHooks[i]();
}
batch.postHooks = null;
}
};







dwr.engine._isHeartbeatBatch = function(batch) {
return (batch.map && batch.map['c0-methodName'] === 'checkHeartbeat' && batch.map['c0-scriptName'] === '__System');
};







dwr.engine._handleError = function(batch, ex) {
if (batch && dwr.engine._isHeartbeatBatch(batch)) {
return;
}
var errorHandlers = [];
var anyCallWithoutErrorHandler = false;
if (batch && batch.isPoll) {
dwr.engine._handlePollRetry(batch, ex);
} else {

if (batch) {
for (var i = 0; i < batch.map.callCount; i++) {
var handlers = batch.handlers[i];
if (!handlers.completed) {
if (typeof handlers.errorHandler == "function")
errorHandlers.push(handlers.errorHandler);
else
anyCallWithoutErrorHandler = true;
handlers.completed = true;
}
}
}

ignoreIfUnloading(batch, function() {
if (batch) {
var textHtmlHandler = batch.textHtmlHandler || batch.globalTextHtmlHandler;
if (ex.name === "dwr.engine.textHtmlReply" && textHtmlHandler) {
dwr.engine.util.logHandlerEx(function() {
textHtmlHandler(ex);
});
return;
}
}
dwr.engine._prepareException(ex);
var errorHandler;
while(errorHandlers.length > 0) {
errorHandler = errorHandlers.shift();
dwr.engine.util.logHandlerEx(function() {
errorHandler(ex.message, ex);
});
}
if (batch) {
dwr.engine.util.logHandlerEx(function() {
if (typeof batch.errorHandler == "function") batch.errorHandler(ex.message, ex);
else if (anyCallWithoutErrorHandler) {
if (typeof batch.globalErrorHandler == "function") batch.globalErrorHandler(ex.message, ex);
}
});
dwr.engine.batch.remove(batch);
}
});
}
};







dwr.engine._handlePollRetry = function(batch, ex) {
var retryInterval = dwr.engine._getRetryInterval();

if (dwr.engine._retries === dwr.engine._retryIntervals.length - 1) {
dwr.engine._debug("poll retry - going offline: " + retryInterval/1000 + " seconds");
dwr.engine._handlePollStatusChange(false, ex, batch);

var heartbeatInterval = setInterval(function() {
if (dwr.engine._maxRetries === -1 || dwr.engine._retries < dwr.engine._maxRetries) {
dwr.engine._execute(null, '__System', 'checkHeartbeat', [ function() {

clearInterval(heartbeatInterval);
dwr.engine._poll();
}]);
dwr.engine._retries++;
dwr.engine._debug("DWR Offline - poll retry - interval: " + retryInterval/1000 + " seconds");
} else {

clearInterval(heartbeatInterval);
dwr.engine._debug("max retries reached, stop polling for server status.");
dwr.engine._handlePollStatusChange(false, ex, batch);
}
}, retryInterval);
} else {
dwr.engine._retries++;
dwr.engine.batch.remove(batch);
dwr.engine._debug("DWR Online - poll retry - interval: " + retryInterval/1000 + " seconds");
setTimeout(dwr.engine._poll, retryInterval);
}
};

dwr.engine._getRetryInterval = function() {
var retryInterval;
if (dwr.engine._retries < dwr.engine._retryIntervals.length) {
retryInterval = dwr.engine._retryIntervals[dwr.engine._retries] * 1000;
} else {

retryInterval = dwr.engine._retryIntervals[dwr.engine._retryIntervals.length - 1] * 1000;
}
return retryInterval;
};






dwr.engine._handlePollStatusChange = function(newStatus, ex, batch) {
if (batch.isPoll || dwr.engine._isHeartbeatBatch(batch)) {
var changed = dwr.engine._pollOnline !== newStatus;
var maxRetriesReached = dwr.engine._maxRetries === dwr.engine._retries;
dwr.engine._pollOnline = newStatus;
if ((changed || maxRetriesReached) && typeof dwr.engine._pollStatusHandler === "function")
dwr.engine.util.logHandlerEx(function() {
dwr.engine._pollStatusHandler(newStatus, ex, maxRetriesReached);
});
if (newStatus) {
dwr.engine._retries = 1;
}
}
};







dwr.engine._handleWarning = function(batch, ex) {
if (batch && dwr.engine._isHeartbeatBatch(batch)) {
return;
}
ignoreIfUnloading(batch, function() {

dwr.engine._prepareException(ex);
dwr.engine.util.logHandlerEx(function() {
if (batch && typeof batch.warningHandler == "function") batch.warningHandler(ex.message, ex);
else if (dwr.engine._warningHandler) dwr.engine._warningHandler(ex.message, ex);
});
if (batch) dwr.engine.batch.remove(batch);
});
};






dwr.engine._prepareException = function(ex) {
if (typeof ex == "string") ex = { name:"unknown", message:ex };
if (ex.message == null) ex.message = "";
if (ex.name == null) ex.name = "unknown";
};





dwr.engine._delegate = (function() {
function F(){}
return (function(obj){
F.prototype = obj;
return new F();
});
})();





dwr.engine._createFromMap = function(map) {
var obj = new this();
for(prop in map) if (map.hasOwnProperty(prop)) obj[prop] = map[prop];
return obj;
};




dwr.engine._global = (function(){return this;}).call(null);







dwr.engine._getObject = function(prop) {
var parts = prop.split(".");
var value = undefined;
var scope = dwr.engine._global;
while(parts.length > 0) {
var currprop = parts.shift();
if (!scope) return undefined;
value = scope[currprop];
if (value && value.tagName && document.getElementById(currprop) == value) return undefined;
scope = value;
}
return value;
};






dwr.engine._setObject = function(prop, obj) {
var parts = prop.split(".");
var level;
var scope = dwr.engine._global;
while(parts.length > 0) {
var currprop = parts.shift();
if (parts.length === 0) {
scope[currprop] = obj;
}
else {
level = scope[currprop];
if (level == null || level.tagName && document.getElementById(currprop) == level) {
scope[currprop] = level = {};
}
scope = level;
}
}
};







dwr.engine._debug = function(message, stacktrace) {
var written = false;
try {
if (window.console) {
if (stacktrace && window.console.trace) window.console.trace();
if (window.console.log) window.console.log(message);
written = true;
}
else if (window.opera && window.opera.postError) {
window.opera.postError(message);
written = true;
}
}
catch (ex) {   }

if (!written) {
var debug = document.getElementById("dwr-debug");
if (debug) {
var contents = message + "<br/>" + debug.innerHTML;
if (contents.length > 2048) contents = contents.substring(0, 2048);
debug.innerHTML = contents;
}
}
};




dwr.engine.remote = {







handleCallback:function(batchId, callId, reply, param3) { // tmr param3
//alert("[handleCallback] 10"); // tmr
var batch = dwr.engine._batches[batchId];
//alert("[handleCallback] 20"); // tmr
if (!batch) return;
//alert("[handleCallback] 30"); // tmr


batch.reply = reply;
// tmr ini
console.log("[handleCallback] reply=" + reply);
console.log("[handleCallback] param3=" + param3);
if (param3 != null) {
	console.log("[handleCallback] >>>> reply vs param3");
	var obj = reply;
	for (let key in obj) {
	    console.log(key + ": " + obj[key] + " --> " + param3[key] + " : " + (obj[key] == param3[key]));
	    /*
	    var obj2 = obj[key]; 
		for (let key in obj2) {
	    	console.log(">> " + key + ": " + obj2[key]);
		}
		*/
	}
	console.log("[handleCallback] <<<<");
} 
// tmr fin

try {
var handlers = batch.handlers[callId];
if (!handlers) {
dwr.engine._debug("Warning: Missing handlers. callId=" + callId, true);
}
else {
batch.handlers[callId].completed = true;
if (typeof handlers.callback == "function") {
dwr.engine.util.logHandlerEx(function() {
if(handlers.callbackArg !== undefined)
handlers.callback.call(handlers.callbackScope, reply, handlers.callbackArg);
else
handlers.callback.call(handlers.callbackScope, reply);
});
}
}
}
catch (ex) {
dwr.engine._handleError(batch, ex);
}
//alert("[handleCallback] 999"); // tmr
},






handleFunctionCall:function(id, args) {
var func = dwr.engine.serialize.remoteFunctions[id];
func.apply(window, args);
},






handleObjectCall:function(id, methodName, args) {
var obj = dwr.engine.serialize.remoteFunctions[id];
obj[methodName].apply(obj, args);
},






handleSetCall:function(id, propertyName, data) {
var obj = dwr.engine.serialize.remoteFunctions[id];
obj[propertyName] = data;
},





handleFunctionClose:function(id) {
delete dwr.engine.serialize.remoteFunctions[id];
},








handleException:function(batchId, callId, ex) {
var batch = dwr.engine._batches[batchId];
if (!batch) return;

var handlers = batch.handlers[callId];
batch.handlers[callId].completed = true;
if (handlers == null) {
dwr.engine._debug("Warning: null handlers in remoteHandleException", true);
return;
}

if (ex.message === undefined) {
ex.message = "";
}

dwr.engine.util.logHandlerEx(function() {
if (typeof handlers.exceptionHandler == "function") {
if (handlers.exceptionArg !== undefined)
handlers.exceptionHandler.call(handlers.exceptionScope, ex.message, ex, handlers.exceptionArg);
else
handlers.exceptionHandler.call(handlers.exceptionScope, ex.message, ex);
}
else if (typeof handlers.errorHandler == "function") {
handlers.errorHandler(ex.message, ex);
}
else if (typeof batch.errorHandler == "function") {
batch.errorHandler(ex.message, ex);
}
else if (typeof batch.globalErrorHandler == "function") {
batch.globalErrorHandler(ex.message, ex);
}
});
},




handleReverseAjax:function(reverseAjaxIndex, reverseAjaxFunc) {
if (reverseAjaxIndex < dwr.engine._nextReverseAjaxIndex) return;
dwr.engine._reverseAjaxQueue[reverseAjaxIndex] = reverseAjaxFunc;
while(true) {
var nextFunc = dwr.engine._reverseAjaxQueue[dwr.engine._nextReverseAjaxIndex];
if (!nextFunc) return;
dwr.engine.util.logHandlerEx(function() {
nextFunc();
});
delete dwr.engine._reverseAjaxQueue[dwr.engine._nextReverseAjaxIndex];
dwr.engine._nextReverseAjaxIndex++;
}
},







handleBatchException:function(ex, batchId) {
var batch = dwr.engine._batches[batchId];
if (ex.message === undefined) ex.message = "";
if (batch) {
dwr.engine._handleError(batch, ex);
} else {
dwr.engine._queuedBatchException = ex;
}
},







pollCometDisabled:function(ex, batchId){
dwr.engine.setActiveReverseAjax(false);
var batch = dwr.engine._batches[batchId];
if (ex.message === undefined) {
ex.message = "";
}
dwr.engine._handleError(batch, ex);
},







newObject:function(dwrClassName, memberMap){
var classfunc = dwr.engine._mappedClasses[dwrClassName];
if (classfunc && classfunc.createFromMap) {
return classfunc.createFromMap(memberMap);
}
else {
memberMap.$dwrClassName = dwrClassName;
return memberMap;
}
}
};




dwr.engine.serialize = {



domDocument:[
"Msxml2.DOMDocument.6.0",
"Msxml2.DOMDocument.5.0",
"Msxml2.DOMDocument.4.0",
"Msxml2.DOMDocument.3.0",
"MSXML2.DOMDocument",
"MSXML.DOMDocument",
"Microsoft.XMLDOM"
],




remoteFunctions:{},




funcId:0,





toDomElement:function(xml) {
return dwr.engine.serialize.toDomDocument(xml).documentElement;
},





toDomDocument:function(xml) {
var dom;
if (window.DOMParser) {
var parser = new DOMParser();
dom = parser.parseFromString(xml, "text/xml");
if (!dom.documentElement || dom.documentElement.tagName == "parsererror") {
var message = dom.documentElement.firstChild.data;
message += "\n" + dom.documentElement.firstChild.nextSibling.firstChild.data;
throw message;
}
return dom;
}
else if (window.ActiveXObject) {
dom = dwr.engine.util.newActiveXObject(dwr.engine.serialize.domDocument);
dom.loadXML(xml);
return dom;
}
else {
var div = document.createElement("div");
div.innerHTML = xml;
return div;
}
},










convert:function(batch, directrefmap, otherrefmap, data, name, depth) {
if (data == null) {
batch.map[name] = "null:null";
return;
}

switch (typeof data) {
case "boolean":
batch.map[name] = "boolean:" + data;
break;
case "number":
batch.map[name] = "number:" + data;
break;
case "string":
batch.map[name] = "string:" + encodeURIComponent(data);
break;
case "object":
var ref = dwr.engine.serialize.lookup(directrefmap, otherrefmap, data, name);
var objstr = Object.prototype.toString.call(data);
if (data.$dwrByRef) batch.map[name] = dwr.engine.serialize.convertByReference(batch, directrefmap, otherrefmap, data, name, depth + 1);
else if (ref != null) batch.map[name] = ref;
else if (objstr == "[object String]") batch.map[name] = "string:" + encodeURIComponent(data);
else if (objstr == "[object Boolean]") batch.map[name] = "boolean:" + data;
else if (objstr == "[object Number]") batch.map[name] = "number:" + data;
else if (objstr == "[object Date]") batch.map[name] = "date:" + data.getTime();
else if (objstr == "[object Array]") batch.map[name] = dwr.engine.serialize.convertArray(batch, directrefmap, otherrefmap, data, name, depth + 1);
else if (data && data.tagName && data.tagName.toLowerCase() == "input" && data.type && data.type.toLowerCase() == "file") {
batch.fileUpload = true;
batch.map[name] = data;
}
else {


if (data.nodeName && data.nodeType) {
batch.map[name] = dwr.engine.serialize.convertDom(data);
}
else {
batch.map[name] = dwr.engine.serialize.convertObject(batch, directrefmap, otherrefmap, data, name, depth + 1);
}
}
break;
case "function":

if (depth === 0) {
batch.map[name] = dwr.engine.serialize.convertByReference(batch, directrefmap, otherrefmap, data, name, depth + 1);
}
break;
default:
dwr.engine._handleWarning(null, { name:"dwr.engine.unexpectedType", message:"Unexpected type: " + typeof data + ", attempting default converter." });
batch.map[name] = "default:" + data;
break;
}
},






convertByReference:function(batch, directrefmap, otherrefmap, data, name, depth) {
var funcId = "f" + dwr.engine.serialize.funcId;
dwr.engine.serialize.remoteFunctions[funcId] = data;
dwr.engine.serialize.funcId++;
return "byref:" + funcId;
},






convertArray:function(batch, directrefmap, otherrefmap, data, name, depth) {
var childName, i;
if (dwr.engine.util.ieCondition("if lte IE 7")) {

var buf = ["array:["];
for (i = 0; i < data.length; i++) {
if (i !== 0) buf.push(",");
batch.paramCount++;
childName = "c" + dwr.engine._batch.map.callCount + "-e" + batch.paramCount;
dwr.engine.serialize.convert(batch, directrefmap, otherrefmap, data[i], childName, depth + 1);
buf.push("reference:");
buf.push(childName);
}
buf.push("]");
reply = buf.join("");
}
else {

var reply = "array:[";
for (i = 0; i < data.length; i++) {
if (i !== 0) reply += ",";
batch.paramCount++;
childName = "c" + dwr.engine._batch.map.callCount + "-e" + batch.paramCount;
dwr.engine.serialize.convert(batch, directrefmap, otherrefmap, data[i], childName, depth + 1);
reply += "reference:";
reply += childName;
}
reply += "]";
}

return reply;
},






convertObject:function(batch, directrefmap, otherrefmap, data, name, depth) {

var reply = "Object_" + dwr.engine.serialize.getObjectClassName(data).replace(/:/g, "?") + ":{";
var elementset = (data.constructor && data.constructor.$dwrClassMembers ? data.constructor.$dwrClassMembers : data);
var element;
for (element in elementset) {
if (typeof data[element] != "function" && !dwr.engine._excludeObjectAttributes[element]) {
batch.paramCount++;
var childName = "c" + dwr.engine._batch.map.callCount + "-e" + batch.paramCount;
dwr.engine.serialize.convert(batch, directrefmap, otherrefmap, data[element], childName, depth + 1);
reply += encodeURIComponent(element) + ":reference:" + childName + ", ";
}
}

if (reply.substring(reply.length - 2) == ", ") {
reply = reply.substring(0, reply.length - 2);
}
reply += "}";

return reply;
},






convertDom:function(data) {
return "xml:" + encodeURIComponent(dwr.engine.serialize.serializeDom(data));
},
serializeDom:function(data) {
var output;
if (window.XMLSerializer) output = new XMLSerializer().serializeToString(data);
else if (data.xml) output = data.xml;
else throw new Error("The browser doesn't support XML serialization of: " + data);
return output;
},






lookup:function(directrefmap, otherrefmap, data, name) {

var ref;
if ("$_dwrConversionRef" in data) {
ref = data.$_dwrConversionRef;
if (ref && directrefmap[ref] != data) ref = null;
}
if (ref == null) {
for(r in otherrefmap) {
if (otherrefmap[r] == data) {
ref = r;
break;
}
}
}
if (ref != null) return "reference:" + ref;

try {
data.$_dwrConversionRef = name;
directrefmap[name] = data;
}
catch(err) {
otherrefmap[name] = data;
}
return null;
},





cleanup:function(directrefmap) {
for(var refName in directrefmap) {
var data = directrefmap[refName];
try {
delete data.$_dwrConversionRef;
}
catch(err) {
data.$_dwrConversionRef = undefined;
}
}
},









getObjectClassName:function(obj) {

if (obj.$dwrClassName)
return obj.$dwrClassName;
else if (obj.constructor && obj.constructor.$dwrClassName)
return obj.constructor.$dwrClassName;
else
return "Object";
}
};




dwr.engine.transport = {





send:function(batch) {
if (batch.path == null) {
batch.path = dwr.engine._effectivePath();
}
dwr.engine.transport.updateDwrSessionFromCookie();
if (!dwr.engine._dwrSessionId) {
dwr.engine._internalOrdered = true;
var retval;
var idbatch = {
map:{
callCount:1,
'c0-scriptName':'__System',
'c0-methodName':'generateId',
'c0-id':0
},
paramCount:0, isPoll:false, async:batch.async,
headers:{}, preHooks:[],
postHooks:[function() {
dwr.engine._internalOrdered = false;
}],
timeout:dwr.engine._timeout,
errorHandler:batch.errorHandler, globalErrorHandler:batch.globalErrorHandler, warningHandler:batch.warningHandler, textHtmlHandler:batch.textHtmlHandler, globalTextHtmlHandler:batch.globalTextHtmlHandler,
path:batch.path,
handlers:[{
exceptionHandler:null,
callback:function(id) {
dwr.engine.transport.updateDwrSessionFromCookie();
if (!dwr.engine._dwrSessionId) {
dwr.engine.transport.setDwrSession(id);
}
retval = dwr.engine.transport.send2(batch);
}
}]
};
dwr.engine.transport.send2(idbatch);
return retval;
}
else {
return dwr.engine.transport.send2(batch);
}
},






send2:function(batch) {
dwr.engine.batch.prepareToSend(batch);


var isCrossDomain = false;
if (batch.path.indexOf("http://") === 0 || batch.path.indexOf("https://") === 0) {
var dwrShortPath = batch.path.split("/", 3).join("/");
var hrefShortPath = window.location.href.split("/", 3).join("/");
isCrossDomain = (dwrShortPath != hrefShortPath);
}

if (batch.fileUpload) {
if (isCrossDomain) {
throw new Error("Cross domain file uploads are not possible with this release of DWR");
}
batch.transport = dwr.engine.transport.iframe;
}
else if (isCrossDomain) {
batch.transport = dwr.engine.transport.scriptTag;
}
else if (batch.isPoll && dwr.engine._useStreamingPoll == "true" && dwr.engine.util.ieCondition("if (IE 8)|(IE 9)")) {
batch.transport = dwr.engine.transport.iframe;
}
else {
batch.transport = dwr.engine.transport.xhr;
}

return batch.transport.send(batch);
},




complete:function(batch) {
dwr.engine.batch.validate(batch);
dwr.engine.transport.remove(batch);
},






abort:function(batch) {
var transport = batch.transport;
if (transport.abort) {
transport.abort(batch);
}
dwr.engine.transport.remove(batch);
},





remove:function(batch) {
if (batch.transport) {
dwr.engine._callPostHooks(batch);
batch.transport.remove(batch);
batch.transport = null;
}
dwr.engine.batch.remove(batch);
},

setDwrSession:function(dwrsess) {
dwr.engine._dwrSessionId = dwrsess;
var attrs = "";
if (!dwr.engine._cookieAttributes.match(/^path=/i)) {
attrs = "; path=" + (dwr.engine._effectiveContextPath() || "/");
}
if (dwr.engine._cookieAttributes) {
attrs += "; " + dwr.engine._cookieAttributes;
}
document.cookie = "DWRSESSIONID=" + dwrsess + attrs;
dwr.engine._scriptSessionId = dwrsess + "/" + dwr.engine._pageId;
},

updateDwrSessionFromCookie:function() {
if (!dwr.engine._dwrSessionId) {
var match = document.cookie.match(/(?:^|; )DWRSESSIONID=([^;]+)/);
if (match) {
dwr.engine.transport.setDwrSession(match[1]);
}
}
},




xhr:{



httpMethod:"POST",






XMLHTTP:["Msxml2.XMLHTTP.6.0", "Msxml2.XMLHTTP.5.0", "Msxml2.XMLHTTP.4.0", "MSXML2.XMLHTTP.3.0", "MSXML2.XMLHTTP", "Microsoft.XMLHTTP"],





send:function(batch) {
if (batch.isPoll) {
batch.map.partialResponse = dwr.engine._partialResponseYes;
}


if (batch.isPoll && dwr.engine._useStreamingPoll == "false") {
batch.map.partialResponse = dwr.engine._partialResponseNo;
}
if (batch.isPoll && dwr.engine.util.ieCondition("if lte IE 9")) {
batch.map.partialResponse = dwr.engine._partialResponseNo;
}

if (window.XMLHttpRequest) {
batch.req = new XMLHttpRequest();
}
else if (window.ActiveXObject) {
batch.req = dwr.engine.util.newActiveXObject(dwr.engine.transport.xhr.XMLHTTP);
}


if (batch.async === true) {
batch.req.onreadystatechange = function() {
if (typeof dwr != 'undefined') {
dwr.engine.transport.xhr.stateChange(batch);
}
};
}

httpMethod = dwr.engine.transport.xhr.httpMethod;

batch.mode = batch.isPoll ? dwr.engine._ModePlainPoll : dwr.engine._ModePlainCall;
var request = dwr.engine.batch.constructRequest(batch, httpMethod);

try {
batch.req.open(httpMethod, request.url, batch.async);
try {
for (var prop in batch.headers) {
var value = batch.headers[prop];
if (typeof value == "string") {
batch.req.setRequestHeader(prop, value);
}
}
if (!batch.headers["Content-Type"]) {
batch.req.setRequestHeader("Content-Type", "text/plain");
}
}
catch (ex) {
dwr.engine._handleWarning(batch, ex);
}

batch.req.send(request.body);
if (batch.async === false) {
dwr.engine.transport.xhr.stateChange(batch);
}
}
catch (ex2) {
dwr.engine._handleError(batch, ex2);
}

if (batch.isPoll && batch.map.partialResponse == dwr.engine._partialResponseYes) {
dwr.engine.transport.xhr.checkCometPoll();
}


return batch.reply;
},






stateChange:function(batch) {
var toEval;

if (batch.aborted) return;
if (batch.completed) {
if (batch.transport) dwr.engine._debug("Error: _stateChange() with batch.completed");
return;
}


var req = batch.req;
var status = 0;
try {
if (req.readyState >= 2) {
status = req.status;
}
}
catch(ignore) {}



if (status === 0 && req.readyState < 4) {
return;
}





if (status === 200) {
dwr.engine._handlePollStatusChange(true, null, batch);
}


if (req.readyState != 4) {
return;
}

if (dwr.engine._unloading) {
dwr.engine._debug("Ignoring reply from server as page is unloading.");
return;
}

// tmr try {
var reply = req.responseText;
reply = dwr.engine._replyRewriteHandler(reply);
var contentType = req.getResponseHeader("Content-Type");
if (status >= 200 && status < 300) {
if (contentType.indexOf("text/plain") < 0 && contentType.indexOf("text/javascript") < 0) {
if (contentType.indexOf("text/html") == 0) {
dwr.engine._handleError(batch, { name:"dwr.engine.textHtmlReply", message:"HTML reply from the server.", responseText:reply||"" });
} else {
dwr.engine._handleError(batch, { name:"dwr.engine.invalidMimeType", message:"Invalid content type: '" + contentType + "'" });
}
} else if (reply == null || reply === "") {
dwr.engine._handleError(batch, { name:"dwr.engine.missingData", message:"No data received from server" });
} else {
if (batch.isPoll && batch.map.partialResponse == dwr.engine._partialResponseYes) {
dwr.engine.transport.xhr.processCometResponse(reply, batch);
} else {
if (reply.search("//#DWR") === -1) {
dwr.engine._handleError(batch, { name:"dwr.engine.invalidReply", message:"Invalid reply from server" });
} else {
toEval = reply;
}
}
}
} else if (status >= 400) {
var statusText = "statusText could not be read.";
try {
statusText = req.statusText;
} catch (ex) {

}
dwr.engine._handleError(batch, { name:"dwr.engine.http." + status, message:statusText });
}

if (toEval != null) toEval = toEval.replace(dwr.engine._scriptTagProtection, "");
dwr.engine._executeScript(toEval);
dwr.engine.transport.complete(batch);

/* tmr
} catch (ex2) {
dwr.engine._handleError(batch, ex2);
}
*/
},





checkCometPoll:function() {
var req = dwr.engine._pollBatch && dwr.engine._pollBatch.req;
if (req) {
var text = req.responseText;
if (text != null) {
dwr.engine.transport.xhr.processCometResponse(text, dwr.engine._pollBatch);
}
}

if (dwr.engine._pollBatch) {
setTimeout(dwr.engine.transport.xhr.checkCometPoll, dwr.engine._pollCometInterval);
}
},








processCometResponse:function(response, batch) {
if (batch.charsProcessed == response.length) return;
if (response.length === 0) {
batch.charsProcessed = 0;
return;
}

var firstStartTag = response.indexOf("//#DWR-START#", batch.charsProcessed);
if (firstStartTag == -1) {

return;
}




var lastEndTag = response.lastIndexOf("//#DWR-END#");
if (lastEndTag == -1) {

return;
}


if (response.charCodeAt(lastEndTag + 11) == 13 && response.charCodeAt(lastEndTag + 12) == 10) {
batch.charsProcessed = lastEndTag + 13;
}
else {
batch.charsProcessed = lastEndTag + 11;
}

var exec = response.substring(firstStartTag + 13, lastEndTag);

try {
dwr.engine._executeScript(exec);
}
catch (ex) {


if (dwr != null) {
dwr.engine._handleError(batch, ex);
}
}
},




abort:function(batch) {
if (batch.req) {
batch.aborted = true;
batch.req.abort();
}
},





remove:function(batch) {

if (batch.req) {
delete batch.req;
}
}
},




iframe:{



httpMethod:"POST",





send:function(batch) {
if (document.body == null) {
setTimeout(function(){dwr.engine.transport.iframe.send(batch);}, 100);
return;
}
batch.httpMethod = dwr.engine.transport.iframe.httpMethod;
if (batch.fileUpload) {
batch.httpMethod = "POST";
batch.encType = "multipart/form-data";
}
batch.loadingStarted = false;
if (window.location.hostname != document.domain) batch.map.documentDomain = document.domain;
var idname = dwr.engine.transport.iframe.getId(batch);
batch.div1 = document.createElement("div");
batch.div1.innerHTML = "<iframe frameborder='0' style='width:0px;height:0px;border:0;display:none;' id='" + idname + "' name='" + idname + "'></iframe>";
batch.iframe = batch.div1.firstChild;
batch.document = document;
batch.iframe.batch = batch;
batch.fileInputs = [];
dwr.engine.util.addEventListener(batch.iframe, "load", function(ev) {

if (!batch.loadingStarted) return;

while(batch.fileInputs.length > 0) {
var entry = batch.fileInputs.pop();
entry.original.setAttribute("id", entry.clone.getAttribute("id"));
entry.original.setAttribute("name", entry.clone.getAttribute("name"));
entry.original.style.display = entry.clone.style.display;
entry.clone.parentNode.replaceChild(entry.original, entry.clone);
}

if (batch.completed) return;

if (dwr.engine._queuedBatchException) {
dwr.engine._handleError(batch, dwr.engine._queuedBatchException);
dwr.engine._queuedBatchException = null;
return;
}

try {
var contentDoc = batch.iframe.contentDocument || batch.iframe.contentWindow.document;
} catch(ex) {
var contentEx = ex;
}
if (typeof dwr != "undefined") {
var htmlResponse = contentDoc && contentDoc.documentElement ? contentDoc.documentElement.outerHTML : "(Could not extract HTML response: " + contentEx.message + ")";
dwr.engine._handleError(batch, {name:"dwr.engine.textHtmlReply", message:"HTML reply from the server.", responseText:htmlResponse});
}
});

// http://stackoverflow.com/questions/18414964/load-event-for-iframe-does-not-fire-in-ie
document.body.appendChild(batch.div1);
dwr.engine.transport.iframe.beginLoader(batch, idname);
},






getId:function(batch) {
return batch.isPoll ? "dwr-if-poll-" + dwr.engine._instanceId + "-" + batch.map.batchId : "dwr-if-" + dwr.engine._instanceId + "-" + batch.map.batchId;
},





beginLoader:function(batch, idname) {
if (batch.isPoll) {
batch.map.partialResponse = dwr.engine._partialResponseYes;
}
batch.mode = batch.isPoll ? dwr.engine._ModeHtmlPoll : dwr.engine._ModeHtmlCall;
var request = dwr.engine.batch.constructRequest(batch, batch.httpMethod);
if (batch.httpMethod === "GET") {
batch.iframe.setAttribute("src", request.url);
}
else {

// See http://soakedandsoaped.com/articles/read/firefox-3-native-ajax-file-upload

batch.div2 = document.createElement("div");
document.body.appendChild(batch.div2);
batch.div2.innerHTML = "<form" + (batch.encType ? " encType='" + batch.encType + "' encoding='" + batch.encType + "'" : "") + "></form>";
batch.form = batch.div2.firstChild;
batch.form.setAttribute("action", request.url);
batch.form.setAttribute("target", idname);
batch.form.setAttribute("style", "display:none");
batch.form.setAttribute("method", batch.httpMethod);
for (var prop in batch.map) {
var value = batch.map[prop];
if (typeof value != "function") {
if (value && value.tagName && value.tagName.toLowerCase() == "input" && value.type && value.type.toLowerCase() == "file") {



if (value.parentNode) {
var clone = value.cloneNode(true);
value.parentNode.replaceChild(clone, value);
batch.fileInputs.push({original:value, clone:clone});
}
value.removeAttribute("id");
value.setAttribute("name", prop);
value.style.display = "none";
batch.form.appendChild(value);
} else {
var formInput = batch.document.createElement("input");
formInput.setAttribute("type", "hidden");
formInput.setAttribute("name", prop);
formInput.setAttribute("value", value);
batch.form.appendChild(formInput);
}
}
}
batch.loadingStarted = true;
batch.form.submit();
}
},




remote:{






beginIFrameResponse:function(iframe, batchId) {
},






endChunk:function(iframeWindow) {
setTimeout(function() {

var scriptTags = iframeWindow.document.body.getElementsByTagName("script");
if (scriptTags.length > 0) {
var s = scriptTags[0];
s.parentNode.removeChild(s);
}
}, 0);
},






endIFrameResponse:function(batchId) {
var batch = dwr.engine._batches[batchId];
if (batch) dwr.engine.transport.complete(batch);
}
},

remove:function(batch) {


setTimeout(function(){
if (batch.iframe && batch.iframe.parentNode) {
batch.iframe.parentNode.removeChild(batch.iframe);
batch.iframe = null;
}
if (batch.div1 && batch.div1.parentNode) {
batch.div1.parentNode.removeChild(batch.div1);
batch.div1 = null;
}
if (batch.form && batch.form.parentNode) {
batch.form.parentNode.removeChild(batch.form);
batch.form = null;
}
if (batch.div2 && batch.div2.parentNode) {
batch.div2.parentNode.removeChild(batch.div2);
batch.div2 = null;
}
}, 100);
}
},




scriptTag:{




send:function(batch) {
if (batch.isPoll) {
batch.map.partialResponse = dwr.engine._partialResponseNo;
}
batch.mode = batch.isPoll ? dwr.engine._ModePlainPoll : dwr.engine._ModePlainCall;
var request = dwr.engine.batch.constructRequest(batch, "GET");

batch.script = document.createElement("script");
batch.script.id = "dwr-st-" + batch.map.batchId;
batch.script.src = request.url;
batch.script.type = "text/javascript";
batch.script.async = true;
dwr.engine.util.addEventListener(batch.script, "load", function(ev) {
if (typeof dwr != "undefined") dwr.engine.transport.scriptTag.complete(batch);
});
dwr.engine.util.addEventListener(batch.script, "error", function(ev) {
if (typeof dwr != "undefined") dwr.engine.transport.scriptTag.complete(batch);
});
dwr.engine.util.addEventListener(batch.script, "readystatechange", function(ev) {
if (typeof dwr != "undefined") {
if (batch.completed) return;
if (batch.script.readyState == "complete" || batch.script.readyState == "loaded") {
dwr.engine.transport.scriptTag.complete(batch);
}
}
});
document.getElementsByTagName("head")[0].appendChild(batch.script);
},




complete:function(batch) {
dwr.engine.transport.complete(batch);
},




remove:function(batch) {

if (!batch.script) return;


batch.script.parentNode.removeChild(batch.script);
batch.script = null;
}
}
};





dwr.engine.batch = {




create:function() {
var batch = {
async:dwr.engine._async,
charsProcessed:0,
handlers:[],
isPoll:false,
map:{ callCount:0 },
paramCount:0,
preHooks:[],
postHooks:[],
timeout:dwr.engine._timeout,
errorHandler:null,
globalErrorHandler:dwr.engine._errorHandler,
warningHandler:dwr.engine._warningHandler,
textHtmlHandler:null,
globalTextHtmlHandler:dwr.engine._textHtmlHandler
};
if (!dwr.engine._activeReverseAjax) batch.map.nextReverseAjaxIndex = dwr.engine._nextReverseAjaxIndex;

if (dwr.engine._preHook) {
batch.preHooks.push(dwr.engine._preHook);
}
if (dwr.engine._postHook) {
batch.postHooks.push(dwr.engine._postHook);
}

dwr.engine.batch.populateHeadersAndAttributes(batch);
return batch;
},






createPoll:function() {
var batch = {
async:true,
charsProcessed:0,
handlers:[{
callback:function(pause) {
dwr.engine._pollBatch = null;
setTimeout(dwr.engine._poll, pause);
}
}],
isPoll:true,
map:{ callCount:1, nextReverseAjaxIndex:dwr.engine._nextReverseAjaxIndex},
paramCount:0,
preHooks:[],
postHooks:[],
timeout:dwr.engine._pollTimeout
};
dwr.engine.batch.populateHeadersAndAttributes(batch);
return batch;
},






populateHeadersAndAttributes:function(batch) {
var propname, data;
batch.headers = {};
if (dwr.engine._headers) {
for (propname in dwr.engine._headers) {
data = dwr.engine._headers[propname];
if (typeof data != "function") batch.headers[propname] = data;
}
}
batch.attributes = {};
if (dwr.engine._attributes) {
for (propname in dwr.engine._attributes) {
data = dwr.engine._attributes[propname];
if (typeof data != "function") batch.attributes[propname] = data;
}
}
},





addCall:function(batch, scriptName, methodName, args) {


var callData, stopAt;
var lastArg = args[args.length - 1];
if (lastArg == null || typeof lastArg == "function") {
callData = { callback:lastArg };
stopAt = args.length - 1;
}
else if (dwr.engine.util.isCallOptionArgument(lastArg)) {
callData = lastArg;
stopAt = args.length - 1;
}
else {
callData = {};
stopAt = args.length;
}


if (dwr.engine._singleShot) dwr.engine.batch.merge(batch, callData);
batch.handlers[batch.map.callCount] = {
callback:callData.callbackHandler || callData.callback,
callbackArg:callData.callbackArg || callData.arg,
callbackScope:callData.callbackScope || callData.scope || window,
exceptionHandler:callData.exceptionHandler,
exceptionArg:callData.exceptionArg || callData.arg,
exceptionScope:callData.exceptionScope || callData.scope || window,
errorHandler:callData.errorHandler
};


var prefix = "c" + batch.map.callCount + "-";
batch.map[prefix + "scriptName"] = scriptName;
batch.map[prefix + "methodName"] = methodName;
batch.map[prefix + "id"] = batch.map.callCount;
var directrefmap = {}, otherrefmap = {};
for (var i = 0; i < stopAt; i++) {
dwr.engine.serialize.convert(batch, directrefmap, otherrefmap, args[i], prefix + "param" + i, 0);
}
dwr.engine.serialize.cleanup(directrefmap);
},







merge:function(batch, overrides) {
var propname, data;
for (var i = 0; i < dwr.engine._propnames.length; i++) {
propname = dwr.engine._propnames[i];
if (dwr.engine._singleShot && propname == "errorHandler") continue;
if (overrides[propname] != null) batch[propname] = overrides[propname];
}
if (overrides.preHook != null) batch.preHooks.unshift(overrides.preHook);
if (overrides.postHook != null) batch.postHooks.push(overrides.postHook);
if (overrides.headers) {
for (propname in overrides.headers) {
data = overrides.headers[propname];
if (typeof data != "function") batch.headers[propname] = data;
}
}
var attrs = null;
if (overrides.attributes) attrs = overrides.attributes;
if (attrs) {
for (propname in attrs) {
data = attrs[propname];
if (typeof data != "function") batch.attributes[propname] = data;
}
}
},






prepareToSend:function(batch) {
batch.map.batchId = dwr.engine._nextBatchId;
dwr.engine._nextBatchId++;
dwr.engine._batches[batch.map.batchId] = batch;
dwr.engine._batchesLength++;
batch.completed = false;
batch.map.instanceId = dwr.engine._instanceId;


batch.map.page = encodeURIComponent(window.location.pathname + window.location.search);
batch.map.scriptSessionId = dwr.engine._scriptSessionId;

for (var i = 0; i < batch.preHooks.length; i++) {
batch.preHooks[i]();
}
batch.preHooks = null;

if (batch.timeout && batch.timeout !== 0) {
batch.timeoutId = setTimeout(function() {
dwr.engine.transport.abort(batch);
dwr.engine._handleError(batch, { name:"dwr.engine.timeout", message:"Timeout" });
}, batch.timeout);
}
},







constructRequest:function(batch, httpMethod) {

var urlBuffer = [];
urlBuffer.push(batch.path);
urlBuffer.push(batch.mode);
if (batch.isPoll) {
urlBuffer.push("ReverseAjax.dwr");
}
else if (batch.map.callCount == 1) {
urlBuffer.push(batch.map["c0-scriptName"]);
urlBuffer.push(".");
urlBuffer.push(batch.map["c0-methodName"]);
urlBuffer.push(".dwr");
}
else {
urlBuffer.push("Multiple.");
urlBuffer.push(batch.map.callCount);
urlBuffer.push(".dwr");
}

var sessionMatchExpr = new RegExp(
"^" +
"[^;\\?#]+" +
"(;[^\\?#]+)");
var sessionMatch = location.href.match(sessionMatchExpr);
if (sessionMatch != null) {
urlBuffer.push(sessionMatch[1]);
}


if (batch.attributes) {
for (var attrname in batch.attributes) {
var data = batch.attributes[attrname];
if (typeof data != "function") batch.map["a-" + attrname] = "" + data;
}
}

var request = {};
var prop;
if (httpMethod == "GET") {


batch.map.callCount = "" + batch.map.callCount;
urlBuffer.push("?");
for (prop in batch.map) {
if (typeof batch.map[prop] != "function") {
urlBuffer.push(encodeURIComponent(prop));
urlBuffer.push("=");
urlBuffer.push(encodeURIComponent(batch.map[prop]));
urlBuffer.push("&");
}
}
urlBuffer.pop();
request.body = null;
}
else {

request.body = "";
if (dwr.engine.util.ieCondition("if lte IE 7")) {

var buf = [];
for (prop in batch.map) {
if (typeof batch.map[prop] != "function") {
buf.push(prop + "=" + batch.map[prop] + dwr.engine._postSeperator);
}
}
request.body = buf.join("");
}
else {

for (prop in batch.map) {
if (typeof batch.map[prop] != "function") {
request.body += prop + "=" + batch.map[prop] + dwr.engine._postSeperator;
}
}
}
request.body = dwr.engine._contentRewriteHandler(request.body);
}
request.url = dwr.engine._urlRewriteHandler(urlBuffer.join(""));
return request;
},







validate:function(batch) {
if (!batch.completed) {
var repliesReceived = 0;
for (var i = 0; i < batch.map.callCount; i++) {
if (batch.handlers[i].completed === true) {
repliesReceived++;
}
}
if (repliesReceived == 0 && dwr.engine._queuedBatchException) {
dwr.engine._handleError(batch, dwr.engine._queuedBatchException);
dwr.engine._queuedBatchException = null;
} else if (repliesReceived < batch.map.callCount) {
dwr.engine._handleError(batch, { name:"dwr.engine.incompleteReply", message:"Incomplete reply from server" });
}
}
},






remove:function(batch) {
if (!batch) {
dwr.engine._debug("Warning: null batch in dwr.engine.batch.remove()", true);
return;
}

if (batch.completed) {
return;
}
batch.completed = true;


dwr.engine.transport.remove(batch);


if (batch.timeoutId != null) {
clearTimeout(batch.timeoutId);
delete batch.timeoutId;
}


if (batch.map && (batch.map.batchId != null)) {
delete dwr.engine._batches[batch.map.batchId];
dwr.engine._batchesLength--;
}
if (batch == dwr.engine._batch) dwr.engine._batch = null;
if (batch == dwr.engine._pollBatch) dwr.engine._pollBatch = null;




if (dwr.engine._batchQueue.length !== 0) {
var sendbatch = dwr.engine._batchQueue.shift();
dwr.engine.transport.send(sendbatch);
}
}
};





dwr.engine.util = {




newActiveXObject:function(axarray) {
var returnValue;
for (var i = 0; i < axarray.length; i++) {
try {
returnValue = new ActiveXObject(axarray[i]);
break;
}
catch (ex) {   }
}
return returnValue;
},

ieCondition:function(cond) {
if (!(cond in dwr.engine._ieConditions)) {
var div = document.createElement("div");
div.innerHTML = "<!--[" + cond + "]><p><![endif]-->";
dwr.engine._ieConditions[cond] = !!(div.getElementsByTagName("p").length);
}
return dwr.engine._ieConditions[cond];
},




tokenify: function(number) {
var tokenbuf = [];
var charmap = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ*$";
var remainder = number;
while (remainder > 0)
{
tokenbuf.push(charmap.charAt(remainder & 0x3F));
remainder = Math.floor(remainder / 64);
}
return tokenbuf.join("");
},

addEventListener: function(elem, name, func) {
if (elem.addEventListener)
elem.addEventListener(name, func, false);
else
elem.attachEvent("on" + name, func);
},

isCallOptionArgument: function(lastArg) {
return (typeof lastArg === "object" && (typeof lastArg.callback === "function" ||
typeof lastArg.exceptionHandler === "function" || typeof lastArg.callbackHandler === "function" ||
typeof lastArg.errorHandler === "function" || typeof lastArg.warningHandler === "function" || lastArg.hasOwnProperty("async")));
},

logHandlerEx: function(func) {
try {
func();
} catch(ex) {
dwr.engine._debug("Exception occured in user-specified handler:");
dwr.engine._debug(ex);
}
}
};


dwr.engine._initializer.init();




dwr.hub = {





publish:function(topicName, data) {
dwr.engine._execute(null, '__System', 'publish', topicName, data, {});
},









subscribe:function(topicName, callback, scope, subscriberData) {
var subscription = "" + dwr.hub._subscriptionId;
dwr.hub._subscriptionId++;
dwr.hub._subscriptions[subscription] = {
callback:callback,
scope:scope,
subscriberData:subscriberData
};
dwr.engine._execute(null, '__System', 'subscribe', topicName, subscription, {});
return subscription;
},







_remotePublish:function(subscriptionId, publishData) {
var subscriptionData = dwr.hub._subscriptions[subscriptionId];
if (!subscriptionData) return;
subscriptionData.callback.call(subscriptionData.scope, publishData, subscriptionData.subscriberData);
},




_subscriptionId:0,




_subscriptions:{}
};





dwr.data = {





StoreChangeListener:{





itemRemoved:function(source, itemId) { },






itemAdded:function(source, item) { },








itemChanged:function(source, item, changedAttributes) { }
},








Cache:function(storeId, listener) {
this.storeId = storeId;
this.listener = listener;
}
};












dwr.data.Cache.prototype.viewRegion = function(region, callbackObj) {
if (!region) region = { };
if (!region.start) region.start = 0;
if (!region.count) region.count = -1;
if (!region.sort) region.sort = [];
else {
for (var index = 0; index < region.sort.length; index++) {
if (typeof region.sort[index].descending == "undefined") {
region.sort[index].descending = false;
}
}
}
if (!region.query) region.query = {};

return dwr.engine._execute(null, '__Data', 'viewRegion', [ this.storeId, region, this.listener, callbackObj ]);
};






dwr.data.Cache.prototype.viewItem = function(itemId, callbackObj) {
return dwr.engine._execute(null, '__Data', 'viewItem', [ this.storeId, itemId, this.listener, callbackObj ]);
};





dwr.data.Cache.prototype.unsubscribe = function(callbackObj) {
if (this.listener) {
return dwr.engine._execute(null, '__Data', 'unsubscribe', [ this.storeId, this.listener, callbackObj ]);
}
};






dwr.data.Cache.prototype.update = function(items, callbackObj) {
return dwr.engine._execute(null, '__Data', 'update', [ this.storeId, items, callbackObj ]);
};

})();


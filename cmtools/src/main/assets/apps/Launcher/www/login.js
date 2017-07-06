/**
 * Created by Administrator on 2017/3/8.
 */
/**
 *  @param 带token Url
 *  @return token
 */
function analysisUrl(url, key) {
	var paramUrl = "";
	paramUrl = url.split("?")[1];
	var params = paramUrl.split("&");
	var json = "{";
	for(var i = 0; i < params.length; i++) {
		json = json + "\"" + params[i].split("=")[0] + "\":\"" + params[i].split("=")[1] + "\"";
		if(i != params.length - 1) {
			json = json + ",";
		}
	}
	json = json + "}";
	return JSON.parse(json)[key];
}

function getUrl() {
	var data = {
		'domainName': 'houjian.com',
		'productCode': 'cmtools'
	};
	data = JSON.stringify(data);
	mui.ajax("https://apphttps.dayang.com:9443/portal/api/projService", {
		async: false,
		data: data,
		dataType: 'json',
		type: 'post',
		timeout: 5000,
		success: function(data) {
			data = typeof data == 'string' ? JSON.parse(data) : data;
			if(true == data.status && null != data.data) {
				localStorage.setItem("casService", data.data.casservice);
				localStorage.setItem("backendService", data.data.backendservice);
			} else if(false == data.status) {
				plus.nativeUI.toast("获取配置地址失败");
				close();
			}
		},
		error: function(request, description, error) {
			plus.nativeUI.toast(description);
			close();
		}
	});
}

function login(url) {
	//	getUrl();
	//	var requestUrl = localStorage.getItem("backendService");
	var requestUrl = "http://LBAppVerifySrv-1569599441.cn-north-1.elb.amazonaws.com.cn/AppVerifyService/api/queryUserInfoByToken";
//	var requestUrl = "http://192.168.20.68:8080/AppVerifyService/api/queryUserInfoByToken";
	var token = analysisUrl(url, "teamToken");
	var appId = analysisUrl(url, "appId");
	var data = {
		"teamToken": token,
		"appId": appId
	};
	data = JSON.stringify(data);
	mui.ajax(requestUrl, {
		async: false,
		data: data,
		dataType: 'json',
		type: 'post',
		timeout: 5000,
		success: function(data) {
			console.log(data);
			analysisPortalData(data, url);
		},
		error: function(request, description, error) {
			plus.nativeUI.toast(description);
			close();
		}
	});
}

function analysisPortalData(data, appUrl) {
	data = (typeof data == 'object') ? data : JSON.parse(data);
	console.log(JSON.stringify(data));
	if(!(true == data.status && null != data.data && data.data)) {
		plus.nativeUI.toast("获取用户信息失败");
		close();
		return;
	}
	plus.storage.setItem("loginSuccess", 'false');
	for(var i = 0; i < data.data.length; i++) {
		var userInfo = data.data[i].userInfo;
		if(data.data[i].ispublic == 0) {
			plus.storage.setItem("creUrl", data.data[i].creServiceUrl+"/cre");
			plus.storage.setItem("queryUserData", data.data[i]);
			userInfo = typeof(userInfo) == 'string' ? JSON.parse(userInfo) : userInfo;
			if(userInfo && userInfo.commonResponse && userInfo.commonResponse.success) {
				var strId = userInfo.userList[0].id;
				var strName = userInfo.userList[0].name;
				var strWorkNo = userInfo.userList[0].workNo;
				var strOrganizationId = userInfo.userList[0].organizationId;
				var strDescription = userInfo.userList[0].description;
				var strStatus = userInfo.userList[0].status;
				var strTel = userInfo.userList[0].tel;
				var strMail = userInfo.userList[0].mail;
				var strType = userInfo.userList[0].type;
				var strPersonalSpaceFlag = userInfo.userList[0].personalSpaceFlag;
				var strMaxCapacity = userInfo.userList[0].maxCapacity;
				var strIconUrl = userInfo.userList[0].iconUrl;
				var strToken = "TGT-2062-afs5tjadprCLLuIXpvG2xOeJyNSHqxBZVY0YyFfn6rvfGyzpNy-cas";
				plus.storage.setItem("userId", strId);
				plus.storage.setItem("token", "TGT-2062-afs5tjadprCLLuIXpvG2xOeJyNSHqxBZVY0YyFfn6rvfGyzpNy-cas");
				plus.storage.setItem("workNo", strWorkNo);
				plus.storage.setItem("userInfo", JSON.stringify({
					"id": strId,
					"name": strName,
					"workNo": strWorkNo,
					"organizationId": strOrganizationId,
					"description": strDescription,
					"status": strStatus,
					"tel": strTel,
					"mail": strMail,
					"type": strType,
					"token": strToken,
					"personalSpaceFlag": strPersonalSpaceFlag,
					"maxCapacity": strMaxCapacity,
					"iconUrl": strIconUrl
				}));
				plus.storage.setItem("loginSuccess", 'true');
			} else {
				continue;
			}
		} else {
			plus.storage.setItem("caCreUrl", data.data[i].creServiceUrl+"/cre");
			plus.storage.setItem("caQueryUserData", data.data[i]);
			userInfo = typeof(userInfo) == 'string' ? JSON.parse(userInfo) : userInfo;
			if(userInfo && userInfo.commonResponse && userInfo.commonResponse.success) {
				var strId = userInfo.userList[0].id;
				var tenantCode = data.data[i].tenantCode;
				var tenantId = data.data[i].tenantId;
				var strName = userInfo.userList[0].name;
				var strWorkNo = userInfo.userList[0].workNo;
				var strOrganizationId = userInfo.userList[0].organizationId;
				var strDescription = userInfo.userList[0].description;
				var strPassword = userInfo.userList[0].password;
				var strStatus = userInfo.userList[0].status;
				var strTel = userInfo.userList[0].tel;
				var strMail = userInfo.userList[0].mail;
				var strType = userInfo.userList[0].type;
				var strPersonalSpaceFlag = userInfo.userList[0].personalSpaceFlag;
				var strMaxCapacity = userInfo.userList[0].maxCapacity;
				var strIconUrl = userInfo.userList[0].iconUrl;
				plus.storage.setItem("caUserId", strId);
				plus.storage.setItem("caToken", "TGT-2062-afs5tjadprCLLuIXpvG2xOeJyNSHqxBZVY0YyFfn6rvfGyzpNy-cas");
				plus.storage.setItem("caWorkNo", strWorkNo);
				plus.storage.setItem("tenantId", tenantId);
				plus.storage.setItem("caUserInfo", JSON.stringify({
					"id": strId,
					"name": strName,
					'password': strPassword,
					'token': "TGT-2062-afs5tjadprCLLuIXpvG2xOeJyNSHqxBZVY0YyFfn6rvfGyzpNy-cas",
					'tenantName': "大洋测试",
					"workNo": strWorkNo,
					"organizationId": strOrganizationId,
					"description": strDescription,
					"status": strStatus,
					"tel": strTel,
					"mail": strMail,
					"type": strType,
					"tenantId": tenantId,
					"tenantCode": tenantCode,
					"personalSpaceFlag": strPersonalSpaceFlag,
					"maxCapacity": strMaxCapacity,
					"iconUrl": strIconUrl
				}));
				plus.storage.setItem("loginSuccess", 'true');
			} else {
				continue;
			}
		}
	}
	if(plus.storage.getItem("loginSuccess") == "true") {
		window.location.href = appUrl;
	} else {
		plus.nativeUI.toast("登录失败，请稍后尝试");
		close();
	}
}
document.addEventListener("plusready", function() {
	plus.storage.removeItem("userId");
	plus.storage.removeItem("caToken");
	plus.storage.removeItem("caWorkNo");
	plus.storage.removeItem("tenantId");
	plus.storage.removeItem("caUserInfo");
	plus.storage.removeItem("loginSuccess");
	plus.storage.removeItem("token");
	plus.storage.removeItem("workNo");
	plus.storage.removeItem("userInfo");
	plus.storage.removeItem("userId");
	plus.storage.setItem("forCCBN", "true");
	var arguments = plus.runtime.arguments;
	if(arguments == "") {
		plus.nativeUI.toast("参数错误");
		close();
		return;
	}
	arguments = typeof arguments == 'string' ? JSON.parse(arguments) : arguments;
	var userInfo = arguments.userInfo;
	var url = arguments.url;
	var token = analysisUrl(url, "teamToken");
	var urlAppId = analysisUrl(url, "appId");
	var appId = arguments.appId;
	if(token == arguments.teamToken && urlAppId == appId&&userInfo) {
		analysisPortalDataFromPhone(userInfo, url);
	} else {
		login(url);
	}
}, false);

function close() {
	plus.webview.currentWebview().close();
}

function analysisPortalDataFromPhone(data, appUrl) {
	data = (typeof data == 'object') ? data : JSON.parse(data);
	console.log(JSON.stringify(data));
	if(!(true == data.status && null != data.data && data.data)) {
		login(appUrl);
		return;
	}
	plus.storage.setItem("loginSuccess", 'false');
	for(var i = 0; i < data.data.length; i++) {
		var userInfo = data.data[i].userInfo;
		if(data.data[i].ispublic == 0) {
			plus.storage.setItem("creUrl", data.data[i].creServiceUrl+"/cre");
			plus.storage.setItem("queryUserData", data.data[i]);
			userInfo = typeof(userInfo) == 'string' ? JSON.parse(userInfo) : userInfo;
			if(userInfo && userInfo.commonResponse && userInfo.commonResponse.success) {
				var strId = userInfo.userList[0].id;
				var strName = userInfo.userList[0].name;
				var strWorkNo = userInfo.userList[0].workNo;
				var strOrganizationId = userInfo.userList[0].organizationId;
				var strDescription = userInfo.userList[0].description;
				var strStatus = userInfo.userList[0].status;
				var strTel = userInfo.userList[0].tel;
				var strMail = userInfo.userList[0].mail;
				var strType = userInfo.userList[0].type;
				var strPersonalSpaceFlag = userInfo.userList[0].personalSpaceFlag;
				var strMaxCapacity = userInfo.userList[0].maxCapacity;
				var strIconUrl = userInfo.userList[0].iconUrl;
				var strToken = "TGT-2062-afs5tjadprCLLuIXpvG2xOeJyNSHqxBZVY0YyFfn6rvfGyzpNy-cas";
				plus.storage.setItem("userId", strId);
				plus.storage.setItem("token", "TGT-2062-afs5tjadprCLLuIXpvG2xOeJyNSHqxBZVY0YyFfn6rvfGyzpNy-cas");
				plus.storage.setItem("workNo", strWorkNo);
				plus.storage.setItem("userInfo", JSON.stringify({
					"id": strId,
					"name": strName,
					"workNo": strWorkNo,
					"organizationId": strOrganizationId,
					"description": strDescription,
					"status": strStatus,
					"tel": strTel,
					"mail": strMail,
					"type": strType,
					"token": strToken,
					"personalSpaceFlag": strPersonalSpaceFlag,
					"maxCapacity": strMaxCapacity,
					"iconUrl": strIconUrl
				}));
				plus.storage.setItem("loginSuccess", 'true');
			} else {
				continue;
			}
		} else {
			plus.storage.setItem("caCreUrl", data.data[i].creServiceUrl+"/cre");
			plus.storage.setItem("caQueryUserData", data.data[i]);
			userInfo = typeof(userInfo) == 'string' ? JSON.parse(userInfo) : userInfo;
			if(userInfo && userInfo.commonResponse && userInfo.commonResponse.success) {
				var strId = userInfo.userList[0].id;
				var tenantCode = data.data[i].tenantCode;
				var tenantId = data.data[i].tenantId;
				var strName = userInfo.userList[0].name;
				var strWorkNo = userInfo.userList[0].workNo;
				var strOrganizationId = userInfo.userList[0].organizationId;
				var strDescription = userInfo.userList[0].description;
				var strPassword = userInfo.userList[0].password;
				var strStatus = userInfo.userList[0].status;
				var strTel = userInfo.userList[0].tel;
				var strMail = userInfo.userList[0].mail;
				var strType = userInfo.userList[0].type;
				var strPersonalSpaceFlag = userInfo.userList[0].personalSpaceFlag;
				var strMaxCapacity = userInfo.userList[0].maxCapacity;
				var strIconUrl = userInfo.userList[0].iconUrl;
				plus.storage.setItem("caUserId", strId);
				plus.storage.setItem("caToken", "TGT-2062-afs5tjadprCLLuIXpvG2xOeJyNSHqxBZVY0YyFfn6rvfGyzpNy-cas");
				plus.storage.setItem("caWorkNo", strWorkNo);
				plus.storage.setItem("tenantId", tenantId);
				plus.storage.setItem("caUserInfo", JSON.stringify({
					"id": strId,
					"name": strName,
					'password': strPassword,
					'token': "TGT-2062-afs5tjadprCLLuIXpvG2xOeJyNSHqxBZVY0YyFfn6rvfGyzpNy-cas",
					'tenantName': "大洋测试",
					"workNo": strWorkNo,
					"organizationId": strOrganizationId,
					"description": strDescription,
					"status": strStatus,
					"tel": strTel,
					"mail": strMail,
					"type": strType,
					"tenantId": tenantId,
					"tenantCode": tenantCode,
					"personalSpaceFlag": strPersonalSpaceFlag,
					"maxCapacity": strMaxCapacity,
					"iconUrl": strIconUrl
				}));
				plus.storage.setItem("loginSuccess", 'true');
			} else {
				continue;
			}
		}
	}
	if(plus.storage.getItem("loginSuccess") == "true") {
		window.location.href = appUrl;
	} else {
		login(appUrl);
	}
}
/**
 * Created by Administrator on 2016/8/2.
 */
function setSize(){
	var size = $(window).width();
	var baseFontSize = 100*size/640;
	if(baseFontSize>70){baseFontSize=70;}
	document.documentElement.style.fontSize = baseFontSize  +  "px";
}
var resizeEvt = 'orientationchange' in window ? 'orientationchange' : 'resize';
window.addEventListener(resizeEvt, setSize, false);
$(function(){
	setSize();
})

jQuery.fn.extend({	
	/*单次触摸事件*/
	tap:function(fn, data){
		this.each(function(){ //this是jquery对象
			var fnn = fn.bind(this);
			this.tap = {};
			this.tap.fnn = fnn; //将fnn存起来
			this.tap.touchStart = function(e){
			  var touches = e.touches[0];
			  startTx = touches.clientX;
			  startTy = touches.clientY;
			  if(typeof data == "boolean" && data){
			  	console.log(this);
			  }else if(typeof data == "function"){
			  	data.call(this, e);
			  }
			};
			this.tap.touchEnd = function(e){
			  var touches = e.changedTouches[0],
			  endTx = touches.clientX,
			  endTy = touches.clientY;
			  if(typeof data !== "boolean"){
			  	 e.data = data;
			  }
			  // 在部分设备上 touch 事件比较灵敏，导致按下和松开手指时的事件坐标会出现一点点变化
			  if( Math.abs(startTx - endTx) < 6 && Math.abs(startTy - endTy) < 6 ){
				fnn(e);
			  }
			};
			var startTx, startTy;
			this.addEventListener('touchstart',this.tap.touchStart, false );
			
			this.addEventListener('touchend',this.tap.touchEnd, false );
		});
	},
	/*滑屏事件*/
	swipe:function(fn, fmove, fstart){
		this.each(function(){
			var isTouchMove, startTx, startTy, startTime, oldX, oldY, speedX, speedY;
			var data = {};
			var fnn = typeof fn == "function" ? fn.bind(this): false;
			var fstartn = typeof fstart == "function" ? fstart.bind(this) : false;
			var fmoven = typeof fmove == "function" ? fmove.bind(this) : false;
			
			this.addEventListener( 'touchstart', function( e ){
			  var touches = e.touches[0];
			  e.sx = oldX = startTx = touches.clientX;
			  e.sy = oldY = startTy = touches.clientY;
			  e.data = data;
			  e.st = startTime = new Date();
			  fstartn && fstartn(e);
			  isTouchMove = false;
			}, false );
			
			if(typeof fmove == "function"){
				this.addEventListener( 'touchmove', function( e ){
				  var touches = e.touches[0];
				  isTouchMove = true;
				  e.x = touches.clientX;
			 	  e.y = touches.clientY;
			 	  speedX = e.x - oldX;
			 	  speedY = e.y - oldY;
			 	  oldX = e.x; oldY = e.y;
			 	  var distanceX = e.x - startTx,
				  distanceY = e.y - startTy;
				  e.dx = distanceX;
				  e.dy = distanceY; 
				  e.timeDis = new Date() - startTime;
				  e.data = data;
				  fmoven(e);
				  e.preventDefault();
				}, false );
			}else{
				this.addEventListener( 'touchmove', function( e ){
				  isTouchMove = true;
				  e.preventDefault();
				}, false );
			}
			
			this.addEventListener( 'touchend', function( e ){
			  if( !isTouchMove ){
				return;
			  }
			  var touches = e.changedTouches[0],
				endTx = touches.clientX,
				endTy = touches.clientY,
				distanceX = endTx - startTx,
				distanceY = endTy - startTy,				
				isSwipe = false;
				e.dx = distanceX;
				e.dy = distanceY;
				e.speedX = speedX;
				e.speedY = speedY;
				e.data = data;
				e.timeDis = new Date() - startTime;
//			  if( Math.abs(distanceX)>20||Math.abs(distanceY)>20 ){
				fnn && fnn(e);
//			  }
			}, false );
		});
	}
});
/************************************************ 单个函数  ****************************************************/
function formatStr(str, num){ //将一个字符串用空格每隔 num 位分隔一次
	var result = "";
	for(var i=0, len=str.length; i<len; i++){
		if(i % num == 0 && i!== 0){
			result += (" " + str.charAt(i));
		}else{
			result += str.charAt(i);
		}
	}
	return result;
}
/*start*********** 跳转 ************/
function clickF(options){ //用于跳转
	for(var prop in options){
		$(prop).tap(function(){
			window.location.href = options[prop];
		});
	}
}

function clickAuto(){ //用于替换a标签的跳转
	$("a[data-href]").tap(function(){
		window.location.href = $(this).attr("data-href");
	});
}
clickAuto();

function clickFF(selector, self){
	selector = selector || "";
	if(typeof selector == "object"){
		console.log($(selector));
		console.log($(selector).find("a[data-hreff]:not([hasadd])"));
		if(self == "self"){
			$(selector).tap(function(e){
				window.location.href = $(this).attr("data-hreff");
			});
			$(selector).attr("hasadd","true");
		}else{
			$(selector).find("a[data-hreff]:not([hasadd])").tap(function(e){
				window.location.href = $(this).attr("data-hreff");
			});
			$(selector).find("a[data-hreff]:not([hasadd])").attr("hasadd","true");
		}
	}else{
		$(selector + "a[data-hreff]:not([hasadd])").tap(function(e){
			window.location.href = $(this).attr("data-hreff");
		});
		$(selector + "a[data-hreff]:not([hasadd])").attr("hasadd","true");
	}
	
} 

function addClick(options){
	for(var prop in options){
		$(prop).tap(function(e){
			window.location.href = e.data;
		},options[prop]);
	}
}
/*end*********** 跳转 ************/
/*start************ 常量 **********************/
var GLOBAL = {};
function addGlobal(name,value){
	GLOBAL[name] = value;
}
function changeGlobal(name,value){
	GLOBAL[name] = value;
}
function removeGlobal(name){
	delete GLOBAL[name];
}
function getGlobal(name){
	return GLOBAL[name];
}

var reg = {
	PWD:/(?!^(\d+|[a-zA-Z]+|[~!@#$%^&*?]+)$)^[\w~!@#$%\^&*?]{8,16}$/,//密码格式
	PHONE:/^(1)\d{10}$/,//手机号格式
	PHONE_CARD:/^\d{10,}$/,//手机号/会员卡号格式
	VCODE:/^[a-zA-Z0-9]{6}$/,//验证码格式
	CARD:/^\d{10,}$/ //待激活的会员卡号格式
}
serverPhone = "400-1369-400";
serverTime = "(9:00 - 18:00)";
$(".server-phone").html(serverPhone);
$(".server-time").html(serverTime);
$(".alert-callServer>.btn-box>.fr").attr("href", "tel:"+serverPhone.replace("-",""));
/*end************ 常量 **********************/
/*start************ 头部 返回 **********************/
$(".backBtn").tap(function(){
    window.history.back();
});
$(".clearBackBtn").tap(function(){//"返回"按钮
	markPop("fromUrl");
	window.history.back();
});
$(".closeBtn img").tap(function(){
	backFunc();
});
function backFunc(defaultUrl){//头部的关闭按钮
	defaultUrl = defaultUrl || "mobile/pages/index.jsp";
	var url = markPop("fromUrl");
	if(url){
		window.location.replace(url);
	}else{
		window.location.href = defaultUrl;
	}
}
/*end************ 头部 返回 **********************/
/*start****** 所有输入框不为空添加类 *********/
function subButton(){
	var num = $(".input-test").length;//所有输入框个数
	for(var i=0;i<num;i++){
		$(".input-test")[i].onkeyup = function(){//每个输入框添加事件
			inputNULL();
		}
		$(".input-test").blur(inputNULL);
	}
}
subButton();
function inputNULL(){
	if($(".input-test").length == 0){
		return;
	}
	var checkResult = true;	
	$(".input-test").each(function(){//遍历每个输入框的val值
		var val = $.trim($(this).val());
		if(null == val || val == ""){
			checkResult = false;
		}
	});
	if(checkResult)	{
		$(".sub-but").addClass("sub-but-active");
	}else {
		$(".sub-but").removeClass("sub-but-active");
	}
}
inputNULL();
/*end****** 所有输入框不为空添加类 *********/
function codeWord(showMsg){//验证码封装
	if(!getGlobal("canSendVcode")){return;}
	(getGlobal("haveSendVcode") === undefined || getGlobal("haveSendVcode")===false) && addGlobal("haveSendVcode",true);
	var TIME;
	var txt =$.trim($(".codeWord").text());//获取按钮内部的值
	if(txt == "获取验证码"||txt == "重新发送"){
		$(".codeWord").html(showMsg);
		var time = parseFloat($("#codeTime").text());
		var TIME = setInterval(function(){
				time--
				$("#codeTime").text(time);
				if($("#codeTime").text() =="0"){
					$(".codeWord").text("重新发送");
					changeGlobal("canSendVcode",true);
					changeGlobal("haveSendVcode",false);
					clearInterval(TIME);					
				}
		},1000);	
	}else{
		changeGlobal("canSendVcode",false);
		addGlobal("haveSendVcode",true);
	}
}
/*start************ 标记 **********************/
function mark(name,value){
	try{
		window.localStorage;
		if(window.localStorage){
			localStorage.setItem(name,JSON.stringify(value));
		}
	}catch(e){
		
	}
	
}
function getMark(name){
	try{
		if(window.localStorage){
			return JSON.parse(localStorage.getItem(name));
		}else{
			return "mobile/pages/index.jsp";
		}
	}catch(e){
		return "mobile/pages/index.jsp";
	}
	
}
function removeMark(name){
	try{
		if(window.localStorage){
			localStorage.removeItem(name);
		}
	}catch(e){}
	
}
function markPush(name,val){
	var value = getMark(name);
	if(value){
		if(value.push){
			value.push(val);
		}
		mark(name,value)
	}else{
		try{
			if(window.localStorage){
				var value = [];
				value.push(val);
			}
		}catch(e){}
		mark(name,value);
	}
}
function markPop(name){
	var value = getMark(name);
	if(value){
		if(!value.pop){ return value; }
		var result =  value.pop();
		if(value.length == 0){
			removeMark(name);
		}else{
			mark(name,value);
		}
		return result;
	}
	return value;
}
/*end************ 标记 **********************/
/*start************ 验证 **********************/
var valiSelector =function(selector,reg,success,failure){//验证input元素的val是否符合reg
	return vali($(selector).val() ,reg,success,failure);
}
var vali = function(content,reg,success,failure){//验证content是否符合reg
	content = $.trim(content);
   if(reg === ""){
       if(content == ""){
           success(content);
           return true;
       }else{
           failure(content);
           return false;
       }
   }else{
       if(reg.exec(content) == null){
           failure(content);
           return false;
       }else{
           success(content);
           return true;
       }
   }
};

function satisfy(bool,message){//如果bool为真，就提示信息，并返回false
    bool && alertAuto.alert(message);
    return !bool;
}
function require(selector,message){//不能为空的验证,selector为input元素的元素器
    var state = null;
    var value = $.trim($(selector).val());

    vali(value,"",function(){
        message && alertAuto.alert(message);
        state = false;
    },function(){
        state = true;
    });
    return state;
}


$(".pass-img>img").tap(function(){//密码输入框右侧的眼睛按钮
    var input = $(this).prev("input");
    input.focus();//使输入框的背景色变白
    var value = input.val();
    if(input.attr("type") == "password"){
        input.attr("type","text");
        $(this).attr("src","mobile/images/register/hiding.png");
    }else if(input.attr("type") == "text"){
        input.attr("type","password");
        $(this).attr("src","mobile/images/register/showing.png");
    }
});

var options = {};
function check_phoneHave(options){
	options = options || {};
	selector = options.selector;
	haveMsg = options.haveMsg;
	notHaveMsg = options.notHaveMsg;
	successf = options.successf;
	failuref = options.failuref;
	var async = options.async || false;//默认为同步 
	
	var name = selector.substr(1);
	addGlobal(name,false)
	var phone = $.trim($(selector).val())
	$.ajax({ 
        url: "check/phoneExist",
        type: "GET",
        dataType: "json",
        data: {
        	"phone": phone
        },
        async: async,
        success: function(data) {
          if(data['result']=="false")
    	  {  
        	 notHaveMsg && alertAuto.alert(notHaveMsg);//用户名不存在
        	 successf && successf();
        	 
        	  return changeGlobal(name,true);
    	  }
          else if(data['result']=="true")
          {
			  haveMsg && alertAuto.alert(haveMsg);//手机号已注册
			  failuref && failuref();
        	  return changeGlobal(name,false);
          }
        },
        error: function() {
          alertAuto.alert("系统错误");
        }
    });  	
}
function sendCode(options,bool){
	bool = bool || true;
	if(bool){
		var options = options || {};
		var selector = options.selector;
		var selectorShow = options.selectorShow;
		var saveCode = options.saveCode;//将验证码存起来
		var successf = options.successf;
		var async = options.async || false;//默认为同步 
		var name = selector.substr(1);
		addGlobal(name,false);
		addGlobal("isExpire",false);
		var phone = $.trim($(selector).val());
		$.ajax({
	        url: "check/phoneCode",
	        type: "POST",
	        dataType: "json",
	        data: {
	        	"phone": phone
	        },
	        async: async,
	        success: function(data) {
	          selectorShow && $(selectorShow).val(data['message']);
	          saveCode && addGlobal("vCode", data['message']);
	          successf && successf(data['message']);
	          setTimeout(function(){
	          	addGlobal("isExpire",true);
	          },60000);
	          changeGlobal(name,true)
	        },
	        error: function() {
	          alertAuto.alert("系统错误"); 
	          changeGlobal(name,false)
	        }
      });
	}else{
		return;
	}
}
/*end************ 验证 **********************/
/**start****  数量增减1 *******/
function sub(box){//传入.change-num-box类的jquery对象
	var value = parseInt(box.children("input").val());
	if(value == 1){		
		return;
	}else{
		if(value == 2){
			box.children(".sub").css("background-image",'url("static/mobile/default/mall/images/shopping/subNotUse.png")');
		}
		box.children("input").val(value-1);
	}
}
function add(box){//增加一个
	var value = parseInt(box.children("input").val());
	if(value + 1 == 2){
		box.children(".sub").css("background-image",'url("static/mobile/default/mall/images/shopping/subCanUse.png")');
	}	
	box.children("input").val(value+1);
}
function isCanSub(box){
	var value = parseInt(box.children("input").val());
	if(value == 1){
		box.children(".sub").css("background-image",'url("static/mobile/default/mall/images/shopping/subNotUse.png")');
	}
}
function valiNumBox(self){//当input元素的change事件发生时，验证其value值;
	var value = $(self).val();
	vali(value,/^[0-9]{1,3}$/,function(){},function(){ 
		$(self).val(1);
		alertAuto.alert("数量必须是1-3位数字"); 
	}) 
	&& vali(value,/^[0]{1,3}$/,function(){
		 $(self).val(1);
		alertAuto.alert("数量最小为1");
	},function(){});
	
	if(value == 1){
		$(self).parent().children(".sub").css("background-image",'url("static/mobile/default/mall/images/shopping/subNotUse.png")');
	}else{
		$(self).parent().children(".sub").css("background-image",'url("static/mobile/default/mall/images/shopping/subCanUse.png")');
	}
}
function format(str,n){//保留n位小数
	return parseFloat(str).toFixed(n);
}
/*end*****  数量增减1 *******/
function toTop(){//回到顶部
	var height = $(window).height();
	var scrollTop = $(window).scrollTop();
	if(scrollTop > height){
		$(".to-top").fadeIn(200);
	}else{
		$(".to-top").fadeOut(200);
	}
}
function toTopFunc(){//回到顶部事件
	$(window).scrollTop(0);
}
$(".goCart").tap(function(){//去购物车
	window.location.href = "cart/mycart";	
});
$(".mask").tap(function(){
	alertHide();
});
function alertHide(){
	$(".mask").fadeOut(100);
	$(".alert").fadeOut(100);
}
$(".goGoodsIndex").tap(function(){//回到内部主页
	window.location.href = "goodsMain";
});

function filter(obj, temp){//用于过滤掉下架的商品，和设置是否显示商品售罄
	var result = {};
	if(obj.shelves !== 0){//如果没下架
		if(obj.shelves == 2){//售罄
			result.temp = temp.replace("@{allSaleDiv}",'<div class="allSale">售 罄<img class="sale-divider" src="/mall/static/mobile/default/mall/images/goodsMain/sale_divider.png"/></div>');
		}else{
			result.temp = temp.replace("@{allSaleDiv}","");
		}
		result.filter =  true;
	}else{
		result.filter =  false;
	}
	return result;
}
/****************************************** 模块  ****************************************************/
/*start************  **********************/

function formatImg(parentSelector){
	parentSelector = parentSelector || "";
	var obj = null;
	if(typeof parentSelector =="object"){
		if(parentSelector.nodeType){ parentSelector = $(parentSelector); }
		obj = parentSelector.find(".img-box img");
	}else{
		obj = $(parentSelector + " .img-box img");
	}
	console.log(obj);
	obj.load(function(){
		var width  = $(this).width(),
		    height  = $(this).height();
		console.log(this, width, height);
		if(width > height){
			$(this).css({"width": "100%", "height": "auto"});
			
		}else if(width < height){
			$(this).css("padding-top", "0.2rem");
			height  = parseInt($(this).css("height"));
			if(width > height){
				$(this).css({"width": "100%", "height": "auto", "padding-top": "0.2rem"});
			}else{
				$(this).css({"width": "auto", "height": "100%", "padding-top": "0.2rem"});
			}
		}else{
			$(this).parent().css("vertical-align", "top");
		}
	});
}
function formatImgHasLoad(parentSelector){
	parentSelector = parentSelector || "";
	var obj = null;
	if(typeof parentSelector =="object"){
		if(parentSelector.nodeType){ parentSelector = $(parentSelector); }
		obj = parentSelector.find(".img-box img");
	}else{
		obj = $(parentSelector + " .img-box img");
	}
	//console.log(obj);
	obj.each(function(){
		var width  = $(this).width(),
		    height  = $(this).height();
		//console.log(this, width, height);
		if(width > height){
			$(this).css({"width": "100%", "height": "auto"});
		}else if(width < height){
			$(this).css("padding-top", "0.2rem");
			height  = parseInt($(this).css("height"));
			if(width > height){
				$(this).css({"width": "100%", "height": "auto", "padding-top": "0.2rem"});
			}else{
				$(this).css({"width": "auto", "height": "100%", "padding-top": "0.2rem"});
			}
		}else{
			$(this).parent().css("vertical-align", "top");
		}
	});
}

/*end************  **********************/

/*start************ 初始化对象 **********************/

function Init(optionsAll){ //初始化对象类
	this.initObj = optionsAll.initObj; //需要初始化的对象
	
	this.innerAttrs = optionsAll.innerAttrs; //这是initObj内部使用的属性
	this.innerAttrs && this.setAll(this.innerAttrs); //进行初始化
	
	this.config = optionsAll.config; //这是initObj可以从外部传入参数进行配置的属性
	this.config && this.setAll(this.config); //进行初始化
}

Init.prototype.init = function(options){ //初始化配置(含有默认值)
	if(!options){ return; }
	for(var prop in this.config){
		if(options[prop] == undefined){
			if( /mustHave/.test( this.config[prop] ) ){ //这样的值是必须进行外部传入参数初始化的
				throw Error(prop + ": 必须初始化。");
			}
		}else{
			if(/mustHaveBind/.test( this.config[prop] )){ //如果config的值是这样，就进行事件绑定this
				this.initObj[prop] = options[prop].bind(this.initObj);
			}else{
				this.initObj[prop] = options[prop];
			}
		}
	}
}

Init.prototype.set = function(options){ //根据 options 更改 initObj的属性值，不可以更改内部属性
	if(!options){ return; }
	for(var prop in options){
		if(prop in this.config){ //如果是可配置的属性，就进行配置
			this.initObj[prop] = options[prop];
		}
	}
}

Init.prototype.setAll = function(options){ //根据 options 更改 initObj的属性值，可以更改所有属性
	if(!options){ return; }
	for(var prop in options){
		this.initObj[prop] = options[prop];
	}
}

Init.prototype.format = function(options, def){ //根据 options 更改 initObj的属性值，可以更改所有属性
	if(!options){ return def; }
	for(var prop in def){
		options[prop] = def[prop];
	}
	return options;
}
Init.format = function(options, def){ //根据 options 更改 initObj的属性值，可以更改所有属性
	if(!options){ return def; }
	for(var prop in def){
		options[prop] = def[prop];
	}
	return options;
}
/*end************ 初始化对象 **********************/

/*start************ 信息提示框 **********************/

function ShowAlertAuto(config){
	this.init(config);
}

ShowAlertAuto.prototype.init = function(config){	
	this.initObject = new Init({
		initObj:this,
		innerAttrs:{
			wWidth:$(window).width()
		},
		config:{
			showTime:3000, //弹出后的显示时间，默认3s
			fadeTime:200, //淡入和淡出时间
			lrDis:10,//弹出框距离窗体左右边距的最小值
			selector:".x-alert-message" //弹出框的选择器
		}
	});
	config && this.initObject.init(config); //用外部参数进行配置
};

ShowAlertAuto.prototype.set = function(options){ //更改属性
	this.initObject.set(options);
}

ShowAlertAuto.prototype.alert = function(message, showTime){ //显示信息提示框[message,seconds]
	var showT = showTime == undefined ? this.showTime : showTime;
	var selector = this.selector, self = this;
    this.create(message);
   	$(selector).css("margin-left", - this.wWidth); //注意这个必须有，否则会不对
    setTimeout(function(){
        self.hide();               
    },showT);
 
    $(selector)[0].style.width = "auto";
    var alertWidth = parseInt($(selector).width()); //弹出框的宽度 
    var paddingLeft = parseInt($(selector).css("padding-left"));
    var paddingTop = parseInt($(selector).css("padding-top"));
    if(alertWidth - 2*paddingLeft - this.lrDis > this.wWidth){ //如果弹出框太宽，就将弹出框的宽度设小一点，保证完全在窗口中显示
    	alertWidth = this.wWidth - 2*paddingLeft - 2*this.lrDis; 
    	$(selector).css("width",alertWidth);
    }
    var alertHeight = parseInt($(selector).height()); //弹出框的高度 (if语句可能会改变高度，所以要在这个位置获取)
    $(selector).css("margin-left",-alertWidth/2- paddingLeft ).css("margin-top",-alertHeight/2-paddingTop);
    this.show();
};

ShowAlertAuto.prototype.create = function(message){
    if(!$(this.selector)[0]){
        message = message || "";
       $('<div class="' + this.selector.replace(".","") + '">' + message + '</div>').appendTo("body");
    }else{
    	$(this.selector).html(message);
    }
};

ShowAlertAuto.prototype.show = function(){//显示信息提示框
    $(this.selector).fadeIn(this.fadeTime);
};

ShowAlertAuto.prototype.hide = function(){//隐藏信息提示框
    $(this.selector).fadeOut(this.fadeTime);
};
var alertAuto = new ShowAlertAuto(); //创建一个对象
/*end************ 信息提示框 **********************/

/*start************ 验证 **********************/
function Validate(config){
	this.init(config);
}

Validate.prototype.init = function(config){	
	this.initObject = new Init({
		initObj:this,
		innerAttrs:{
			reg: reg,
			result:true,//验证的结果
		}
	});
	config && this.initObject.init(config); //用外部参数进行配置
};

Validate.prototype.set = function(options){ //更改属性
	this.initObject.set(options);
}
Validate.prototype.getResult = function(){ //获取验证结果
	var result = this.result;
	this.result = true;
	return result;
}
Validate.prototype.valiSelector = function(selector, reg, success, failure){//验证content是否符合reg
	if(!this.result){ return this; }
	var value = this.getValue(selector);
	if(value == undefined){ return; }
	this.vali(value, reg, success, failure);
    return this;
};

Validate.prototype.vali = function(value, reg, success, failure){//验证content是否符合reg
	if(!this.result){ return this; }
	value = $.trim(value);
	var result = reg === "" ? value === "" : reg.exec(value) !== null;
    result ? success && success.call(this, value) : failure && failure.call(this, value);
    this.result = this.result && result;
    return this;
};

Validate.prototype.require = function (selector, message){//不能为空的验证,selector为input元素的元素器
	if(!this.result){ return this; }
	var value = this.getValue(selector);
	if(value == undefined){ return; }
	var result = $.trim(value) === "";
	result && message && this.showMsg(message);
	this.result = this.result && !result;
    return this;
}
Validate.prototype.satisfy = function (bool, message){//如果bool为真，就提示信息，并返回false
	if(!this.result){ return this; }
    bool && message && this.showMsg(message);
    this.result = this.result && !bool;
    return this;
}
Validate.prototype.showMsg = function(message){
	alertAuto.alert(message);
}

Validate.prototype.getValue = function(selector){
	if(!selector){ throw Error(selector + " 为空"); }
	if(typeof selector == "function"){ return selector(); } //如果是一个函数，就用函数的返回值
	if(typeof selector == "string" || selector.nodeName){ //如果是一个字符串 或 DOM元素
		var elem = $(selector)[0]; //将其中的DOM元素选择出来
		if(elem){ 
			return elem.nodeName == "INPUT" ? $(selector).val() : $(selector).html(); //返回valu或html值
		}
	}
}

/*end************ 验证 **********************/

var CountN = 1; // 总抽奖次数
var lottery = {
	rotateSelector : "#rotate", // 旋转对象的选择器（转盘）
	pointerSelector : "#pointer", // 中间的指针
	scale : 10000, // 缩放倍数
	hasRotateDeg : 0, // 临时变量
//	isCanRotate : true,
//	isDisabled : false,
	maxN : 4, // 每次至多旋转的圈数
	minN : 2, // 每次至少旋转的圈数
	time : 1, // 旋转一次的时间
	// maxCount : 1, // 最多抽奖2次
	// count : 1, // 已经抽了的几次了
	direction : true, // true表示 顺时针方向旋转转盘

	// 图片为正方形
	// 第一个要有一个开始角度， 从最高处逆时针旋转取正值,每一块都必须有一个不为0的概率
	params : [ {
		name : "一等奖",
		startDeg : 14.8,
		endDeg : 0,
		size : 20,
		posibility : 0.02
	}, {
		name : "三等奖",
		startDeg : 0,
		endDeg : 0,
		size : 49.6,
		posibility : 0.15
	}, {
		name : "四等奖",
		startDeg : 0,
		endDeg : 0,
		size : 69,
		posibility : 0.2
	}, {
		name : "五等奖",
		startDeg : 0,
		endDeg : 0,
		size : 87.7,
		posibility : 0.25
	}, {
		name : "二等奖",
		startDeg : 0,
		endDeg : 0,
		size : 30,
		posibility : 0.08
	}, {
		name : "六等奖",
		startDeg : 0,
		endDeg : 0,
		size : 104.7,
		posibility : 0.3
	}, ],

	init : function() {
		var self = this;

		function setSize() { // 根据窗口的大小来设置转盘的大小
			var width = $(self.rotateSelector).width(), height = $(
					self.rotateSelector).height();
			var rate = ($(window).width() * 0.8) / width;
			$(self.rotateSelector).parent().height(rate * width);
		}
		setSize(); // 页面一加载就设置一次
		$(window).resize(setSize); // 当改变窗口大小时，转盘大小也随之改变
	},
	
	//转盘初始化
	dateInit : function(){  
		var allDeg = 0, // 所有扇形的角度和，应该等于360
		allPosibility = 0; // 所有扇形的概率和，应该等于1
		for (var i = 0; i < this.params.length; i++) {
			var item = this.params[i];
			if (i == 0) {
				item.endDeg = item.startDeg + item.size;
			} else {
				item.startDeg = this.params[i - 1].endDeg;
				item.endDeg = item.startDeg + item.size; // 根据角度大小和起始角度，计算
			}
			allPosibility += item.posibility;
			item.max = allPosibility * this.scale; // 增加一个属性，表示区间的最大值
			allDeg += this.params[i].size;
		}
	},
	
	//调用转盘初始化和后台数据，生成奖项
	start : function(data) {
		this.params = data;
		this.dateInit();
	},
	
	lottery : function(beforeStartCallback) {
		// if(!this.isCanRotate || this.isDisabled){ return this; }
		// //如果抽奖被禁用或正在抽奖过程中
		// if(this.count > this.maxCount){ this.end(this); this.isCanRotate =
		// false; ; return this; } //如果抽奖次数用完
		if (typeof beforeStartCallback == "function") {
			beforeStartCallback(this);
		}
		this.isCanRotate = false;

		this.level = this.getLotteryLevel(); // 进行抽奖(注意，页面还没有任何变化)，现在已经知道自己抽中了几等奖了
		var toDeg = this.getRandom(this.level.startDeg, this.level.endDeg, 5); // 计算转盘应该旋转的角度
		var n = Math.floor(Math.random() * (this.maxN - this.minN + 1)
				+ this.minN); // 随机产生应该转的圈数

		this.time = Math.log2(n + 1) * 0.8; // 本次旋转所需的时间
		$(this.rotateSelector).css("transition-duration", this.time + "s"); // 设置时间

		if (this.direction) { // 顺时针
			this.hasRotateDeg += (toDeg + n * 360); // 计算加上圈数后的旋转角度
			this.rotate(this.hasRotateDeg); // 进行旋转
			this.hasRotateDeg += 360 - toDeg;
		} else {
			this.hasRotateDeg += (toDeg - n * 360) - 360;
			this.rotate(this.hasRotateDeg);
			this.hasRotateDeg -= toDeg;
		}
		return this;
	},

	// 产生一个随机数， 如：getRandom(10, 70, 10) 则产生的随机数范围： 20 ~ 60
	getRandom : function(start, end, offset) { // start + offset <= x <= end -
		// offset
		// console.log("start: " + start + ", end: " + end);
		offset = offset || 0;
		var random = Math.random() * (end - start + 1) + start;
		if (random >= end - offset || random <= start + offset) {
			return random + (random >= end - offset ? (-offset) : offset);
		} else {
			return random;
		}
	},
	
	// 随机产生一个奖
	getLotteryLevel : function() { 
		var randomDig = Math.random() * this.scale; // 产生一个 0 ~ this.Scale的随机数
		// 比如 500.2388392

		for (var i = 0; i < this.params.length; i++) {
			if (randomDig < this.params[i].max) { // 判断500.2388392在哪一个奖项的范围
				return this.params[i]; // 返回抽中的奖
			}
		}

	},
	
	// 旋转到指定角度
	rotate : function(toDeg) {
		$(this.rotateSelector).css("transform", "rotate(" + toDeg + "deg)");
	},

	//中奖奖项显示出来
	showBtn : function() {
		var $dateResult = this.level.name;
		if ('红包一' == $dateResult) {
			// 请求后台发红包
			$.ajax({
				url : "/Joffro/front/wxpay/index",
				type : "POST",
				success : function(data) {
					$('.money>h2').html('¥&nbsp;&nbsp' + data.money);
				},
				error : function() {
					alertAuto.alert("提交失败！");
				}
			});
			$('#hongbao2').css('display', 'block');
		} else if ('红包二' == $dateResult) {
			// 请求后台发红包
			$.ajax({
				url : "/Joffro/front/wxpay/index",
				type : "POST",
				success : function(data) {
					$('.money>h2').html('¥&nbsp;&nbsp' + data.money);
				},
				error : function() {
					alertAuto.alert("提交失败！");
				}
			});
			$('#hongbao5').css('display', 'block');
		} else if ('红包三' == $dateResult) {
			// 请求后台发红包
			$.ajax({
				url : "/Joffro/front/wxpay/index",
				type : "POST",
				success : function(data) {
					$('.money>h2').html('¥&nbsp;&nbsp' + data.money);
				},
				error : function() {
					alertAuto.alert("提交失败！");
				}
			});
			$('#hongbao7').css('display', 'block');
		} else if ('年画' == $dateResult) {
			$('#nianhua').css('display', 'block');
		} else if ('iphone7' == $dateResult) {
			$('#iphone7').css('display', 'block');
		} else if ('五粮液' == $dateResult) {
			$('#wulye').css('display', 'block');
		}
	},

	// 一次抽奖完成后的回调
	success : function(callback) { // 旋转完成后的回调函数
//		this.count++;
		var self = this;
		setTimeout(function() { // 要等到抽奖旋转停止后，才执行回调函数
			if (typeof callback == "function") {
				callback(self.level);
			}
			self.isCanRotate = true;
		}, this.time * 1000 + 20);
		return this;
	},
	
	//抽奖次数用完方法
	setEndFunc : function(callback) { //
		this.end = callback;
		return this;
	}
}

//手机号码界面验证部分
$(function() {
	lottery.init();
	var submitIp = $("#submit>p:first-of-type>input");
	var isture = false;
	submitIp.blur(function() {
		var patrn = /^1[34578]\d{9}$/;
		var phone = $("#submit>p:first-of-type>input").val().trim();
		if (phone == "") {
			$(this).after("<h3 class=redClass>*号码不能为空*</h3>");
		} else {
			if (!patrn.test(phone)) {
				$(this).after("<h3 class=redClass>*请输入正确的手机号码*</h3>");
			} else {
				isture = true;
			}
		}
	}).focus(function() {
		$(this).nextAll().remove();
	});
	var submitBtn = $('#submit>h2');
	$(submitBtn).click(function() {
		var phone = $("#submit>p>input").val().trim();
		if (isture == false) {
			alert("请输入正确的手机号码");
		} else {
			var phone = $('#submit>p>input').val().trim();
			$.ajax({
				type : "GET",
				url : "/Joffro/saveByUser",
				async : false,
				dataType : "json",
				data : {
					"phone" : phone
				},
				success : function(data) {
					if (data) {
						$('#submit').fadeOut(200);
						//开始
					} else {
						alert("已抽过或未录入！");

					}
				},
				error : function() {
					alert("后台出错了，谢谢！");
				}
			});
			
			$.ajax({
				url : "/Joffro/wining/send2",
				type : "POST",
				success : function(data) {
					lottery.start(data.result);
					lottery.isDisabled = data.isDisabled;
				},
				error : function() {
					alertAuto.alert("提交失败！");
				}
			});
	
			$(".start").tap(function() { // 开始抽奖按钮
				startLottery(); // 进行抽奖
				$('.start').css('display', 'none');
				$(".pointer").fadeTo(20, 1);
			}, function() {
				$(".pointer").fadeTo(20, 0.8);
			});
		}
	});
});

// 进行抽奖
function startLottery() {
	lottery.setEndFunc(function() {
//		alertAuto.alert("次数用完");
	})

	.lottery(function() {

	})

	.success(function(date) { // 抽奖完成后
		lottery.showBtn();
		$.ajax({
			url : "/Joffro/shareOver",
			type : "POST",
			dataType : "json",
			data : {
				prize : date.name
			// 向后台说明本次抽奖的结果
			},
			success:function(data){
				CountN--;
				if(CountN <= 0){
					$('.start').css('display', 'none');
				}else{
					$('.start').css('display', 'block');
				};
//				lottery.isDisabled = !data.isCanNext; // 设置是否还能再次抽奖s
				alertAuto.alert(data.message);
			},
			error : function() {
				alertAuto.alert("提交失败！");
			}

		});
	});
}

//关闭图标
var $closeIcon = $('.closeIcon>h1>img')
$($closeIcon).click(function() {
	$closeIcon.parents('.closeIcon').fadeOut(100);
})

var SYSCONFIG={
		title:'数据服务-个人中心',
		apihost:'http://172.16.35.50:8080/bus/',
		token:$.cookie('token'),
		username:'欢迎你，'+$.cookie('username'),
		apiurl:function(method){
			return SYSCONFIG.apihost + method+"?token="+SYSCONFIG.token;
		}
};
$(function(){
	window.document.title=SYSCONFIG.title;
});
require.config({
	waitSeconds: 200000000,
	paths:{
		vendors:'vendors',
		views:'views',
		css:'vendors/r/css',
		text:'vendors/r/text',
		vue:"vendors/vue/vue.min",
        "vue-router":"vendors/vue-router/vue-router",
        "vue-validator": "vendors/vue-validator/vue-validator.min",
        "elements":"https://unpkg.com/element-ui/lib/index",
        underscore:"vendors/underscore/underscore-min"
	},
	shim:{
		//'shCore':{deps:['css!vendors/syntaxhighlighter/css/shCore.css']},
		//'prism':{deps:['css!vendors/prism/prism.css']}
	}
});

//require(["./main2"]);
require(["vue-router",'js/hierarchy'],function(VueRouter,Hierarchy){
	
	Vue.config.debug = true;
	Vue.config.devtools = true;
	Vue.use(VueRouter);
	
	var toComponent = function(item){
		var path = item.component;
		return {
			component: function (resolve) {
	            require([path], resolve);
	        }
	    };
	};
	console.log(VueRouter,Hierarchy)
	$.getJSON("data/menu2.json").then(function (json) {
	    var route;
	    if (json) {
	        route = {};
	        $.each(json,function(i,v){
	        	var p = v.component;
	        	v.component=function (resolve) {
	                require([p], resolve)
	            };
	            console.info(v.path);
	        });
	        var hierarchyArray = Hierarchy.toHierarchy(json);
	        sessionStorage.setItem("route", JSON.stringify(hierarchyArray));
	    }
	    return hierarchyArray;
	}).then(function (routeMap) {
        if (!routeMap) {
            console.log("routeMap is undefined");
            return;
        }
        var Foo = { template: '<div>foo</div>' }
        var Bar = { template: '<div>bar</div>' }
        var router = new VueRouter({
            routes:routeMap
        });
        new Vue({
        	  el: '#app',
        	  template: '<router-view></router-view>',
        	  router:router
        	});
    })
});



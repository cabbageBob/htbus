var SYSCONFIG={
		title:'数据服务管理平台',
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
		ztree:'vendors/ztree/jquery.ztree.all.min',
		vue:"vendors/vue/vue.min",
		"vue-vuex":'vendor/vue-vuex/vuex',
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
require(["vue-router",'js/hierarchy'/*,"vue-vuex","store/store"*/,"vendors/monitor/monitor"],function(VueRouter,Hierarchy/*,Vuex,store*/,monitor){
	
	Vue.config.debug = true;
	Vue.config.devtools = true;
	Vue.use(VueRouter);
	
	Vue.component('monitor',monitor);
	var toComponent = function(item){
        var componentPath = item.component,
        COMPONENT_TYPE = "100",
        IFRAME_TYPE    = "200",
        WINDOW_TYPE    = "300";
        if (item.component && item.type === COMPONENT_TYPE) {
            return {
                component: function (resolve) {
                    require([componentPath], resolve);
                }
            };
        } else if (item.component && item.type === IFRAME_TYPE) {
            return {
                component: {
                    template: "<div style='overflow: hidden;height:100%;'><iframe width='100%' height='100%' frameBorder='0' src='" + componentPath + "'></iframe></div>"
                }
            };
        } else if(item.component && item.type === WINDOW_TYPE){
            return {
                component: {
                    template: componentPath
                }
            };
        } else {
            return {
                component: {
                    template: "<div>Unknown type or not set component property</div>"
                }
            };
        };
    };
	
	/*var toComponent = function(item){
		var path = item.component;
		return {
			component: function (resolve) {
	            require([path], resolve);
	        }
	    };
	};*/
	//console.log(VueRouter,Hierarchy);
	if($.cookie('username') != 'admin') {
		SYSCONFIG.token = '';
		$.cookie('token','');
		window.location.replace("./login.html");
		return;
	}
	$.getJSON("data/menu.json").then(function (json) {
	    var route;
	    if (json) {
	    	$.each(json,function(i,v){
				v.component = toComponent(v).component;
	        });
	        var hierarchyArray = Hierarchy.toHierarchy(json);
	        /*route = {};
	        $.each(json,function(i,v){
	        	var p = v.component;
	        	v.component=function (resolve) {
	                require([p], resolve)
	            };
	            console.info(v.path);
	        });
	        var hierarchyArray = Hierarchy.toHierarchy(json);
	        sessionStorage.setItem("route", JSON.stringify(hierarchyArray));*/
	    }
	    return hierarchyArray;
	}).then(function (routeMap) {
        if (!routeMap) {
            console.log("routeMap is undefined");
            return;
        }
        /*var Foo = { template: '<div>foo</div>' }
        var Bar = { template: '<div>bar</div>' }*/
        var router = new VueRouter({
            routes:routeMap
        });
        new Vue({
        	  el: '#app',
        	  template: '<router-view></router-view>',
        	  router:router,
        	  //store:store
        	});
    })
});



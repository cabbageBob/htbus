define(["text!./index.html"],function(tpl){
	return {
		template: tpl,
        data: function(){
        	return {
				sysName:SYSCONFIG.title,
				collapsed:false,
				sysUserName: SYSCONFIG.username,
				sysUserAvatar: './img/user.png',
				form: {
					name: '',
					region: '',
					date1: '',
					date2: '',
					delivery: false,
					type: [],
					resource: '',
					desc: ''
				}
			};
//            return {
//                mainNav: {},
//                activeLinkIndex: 0,
//                hoverLinkIndex: 0,
//                title:'长江委水文局水文数据管理系统',
//            	username:localStorage.getItem("realname")
//            };
        },
        mounted : function(){
        	/*var vm=this
        	$.getJSON('getAllUserInfo!userinfoService',function(json){
        		vm.username=json.htData[0].realname;
        	});*/
        	//debugger;
        },
        methods:{
        	onSubmit : function() {
				console.log('submit!');
			},
			handleopen:function() {
				//console.log('handleopen');
			},
			handleclose:function() {
				//console.log('handleclose');
			},
			handleselect: function (a, b) {
			},
			//退出登录
			logout: function (event) {
				event.preventDefault();
        		$.get("logout!manage",function(){
        			SYSCONFIG.token = '';
        			$.cookie('token','');
        			window.location.replace("./login.html");
        		});
			},
			//折叠导航栏
			collapse:function(){
				this.collapsed=!this.collapsed;
			},
			showMenu: function(i,status){
				this.$refs.menuCollapsed.getElementsByClassName('submenu-hook-'+i)[0].style.display=status?'block':'none';
			},
			clickMenu:function(event,item){
				if(item.type=="300"){
                    event.stopPropagation();
                    window.open(item.component.template);
				}
			}
//        	logout: function(event){
//        		event.preventDefault();
//        		$.get("logout!manage",function(){
//        			window.location.replace("./login.html");
//        		});
//        		
//        	},
//        	gettoken:function(){
//        		var sName="token";
//        		var aCookie = document.cookie.split("; ");
//        		for (var i=0; i < aCookie.length; i++)
//        		{
//	        		var aCrumb = aCookie[i].slice(0,5);
//	        		if (sName == aCrumb)
//	        		return unescape(aCookie[i].slice(7,-1));
//        		}
//        		return null;
//        	}
        },
        components:{
        	mynav: function(resolve){
                require(["views/index/nav"], resolve);
            },
            left: function(resolve){
                require(["views/index/left"], resolve);
            }
        }
	};
});
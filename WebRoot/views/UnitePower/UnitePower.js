define(['underscore','text!./UnitePower.html','css!./UnitePower.css'],function(_,tpl){
	return {
		template:tpl,
		data:function(){
			var vm = this;
			
			return {
				tabPosition: 'left',
				list:[],
				listcopy:[],
				list1:[],
				listcopy1:[],
				value:'0',
				value2:'all',
				value3:'',
				test:false,
				noSearchvalue:'',
				hasSearchvalue:'',
				yewu:[],
				listLoading:false,
				addVisible:false,
				editVisible:false,
				multipleSelection:[],
				selectNum:'',
				multipleSelection2:[],
				selectNum2:'',
			};
		},
		mounted:function(){
			this.getMenu();
			this.getList();
		},
		methods:{
            tablechange:function(){
                this.getList();
			},
			getList:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('noPowerServiceList!service'),{appid:this.yewu[this.value].id,from_appid:"all"})
                .then(function(json){
                	vm.list = json;
                    vm.listcopy = json;
                });
                $.getJSON(SYSCONFIG.apiurl('hasPowerServiceList!service'),{appid:this.yewu[this.value].id,from_appid:"all"})
                .then(function(json){
                    vm.list1 = json;
                    vm.listcopy1 = json;
                });
			},
			getMenu:function(){
				var vm = this;
				$.ajaxSettings.async = false;
				$.getJSON(SYSCONFIG.apiurl('getAppList!resource'))
				.then(function(json){
					vm.yewu = json;
				});
				$.ajaxSettings.async = true;
			},
/*
            checkboxInit:function(row,index){
			      if (row.result==1) 
			        return 0;//不可勾选
			      else
			        return 1;//可勾选
			},*/
			search:function(){
				this.getList();
			},
			add:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('powerService!service'),{appid:this.yewu[this.value].id,serviceids:this.selectNum,power:1})
                .then(function(json){
                    vm.getMenu();
                    vm.getList();
                });
			},
			remove:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('powerService!service'),{appid:this.yewu[this.value].id,serviceids:this.selectNum2,power:0})
                .then(function(json){
                    vm.getMenu();
                    vm.getList();
                });
			},
			noSearch:function(){
				var reg = new RegExp(this.noSearchvalue,'i');
				this.list=[];
				for(var i=0; i <  this.listcopy.length; i ++) {
                    if(this.listcopy[i].servicename.match(reg) || this.listcopy[i].path.match(reg) || this.noSearchvalue==''){
                        this.list.push(this.listcopy[i])
                    }
                }
			},
			hasSearch:function(){
				var reg = new RegExp(this.hasSearchvalue,'i');
				this.list1=[];
				for(var i=0; i <  this.listcopy1.length; i ++) {
                    if(this.listcopy1[i].servicename.match(reg) || this.listcopy1[i].path.match(reg) || this.hasSearchvalue==''){
                        this.list1.push(this.listcopy1[i])
                    }
                }
			},
			handleSelectionChange:function(val){
				this.multipleSelection = val;
				this.selectNum = '';
		        if(this.multipleSelection.length ==0 ){
		        	return;
		        }
		        this.selectNum = this.multipleSelection[0].serviceid;
		        for(var i=1;i<this.multipleSelection.length;i++){
		        	this.selectNum = this.selectNum + ',' + this.multipleSelection[i].serviceid;
		        }
		        console.log(this.selectNum)
			},
			handleSelectionChange2:function(val){
				this.multipleSelection2 = val;
				this.selectNum2 = '';
		        if(this.multipleSelection2.length ==0 ){
		        	return;
		        }
		        this.selectNum2 = this.multipleSelection2[0].serviceid;
		        for(var i=1;i<this.multipleSelection2.length;i++){
		        	this.selectNum2 = this.selectNum2 + ',' + this.multipleSelection2[i].serviceid;
		        }
			},
		}
	      
	};
});
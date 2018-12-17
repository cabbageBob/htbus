define(['text!./datalist.html'],function(tpl){
	return {
		template:tpl,
		data:function(){
			var vm = this;
			return {
				currentPage: 1,
				listFy:[],
				list:[],
				listcopy:[],
				days:null,
				listLoading:false,
				value1:null,
				value2:null,
				value3:null,
				yewu:[],
				multipleSelection:[],
				duoxuan:true,
				pageNum:1,
				pageSize:10,
			    abcd:{},
			};
		},
		methods:{
			getList:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('getServiceListWithAsk!service'),{pageNum:this.pageNum,pageSize:this.pageSize})
				.then(function(json){
					vm.listFy = json;
					vm.list = json.recordList;
					vm.listcopy = json.recordList;
				});
			},
			getValue2:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('getAppList!resource'))
				.then(function(json){
					vm.yewu = json;
				});
			},
			selectValue1:function(){
				this.selectV(this.value1,this.value2,this.value3);
			},
			selectValue2:function(){
				this.selectV(this.value1,this.value2,this.value3);
			},
			selectValue3:function(){
				this.selectV(this.value1,this.value2,this.value3);
			},
			selectV:function(a,b,c){
				var vm = this;
				this.abcd = {};
				this.abcd = {pageNum:this.pageNum,pageSize:this.pageSize};
				if(a!=null){ this.abcd['path'] = a}
				if(b!=null){ this.abcd['appid'] = b}
				if(c!=null){ this.abcd['askstatus'] = c}
				$.getJSON(SYSCONFIG.apiurl('getServiceListWithAsk!service'),this.abcd).then(function(json){
					vm.listFy = json;
					vm.list = json.recordList;
				});
			},
			handleSelectionChange:function(val){
				this.multipleSelection = val;
				if(this.multipleSelection.length>0){
					this.duoxuan = false;
				}else{
					this.duoxuan = true;
				}
			},
			plsq:function(){
				var vm = this;
				var aaa = '',l = this.multipleSelection.length;
				for(var i=0;i<l-1;i++){
					aaa = aaa + this.multipleSelection[i].serviceid + ",";
				}
				aaa = aaa + this.multipleSelection[l-1].serviceid
				$.getJSON(SYSCONFIG.apiurl('askService!service'),{serviceids:aaa})
				.then(function(json){
					vm.getList();
				});
			},
			ddsq:function(index,low){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('askService!service'),{serviceids:low.serviceid})
				.then(function(json){
					vm.getList();
				});
			},
			handleSizeChange:function(val) {
				var vm = this;
		    	this.abcd.pageSize = val
				$.getJSON(SYSCONFIG.apiurl('getServiceListWithAsk!service'),this.abcd)
				.then(function(json){
					vm.listFy = json;
					vm.list = json.recordList;
				});
		    },
		    handleCurrentChange:function(val) {
		    	var vm = this;
		    	this.abcd.pageNum = val;
				$.getJSON(SYSCONFIG.apiurl('getServiceListWithAsk!service'),this.abcd)
				.then(function(json){
					vm.listFy = json;
					vm.list = json.recordList;
				});
		    }
		},
		mounted:function(){
			this.getList();
			this.getValue2();
		}
	};
});
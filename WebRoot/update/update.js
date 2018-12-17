define(['text!./update.html'],function(tpl){
	return {
		template:tpl,
		data:function(){
			var vm = this;
			return {
				list:[],
				listcopy:[],
				days:null,
				listLoading:false,
				value1:'',
				value2:'',
				value3:'',
				yewu:[],
				multipleSelection:[],
				duoxuan:true,
			};
		},
		methods:{
			getList:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('getServiceListWithAsk!service'))
				.then(function(json){
					vm.list = json;
					vm.listcopy = json;
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
				var reg = new RegExp(a);
				this.list=[];
				for(var i=0; i <  this.listcopy.length; i ++) {
					if((this.listcopy[i].servicename.match(reg) || a=='') && (b==this.listcopy[i].appname || b=='') && (c==this.listcopy[i].askstatus || c=='')){
						this.list.push(this.listcopy[i])
					}
				}
			},
			handleSelectionChange:function(val){
				this.multipleSelection = val;
				if(this.multipleSelection!=[]){
					this.duoxuan = false;
				}else{
					this.duoxuan = true;
				}
				//console.log(this.multipleSelection)
			},
			plsq:function(){
				var vm = this;
				var aaa = '',l = this.multipleSelection.length;
				for(var i=0;i<l-1;i++){
					aaa = aaa + this.multipleSelection[i].serviceid + ",";
				}
				aaa = aaa + this.multipleSelection[l].serviceid
				$.getJSON(SYSCONFIG.apiurl('askService!service'),{serviceids:aaa})
				.then(function(json){
					//vm.yewu = json;
				});
			}
		},
		mounted:function(){
			this.getList();
			this.getValue2();
		}
	};
});
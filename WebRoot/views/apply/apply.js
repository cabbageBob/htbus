define(['text!./apply.html'],function(tpl){
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
			};
		},
		methods:{
			getList:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('getAskList!service'))
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
					if((this.listcopy[i].servicename.match(reg) || this.listcopy[i].path.match(reg) || a=='') && (b==this.listcopy[i].from_appname || b=='') && (c==this.listcopy[i].result || c=='')){
						this.list.push(this.listcopy[i])
					}
				}
			}
		},
		mounted:function(){
			this.getList();
			this.getValue2();
		}
	};
});
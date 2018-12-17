define(['text!./request.html'],function(tpl){
	return {
		template:tpl,
		data:function(){
			var vm = this;
			return {
				list:[],
				listcopy:[],
				days:null,
				listLoading:false,
				filter:{
					year:null
				},
				value1:[
				    new Date()-3600*1000*24,
				    new Date()-0
				],
				value2:'',
				value3:'',
				yewu:[],
				
			};
		},
		methods:{
			getList:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('getRequestMonitorList!service'),{
					tm1:(new Date(this.value1[0])).format('yyyy-MM-dd'),
					tm2:(new Date(this.value1[1])).format('yyyy-MM-dd')
				}).then(function(json){
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
				this.getList();
			},
			selectValue2:function(){
				this.selectV(this.value2,this.value3);
			},
			selectValue3:function(){
				this.selectV(this.value2,this.value3);
			},
			selectV:function(b,c){
				this.list=[];
				var c1=c.split(',')[0],c2=c.split(',')[1];
				for(var i=0; i <  this.listcopy.length; i ++) {
					if((b==this.listcopy[i].appname || b=='') && (c1==this.listcopy[i].res_status || c2==this.listcopy[i].res_status || c=='')){
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
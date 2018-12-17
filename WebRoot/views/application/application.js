define(['text!./application.html'],function(tpl){
	return {
		template:tpl,
		data:function(){
			var vm = this;
			return {
				list:[],
				days:null,
				listLoading:false,
				value2:'',
				value3:'',
			};
		},
		methods:{
			getList:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('currentAppInfo!resource')).then(function(json){
					vm.list = json;
				});
			},
		},
		mounted:function(){
			this.getList();
		}
	};
});
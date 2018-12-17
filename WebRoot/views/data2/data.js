define(['text!./data.html'],function(tpl){
	return {
		template:tpl,
		data:function(){
			var vm = this;
			return {
				filters:{
					name:''
				},
				data:[],
				listLoading:false
			};
		},
		mounted:function(){
			this.data=[
			    {name:'数据服务管理平台',company:'弘泰公司',url:'http://localhost:8083/htbus',ip:'127.0.0.1',domain:'localhost',account:'htbus/123456',token:'d041f90a99c6'}
			];
		},
		methods:{
			selsChange:function(){
				
			},
			formatSex:function(){
				
			}
		}
	};
});
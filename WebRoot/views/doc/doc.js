define(['text!./doc.html'],function(tpl){
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
			    {name:'数据服务管理平台设计说明书v1.0.docx',appname:'数据服务管理平台',uptime:'2017-7-26'}
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
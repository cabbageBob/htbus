define(['text!./user.html'],function(tpl){
	return {
		template:tpl,
		data:function(){
			var vm = this;
			return {
				filters:{
					name:''
				},
				users:[],
				listLoading:false
			};
		},
		methods:{
			selsChange:function(){
				
			},
			formatSex:function(){
				
			}
		}
	};
});
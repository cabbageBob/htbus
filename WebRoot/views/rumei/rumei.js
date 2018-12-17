define(['text!./rumei.html'],function(tpl){
	return {
		template:tpl,
		data:function(){
			var vm = this;
			return {
				list:[],
				days:null,
				listLoading:false,
				filter:{
					year:null
				},
				editFormVisible:false,
				editForm:{},
				addFormVisible:false,
				addForm:{},
				editFormRules: {
					TM1: [
						{ required: true, message: '请输入开始时间', trigger: 'blur' }
					],
					TM2: [
					    { required: true, message: '请输入结束时间', trigger: 'blur' }
					]
				}
			};
		},
		methods:{
			getList:function(){
				var vm = this;
				$.getJSON('http://218.2.110.162/njgl/getRumeiList!ywgl?callback=?')
				.then(function(json){
					if(json){
						vm.list = json;
						
						var first = json[0];
						vm.days = +(new Date().diff(new Date(first.NIAN+'-'+first.TM1)).toFixed())+1;
					}
				});
			},
			handleEdit: function (index, row) {
				this.editFormVisible = true;
				this.editForm = Object.assign({}, row);
			},
			handleAdd:function(){
				this.addFormVisible = true;
				this.addForm = {NIAN:new Date().format('yyyy')};
			},
			editSubmit:function(){
				var par = Object.assign({}, this.editForm);
			},
			addSubmit:function(){
				var vm = this;
				var par = Object.assign({}, this.addForm);
				$.getJSON('http://218.2.110.162/njgl/addRumei!ywgl?callback=?',par)
            	.then(function(json){
            		vm.addFormVisible = false;
            		vm.getList();
            	});
			}
		},
		mounted:function(){
			this.getList();
		}
	};
});
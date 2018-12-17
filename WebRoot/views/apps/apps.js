define(['text!./apps.html','underscore'],function(tpl,_){
	return {
		template:tpl,
		data:function(){
			var vm = this;
			return {
				data:[],
				tableData:[],
				addVisible:false,
				editVisible:false,
				options:[],//所有开发商信息
				search:'',
				currentApp:{appname:'',companyid:'',url:'',ip:'',domain:'',account:'',password:'',remark:''},
				newApp:{appname:'',companyid:'',url:'',ip:'',domain:'',account:'',password:'',remark:''},
				rules:{
				       appname:[{required:true,message:'请输入应用系统名称'}]
				}
			};
		},
		mounted:function(){
			var vm = this;
			this.getCompanys()
			.then(function(){
				vm.getApps();
			});
		},
		watch:{
			search:function(newValue,oldValue){
				this.doSearch(this);
			},
			data:function(){
				this.doSearch(this);
			}
		},
		methods:{
			doSearch:_.debounce(function(){
				var vm =this;
				this.tableData = _.filter(this.data,function(a){
					return a.appname.indexOf(vm.search)>-1;
				});
			},300),
			getCompanys:function(){
				vm = this;
				var promise  = $.getJSON(SYSCONFIG.apiurl('getCompanyList!resource'))
				.then(function(json){
					vm.options = json;
				});
				return promise;
			},
			getApps:function(){
				vm = this;
				$.getJSON(SYSCONFIG.apiurl('getAppList!resource'))
				.then(function(json){
					vm.data = json;
				});
			},
			onAddApp:function(){
				var vm = this;
				this.$refs.addForm.validate(function(result){
					if(result){
						$.getJSON(SYSCONFIG.apiurl('addApp!resource'),vm.newApp)
				    	.then(function(json){
				    		if(json.success){
				    			vm.$message({type: 'success',message:json.message});
				    			vm.addVisible = false;
				    			vm.getApps();
				    			vm.$refs.addForm.resetFields();
				    		}else{
				    			vm.$message({type: 'warning',message:json.message});
				    		}
				    	});
					}
				});
			},
			onEditApp:function(){
				var vm = this;
				this.$refs.editForm.validate(function(result){
					if(result){
						$.getJSON(SYSCONFIG.apiurl('updateApp!resource'),vm.currentApp)
				    	.then(function(json){
				    		if(json.success){
				    			vm.$message({type: 'success',message:json.message});
				    			vm.editVisible = false;
				    			vm.getApps();
				    		}else{
				    			vm.$message({type: 'warning',message:json.message});
				    		}
				    	});
					}
				});
			},
			onDelete:function(index,row){
				var vm = this;
				this.$confirm('您确定删除应用系统'+row.appname+'吗?删除后不可恢复。', '请确认', {
			          confirmButtonText: '确定',
			          cancelButtonText: '取消',
			          type: 'warning'
			    }).then(function(){
			    	$.getJSON(SYSCONFIG.apiurl('deleteApp!resource'),{id:row.id})
			    	.then(function(json){
			    		if(json.success){
			    			vm.$message({type: 'success',message:json.message});
			    			vm.getApps();
			    		}else{
			    			vm.$message({type: 'warning',message:json.message});
			    		}
			    	});
			    }).catch(function(){});
			},
			onAddCancle:function(){
				this.$refs.addForm.resetFields();
				this.addVisible=false;
			},
			onRowClick:function(row){
				this.currentApp = _.clone(row);
			}
		}
	};
});
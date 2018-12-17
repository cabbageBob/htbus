define(['text!./role.html','underscore'],function(tpl,_){
	return {
		template:tpl,
		data:function(){
			var vm = this;
			return {
				tableData:[],
				addVisible:false,
				editVisible:false,
				currentRow:{rolename:''},
				newModel:{rolename:''},
				rules:{
				       rolename:[{required:true,message:'请输入角色名称'}]
				}
			};
		},
		mounted:function(){
			this.getRoles();
		},
		methods:{
			getRoles:function(){
				vm = this;
				$.getJSON(SYSCONFIG.apiurl('getRoleList!org'))
				.then(function(json){
					vm.tableData = json;
				});
			},
			onAddRole:function(){
				var vm = this;
				this.$refs.addForm.validate(function(result){
					if(result){
						$.getJSON(SYSCONFIG.apiurl('addRole!org'),vm.newModel)
				    	.then(function(json){
				    		if(json.success){
				    			vm.$message({type: 'success',message:json.message});
				    			vm.addVisible = false;
				    			vm.getRoles();
				    		}else{
				    			vm.$message({type: 'warning',message:json.message});
				    		}
				    	});
					}
				});
			},
			onEditRole:function(){
				var vm = this;
				this.$refs.editForm.validate(function(result){
					if(result){
						$.getJSON(SYSCONFIG.apiurl('updateRole!org'),vm.currentRow)
				    	.then(function(json){
				    		if(json.success){
				    			vm.$message({type: 'success',message:json.message});
				    			vm.editVisible = false;
				    			vm.getRoles();
				    		}else{
				    			vm.$message({type: 'warning',message:json.message});
				    		}
				    	});
					}
				});
			},
			onDeleteRole:function(index,row){
				var vm = this;
				this.$confirm('您确定删除'+row.rolename+'吗?删除后不可恢复。', '请确认', {
			          confirmButtonText: '确定',
			          cancelButtonText: '取消',
			          type: 'warning'
			    }).then(function(){
			    	$.getJSON(SYSCONFIG.apiurl('deleteRole!org'),{roleid:row.roleid})
			    	.then(function(json){
			    		if(json.success){
			    			vm.$message({type: 'success',message:json.message});
			    			vm.getRoles();
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
				this.currentRow = _.clone(row);
			}
		}
	};
});
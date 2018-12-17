define(['text!./data.html','css!./data.css'],function(tpl){
	return {
		template:tpl,
		data:function(){
			var vm = this;
			return {
				currentTab:'tp1',
				data1:[],
				data2:[],
				data3:[],
				data4:[],
				currentDB:{label:''},
				addVisible:false,
				editVisible:false,
				rules:{
					name:[{required:true,message:'请输入实例中文名称'}],
					instance_name:[{required:true,message:'请输入实例名'}],
					ip:[{required:true,message:'请输入IP地址'}],
					port:[{required:true,message:'请输入端口号'}],
					account:[{required:true,message:'请输入账号'}],
					password:[{required:true,message:'请输入密码'}],
					dbtype:[{required:true,message:'请选择数据库类型'}]
				},
				
				filters:{
					name:''
				},
				users:[],
				cancleHide:false,
				newInstance:{name:'',instance_id:'',instance_name:'',ip:'',port:'',account:'',password:'',dbtype:''},
				editingInstance:{name:'',instance_id:'',instance_name:'',ip:'',port:'',account:'',password:'',dbtype:''}
			};
		},
		mounted:function(){
			//this.getTree();
			this.getInstanceList();
		},
		methods:{
			getInstanceList:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('getInstanceList!db'))
				.then(function(json){
					vm.data1 = json;
				});
			},
			getDBList:function(iid){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('getDBlistByIID!db'),{instance_id:iid})
				.then(function(json){
					vm.data2 = json;
				});
			},
			getTableList:function(iid,dbname){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('analyzeDBTables!db'),{instance_id:iid,dbname:dbname})
				.then(function(json){
					vm.data3 = json;
				});
			},
			getFieldList:function(iid,dbname,tbname){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('analyzeTableFields!db'),{instance_id:iid,dbname:dbname,tbname:tbname})
				.then(function(json){
					vm.data4 = json;
				});
			},
			onClickInstance:function(row, event, column){
				this.getDBList(row.instance_id);
			},
			onClickDB:function(row, event, column){
				this.currentTab="tp2";
				this.currentDB=row;
				this.getTableList(row.instance_id,row.dbname);
			},
			onClickTable:function(row, event, column){
				this.getFieldList(this.currentDB.instance_id,this.currentDB.dbname,row.tbname);
			},
			
			delInstance:function(){
				var vm = this;
				this.$confirm('您确定删除此数据库服务实例吗？', '请确认', {
			          confirmButtonText: '确定',
			          cancelButtonText: '取消',
			          type: 'warning'
			    }).then(function(){
			    	$.getJSON(SYSCONFIG.apiurl('deleteInstance!db'),{instance_id:vm.currentNodeData.id})
			    	.then(function(json){
			    		if(json.success){
			    			vm.$message({type: 'success',message:json.message});
			    			vm.getTree(true);
			    		}else{
			    			vm.$message({type: 'warning',message:json.message});
			    		}
			    	});
			    	return true;
			    }).catch(function(){});
			},
			editInstance:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('getInstance!db'),{instance_id:vm.currentNodeData.id})
				.then(function(json){
					vm.editingInstance = json;
					vm.editVisible = true;
				});
			},
			onAddInstance:function(){
				var vm = this;
				this.$refs.addForm.validate(function(result){
					if(result){
						$.getJSON(SYSCONFIG.apiurl('addInstance!db'),vm.newInstance)
				    	.then(function(json){
				    		if(json.success){
				    			vm.$message({type: 'success',message:json.message});
				    			vm.addVisible = false;
				    			vm.$refs.addForm.resetFields();
				    			vm.getTree(true);
				    		}else{
				    			vm.$message({type: 'warning',message:json.message});
				    		}
				    	});
					}
				});
			},
			onEditInstance:function(){
				var vm = this;
				this.$refs.editForm.validate(function(result){
					if(result){
						$.getJSON(SYSCONFIG.apiurl('updateInstance!db'),vm.editingInstance)
				    	.then(function(json){
				    		if(json.success){
				    			vm.$message({type: 'success',message:json.message});
				    			vm.editVisible = false;
				    			vm.getTree(true);
				    			vm.$refs.editForm.resetFields();
				    		}else{
				    			vm.$message({type: 'warning',message:json.message});
				    		}
				    	});
					}
				});
			},
			onEditTree:function(m){
				this.currentNodeData ? this[m].call() : this.$message.error('请选择一个数据库服务实例再继续操作！');
			},
			onAddCancel:function(){
				this.$refs.addForm.resetFields();
				this.addVisible = false;
			},
			onEditCancel:function(){
				this.$refs.editForm.resetFields();
				this.editVisible = false;
			}
		}
	};
});
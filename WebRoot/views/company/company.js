define(['text!./company.html','underscore'],function(tpl,_){
	return {
		template:tpl,
		data:function(){
			var vm = this;
			return {
				data:[],
				tableData:[],
				addVisible:false,
				editVisible:false,
				search:'',
				currentCompany:{companyname:'',keeper:'',mobile:''},
				newCompany:{companyname:'',keeper:'',mobile:''},
				rules:{
				       companyname:[{required:true,message:'请输入开发商名称'}]
				}
			};
		},
		mounted:function(){
			this.getCompanys();
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
					return a.companyname.indexOf(vm.search)>-1;
				});
			},300),
			getCompanys:function(){
				vm = this;
				$.getJSON(SYSCONFIG.apiurl('getCompanyList!resource'))
				.then(function(json){
					vm.data = json;
				});
			},
			onAddCompany:function(){
				var vm = this;
				this.$refs.addForm.validate(function(result){
					if(result){
						$.getJSON(SYSCONFIG.apiurl('addCompany!resource'),vm.newCompany)
				    	.then(function(json){
				    		if(json.success){
				    			vm.$message({type: 'success',message:json.message});
				    			vm.addVisible = false;
				    			vm.getCompanys();
				    		}else{
				    			vm.$message({type: 'warning',message:json.message});
				    		}
				    	});
					}
				});
			},
			onEditCompany:function(){
				var vm = this;
				this.$refs.editForm.validate(function(result){
					if(result){
						$.getJSON(SYSCONFIG.apiurl('updateCompany!resource'),vm.currentCompany)
				    	.then(function(json){
				    		if(json.success){
				    			vm.$message({type: 'success',message:json.message});
				    			vm.editVisible = false;
				    			vm.getCompanys();
				    		}else{
				    			vm.$message({type: 'warning',message:json.message});
				    		}
				    	});
					}
				});
			},
			onDeleteCompany:function(index,row){
				var vm = this;
				this.$confirm('您确定删除开发商'+row.companyname+'吗?删除后不可恢复。', '请确认', {
			          confirmButtonText: '确定',
			          cancelButtonText: '取消',
			          type: 'warning'
			    }).then(function(){
			    	$.getJSON(SYSCONFIG.apiurl('deleteCompany!resource'),{id:row.id})
			    	.then(function(json){
			    		if(json.success){
			    			vm.$message({type: 'success',message:json.message});
			    			vm.getCompanys();
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
				this.currentCompany = _.clone(row);
			}
		}
	};
});
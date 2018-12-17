define(['underscore','text!./post.html','css!./post.css'],function(_,tpl){
	return {
		template:tpl,
		data:function(){
			var vm = this;
			return {
				tableData:[],
				treedata:[],
				treeprops:{label:'orgname',children:'children'},
				newPost:{postname:'',orgid:''},
				editingPost:{postid:'',postname:'',orgid:''},
				currentNode:{data:{id:'0'}},
				addVisible:false,
				editVisible:false,
				rules:{
					postname:[{required:true,message:'请输入岗位名称'}],
				}
			};
		},
		mounted:function(){
			this.getOrgTree();
		},
		methods:{
			getOrgTree:function(){
				var vm = this;
				var promise = $.getJSON(SYSCONFIG.apiurl('getOrgTree!org'))
				.then(function(json){
					vm.treedata = json[0].children;
				});
				return promise;
			},
			getPostList:function(orgid){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('getPostList!org'),{orgid:orgid})
				.then(function(json){
					vm.tableData = json;
				});
			},
			onNodeClick:function(data,node,tree){
				this.currentNode=node;
				this.getPostList(data.id);
			},
			onAddCancel:function(){
				this.$refs.addPostForm.resetFields();
				this.addVisible=false;
			},
			onAddPost:function(){
				var vm = this;
				this.$refs.addPostForm.validate(function(result){
					if(result){
						vm.newPost.orgid = vm.currentNode.data.id;
						$.getJSON(SYSCONFIG.apiurl('addPost!org'),vm.newPost)
				    	.then(function(json){
				    		if(json.success){
				    			vm.$message({type: 'success',message:json.message});
				    			vm.addVisible = false;
				    			vm.getPostList(vm.currentNode.data.id);
				    			vm.$refs.addPostForm.resetFields();
				    		}else{
				    			vm.$message({type: 'warning',message:json.message});
				    		}
				    	});
					}
				});
			},
			onDelPost:function(i,row){
				var vm = this;
				this.$confirm('您确定删除'+row.postname+'吗?删除后不可恢复。', '请确认', {
			          confirmButtonText: '确定',
			          cancelButtonText: '取消',
			          type: 'warning'
			    }).then(function(){
			    	$.getJSON(SYSCONFIG.apiurl('deletePost!org'),{postid:row.postid})
			    	.then(function(json){
			    		if(json.success){
			    			vm.$message({type: 'success',message:json.message});
			    			vm.getPostList(vm.currentNode.data.id);
			    		}else{
			    			vm.$message({type: 'warning',message:json.message});
			    		}
			    	});
			    });
			},
			onEditPost:function(i,row){
				var vm = this;
				this.$refs.editPostForm.validate(function(result){
					if(result){
						vm.editingPost.orgid = vm.currentNode.data.id;
						$.getJSON(SYSCONFIG.apiurl('updatePost!org'),vm.editingPost)
				    	.then(function(json){
				    		if(json.success){
				    			vm.$message({type: 'success',message:json.message});
				    			vm.editVisible = false;
				    			vm.getPostList(vm.currentNode.data.id);
				    		}else{
				    			vm.$message({type: 'warning',message:json.message});
				    		}
				    	});
					}
				});
			},
			onRowClick:function(row, event, column){
				this.editingPost = _.clone(row);
			}
		}
	};
});
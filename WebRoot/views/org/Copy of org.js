define(['underscore','text!./org.html','css!./org.css'],function(_,tpl){
	return {
		template:tpl,
		data:function(){
			var vm = this;
			return {
				filters:{
					name:''
				},
				users:[],
				listLoading:false,
				isTreeEditing:false,
				treedata:[],
				treeprops:{label:'orgname',children:'children'},
				users:[],
				newOrg:{},
				//currentOrg:null,
				//currentExpandedSelectKey:[],
				//currentParent:null,
				up:false,
				down:false,
				currentNode:{data:{id:'0'}}
			};
		},
		computed:{
			currentOrg:function(){
				return this.currentNode.data
			}
		},
		mounted:function(){
			this.getOrgTree();
		},
		methods:{
			getOrgTree:function(){
				var vm = this;
				var promise = $.getJSON('http://localhost:8083/htbus/getOrgTree!org?token=admin')
				.then(function(json){
					vm.treedata = json[0].children;
				});
				return promise;
			},
			getPersonList:function(orgid){
				var vm = this;
				$.getJSON('http://localhost:8083/htbus/getPersonListByOrgid!org?token=admin',{orgid:orgid})
				.then(function(json){
					vm.users = json;
				});
			},
			addOrg:function(){
				var vm = this;
				this.$prompt('请输入行政机构名称', '添加行政机构', {
			          confirmButtonText: '确定',
			          cancelButtonText: '取消',
			          inputPattern: /\S/,
			          inputErrorMessage: '行政机构名称不能为空！'
			    }).then(function(v){
			    	$.getJSON('http://localhost:8083/htbus/addOrg!org?token=admin',{pid:vm.currentOrg.id,orgname:v.value})
			    	.then(function(json){
			    		if(json.success){
			    			vm.$message({type: 'success',message:'添加成功'});
			    			vm.resetTree();
			    		}else{
			    			vm.$message({type: 'warning',message:'添加失败'});
			    		}
			    	});
			    	return true;
			    }).catch(function(){});
			},
			delOrg:function(){
				var vm = this;
				if(vm.users.length){
					this.$alert('请先迁移或删除此机构下的所有人员，再进行删除操作。', '提示',{confirmButtonText: '确定'});
				}else{
					this.$confirm('您确实删除啊该组织机构吗?', '请确认', {
				          confirmButtonText: '确定',
				          cancelButtonText: '取消',
				          type: 'warning'
				    }).then(function(){
				    	$.getJSON('http://localhost:8083/htbus/delOrg!org?token=admin',{id:vm.currentOrg.id})
				    	.then(function(json){
				    		if(json.success){
				    			vm.$message({type: 'success',message:json.message});
				    			vm.currentOrg = vm.currentParent;
				    			vm.resetTree();
				    		}else{
				    			vm.$message({type: 'warning',message:json.message});
				    		}
				    	});
				    	return true;
				    }).catch(function(){});
				}
			},
			editOrg:function(){
				var vm = this;
				this.$prompt('请输入行政机构名称', '修改行政机构', {
			          confirmButtonText: '确定',
			          cancelButtonText: '取消',
			          inputPattern: /\S/,
			          inputErrorMessage: '行政机构名称不能为空！',
			          inputValue:vm.currentOrg.orgname
			    }).then(function(v){
			    	$.getJSON('http://localhost:8083/htbus/updateOrg!org?token=admin',{id:vm.currentOrg.id,orgname:v.value})
			    	.then(function(json){
			    		if(json.success){
			    			vm.$message({type: 'success',message:json.message});
			    			vm.currentOrg.orgname=v.value;
			    			vm.resetTree();
			    		}else{
			    			vm.$message({type: 'warning',message:json.message});
			    		}
			    	});
			    	return true;
			    }).catch(function(){});
			},
			upOrg:function(){
				
			},
			downOrg:function(){
				
			},
			orderOrg:function(id1,id2){
				$.getJSON('http://localhost:8083/htbus/updateOrgOrd!org?token=admin',{id1:id1,id2:id2})
		    	.then(function(json){
		    		if(json.success){
		    			vm.$message({type: 'success',message:json.message});
		    			vm.currentOrg.orgname=v.value;
		    			vm.resetTree();
		    		}else{
		    			vm.$message({type: 'warning',message:json.message});
		    		}
		    	});
			},
			resetTree:function(){
				var vm = this;
				this.getOrgTree().then(function(){
					//if(vm.currentOrg) vm.currentExpandedSelectKey=[vm.currentOrg.id];
				});
			},
			onNodeClick:function(data,node,tree){
				//this.currentOrg = data;
				this.currentNode=node;
//				if(data.pId != "0"){//如果选中节点不是顶级节点
//					this.currentParent = node.parent.data;
//				}
				
				//上下移动按钮的状态
//				var ns = node.parent.childNodes
//				if(ns.length == 1){
//					this.up = this.down = false;
//				}else{
//					this.up= (_.first(ns) == node) ? false : true;
//					this.down = (_.last(ns) == node) ? false : true; 
//				}
				
				this.getPersonList(data.id);
			},
			onEditTree:function(editmethod){
				this.currentOrg ? this[editmethod].call() : this.$message.error('请选择一个行政机构再继续操作！');
			},
			selsChange:function(){
				
			}
		}
	};
});
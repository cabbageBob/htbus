define(['underscore','text!./org.html','css!./org.css'],function(_,tpl){
	return {
		template:tpl,
		data:function(){
			var vm = this;
			var checkUid=function(rule,value,callback){
				if(vm.editVisible) return callback();//编辑人员时不需要检测id，因为编辑时id不写修改
				if(value === "") return callback(new Error('人员ID不能为空')); 
				var result = false;
				$.ajax({url:SYSCONFIG.apiurl('checkPersonUid!org'),data:{uid:value},async:false})
				.then(function(json){
					result=json.success;
				});
				if(!result){
					return callback(new Error('ID重复，请使用其他ID'));
				}else{
					return callback();
				}
			};
			return {
				filters:{
					name:''
				},
				users:[],
				tableData:[],
				listLoading:false,
				isTreeEditing:false,
				treedata:[],
				treeprops:{label:'orgname',children:'children'},
				users:[],
				options_post:[],
				options_role:[],
				newPerson:{uid:'',name:'',sex:'',tel:'',mobile:'',smobile:'',mail:'',xingzheng:[],dang:[],roleid:'',postid:''},
				editingPerson:{uid:'',name:'',sex:'',tel:'',mobile:'',smobile:'',mail:'',xingzheng:[],dang:[],roleid:'',postid:''},
				currentNode:{data:{id:'0'}},
				search:'',
				addVisible:false,
				editVisible:false,
				cascader:{value:'id',label:'orgname'},
				rules:{
					uid:[{required:true,validator: checkUid, trigger: 'blur'}],
					name:[{required:true,message:'请输入姓名'}],
					sex:[{required:true,message:'请输入性别'}],
					xingzheng:[{required:true,message:'请输入所属行政组织机构'}]
				}
			};
		},
		computed:{
			up:function(){
				if(!this.currentNode.parent) return false;
				var ns = this.currentNode.parent.childNodes
				if(ns.length == 1){
					return false;
				}else{
					return (_.first(ns) == this.currentNode) ? false : true;
				}
			},
			down:function(){
				if(!this.currentNode.parent) return false;
				var ns = this.currentNode.parent.childNodes
				if(ns.length == 1){
					return false;
				}else{
					return (_.last(ns) == this.currentNode) ? false : true; 
				}
			}
		},
		mounted:function(){
			this.getOrgTree();
			
			var vm = this;
			$.getJSON(SYSCONFIG.apiurl('getRoleList!org'))
			.then(function(json){
				vm.options_role = json;
			});
		},
		watch:{
			search:function(newValue,oldValue){
				this.doSearch(this);
			},
			users:function(){
				this.doSearch(this);
			},
			currentNode:function(newValue){
				
			},
			"newPerson.xingzheng":function(newv,oldv){
				var n = _.last(newv);
				var o = _.last(oldv);
				if(n && n!=o){
					this.getOrgPosts(n);
				}
			},
			"editingPerson.xingzheng":function(newv,oldv){
				var n = _.last(newv);
				var o = _.last(oldv);
				if(n!=o){
					this.getOrgPosts(n);
				}
			}
		},
		methods:{
			getOrgPosts:function(orgid){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('getPostList!org'),{orgid:orgid})
				.then(function(json){
					vm.options_post = json;
				});
			},
			getOrgTree:function(){
				var vm = this;
				var promise = $.getJSON(SYSCONFIG.apiurl('getOrgTree!org'))
				.then(function(json){
					vm.treedata = json[0].children;
				});
				return promise;
			},
			getPersonList:function(orgid){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('getPersonListByOrgid!org'),{orgid:orgid})
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
			    	$.getJSON(SYSCONFIG.apiurl('addOrg!org'),{pid:vm.currentNode.data.id,orgname:v.value})
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
				if(vm.currentNode.childNodes.length){
					this.$alert('请先删除该机构的所有下级机构，再进行删除操作。', '提示',{confirmButtonText: '确定'});
					return;
				}
				if(vm.users.length){
					this.$alert('请先迁移或删除此机构下的所有人员，再进行删除操作。', '提示',{confirmButtonText: '确定'});
				}else{
					this.$confirm('您确定删除该组织机构吗?', '请确认', {
				          confirmButtonText: '确定',
				          cancelButtonText: '取消',
				          type: 'warning'
				    }).then(function(){
				    	var p = vm.currentNode.parent;
				    	$.getJSON(SYSCONFIG.apiurl('delOrg!org'),{id:vm.currentNode.data.id})
				    	.then(function(json){
				    		if(json.success){
				    			vm.$message({type: 'success',message:json.message});
				    			vm.currentNode = p;
				    			vm.resetTree(true);
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
			          inputValue:vm.currentNode.data.orgname
			    }).then(function(v){
			    	$.getJSON(SYSCONFIG.apiurl('updateOrg!org'),{id:vm.currentNode.data.id,orgname:v.value})
			    	.then(function(json){
			    		if(json.success){
			    			vm.$message({type: 'success',message:json.message});
			    			vm.currentNode.data.orgname=v.value;
			    			vm.resetTree();
			    		}else{
			    			vm.$message({type: 'warning',message:json.message});
			    		}
			    	});
			    	return true;
			    }).catch(function(){});
			},
			upOrg:function(){
				var ns = this.currentNode.parent.childNodes;
				var uper = ns[ns.indexOf(this.currentNode)-1];
				this.orderOrg(this.currentNode.data.id,uper.data.id);
			},
			downOrg:function(){
				var ns = this.currentNode.parent.childNodes;
				var downer = ns[ns.indexOf(this.currentNode)+1];
				this.orderOrg(this.currentNode.data.id,downer.data.id);
			},
			orderOrg:function(id1,id2){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('updateOrgOrd!org'),{id1:id1,id2:id2})
		    	.then(function(json){
		    		if(json.success){
		    			vm.$message({type: 'success',message:json.message});
		    			vm.resetTree();
		    		}else{
		    			vm.$message({type: 'warning',message:json.message});
		    		}
		    	});
			},
			resetTree:function(isdel){
				var vm = this;
				this.getOrgTree().then(function(){
					setTimeout(function(){
						if(!isdel) $(vm.$refs.tree.currentNode.$el).click();
						else{
							$(vm.$refs.tree.currentNode.$parent.$el).click();
						}
					},0);
				});
			},
			doSearch:_.debounce(function(){
				var vm =this;
				this.tableData = _.filter(this.users,function(a){
					return a.uid.indexOf(vm.search)>-1 || a.name.indexOf(vm.search)>-1;
				});
			},300),
			onNodeClick:function(data,node,tree){
				this.currentNode=node;
				
				this.getPersonList(data.id);
			},
			onEditTree:function(editmethod){
				this.currentNode.data.orgname ? this[editmethod].call() : this.$message.error('请选择一个行政机构再继续操作！');
			},
			onAddCancel:function(){
				this.$refs.addPersonForm.resetFields();
				this.addVisible=false;
			},
			onAddPerson:function(){
				var vm = this;
				this.$refs.addPersonForm.validate(function(result){
					if(result){
						var dang="";
						if(vm.newPerson.dang.length) dang = ","+_.last(vm.newPerson.dang);
						vm.newPerson.orgids = _.last(vm.newPerson.xingzheng)+dang;
						$.getJSON(SYSCONFIG.apiurl('addPerson!org'),vm.newPerson)
				    	.then(function(json){
				    		if(json.success){
				    			vm.$message({type: 'success',message:json.message});
				    			vm.addVisible = false;
				    			vm.getPersonList(vm.currentNode.data.id);
				    		}else{
				    			vm.$message({type: 'warning',message:json.message});
				    		}
				    	});
					}
				});
			},
			onDelPerson:function(i,row){
				var vm = this;
				this.$confirm('您确定删除用户'+row.name+'吗?删除后不可恢复。', '请确认', {
			          confirmButtonText: '确定',
			          cancelButtonText: '取消',
			          type: 'warning'
			    }).then(function(){
			    	$.getJSON(SYSCONFIG.apiurl('delPerson!org'),{uid:row.uid})
			    	.then(function(json){
			    		if(json.success){
			    			vm.$message({type: 'success',message:json.message});
			    			vm.getPersonList(vm.currentNode.data.id);
			    		}else{
			    			vm.$message({type: 'warning',message:json.message});
			    		}
			    	});
			    });
			},
			onEditPerson:function(i,row){
				var vm = this;
				this.$refs.editPersonForm.validate(function(result){
					if(result){
						var dang="";
						if(vm.editingPerson.dang.length) dang = ","+_.last(vm.editingPerson.dang);
						vm.editingPerson.orgids = _.last(vm.editingPerson.xingzheng)+dang;
						$.getJSON(SYSCONFIG.apiurl('updatePerson!org'),vm.editingPerson)
				    	.then(function(json){
				    		if(json.success){
				    			vm.$message({type: 'success',message:json.message});
				    			vm.editVisible = false;
				    			vm.getPersonList(vm.currentNode.data.id);
				    		}else{
				    			vm.$message({type: 'warning',message:json.message});
				    		}
				    	});
					}
				});
			},
			onRowClick:function(row, event, column){
				var xingzheng=[],dang=[];
				
				var arr = row.org.split(',');
				var arr1 = arr[0].split('-');
				xingzheng.push(arr[0].substring(0,5));
				for(i=3;i<arr1.length;i++){
					xingzheng.push(xingzheng[i-3]+'-'+arr1[i]);
				}
				if(arr.length>1){
					var arr2 = arr[1].split('-');
					dang.push(arr[1].substring(0,5));
					for(i=3;i<arr2.length;i++){
						dang.push(dang[i-3]+'-'+arr2[i]);
					}
				}
				
				this.editingPerson = {
						uid:row.uid,name:row.name,sex:row.sex,tel:row.tel,mobile:row.mobile,smobile:row.smobile,mail:row.mail,
						roleid:row.roleid,postid:row.postid,
						xingzheng:xingzheng,dang:dang
				};
			}
		}
	};
});
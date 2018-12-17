define(['text!./resource.html','css!./metroStyle/metroStyle.css'],function(tpl){
	return {
		template:tpl,
		data:function(){
			var vm = this;
			return {
				dataResources:[],
				serviceResources:[],
				selectedMatedata:null,
				newMatedata:{
					type:'',
					/*code:'',*/
					name:'',
				},
				rules:{
					type:[{ required: true, message: '请选择类型', trigger: 'change' }],
					code:[{ required: true, message: 'ID不能为空！', trigger: 'blur' }],
					name:[{ required: true, message: '名称不能为空！', trigger: 'blur' }],
				},
				newItem:{
					schame_code:'',
					schame_name:'',
					value:'',
				},
				rulesItem:{
					schame_code:[{ required: true, message: 'code不能为空', trigger: 'blur' }],
					schame_name:[{ required: true, message: '名称不能为空！', trigger: 'blur' }],
					value:[{ required: true, message: '值不能为空！', trigger: 'blur' }],
				},
				currentMatedata:[],
				defaultData:[
				    {matedate_code:null,schame_code:'store_type',schame_name:'资源描述',value:null},
				],
				defaultData1:[
					{matedate_code:null,schame_code:'store_type',schame_name:'资源描述',value:null},
				],
				radioData:[
				],
			    radioarray:['store_type'],
			    defarray:['store_type'],
			    schamecodeArray:[],
				currentMatedata:[],
				activeName2:'first',
				exModel:false,
				delModel:false,
				selectedItem:[],
				input:'',
				codeArray:[],
				treenode:'',
			    searchValue:'',
			    scModel:false,
			    IDopen:false,
			    nameOpen:false,
			    repeatId:false,
			    zTreeSetting: {
			    	async: {
			    		enable: true,
						url: SYSCONFIG.apiurl('getMatedataTree!db'),
						dataType: "json",
						dataFilter:function(treeId, parentNode, responseData){
							var darray=responseData.htData;
							for(var i=0,len=darray.length;i<len;i++){
								vm.codeArray[i]=darray[i].code;
							}
							return responseData.htData;
						}
					},
					expandAll: false,
					check: {
						enable: false
					},
					edit:{
						drag: {
							autoExpandTrigger: true,
						},
						enable:true
					},
					data: {
						keep:{
							leaf:true,
							parent:true
						},
						simpleData: {
							enable: true,
							idKey:"code",
							pIdKey:"parent_code",
							rootPId:"root"
						}
					},
					view: {
						showLine: true,
						nameIsHTML: false,
						addHoverDom: vm.addHoverDom,
			            removeHoverDom: vm.removeHoverDom,
			            selectedMulti: true
					},
					chkboxType: {
						"Y": "ps",
						"N": "ps",
					},
					callback: {
						onNodeCreated:function(event,treeId,treeNode){
							if(treeNode.type=="class"){
								treeNode.isParent=true;
							}
							var zTree = $.fn.zTree.getZTreeObj("tree");
							zTree.updateNode(treeNode);
						},
						onClick: function(event, treeId, treeNode){
							document.getElementById("box1").style.width = '300px';
							document.getElementById("box2").style.width = document.body.clientWidth-590+'px'
							if(treeNode.type=="class"){
								vm.selectedClass = treeNode;
							}else if(treeNode.type == "matedata"){
								vm.selectedMatedata = treeNode;
							}
						},
						onAsyncSuccess:function(event, treeId, treeNode, msg){
							var zTree=$.fn.zTree.getZTreeObj("tree");
							var nodes=zTree.getNodes();
							zTree.expandNode(nodes[0]);
							return msg.htData;
						},
						beforeRemove: vm.beforeRemove,
						beforeRename: vm.beforeRename,
						beforeDrag: vm.beforeDrag,
						onRemove: vm.onRemove,
						onRename: vm.onRename,
						onDrop: vm.onDrop
					}
				}
			};
		},
		methods:{
			search: function(){
				var value=this.searchValue;
				var zTreeObj = $.fn.zTree.getZTreeObj("tree");
				zTreeObj.cancelSelectedNode();
				if(!$.trim(value)){
					return;
				}
				value=value.toLowerCase();
				var toSelectedNodes = zTreeObj.getNodesByFilter(function(treeNode){
					return treeNode._s.indexOf(value) > -1;
				});
				$.each(toSelectedNodes, function(index, treeNode){
					zTreeObj.selectNode(treeNode, true);						
					var parentNode = treeNode.getParentNode();
					while(parentNode){
						zTreeObj.expandNode(parentNode, true, false, false);
						parentNode = parentNode.getParentNode();
					}
				});
				//使输入框重新获得焦点
				$("#keyword").focus();
			},
			changeId:function(){
				if(this.codeArray.indexOf(this.newMatedata.code) != -1){
					this.repeatId=true;
				}else{
					this.repeatId=false;
				}
			},
			idblur: function(){
				this.IDopen=true;
			},
			nameblur: function(){
				this.nameOpen=true;
			},
			addHoverDom:function(treeId, treeNode) {
				var vm=this;
				if (treeNode.type=="class"){
		            var sObj = $("#" + treeNode.tId + "_span");
		            if (treeNode.editNameFlag || $("#addBtn_"+treeNode.tId).length>0) return;
		            var addStr = "<span class='button add' id='addBtn_" + treeNode.tId
		                + "' title='add node' onfocus='this.blur();'></span>";
		            sObj.after(addStr);
		            var btn = $("#addBtn_"+treeNode.tId);
		            if (btn) btn.bind("click", function(){
		            	vm.newMatedata={
		    				type:'',
		    				code:'',
		    				name:'',
		    			};
		            	vm.treenode=treeNode;
		            	vm.scModel=true;                
		            });
				}
	        },
	        addCMBack:function(){
	        	var vm=this;
	        	if(this.newMatedata.type=='' || this.newMatedata.name==""){
	        		this.scModel=true;
	        	}else if(this.codeArray.indexOf(this.newMatedata.code) != -1){
	        		this.repeatId=true;
	        		this.scModel=true;
	        	}else{
		        	var zTree = $.fn.zTree.getZTreeObj("tree");
	                zTree.addNodes(this.treenode, {id:this.newMatedata.code, pId:this.treenode.code, name:this.newMatedata.name});
	            	vm.newMatedata.parent_code=vm.treenode.code;
	            	if(this.treenode.children && this.treenode.children.length && (this.treenode.children.length!=1)){
	            		var len=this.treenode.children.length;
	            		vm.newMatedata.ord=this.treenode.children[len-2].ord+1
	            	}else{
	            		vm.newMatedata.ord=this.treenode.ord*10;
	            	}           	
	            	$.getJSON(SYSCONFIG.apiurl("addMatedataClass!db"),vm.newMatedata)
	            	.then(function(json){
	            		vm.scModel=false;
	            		zTree.reAsyncChildNodes(null, "refresh");
	            		//window.location.reload(true);//添加后刷新了整个页面，用户体验很差，需要优化。	            			            		
	            	});
	        	}
	        },
	        removeHoverDom: function(treeId, treeNode) {
	            $("#addBtn_"+treeNode.tId).unbind().remove();
	        },
	        beforeRemove: function(treeId, treeNode) {
				var zTree = $.fn.zTree.getZTreeObj("tree");
				zTree.selectNode(treeNode);
				if(treeNode.type=='class' && treeNode.children!=null && treeNode.children.length!=null && treeNode.children.length>0){
					alert("该资源分类还有数据，请先清空该分类下的所有元数据再删除分类！");
					return false;
				}
				return confirm("确认删除 节点 -- " + treeNode.name + " 吗？");
			},
			onRemove: function(e, treeId, treeNode) {
				var index=this.codeArray.indexOf(treeNode.code);
				this.codeArray.splice(index,1);
				$.getJSON(SYSCONFIG.apiurl("deleteMatedata!db"),treeNode)
            	.then(function(json){
            		
            	});
			},
			beforeRename: function(treeId, treeNode, newName, isCancel) {
				if (newName.length == 0) {
					alert("节点名称不能为空.");
					var zTree = $.fn.zTree.getZTreeObj("tree");
					setTimeout(function(){zTree.editName(treeNode)}, 10);
					return false;
				}
				return true;
			},
			onRename: function(e, treeId, treeNode, isCancel){
				$.getJSON(SYSCONFIG.apiurl("updateMatedata!db"),treeNode)
            	.then(function(json){
            		
            	});
			},
			onDrop: function(event, treeId, treeNodes, targetNode, moveType, isCopy) {
				var node=treeNodes[0],nodeCode={},zTree = $.fn.zTree.getZTreeObj("tree");
				var pnode=node.getParentNode();
				if(node.isLastNode){
					nodeCode.parent_code = node.getParentNode().code
					nodeCode.code = node.code
					nodeCode.prev = node.getPreNode().ord
					//nodeCode.post = null;
				}else if(node.isFirstNode){
					nodeCode.parent_code = node.getParentNode().code
					nodeCode.code = node.code
					//nodeCode.prev = null;
					nodeCode.post = node.getNextNode().ord
				}else{
					nodeCode.parent_code = node.getParentNode().code
					nodeCode.code = node.code
					nodeCode.prev = node.getPreNode().ord;
					nodeCode.post = node.getNextNode().ord
				}
				/*if(node.isLastNode){
					var len=pnode.children.length;
					if(len==1){
						node.ord=pnode.ord*10;
					}else{
						node.ord=pnode.children[len-2].ord+1;
					}
				}else{
					node.ord=targetNode.ord;
					var nextnode=targetNode;
					while(nextnode.isLastNode == false){
						nextnode.ord += 1;
						$.getJSON(SYSCONFIG.apiurl("afterDrag!db"),nextnode).then(function(){});
						nextnode=nextnode.getNextNode();
					}
					nextnode.ord += 1;
					$.getJSON(SYSCONFIG.apiurl("afterDrag!db"),nextnode).then(function(){});
				};*/
				$.getJSON(SYSCONFIG.apiurl("dragMatedata!db"),nodeCode).then(function(json){
					zTree.reAsyncChildNodes(null, "refresh");
					$.fn.zTree.init($('tree'), vm.setting, json);
				});
			},
			addItemBack:function(){
				var a = this.newItem;
				this.currentMatedata.push(a);//数组变化后页面会自动更新。
				this.exModel = false;				
			},
			deleteItem:function(item){
				this.selectedItem = item;
				this.delModel = true;
			},
			delBack :function(){
				var vm = this;
            	$.getJSON(SYSCONFIG.apiurl('delMatedataDetail!db'),this.selectedItem)
            	.then(function(json){
            		if(json.htStatus==100){
            			vm.delModel=false;
            			var index=vm.currentMatedata.indexOf(vm.selectedItem);
            			vm.currentMatedata.splice(index,1);
            		}
            	});
			},
			save:function(){
				var vm=this;
				var flag=true;
				for(var i=0,len=vm.currentMatedata.length;i<len;i++){
					vm.currentMatedata[i].matedata_code=vm.selectedMatedata.code;
					if(vm.matedataCode.indexOf(vm.currentMatedata[i].schame_code)>-1){
						$.getJSON(SYSCONFIG.apiurl('updateMatedataDetail!db'),vm.currentMatedata[i])
						.then(function(json){
							if(json.htStatus!="100"){flag=false;}
						});
					}else{
						$.getJSON(SYSCONFIG.apiurl('addMatedataDetail!db'),vm.currentMatedata[i])
						.then(function(json){
							if(json.htStatus!="100"){flag=false;}
						});
					}
				}
				if(flag){
					this.$message.success('保存成功');
				}else{
					this.$message.error('保存失败');
				}
			}
		},
		mounted:function(){
		},
		watch:{
			//根据selectedMatedata的值是否变化来执行这个函数
			'selectedMatedata':function(v,oldv){
				var vm=this;
				$.getJSON(SYSCONFIG.apiurl('getMatedataDetail!db'),{code:v.code}).then(function(json){
					vm.matedataCode=[];
					if(json.htData==null || json.htData.length == 0){
						vm.currentMatedata=vm.defaultData.slice();
					}else{
						vm.currentMatedata=vm.defaultData1.slice();
						for(var i=0,len=vm.currentMatedata.length;i<len;i++){
							vm.schamecodeArray[i]=vm.currentMatedata[i].schame_code;
						}
						for(var i=0,len=json.htData.length;i<len;i++){
							vm.matedataCode[i]=json.htData[i].schame_code;
							var a=vm.schamecodeArray.indexOf(json.htData[i].schame_code);
							if(a>-1){
								vm.currentMatedata[a].value=json.htData[i].value;
							}else{
								vm.currentMatedata.push(json.htData[i]);
							}
						}
					}
				});
				$.getJSON(SYSCONFIG.apiurl('getMateDataResources!db'),{code:v.code,resourceType:0})
				.then(function(json){
					vm.dataResources = json;
				});
				$.getJSON(SYSCONFIG.apiurl('getMateDataResources!db'),{code:v.code,resourceType:1})
				.then(function(json){
					vm.serviceResources = json;
				});
			},
			
		
		},
		components:{
			ztree: function(resolve){
				require(["js/ztree/ztree"], resolve);
			}
		},
	};
});
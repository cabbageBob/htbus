define(['text!./update.html','css!./update.css'],function(tpl){
	return {
		template:tpl,
		data:function(){
			var vm = this;
			return {
				props: {
			        children: 'child',
			        label: 'name',
			    },
			    dataCode:'',
			    dataCode2:'',
			    count: 1,
				activeName:'first',
				name:'',
				list:[],
				listcopy:[],
				listFy:[],
				menuList:[],
				days:null,
				listLoading:false,
				value1:null,
				value2:null,
				value3:null,
				value4:null,
				value5:null,
				yewu:[],
				treeData:[],
				cachetime:'',
				cacheid:'',
				cacheModel:false,
				editionData:[],
				editionList:[],
				editionModel:false,
				intfData1:[],
				intfData2:[],
				intfModel:false,
				qtData:'',
				qtModel:false,
				testData:[],
				testModel:false,
				message:'',
				bindModel:false,
				bindData:'',
				duoxuan:true,
				plModel:false,
				pageSize:10,
			    pageNum:1,
			    abcd:{
			    	pageNum:1,
			    	pageSize:10,
			    },
			    multipleSelection:[],
			};
		},
		methods:{
			getList:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('getServiceList!service'),{pageNum:this.pageNum,pageSize:this.pageSize})
				.then(function(json){
					vm.listFy = json;
					vm.list = [];
					for(var i=0;i<json.recordList.length;i++){
						if(json.recordList[i].response_sample.indexOf('\n') !== -1){
							vm.list.push(json.recordList[i]);
						}else{
							var aaa = '';
							for(var j=0;j<json.recordList[i].response_sample.length;j++){
								if(json.recordList[i].response_sample[j] == '[' || json.recordList[i].response_sample[j]=='{'
								|| json.recordList[i].response_sample[j] == ']' || json.recordList[i].response_sample[j+1]==']'
								|| json.recordList[i].response_sample[j] == '}' || json.recordList[i].response_sample[j+1]=='}'
								|| json.recordList[i].response_sample[j] == ',' 
								|| (json.recordList[i].response_sample[j]=='>' && json.recordList[i].response_sample[j-1]=='/')){
									aaa = aaa + json.recordList[i].response_sample[j] + '\n'
								}else{
									aaa = aaa + json.recordList[i].response_sample[j]
								}
							}
							json.recordList[i].response_sample = aaa
							vm.list.push(json.recordList[i]);
						}
					}
				});
			},
			getList1:function(){
				var vm = this;
				console.log(this.abcd)
				$.getJSON(SYSCONFIG.apiurl('getServiceList!service'),this.abcd)
				.then(function(json){
					vm.listFy = json;
					vm.list = [];
					for(var i=0;i<json.recordList.length;i++){
						if(json.recordList[i].response_sample.indexOf('\n') !== -1){
							vm.list.push(json.recordList[i]);
						}else{
							var aaa = '';
							for(var j=0;j<json.recordList[i].response_sample.length;j++){
								if(json.recordList[i].response_sample[j] == '[' || json.recordList[i].response_sample[j]=='{'
								|| json.recordList[i].response_sample[j] == ']' || json.recordList[i].response_sample[j+1]==']'
								|| json.recordList[i].response_sample[j] == '}' || json.recordList[i].response_sample[j+1]=='}'
								|| json.recordList[i].response_sample[j] == ',' 
								|| (json.recordList[i].response_sample[j]=='>' && json.recordList[i].response_sample[j-1]=='/')){
									aaa = aaa + json.recordList[i].response_sample[j] + '\n'
								}else{
									aaa = aaa + json.recordList[i].response_sample[j]
								}
							}
							json.recordList[i].response_sample = aaa
							vm.list.push(json.recordList[i]);
						}
					}
				});
			},
			getValue1:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('getMatedataTreeBind!db'))
				.then(function(json){
					vm.treeData = json.htData;
				});
			},
			getValue2:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('getAppList!resource'))
				.then(function(json){
					vm.yewu = json;
				});
			},
			selectValue:function(){
				this.selectV(this.value1,this.value2,this.value3,this.value4,this.value5);
			},
			selectV:function(a,b,c,d,e){
				/*var reg = new RegExp(a);
				this.list=[];
				for(var i=0; i <  this.listcopy.length; i ++) {
					if((this.listcopy[i].servicename.match(reg) || this.listcopy[i].path.match(reg) || a=='') 
							&& (b==this.listcopy[i].appname || b=='') && (c==this.listcopy[i].onoff || c=='')
							&& (d==this.listcopy[i].iscache || d=='')){
						this.list.push(this.listcopy[i])
					}
				}*/
				var vm = this;
				this.abcd = {};
				this.abcd = {pageNum:this.pageNum,pageSize:this.pageSize};
				if(a!=null){ this.abcd['path'] = a}
				if(b!=null){ this.abcd['appid'] = b}
				if(c!=null){ this.abcd['onoff'] = c}
				if(d!=null){ this.abcd['iscache'] = d}
				if(e!=null){ this.abcd['isClassNull'] = e}
				$.getJSON(SYSCONFIG.apiurl('getServiceList!service'),this.abcd).then(function(json){
					vm.listFy = json;
					vm.list = [];
					for(var i=0;i<json.recordList.length;i++){
						if(json.recordList[i].response_sample.indexOf('\n') !== -1){
							vm.list.push(json.recordList[i]);
						}else{
							var aaa = '';
							for(var j=0;j<json.recordList[i].response_sample.length;j++){
								if(json.recordList[i].response_sample[j] == '[' || json.recordList[i].response_sample[j]=='{'
								|| json.recordList[i].response_sample[j] == ']' || json.recordList[i].response_sample[j+1]==']'
								|| json.recordList[i].response_sample[j] == '}' || json.recordList[i].response_sample[j+1]=='}'
								|| json.recordList[i].response_sample[j] == ',' 
								|| (json.recordList[i].response_sample[j]=='>' && json.recordList[i].response_sample[j-1]=='/')){
									aaa = aaa + json.recordList[i].response_sample[j] + '\n'
								}else{
									aaa = aaa + json.recordList[i].response_sample[j]
								}
							}
							json.recordList[i].response_sample = aaa
							vm.list.push(json.recordList[i]);
						}
					}
				});
			},
			cache:function(index,low){
				this.cacheModel = true;
				this.cachetime = low.cache_effective;
				this.cacheid = low.serviceid;
			},
			cacheSubmit:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl("setServiceCache!service"),{serviceid:this.cacheid,cache_effective:this.cachetime})
				.then(function(json){
					if(json.success == true){
						vm.getList1();
					};
					vm.cacheModel = false;
				})
			},
			edition:function(index,low){
				var vm = this;
				this.editionModel = true;
				this.editionData = low;
				this.menuList = [];
				for(var i=1;i<low.current_version;i++){
					var arr = {};
					arr.indexId = i;
					arr.name = '版本-'+ (low.current_version-i);
					arr.id = low.current_version-i;
					this.menuList.push(arr);
				}
				$.getJSON(SYSCONFIG.apiurl("getServiceInfo!service"),{serviceid:this.editionData.serviceid,
				his_version:this.editionData.current_version-1}).then(function(json){
					vm.editionList = json;
				})
			},
			handleselect:function(index,indexPath){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl("getServiceInfo!service"),{serviceid:this.editionData.serviceid,
				his_version:this.menuList[index-1].id}).then(function(json){
					vm.editionList = json;
				})
			},
			intf:function(index,low){
				var vm = this;
				this.name = low.servicename
				this.intfModel = true;
				$.getJSON(SYSCONFIG.apiurl("getAuthorizedApp!service"),{serviceid:low.serviceid}).then(function(json){
					vm.intfData1 = json;
				})
				$.getJSON(SYSCONFIG.apiurl("getRequestMonitorListBySid!service"),{serviceid:low.serviceid}).then(function(json){
					vm.intfData2 = json;
				})
			},
			qiting:function(data,low){
				this.qtModel = true;
				this.qtData = low;
				this.qtDataT = data;
			},
			qitingBtn:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl("onoffService!service"),{serviceid:this.qtData.serviceid,onoff:this.qtDataT}).then(function(json){
					vm.qtModel = false;
					vm.getList1();
				})
			},
			test:function(index,low){
				this.message = '';
				this.testModel = true;
				this.testData = low;
			},
			testBtn:function(){
				var vm = this,aaa = {};
				for(var i=0;i<this.testData.params.length;i++){
					aaa[this.testData.params[i].param] = this.testData.params[i].param_sample;
				};
				aaa.bus_service_id = this.testData.serviceid;
				$.getJSON(SYSCONFIG.apiurl("testService!service"),aaa).then(function(json){
					vm.message = json;
				})
			},
			bind:function(index,low){
				var vm = this;
				this.testData = low;
				this.bindModel = true;
				$.ajaxSettings.async = false;
				$.getJSON(SYSCONFIG.apiurl("getMateDataCode!db"),{resourceId:low.serviceid,resourceType:1}).then(function(json){
					if(json != null){
						vm.bindData = json.matedata_code;
					}else {
						vm.bindData = '';
					}
				})
				if(this.$refs.tree!= undefined) this.$refs.tree.setCheckedKeys([this.bindData]);
				$.ajaxSettings.async = true;
			},
			bindBtn:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl("addMateDataResources!db"),{code:this.dataCode,resourceId:this.testData.serviceid,resourceType:1}).then(function(json){
					vm.$message({message:'绑定成功',type:'success',showClose:true})
					vm.bindModel = false;
				})
			},
			piliang:function(){
				var vm = this;
				this.plModel = true;
				this.dataCode2 = '';
				if(this.$refs.tree2!= undefined) this.$refs.tree2.setCheckedKeys([]);
			},
			plBtn:function(){
				var vm = this;
				var reId = '',l = this.multipleSelection.length;
				for(var i=0;i<l-1;i++){
					reId = reId + this.multipleSelection[i].serviceid + ",";
				}
				reId = reId + this.multipleSelection[l-1].serviceid
				$.getJSON(SYSCONFIG.apiurl("addMateDataResources!db"),{code:this.dataCode2,resourceId:reId,resourceType:1}).then(function(json){
					vm.$message({message:'绑定成功',type:'success',showClose:true})
					vm.plModel = false;
				})
			},
			handleCheckChange:function(data, checked, node) {
		        if(data.type == 'matedata' && checked) {
		        	this.$refs.tree.setCheckedKeys([]);
		        	this.$refs.tree.setCheckedKeys([data.code]);
		        	this.dataCode = data.code;
		        }
		        /*var indexs = this.selectOrg.indexOf(data.code)
		        if (indexs<0 && this.selectOrg.length ===1 && checked){
		        	console.log('only one')
		        	this.$message({message:'只能选择一个机构',type:'error',showClose:true})
		        	this.$refs.tree.setChecked(false)
		        }else if(this.selectOrg.length ===0 && checked){
		        	this.selectOrg = []
		        	this.selectOrg.push(data.code)
		        }else if(indexs >=0 && this.selectOrg.length ===1 && !checked){
		        	this.selectOrg = []
		        }*/
		    },
		    handleCheckChange2:function(data, checked, node) {
		        if(data.type == 'matedata' && checked) {
		        	this.$refs.tree2.setCheckedKeys([]);
		        	this.$refs.tree2.setCheckedKeys([data.code]);
		        	this.dataCode2 = data.code;
		        }
		    },
		    handleSelectionChange:function(val){
		    	this.multipleSelection = val;
				if(this.multipleSelection.length>0){
					this.duoxuan = false;
				}else{
					this.duoxuan = true;
				}
		    },
		    handleSizeChange:function(val) {
		    	var vm = this;
		    	this.abcd.pageSize = val
				$.getJSON(SYSCONFIG.apiurl('getServiceList!service'),this.abcd)
				.then(function(json){
					vm.listFy = json;
					vm.list = [];
					for(var i=0;i<json.recordList.length;i++){
						if(json.recordList[i].response_sample.indexOf('\n') !== -1){
							vm.list.push(json.recordList[i]);
						}else{
							var aaa = '';
							for(var j=0;j<json.recordList[i].response_sample.length;j++){
								if(json.recordList[i].response_sample[j] == '[' || json.recordList[i].response_sample[j]=='{'
								|| json.recordList[i].response_sample[j] == ']' || json.recordList[i].response_sample[j+1]==']'
								|| json.recordList[i].response_sample[j] == '}' || json.recordList[i].response_sample[j+1]=='}'
								|| json.recordList[i].response_sample[j] == ',' 
								|| (json.recordList[i].response_sample[j]=='>' && json.recordList[i].response_sample[j-1]=='/')){
									aaa = aaa + json.recordList[i].response_sample[j] + '\n'
								}else{
									aaa = aaa + json.recordList[i].response_sample[j]
								}
							}
							json.recordList[i].response_sample = aaa
							vm.list.push(json.recordList[i]);
						}
					}
				});
		    },
		    handleCurrentChange:function(val) {
		    	var vm = this;
		    	this.abcd.pageNum = val;
				$.getJSON(SYSCONFIG.apiurl('getServiceList!service'),this.abcd)
				.then(function(json){
					vm.listFy = json;
					vm.list = [];
					for(var i=0;i<json.recordList.length;i++){
						if(json.recordList[i].response_sample.indexOf('\n') !== -1){
							vm.list.push(json.recordList[i]);
						}else{
							var aaa = '';
							for(var j=0;j<json.recordList[i].response_sample.length;j++){
								if(json.recordList[i].response_sample[j] == '[' || json.recordList[i].response_sample[j]=='{'
								|| json.recordList[i].response_sample[j] == ']' || json.recordList[i].response_sample[j+1]==']'
								|| json.recordList[i].response_sample[j] == '}' || json.recordList[i].response_sample[j+1]=='}'
								|| json.recordList[i].response_sample[j] == ',' 
								|| (json.recordList[i].response_sample[j]=='>' && json.recordList[i].response_sample[j-1]=='/')){
									aaa = aaa + json.recordList[i].response_sample[j] + '\n'
								}else{
									aaa = aaa + json.recordList[i].response_sample[j]
								}
							}
							json.recordList[i].response_sample = aaa
							vm.list.push(json.recordList[i]);
						}
					}
				});
		    },
		    paixu:function(val){
		    	var vm = this;
		    	if(val.order == null) this.abcd.order = null;
		    	if(val.order == 'ascending') this.abcd.order = 'asc';
		    	if(val.order == 'descending') this.abcd.order = 'desc';
				$.getJSON(SYSCONFIG.apiurl('getServiceList!service'),this.abcd)
				.then(function(json){
					vm.listFy = json;
					vm.list = [];
					for(var i=0;i<json.recordList.length;i++){
						if(json.recordList[i].response_sample.indexOf('\n') !== -1){
							vm.list.push(json.recordList[i]);
						}else{
							var aaa = '';
							for(var j=0;j<json.recordList[i].response_sample.length;j++){
								if(json.recordList[i].response_sample[j] == '[' || json.recordList[i].response_sample[j]=='{'
								|| json.recordList[i].response_sample[j] == ']' || json.recordList[i].response_sample[j+1]==']'
								|| json.recordList[i].response_sample[j] == '}' || json.recordList[i].response_sample[j+1]=='}'
								|| json.recordList[i].response_sample[j] == ',' 
								|| (json.recordList[i].response_sample[j]=='>' && json.recordList[i].response_sample[j-1]=='/')){
									aaa = aaa + json.recordList[i].response_sample[j] + '\n'
								}else{
									aaa = aaa + json.recordList[i].response_sample[j]
								}
							}
							json.recordList[i].response_sample = aaa
							vm.list.push(json.recordList[i]);
						}
					}
				});
		    },
		},
		mounted:function(){
			this.getList();
			this.getValue1();
			this.getValue2();
		}
	};
});
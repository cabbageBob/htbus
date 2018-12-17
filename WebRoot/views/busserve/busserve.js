define(['text!./busserve.html'],function(tpl){
	return {
		template:tpl,
		data:function(){
			var vm = this;
			return {
				message:'',
				testModel:false,
				testData:'',
				list:[],
				listcopy:[],
				days:null,
				listLoading:false,
				filter:{
					year:null
				},
				value1:'',
				value2:'',
				value3:'',
				mess:'',
				aaa:[],
				yewu:[],
				addcsVisible:false,
				scVisible:false,
				editVisible:false,
				delVisible:false,
				editForm:{},
				addFormVisible:false,
				addcsForm:{
					
				},
				addForm:{
					name:'',
					path:'',
					add:'',
					fang:'',
					response_sample:'',
				},
				addFormRules: {
					name: [
						{ required: true, message: '接口名称为必填项', trigger: 'blur' }
					],
					servicename: [
						{ required: true, message: '接口名称为必填项', trigger: 'blur' }
					],
					path: [
						{ required: true, message: '方法名为必填项', trigger: 'blur' }
					],
					method: [
						{ required: true, message: '方法名为必填项', trigger: 'blur' },
						{ validator:this.messages.bind(this), trigger: 'blur' }
					],
					add: [
					    { required: true, message: '来源地址为必填项', trigger: 'blur' }
					],
					fang: [
						{ required: true, message: '请选择你的请求方式', trigger: 'change' }
					],
					from_request_type:[
					    { required: true, message: '请选择你的请求方式', trigger: 'change' }
					],
					response_sample:[
					    { required: true, message: '响应示例为必填项', trigger: 'blur' }
					]
				}
			};
		},
		methods:{
			getList:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('getAppServiceList!service'))
				.then(function(json){
					vm.list = json;
					vm.listcopy = json;
				});
			},
			selectValue1:function(){
				this.selectV(this.value1);
			},
			selectV:function(a){
				var reg = new RegExp(a);
				this.list=[];
				for(var i=0; i <  this.listcopy.length; i ++) {
					if(this.listcopy[i].servicename.match(reg) || a=='' || this.listcopy[i].path.match(reg)){
						this.list.push(this.listcopy[i])
					}
				}
			},
			edit:function(index, low){
				var vm = this;
				this.par = low;
				aaa=[];
				$.getJSON(SYSCONFIG.apiurl('getServiceInfo!service'),{serviceid:low.serviceid})
            	.then(function(json){
            		vm.editVisible = true;
            		vm.editForm = json;
            		if(json.path!=null){
            			vm.aaa = json.path.split('/');
            		}else{
            			vm.aaa[1] = 'underfined';
            			vm.aaa[2] = 'underfined';
            		}
            	});
			},
			del:function(index, low){
				this.delVisible = true;
				this.par = low;
			},
			test:function(index,low){
				var vm = this;
				this.message = '';
				$.getJSON(SYSCONFIG.apiurl('getServiceInfo!service'),{serviceid:low.serviceid})
            	.then(function(json){
            		vm.testModel = true;
    				vm.testData = json;
            	})
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
			zhuce:function(){
				this.scVisible = true;
			},
			addcs:function(){
				this.addcsVisible = true;
			},
			messages:function(rule,value,callback){
				$.getJSON(SYSCONFIG.apiurl('checkMethod!service'),{serviceid:this.editForm.serviceid,method:value})
            	.then(function(json){
            		if(json.success==false){
            			callback(json.message)
            		}else{
            			callback()
            		}
            	});
			},
			editSubmit:function(formName){
				//var par = Object.assign({}, this.editForm);
				var vm = this,jsondata={},suc = 1;
				$.ajaxSettings.async = false;
				this.$refs[formName].validate((valid) => {
			          if (valid) {
			          } else {
			        	suc = 0;
			            return false;
			          }
			    });
				$.ajaxSettings.async = true;
				if(suc == 0) return;
				jsondata.serviceid=this.editForm.serviceid;
				jsondata.servicename=this.editForm.servicename;
				jsondata.method=this.editForm.method;
				jsondata.remark=this.editForm.remark;
				jsondata.response_type=this.editForm.response_type;
				jsondata.response_sample=this.editForm.response_sample;
				jsondata.cache_type=this.editForm.cache_type;
				jsondata.cache_effective=this.editForm.cache_effective;
				jsondata.from_url=this.editForm.from_url;
				jsondata.from_request_type=this.editForm.from_request_type;
				this.messages.bind(this);
				$.getJSON(SYSCONFIG.apiurl('updateService!service'),{jsondata:JSON.stringify(jsondata)})
            	.then(function(json){
            		if(json.success==true){
            			vm.editVisible = false;
            			vm.getList();
            		}else{
            			vm.editVisible = false;
            			vm.getList();
            		}
            	});
			},
			fbSubmit:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('checkMethod!service'),{serviceid:this.editForm.serviceid,method:this.editForm.method})
            	.then(function(json){
            		if(json.success==true){
            			$.getJSON(SYSCONFIG.apiurl('publishService!service'),{jsondata:JSON.stringify(vm.editForm)})
                    	.then(function(json2){
                    		
                    	});
            			vm.editVisible = false;
            			vm.getList();
            		}else{
            			vm.editVisible = false;
            			vm.getList();
            		}
            	});
			},
			delSubmit:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('deleteService!service'),{serviceid:this.par.serviceid})
            	.then(function(json){
            		vm.delVisible = false;
            		vm.getList();
            	});
			},
			scSubmit:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('regService!service'),
					{servicename:this.addForm.name,from_url:this.addForm.add,from_request_type:this.addForm.fang})
            	.then(function(json){
            		vm.scVisible = false;
            		vm.getList();
            	});
			},
			addcsSubmit:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('addParam!service'),
					{serviceid:this.editForm.serviceid,param:this.addcsForm.param,remark:this.addcsForm.remark,type:this.addcsForm.type,sample:this.addcsForm.sample})
            	.then(function(json){
            		vm.addcsVisible = false;
            		vm.edit(1, vm.par)
            	});
			},
			delcs:function(index,low){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('deleteParam!service'),{serviceid:this.par.serviceid,param:low.param})
				.then(function(json){
            		vm.edit(1, vm.par)
				});
			},
		},
		mounted:function(){
			this.getList();
		}
	};
});
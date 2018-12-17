define(['text!./busdata.html'],function(tpl){
	return {
		template:tpl,
		data:function(){
			var vm = this;
			return {
				value1:'',
				value2:'',
				value3:'0',
				value4:'',
				value5:'0',
				editableTabsValue: '1',
		        editableTabs: [{
		        	title: '数据库服务实例',
			        name: '1',
			        content: 'Tab 1 content'
		        }],
				editableTabsValue2: '1',
		        editableTabs2: [],
		        tabIndex: 1,
				list:[],
				listcopy:[],
				list2:[],
				listcopy2:[],
				list3:[],
				listcopy3:[],
				docurl:'',
				listLoading:false,
				editVisible1:false,
				editVisible2:false,
				editVisible3:false,
				editVisible12:false,
				editVisible22:false,
				editVisible13:false,
				editVisible23:false,
				par:'',
				par2:'',
				par3:'',
				part:'',
				par2t:'',
				editpar:'',
				bindModel:false,
				bindList:[],
				multipleSelection:[],
				selectNum:[],
			};
		},
		methods:{
			getList:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('getDBListByApp!db'))
				.then(function(json){
					vm.list = json;
					function sortsta(a,b){  
						return b.sta-a.sta 
		            } 
					vm.list.sort(sortsta); 
					vm.listcopy = vm.list;
				});
			},
			getValue2:function(){
				
			},
			selectValue1:function(){
				var a = this.value1;
				var reg = new RegExp(a,'i');
				this.list=[];
				for(var i=0; i <  this.listcopy.length; i ++) {
					if(this.listcopy[i].instance_name.match(reg) || a=='' || this.listcopy[i].label.match(reg)){
						this.list.push(this.listcopy[i])
					}
				}
			},
			daochu:function(){
				this.table2Excel("table1", 'Sheet 1', 'DBlist');
			},
			selectValue2:function(){
				this.selectLeft(this.value3,this.value2);
			},
			selectValue3:function(){
				this.selectLeft(this.value3,this.value2);
			},
			selectLeft:function(a,b){
				var reg = new RegExp(b);
				this.list2=[];
				for(var i=0; i <  this.listcopy2.length; i ++) {
					if((a==(this.listcopy2[i].label=='-'?'2':'1') || a=='0') && (this.listcopy2[i].tbname.match(reg) || b=='' || this.listcopy2[i].label.match(reg))){
						this.list2.push(this.listcopy2[i])
					}
				}
			},
			selectValue4:function(){
				this.selectRight(this.value5,this.value4);
			},
			selectValue5:function(){
				this.selectRight(this.value5,this.value4);
			},
			selectRight:function(a,b){
				var reg = new RegExp(b);
				this.list3=[];
				for(var i=0; i <  this.listcopy3.length; i ++) {
					if((a==(this.listcopy3[i].label==null?'2':'1') || a=='0') && (this.listcopy3[i].field.match(reg) || b=='' || this.listcopy3[i].label.match(reg))){
						this.list3.push(this.listcopy3[i])
					}
				}
			},
			daochu2:function(){
				this.table2Excel("table2", 'Sheet 1', 'tablelist');
			},
			daochu3:function(){
				this.table2Excel("table3", 'Sheet 1', 'fieldlist');
			},
			table2Excel: function (table, sheetname, filename) {
	            var uri = 'data:application/vnd.ms-excel;base64,',
	                template = '<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40"><head><style>{style}</style><!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet><x:Name>{worksheet}</x:Name><x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions></x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]--></head><body><table>{table}</table></body></html>',
	                base64 = function (s) {
	                    return window.btoa(unescape(encodeURIComponent(s)));
	                },
	                format = function (s, c) {
	                    return s.replace(/{(\w+)}/g, function (m, p) {
	                        return c[p];
	                    });
	                };

	            if (!table.nodeType)
	                table = document.getElementById(table);
	            var ctx = {
	                worksheet: sheetname || 'Worksheet',
	                table: table.innerHTML,
	                style: $('style').html().replace('1px','.5pt')
	            };
	            var el = document.getElementById("table2excel");
	            if (!el) {
	                el = document.createElement("a");
	                el.id = "table2excel";
	                el.style.display = "none;";
	                document.body.appendChild(el);
	            }
	            el.href = uri + base64(format(template, ctx));
	            el.download = filename;
	            el.click();
	        },
			addTab:function(index,low) {
				var vm = this;
				this.part = low;
				this.nowIndex = index;
				this.list3 = [];
				for(var i=0;i<this.editableTabs2.length;i++){
					if(this.editableTabs2[i].title == this.list[index].instance_name){
						this.editableTabsValue2 = this.editableTabs2[i].name;
						this.list2 = this.editableTabs2[i].list2;
						return;
					}
				};
				$.ajaxSettings.async = false;
				$.getJSON(SYSCONFIG.apiurl('analyzeDBTables!db'),{instance_id:low.instance_id,dbname:low.instance_name})
				.then(function(json){
					vm.list2 = json;
					function sortsta(a,b){  
						return b.sta-a.sta 
		            } 
					vm.list2.sort(sortsta); 
					vm.listcopy2 = vm.list2;
				});
				$.ajaxSettings.async = true;
		        let newTabName = ++this.tabIndex + '';
		        this.editableTabs2.push({
		          title: low.instance_name,
		          name: newTabName,
		          content: 'New Tab content',
		          list2: vm.list2,
		          part:low
		        });
		        this.editableTabsValue2 = newTabName;
		    },
		    removeTab:function(targetName) {
		    	this.tabIndex = this.tabIndex -1;
		        let tabs = this.editableTabs2;
		        let activeName = this.editableTabsValue2;
		        if (activeName === targetName) {
		          tabs.forEach((tab, index) => {
		            if (tab.name === targetName) {
		              let nextTab = tabs[index + 1] || tabs[index - 1];
		              if (nextTab) {
		                activeName = nextTab.name;
		                this.part = nextTab.part;
		                this.list2 = nextTab.list2;
		                this.listcopy2 = this.list2;
		                this.list3 = [];
				    	this.value2 = '';
						this.value3 = '0';
						this.value4 = '';
						this.value5 = '0';
		              }
		              if (nextTab==undefined){
		            	activeName = '1';
		              }
		            }
		          });
		        }
		        this.editableTabsValue2 = activeName;
		        this.editableTabs2 = tabs.filter(tab => tab.name !== targetName);
		    },
		    addfield:function(index,low){
		    	var vm = this;
		    	this.par2t = low;
		    	$.getJSON(SYSCONFIG.apiurl('analyzeTableFields!db'),{instance_id:this.part.instance_id,dbname:
					this.part.instance_name,tbname:low.tbname})
				.then(function(json){
					vm.list3 = json;
					vm.listcopy3 = json;
				});
		    	
		    },
		    editLabel:function(index,low){
		    	this.editVisible1 = true;
		    	this.par = low
		    },
		    labelSubmit:function(){
		    	var vm = this;
				$.getJSON(SYSCONFIG.apiurl('remarkDB!db'),{instance_id:this.par.instance_id,dbname:
					this.par.instance_name,remark:this.par.label})
				.then(function(json){
					vm.editVisible1 = false;
					vm.getList();
				});
		    },
		    editLabel2:function(index,low){
		    	this.editVisible12 = true;
		    	this.par2 = low
		    },
		    labelSubmit2:function(){
		    	var vm = this;
				$.getJSON(SYSCONFIG.apiurl('remarkTable!db'),{instance_id:this.part.instance_id,dbname:
					this.part.instance_name,remark:this.par2.label,tablename:this.par2.tbname})
				.then(function(json){
					vm.editVisible12 = false;
					$.getJSON(SYSCONFIG.apiurl('analyzeDBTables!db'),{instance_id:vm.part.instance_id,dbname:vm.part.instance_name})
					.then(function(json2){
						vm.list2 = json2;
						function sortsta(a,b){  
							return b.sta-a.sta 
			            } 
						vm.list2.sort(sortsta); 
						vm.listcopy2 = vm.list2;
						//this.editableTabs2
					});
				});
		    },
		    editLabel3:function(index,low){
		    	this.editVisible13 = true;
		    	this.par3 = low
		    },
		    labelSubmit3:function(){
		    	var vm = this;
				$.getJSON(SYSCONFIG.apiurl('remarkField!db'),{instance_id:this.part.instance_id,dbname:
					this.part.instance_name,remark:this.par3.label,tablename:this.par2t.tbname,filedname:this.par3.field})
				.then(function(json){
					vm.editVisible13 = false;
					$.getJSON(SYSCONFIG.apiurl('analyzeTableFields!db'),{instance_id:vm.part.instance_id,dbname:vm.part.instance_name,tbname:vm.par2t.tbname})
					.then(function(json2){
						vm.list3 = json2;
						function sortsta(a,b){  
							return b.sta-a.sta 
			            } 
						vm.list3.sort(sortsta); 
						vm.listcopy3 = vm.list3;
					});
				});
		    },
		    editRemark:function(index,low){
		    	this.editVisible2 = true;
		    	this.par = low
		    },
		    remarkSubmit:function(){
		    	var vm = this;
				$.getJSON(SYSCONFIG.apiurl('setRemark!db'),{id:this.par.id,remark:this.par.remark})
				.then(function(json){
					vm.editVisible2 = false;
					vm.getList();
				});
		    },
		    editRemark2:function(index,low){
		    	this.editVisible22 = true;
		    	this.par2 = low
		    },
		    remarkSubmit2:function(){
		    	var vm = this;
				$.getJSON(SYSCONFIG.apiurl('setRemark!db'),{id:this.part.id+'-'+this.par2.tbname,remark:this.par2.remark})
				.then(function(json){
					vm.editVisible22 = false;
					$.getJSON(SYSCONFIG.apiurl('analyzeDBTables!db'),{instance_id:vm.part.instance_id,dbname:vm.part.instance_name})
					.then(function(json2){
						vm.list2 = json2;
						function sortsta(a,b){  
							return b.sta-a.sta 
			            } 
						vm.list2.sort(sortsta); 
						vm.listcopy2 = vm.list2;
					});
				});
		    },
		    editRemark3:function(index,low){
		    	this.editVisible23 = true;
		    	this.par3 = low
		    },
		    remarkSubmit3:function(){
		    	var vm = this;
				$.getJSON(SYSCONFIG.apiurl('setRemark!db'),{id:this.part.id+'-'+this.par2t.tbname+'-'+this.par3.field,remark:this.par3.remark})
				.then(function(json){
					vm.editVisible23 = false;
					$.getJSON(SYSCONFIG.apiurl('analyzeTableFields!db'),{instance_id:vm.part.instance_id,dbname:vm.part.instance_name,tbname:vm.par2t.tbname})
					.then(function(json2){
						vm.list3 = json2;
						function sortsta(a,b){  
							return b.sta-a.sta 
			            } 
						vm.list3.sort(sortsta); 
						vm.listcopy3 = vm.list3;
					});
				});
		    },
		    edit:function(index,low){
		    	var vm = this;
		    	this.editpar = low
				$.getJSON(SYSCONFIG.apiurl('getDatabaseDicdoc!db'),{instance_id:low.instance_id,dbname:low.instance_name})
				.then(function(json){
					vm.editVisible3 = true;
					vm.docurl = json.docurl;
				});
		    },
		    expSubmit:function(){
		    	var vm = this;
				$.getJSON(SYSCONFIG.apiurl('createDatabaseDicdoc!db'),{instance_id:this.editpar.instance_id,dbname:this.editpar.instance_name})
				.then(function(json){
					vm.docurl = json.docurl;
				});
		    },
		    bind:function(index,low){
		    	var vm = this;
		    	this.editpar = low;
		    	$.getJSON(SYSCONFIG.apiurl('getAppListWithDBBind!db'),{instance_id:low.instance_id,dbname:low.instance_name})
				.then(function(json){
					vm.bindList = json;
					vm.bindModel = true;
					vm.$nextTick(function(){
						for(var i=0;i<json.length;i++){
							if(json[i].isbind == 1){
								vm.$refs.multipleTable.toggleRowSelection(json[i],true);
							} 
						}
					})
				});
		    },
		    bindSubmit:function(){
		    	var vm = this;
		    	$.getJSON(SYSCONFIG.apiurl('bindDBtoApp!db'),{instance_id:this.editpar.instance_id,dbname:this.editpar.instance_name,appids:this.selectNum})
				.then(function(json){
					if(json.success == true){
						vm.getList();
					};
					vm.bindModel = false;
				});
		    },
		    handleSelectionChange:function(val) {
		        this.multipleSelection = val;
		        this.selectNum = '';
		        if(this.multipleSelection.length ==0 ){
		        	return;
		        }
		        this.selectNum = this.multipleSelection[0].appid;
		        for(var i=1;i<this.multipleSelection.length;i++){
		        	this.selectNum = this.selectNum + ',' + this.multipleSelection[i].appid;
		        }
		    },
		    handleClick:function(tab,event){
		    	console.log(tab);
		    	for(var i=0;i<this.editableTabs2.length;i++){
		    		if(this.editableTabs2[i].name == tab.name){
				    	this.part = this.editableTabs2[i].part;
				    	this.list2 = this.editableTabs2[i].list2;
				    	this.listcopy2 = this.list2;
				    	this.list3 = [];
				    	this.value2 = '';
						this.value3 = '0';
						this.value4 = '';
						this.value5 = '0';
		    		}
		    	}
		    }
		},
		mounted:function(){
			this.getList();
			this.getValue2();
		}
	};
});
define(['text!./database.html'],function(tpl){
	return {
		template:tpl,
		data:function(){
			var vm = this;
			return {
				duoxuan:true,
				props: {
			        children: 'child',
			        label: 'name',
			    },
			    dataCode:'',
			    dataCode2:'',
				treeData:[],
				activeName: 'first',
				multipleSelection:[],
				mul:[],
				selectNum:'',
				options: [
				          {value: 'MSSQL',label: 'MSSQL'}, 
				          {value: 'MYSQL',label: 'MYSQL'}, 
				          {value: 'ORACLE',label: 'ORACLE'}
				],
				valueSearch:'',
				valueId:'',
				value1:'',
				value2:'',
				value3:'0',
				value4:'',
				value5:'0',
				valueClass:null,
				path:'0',
				ckModel:false,
				xzModel:false,
				scModel:false,
				editableTabs0: [{
		        	title: '数据资源检索',
			        name: '0',
			        content: 'Tab 0 content'
		        }],
		        editableTabsValue: '1',
		        editableTabs: [{
		        	title: '数据库服务实例',
			        name: '1',
			        content: 'Tab 1 content'
		        }],
				editableTabsValue2: '1',
		        editableTabs2: [],
		        tabIndex: 1,
		        menuList:[],
		        menupar:[],
		        addmenupar:{
		        	dbtype:'',
		        },
		        sqlpar:'',
		        sqlList:{},
				list:[],
				listcopy:[],
				list2:[],
				listcopy2:[],
				list3:[],
				listcopy3:[],
				listSearch:[],
				docurl:'',
				bindList:[],
				multipleSelection:[],
				selectNum:[],
				listLoading:false,
				editVisible1:false,
				editVisible2:false,
				editVisible3:false,
				editVisible12:false,
				editVisible22:false,
				editVisible13:false,
				editVisible23:false,
				bindModel:false,
				editpar:'',
				par:'',
				par2:'',
				par3:'',
				part:'',
				par2t:'',
				sqlData:{
					sql: [{ required: true, message: '请输入必要的SQL语句', trigger: 'blur' }],
				},
				menuData:{
					name: [{ required: true, message: '中文名称为必填项', trigger: 'blur' }],
				    ip: [{ required: true, message: 'IP地址为必填项', trigger: 'blur' }],
				    port: [{ required: true, message: '端口为必填项', trigger: 'blur' }],
				    instance_name: [{ required: true, message: '实例名为必填项', trigger: 'blur' }],
				    dbtype: [{ required: true, message: '请选择数据库类型', trigger: 'blur' }],
				    account: [{ required: true, message: '账号为必填项', trigger: 'blur' }],
				    password: [{ required: true, message: '密码为必填项', trigger: 'blur' }],
				},
				dataBindModel:false,
				dataBindCode:'',
				plModel:false,
			};
		},
		methods:{
			getValue1:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('getMatedataTreeBind!db'))
				.then(function(json){
					vm.treeData = json.htData;
				});
			},
			/*getMenu:function(){
				var vm = this;
				$.ajaxSettings.async = false;
				$.getJSON(SYSCONFIG.apiurl('getInstanceList!db'))
				.then(function(json){
					for(var i=0;i<json.length;i++){
						json[i].indexId = i;
					}
					vm.menuList = json;
				});
				$.ajaxSettings.async = true;
			},*/
			getList:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('getInstanceList!db'))
				.then(function(json){
					vm.list = json;
					vm.listcopy = vm.list;
				});
				/*$.getJSON(SYSCONFIG.apiurl('getDBlistByIID!db'),{instance_id:this.menuList[0].instance_id})
				.then(function(json){
					vm.list = json;
					function sortsta(a,b){  
						return b.sta-a.sta 
		            } 
					vm.list.sort(sortsta); 
					vm.listcopy = vm.list;
				});*/
			},
			/*handleselect:function(index,indexPath){
				var vm = this;
				this.path = index;
				$.getJSON(SYSCONFIG.apiurl('getDBlistByIID!db'),{instance_id:this.menuList[index].instance_id})
				.then(function(json){
					vm.list = json;
					function sortsta(a,b){  
						return b.sta-a.sta 
		            } 
					vm.list.sort(sortsta); 
					vm.listcopy = vm.list;
				})
			},*/
			searchtab:function(val){
				this.addTab2(val);
			},
			chakan:function(index,low){
				var vm = this;
		    	this.editpar = low
				this.ckModel = true;
				$.getJSON(SYSCONFIG.apiurl('getInstance!db'),{instance_id:this.editpar.instance_id})
				.then(function(json){
					vm.menupar = json;
				})
			},
			ckSubmit:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('updateInstance!db'),{instance_id:this.menupar.instance_id,
					name:this.menupar.name,instance_name:this.menupar.instance_name,ip:this.menupar.ip,
					port:this.menupar.port,account:this.menupar.account,password:this.menupar.password,
					dbtype:this.menupar.dbtype})
				.then(function(json){
					if(json.success == true){
						//vm.getMenu();
						vm.getList();
					};
					vm.ckModel = false;
				})
			},
			xinzeng:function(){
				this.xzModel = true;
			},
			xzSubmit:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('addInstance!db'),{dbtype:this.addmenupar.dbtype,
					name:this.addmenupar.name,instance_name:this.addmenupar.instance_name,ip:this.addmenupar.ip,
					port:this.addmenupar.port,account:this.addmenupar.account,password:this.addmenupar.password})
				.then(function(json){
					if(json.success ==true){
						//vm.getMenu();
						vm.getList();
					};
					vm.xzModel = false;
				})
			},
			shanchu:function(index,low){
				var vm = this;
		    	this.editpar = low;
				this.scModel = true;
			},
			scSubmit:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('deleteInstance!db'),{instance_id:this.editpar.instance_id})
				.then(function(json){
					if(json.success == true){
						//vm.getMenu();
						vm.getList();
					}
					vm.scModel = false;
				})
			},
			selectValue1:function(){
				var a = this.value1;
				var reg = new RegExp(a,'i');
				this.list=[];
				for(var i=0; i <  this.listcopy.length; i ++) {
					if(this.listcopy[i].name.match(reg) || a=='' || this.listcopy[i].instance_name.match(reg)){
						this.list.push(this.listcopy[i])
					}
				}
			},
			jiansuo:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('searchField!db'),{keyword:this.valueSearch})
				.then(function(json){
					vm.listSearch = json;
				})
			},
			daochu:function(){
				this.table2Excel("table1", 'Sheet 1', 'DBlist');
			},
			selectValue2:function(){
				this.selectLeft(this.value3,this.value2,this.valueClass);
			},
			selectLeft:function(a,b,c){
				var reg = new RegExp(b,'i');
				this.list2=[];
				for(var i=0; i <  this.listcopy2.length; i ++) {
					if((a==(this.listcopy2[i].label=='-'?'2':'1') || a=='0') && (this.listcopy2[i].tbname.match(reg) || b=='' || this.listcopy2[i].label.match(reg)) 
					&& ((this.listcopy2[i].class !=null && c==1) || (this.listcopy2[i].class ==null && c==0) || c==null)){
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
				var reg = new RegExp(b,'i');
				this.list3=[];
				for(var i=0; i <  this.listcopy3.length; i ++) {
					if((a==(this.listcopy3[i].label==null?'2':'1') || a=='0') && (this.listcopy3[i].field.match(reg) || b=='' 
					|| (this.listcopy3[i].label !=null?this.listcopy3[i].label.match(reg):false))){
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
					if((this.editableTabs2[i].title == this.list[index].label) || (this.editableTabs2[i].title == this.list[index].instance_name)){
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
		          title: low.instance_name ,
		          dbname: low.instance_name,
		          name: newTabName,
		          content: 'New Tab content',
		          list2: vm.list2,
		          part:low
		        });
		        this.activeName = 'first';
		        this.editableTabsValue2 = newTabName;
		    },
		    addTab2:function(low) {
				var vm = this;
				low.id = low.instanceId + '-' + low.instance_name;
				low.label = low.dbDescription;
				low.instance_id = low.instanceId;
				this.part = low;
				this.list3 = [];
				for(var i=0;i<this.editableTabs2.length;i++){
					if((this.editableTabs2[i].title == low.label) || (this.editableTabs2[i].title == low.instance_name)){
						this.editableTabsValue2 = this.editableTabs2[i].name;
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
						$.getJSON(SYSCONFIG.apiurl('analyzeTableFields!db'),{instance_id:low.instance_id,dbname:low.instance_name,
							tbname:low.tbname})
						.then(function(json){
							vm.list3 = json;
							vm.listcopy3 = json;
						});
						$.ajaxSettings.async = true;
						this.activeName = 'first';
						this.value3 = '0';
						this.value2 = low.tbname;
						this.selectValue3();
						this.value5 = '0';
						this.value4 = low.field;
						this.selectValue5();
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
				$.getJSON(SYSCONFIG.apiurl('analyzeTableFields!db'),{instance_id:low.instance_id,dbname:low.instance_name,
					tbname:low.tbname})
				.then(function(json){
					vm.list3 = json;
					vm.listcopy3 = json;
				});
				$.ajaxSettings.async = true;
		        let newTabName = ++this.tabIndex + '';
		        this.editableTabs2.push({
		          title: low.instance_name ,
		          dbname: low.instance_name,
		          name: newTabName,
		          content: 'New Tab content',
		          list2: vm.list2,
		          part:low
		        });
		        this.activeName = 'first';
		        this.value3 = '0';
				this.value2 = low.tbname;
				this.selectValue3();
				this.value5 = '0';
				this.value4 = low.field;
				this.selectValue5();
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
						this.activeName = 'first';
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
				$.getJSON(SYSCONFIG.apiurl('setRemark!db'),{id:this.part.instance_id+'-'+this.par2.tbname,remark:this.par2.remark})
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
				$.getJSON(SYSCONFIG.apiurl('setRemark!db'),{id:this.part.instance_id+'-'+this.par2t.tbname+'-'+this.par3.field,remark:this.par3.remark})
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
		    	this.editpar = low;
				$.getJSON(SYSCONFIG.apiurl('getDatabaseDicdoc!db'),{instance_id:low.instance_id,dbname:low.instance_name})
				.then(function(json){
					vm.docurl = json.docurl;
					vm.editVisible3 = true;
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
						//vm.getMenu();
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
						this.activeName = 'first';
		    		}
		    	}
		    },
		    sqlsearch:function(){
		    	var vm = this;
		    	$.getJSON(SYSCONFIG.apiurl('executeSQL!db'),{instance_id:this.part.instance_id,dbname:this.part.instance_name,sql:this.sqlpar})
				.then(function(json){
					vm.sqlList = json;
					if(json.success==true){
						vm.sqlList.success = '是';
					}else {
						vm.sqlList.success = '否';
					}
				});
		    },
		    saveSql:function(){//保存为文件函数
		      var data = this.sqlpar,filename = 'sqltxt'
		      if(/msie/i.test(navigator.userAgent)){
			  var   winSave   =   window.open();  
			  winSave.document.open   ("text/html","utf-8");  
			  winSave.document.write   (data);  
			  winSave.document.execCommand   ('SaveAs',true,filename);  
			  winSave.close();
				}else{
			    var a = document.createElement("a"),
			    file = new Blob([data], {
			        type: "text/plain"
			    });
			    if (window.navigator.msSaveOrOpenBlob) // IE10+
			    window.navigator.msSaveOrOpenBlob(file, filename);
			    else { // Others
			        var url = URL.createObjectURL(file);
			        a.href = url;
			        a.download = filename;
			        document.body.appendChild(a);
			        a.click();
			        setTimeout(function() {
			            document.body.removeChild(a);
			            window.URL.revokeObjectURL(url);
			        },
			        0);
			    }
				}
			},
		    daochuSql:function(){
		    	this.table2Excel("tableSql", 'Sheet 1', 'sqlsh');
		    },
		    tsSubmit:function(){
		    	var vm = this;
				$.getJSON(SYSCONFIG.apiurl('checkDb!db'),{dbtype:this.addmenupar.dbtype,
					instance_name:this.addmenupar.instance_name,ip:this.addmenupar.ip,
					port:this.addmenupar.port,account:this.addmenupar.account,password:this.addmenupar.password})
				.then(function(json){
					if(json.success==true){
						vm.$message.success('连接成功');
					}else{
						vm.$message.error('连接失败');
					}
				})
		    },
		    handle:function(val) {
		        this.mul = val;
		        if(this.mul.length>0){
					this.duoxuan = false;
				}else{
					this.duoxuan = true;
				}
		    },
		    dataBind:function(index,low){
				var vm = this;
				this.par2 = low;
				this.dataBindModel = true;
				$.ajaxSettings.async = false;
				$.getJSON(SYSCONFIG.apiurl("getMateDataCode!db"),{
					resourceId:this.part.instance_id+'-'+low.tbname,resourceType:0
				}).then(function(json){
					if(json != null){
						vm.dataBindCode = json.matedata_code;
					}else {
						vm.dataBindCode = '';
					}
				})
				if(this.$refs.tree!= undefined) this.$refs.tree.setCheckedKeys([this.dataBindCode]);
				$.ajaxSettings.async = true;
			},
			dataBindBtn:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl("addMateDataResources!db"),{
					code:this.dataCode,resourceId:this.part.instance_id+'-'+this.par2.tbname,resourceType:0
				}).then(function(json){
					vm.$message({message:'绑定成功',type:'success',showClose:true})
					vm.dataBindModel = false;
				})
			},
			handleCheckChange:function(data, checked, node) {
		        if(data.type == 'matedata' && checked) {
		        	this.$refs.tree.setCheckedKeys([]);
		        	this.$refs.tree.setCheckedKeys([data.code]);
		        	this.dataCode = data.code;
		        }
		    },
		    piliang:function(){
				var vm = this;
				this.plModel = true;
				this.dataCode2 = '';
				if(this.$refs.tree2!= undefined) this.$refs.tree2.setCheckedKeys([]);
			},
			plBtn:function(){
				var vm = this;
				var reId = '',l = this.mul.length;
				for(var i=0;i<l-1;i++){
					reId = reId + this.part.instance_id+'-'+this.mul[i].tbname + ",";
				}
				reId = reId + this.part.instance_id+'-'+this.mul[l-1].tbname
				$.getJSON(SYSCONFIG.apiurl("addMateDataResources!db"),{code:this.dataCode2,resourceId:reId,resourceType:0}).then(function(json){
					vm.$message({message:'绑定成功',type:'success',showClose:true})
					vm.plModel = false;
				})
			},
			handleCheckChange2:function(data, checked, node) {
		        if(data.type == 'matedata' && checked) {
		        	this.$refs.tree2.setCheckedKeys([]);
		        	this.$refs.tree2.setCheckedKeys([data.code]);
		        	this.dataCode2 = data.code;
		        }
		    },
		},
		mounted:function(){
			//this.getMenu();
			this.getList();
			this.getValue1();
		}
	};
});
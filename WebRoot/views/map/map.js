define(['text!./map.html','css!./map.css'],function(tpl){
	return {
		template:tpl,
		data:function(){
			var vm = this;
			return {
				activeName: 'first',
				valueId:'',
				value1:'',
				par:'0',
				path:'',
				fileUrl:'',
				ckModel:false,
				xzModel:false,
				scModel:false,
				mapModel:false,
		        menuList:[],
		        menupar:[],
		        addmenupar:[],
				list:[],
				list2:{
					fullExtent:{
						spatialReference:{}
					},
					tileInfo:{
						lods:[]
					}
				},
				listLoading:false,
				editpar:'',
				menuData:{
					map_name: [{ required: true, message: '名称为必填项', trigger: 'blur' }],
				    map_url: [{ required: true, message: '地址为必填项', trigger: 'blur' }],
				    username: [{ required: true, message: '账号为必填项', trigger: 'blur' }],
				    password: [{ required: true, message: '密码为必填项', trigger: 'blur' }],
				},
			};
		},
		methods:{
			getMenu:function(){
				var vm = this;
				$.ajaxSettings.async = false;
				$.getJSON(SYSCONFIG.apiurl('getMapServerList!map'))
				.then(function(json){
					for(var i=0;i<json.length;i++){
						json[i].indexId = i;
					}
					vm.menuList = json;
				});
				$.ajaxSettings.async = true;
			},
			getList:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('getMapServices!map'),{id:this.menuList[this.par].id,path:this.path})
				.then(function(json){
					vm.list = json;
					vm.listcopy = vm.list;
				});
			},
			handleselect:function(index,indexPath){
				var vm = this;
				this.par = index;
				this.fileUrl = '';
				$.getJSON(SYSCONFIG.apiurl('getMapServices!map'),{id:this.menuList[index].id,path:''})
				.then(function(json){
					vm.list = json;
					vm.listcopy = vm.list;
				})
				this.list2 = {
					fullExtent:{
						spatialReference:{}
					},
					tileInfo:{
						lods:[]
					}
				}
			},
			chakan:function(){
				var vm = this;
				this.ckModel = true;
				$.getJSON(SYSCONFIG.apiurl('getMapServerById!map'),{id:this.menuList[this.par].id})
				.then(function(json){
					vm.menupar = json;
				})
			},
			ckSubmit:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('updateMapServer!map'),{id:this.menupar.id,
					map_name:this.menupar.map_name,map_url:this.menupar.map_url,
					username:this.menupar.username,password:this.menupar.password})
				.then(function(json){
					if(json.success == true){
						vm.getMenu();
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
				$.getJSON(SYSCONFIG.apiurl('addMapServer!map'),{
					map_name:this.addmenupar.map_name,map_url:this.addmenupar.map_url,
					username:this.addmenupar.username,password:this.addmenupar.password})
				.then(function(json){
					if(json.success ==true){
						vm.getMenu();
						vm.getList();
					};
					vm.xzModel = false;
				})
			},
			shanchu:function(){
				this.scModel = true;
			},
			scSubmit:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('deleteMapServer!map'),{id:this.menuList[this.par].id})
				.then(function(json){
					if(json.success == true){
						vm.getMenu();
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
					if(this.listcopy[i].dbname.match(reg) || a=='' || this.listcopy[i].label.match(reg)){
						this.list.push(this.listcopy[i])
					}
				}
			},
			back:function(){
				this.fileUrl = this.fileUrl.replace('/' + this.path,'');
				if(this.fileUrl == ''){
					this.path = '';
				}else{
					var aaa = this.fileUrl.split('/');
					this.path = aaa[aaa.length-1];
				}
				this.getList();
				this.list2 = {
					fullExtent:{
						spatialReference:{}
					},
					tileInfo:{
						lods:[]
					}
				};
			},
			files:function(low){
				this.path = low;
				this.fileUrl = this.fileUrl + '/' + low;
				this.getList();
			},
			services:function(low){
				var vm = this,uuurl = '',mmurl='';
				if(low.type=='GPServer'){
					this.list2 = {
						fullExtent:{
							spatialReference:{}
						},
						tileInfo:{
							lods:[]
						}
					};
					return;
				}
				if(low.folderName == '/'){
					uuurl = low.serviceName+'.'+low.type
				}else{
					uuurl = low.folderName+'/'+low.serviceName+'.'+low.type
				}
				/*$.getJSON(SYSCONFIG.apiurl('getMapServices!map'),{id:this.menuList[this.par].id,path:uuurl})
				.then(function(json){
					vm.list2 = json
				})*/
				$.ajaxSettings.async = false;
				$.getJSON(SYSCONFIG.apiurl('getMapServerById!map'),{id:this.menuList[this.par].id})
				.then(function(json){
					mmurl = json.map_url;
				})
				$.ajaxSettings.async = true;
				$.getJSON(mmurl+'/rest/services/'+uuurl.split('.')[0]+'/MapServer?f=pjson')
				.then(function(json){
					vm.list2 = json;
					vm.list2.name = low.serviceName+'('+low.type+')';
					vm.list2.type = low.type;
					vm.list2.url = mmurl+'/rest/services/'+uuurl.split('.')[0]+'/MapServer';
					vm.list2.mapThumbnail = mmurl+'/rest/services/'+uuurl.split('.')[0]+'/MapServer?f=jsapi';
					if(vm.list2.tileInfo == undefined) {
						vm.list2.tileInfo = {
								lods:[]
						};
					};
				})
			},
			seaMap:function(){
				var vm = this;
				if(document.getElementById('mm') != undefined){
					document.getElementById('mm').src = '';
				}
				this.mapModel = true;
				setTimeout(function(){
					document.getElementById('mm').src = vm.list2.mapThumbnail
				},1000);
			},
		},
		mounted:function(){
			this.getMenu();
			this.getList();
		}
	};
});
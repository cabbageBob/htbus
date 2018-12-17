define(['text!./file.html','css!./file.css'],function(tpl){
	return {
		template:tpl,
		data:function(){
			var vm = this;
			return {
				dynamicTags: ['设计', '需求'],
				inputVisible: false,
		        inputValue: '',
		        name:'',
		        appid:'',
		        path:'',
		        remark:'',
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
				yewu:[],
				scVisible:false,
				editForm:{},
				addFormVisible:false,
				addForm:{},
			};
		},
		methods:{
			getList:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('getAllFileList!resource'))
				.then(function(json){
					for(var i =0;i<json.length;i++){
						var bbb = json[i].tag.split(',')
						json[i].tag2 = bbb;
					}
					vm.list = json;
					vm.listcopy = json;
				});
			},
			getValue2:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('getAppList!resource'))
				.then(function(json){
					vm.yewu = json;
				});
			},
			selectValue1:function(){
				this.selectV(this.value1,this.value2,this.value3);
			},
			selectValue2:function(){
				this.selectV(this.value1,this.value2,this.value3);
			},
			selectValue3:function(){
				this.selectV(this.value1,this.value2,this.value3);
			},
			selectV:function(a,b,c){
				console.log(c)
				var reg = new RegExp(a),c1 = new RegExp(c[0]),c2 = new RegExp(c[1]);
				this.list=[];
				for(var i=0; i <  this.listcopy.length; i ++) {
					if((this.listcopy[i].filename.match(reg) || a=='') && (b==this.listcopy[i].appname || b=='') && (this.listcopy[i].tag.match(c1) && this.listcopy[i].tag.match(c2) || c=='')){
						this.list.push(this.listcopy[i])
					}
				}
			},
			xiazai:function(index, low){
				console.log(index,low)
				location.href = "../htbus_file/doc/"+low.filename+".doc?";
			},
			shangchuan:function(){
				this.scVisible = true;
			},
			editSubmit:function(){
				var par = Object.assign({}, this.editForm);
			},
			addSubmit:function(){
				var vm = this;
				var par = Object.assign({}, this.addForm);
				$.getJSON('http://218.2.110.162/njgl/addRumei!ywgl?callback=?',par)
            	.then(function(json){
            		vm.addFormVisible = false;
            		vm.getList();
            	});
			},
			handleRemove:function(file, fileList) {
		        console.log(file, fileList);
		    },
		    handlePreview:function(file) {
		        console.log(file);
		    },
		    handleClose:function(tag) {
		        this.dynamicTags.splice(this.dynamicTags.indexOf(tag), 1);
		    },
		    showInput:function() {
		        this.inputVisible = true;
		        this.$nextTick(_ => {
		          this.$refs.saveTagInput.$refs.input.focus();
		        });
		    },
		    handleInputConfirm:function() {
		        let inputValue = this.inputValue;
		        if (inputValue) {
		          this.dynamicTags.push(inputValue);
		        }
		        this.inputVisible = false;
		        this.inputValue = '';
		    },
		    scSubmit:function(){
		    	var vm = this,aaa=this.dynamicTags[0];
		    	for(var i=1;i<this.dynamicTags.length;i++){
		    		aaa = aaa + ',' + this.dynamicTags[i];
		    	};
		    	$.getJSON(SYSCONFIG.apiurl('addAppFile!resource'),{filename:this.name,filepath:this.path,tag:aaa,appid:'',remark:this.remark})
		    	.then(function(json){
		    		if(json.success){
		    			vm.scVisible = false;
	            		vm.getList();
		    		}
		    	})
		    },
		    uploadSuccess:function(response, file, fileList) {
		        this.path = response.path;
		    },
		},
		mounted:function(){
			this.getList();
			this.getValue2();
		}
	};
});
define(['text!./applyAll.html'],function(tpl){
	return {
		template:tpl,
		data:function(){
			var vm = this;
			return {
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
				bbb:null,
				yewu:[],
				editFormVisible:false,
				editForm:{},
				addFormVisible:false,
				addForm:{},
                multipleSelection: [],
                askidlist:[],
                asklist:[],

			};
		},
		methods:{
            dealfailAsk:function(index,row){
            	var vm =this;
                $.getJSON(SYSCONFIG.apiurl('dealServiceAsk!service'),{askid:row.askid,result:"0"})
                    .then(function(json){
                       vm.getList();
                    });
			},
            dealpassAsk:function(index,row){
                var vm =this;
                $.getJSON(SYSCONFIG.apiurl('dealServiceAsk!service'),{askid:row.askid,result:"1"})
                    .then(function(json){
                        vm.getList();
                    });
            },
			getList:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('getServiceAskList!service'))
				.then(function(json){
					vm.list = json;
					vm.listcopy = json;
					aaa=vm.list;
				});
			},
			getValue2:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('getAppList!resource'))
				.then(function(json){
					vm.yewu = json;
				});
			},
            // selectValue1:function(){
            //     this.selectV(this.value1,this.value2,this.value3);
            // },
            selectValue2:function(){
                this.selectV(this.value2,this.value3);
            },
            selectValue3:function(){
                this.selectV(this.value2,this.value3);
            },
            selectV:function(a,b){
                console.log(b);
                this.list=[];
                for(var i=0; i <  this.listcopy.length; i ++) {
                    if((a==this.listcopy[i].ask_appid || a=='') && (b==this.listcopy[i].result || b=='')){
                        this.list.push(this.listcopy[i])
                    }
                }
            },
			handleClick:function(row){
				var aaa='';
				if(row.result=="1"){
					aaa="通过"
				}else if(row.result=="0"){
					aaa="拒绝"
				}else if(row.result=="-1"){
					aaa="未处理"
				};
		    },

            toggleRowSelection:function(){
                var vm = this;
				this.asklist=this.multipleSelection;
                this.askidlist=[];
				for(var i=0;i<this.asklist.length;i++){
					if(i<this.asklist.length-1){
                        this.askidlist=this.askidlist+this.asklist[i].askid+","
					}else{
						this.askidlist=this.askidlist+this.asklist[i].askid
					}
				};
                $.getJSON(SYSCONFIG.apiurl('dealServiceAsk!service'),{askid:this.askidlist,result:"1"})
                    .then(function(json){
                        vm.getList();
                });
			},
            failAsks:function(){
                var vm = this;
                this.asklist=this.multipleSelection;
                this.askidlist=[];
                for(var i=0;i<this.asklist.length;i++){
                    if(i<this.asklist.length-1){
                    	this.askidlist=this.askidlist+this.asklist[i].askid+","
                    }else{
                    	this.askidlist=this.askidlist+this.asklist[i].askid
                    }
                };
                $.getJSON(SYSCONFIG.apiurl('dealServiceAsk!service'),{askid:this.askidlist,result:"0"})
                    .then(function(json){
                        vm.getList();
                    });
			},
			checkboxInit:function(row){
			      if (row.result==1||row.result==0){
                      return 0;//不可勾选
				  }else{
                      return 1;//可勾选
				  }

			},
            handleSelectionChange:function(val) {
                this.multipleSelection = val;
            }
		},
		mounted:function(){
			this.getList();
			this.getValue2();
		},

	};
});
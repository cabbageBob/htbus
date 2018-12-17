define(["././echarts.min.js",'text!./reportService.html','css!./reportService.css'],function(echarts,tpl){
	return {
		template:tpl,
		data:function(){
			var vm = this;
			return {
				list:[],
				list2:[],
				list2err:[],
				list3:[],
				list4:[],
				list4cht:[],
				top3:[],
				listLoading:false,
				start1:new Date().addDay(-29).format('yyyy-MM-dd'),
				end1:new Date().addDay(1).format('yyyy-MM-dd'),
			};
		},
		methods:{
			getList1:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('staDataResourceInfo!db'))
				.then(function(json){
					vm.list = json;
				});
			},
			getList2:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('staServiceResourceInfo!service'))
				.then(function(json){
					vm.list2 = json;
					var data1=[],data2=[];
					for(var i=0;i<json.detail.length;i++){
						data1[i] = json.detail[i].appname;
						data2[i] = json.detail[i].service_cnt;
					}
					var chart1 = echarts.init(document.getElementById('chart1'));
					chart1.setOption({
						    tooltip : {
						        trigger: 'axis',
						        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
						            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
						        }
						    },
						    legend: {
						    },
						    grid: {
						        left: '3%',
						        right: '4%',
						        bottom: '3%',
						        containLabel: true
						    },
						    xAxis:  {
						        type: 'value'
						    },
						    yAxis: {
						        type: 'category',
						        data: data1
						    },
						    series: [
						        {
						            name: '服务接口数量',
						            type: 'bar',
						            label: {
						                normal: {
						                    show: true,
						                    //position: 'insideRight',
						                }
						            },
						            itemStyle:{
						            	normal:{
						            		color:'#1478E4',
						            	}
						            },
						            data: data2
						        },
						    ]
						});
				});
			},
			getList2err:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('staErrorServiceList!service'))
				.then(function(json){
					vm.list2err = json;
				});
			},
			getList3:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('staSurportInfo!service'))
				.then(function(json){
					vm.list3 = json;
				});
			},
			getList4:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('staRequestInfo!service'))
				.then(function(json){
					vm.list4 = json;
					vm.top3 = [];
					if(vm.list4.top3.length>0){
						vm.top3 = vm.list4.top3[0].servicename;
						for(var i=1;i<vm.list4.top3.length;i++){
							vm.top3 = vm.top3 + ',' + vm.list4.top3[i].servicename;
						}
					}
				});
			},
			getList4cht:function(){
				var vm = this;
				$.getJSON(SYSCONFIG.apiurl('staRequestDayLine!service'),{tm1:this.start1,tm2:this.end1})
				.then(function(json){
					vm.list4cht = json;
					var data3=[],data4=[],l=0,ttmm = new Date(vm.start1).format('MM-dd');
					for(var i=0;i<json.length;i++){
						json[i].tm = new Date(json[i].tm).format('MM-dd')
					}
					for(var i=0;i<31;i++){
						if(l<json.length && json[l].tm == ttmm){
							data3[i] = json[l].tm;
							data4[i] = json[l].cnt;
							l++
						}else{
							data3[i] = ttmm;
							data4[i] = 0;
						}
						ttmm = new Date(ttmm).addDay(1).format('MM-dd')
					}
					
					var chart2 = echarts.init(document.getElementById('chart2'));
					chart2.setOption({
						    tooltip : {
						    },
						    legend: {
						    },
						    grid: {
						        left: '3%',
						        right: '4%',
						        bottom: '3%',
						        containLabel: true,	
						    },
						    xAxis:  {
						        type: 'category',
						        boundaryGap: false,
						        data:data3,
						        minInterval:1,
						        maxInterval:1,
						    },
						    yAxis: {
						        type: 'value',
						    },
						    series: [
						        {
						            name: '服务调用次数',
						            type: 'line',
						            /*label: {
						                normal: {
						                    show: true,
						                    //position: 'insideRight',
						                }
						            },*/
						            itemStyle:{
						            	normal:{
						            		color:'#1478E4',
						            	}
						            },
						            data: data4
						        },
						    ]
					});
				});
			},
		},
		mounted:function(){
			this.getList1();
			this.getList2();
			this.getList2err();
			this.getList3();
			this.getList4();
			this.getList4cht();
		}
	};
});
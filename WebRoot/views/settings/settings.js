define(['text!./settings.html'],function(tpl){
	return {
		template:tpl,
		data:function(){
			var vm = this;
			var checkOld = (rule, value, callback) => {
				if (value === '') {
			        callback(new Error('请输入旧密码!'));
			    }else{
			    	callback();
			    }
		    };
		    var validatePass = (rule, value, callback) => {
		    	if (value === '') {
		          callback(new Error('请输入新密码!'));
		        } else {
		          if (this.ruleForm2.checkPass !== '') {
		            this.$refs.ruleForm2.validateField('checkPass');
		          }
		          callback();
		        }
		    };
		    var validatePass2 = (rule, value, callback) => {
		        if (value === '') {
		          callback(new Error('请再次新输入密码!'));
		        } else if (value !== this.ruleForm2.pass) {
		          callback(new Error('两次输入密码不一致!'));
		        } else {
		          callback();
		        }
		    };
			return {
				list:[],
				days:null,
				listLoading:false,
				filter:{
					year:null
				},
				value1:'',
				value2:'',
				value3:'',
				yewu:[],
				ruleForm2: {
					pass: '',
			        checkPass: '',
			        old: ''
			    },
				rules2: {
					pass: [
					       { required: true, validator: validatePass, trigger: 'blur' }
				    ],
				    checkPass: [
				                { required: true, validator: validatePass2, trigger: 'blur' }
				    ],
				    old: [
				          { required: true, validator: checkOld, trigger: 'blur' }
				    ]
				}
			};
		},
		methods:{
			submitForm:function(ruleForm2){
				var vm = this;
				this.$refs.ruleForm2.validate((valid) =>{
					$.getJSON(SYSCONFIG.apiurl('updatePassword!user'),{oldpassword:this.ruleForm2.old,newpassword:this.ruleForm2.pass})
					.then(function(json){
						vm.list = json;
						if(json.success==true){
							vm.$message.success(json.message);
						}else{
							vm.$message.error(json.message);
						}
					});
				})
			},
		},
		mounted:function(){
		}
	};
});
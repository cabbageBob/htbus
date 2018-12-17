define(["text!./monitor.html"],function(tpl){
   return {
       template:tpl,
       data:function(){
           return {
               off:false,
               interID:null
           }
       },
       props:{
           color:{
               default:"green"
           },
           interval:{
               default:5 * 60 * 1000
           }
       },
       mounted:function(){
           this.$nextTick( () => {
              if(!this.off){
                  this.interID = setInterval(() => {
                      this.$emit("monitor");
                  },this.interval);
              }
           });
       },
       watch:{
           off:function(val){
               if(val){
                   clearInterval(this.interID);
               }else{
                   this.interID = setInterval(() => {
                       this.$emit("monitor");
                   },this.interval);
                }
           }
       },
       methods:{
           monitor:function(){
               this.off = !this.off;
           }
       }
   }
});
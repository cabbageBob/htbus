define(["ztree"], function(){
    return {
        template: "<ul id='tree' class='ztree'></ul>",
        props: {
            setting: {
                type: Object,
                required: true
            } 
        },
        mounted: function(){
            this.ztreeObj = $.fn.zTree.init($(this.$el), this.setting)
        },
        beforeDestroy: function(){
            this.ztreeObj && this.ztreeObj.destroy()
        }
    }
})
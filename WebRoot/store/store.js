define(['vue-vuex'],function(Vuex){
	var state = {
		    count: 10,
		    mainReginHeight:300
		};
	var mutations = {
		    INCREMENT:function(_state) {
		        _state.count++
		    },
		    DECREMENT:function(_state) {
		        _state.count--
		    },
		    RE_MAINREGION_HEIGHT:function(_state,height){
		    	_state.mainReginHeight = height;
		    }
		};
	var actions={
		increment:function(context){
			context.commit('INCREMENT');
		},
		decrement:function(commit){
			context.commit('DECREMENT');
		},
		reMainRegionHeight:function(context,height){
			context.commit('RE_MAINREGION_HEIGHT',height);
		}
	}
	var getters = {
	    	getCount:function(state){
	    		return state.count;
	    	},
	    	mainRegionBodyHeight:function(state){
	    		return state.mainReginHeight - 40 - 0;
	    	}
	    };
	return new Vuex.Store({
		actions:actions,
	    getters:getters,
	    state:state,
	    mutations:mutations
	});
});
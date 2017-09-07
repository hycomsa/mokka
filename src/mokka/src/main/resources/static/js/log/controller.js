app.controller('LogsController', function($rootScope, $mdToast, LogsService){
    var self = this;
    	self.logs=[];
    	self.fetchingLogs = false;
    	self.fetchMethod = 'new';
    	self.search = {};
    	self.activeSearch = {};
    	self.getLogs = []

    self.fetchLogs = function(){
    	if (self.fetchingLogs) {
    		return;
    	}

    	self.fetchingLogs = true;

    	var params = {
    		'fetchMethod': self.fetchMethod
    	};

    	if (Object.keys(self.activeSearch).length) {
    		angular.extend(params, self.activeSearch);
    	}

    	if (self.logs && self.logs.length > 0) {
    		params.lastId = self.logs[self.logs.length - 1].id;
    	}

    	LogsService.fetchLogs(params).then(function(d) {
    		angular.forEach(d, function(e) {
    			  this.push(e);
    		}, self.logs);
    		self.fetchingLogs = false;
    	}, function() {
    		self.fetchingLogs = false;
    	});
    };

    self.changeFetchMethod = function() {
    	if (self.fetchMethod === 'new') {
    		self.fetchMethod = 'old';
    	} else {
    		self.fetchMethod = 'new';
    	}

    	self.logs = self.logs.reverse();
    	self.fetchLogs();
    };

    self.showDetails = function(log, type) {
    	if (log[type]) {
    		log[type] = null;
    	} else {
    		log[type+'Loading'] = true;

    		LogsService.getLog(log.id).then(function(l){
    			log[type] = l[type] ? l[type] : 'EMPTY!';
    			log[type+'Loading'] = false;
    		});
    	}
    };

    self.activateFilter = function() {
    	self.activeSearch = {};
    	self.search.update = false;
    	angular.extend(self.activeSearch, self.search);
    	self.logs=[];
    	self.fetchLogs();

    	$mdToast.show($mdToast.simple().position('bottom right').textContent('Results filtered'));
    };

    self.resetFilter = function() {
    	self.search = {};
    	self.activeSearch = {};
    	self.logs = [];
    	self.fetchLogs();

    	$mdToast.show($mdToast.simple().position('bottom right').textContent('Filters reseted'));
    }

    // self.fetchLogs();

    self.getSetOfLogs = function() {
        LogsService.getSetOfLogs().then (function(response){
            self.getLogs=response;
        });
	};
});

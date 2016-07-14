app.controller('ConfigurationController', function($rootScope, $scope, $mdToast, $location, $anchorScroll, ConfigurationService, Upload) {
	
    var self = this;  
    	self.newMock = false;
    	self.mocks=[];
    	self.search = {};
    	self.activeSearch = {};
    	self.pagination = 0;
    	self.paginationHasNext = false;
    	self.loading = false;
    	self.mocksPerPage = 10;
       
    self.setEnabled = function(mock){
    	ConfigurationService.setEnabled(mock.id, mock.enabled).then (function(response){			
		});
    };
    
    self.removeMockConfiguration = function(mock){
    	ConfigurationService.removeMockConfiguration(mock.id).then(function(){
    		console.log("Mock with id "+mock.id+" removed");
			var index = self.mocks.indexOf(mock);
			if (index > -1) {
				self.mocks.splice(index, 1);
			}
		});
    };
    
    self.importFromFile =  function(file, errFiles) {
    	$scope.f = file;
        $scope.errFile = errFiles && errFiles[0];
        if (file) {
        	$mdToast.show($mdToast.simple().position('bottom right').textContent('File upload started'));
        	
            file.upload = Upload.upload({
                url: '/configurations/import',
                data: {file: file}
            });

            file.upload.then(function (response) {
            	$mdToast.show($mdToast.simple().position('bottom right').textContent('File upload finished ['+response.data.status+']'));
            });
        }  		
	};
	
	self.showChanges = function(mock) {
		if (mock.showChanges) {
			mock.changes = null;
			mock.showChanges = false;
		} else {
			mock.changesLoading = true;
			ConfigurationService.getMockChanges(mock.id).then(function(data){
				mock.changes = data;
				mock.changesLoading = false;
				mock.showChanges = true;			
			});
		}
	};
	
	self.editMode = function(mock) {
		ConfigurationService.getMockConfiguration(mock.id).then(function(m){
			angular.extend(mock, m);
			mock.editMode = true;
			$mdToast.show($mdToast.simple().position('bottom right').textContent('Edit mode enabled'));
		});		
	};
	
	self.cancelEditMode = function(mock) {
		if (mock.isNew) {
			var index = self.mocks.indexOf(mock);
			if (index > -1) {
				self.mocks.splice(index, 1);
			}		
			self.newMock = false;
			
		} else {			
			ConfigurationService.getMockConfiguration(mock.id).then(function(m){
				angular.extend(mock, m);
				mock.editMode = false;
				mock.errors = null;
				$mdToast.show($mdToast.simple().position('bottom right').textContent('Changes cancelled'));
			});
		}		
	};
	
	self.saveEditMode = function(mock) {
		ConfigurationService.saveMockConfiguration(mock).then(function(m){
			if (m.errors) {
				mock.errors = m.errors;
				$mdToast.show($mdToast.simple().position('bottom right').textContent('Error saving mock, check configuration'));
				
			} else {				
				if (mock.isNew) {
					self.newMock = false;
					mock.isNew = false;
				}
				
				mock.editMode = false;
				mock.errors = null;
				mock.changes = null;
				mock.showChanges = false;
				mock.showConfiguration = true;
				$mdToast.show($mdToast.simple().position('bottom right').textContent('Mock configuration saved'));
			}
			
		});
	};
	
	self.addMockConfiguration = function() {
		
		if (!self.newMock) {
			self.newMock = true;
			
			var newMock = {};
			newMock.editMode = true;
			newMock.isNew = true;
			
			self.mocks.unshift(newMock)	
		}
		
		$location.hash('');
	    $anchorScroll();
	};
	
	self.duplicateMock = function(mock) {
		if (!self.newMock) {		
			self.newMock = true;
			
			ConfigurationService.getMockConfiguration(mock.id).then(function(m){
				var newMock = angular.copy(m);
				
				newMock.errors = null;
				newMock.id = null;
				newMock.editMode = true;
				newMock.isNew = true;
				newMock.description = m.description + ' COPY';
				
				if (newMock.type === 'groovy') {
					newMock.configurationContent.groovy.id = null;
				} else if (newMock.type === 'xml') {
					newMock.configurationContent.xml.id = null;
				} else {
					newMock.configurationContent.string.id = null;
				}	
				
				self.mocks.unshift(newMock)

				$location.hash('');
				$anchorScroll();
			});
		}
	};
	
	self.activateFilter = function() {
    	self.activeSearch = {};
    	self.search.update = false;
    	angular.extend(self.activeSearch, self.search);
    	self.mocks=[];  
    	self.pagination = 0;
    	self.fetchMocks();
    	
    	$mdToast.show($mdToast.simple().position('bottom right').textContent('Results filtered'));
    };	
    
    self.resetFilter = function() {
    	self.search = {};
    	self.activeSearch = {};
    	self.mocks = [];  
    	self.pagination = 0;
    	self.fetchMocks();
    	
    	$mdToast.show($mdToast.simple().position('bottom right').textContent('Filters reseted'));
    };
	   
    self.fetchMocks = function(initParams){
    	self.loading = true;
    	var params = {
    		'from': (self.pagination * self.mocksPerPage),
    		'perPage': (self.mocksPerPage + 1),    		
    	};
    	
    	angular.extend(params, initParams);
    	
    	if (Object.keys(self.activeSearch).length) {
    		angular.extend(params, self.activeSearch);    		
    	}
    	
    	if (self.mocks && self.mocks.length > 0) {
    		params.lastId = self.mocks[self.mocks.length - 1].id;
    	}
    	
        ConfigurationService.fetchMocks(params).then(function(d) {
        	self.paginationHasNext = (d.length > self.mocksPerPage);
        	self.mocks = d.slice(0, self.mocksPerPage);
        	
        	self.loading = false;
        	$location.hash('');
        	$anchorScroll();
        });        
    };    

    self.paginationPrevious = function() {
    	self.pagination = self.pagination - 1;
    	self.mocks = [];
    	self.fetchMocks();
    };
    
    self.paginationNext = function() {
    	self.pagination = self.pagination + 1;
    	self.mocks = [];  
    	self.fetchMocks();
    };
    
    self.showConfiguration = function(mock) {
    	if (mock.showConfiguration) {
    		mock.showConfiguration = false;
    		mock.configurationContent = null;
    		
    	} else {
    		mock.configurationLoading = true;
    		ConfigurationService.getMockConfiguration(mock.id).then(function(m){
    			angular.extend(mock, m);
    			mock.configurationLoading = false;
    			mock.showConfiguration = true;
    		});
    	}
    };

    self.clearConfiguration = function(mock) {
    	mock.configurationContent = {};
    	if (mock.type == 'groovy') {
    		mock.configurationContent.groovy = {}
    	} else if (mock.type == 'xml') {
    		mock.configurationContent.xml = {}
    	} else {
    		mock.configurationContent.string = {}
    	}
    };
    
    self.fetchMocks({
    	'show': ($location.hash() ? $location.hash().replace('mock-','') : '') 
    });

});
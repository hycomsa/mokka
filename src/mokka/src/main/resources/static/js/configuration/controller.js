app.controller('ConfigurationController', function($rootScope, $scope, $mdToast, $location, $anchorScroll, ConfigurationService, Upload) {

    var self = this;
    	self.newMock = false;
    	self.mocks=[];
    	self.httpMethods=["GET","POST","PUT","DELETE","OPTIONS","HEAD","PATCH"]
    	self.statuses=[101,201,202,203,204,205,206,02012,207,108,109,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30];
    	self.search = {};
    	self.search.enabled = true;
    	self.activeSearch = {};
    	self.pagination = 0;
    	self.pageCount = 1;
    	self.pageIndexes = [1];
    	self.maxPageIndexes = 9;
    	self.loading = false;
    	self.hasNext=false;
    	// self.mocksPerPage = 10;
    	self.paths = [];
    	self.restoredChange = [];
    	self.oldValue = "";
    	self.newValue = "";


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
                data: {'file': file}
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
			        mock=angular.extend(mock,mock,m);
					self.newMock = false;
					mock.isNew = false;
				}

				mock.editMode = false;
				mock.errors = null;
				mock.changes = null;
				mock.showChanges = false;
				mock.showConfiguration = true;
				mock.id=m.id;
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
			newMock.status = "200";

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
        $location.search('mockId', null);

    	angular.extend(self.activeSearch, self.search);
    	self.mocks = [];
    	self.paths = [];
    	self.pagination = 0;
    	self.fetchMocks();

    	$mdToast.show($mdToast.simple().position('bottom right').textContent('Results filtered'));
    };

    self.resetFilter = function() {
    	self.search = {};
    	self.activeSearch = {};
    	self.mocks = [];
    	self.paths = [];
    	self.pagination = 0;
    	self.fetchMocks();

    	$mdToast.show($mdToast.simple().position('bottom right').textContent('Filters reseted'));
    };

    self.fetchMocks = function(initParams){

        mockId = $location.search().mockId;
        if(mockId != null){
            self.search.enabled = "";
            self.search.id = mockId;
        }

        angular.extend(self.activeSearch, self.search);
    	self.loading = true;
    	var params = {
    		'from': (self.pagination),
    		// 'from': (self.pagination * self.mocksPerPage),
    		'perPage': (1),
    		// 'perPage': (self.mocksPerPage + 1),
    	};

    	angular.extend(params, initParams);

    	if (Object.keys(self.activeSearch).length) {
    		angular.extend(params, self.activeSearch);
    	}

    	if (self.mocks && self.mocks.length > 0) {
    		params.lastId = self.mocks[self.mocks.length - 1].id;
    	}

        ConfigurationService.fetchMocks(params).then(function(d) {
            if(mockId!=null && Array.isArray(d.mocks) && d.mocks.length){
                self.showConfiguration(d.mocks[0]);
                d.mocks[0].editMode = true;
            }
        	self.pageCount = d.pageCount;
        	self.updatePageIndexes();
        	self.mocks = d.mocks;
        	self.loading = false;
        	$location.hash('');
        	$anchorScroll();
        });

    };

    $scope.init = function(initParams) {
        self.search.enabled = true;
        if($location.search().mockId != null){

            var params = {
                'from': (self.pagination),
                // 'from': (self.pagination * self.mocksPerPage),
                'perPage': (1),
                // 'perPage': (self.mocksPerPage + 1),
            };

            angular.extend(params, initParams);

            var mockId = parseInt($location.search().mockId);
            if(isFinite(mockId)){

                self.search.id = mockId;
                            ConfigurationService.getMock(params,mockId).then(function(m){
                                self.search.path = m.path;
                                self.search.description = m.description;
                                self.search.pattern = m.pattern;
                                self.search.name = m.name;
                                self.search.enabled = "";
                                self.paths = [m.path];
                            });
            }
        }
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

    self.getStatuses = function() {
    	ConfigurationService.getStatuses().then (function(response){
    	    self.statuses=response;
		});
    };

    self.getPaths = function() {
        ConfigurationService.getPaths().then (function(response){
            self.paths=response;
            self.newMock=false;
        });
    };

    self.restoreChange = function (mock, change) {
        ConfigurationService.restoreChange(mock.id, change.id).then(function (response) {
            $mdToast.show($mdToast.simple().position('bottom right start').textContent('Change restored'));
        });
    };

    self.hasNextPage = function () {
        return self.pagination < (self.pageCount - 1);
    }

    self.goToPage = function(index) {
        self.pagination = index - 1;
        self.mocks = [];
        self.fetchMocks();
    }

    self.getCurrentPageNumber = function() {
        return self.pagination + 1;
    }

    self.updatePageIndexes = function() {
        const maxPagesAhead = (self.maxPageIndexes - 1) / 2;
        const maxPagesBehind = (self.maxPageIndexes - 1) / 2;

        let currentPage = self.getCurrentPageNumber();
        let pagesAhead = self.pageCount - currentPage;
        pagesAhead = pagesAhead > maxPagesAhead ? maxPagesAhead : pagesAhead;
        let pagesBehind = currentPage - 1;
        pagesBehind = pagesBehind > maxPagesBehind ? maxPagesBehind : pagesBehind;

        if(pagesAhead < maxPagesAhead) {
            pagesBehind += maxPagesAhead - pagesAhead;
        }
        if(pagesBehind < maxPagesBehind) {
            pagesAhead += maxPagesBehind - pagesBehind;
        }

        let startIndex = currentPage - pagesBehind;
        startIndex = startIndex < 1 ? 1 : startIndex;
        let endIndex = currentPage + pagesAhead;
        self.pageIndexes = [];
        for(let i=0; startIndex <= endIndex && startIndex <= self.pageCount; i++, startIndex++) {
            self.pageIndexes[i] = startIndex;
        }
    }
});

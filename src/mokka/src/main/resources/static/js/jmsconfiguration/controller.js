app.controller('JmsConfigurationController', function($rootScope, $scope, $mdToast, $location, $anchorScroll, JmsConfigurationService) {

    var self = this;
    self.loading = false;
    self.showingResult = false;
    self.greeting = "Click filter";
    self.name = "name";
    self.search = {};
    self.jmsmocks = [];
    self.pagination = 0;
    self.paginationHasNext = false;

    self.activateFilter = function () {
        self.loading = true;
        $mdToast.show($mdToast.simple().position('bottom right').textContent('Searching'));
        self.greeting = "Click reset";

        JmsConfigurationService.listJmsMocks().then(function (result) {

            var newMock = {};

            newMock.editMode = false;
            newMock.description = result.description;
            newMock.timeout = result.timeout;
            newMock.requestQueueName = result.requestQueueName;
            newMock.responseQueueName = result.responseQueueName;
            newMock.name = result.name;
            newMock.order = result.order;
            newMock.replyToHeader = result.replyToHeader;
            newMock.id = result.id;
            newMock.type = result.type;
            newMock.configurationContent = result.configurationContent;
            newMock.showConfiguration = false;
            self.jmsmocks.unshift(newMock);

            $location.hash('');
            $anchorScroll();
            self.showingResult = true;
            self.loading = false;

        });
    };

    self.resetFilter = function () {
        $mdToast.show($mdToast.simple().position('bottom right').textContent('Filters reseted'));
        self.greeting = "Click filter";
        self.showingResult = false;
        self.jmsmocks = [];
    };

    self.setReplyToHeader = function(jmsmock) {
    };

    self.showConfiguration = function(jmsmock) {
        if (jmsmock.showConfiguration) {
            self.jmsmocks[0].showConfiguration = false;
        } else {
            self.jmsmocks[0].showConfiguration = true;
        }
    };

    self.editMode = function (jmsmock) {
          if(!jmsmock.editMode){
              self.jmsmocks[0].editMode = true;
          }
        $mdToast.show($mdToast.simple().position('bottom right').textContent('Edit mode enabled'));
    };

    self.cancelEditMode = function (jmsmock) {
        if(jmsmock.editMode){
            self.jmsmocks[0].editMode = false;
        }
        $mdToast.show($mdToast.simple().position('bottom right').textContent('Edit mode disabled'));
    }
});

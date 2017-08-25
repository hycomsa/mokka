app.controller('JmsConfigurationController', function($rootScope, $scope, $mdToast, JmsConfigurationService) {

    var self = this;
    self.newMock = false;
    self.mocks=[];
    self.loading = false;
    self.search = {};
    self.greeting = "Hello World";
    self.name = "tomek";


    self.sayHi = function() {

        $mdToast.show($mdToast.simple().position('bottom right').textContent('Zrobione'));
        self.greeting = "zrobione";

        JmsConfigurationService.powiedzCzesc(self.name).then(function (data) {
            self.greeting = data;
        });

    };


});

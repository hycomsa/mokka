app.factory('JmsConfigurationService',function($http, $q){
    return {


        powiedzCzesc: function (imie) {
            return $http.get('/jmsconfiguration/' + self.name).then(function(response){
                return response.data
            });
        }


    };
});

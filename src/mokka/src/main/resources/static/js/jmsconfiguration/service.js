app.factory('JmsConfigurationService',function($http, $q){
    return {

        activateFilter: function (param) {
            return $http.get('/jmsconfiguration/' + param).then(function(response){
                return response.data.description
            });
        },

        listJmsMocks: function () {
            return $http.post('/jmsconfiguration/list').then(function (response) {
               return response.data
            });
        }

    };
});

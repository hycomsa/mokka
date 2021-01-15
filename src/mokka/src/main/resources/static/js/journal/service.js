app.factory('JournalService', function($http, $location){
    return {
           fetchJournal: function(){
                return $http.get('/journal').then(function(response){
                    return response.data;
                    });
            }
    };
});

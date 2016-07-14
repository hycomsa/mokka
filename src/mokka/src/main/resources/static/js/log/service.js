app.factory('LogsService', function($http, $q){
	return {
		
		fetchLogs: function(params){
			return $http.get('/logs/', {
				'params': params
			}).then(										
					function(response){
						return response.data;
					},
					function(errResponse){
						console.error('Error while fetching logs');
						return $q.reject(errResponse);
					}
			);
		},
		
		getLog: function(id){
			return $http.get('/logs/' + id).then(
					function(response){
						return response.data;
					},
					function(errResponse){
						console.error('Error while fetching log exchanges');
						return $q.reject(errResponse);
					}
			);
		}		
	};
});
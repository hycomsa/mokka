app.service('ConfiguratorService', function ($http) {
	_handleResponse = function(response, url, method, data, params, callback){
		if (angular.isFunction(callback)) {
			callback(response.data)
		}
	}
	
	_callRestService = function(url, method, data, params, callback ) {
		$http({
			url: '/configurator/types/' + url,
			params: params, 
			method: method, 
			data: data
		}).then(function(response) {
			_handleResponse(response, url, method, data, params, callback);						
		}, function(response) {
			_handleResponse(response, url, method, data, params, callback);
		});	
	}
	
	this.getAllThingTypes = function(callback) {
		_callRestService('thing-types', 'GET', null, null, callback);
	}
	
	this.addThingType = function(thing, callback) {
		_callRestService('thing-types', 'PUT', thing, null, callback);
	}
	
	this.saveTimeout = function(timeout){

		return $http.post('/configurator/types/saveTimeout',timeout)
   	    .then(
   	    		function(response){
   	    			console.log("Saving timeout");
   	    			return response;
   	    		},
                function(errResponse){
               	 console.error('Error while saving timeout');
               	 return $q.reject(errResponse);
                }
   	    );
	}
	
});
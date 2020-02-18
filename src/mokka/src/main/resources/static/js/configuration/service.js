app.factory('ConfigurationService',function($http, $q){
   return {
	   setEnabled: function(id,enabled){
	    	 return $http.post('/configuration/' + id + '/' + (enabled ? 'enable' : 'disable')).then(function(response){
	    	 	return response.data
	    	 });
	   },

	   removeMockConfiguration: function(id){
	    	return $http.delete('/configuration/' + id).then(function(response){
				return response.data
   	 		});
	   },

	   getMockChanges: function(id) {
		   return $http.get('/configuration/' + id + '/changes').then(function(response){
				return response.data
  	 		});
	   },

	   getMockConfiguration: function(id) {
		   return $http.get('/configuration/'+id).then(function(response){
			    if (!response.data.configurationContent) {
			    	response.data.configurationContent = {};
				}

			    if (response.data.configurationContent.groovy) {
			   		response.data.type='groovy';
				} else if (response.data.configurationContent.xml) {
			   		response.data.type='xml';
				} else {
					response.data.type='string';
				}

                return response.data;
		   });
	   },

	   saveMockConfiguration: function(mock) {
		   return $http.put('/configuration', mock).then(function(response){
                        return response.data;
		   });
	   },

	   fetchMocks: function(params) {
		   return $http.get('/configuration/', {
				'params': params
			}).then(function(response){
				angular.forEach(response.data.mocks, function(e) {
					if (!e.configurationContent) {
						e.configurationContent = {};
					}

					if (e.configurationContent.groovy) {
				   		e.type='groovy';
					} else if (e.configurationContent.xml) {
				   		e.type='xml';
					} else {
						e.type='string';
					}
				});

                return response.data;
		   });
	   },

       getMock: function(params, mockId) {
           return $http.get('/configuration/' + mockId, {
                'params': params
            }).then(function(response){

                    if (!response.data.configurationContent) {
                        e.configurationContent = {};
                    }

                    if (response.data.configurationContent.groovy) {
                        response.data.type='groovy';
                    } else if (response.data.configurationContent.xml) {
                        response.data.type='xml';
                    } else {
                        response.data.type='string';
                    }


                   return response.data;
           });
       },

	   getStatuses: function(mock) {
		   return $http.post('/configuration/statuses').then(function(response){
		   console.debug(response);
                        return response.data;
		   });
	   },

       getPaths: function(mock) {
           return $http.get('/configuration/paths').then(function (response) {
               console.debug(response);
               return response.data;
           });
       },

	   restoreChange: function(configId, changeId) {
           return $http.get('/configuration/' + configId + '/restore/' + changeId).then(function (response) {
               console.debug(response);
			   return response.data.diffs.configurationContent;
               // mock.configurationContent.value

           });
       }


   };
});

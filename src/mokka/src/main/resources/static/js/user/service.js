app.factory('UsersService', function($http, $q){
	return {

	   fetchUsers: function(){
			return $http.get('/user').then(										
					function(response){
						angular.forEach(response.data, function(e) {
							var r;
							for (i = 0; i < e.roles.length; i++) { 
								r = e.roles[i];
								if (r === 'ADMIN') {
									e.isAdmin = true;
								} else if (r === 'EDITOR') {
									e.isEditor = true;
								} else if (r === 'USER_ADMIN') {
									e.isUserAdmin = true;
								}
							}			
						});
						
						return response.data;
					},
					function(errResponse){
						console.error('Error while fetching users');
						return $q.reject(errResponse);
					}
			);
	   },
		
	   setDisabled: function(id,disabled){
	    	 return $http.post('/user/' + id + '/' + (disabled ? 'disable' : 'enable')).then(function(response){
	    	 	return response.data
	    	 });
	   },
	   
	   removeUser: function(id){
	    	return $http.delete('/user/' + id).then(function(response){
				return response.data 					    	 			
  	 		});
	   },
	   	   
	   getUser: function(id) {
		   return $http.get('/user/'+id).then(function(response){
			   var user = response.data; 
			   var r;
				for (i = 0; i < user.roles.length; i++) { 
					r = user.roles[i];
					if (r === 'ADMIN') {
						user.isAdmin = true;
					} else if (r === 'EDITOR') {
						user.isEditor = true;
					} else if (r === 'USER_ADMIN') {
						user.isUserAdmin = true;
					}
				}
			   
				return user;
		   });
	   },
	   
	   saveUser: function(user) {
		   return $http.put('/user', user).then(function(response){
                       return response.data;
		   });
	   },
	   
	   resetPassword: function(id) {
		   return $http.post('/user/'+id+'/reset-password').then(function(response){
                       return response.data;
		   });
	   },

	   switchRole: function(id, role) {
		   return $http.post('/user/'+id+'/role', {'role': role}).then(function(response){
                       return response.data;
		   });
	   }		
	};
});

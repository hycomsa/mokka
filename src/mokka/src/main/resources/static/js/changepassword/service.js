app.factory('ChangePasswordService',function($http, $q){
   return {
	   
	   changePassword: function(id,password){
	    	 return $http.post('/user/' + id + '/change-password', {'password':password}).then(function(response){
	    	 	return response.data
	    	 });
	   }
	   
   };
});
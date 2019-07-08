var app = angular.module('ConfiguratorApp', ['ngMaterial', 'ngMdIcons', 'lazy-scroll', 'ngFileUpload', 'ui.ace', 'smDateTimeRangePicker']);

app.config(function($mdDateLocaleProvider) {
    $mdDateLocaleProvider.formatDate = function(date) {
       return date ? moment(date).format('YYYY-MM-DD') : '';
    };
});

app.config(function ($httpProvider) {
	var metaTags=document.getElementsByTagName("meta");
	var token = '';
	var header = '';
	
	for (var i = 0; i < metaTags.length; i++) {
	    if (metaTags[i].getAttribute("name") == "_csrf") {
	    	token = metaTags[i].getAttribute("content");	        
	    }
	    if (metaTags[i].getAttribute("name") == "_csrf_header") {
	    	header = metaTags[i].getAttribute("content");	        
	    }
	}	
	
    $httpProvider.defaults.headers.common[header] = token;
    $httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
});

app.config(['$locationProvider', function($locationProvider) {
	$locationProvider.html5Mode({
		  enabled: true,
		  requireBase: false
	});
}]);

app.filter('to_trusted', ['$sce', function($sce){
    return function(text) {
        return $sce.trustAsHtml(text);
    };
}]);

app.run(function($rootScope, $http) {
	return $http.get('/user/current').then(function(response){
		$rootScope.user = response.data;
		
		$rootScope.user.isAdmin = false;
		$rootScope.user.isEditor = false;
		$rootScope.user.isUserAdmin = false;
		
		angular.forEach($rootScope.user.authorities, function(value) {
			  if (value.authority === 'ROLE_ADMIN') {
				  $rootScope.user.isAdmin = true;
			  } else if (value.authority === 'ROLE_EDITOR') {
				  $rootScope.user.isEditor = true;
			  } else if (value.authority === 'ROLE_USER_ADMIN') {
				  $rootScope.user.isUserAdmin = true;
			  }
		});
	});
})

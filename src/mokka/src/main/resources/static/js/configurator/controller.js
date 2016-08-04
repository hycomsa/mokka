app.controller('ConfiguratorCtrl', function($scope, $mdDialog, $mdMedia,ConfiguratorService) {
	
  var self = this;	

	
  $scope.customFullscreen = $mdMedia('xs') || $mdMedia('sm');

  $scope.showTypeConfigurationDialog = function(ev) {
	  $mdDialog.show({
	      controller: TypeConfigurationDialogCtrl,
	      templateUrl: 'ajax/configurator/types-configuration',
	      parent: angular.element(document.body),
	      targetEvent: ev,
	      clickOutsideToClose:true,
	      fullscreen: true
	  });	  
  };  
  
  
  self.saveTimeout = function(){
	  console.log(self.timeout);
	  ConfiguratorService.saveTimeout(self.timeout)
	  .then(
		  function(d){
			  alert("Zapisano timeout");
			  console.log("poszlo do conf service ");  
		  },
		  function(errResponse){
              console.error('Error while saving timeout');
          }
	  );
  	
  };
  

  
});


function TypeConfigurationDialogCtrl($scope, $mdDialog, ConfiguratorService) {
	ConfiguratorService.getAllThingTypes(function(response){
		$scope.thingTypes = response;
	});
	
	$scope.addThingType = function() {
		ConfiguratorService.addThingType($scope.newDefinition, function(){
			ConfiguratorService.getAllThingTypes(function(response){
				$scope.thingTypes = response;
			});
			$scope.showAddNewDefinition = false;
			$scope.newDefinition = {name:'', fields: []}
		});
	}
	
	$scope.newDefinition = {name:'', fields: []}
	
	$scope.hide = function() {
		$mdDialog.hide();
	};
	$scope.cancel = function() {
		$mdDialog.cancel();
	};
	$scope.answer = function(answer) {
		$mdDialog.hide(answer);
	};
}
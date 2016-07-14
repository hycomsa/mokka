app.controller('ChangePasswordController', function($rootScope, $mdToast, ChangePasswordService){
    var self = this;
    	self.showPassword = false;
    	self.password = "";
    	self.error = false;
    	self.success = false;

    self.changePassword = function(){
    	ChangePasswordService.changePassword($rootScope.user.id, self.password).then(function(result){
    		if (result) {
    			self.success = true;
    			$mdToast.show($mdToast.simple().position('bottom right').textContent('Password changed'));
    		} else {
    			self.error = true;
    			$mdToast.show($mdToast.simple().position('bottom right').textContent('Password NOT changed'));
    		}
    	});
    	
    };
    

});

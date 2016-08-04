app.controller('UsersController', function($scope, $rootScope, $mdToast, $location, $anchorScroll, UsersService){
    var self = this;
    	self.newUser = false;
    	self.users = [];
    	self.loading = false;

    self.fetchUsers = function(){
    	self.loading = true;
    	UsersService.fetchUsers().then(function(d) {
    		angular.forEach(d, function(e) {
    				this.push(e);
    		}, self.users);
    		self.loading = false;
    	});
    };
    
    self.setDisabled = function(user) {
    	if (!user.isNew) {
    		UsersService.setDisabled(user.id, user.disabled).then (function(response){});
    	}
    };
	
    self.editUser = function(user) {
    	UsersService.getUser(user.id).then(function(u){
    		user.editMode = true;
			angular.extend(user, u);
			$mdToast.show($mdToast.simple().position('bottom right').textContent('Edit mode enabled'));
		});
    };
    
	self.saveEditMode = function(user) {
		UsersService.saveUser(user).then(function(u){
			if (u.errors) {
				user.errors = u.errors;
				$mdToast.show($mdToast.simple().position('bottom right').textContent('Error saving user, check configuration'));
				
			} else {				
				if (user.isNew) {
					self.newUser = false;
					user.isNew = false;
				}
				
				user.editMode = false;
				user.errors = null;
				
				$mdToast.show($mdToast.simple().position('bottom right').textContent('User saved'));
			}
			
		});
    };
    
	self.cancelEditMode = function(user) {
		if (user.isNew) {
			var index = self.users.indexOf(user);
			if (index > -1) {
				self.users.splice(index, 1);
			}		
			self.newUser = false;
			
		} else {			
			UsersService.getUser(user.id).then(function(u){
				angular.extend(user, u);
				user.editMode = false;
				user.errors = null;
				$mdToast.show($mdToast.simple().position('bottom right').textContent('Changes cancelled'));
			});
		}	
    };
    
	self.removeUser = function(user) {
		UsersService.removeUser(user.id).then(function(){
    		console.log("User with id "+user.id+" removed");
			var index = self.users.indexOf(user);
			if (index > -1) {
				self.users.splice(index, 1);
			}
		});
    };
    
    self.resetPassword = function(user) {
    	UsersService.resetPassword(user.id).then(function(r){
    		$mdToast.show($mdToast.simple().position('bottom right').textContent('Password reseted'));
    	});
    };
    
    self.addUser = function() {
    	if (!self.newUser) {
			self.newUser = true;
			
			var newUser = {};
			newUser.editMode = true;
			newUser.isNew = true;
			
			self.users.unshift(newUser)	
		}
		
		$location.hash('user-');
	    $anchorScroll();
	    $location.hash('');
    };
    
    self.switchRole = function(user, role) {
    	if (user.isNew) {
    		if (typeof user.roles == 'undefined') {
    			user.roles = [];
    		}
    		
    		var index = user.roles.indexOf(role);
			if (index > -1) {
				self.roles.splice(index, 1);
			} else {				
				user.roles.push(role);
			}			
    		
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
			
    	} else {    		
    		UsersService.switchRole(user.id, role).then(function(r){    
    			UsersService.getUser(user.id).then(function(u){
    				angular.copy(u, user);
    			});
    		});
    	}
    	
    };
    
    self.userFilter = function (user) {
    	if (!self.search) {
    		return true;
    	}
    	
    	if (user.isNew) {
    		return true;
    	}
    	
        return user.firstName.indexOf(self.search) > -1 || user.lastName.indexOf(self.search) > -1 || user.userName.indexOf(self.search) > -1;
    };
    
    self.fetchUsers();
});

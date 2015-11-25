app.controller('AppController', function($scope, $http) {
	$scope.products = [];
	$scope.loadAll = function() {
		$http.get("/product").success(function(response) {
			$scope.products = response;
		});
	};
	
	$scope.loadAll();
	
	$scope.product = {};
	
	$scope.addProduct = function(product) {
		$http.post("/product", product).success(function(data, status) {
			console.log(status);
			$scope.loadAll();
			$scope.clear();
		});
		
	}
	
	$scope.deleteProduct = function(id, product){
		$http.delete("/product/" + id, product).success(function(data, status) {
			console.log(data);
			$scope.loadAll();
		});
	}
	
	$scope.showEditProduct = function(id){
		$http.get("/product/"+id).success(function(response) {
			$scope.product = response;
			$('#editProduct').modal('show');
		});
	}
	$scope.update = function (id,product){
		$http.put("/product/" + id,product).success(function(data, status) {
			console.log(data);
			$('#editProduct').modal('hide');
			$scope.loadAll();
			$scope.clear();
		});
	}
	
	$scope.clear = function (){
		$scope.product = {id : null, name : null, description : null};
	}
});
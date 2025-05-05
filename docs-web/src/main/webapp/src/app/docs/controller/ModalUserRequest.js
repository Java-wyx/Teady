'use strict';

angular.module('docs').controller('ModalUserRequest', function($scope, $dialog, $state, Restangular, $translate) {

  $scope.user = {};

  $scope.submitRequest = function () {
    var promise = null;
    var user = angular.copy($scope.user);
    user.storage_quota = 100000;
    user.storage_quota *= 1000000;

    promise = Restangular
        .one('user')
        .put(user);

    promise.then(function () {
      $scope.loadUsers();
      $state.go('settings.user');
    }, function (e) {
      if (e.data.type === 'AlreadyExistingUsername') {
        var title = $translate.instant('settings.user.edit.edit_user_failed_title');
        var msg = $translate.instant('settings.user.edit.edit_user_failed_message');
        var btns = [{result: 'ok', label: $translate.instant('ok'), cssClass: 'btn-primary'}];
        $dialog.messageBox(title, msg, btns);
      }
    });
  };

  $scope.cancel = function() {
    $uibModalInstance.dismiss('cancel');
  };
});
'use strict';

angular.module('docs').controller('ModalUserRequest', function($scope, $dialog, $state, Restangular, $translate) {

  $scope.user = {};

  $scope.submitRequest = function () {
    var user = angular.copy($scope.user);
    user.storage_quota = 100000;
    user.storage_quota *= 1000000;

    // Prepare data as x-www-form-urlencoded
    var data = 'username=' + user.username +
        '&password=' + user.password +
        '&email=' + user.email +
        '&storage_quota=' + user.storage_quota;

    var promise = Restangular
        .one('user')
        .one('register_request')
        .customPUT(data, '', {}, { 'Content-Type': 'application/x-www-form-urlencoded' });

    promise.then(function () {
      var title = $translate.instant('settings.user.edit.register_request_sent_title');
      var msg = $translate.instant('settings.user.edit.register_request_sent_message');
      var btns = [{result: 'ok', label: $translate.instant('ok'), cssClass: 'btn-primary'}];
      $dialog.messageBox(title, msg, btns);
      $scope.cancel();
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
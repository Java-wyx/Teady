'use strict';

angular.module('docs')
    .controller('UserRegistrationRequests', function($scope, Restangular, $translate, $dialog) {
        $scope.requests = [];
        $scope.error    = null;

        // 加载所有待审批请求
        $scope.loadRequests = function() {
            Restangular
                .one('user/register_request')  // 对应 GET /api/user/register_request
                .get()
                .then(function(response) {
                    // 后端返回 { requests: [...] }
                    $scope.requests = response.requests || [];
                    $scope.error    = null;
                })
                .catch(function(err) {
                    console.error('Load requests failed', err);
                    $scope.error = $translate.instant('settings.user.registration_requests.load_error');
                });
        };

        // 审批通过
        $scope.approveRequest = function(requestId) {
            var title = $translate.instant('settings.user.registration_requests.approve_confirm_title');
            var msg   = $translate.instant('settings.user.registration_requests.approve_confirm_message');
            var btns  = [
                { result: 'cancel', label: $translate.instant('cancel') },
                { result: 'ok',     label: $translate.instant('ok'), cssClass: 'btn-primary' }
            ];

            $dialog.messageBox(title, msg, btns).result.then(function(btn) {
                if (btn === 'ok') {
                    Restangular
                        .one('user/register_request', requestId)
                        .one('approve')
                        .post()
                        .then($scope.loadRequests)
                        .catch(function(err) {
                            console.error('Approve failed', err);
                            // 可视化错误提示
                            $scope.error = $translate.instant('settings.user.registration_requests.approve_error');
                        });
                }
            });
        };

        // 审批拒绝
        $scope.rejectRequest = function(requestId) {
            var title = $translate.instant('settings.user.registration_requests.reject_confirm_title');
            var msg   = $translate.instant('settings.user.registration_requests.reject_confirm_message');
            var btns  = [
                { result: 'cancel', label: $translate.instant('cancel') },
                { result: 'ok',     label: $translate.instant('ok'), cssClass: 'btn-primary' }
            ];

            $dialog.messageBox(title, msg, btns).result.then(function(btn) {
                if (btn === 'ok') {
                    Restangular
                        .one('user/register_request', requestId)
                        .one('reject')
                        .post()
                        .then($scope.loadRequests)
                        .catch(function(err) {
                            console.error('Reject failed', err);
                            $scope.error = $translate.instant('settings.user.registration_requests.reject_error');
                        });
                }
            });
        };

        // 首次加载
        $scope.loadRequests();
    });

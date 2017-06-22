(ns tictactoe-api.invalid-route-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [clojure.data.json :as json]
            [tictactoe-api.handler :refer :all]))

(deftest invalid-route
  (testing "invalid-route"
    (let [response (app (-> (mock/request :get "/no-route-here")))]
      (is (= (:status response) 404)))))

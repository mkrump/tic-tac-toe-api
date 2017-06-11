(ns tictactoe-api.invalid-route-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [clojure.data.json :as json]
            [tictactoe.board :as board]
            [tictactoe-api.handler :refer :all]
            [tictactoe.computer-minimax-ab-player :refer :all]))

(deftest invalid-route
  (testing "invlaid-route"
    (let [response (app (-> (mock/request :get "/no-route-here")))]
      (is (= (:status response) 404)))))



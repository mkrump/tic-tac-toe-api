(ns tictactoe-api.computer-move-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [clojure.data.json :as json]
            [cheshire.core :as cheshire]
            [tictactoe-api.handler :refer :all]))

(defn response-body->map [response] (cheshire/parse-string (:body response) true))

(deftest computer-move-multiple-open-tests
  (testing "computer move returns 200 and updates game state"
    (let [test-request {:game-state
                        {:board          {:board-contents [-1, 0, 0, 0, 0, 0, 0, 0, 0] :gridsize 3}
                         :current-player 1
                         :winner         0
                         :is-tie         false}}
          expected-game-state {:board          {:board-contents [-1, 0, 0, 0, 1, 0, 0, 0, 0] :gridsize 3}
                               :current-player -1
                               :winner         0
                               :is-tie         false
                               :game-over      false}
          response (app (-> (mock/request :post "/computer-move")
                            (mock/content-type "application/json")
                            (mock/body (json/json-str test-request))))
          response-body (response-body->map response)]

      (is (= (:status response) 200))
      (is (= (:message response-body) "Success"))
      (is (= (:game-state response-body) expected-game-state))
      (is (get-in response [:headers "Content-Type"] "application/json; charset=utf-8")))))

(deftest computer-move-one-open-test
  (testing "computer move returns 200 and 3 (the only open square)"
    (let [test-request {:game-state
                        {:board          {:board-contents [1, -1, 1, 0, -1, -1, -1, 1, -1] :gridsize 3}
                         :current-player -1
                         :winner         0
                         :is-tie         false}}
          expected-game-state {:board          {:board-contents [1, -1, 1, -1, -1, -1, -1, 1, -1] :gridsize 3}
                               :current-player 1
                               :winner         -1
                               :is-tie         false
                               :game-over      true}
          response (app (-> (mock/request :post "/computer-move")
                            (mock/content-type "application/json")
                            (mock/body (json/json-str test-request))))
          response-body (response-body->map response)]
      (is (= (:status response) 200))
      (is (= (:game-state response-body) expected-game-state))
      (is (= (get-in response-body [:message] "Success")))
      (is (= (get-in response [:headers "Content-Type"]) "application/json; charset=utf-8")))))
(ns tictactoe-api.valid-move-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [clojure.data.json :as json]
            [tictactoe-api.handler :refer :all]
            [cheshire.core :as cheshire]))

(defn response-body->map [response] (cheshire/parse-string (:body response) true))

(deftest valid-move-test
  (testing "if move is valid returns 200 and updates game state"
    (let [intial-game-state {:board          {:board-contents [-1, 0, 0, 0, 0, 0, 0, 0, 0] :gridsize 3}
                             :current-player 1
                             :winner         0
                             :game-over      false
                             :is-tie         false}
          expected-game-state {:board          {:board-contents [-1, 1, 0, 0, 0, 0, 0, 0, 0] :gridsize 3}
                               :current-player -1
                               :winner         0
                               :game-over      false
                               :is-tie         false}
          test-request {:move 1 :game-state intial-game-state}
          response (app (-> (mock/request :post "/valid-move")
                            (mock/content-type "application/json")
                            (mock/body (json/json-str test-request))))
          response-body (response-body->map response)]
      (is (= (:status response) 200))
      (is (= expected-game-state (:game-state response-body)))
      (is (= (get-in response-body [:message]) "Success"))
      (is (= (get-in response [:headers "Content-Type"]) "application/json; charset=utf-8")))))

(deftest square-occupied-test
  (testing "if a square is occupied returns 406 and returns game state unchanged"
    (let [intial-game-state {:board          {:board-contents [1, 0, 0, 0, 0, 0, 0, 0, 0] :gridsize 3}
                             :current-player -1
                             :winner         0
                             :game-over      false
                             :is-tie         false}
          test-request {:move 0 :game-state intial-game-state}
          response (app (-> (mock/request :post "/valid-move")
                            (mock/content-type "application/json")
                            (mock/body (json/json-str test-request))))
          response-body (response-body->map response)]
      (is (= (:status response) 406))
      (is (= (get-in response-body [:game-state]) intial-game-state))
      (is (= (get-in response-body [:message]) "Square occupied"))
      (is (= (get-in response [:headers "Content-Type"]) "application/json; charset=utf-8")))))

(deftest out-of-range-test
  (testing "that a move that is out of the board range returns 404 returns the game state unchanged"
    (let [intial-game-state {:board          {:board-contents [1, 0, 0, 0, 0, 0, 0, 0, 0] :gridsize 3}
                             :current-player -1
                             :winner         0
                             :game-over      false
                             :is-tie         false}
          test-request {:move 1000 :game-state intial-game-state}
          response (app (-> (mock/request :post "/valid-move")
                            (mock/content-type "application/json")
                            (mock/body (json/json-str test-request))))
          response-body (response-body->map response)]
      (is (= (:status response) 404))
      (is (= (get-in response-body [:game-state]) intial-game-state))
      (is (= (get-in response-body [:message]) "Out of range"))
      (is (= (get-in response [:headers "Content-Type"]) "application/json; charset=utf-8")))))

(ns tictactoe-api.computer-move-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [clojure.data.json :as json]
            [tictactoe.board :as board]
            [tictactoe-api.handler :refer :all]
            [tictactoe.computer-minimax-ab-player :refer :all]
            [cheshire.core :as cheshire]))

(defn response-body->map [response] (cheshire/parse-string (:body response) true))

(deftest computer-move-multiple-open-tests
  (testing "computer move returns 200 and a number in range of open squares (1-8)"
    (let [test-request {:current-player -1, :board {:board-contents [1, 0, 0,
                                                                     0, 0, 0,
                                                                     0, 0, 0] :gridsize 3}}
          response (app (-> (mock/request :post "/computer-move")
                            (mock/content-type "application/json")
                            (mock/body (json/json-str test-request))))
          response-body (response-body->map response)]
      (is (= (:status response) 200))
      (is (= 4 (get-in response-body [:game-state :move])))
      (is (= (:message response) ""))
      (is (= (get-in response [:headers "Content-Type"]) "application/json; charset=utf-8")))))

(deftest computer-move-one-open-test
  (testing "computer move returns 200 and 3 (the only open square)"
    (let [test-request {:current-player 1, :board {:board-contents [1, -1, 1,
                                                                    0, -1, -1,
                                                                    -1, 1, -1] :gridsize 3}}
          response (app (-> (mock/request :post "/computer-move")
                            (mock/content-type "application/json")
                            (mock/body (json/json-str test-request))))
          response-body (response-body->map response)]
      (is (= (:status response) 200))
      (is (= 3 (get-in response-body [:game-state :move])))
      (is (= (:message response) ""))
      (is (= (get-in response [:headers "Content-Type"]) "application/json; charset=utf-8")))))


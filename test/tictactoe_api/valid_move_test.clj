(ns tictactoe-api.valid-move-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [clojure.data.json :as json]
            [tictactoe.board :as board]
            [tictactoe-api.handler :refer :all]
            [tictactoe.computer-minimax-ab-player :refer :all]))

(defn response-body->map [response] (json/read-str (get-in response [:body]) :key-fn keyword))

(deftest valid-move-test
  (testing "if move is valid returns 200 :valid is true"
    (let [test-request {:move 1, :board {:board-contents [1, 0, 0, 0, 0, 0, 0, 0, 0] :gridsize 3}}
          response (app (-> (mock/request :post "/valid-move")
                            (mock/content-type "application/json")
                            (mock/body (json/json-str test-request))))
          response-body (response-body->map response)]
      (is (= (:status response) 200))
      (is (= (:valid response-body) true))
      (is (= (:error-response response-body) ""))
      (is (= (get-in response [:headers "Content-Type"]) "application/json; charset=utf-8")))))

(deftest square-occupied-test
  (testing "if a square is occupied returns 406 :valid is false"
    (let [test-request {:move 0, :board {:board-contents [1, 0, 0, 0, 0, 0, 0, 0, 0] :gridsize 3}}
          response (app (-> (mock/request :post "/valid-move")
                            (mock/content-type "application/json")
                            (mock/body (json/json-str test-request))))
          response-body (response-body->map response)]
      (is (= (:status response) 406))
      (is (= (:valid response-body) false))
      (is (= (:error-response response-body) "Square occupied"))
      (is (= (get-in response [:headers "Content-Type"]) "application/json; charset=utf-8")))))

(deftest out-of-range-test
  (testing "that a move that is out of the board range returns 404 :valid is false"
    (let [test-request {:move 100000, :board {:board-contents [1, 0, 0, 0, 0, 0, 0, 0, 0] :gridsize 3}}
          response (app (-> (mock/request :post "/valid-move")
                            (mock/content-type "application/json")
                            (mock/body (json/json-str test-request))))
          response-body (response-body->map response)]
      (is (= (:status response) 404))
      (is (= (:valid response-body) false))
      (is (= (:error-response response-body) "Out of range"))
      (is (= (get-in response [:headers "Content-Type"]) "application/json; charset=utf-8")))))




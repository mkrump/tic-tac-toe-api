(ns tictactoe-api.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [clojure.data.json :as json]
            [tictactoe.board :as board]
            [tictactoe-api.handler :refer :all]
            [tictactoe.computer-minimax-ab-player :refer :all]))

(defn response-body->map [response] (json/read-str (get-in response [:body]) :key-fn keyword))

(deftest valid-move-test
  (testing "move valid returns 200 :valid true"
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
  (testing "square occupied returns 406 :valid false"
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
  (testing "A move that is out of the board range returns 404 :valid false"
    (let [test-request {:move 100000, :board {:board-contents [1, 0, 0, 0, 0, 0, 0, 0, 0] :gridsize 3}}
          response (app (-> (mock/request :post "/valid-move")
                            (mock/content-type "application/json")
                            (mock/body (json/json-str test-request))))
          response-body (response-body->map response)]
      (is (= (:status response) 404))
      (is (= (:valid response-body) false))
      (is (= (:error-response response-body) "Out of range"))
      (is (= (get-in response [:headers "Content-Type"]) "application/json; charset=utf-8")))))

(deftest invalid-route
  (testing "invlaid-route"
    (let [response (app (-> (mock/request :get "/no-route-here")))]
      (is (= (:status response) 404)))))

(deftest computer-move-valid1-tests
  (testing "computer move returns 200 and a number in range of open squares"
    (let [test-request {:current-player -1, :board {:board-contents [1, 0, 0, 0, 0, 0, 0, 0, 0] :gridsize 3}}
          response (app (-> (mock/request :post "/computer-move")
                            (mock/content-type "application/json")
                            (mock/body (json/json-str test-request))))
          response-body (response-body->map response)]
      (is (= (:status response) 200))
      (is (<= 1 (:move response-body) 8))
      (is (= (:error-response response-body) ""))
      (is (= (get-in response [:headers "Content-Type"]) "application/json; charset=utf-8")))))

(deftest computer-move-valid2-tests
  (testing "computer move returns 200 and a number in range of open squares"
    (let [test-request {:current-player 1, :board {:board-contents [1, -1, 1,
                                                                    0, -1, -1,
                                                                    -1, 1, -1] :gridsize 3}}
          response (app (-> (mock/request :post "/computer-move")
                            (mock/content-type "application/json")
                            (mock/body (json/json-str test-request))))
          response-body (response-body->map response)]
      (is (= (:status response) 200))
      (is (= 3))
      (is (= (:error-response response-body) ""))
      (is (= (get-in response [:headers "Content-Type"]) "application/json; charset=utf-8")))))


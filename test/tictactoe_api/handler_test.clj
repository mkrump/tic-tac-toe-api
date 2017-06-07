(ns tictactoe-api.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [clojure.data.json :as json]
            [tictactoe.board :as board]
            [tictactoe-api.handler :refer :all]))

(deftest valid-move-test
  (testing "move valid returns 200 :valid true"
    (let [test-request {:move 1, :board {:board-contents [1,0,0,0,0,0,0,0,0] :gridsize 3}}
          expected {:board {:board-contents [1,0,0,0,0,0,0,0,0], :gridsize 3}, :valid true}
          response (app (-> (mock/request :post "/valid-move")
                            (mock/content-type "application/json")
                            (mock/body (json/json-str test-request))))]
      (is (= (:status response) 200))
      (is (= (json/read-str (get-in response [:body]))) expected)
      (is (= (get-in response [:headers "Content-Type"]) "application/json; charset=utf-8")))))

(deftest square-occupied-test
  (testing "square occupied returns 206 :valid false"
    (let [test-request {:move 0, :board {:board-contents [1,0,0,0,0,0,0,0,0] :gridsize 3}}
          expected {:board {:board-contents [1,0,0,0,0,0,0,0,0], :gridsize 3}, :valid false}
          response (app (-> (mock/request :post "/valid-move")
                            (mock/content-type "application/json")
                            (mock/body (json/json-str test-request))))]
      (is (= (:status response) 206))
      (is (= (json/read-str (get-in response [:body]))) expected)
      (is (= (get-in response [:headers "Content-Type"]) "application/json; charset=utf-8")))))

(deftest invalid-route
  (testing "invlaid-route"
    (let [response (app (-> (mock/request :get "/no-route-here")))]
      (is (= (:status response) 404)))))


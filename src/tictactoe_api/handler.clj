(ns tictactoe-api.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.coercions :as coercions]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :as ring-json]
            [ring.util.response :as rr]
            [tictactoe.board :as board]
            [tictactoe.game :as game]
            [compojure.handler :as handler]))

(defn move-is-valid [request]
  (let [board (get-in request [:body :board])
        move  (get-in request [:body :move])
        occupied (board/square-occupied? board move)]
       (cond
         (nil? occupied)
         {:status 404 :body {:board board :move move :valid false :error-response "Out of range"}}
         (not occupied)
         {:status 200 :body {:board board :move move :valid false :error-response ""}}
         occupied
         {:status  206 :body {:board board :move move :valid false :error-response "Square occupied"}}
         :else
         {:status 404 :body {:board board :move move :valid false :error-response "Out of range"}})))

(defroutes app-routes
  (POST "/valid-move" [] (fn [request] (move-is-valid request)))
  (route/not-found {:status 404 :body "Not Found"}))

(def app
  (-> (handler/site app-routes)
      (ring-json/wrap-json-body {:keywords? true})
      (ring-json/wrap-json-response)))



(ns tictactoe-api.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.coercions :as coercions]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :as ring-json]
            [ring.util.response :as rr]
            [tictactoe.board :as board]
            [ring.middleware.cors :as cors]
            [tictactoe.game :as game]
            [tictactoe.computer-minimax-ab-player :as computer-minimax-ab-player]
            [compojure.handler :as handler]))

(defn move-is-valid [request]
  (let [board (get-in request [:body :board])
        move  (get-in request [:body :move])
        occupied (board/square-occupied? board move)]
       (cond
         (nil? occupied)
         {:status 404 :body {:game-state {:board board :move move :valid false }} :message "Out of range"}
         (not occupied)
         {:status 200 :body {:game-state {:board board :move move :valid true}} :message ""}
         occupied
         {:status  406 :body {:game-state {:board board :move move :valid false}} :message "Square occupied"}
         :else
         {:status 404 :body {:game-state {:board board :move move :valid false}} :message "Bad request"})))

(defn computer-move [request]
  (let [board (get-in request [:body :board])
        current-player  (get-in request [:body :current-player])
        move (computer-minimax-ab-player/minimax-move board current-player)]
    {:status 200 :body {:game-state {:board board :move move }} :message ""}))

(defroutes app-routes
  (POST "/valid-move" [] (fn [request] (move-is-valid request)))
  (POST "/computer-move" [] (fn [request] (computer-move request)))
  (route/not-found {:status 404 :body "Not Found"}))

(def app
  (-> (handler/site app-routes)
      (cors/wrap-cors :access-control-allow-origin [#".+"]
                 :access-control-allow-methods [:get :put :post :delete])
      (ring-json/wrap-json-body {:keywords? true})
      (ring-json/wrap-json-response)))


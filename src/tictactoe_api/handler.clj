(ns tictactoe-api.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :as ring-json]
            [ring.middleware.cors :as cors]
            [tictactoe.game :as game]
            [tictactoe.computer-minimax-ab-player :as computer-minimax-ab-player]
            [tictactoe.board :as board]
            [tictactoe.detect-board-state :as detect-board-state]))

(defn move-is-valid [request]
  (let [move (get-in request [:body :move])
        board (get-in request [:body :game-state :board])
        current-player (get-in request [:body :game-state :current-player])
        winner (get-in request [:body :game-state :winner])
        is-tie (get-in request [:body :game-state :is-tie])
        occupied (board/square-occupied? board move)]
    (cond
      (nil? occupied)
      {:status  404 :body {:game-state
                           {:board          board
                            :winner         winner
                            :is-tie         is-tie
                            :current-player current-player}}
       :message "Out of range"}
      (not occupied)
      (do
        (let [updated-board (board/make-move board move current-player)
              updated-winner (detect-board-state/winner (:board-contents updated-board) (:gridsize updated-board))
              updated-is-tie (detect-board-state/tie? (:board-contents updated-board) (:gridsize updated-board))
              updated-current-player (game/switch-player current-player)]
          {:status  200 :body {:game-state
                               {:board          updated-board
                                :winner         updated-winner
                                :is-tie         updated-is-tie
                                :current-player updated-current-player}}
           :message ""}))
      occupied
      {:status  406 :body {:game-state
                           {:board          board
                            :winner         winner
                            :is-tie         is-tie
                            :current-player current-player}}
       :message "Square occupied"}
      :else
      {:status  404 :body {:game-state
                           {:board          board
                            :winner         winner
                            :is-tie         is-tie
                            :current-player current-player}}
       :message "Bad request"})))

(defn computer-move [request]
  (let [board (get-in request [:body :game-state :board])
        current-player (get-in request [:body :game-state :current-player])
        move (computer-minimax-ab-player/minimax-move board current-player)
        updated-board (board/make-move board move current-player)
        updated-winner (detect-board-state/winner (:board-contents updated-board) (:gridsize updated-board))
        updated-is-tie (detect-board-state/tie? (:board-contents updated-board) (:gridsize updated-board))
        updated-player (game/switch-player current-player)]
    {:status  200 :body {:game-state
                         {:board          updated-board
                          :winner         updated-winner
                          :is-tie         updated-is-tie
                          :current-player updated-player}}
     :message ""}))

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


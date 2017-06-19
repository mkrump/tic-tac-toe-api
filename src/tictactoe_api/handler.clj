(ns tictactoe-api.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :as ring-json]
            [ring.middleware.cors :as cors]
            [tictactoe.computer-minimax-ab-player :as computer-minimax-ab-player]
            [tictactoe.board :as board]
            [tictactoe.detect-board-state :as detect-board-state]
            [tictactoe.game :as game]))

(def occupied-response-map
  {nil :out-of-range false :valid-move true :occupied})

(defn square-occupied [game-state move]
  (let [{:keys [board winner is-tie current-player]} game-state
        occupied (board/square-occupied? board move)]
    (occupied-response-map occupied)))

(defn update-game-state [game-state move]
  (let [{:keys [board winner is-tie current-player]} game-state
        updated-board (board/make-move board move current-player)
        updated-winner (detect-board-state/winner (:board-contents updated-board) (:gridsize updated-board))
        updated-is-tie (detect-board-state/tie? (:board-contents updated-board) (:gridsize updated-board))
        updated-current-player (game/switch-player current-player)]
    {:board          updated-board
     :winner         updated-winner
     :is-tie         updated-is-tie
     :current-player updated-current-player}))

(defn move-is-valid [request]
  (let [request-body (:body request)
        original-game-state (:game-state request-body)
        move (:move request-body)
        response (square-occupied original-game-state move)]
    (cond
      (= response :out-of-range)
      {:status 404 :body {:game-state original-game-state :message "Out of range"}}
      (= response :valid-move)
      (do
        (let [updated-game-state (update-game-state original-game-state move)]
          {:status 200 :body {:game-state updated-game-state :message "Success"}}))
      (= response :occupied)
      {:status 406 :body {:game-state original-game-state :message "Square occupied"}}
      :else
      {:status 404 :body {:game-state original-game-state :message "Bad request"}})))

(defn computer-move [request]
  (let [request-body (:body request)
        original-game-state (:game-state request-body)
        {:keys [board winner is-tie current-player]} original-game-state
        move (computer-minimax-ab-player/minimax-move board current-player)
        updated-game-state (update-game-state original-game-state move)]
    {:status 200 :body {:game-state updated-game-state :message "Success"}}))

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
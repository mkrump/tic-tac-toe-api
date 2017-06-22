(ns tictactoe-api.computer-move
  (require [tictactoe.tictactoe-core.ttt-core :as ttt-core]))

(defn computer-move [request]
  (let [request-body (:body request)
        original-game-state (:game-state request-body)
        move (ttt-core/get-computer-move original-game-state)
        updated-game-state (ttt-core/update-game-state original-game-state move)]
    {:status 200 :body {:game-state updated-game-state :message "Success"}}))
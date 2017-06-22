(ns tictactoe-api.move-is-valid
  (require [tictactoe.tictactoe-core.ttt-core :as ttt-core]))

(defn move-is-valid [request]
  (let [request-body (:body request)
        original-game-state (:game-state request-body)
        move (:move request-body)
        {:keys [status message]} (ttt-core/move-status original-game-state move)]
    (cond
      (= status :out-of-range)
      {:status 404 :body {:game-state original-game-state :message message}}
      (= status :valid-move)
      (do
        (let [updated-game-state (ttt-core/update-game-state original-game-state move)]
          {:status 200 :body {:game-state updated-game-state :message message}}))
      (= status :occupied)
      {:status 406 :body {:game-state original-game-state :message message}}
      :else
      {:status 404 :body {:game-state original-game-state :message message}})))

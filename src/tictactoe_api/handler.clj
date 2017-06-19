(ns tictactoe-api.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :as ring-json]
            [ring.middleware.cors :as cors]
            [tictactoe.ttt-core :as ttt-core]))

(defn move-is-valid [request]
  (let [request-body (:body request)
        original-game-state (:game-state request-body)
        move (:move request-body)
        response (ttt-core/square-occupied original-game-state move)]
    (cond
      (= response :out-of-range)
      {:status 404 :body {:game-state original-game-state :message "Out of range"}}
      (= response :valid-move)
      (do
        (let [updated-game-state (ttt-core/update-game-state original-game-state move)]
          {:status 200 :body {:game-state updated-game-state :message "Success"}}))
      (= response :occupied)
      {:status 406 :body {:game-state original-game-state :message "Square occupied"}}
      :else
      {:status 404 :body {:game-state original-game-state :message "Bad request"}})))

(defn computer-move [request]
  (let [request-body (:body request)
        original-game-state (:game-state request-body)
        updated-game-state (ttt-core/make-computer-move original-game-state)]
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
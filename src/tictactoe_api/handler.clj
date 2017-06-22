(ns tictactoe-api.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :as ring-json]
            [ring.middleware.cors :as cors]
            [tictactoe-api.computer-move :as computer-move]
            [tictactoe-api.move-is-valid :as move-is-valid]))

(defroutes app-routes
           (POST "/valid-move" [] (fn [request] (move-is-valid/move-is-valid request)))
           (POST "/computer-move" [] (fn [request] (computer-move/computer-move request)))
           (route/not-found {:status 404 :body "Not Found"}))

(def app
  (-> (handler/site app-routes)
      (cors/wrap-cors :access-control-allow-origin [#".+"]
                      :access-control-allow-methods [:post])
      (ring-json/wrap-json-body {:keywords? true})
      (ring-json/wrap-json-response)))


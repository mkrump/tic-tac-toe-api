(defproject tictactoe-api "0.1.0-SNAPSHOT"
  :description "Clojure tictactoe api"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.5.1"]
                 [org.clojure/data.json "0.2.6"]
                 [ring/ring-defaults "0.3.0"]
                 [ring-cors "0.1.10"]
                 [ring/ring-json "0.4.0"]
                 [org.clojars.mkrump/tictactoe "0.1.1-SNAPSHOT"]]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler tictactoe-api.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}})

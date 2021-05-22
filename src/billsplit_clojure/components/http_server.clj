(ns billsplit-clojure.components.http-server
  (:require [com.stuartsierra.component :as component]
            [org.httpkit.server :as server]
            [ring.middleware.defaults :refer :all]))

(defn run-server [port service]
  (-> (:service-routes service)
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
      ;(logger/wrap-with-logger) ; not needed right now
      (server/run-server {:port port})))

(defrecord HttpServer [port service]
  component/Lifecycle

  (start [this]
    (println (str "Running webserver at http://localhost:" port "/"))
    (assoc this :http-server (run-server port service)))

  (stop [this]
    (println "Server stopped")
    (when-let [stop-server (:http-server this)]
      (stop-server :timeout 100))
    (assoc this :http-server nil)))

(defn new-server [port]
  (map->HttpServer {:port port}))

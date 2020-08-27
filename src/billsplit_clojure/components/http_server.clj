(ns billsplit-clojure.components.http-server
  (:require [billsplit-clojure.controllers :as c]
            [com.stuartsierra.component :as component]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [org.httpkit.server :as server]
            [ring.middleware.defaults :refer :all]
            [billsplit-clojure.web :as web]))

(defn index-handler [app-component request]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (web/layout (web/index))})

(defn split-handler [app-component request]
  (let [people-list (-> request
                        :params
                        :fields)]
    (c/add-people-to-bill! (:bill app-component) people-list)
    {:status  200
     :headers {"Content-Type" "text/html"}
     :body    @(:bill app-component)}))

(defn app-routes
  [app-component]
  (routes
    (GET "/" request (index-handler app-component request))
    (POST "/split" request (split-handler app-component request))
    (route/not-found "Error, page not found!")))

(defrecord HttpServer [port http-server app-component]
  component/Lifecycle

  (start [this]
    (let [port (or port 3000)
          server (-> (app-routes app-component)
                     (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
                     (server/run-server {:port port}))]
      (println (str "Running webserver at http://localhost:" port "/"))
      (assoc this :http-server server)))

  (stop [this]
    (println "Server stopped")
    (when-let [stop-server (:http-server this)]
      (stop-server :timeout 100))
    (assoc this :http-server nil)))

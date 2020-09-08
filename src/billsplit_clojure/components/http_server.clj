(ns billsplit-clojure.components.http-server
  (:require [com.stuartsierra.component :as component]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [org.httpkit.server :as server]
            [ring.middleware.defaults :refer :all]
            [billsplit-clojure.web :as web]))

(defn index-handler [_]
  {:status  200
   :headers {"Content-Type" "text/html"
             "Cache-Control" "no-cache"}
   :body    (web/layout (web/index))})

(defn split-handler [request]
  (let [people-list (-> request
                        :params
                        :fields)]
    {:status  200
     :headers {"Content-Type" "text/html"
               "Cache-Control" "no-cache"}
     :body    (-> people-list
                  web/split
                  web/layout)
     :session {:people people-list}}))

(defn result-handler [request]
  (println request)
  {:status 200
   :headers {"Content-Type" "text/html"
             "Cache-Control" "no-cache"}
   :body (get-in request [:session :people])})

(defn app-routes []
  (routes
    (GET "/" request (index-handler request))
    (POST "/split" request (split-handler request))
    (POST "/result" request (result-handler request))
    (route/not-found "Error, page not found!")))

(defrecord HttpServer [port http-server app-component]
  component/Lifecycle

  (start [this]
    (let [port (or port 3000)
          server (-> (app-routes)
                     (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
                     (server/run-server {:port port}))]
      (println (str "Running webserver at http://localhost:" port "/"))
      (assoc this :http-server server)))

  (stop [this]
    (println "Server stopped")
    (when-let [stop-server (:http-server this)]
      (stop-server :timeout 100))
    (assoc this :http-server nil)))

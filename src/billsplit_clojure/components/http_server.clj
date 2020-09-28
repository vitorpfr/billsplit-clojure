(ns billsplit-clojure.components.http-server
  (:require [com.stuartsierra.component :as component]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [org.httpkit.server :as server]
            [ring.middleware.defaults :refer :all]
            [billsplit-clojure.web :as web]
            [billsplit-clojure.controllers :as c]
            [billsplit-clojure.logic :as l]))

(defn index-handler [_]
  {:status  200
   :headers {"Content-Type"  "text/html"
             "Cache-Control" "no-cache"}
   :body    (web/layout (web/index))})

(defn split-handler [request]
  (let [people-list (-> request
                        :params
                        :fields)]
    {:status  200
     :headers {"Content-Type"  "text/html"
               "Cache-Control" "no-cache"}
     :body    (-> people-list
                  web/split
                  web/layout)
     :session {:people people-list}}))

(defn result-handler [{:keys [params] :as request}]
  (let [people (get-in request [:session :people])
        bill-result (-> people
                        (c/create-bill params)
                        (c/calculate-bill params))
        total-bill-value (l/get-total-bill-value bill-result)]
    (println params)
    {:status  200
     :headers {"Content-Type"  "text/html"
               "Cache-Control" "no-cache"}
     :body    (-> bill-result
                  (web/result total-bill-value)
                  web/layout)}))

(defn about-handler [_]
  {:status  200
   :headers {"Content-Type"  "text/html"
             "Cache-Control" "no-cache"}
   :body    (web/layout (web/about))})

(defn app-routes []
  (routes
    (GET "/" request (index-handler request))
    (POST "/split" request (split-handler request))
    (POST "/result" request (result-handler request))
    (GET "/about" request (about-handler request))
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

(ns billsplit-clojure.components.service
  (:require [billsplit-clojure.web :as web]
            [billsplit-clojure.controllers :as c]
            [billsplit-clojure.logic :as l]
            [com.stuartsierra.component :as component]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [billsplit-clojure.web :as web]
            [billsplit-clojure.controllers :as c]
            [billsplit-clojure.logic :as l]))

(defn success
  [body]
  {:status  200
   :headers {"Content-Type"  "text/html"
             "Cache-Control" "no-cache"}
   :body    body})

(defn index-handler [_]
  (-> (web/index)
      web/with-layout
      success))

(defn split-handler [request]
  (let [people-list (get-in request [:params :fields])]
    (-> (web/split people-list)
        web/with-layout
        success
        (assoc :session {:people people-list}))))

(defn result-handler [{:keys [params] :as request}]
  (let [people (get-in request [:session :people])
        bill-result (-> people
                        (c/create-bill params)
                        (c/calculate-bill params))
        total-bill-value (l/get-total-bill-value bill-result)]
    (-> (web/result bill-result total-bill-value)
        web/with-layout
        success)))

(defn about-handler [_]
  (-> (web/about)
      web/with-layout
      success))

(defn app-routes []
  (routes
    (GET "/" request (index-handler request))
    (POST "/split" request (split-handler request))
    (POST "/result" request (result-handler request))
    (GET "/about" request (about-handler request))
    (route/not-found "Error, page not found!")))

(defrecord Service [routes]
  component/Lifecycle
  (start [this]
    (assoc this :service-routes (routes)))

  (stop [this]
    (assoc this :service-routes nil)))

(defn new-service [] (map->Service {:routes app-routes}))

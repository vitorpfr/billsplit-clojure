(ns billsplit-clojure.components.system
  (:require [com.stuartsierra.component :as component]
            [billsplit-clojure.components.bill :as bill]
            [billsplit-clojure.components.http-server :as http-server]))

(def system-config
  {:port 3000})

(defn new-system [config-options]
  (let [{:keys [port]} config-options]
    (-> (component/system-map
          :http-server (component/using (http-server/map->HttpServer {:port port}) [:app-component])
          :app-component (bill/map->Bill {}))
        (component/system-using
          {:http-server   [:app-component]
           :app-component []}))))

(def system (new-system system-config))

(defn start-all []
  (component/start system))
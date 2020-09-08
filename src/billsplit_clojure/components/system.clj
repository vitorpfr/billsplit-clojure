(ns billsplit-clojure.components.system
  (:require [com.stuartsierra.component :as component]
            [billsplit-clojure.components.http-server :as http-server]))

(def system-config
  {:port 3000})

(defn new-system [{:keys [port]}]
  (component/system-map :http-server (http-server/map->HttpServer {:port port})))

(def system (new-system system-config))

(defn start-all []
  (component/start system))
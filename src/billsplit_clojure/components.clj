(ns billsplit-clojure.components
  (:require [com.stuartsierra.component :as component]
            [billsplit-clojure.components.http-server :as http-server]
            [billsplit-clojure.components.service :as service]))

(def system-config {:port 3030})

(defn new-system [{:keys [port]}]
  (component/system-map
    :service (service/new-service)
    :http-server (component/using (http-server/new-server port) [:service])))

(def system (new-system system-config))

(defn start-all []
  (component/start system))

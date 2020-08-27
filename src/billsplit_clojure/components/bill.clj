(ns billsplit-clojure.components.bill
  (:require [com.stuartsierra.component :as component]))

(defrecord Bill [bill]
  component/Lifecycle

  (start [this]
    (println "Starting empty bill")
    (let [bill-data (atom {})]
      (assoc this :bill bill-data)))

  (stop [this]
    (println "Stopping and deleting bill")
    (assoc this :bill nil)))
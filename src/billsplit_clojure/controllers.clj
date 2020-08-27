(ns billsplit-clojure.controllers
  (:require [billsplit-clojure.logic :as l]))

(defn add-people-to-bill!
  [bill people]
  (swap! bill l/add-people-list people))

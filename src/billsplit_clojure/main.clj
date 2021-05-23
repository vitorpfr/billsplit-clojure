(ns billsplit-clojure.main
  (:require [billsplit-clojure.components :as components])
  (:gen-class))

(defn -main [& args]
  (components/start-all))

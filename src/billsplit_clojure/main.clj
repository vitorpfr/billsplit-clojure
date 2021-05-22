(ns billsplit-clojure.main
  (:require [billsplit-clojure.components :as components]))

(defn -main [& args]
  (components/start-all))

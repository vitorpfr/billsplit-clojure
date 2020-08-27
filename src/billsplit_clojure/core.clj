(ns billsplit-clojure.core
  (:require [billsplit-clojure.components.system :as system]))

(defn -main [& args]
  (system/start-all))